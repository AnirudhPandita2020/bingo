package com.anirudh.bingo.exception.game;

import com.anirudh.bingo.exception.BingoException;
import com.anirudh.bingo.exception.ErrorType;

public class GameAlreadyStartedException extends BingoException {
    public GameAlreadyStartedException() {
        super(ErrorType.GAME_ALREADY_STARTED, "Game has already started");
    }
}
