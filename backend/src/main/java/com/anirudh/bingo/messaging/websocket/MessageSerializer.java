package com.anirudh.bingo.messaging.websocket;

import com.anirudh.bingo.messaging.common.OutboundMessage;

public interface MessageSerializer {
    String serialize(OutboundMessage message);

    <T> T deserialize(String payload, Class<T> type);
}
