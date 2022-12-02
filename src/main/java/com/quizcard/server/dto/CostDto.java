package com.quizcard.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CostDto {
    private String id;
    private Integer cost;
    private boolean cat;
    private boolean passed;
}
