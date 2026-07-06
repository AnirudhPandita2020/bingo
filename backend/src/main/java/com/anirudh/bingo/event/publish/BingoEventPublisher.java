package com.anirudh.bingo.event.publish;

import com.anirudh.bingo.event.BingoEvent;

public interface BingoEventPublisher {
    <T extends BingoEvent> void publish(T event);
}
