package com.quizcard.server.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum MediaType {
    IMAGE(List.of("jpeg", "jpg", "png", "gif")),
    VIDEO(List.of("mp4","avi")),
    AUDIO(List.of("mp3", "wav", "m4a")),
    NONE(List.of("")),
    ;

    private final List<String> extensions;

    public static MediaType byExtension(String fileExtension) {
        return Arrays.stream(values()).
                filter(it -> it.getExtensions().contains(fileExtension))
                .findFirst()
                .orElse(NONE);
    }

    public boolean isMedia() {
        return this == IMAGE || this == VIDEO || this == AUDIO;
    }
}
