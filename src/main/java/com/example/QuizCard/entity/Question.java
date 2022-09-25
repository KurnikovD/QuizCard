package com.example.QuizCard.entity;

import com.example.QuizCard.entity.pk.QuestionId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Getter
@Setter
@Table(name = "question")
public class Question {

    @EmbeddedId
    private QuestionId id;

    private String question;

    private String answer;

    private Integer cost;

    private String mediaType;

    private String mediaPath;

    @Transient
    private Boolean passed = false;


}
