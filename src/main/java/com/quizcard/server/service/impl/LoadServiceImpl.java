package com.quizcard.server.service.impl;

import com.jlefebure.spring.boot.minio.MinioException;
import com.quizcard.server.entity.*;
import com.quizcard.server.exception.LoadDataException;
import com.quizcard.server.repository.QuestionRepository;
import com.quizcard.server.repository.QuizRepository;
import com.quizcard.server.repository.RoundRepository;
import com.quizcard.server.repository.TopicRepository;
import com.quizcard.server.security.CustomMinioService;
import com.quizcard.server.service.LoadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.quizcard.server.util.ExcelUtil.getNumberValue;
import static com.quizcard.server.util.ExcelUtil.getValue;
import static com.quizcard.server.util.FileUtils.getExtension;
import static com.quizcard.server.util.StringUtil.isBlank;

@Service
//@Scope(value = WebApplicationContext.SCOPE_SESSION,
//        proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
@RequiredArgsConstructor
public class LoadServiceImpl implements LoadService {

    @Value("${QuizCard.path.temp}")
    private String tempPath;

    private final QuizRepository quizRepository;
    private final RoundRepository roundRepository;
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final CustomMinioService minioService;

    private List<String> exceptionLogs;

    private final List<Path> files = new ArrayList<>();

    private Quiz quiz;
    private String dataFileName;
    private String dirTempPath;

    public void savePack(MultipartFile file) {
        exceptionLogs = new ArrayList<>();
        try {
            quiz = new Quiz(file.getOriginalFilename().split("\\.")[0]);
            dirTempPath = tempPath + quiz.getTitle() + File.separator;
            File destDir = new File(dirTempPath);

            ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream());
            byte[] buffer = new byte[1024];
            ZipEntry entry = zipInputStream.getNextEntry();
            while (entry != null) {
                File newFile = newFile(destDir, entry);
                if (entry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new LoadDataException("Failed to create directory " + newFile.getName());
                    }
                } else {
                    saveFile(zipInputStream, buffer, entry, newFile);
                }
                entry = zipInputStream.getNextEntry();
            }
            zipInputStream.closeEntry();
            zipInputStream.close();

