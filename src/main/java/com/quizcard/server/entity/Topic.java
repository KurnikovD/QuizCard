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
@NoArgsConstructor
@Table(name = "topic")
public class Topic {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String name;

    private Integer topicNumber;

    @Transient
    private Integer countOfQuestionPass = 0;

    @Transient
    private Boolean passed = false;

    @OneToMany(mappedBy = "topic", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Question> questions = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "round_id")
    private Round round;

    public Topic(Round round) {
        this.round = round;
    }

    public Topic(Round currentRound, int topicNumber, String stringCellValue) {
        this.round = currentRound;
        this.topicNumber = topicNumber;
        this.name = stringCellValue;
    }
}
