package com.quizcard.server.exception;

public class LoadDataException extends Throwable {
    private final String exceptionLog;
    public LoadDataException(String exceptionLog) {
        this.exceptionLog = exceptionLog;
    }

    public String getExceptionLog() {
        return exceptionLog;
    }
}
