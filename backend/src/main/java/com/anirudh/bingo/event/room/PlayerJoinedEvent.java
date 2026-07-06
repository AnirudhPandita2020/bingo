package com.anirudh.bingo.event.room;

import com.anirudh.bingo.core.player.Player;
import com.anirudh.bingo.event.AbstractBingoEvent;
import lombok.Getter;

@Getter
public final class PlayerJoinedEvent extends AbstractBingoEvent {
    private final Player player;

    public PlayerJoinedEvent(String roomId, Player player) {
        super(roomId);
        this.player = player;
    }
}
