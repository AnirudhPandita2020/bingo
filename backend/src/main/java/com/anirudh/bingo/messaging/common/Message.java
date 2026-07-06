package com.anirudh.bingo.messaging.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Message {
    private final MessageType messageType;
    private final String roomId;
}
