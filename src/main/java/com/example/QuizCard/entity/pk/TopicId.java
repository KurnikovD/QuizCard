package com.example.QuizCard.entity.pk;

import com.example.QuizCard.entity.Round;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class TopicId implements Serializable {

    private Integer topicNumber;

    @Embedded
    private RoundId roundId;

//    @ManyToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "round_id" )
//    private Round round;

}