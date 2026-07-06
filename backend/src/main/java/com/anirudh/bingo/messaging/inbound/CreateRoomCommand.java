package com.anirudh.bingo.messaging.inbound;

import com.anirudh.bingo.messaging.common.InboundMessage;
import com.anirudh.bingo.messaging.common.MessageType;
import lombok.Getter;

@Getter
public class CreateRoomCommand extends InboundMessage {
    private final int maxPlayers;
    private final String playerName;

    public CreateRoomCommand(int maxPlayers, String playerName) {
        super(MessageType.CREATE_ROOM, null);
        this.maxPlayers = maxPlayers;
        this.playerName = playerName;
    }
}
