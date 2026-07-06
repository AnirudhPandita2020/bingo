package com.anirudh.bingo.messaging.mapper;

import com.anirudh.bingo.core.player.Player;
import com.anirudh.bingo.core.room.Room;
import com.anirudh.bingo.event.BingoEvent;
import com.anirudh.bingo.event.game.*;
import com.anirudh.bingo.event.room.PlayerJoinedEvent;
import com.anirudh.bingo.event.room.PlayerLeftRoomEvent;
import com.anirudh.bingo.event.room.RoomClosedEvent;
import com.anirudh.bingo.messaging.outbound.*;
import com.anirudh.bingo.utils.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class OutboundMessageMapperTest {

    private final OutboundMessageMapper mapper = new OutboundMessageMapper();

    @Test
    void shouldMapPlayerJoinedEvent() {
        Player player = new Player("player-1", "Anirudh");
        PlayerJoinedEvent event = new PlayerJoinedEvent("ROOM", player);

        PlayerJoinedMessage message = (PlayerJoinedMessage) mapper.map(event);

        assertEquals("ROOM", message.getRoomId());
        assertEquals(player, message.getPlayer());
    }

    @Test
    void shouldMapPlayerLeftRoomEvent() {
        Player player = new Player("player-1", "Anirudh");
        PlayerLeftRoomEvent event = new PlayerLeftRoomEvent("ROOM", player);

        PlayerLeftMessage message = (PlayerLeftMessage) mapper.map(event);

        assertEquals("ROOM", message.getRoomId());
        assertEquals("Anirudh", message.getPlayerName());
    }

    @Test
    void shouldMapRoomClosedEvent() {
        RoomClosedEvent event = new RoomClosedEvent("ROOM");

        RoomClosedMessage message = (RoomClosedMessage) mapper.map(event);

        assertEquals("ROOM", message.getRoomId());
    }

    @Test
    void shouldMapGameStartedEvent() {
        Room room = Room.create("ROOM", 2);
        GameStartedEvent event = new GameStartedEvent("ROOM", room);

        GameStartedMessage message = (GameStartedMessage) mapper.map(event);

        assertEquals("ROOM", message.getRoomId());
        assertSame(room, message.getRoom());
    }

    @Test
    void shouldMapNumberCalledEvent() {
        NumberCalledEvent event = new NumberCalledEvent("ROOM", 7, "host");

        NumberCalledMessage message = (NumberCalledMessage) mapper.map(event);

        assertEquals("ROOM", message.getRoomId());
        assertEquals("host", message.getCalledBy());
        assertEquals(7, message.getNumber());
    }

    @Test
    void shouldMapBingoClaimEvent() {
        BingoClaimEvent event = new BingoClaimEvent("ROOM", "player", true);

        BingoClaimedMessage message = (BingoClaimedMessage) mapper.map(event);

        assertEquals("ROOM", message.getRoomId());
        assertEquals("player", message.getPlayerId());
        assertTrue(message.isAccepted());
    }

    @Test
    void shouldMapGameWonEvent() {
        GameWonEvent event = new GameWonEvent("ROOM", "winner");

        GameWonMessage message = (GameWonMessage) mapper.map(event);

        assertEquals("ROOM", message.getRoomId());
        assertEquals("winner", message.getWinnerId());
    }

    @Test
    void shouldMapGameEndedEvent() {
        GameEndedEvent event = new GameEndedEvent("ROOM");

        GameEndedMessage message = (GameEndedMessage) mapper.map(event);

        assertEquals("ROOM", message.getRoomId());
    }

    @Test
    void shouldMapTurnChangedEvent() {
        TurnChangedEvent event = new TurnChangedEvent("ROOM", "player");

        TurnChangedMessage message = (TurnChangedMessage) mapper.map(event);

        assertEquals("ROOM", message.getRoomId());
        assertEquals("player", message.getCurrentPlayerId());
    }

    @Test
    void shouldThrowWhenUnsupportedEventProvided() {
        BingoEvent event = new BingoEvent() {
            @Override
            public String getEventId() {
                return "";
            }

            @Override
            public Instant occurredAt() {
                return null;
            }

            @Override
            public String getRoomId() {
                return "";
            }
        };

        assertThrows(IllegalArgumentException.class, () -> mapper.map(event));
    }
}