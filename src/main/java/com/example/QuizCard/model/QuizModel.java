package com.example.QuizCard.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuizModel {
    private String title;
//    private Integer countOfRounds;
    private List<RoundModel> rounds;
    public void addRound(RoundModel round){
        rounds.add(round);
    }
}
