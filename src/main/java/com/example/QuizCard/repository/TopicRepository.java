package com.example.QuizCard.repository;

import com.example.QuizCard.entity.Quiz;
import com.example.QuizCard.entity.Topic;
import com.example.QuizCard.entity.pk.TopicId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, TopicId> {
    List<Topic> findById_RoundId_QuizAndId_RoundId_RoundNumber(Quiz quiz, Integer roundNumber);


}