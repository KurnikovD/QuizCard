package com.quizcard.server.dto;

import com.quizcard.server.entity.Question;
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
    private String mediaPath = "";
    private Integer cost;

    public QuestionDto(Question question, String media) {
        this.id = question.getId();
        this.question = question.getQuestion();
        this.mediaType = question.getQuestionMediaType().toString();
        if (question.getQuestionMediaType().isMedia()) {
            this.mediaPath = media + question.getQuiz().getId() + "/" + question.getId() +
                    question.getQuestionMediaPath();
        }
        this.cost = question.getCost();
    }
}
