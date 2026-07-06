package com.anirudh.bingo.messaging.outbound;

import com.anirudh.bingo.messaging.common.MessageType;
import com.anirudh.bingo.messaging.common.OutboundMessage;
import lombok.Getter;

@Getter
public class RoomCreatedMessage extends OutboundMessage {
    private final int maxPlayers;

    public RoomCreatedMessage(String roomId, int maxPlayers) {
        super(MessageType.ROOM_CREATED, roomId);
        this.maxPlayers = maxPlayers;
    }
}
