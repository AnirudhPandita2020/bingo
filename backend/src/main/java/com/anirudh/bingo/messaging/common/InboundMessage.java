package com.anirudh.bingo.messaging.common;

import com.anirudh.bingo.messaging.inbound.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "messageType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CallNumberCommand.class, name = "CALL_NUMBER"),
        @JsonSubTypes.Type(value = ClaimBingoCommand.class, name = "CLAIM_BINGO"),
        @JsonSubTypes.Type(value = JoinRoomCommand.class, name = "JOIN_ROOM"),
        @JsonSubTypes.Type(value = LeaveRoomCommand.class, name = "LEAVE_ROOM"),
        @JsonSubTypes.Type(value = StartGameCommand.class, name = "START_GAME"),
        @JsonSubTypes.Type(value = CreateRoomCommand.class, name = "CREATE_ROOM")
})
public abstract class InboundMessage extends Message {
    protected InboundMessage(MessageType messageType, String roomId) {
        super(messageType, roomId);
    }
}
