package com.anirudh.bingo.messaging.outbound;

import com.anirudh.bingo.messaging.common.MessageType;
import com.anirudh.bingo.messaging.common.OutboundMessage;
import lombok.Getter;

@Getter
public class BingoClaimedMessage extends OutboundMessage {

    private final String playerId;
    private final boolean accepted;

    public BingoClaimedMessage(String roomId, String playerId, boolean accepted) {
        super(MessageType.BINGO_CLAIMED, roomId);
        this.playerId = playerId;
        this.accepted = accepted;
    }
}