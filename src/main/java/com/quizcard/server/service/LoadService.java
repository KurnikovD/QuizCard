package com.quizcard.server.service;

import org.springframework.web.multipart.MultipartFile;

public interface LoadService {

    void savePack(MultipartFile file);

    void addData();
    void removeTempDir();
}
