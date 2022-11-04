package com.example.QuizCard.repository;

import com.example.QuizCard.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, String> {
    List<Quiz> findByTitleContainsIgnoreCase(String title);
}
