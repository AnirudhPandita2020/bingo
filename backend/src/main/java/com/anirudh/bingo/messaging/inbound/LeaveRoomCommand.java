package com.anirudh.bingo.messaging.inbound;

import com.anirudh.bingo.core.player.Player;
import com.anirudh.bingo.messaging.common.InboundMessage;
import com.anirudh.bingo.messaging.common.MessageType;
import lombok.Getter;

@Getter
public class LeaveRoomCommand extends InboundMessage {
    private final Player player;

    public LeaveRoomCommand(String roomId, Player player) {
        super(MessageType.LEAVE_ROOM, roomId);
        this.player = player;
    }
}
