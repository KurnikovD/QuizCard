package com.example.QuizCard.entity.pk;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class TopicId implements Serializable {

    private Integer topicNumber;

    @Embedded
    private RoundId roundId;

}
