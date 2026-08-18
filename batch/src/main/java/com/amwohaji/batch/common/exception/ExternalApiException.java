package com.amwohaji.batch.common.exception;

public class ExternalApiException extends BatchException {

    public ExternalApiException() {
        super(ErrorCode.API_ERROR);
    }

    public ExternalApiException(String message) {
        super(ErrorCode.API_ERROR, message);
    }
}