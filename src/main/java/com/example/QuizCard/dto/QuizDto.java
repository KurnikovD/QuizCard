package com.example.QuizCard.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizDto {
    String id;
    String title;

    Integer countOfRounds;

    public QuizDto(String id, String title, Integer countOfRound) {
        this.id = id;
        this.title = title;
        this.countOfRounds = countOfRound;

    }
}
