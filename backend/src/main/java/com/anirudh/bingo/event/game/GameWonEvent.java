package com.anirudh.bingo.event.game;

import com.anirudh.bingo.event.AbstractBingoEvent;
import lombok.Getter;

@Getter
public final class GameWonEvent extends AbstractBingoEvent {
    private final String winnerPlayerId;

    public GameWonEvent(String roomId, String winnerPlayerId) {
        super(roomId);
        this.winnerPlayerId = winnerPlayerId;
    }
}
