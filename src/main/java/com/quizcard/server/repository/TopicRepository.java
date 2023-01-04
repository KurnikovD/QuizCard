package com.quizcard.server.repository;

import com.quizcard.server.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, String> {
    List<Topic> findByRound_Id(String id);

}
