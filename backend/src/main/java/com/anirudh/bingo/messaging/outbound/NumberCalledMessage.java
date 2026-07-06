package com.anirudh.bingo.messaging.outbound;

import com.anirudh.bingo.messaging.common.MessageType;
import com.anirudh.bingo.messaging.common.OutboundMessage;
import lombok.Getter;

@Getter
public class NumberCalledMessage extends OutboundMessage {
    private final String calledBy;
    private final int number;

    public NumberCalledMessage(String roomId, String calledBy, int number) {
        super(MessageType.NUMBER_CALLED, roomId);
        this.calledBy = calledBy;
        this.number = number;
    }
}
