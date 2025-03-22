package com.quizcard.server.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class LoadDataException extends RuntimeException {

    private final List<String> exceptionLogs;

    public LoadDataException(String singleErrorMessage) {
        exceptionLogs = List.of(singleErrorMessage);
    }
}
