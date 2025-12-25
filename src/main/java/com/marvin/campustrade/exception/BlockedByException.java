package com.marvin.campustrade.exception;

public class BlockedByException extends RuntimeException {
    public BlockedByException(String message) {
        super(message);
    }
}