package com.anirudh.bingo.exception.room;

import com.anirudh.bingo.exception.BingoException;
import com.anirudh.bingo.exception.ErrorType;

public class RoomNotInProgressException extends BingoException {
    public RoomNotInProgressException() {
        super(ErrorType.ROOM_NOT_IN_PROGRESS, "No game is currently in progress");
    }
}
