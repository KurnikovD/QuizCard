package com.example.QuizCard.repository;

import com.example.QuizCard.entity.Round;
import com.example.QuizCard.entity.pk.RoundId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoundRepository extends JpaRepository<Round, RoundId> {
    List<Round> findById_Quiz_Id(Long id);

    Round findById_Quiz_IdAndId_RoundNumber(Long id, Integer roundNumber);

}
