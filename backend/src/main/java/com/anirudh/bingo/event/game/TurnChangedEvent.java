package com.anirudh.bingo.event.game;

import com.anirudh.bingo.event.AbstractBingoEvent;
import lombok.Getter;

@Getter
public final class TurnChangedEvent extends AbstractBingoEvent {
    private final String currentPlayerId;

    public TurnChangedEvent(String roomId, String currentPlayerId) {
        super(roomId);
        this.currentPlayerId = currentPlayerId;
    }
}
