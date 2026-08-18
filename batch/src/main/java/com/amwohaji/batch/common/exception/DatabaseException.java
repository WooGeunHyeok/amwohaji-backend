package com.amwohaji.batch.common.exception;

public class DatabaseException extends BatchException {

    public DatabaseException(String message) {
        super(ErrorCode.DB_ERROR, message);
    }
}