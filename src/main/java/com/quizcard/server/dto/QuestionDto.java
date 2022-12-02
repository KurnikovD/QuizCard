package com.quizcard.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class QuestionDto {
    private String id;
    private String question;
    private String mediaType;
    private String mediaPath;
    private Integer cost;
}
