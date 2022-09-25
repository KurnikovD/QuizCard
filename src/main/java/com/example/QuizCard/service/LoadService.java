package com.example.QuizCard.service;

import com.example.QuizCard.model.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION,
        proxyMode = ScopedProxyMode.TARGET_CLASS)
public class LoadService {

    @Autowired
    QueryService queryService;

    MultipartFile file;


    @Value("${pack.temp.path}")
    private String tempPath;

    private String dataFileName;
    private String dirPath;
    private String quizName;

    private QuizModel quiz;


    public void savePack(MultipartFile file) {
        try (ZipInputStream inputZipFile = new ZipInputStream(file.getInputStream())) {
            quizName = file.getOriginalFilename().split("\\.")[0];
            dirPath = tempPath + quizName + File.separator;

            File destDir = new File(dirPath);

            byte[] buffer = new byte[1024];
            ZipEntry entry = inputZipFile.getNextEntry();
            while (entry != null) {
                File newFile = newFile(destDir, entry);
                if (entry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {

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
                    while ((len = inputZipFile.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();


                }
                entry = inputZipFile.getNextEntry();
            }
            inputZipFile.closeEntry();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(dirPath + dataFileName));
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

            quiz = new QuizModel();
            quiz.setTitle(quizName);

            List<RoundModel> rounds = new ArrayList<>();

            for (Sheet sheet : workbook) {
                TopicModel topic = null;
                rounds.add(new RoundModel(sheet.getSheetName()));

                int roundIndex = 0;

                for (Row row : sheet) {
                    if (row.getPhysicalNumberOfCells() < 2) {
//                        questions.put(new TopicModel(row.getCell(row.getFirstCellNum()).getStringCellValue()), new ArrayList<>());
                        if (topic != null) {
                            rounds.get(roundIndex).addItem(topic);
                        }
                        topic = new TopicModel();
                        topic.setName(row.getCell(row.getFirstCellNum()).getStringCellValue());

                    } else {
                        int cellIndex = row.getPhysicalNumberOfCells();
                        QuestionModel question = new QuestionModel();
                        question.setQuestion(row.getCell(cellIndex++).getStringCellValue());
                        question.setAnswer(row.getCell(cellIndex++).getStringCellValue());
                        question.setCost(Integer.parseInt(row.getCell(cellIndex++).getStringCellValue()));

                        String mediaType = row.getCell(cellIndex).getStringCellValue();
                        if (MediaType.image.contains(mediaType)) {
                            question.setTypeOfMedia(MediaType.image);
                            question.setPathToMedia(row.getCell(cellIndex + 1).getStringCellValue());
                            /*create method for replacing image*/
                        }
//                        assert topic != null;
                        topic.addQuestion(question);

                    }

                }

            }

            quiz.setRounds(rounds);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
