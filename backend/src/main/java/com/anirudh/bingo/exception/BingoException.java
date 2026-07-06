package com.anirudh.bingo.exception;

import lombok.Getter;

@Getter
public abstract class BingoException extends RuntimeException {
    private final ErrorType errorType;

    protected BingoException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }
}
