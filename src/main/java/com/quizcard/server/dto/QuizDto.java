package com.quizcard.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class QuizDto {
    private String id;
    private String title;
    private Integer countOfRounds;
    private boolean isPublic;
}
