package com.quizcard.server.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private Integer cost;
    private boolean isCat = false;
    private String catMediaPath;

    private String question;
    @Enumerated(EnumType.STRING)
    private MediaType questionMediaType = MediaType.NONE;
    private String questionMediaPath;

    private String answer;
    @Enumerated(EnumType.STRING)
    private MediaType answerMediaType = MediaType.NONE;
    private String answerMediaPath;

    @Transient
    private Boolean passed = false;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    public Question(Topic topic) {
        this.topic = topic;
    }

}
