package com.example.QuizCard.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "quiz")
public class Quiz {

    private String title;

    private Integer countOfRounds;

    @Transient
    private Integer countOfRoundsPass = 0;

    @Id
    @Column(name = "id", nullable = false)
    private Long id;


}
