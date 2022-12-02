package com.quizcard.server.dto;

import com.quizcard.server.entity.Round;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoundDto {
    private String id;
    private String name;
    private Integer roundNumber;
    private Boolean passed;

    private List<DeskDto> desk;

    public RoundDto(String id, String name, Integer roundNumber, Boolean passed) {
        this.id = id;
        this.name = name;
        this.roundNumber = roundNumber;
        this.passed = passed;
    }

    public static RoundDto toRoundDto(Round round) {
        return new RoundDto(round.getId(),
                round.getName(),
                round.getRoundNumber(),
                false);
    }
}
