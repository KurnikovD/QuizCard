package com.example.QuizCard.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoundDto {
    String id;
    String name;
    Integer roundNumber;
    Boolean passed;

    public RoundDto(String id, String name, Integer roundNumber, Boolean passed) {
        this.id = id;
        this.name = name;
        this.roundNumber = roundNumber;
        this.passed = passed;
    }
}
