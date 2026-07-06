package com.anirudh.bingo.messaging.websocket;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class BingoWebSocketHandler extends TextWebSocketHandler {

    private final InboundMessageDispatcher inboundMessageDispatcher;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        SessionRegistry.register(session);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        SessionRegistry.purge(session.getId());
        String playerId = session.getId();
        var roomId = SessionRegistry.findRoomId(playerId);
        roomId.ifPresent(room -> inboundMessageDispatcher.handleDisconnect(room, playerId));
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        inboundMessageDispatcher.dispatch(session, message.getPayload());
    }
}
