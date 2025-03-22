package com.quizcard.server.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(LoadDataException.class)
    @ResponseBody
    public String handleLoadDataException(LoadDataException e) {
        return String.join("\n", e.getExceptionLogs());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handleException(Exception e) {
        e.printStackTrace();
        return "unexpected error";
    }
}
