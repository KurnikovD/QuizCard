package com.quizcard.server.dto;

import com.quizcard.server.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CatDto {
    private String id;
    private String mediaPath;
    private String mediaType;

    public CatDto(Question question, String media) {
        this.id = question.getId();
        this.mediaType = question.getCatMediaType().toString();
        if (question.getCatMediaPath().equals("cat.jpg")) {
            mediaPath = media + "packs/cat.jpg";
        } else {
            mediaPath = media + question.getQuiz().getId() + "/" + question.getId() + question.getCatMediaPath();
        }
    }
}

