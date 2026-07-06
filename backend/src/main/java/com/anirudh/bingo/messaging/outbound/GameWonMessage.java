package com.anirudh.bingo.messaging.outbound;

import com.anirudh.bingo.messaging.common.MessageType;
import com.anirudh.bingo.messaging.common.OutboundMessage;
import lombok.Getter;

@Getter
public class GameWonMessage extends OutboundMessage {

    private final String winnerId;

    public GameWonMessage(String roomId, String winnerId) {
        super(MessageType.GAME_WON, roomId);
        this.winnerId = winnerId;
    }
}