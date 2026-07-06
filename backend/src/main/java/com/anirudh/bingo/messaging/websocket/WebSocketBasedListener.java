package com.anirudh.bingo.messaging.websocket;

import com.anirudh.bingo.event.AbstractBingoEvent;
import com.anirudh.bingo.event.listener.BingoEventListener;
import com.anirudh.bingo.messaging.common.OutboundMessage;
import com.anirudh.bingo.messaging.mapper.OutboundMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
public class WebSocketBasedListener implements BingoEventListener<AbstractBingoEvent> {

    private final OutboundMessageMapper outboundMessageMapper;
    private final MessageSerializer messageSerializer;

    @Override
    public void onEvent(AbstractBingoEvent event) {
        OutboundMessage message = outboundMessageMapper.map(event);
        String serializedMessage = messageSerializer.serialize(message);
        TextMessage textMessage = new TextMessage(serializedMessage);
        SessionRegistry.fetchRoomSessions(event.getRoomId()).forEach(session -> send(session, textMessage));
    }

    @Override
    public Class<AbstractBingoEvent> supports() {
        return AbstractBingoEvent.class;
    }

    @SneakyThrows
    private void send(WebSocketSession session, TextMessage message) {
        session.sendMessage(message);
    }
}
