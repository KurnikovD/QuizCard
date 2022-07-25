package com.example.QuizCard.entity;

import com.example.QuizCard.entity.pk.TopicId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Getter
@Setter
@Table(name = "topic")
public class Topic {

    @EmbeddedId
    private TopicId id;

    private String name;

    private Integer countOfQuestion;

    @Transient
    private Integer countOfQuestionPass = 0;

    @Transient
    private Boolean passed = false;


}
