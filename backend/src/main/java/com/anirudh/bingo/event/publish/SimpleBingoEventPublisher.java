package com.anirudh.bingo.event.publish;

import com.anirudh.bingo.event.BingoEvent;
import com.anirudh.bingo.event.listener.BingoEventListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SimpleBingoEventPublisher implements BingoEventPublisher {

    private final List<BingoEventListener<? extends BingoEvent>> listeners;

    @Override
    public <T extends BingoEvent> void publish(T event) {
        listeners.forEach(listener -> {
            if (listener.supports().isAssignableFrom(event.getClass())) {
                invoke(listener, event);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private <T extends BingoEvent> void invoke(BingoEventListener<? extends BingoEvent> listener, T event) {
        ((BingoEventListener<T>) listener).onEvent(event);
    }
}

