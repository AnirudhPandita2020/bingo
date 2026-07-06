package com.anirudh.bingo.exception.room;

import com.anirudh.bingo.exception.BingoException;
import com.anirudh.bingo.exception.ErrorType;

public class PlayerNotInRoomException extends BingoException {
    public PlayerNotInRoomException() {
        super(ErrorType.PLAYER_NOT_IN_ROOM, "Player is not part of the room");
    }
}
