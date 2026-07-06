package com.anirudh.bingo.event.room;

import com.anirudh.bingo.event.AbstractBingoEvent;
import lombok.Getter;

@Getter
public final class RoomClosedEvent extends AbstractBingoEvent {
    public RoomClosedEvent(String roomId) {
        super(roomId);
    }
}
