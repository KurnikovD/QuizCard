package com.quizcard.server.service;

import com.example.QuizCard.entity.*;
import com.quizcard.server.repository.QuestionRepository;
import com.quizcard.server.repository.QuizRepository;
import com.quizcard.server.repository.RoundRepository;
import com.quizcard.server.repository.TopicRepository;
import com.jlefebure.spring.boot.minio.MinioConfigurationProperties;
import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import com.quizcard.server.entity.*;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.binary.XSSFBParseException;
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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
//@Scope(value = WebApplicationContext.SCOPE_SESSION,
//        proxyMode = ScopedProxyMode.TARGET_CLASS)
public class LoadService {

    @Value("${QuizCard.path.temp}")
    private String tempPath;
    @Value("${QuizCard.type.image}")
    private String imageType;
    @Value("${QuizCard.type.video}")
    private String videoType;
    @Value("${QuizCard.type.audio}")
    private String audioType;

    @Value("${spring.minio.url}")
    private String minioUrl;
    @Value("${spring.minio.access-key}")
    private String minioAccessKey;
    @Value("${spring.minio.secret-key}")
    private String minioSecretKey;

    private final QuizRepository quizRepository;
    private final RoundRepository roundRepository;
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;

    private MinioService minioService;
    private String exceptionLog = "";

    private Quiz quiz;
    private String dataFileName;
    private String dirTempPath;

    public LoadService(QuizRepository quizRepository,
                       RoundRepository roundRepository,
                       TopicRepository topicRepository,
                       QuestionRepository questionRepository) {
        this.quizRepository = quizRepository;
        this.roundRepository = roundRepository;
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
    }

    public void savePack(MultipartFile file) {
        try {
            quiz = new Quiz();
            quiz.setTitle(file.getOriginalFilename().split("\\.")[0]);
            dirTempPath = tempPath + quiz.getTitle() + File.separator;
            File destDir = new File(dirTempPath);

            ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream());
            byte[] buffer = new byte[1024];
            ZipEntry entry = zipInputStream.getNextEntry();
            while (entry != null) {
                File newFile = newFile(destDir, entry);
                if (entry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    saveFile(zipInputStream, buffer, entry, newFile);

                }
                entry = zipInputStream.getNextEntry();
            }
            zipInputStream.closeEntry();
            zipInputStream.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void buildMinio() {
        MinioClient minioClient = new MinioClient.Builder()
                .endpoint(minioUrl)
                .credentials(minioAccessKey, minioSecretKey)
                .build();

        MinioConfigurationProperties minioConfigurationProperties = new MinioConfigurationProperties();
        minioConfigurationProperties.setUrl(minioUrl);
        minioConfigurationProperties.setAccessKey(minioAccessKey);
        minioConfigurationProperties.setSecretKey(minioSecretKey);
        minioConfigurationProperties.setSecure(false);
        minioConfigurationProperties.setBucket(quiz.getId());


        try {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(quiz.getId())
                            .build());

        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }


        minioService = new MinioService(minioClient, minioConfigurationProperties);
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
                XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        ) {

            quiz = quizRepository.save(quiz);
            buildMinio();

            List<Round> rounds = new ArrayList<>();
            int roundNumber = 0;

            for (Sheet sheet : workbook) {
                Round currentRound = saveRound(sheet, roundNumber++);

                int topicNumber = 0;
                Topic currentTopic = null;

                for (Row row : sheet) {
                    if (isTopic(row)) {
                        currentTopic = saveTopic(row, currentRound, topicNumber++);
                    } else {
                        if (currentTopic == null) {
                            throw new XSSFBParseException("Topic is null - " + sheet.getSheetName() + " " + row.getRowNum());
                        }
                        saveQuestion(currentTopic, row);
                    }
                }

            }

        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        if (!exceptionLog.isEmpty()) {
            throw new IllegalArgumentException(exceptionLog);
        }

    }

    private boolean isTopic(Row row) {
        int count = 0;
        for (Cell cell : row) {
            if (!cell.toString().isEmpty()) {
                count++;
            }
            if (count > 1) {
                return false;
            }
        }
        return true;
    }

