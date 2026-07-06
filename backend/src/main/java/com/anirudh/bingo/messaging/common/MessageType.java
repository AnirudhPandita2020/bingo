package com.anirudh.bingo.messaging.common;

public enum MessageType {
    JOIN_ROOM,
    LEAVE_ROOM,
    START_GAME,
    CALL_NUMBER,
    CLAIM_BINGO,
    ROOM_CREATED,
    PLAYER_JOINED,
    PLAYER_LEFT,
    GAME_STARTED,
    NUMBER_CALLED,
    TURN_CHANGED,
    BINGO_CLAIMED,
    GAME_WON,
    ROOM_CLOSED,
    CREATE_ROOM,
    GAME_ENDED, ERROR
}
