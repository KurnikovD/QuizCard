package com.example.QuizCard.service;

import com.example.QuizCard.entity.*;
import com.example.QuizCard.repository.QuestionRepository;
import com.example.QuizCard.repository.QuizRepository;
import com.example.QuizCard.repository.RoundRepository;
import com.example.QuizCard.repository.TopicRepository;
import com.jlefebure.spring.boot.minio.MinioConfigurationProperties;
import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
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
                    writeFile(zipInputStream, buffer, entry, newFile);

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

    private void writeFile(ZipInputStream zipInputStream, byte[] buffer, ZipEntry entry, File newFile) throws IOException {
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
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(dirTempPath + dataFileName));
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

            quiz = quizRepository.save(quiz);
            buildMinio();

            List<Round> rounds = new ArrayList<>();
            int roundNumber = 0;

            for (Sheet sheet : workbook) {
                Round currentRound = saveRound(sheet, roundNumber++);

                int topicNumber = 0;
                Topic currentTopic = null;

                for (Row row : sheet) {
                    if (row.getPhysicalNumberOfCells() < 2) {
                        currentTopic = saveTopic(row, currentRound, topicNumber++);
                    } else {
                        if (currentTopic == null) {
                            throw new RuntimeException("Topic is null:167 - " + sheet.getSheetName() + " " + row.getRowNum());
                        }
                        saveQuestion(currentTopic, row);
                    }
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!exceptionLog.isEmpty()) {
            throw new IllegalArgumentException(exceptionLog);
//            try {
//                minioClient.removeBucket(
//                        RemoveBucketArgs.builder()
//                                .bucket(quiz.getId())
//                                .build());
//            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
//                     InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
//                     XmlParserException e) {
//                throw new RuntimeException(e);
//            }
        }

    }

    private Round saveRound(Sheet sheet, int roundNumber) {
        Round round = new Round();
        round.setName(sheet.getSheetName());
        round.setQuiz(quiz);
        round.setRoundNumber(roundNumber++);
        return roundRepository.save(round);
    }

    private Topic saveTopic(Row row, Round currentRound, int topicNumber) {
        Topic topic = new Topic();
        topic.setRound(currentRound);
        topic.setTopicNumber(topicNumber++);
        topic.setName(row.getCell(0).getStringCellValue());

        return topicRepository.save(topic);
    }

    private void saveQuestion(Topic currentTopic, Row row) {
        if (currentTopic == null) {
            exceptionLog += "Topic is null:197 - " + row.getRowNum() + "\n";
        }
        Question currentQuestion = new Question();
        currentQuestion.setTopic(currentTopic);
        currentQuestion.setQuestion(row.getCell(0).toString());
        currentQuestion.setAnswer(row.getCell(1).toString());
        currentQuestion.setCost((int) row.getCell(2).getNumericCellValue());
        currentQuestion.setMediaType(MediaType.NONE);
        currentQuestion = questionRepository.save(currentQuestion);


        if (row.getPhysicalNumberOfCells() > 3) {
            String mediaPath = row.getCell(3).getStringCellValue();
            saveMedia(currentQuestion.getId(), mediaPath);
            currentQuestion.setMediaExtensionPath("." + mediaPath.split("\\.")[1]);
            currentQuestion.setMediaType(getMediaType(mediaPath));
            questionRepository.save(currentQuestion);
        }
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
