package com.anirudh.bingo.event.game;

import com.anirudh.bingo.event.AbstractBingoEvent;
import lombok.Getter;

@Getter
public final class NumberCalledEvent extends AbstractBingoEvent {
    private final int number;
    private final String numberCalledBy;

    public NumberCalledEvent(String roomId, int number, String numberCalledBy) {
        super(roomId);
        this.number = number;
        this.numberCalledBy = numberCalledBy;
    }
}
