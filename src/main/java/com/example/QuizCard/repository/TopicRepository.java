package com.example.QuizCard.repository;

import com.example.QuizCard.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, String> {
    List<Topic> findByRound_Id(String id);
}
