package com.anirudh.bingo.event.game;

import com.anirudh.bingo.event.AbstractBingoEvent;
import lombok.Getter;

@Getter
public final class GameEndedEvent extends AbstractBingoEvent {
    public GameEndedEvent(String roomId) {
        super(roomId);
    }
}
