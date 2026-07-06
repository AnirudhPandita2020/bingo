package com.anirudh.bingo.messaging.outbound;

import com.anirudh.bingo.core.player.Player;
import com.anirudh.bingo.messaging.common.MessageType;
import com.anirudh.bingo.messaging.common.OutboundMessage;
import lombok.Getter;

@Getter
public class PlayerJoinedMessage extends OutboundMessage {
    private final Player player;

    public PlayerJoinedMessage(String roomId, Player player) {
        super(MessageType.PLAYER_JOINED, roomId);
        this.player = player;
    }
}
