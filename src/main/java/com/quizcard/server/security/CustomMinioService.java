package com.quizcard.server.security;

import com.jlefebure.spring.boot.minio.MinioConfigurationProperties;
import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.RemoveBucketArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
@Service
public class CustomMinioService {

    @Value("${spring.minio.url}")
    private String minioUrl;
    @Value("${spring.minio.access-key}")
    private String minioAccessKey;
    @Value("${spring.minio.secret-key}")
    private String minioSecretKey;

    private MinioService minioService;


    public void createBucket(String bucketId) {
        MinioClient minioClient = new MinioClient.Builder()
                .endpoint(minioUrl)
                .credentials(minioAccessKey, minioSecretKey)
                .build();

        MinioConfigurationProperties minioConfigurationProperties = new MinioConfigurationProperties();
        minioConfigurationProperties.setUrl(minioUrl);
        minioConfigurationProperties.setAccessKey(minioAccessKey);
        minioConfigurationProperties.setSecretKey(minioSecretKey);
        minioConfigurationProperties.setSecure(false);
        minioConfigurationProperties.setBucket(bucketId);


        try {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketId)
                            .build());

            String policyJson = new JSONObject()
                    .put("Version", "2012-10-17")
                    .put("Statement", new JSONArray().put(
                            new JSONObject()
                                    .put("Effect", "Allow")
                                    .put("Principal", new JSONObject().put("AWS", new JSONArray().put("*")))
                                    .put("Action", new JSONArray().put("s3:GetObject"))
                                    .put("Sid", "Public")
                                    .put(
                                            "Resource",
                                            new JSONArray()
                                                    .put(String.format("arn:aws:s3:::%s/*", bucketId))

                                    )
                    ))
                    .toString();

            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder().bucket(bucketId).config(policyJson).build());

        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException | JSONException e) {
            log.error(e.getMessage());
        }


        minioService = new MinioService(minioClient, minioConfigurationProperties);
    }


    public void clearerFiles(List<Path> files, String bucketId) {
        files.forEach(path -> {
            try {
                minioService.remove(path);
            } catch (MinioException e) {
                e.printStackTrace();
            }
        });

        MinioClient minioClient = new MinioClient.Builder()
                .endpoint(minioUrl)
                .credentials(minioAccessKey, minioSecretKey)
                .build();
        try {
            minioClient.removeBucket(
                    RemoveBucketArgs.builder()
                            .bucket(bucketId)
                            .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            e.printStackTrace();
        }
    }

    public void upload(Path to, File from) throws MinioException {
        minioService.upload(to, from);
    }
}
