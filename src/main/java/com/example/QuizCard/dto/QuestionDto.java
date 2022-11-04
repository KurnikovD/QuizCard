package com.example.QuizCard.dto;

import com.example.QuizCard.entity.MediaType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionDto {
    String id;
    String question;
    String answer;
    Integer cost;
    String mediaType;
    String mediaPath;
    Boolean passed;

    public QuestionDto(String id, String question, String answer, Integer cost, MediaType mediaType, String mediaPath, Boolean passed) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.cost = cost;
        this.mediaPath = mediaPath;
        this.mediaType = mediaType.toString();
        this.passed = passed;
    }


}
