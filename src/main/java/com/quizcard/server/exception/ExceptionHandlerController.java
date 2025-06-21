package com.quizcard.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(LoadDataException.class)
    @ResponseBody
    public List<String> handleLoadDataException(LoadDataException e) {
        log.error(String.join("\n", e.getExceptionLogs()));
        return e.getExceptionLogs();
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handleException(Exception e) {
        log.error(e.getMessage(), e);
        return "unexpected error";
    }
}
