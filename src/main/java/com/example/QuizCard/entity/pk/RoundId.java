package com.example.QuizCard.entity.pk;

import com.example.QuizCard.entity.Quiz;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class RoundId implements Serializable {

    private Integer roundNumber;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    public RoundId(Quiz quiz, Integer roundNumber) {
        this.roundNumber = roundNumber;
        this.quiz = quiz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RoundId entity = (RoundId) o;
        return Objects.equals(this.quiz, entity.quiz) &&
                Objects.equals(this.roundNumber, entity.roundNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quiz, roundNumber);
    }
}
