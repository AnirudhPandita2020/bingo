package com.anirudh.bingo.event.game;

import com.anirudh.bingo.core.room.Room;
import com.anirudh.bingo.event.AbstractBingoEvent;
import lombok.Getter;

@Getter
public final class GameStartedEvent extends AbstractBingoEvent {
    private final Room room;

    public GameStartedEvent(String roomId, Room room) {
        super(roomId);
        this.room = room;
    }
}
