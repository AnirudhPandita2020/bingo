package com.anirudh.bingo.exception.room;

import com.anirudh.bingo.exception.BingoException;
import com.anirudh.bingo.exception.ErrorType;

public class RoomClosedException extends BingoException {
    public RoomClosedException() {
        super(ErrorType.ROOM_CLOSED, "Room is not accepting players");
    }
}
