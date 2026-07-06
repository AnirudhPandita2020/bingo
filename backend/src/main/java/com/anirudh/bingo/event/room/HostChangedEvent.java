package com.anirudh.bingo.event.room;

import com.anirudh.bingo.event.AbstractBingoEvent;
import lombok.Getter;

@Getter
public final class HostChangedEvent extends AbstractBingoEvent {
    private final String previousHostId;
    private final String newHostId;

    public HostChangedEvent(String roomId, String previousHostId, String newHostId) {
        super(roomId);
        this.previousHostId = previousHostId;
        this.newHostId = newHostId;
    }
}
