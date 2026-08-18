package com.amwohaji.batch.common.exception;

import lombok.Getter;

@Getter
public class BatchException extends RuntimeException {

    private final ErrorCode errorCode;

    public BatchException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BatchException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}