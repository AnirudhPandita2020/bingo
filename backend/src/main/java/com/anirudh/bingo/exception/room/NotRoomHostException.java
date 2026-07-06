package com.anirudh.bingo.exception.room;

import com.anirudh.bingo.exception.BingoException;
import com.anirudh.bingo.exception.ErrorType;

public class NotRoomHostException extends BingoException {
    public NotRoomHostException() {
        super(ErrorType.NOT_ROOM_HOST, "Only the host can start the game");
    }
}
