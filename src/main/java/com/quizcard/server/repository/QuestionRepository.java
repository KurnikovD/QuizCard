package com.quizcard.server.repository;

import com.quizcard.server.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {

    List<Question> findByTopic_IdOrderByCostAsc(String id);

}
