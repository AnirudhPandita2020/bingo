package com.anirudh.bingo.messaging.outbound;

import com.anirudh.bingo.messaging.common.MessageType;
import com.anirudh.bingo.messaging.common.OutboundMessage;

public class RoomClosedMessage extends OutboundMessage {

    public RoomClosedMessage(String roomId) {
        super(MessageType.ROOM_CLOSED, roomId);
    }
}