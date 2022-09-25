package com.example.QuizCard.entity;


import com.example.QuizCard.entity.pk.RoundId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Getter
@Setter
@Table(name = "round")
@NoArgsConstructor
public class Round {

    @EmbeddedId
    private RoundId id;

    private String name;

    private Integer countOfTopics;

    @Transient
    private Boolean passed = false;

    @Transient
    private Integer countOfTopicsPass = 0;

    public Round(RoundId roundId) {
        this.id = roundId;
    }
}
