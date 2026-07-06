package com.anirudh.bingo.messaging.inbound;

import com.anirudh.bingo.messaging.common.InboundMessage;
import com.anirudh.bingo.messaging.common.MessageType;
import lombok.Getter;

@Getter
public class CallNumberCommand extends InboundMessage {
    private final int number;

    public CallNumberCommand(String roomId, int number) {
        super(MessageType.CALL_NUMBER, roomId);
        this.number = number;
    }
}
