package com.anirudh.bingo.event.listener;

import com.anirudh.bingo.event.BingoEvent;

public interface BingoEventListener<T extends BingoEvent> {
    void onEvent(T event);

    Class<T> supports();
}
