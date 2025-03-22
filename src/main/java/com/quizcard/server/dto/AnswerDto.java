package com.quizcard.server.dto;

import com.quizcard.server.entity.Question;
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

    public AnswerDto(Question question, String media) {
        this.id = question.getId();
        this.answer = question.getAnswer();
        this.mediaType = question.getAnswerMediaType().toString();
        if (question.getAnswerMediaType().isMedia()) {
            this.mediaPath = media + question.getQuiz().getId() + "/" + question.getId() +
                    question.getAnswerMediaPath();
        }
    }
}
