package com.example.QuizCard.model;

import com.example.QuizCard.entity.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.repository.cdi.Eager;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TopicModel {
    private String name;
//    private String countOfQuestion;
    private List<QuestionModel> questions;

    public TopicModel(String name) {
        this.name = name;
    }

    public void addQuestion(QuestionModel question){
        questions.add(question);
    }
}
