package com.example.QuizCard.entity.pk;

import com.example.QuizCard.entity.Quiz;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class QuestionId implements Serializable {

    private Integer questionNumber;


    @Embedded
    private TopicId topicId;

}
