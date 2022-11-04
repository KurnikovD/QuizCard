package com.example.QuizCard.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TopicDto {
    String id;
    String name;
    List<QuestionDto> questions;
    Boolean passed;

    public TopicDto(String id, String name, List<QuestionDto> questions, Boolean passed) {
        this.id = id;
        this.name = name;
        this.questions = questions;
        this.passed = passed;
    }
}
