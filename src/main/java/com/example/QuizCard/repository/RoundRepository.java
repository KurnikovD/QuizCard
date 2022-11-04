package com.example.QuizCard.repository;

import com.example.QuizCard.entity.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoundRepository extends JpaRepository<Round, String> {
    List<Round> findByQuiz_Id(String id);

}
