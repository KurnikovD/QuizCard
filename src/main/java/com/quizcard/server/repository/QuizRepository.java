package com.quizcard.server.repository;

import com.quizcard.server.entity.Quiz;
import com.quizcard.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, String> {
    List<Quiz> findByIsProtect(boolean isProtect);
    List<Quiz> findAllByUser_Id(String id);

    Quiz findByIdAndUser_Id(String id, String userId);
    List<Quiz> findByTitleContainsIgnoreCase(String title);

    List<Quiz> findByUser_IdOrIsProtect(String id, boolean isProtect);


}
