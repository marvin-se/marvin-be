package com.marvin.campustrade.exception;

public class NoActiveSessionException extends RuntimeException {
    public NoActiveSessionException(String message) {
        super(message);
    }
}
