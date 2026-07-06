package com.anirudh.bingo.exception.room;

import com.anirudh.bingo.exception.BingoException;
import com.anirudh.bingo.exception.ErrorType;

public class PlayerAlreadyInRoomException extends BingoException {
    public PlayerAlreadyInRoomException(String message) {
        super(ErrorType.PLAYER_ALREADY_IN_ROOM, message);
    }
}
