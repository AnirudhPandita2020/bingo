package com.anirudh.bingo.messaging.outbound;

import com.anirudh.bingo.core.room.Room;
import com.anirudh.bingo.messaging.common.MessageType;
import com.anirudh.bingo.messaging.common.OutboundMessage;
import lombok.Getter;

public class GameStartedMessage extends OutboundMessage {
    @Getter
    private final Room room;

    public GameStartedMessage(String roomId, Room room) {
        super(MessageType.GAME_STARTED, roomId);
        this.room = room;
    }
}
