package com.anirudh.bingo.exception.room;

import com.anirudh.bingo.exception.BingoException;
import com.anirudh.bingo.exception.ErrorType;

public class RoomNotFoundException extends BingoException {
    public RoomNotFoundException(String roomId) {
        super(ErrorType.ROOM_NOT_FOUND, "Room not found: " + roomId);
    }
}
