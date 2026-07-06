package com.anirudh.bingo.messaging.outbound;

import com.anirudh.bingo.messaging.common.MessageType;
import com.anirudh.bingo.messaging.common.OutboundMessage;
import lombok.Getter;

@Getter
public class TurnChangedMessage extends OutboundMessage {
    private final String currentPlayerId;

    public TurnChangedMessage(String roomId, String currentPlayerId) {
        super(MessageType.TURN_CHANGED, roomId);
        this.currentPlayerId = currentPlayerId;
    }
}