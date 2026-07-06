package com.anirudh.bingo.event;

import java.time.Instant;

public interface BingoEvent {
    String getEventId();

    Instant occurredAt();

    String getRoomId();
}
