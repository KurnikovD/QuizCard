package com.example.QuizCard.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionModel {
    private String question;
    private String answer;
    private String typeOfMedia;
    private String pathToMedia;
    private Integer cost;
}
