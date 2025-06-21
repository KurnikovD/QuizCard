package com.quizcard.server.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Getter
@RequiredArgsConstructor
@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Bad request")
public class LoadDataException extends RuntimeException {

    private final List<String> exceptionLogs;

    public LoadDataException(String singleErrorMessage) {
        exceptionLogs = List.of(singleErrorMessage);
    }
}
