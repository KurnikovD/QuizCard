package com.quizcard.server.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

import static com.quizcard.server.util.StringUtil.isBlank;

@Getter
@RequiredArgsConstructor
public enum MediaType {
    IMAGE(List.of("jpeg", "jpg", "png", "gif", "svg")),
    VIDEO(List.of("mp4","avi")),
    AUDIO(List.of("mp3", "wav", "m4a")),
    NONE(List.of("")),
    ;

    private final List<String> extensions;

    public static MediaType byExtension(String extension) {
        if (isBlank(extension)) {
            return NONE;
        }
        return Arrays.stream(values()).
                filter(it -> it.getExtensions().contains(extension))
                .findFirst()
                .orElse(NONE);
    }

    public boolean isMedia() {
        return this == IMAGE || this == VIDEO || this == AUDIO;
    }
}
