package com.anirudh.bingo.exception.game;

import com.anirudh.bingo.exception.BingoException;
import com.anirudh.bingo.exception.ErrorType;

public class NotPlayersTurnException extends BingoException {
    public NotPlayersTurnException() {
        super(ErrorType.NOT_PLAYERS_TURN, "Current player to play does not match with the caller");
    }
}