    private Round saveRound(Sheet sheet, int roundNumber) {
        Round round = new Round(quiz);
        round.setName(sheet.getSheetName());
        round.setRoundNumber(roundNumber);
        return roundRepository.save(round);
    }

    private Topic saveTopic(Row row, Round currentRound, int topicNumber) {
        Topic topic = new Topic(currentRound);
        topic.setTopicNumber(topicNumber);
        topic.setName(row.getCell(0).getStringCellValue());

        return topicRepository.save(topic);
    }

    private void saveQuestion(Topic currentTopic, Row row) {
        if (currentTopic == null) {
            exceptionLog += "Topic is null - " + row.getRowNum() + "\n";
        }
        Question question = new Question(currentTopic);
        question = questionRepository.save(question);

        // cost
        if (row.getCell(0) == null) {
            exceptionLog += "Cost is not set - " + row.getRowNum() + "\n";
        }
        question.setCost((int) Double.parseDouble(row.getCell(0).toString()));
        if (row.getCell(1) != null) {
            question.setCat(true);
            if (row.getCell(1).getStringCellValue().equals("cat")) {
                question.setCatMediaPath("cat.jpg");
            } else {
                String path = row.getCell(1).getStringCellValue();
                saveMedia(question.getId() + "-cat", path);
                question.setCatMediaPath("-cat." + path.split("\\.")[1]);
            }
        }

        // question
        int questionCellNumber = 2;
        if (row.getCell(2) != null) {
            question.setQuestion(row.getCell(2).toString());
        } else {
            questionCellNumber--;
        }

        if (row.getCell(3) != null) {
            String path = row.getCell(3).getStringCellValue();
            saveMedia(question.getId() + "-question", path);
            question.setQuestionMediaType(getMediaType(path));
            question.setQuestionMediaPath("-question." + path.split("\\.")[1]);
        } else {
            questionCellNumber--;
        }

        if (questionCellNumber == 0) {
            exceptionLog += "Question is not set - " + row.getRowNum() + "\n";
        }

        // answer
        int answerCellNumber = 2;
        if (row.getCell(4) != null) {
            question.setAnswer(row.getCell(4).toString());
        } else {
            answerCellNumber--;
        }

        if (row.getCell(5) != null) {
            String path = row.getCell(5).getStringCellValue();
            saveMedia(question.getId() + "-answer", path);
            question.setAnswerMediaType(getMediaType(path));
            question.setAnswerMediaPath("-answer." + path.split("\\.")[1]);
        } else {
            answerCellNumber--;
        }
        if (answerCellNumber == 0) {
            exceptionLog += "Answer is not set - " + row.getRowNum() + "\n";
        }

        questionRepository.save(question);
    }

    private void saveMedia(String id, String mediaPath) {
        File from = new File(dirTempPath + mediaPath);
        Path to = Paths.get(getPathToMedia(id, mediaPath));
        try {
            minioService.upload(to, from);
        } catch (MinioException e) {
            exceptionLog += "File not found:" + e.getStackTrace()[0].getLineNumber() + ":" + mediaPath + "\n";
        }
    }

    private String getPathToMedia(String id, String mediaPath) {
        return id + "." + mediaPath.split("\\.")[1];
    }

    private MediaType getMediaType(String mediaPath) {
        if (mediaPath == null || mediaPath.isEmpty()) {
            return MediaType.NONE;
        }
        String fileExtension = mediaPath.split("\\.")[1];

        if (isImage(fileExtension)) {
            return MediaType.IMAGE;
        } else if (isVideo(fileExtension)) {
            return MediaType.VIDEO;
        } else if (isAudio(fileExtension)) {
            return MediaType.AUDIO;
        }
        return MediaType.NONE;
    }

    private boolean isAudio(String fileExtension) {
        String[] audio = audioType.split(",");
        return Arrays.asList(audio).contains(fileExtension);
    }

    private boolean isVideo(String fileExtension) {
        String[] video = videoType.split(",");
        return Arrays.asList(video).contains(fileExtension);
    }

    private boolean isImage(String fileExtension) {
        String[] image = imageType.split(",");
        return Arrays.asList(image).contains(fileExtension);
    }
}
