package com.anirudh.bingo.exception.message;

public class MessageException extends RuntimeException {
    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
