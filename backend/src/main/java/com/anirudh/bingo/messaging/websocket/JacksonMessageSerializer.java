package com.anirudh.bingo.messaging.websocket;

import com.anirudh.bingo.exception.message.MessageException;
import com.anirudh.bingo.messaging.common.OutboundMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class JacksonMessageSerializer implements MessageSerializer {

    private final ObjectMapper objectMapper;

    @Override
    public String serialize(OutboundMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JacksonException e) {
            throw new MessageException("Unable to serialize message", e);
        }
    }

    @Override
    public <T> T deserialize(String payload, Class<T> type) {
        try {
            return objectMapper.readValue(payload, type);
        } catch (JacksonException e) {
            throw new MessageException("Unable to deserialize message", e);
        }
    }
}
