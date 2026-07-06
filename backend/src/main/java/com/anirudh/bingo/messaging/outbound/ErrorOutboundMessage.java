package com.anirudh.bingo.messaging.outbound;

import com.anirudh.bingo.exception.ErrorType;
import com.anirudh.bingo.messaging.common.MessageType;
import com.anirudh.bingo.messaging.common.OutboundMessage;
import lombok.Getter;

@Getter
public class ErrorOutboundMessage extends OutboundMessage {
    private final ErrorType errorType;
    private final String message;

    public ErrorOutboundMessage(ErrorType errorType, String message) {
        super(MessageType.ERROR, null);
        this.errorType = errorType;
        this.message = message;
    }
}
