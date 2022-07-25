package com.example.QuizCard.repository;

import com.example.QuizCard.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByTitleContainsIgnoreCase(String title);

    @Override
    Optional<Quiz> findById(Long aLong);

    @Override
    List<Quiz> findAll();


}
