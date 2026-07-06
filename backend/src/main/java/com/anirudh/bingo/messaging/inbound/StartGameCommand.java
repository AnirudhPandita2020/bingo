package com.anirudh.bingo.messaging.inbound;

import com.anirudh.bingo.messaging.common.InboundMessage;
import com.anirudh.bingo.messaging.common.MessageType;
import lombok.Getter;

@Getter
public class StartGameCommand extends InboundMessage {
    protected StartGameCommand(String roomId) {
        super(MessageType.START_GAME, roomId);
    }
}
