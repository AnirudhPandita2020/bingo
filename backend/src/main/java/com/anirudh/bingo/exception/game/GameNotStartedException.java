package com.anirudh.bingo.exception.game;

import com.anirudh.bingo.exception.BingoException;
import com.anirudh.bingo.exception.ErrorType;

public class GameNotStartedException extends BingoException {
    public GameNotStartedException() {
        super(ErrorType.GAME_NOT_STARTED, "Game is not in progress");
    }
}
