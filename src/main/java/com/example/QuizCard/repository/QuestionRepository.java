package com.example.QuizCard.repository;

import com.example.QuizCard.entity.Question;
import com.example.QuizCard.entity.pk.QuestionId;
import com.example.QuizCard.entity.pk.RoundId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, QuestionId> {


    @Override
    Optional<Question> findById(QuestionId questionId);

    List<Question> findById_TopicId_RoundIdAndId_TopicId_TopicNumber(RoundId roundId, Integer topicNumber);


}
