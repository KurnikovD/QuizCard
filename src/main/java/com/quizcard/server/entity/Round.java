package com.quizcard.server.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "round")
@NoArgsConstructor
public class Round {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String name;

    private Integer roundNumber;

    @Transient
    private Boolean passed = false;

    @Transient
    private Integer countOfTopicsPass = 0;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @OneToMany(mappedBy = "round", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Topic> topics = new ArrayList<>();

    public Round(Quiz quiz) {
        this.quiz = quiz;
    }

}
