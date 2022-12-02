package com.quizcard.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DeskDto {
    private String id;
    private String name;
    private List<CostDto> costs;
}
