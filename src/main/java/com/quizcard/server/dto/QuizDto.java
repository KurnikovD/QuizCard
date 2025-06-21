package com.quizcard.server.dto;

import com.quizcard.server.entity.Quiz;
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

    public QuizDto(Quiz quiz) {
        this.id = quiz.getId();
        this.title = quiz.getTitle();
        this.countOfRounds = quiz.getRounds().size();
        this.isPublic = quiz.isProtect();
    }
}
