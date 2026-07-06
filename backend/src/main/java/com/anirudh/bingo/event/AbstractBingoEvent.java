package com.anirudh.bingo.event;

import java.time.Instant;
import java.util.UUID;

public abstract class AbstractBingoEvent implements BingoEvent {
    private final String eventId = UUID.randomUUID().toString();
    private final Instant occurredAt = Instant.now();
    private final String roomId;

    protected AbstractBingoEvent(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String getRoomId() {
        return roomId;
    }
}
