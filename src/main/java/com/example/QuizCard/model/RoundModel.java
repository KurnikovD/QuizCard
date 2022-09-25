package com.example.QuizCard.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RoundModel {
    private String name;
//    private Integer countOfTopics;
    private List<TopicModel> topics;

    public RoundModel(String name) {
        this.name = name;
    }

    public void addItem(TopicModel topic){
        topics.add(topic);
    }
}
