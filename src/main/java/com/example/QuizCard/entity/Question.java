package com.example.QuizCard.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String question;

    private String answer;

    private Integer cost;

    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    private String mediaExtensionPath;

    @Transient
    private Boolean passed = false;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "topic_id")
    private Topic topic;

}
