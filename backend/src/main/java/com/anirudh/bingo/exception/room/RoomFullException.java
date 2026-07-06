package com.anirudh.bingo.exception.room;

import com.anirudh.bingo.exception.BingoException;
import com.anirudh.bingo.exception.ErrorType;

public class RoomFullException extends BingoException {
    public RoomFullException() {
        super(ErrorType.ROOM_FULL, "Room is full");
    }
}
