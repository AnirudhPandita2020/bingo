package com.anirudh.bingo.messaging.inbound;

import com.anirudh.bingo.messaging.common.InboundMessage;
import com.anirudh.bingo.messaging.common.MessageType;
import lombok.Getter;

@Getter
public class ClaimBingoCommand extends InboundMessage {
    public ClaimBingoCommand(String roomId) {
        super(MessageType.CLAIM_BINGO, roomId);
    }
}
