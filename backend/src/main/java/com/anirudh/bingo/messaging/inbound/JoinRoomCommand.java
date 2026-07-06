package com.anirudh.bingo.messaging.inbound;

import com.anirudh.bingo.core.player.Player;
import com.anirudh.bingo.messaging.common.InboundMessage;
import com.anirudh.bingo.messaging.common.MessageType;
import lombok.Getter;

@Getter
public class JoinRoomCommand extends InboundMessage {
    private final Player player;

    public JoinRoomCommand(String roomId, Player player) {
        super(MessageType.JOIN_ROOM, roomId);
        this.player = player;
    }
}
