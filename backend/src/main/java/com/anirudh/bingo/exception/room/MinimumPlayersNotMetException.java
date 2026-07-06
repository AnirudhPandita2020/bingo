package com.anirudh.bingo.exception.room;

import com.anirudh.bingo.exception.BingoException;
import com.anirudh.bingo.exception.ErrorType;

public class MinimumPlayersNotMetException extends BingoException {
    public MinimumPlayersNotMetException() {
        super(ErrorType.INSUFFICIENT_PLAYERS, "At least two players are required to start the game");
    }
}
