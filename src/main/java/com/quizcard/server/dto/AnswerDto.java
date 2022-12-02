package com.quizcard.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AnswerDto {
    private String id;
    private String answer;
    private String mediaType;
    private String mediaPath;
}
