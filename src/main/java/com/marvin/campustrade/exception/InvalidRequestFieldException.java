package com.marvin.campustrade.exception;

public class InvalidRequestFieldException extends RuntimeException {
    public InvalidRequestFieldException(String message) {
        super(message);
    }
}
