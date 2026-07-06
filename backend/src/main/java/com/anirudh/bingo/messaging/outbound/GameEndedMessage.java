package com.anirudh.bingo.messaging.outbound;

import com.anirudh.bingo.messaging.common.MessageType;
import com.anirudh.bingo.messaging.common.OutboundMessage;

public class GameEndedMessage extends OutboundMessage {
    public GameEndedMessage(String roomId) {
        super(MessageType.GAME_ENDED, roomId);
    }
}
