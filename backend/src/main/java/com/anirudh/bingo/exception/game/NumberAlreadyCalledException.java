package com.anirudh.bingo.exception.game;

import com.anirudh.bingo.exception.BingoException;
import com.anirudh.bingo.exception.ErrorType;

public class NumberAlreadyCalledException extends BingoException {
    public NumberAlreadyCalledException() {
        super(ErrorType.NUMBER_ALREADY_CALLED, "Number has been already called");
    }
}
