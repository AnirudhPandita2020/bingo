package com.anirudh.bingo.event.game;

import com.anirudh.bingo.event.AbstractBingoEvent;
import lombok.Getter;

@Getter
public final class BingoClaimEvent extends AbstractBingoEvent {
    private final String playerId;
    private final boolean accepted;

    public BingoClaimEvent(String roomId, String playerId, boolean accepted) {
        super(roomId);
        this.playerId = playerId;
        this.accepted = accepted;
    }
}
