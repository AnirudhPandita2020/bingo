package com.anirudh.bingo.messaging.websocket;

import com.anirudh.bingo.core.player.Player;
import com.anirudh.bingo.exception.BingoException;
import com.anirudh.bingo.exception.ErrorType;
import com.anirudh.bingo.exception.message.MessageException;
import com.anirudh.bingo.matchmaking.service.RoomService;
import com.anirudh.bingo.messaging.common.InboundMessage;
import com.anirudh.bingo.messaging.inbound.*;
import com.anirudh.bingo.messaging.outbound.ErrorOutboundMessage;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
public class InboundMessageDispatcher {

    private final RoomService roomService;
    private final MessageSerializer messageSerializer;

    @SneakyThrows
    public void dispatch(WebSocketSession session, String payload) {
        String playerId = session.getId();
        try {
            InboundMessage inboundMessage = messageSerializer.deserialize(payload, InboundMessage.class);
            switch (inboundMessage) {
                case CreateRoomCommand createRoomCommand -> {
                    var room = roomService.createRoom(createRoomCommand.getMaxPlayers());
                    joinRoom(session, room.getId(), new Player(playerId, createRoomCommand.getPlayerName()));
                }
                case JoinRoomCommand joinRoomCommand ->
                        joinRoom(session, joinRoomCommand.getRoomId(), joinRoomCommand.getPlayer());
                case LeaveRoomCommand leaveRoomCommand -> roomService.leaveRoom(leaveRoomCommand.getRoomId(), playerId);
                case StartGameCommand startGameCommand -> roomService.startGame(startGameCommand.getRoomId(), playerId);
                case CallNumberCommand callNumberCommand ->
                        roomService.callNumber(callNumberCommand.getRoomId(), playerId, callNumberCommand.getNumber());
                case ClaimBingoCommand claimBingoCommand ->
                        roomService.claimBingo(claimBingoCommand.getRoomId(), playerId);
                default ->
                        throw new IllegalArgumentException("Unknown message: " + inboundMessage.getClass().getName());
            }
        } catch (BingoException exception) {
            ErrorOutboundMessage errorOutboundMessage = new ErrorOutboundMessage(exception.getErrorType(), exception.getMessage());
            session.sendMessage(new TextMessage(messageSerializer.serialize(errorOutboundMessage)));
        } catch (MessageException exception) {
            ErrorOutboundMessage messageErrorOutboundMessage = new ErrorOutboundMessage(ErrorType.INTERNAL_SERVER_ERROR, "Something went wrong.Please try again");
            session.sendMessage(new TextMessage(messageSerializer.serialize(messageErrorOutboundMessage)));
        }
    }

    private void joinRoom(WebSocketSession session, String roomId, Player player) {
        SessionRegistry.trackSessionPerRoom(roomId, session);
        try {
            if (StringUtils.isEmpty(player.id())) {
                player = new Player(session.getId(), player.name());
            }
            roomService.joinRoom(roomId, player);
        } catch (BingoException bingoException) {
            SessionRegistry.untrackSessionPerRoom(roomId, session.getId());
            throw bingoException;
        }
    }

    public void handleDisconnect(String roomId, String playerId) {
        roomService.leaveRoom(roomId, playerId);
    }
}
