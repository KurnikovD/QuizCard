package com.quizcard.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TopicDto {
    String id;
    String name;
    List<QuestionDto> questions;
    Boolean passed;
}