            log.info("Successfully unpacked: {}", file.getOriginalFilename());

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new LoadDataException("Failed to process file " + file.getOriginalFilename());
        }
    }

    public void removeTempDir() {
        dirTempPath = tempPath + quiz.getTitle() + File.separator;
        File destDir = new File(dirTempPath);
        try {
            FileUtils.deleteDirectory(destDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveFile(ZipInputStream zipInputStream, byte[] buffer, ZipEntry entry, File newFile) throws IOException {
        File parent = newFile.getParentFile();
        if (!parent.isDirectory() && !parent.mkdirs()) {
            throw new IOException("Failed to create directory " + parent);
        }

        if (entry.getName().contains(".xlsx")) {
            dataFileName = entry.getName();
        }

        // write file content
        FileOutputStream fos = new FileOutputStream(newFile);
        int len;
        while ((len = zipInputStream.read(buffer)) > 0) {
            fos.write(buffer, 0, len);
        }
        fos.close();
    }

    private File newFile(File dirName, ZipEntry entry) throws IOException {
        File destFile = new File(dirName, entry.getName());

        String destDirPath = dirName.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + entry.getName());
        }

        return destFile;

    }

    @Transactional
    public void addData() {
        try (
                FileInputStream fileInputStream = new FileInputStream(dirTempPath + dataFileName);
                XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream)
        ) {
            quiz = quizRepository.save(quiz);
            minioService.createBucket(quiz.getId());

            int roundNumber = 0;
            log.info(" --- Start save quiz data --- ");
            for (Sheet sheet : workbook) {
                log.info("\t{}. Start save round: {}", roundNumber + 1, sheet.getSheetName());
                Round currentRound = saveRound(sheet, roundNumber++);

                int topicNumber = 0;
                Topic currentTopic = null;

                for (Row row : sheet) {
                    if (isEmptyLine(row)) {
                        break;
                    }
                    if (isTopic(row)) {
                        currentTopic = saveTopic(row, currentRound, topicNumber++);
                        log.info("\t\t{}. Create new topic: {}", currentTopic.getTopicNumber(), currentTopic.getName());
                    } else {
                        if (currentTopic == null) {
                            throw new LoadDataException(
                                    "Topic is empty on " + sheet.getSheetName() + " at " + row.getRowNum()
                            );
                        }
                        saveQuestion(currentTopic, row);
                    }
                }
            }
        } catch (IOException e) {
            throw new LoadDataException("Failed to load data from " + dirTempPath + dataFileName);
        }

        if (!exceptionLogs.isEmpty()) {
            minioService.clearerFiles(files, quiz.getId());
            throw new LoadDataException(exceptionLogs);
        }

        log.info(" --- Quiz successfully upload ---");

    }

    private boolean isTopic(Row row) {
        int count = 0;
        int maxCount = 4;
        for (Cell cell : row) {
            maxCount--;
            if (!isBlank(getValue(cell))) {
                count++;
            }
            if (count > 1) {
                return false;
            }
            if (maxCount == 0 && count == 1) {
                return true;
            }
        }
        return true;
    }

    private boolean isEmptyLine(Row row) {
        int count = 0;
        for (Cell cell : row) {
            if (!isBlank(getValue(cell))) {
                return false;
            }
            count++;
            if (count > 6) {
                return true;
            }
        }
        return true;
    }

    private Round saveRound(Sheet sheet, int roundNumber) {
        return roundRepository.save(new Round(quiz, sheet.getSheetName(), roundNumber));
    }

    private Topic saveTopic(Row row, Round currentRound, int topicNumber) {
        return topicRepository.save(
                new Topic(currentRound,
                        topicNumber,
                        row.getCell(0).getStringCellValue())
        );
    }

    private void saveQuestion(Topic currentTopic, Row row) {
        Question question = new Question(currentTopic);
        question = questionRepository.save(question);

        // cost
        Integer costValue = getNumberValue(row.getCell(0));
        if (costValue == null) {
            exceptionLogs.add("Cost is not set - " + row.getRowNum() + ":0");
        }
        question.setCost(costValue);
        // cat
        String catCell = getValue(row.getCell(1));
        if (!isBlank(catCell)) {
            question.setCat(true);
            if (catCell.equals("cat")) {
                question.setCatMediaPath("cat.jpg");
            } else {
                saveMedia(question.getId() + "-cat", catCell);
                question.setCatMediaPath("-cat." + getExtension(catCell));
                question.setCatMediaType(getMediaType(catCell));
            }
        }

        // question
        int questionCellNumber = 2;
        String questionCell = getValue(row.getCell(2));
        if (!isBlank(questionCell)) {
            question.setQuestion(questionCell);
        } else {
            questionCellNumber--;
        }

        String questionMediaCell = getValue(row.getCell(3));
        if (!isBlank(questionMediaCell)) {
            saveMedia(question.getId() + "-question", questionMediaCell);
            question.setQuestionMediaType(getMediaType(questionMediaCell));
            question.setQuestionMediaPath("-question." + getExtension(questionMediaCell));
        } else {
            questionCellNumber--;
        }

        if (questionCellNumber == 0) {
            exceptionLogs.add("Question is not set - " + row.getRowNum() + ":2,3");
        }

        // answer
        int answerCellNumber = 2;
        String answerCell = getValue(row.getCell(4));
        if (!isBlank(answerCell)) {
            question.setAnswer(answerCell);
        } else {
            answerCellNumber--;
        }

        String answerMediaCell = getValue(row.getCell(5));
        if (!isBlank(answerMediaCell)) {
            saveMedia(question.getId() + "-answer", answerMediaCell);
            question.setAnswerMediaType(getMediaType(answerMediaCell));
            question.setAnswerMediaPath("-answer." + getExtension(answerMediaCell));
        } else {
            answerCellNumber--;
        }
        if (answerCellNumber == 0) {
            exceptionLogs.add("Answer is not set - " + row.getRowNum() + ":4,5");
        }

        questionRepository.save(question);
    }

    private void saveMedia(String id, String mediaPath) {
        File from = new File(dirTempPath + mediaPath);
        Path to = Paths.get(getPathToMedia(id, mediaPath));
        try {
            minioService.upload(to, from);
            files.add(to);
        } catch (MinioException e) {
            exceptionLogs.add("File not found:" + mediaPath);
        }
    }

    private String getPathToMedia(String id, String mediaPath) {
        return id + "." + getExtension(mediaPath);
    }

    private MediaType getMediaType(String mediaPath) {
        if (mediaPath == null || mediaPath.isEmpty()) {
            return MediaType.NONE;
        }

        return MediaType.byExtension(getExtension(mediaPath));
    }

}
