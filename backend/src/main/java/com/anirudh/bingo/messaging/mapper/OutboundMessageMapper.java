package com.anirudh.bingo.messaging.mapper;

import com.anirudh.bingo.event.BingoEvent;
import com.anirudh.bingo.event.game.*;
import com.anirudh.bingo.event.room.PlayerJoinedEvent;
import com.anirudh.bingo.event.room.PlayerLeftRoomEvent;
import com.anirudh.bingo.event.room.RoomClosedEvent;
import com.anirudh.bingo.messaging.common.OutboundMessage;
import com.anirudh.bingo.messaging.outbound.*;
import org.springframework.stereotype.Component;

@Component
public final class OutboundMessageMapper {

    public OutboundMessage map(BingoEvent bingoEvent) {
        return switch (bingoEvent) {
            case PlayerJoinedEvent playerJoinedEvent ->
                    new PlayerJoinedMessage(playerJoinedEvent.getRoomId(), playerJoinedEvent.getPlayer());
            case PlayerLeftRoomEvent playerLeftRoomEvent ->
                    new PlayerLeftMessage(playerLeftRoomEvent.getRoomId(), playerLeftRoomEvent.getPlayer().name());
            case RoomClosedEvent roomClosedEvent -> new RoomClosedMessage(roomClosedEvent.getRoomId());
            case GameStartedEvent gameStartedEvent ->
                    new GameStartedMessage(gameStartedEvent.getRoomId(), gameStartedEvent.getRoom());
            case NumberCalledEvent numberCalledEvent ->
                    new NumberCalledMessage(numberCalledEvent.getRoomId(), numberCalledEvent.getNumberCalledBy(), numberCalledEvent.getNumber());
            case BingoClaimEvent bingoClaimEvent ->
                    new BingoClaimedMessage(bingoClaimEvent.getRoomId(), bingoClaimEvent.getPlayerId(), bingoClaimEvent.isAccepted());
            case GameWonEvent gameWonEvent ->
                    new GameWonMessage(gameWonEvent.getRoomId(), gameWonEvent.getWinnerPlayerId());
            case GameEndedEvent gameEndedEvent -> new GameEndedMessage(gameEndedEvent.getRoomId());
            case TurnChangedEvent turnChangedEvent ->
                    new TurnChangedMessage(turnChangedEvent.getRoomId(), turnChangedEvent.getCurrentPlayerId());
            default -> throw new IllegalArgumentException("Unsupported event type: " + bingoEvent.getClass().getName());
        };
    }
}
