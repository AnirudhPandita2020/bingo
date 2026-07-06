package com.anirudh.bingo.messaging.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class OutboundMessage extends Message {
    protected OutboundMessage(MessageType messageType, String roomId) {
        super(messageType, roomId);
    }
}
