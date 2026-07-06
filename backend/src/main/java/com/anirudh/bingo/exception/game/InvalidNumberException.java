package com.anirudh.bingo.exception.game;

import com.anirudh.bingo.exception.BingoException;
import com.anirudh.bingo.exception.ErrorType;

public class InvalidNumberException extends BingoException {
    public InvalidNumberException() {
        super(ErrorType.INVALID_NUMBER, "Number must be between 1 and 25");
    }
}
