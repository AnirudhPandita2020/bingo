package com.anirudh.bingo.messaging.outbound;

import com.anirudh.bingo.messaging.common.MessageType;
import com.anirudh.bingo.messaging.common.OutboundMessage;
import lombok.Getter;

@Getter
public class PlayerLeftMessage extends OutboundMessage {
    private final String playerName;

    public PlayerLeftMessage(String roomId, String playerName) {
        super(MessageType.PLAYER_LEFT, roomId);
        this.playerName = playerName;
    }
}
