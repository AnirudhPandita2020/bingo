package com.anirudh.bingo.messaging.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(com.anirudh.bingo.utils.CamelCaseDisplayNameGenerator.class)
class SessionRegistryTest {

    @BeforeEach
    void setUp() {
        SessionRegistry.clear();
    }

    @Test
    void shouldRegisterSession() {
        WebSocketSession session = mock(WebSocketSession.class);

        when(session.getId()).thenReturn("session-1");
        when(session.isOpen()).thenReturn(true);

        SessionRegistry.register(session);

        List<WebSocketSession> sessions = SessionRegistry.fetchSession(Set.of("session-1"));

        assertEquals(1, sessions.size());
        assertSame(session, sessions.getFirst());
    }

    @Test
    void shouldThrowWhenRegisteringNullSession() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SessionRegistry.register(null)
        );
    }

    @Test
    void shouldThrowWhenRegisteringClosedSession() {
        WebSocketSession session = mock(WebSocketSession.class);

        when(session.isOpen()).thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> SessionRegistry.register(session)
        );
    }

    @Test
    void shouldTrackSessionToRoom() {
        WebSocketSession session = mock(WebSocketSession.class);

        when(session.getId()).thenReturn("session-1");
        when(session.isOpen()).thenReturn(true);

        SessionRegistry.register(session);
        SessionRegistry.trackSessionPerRoom("ROOM", session);

        Optional<String> roomId = SessionRegistry.findRoomId("session-1");

        assertTrue(roomId.isPresent());
        assertEquals("ROOM", roomId.get());
    }

    @Test
    void shouldTrackMultipleSessionsInSameRoom() {
        WebSocketSession session1 = mock(WebSocketSession.class);
        WebSocketSession session2 = mock(WebSocketSession.class);

        when(session1.getId()).thenReturn("session-1");
        when(session1.isOpen()).thenReturn(true);

        when(session2.getId()).thenReturn("session-2");
        when(session2.isOpen()).thenReturn(true);

        SessionRegistry.register(session1);
        SessionRegistry.register(session2);

        SessionRegistry.trackSessionPerRoom("ROOM", session1);
        SessionRegistry.trackSessionPerRoom("ROOM", session2);

        List<WebSocketSession> sessions = SessionRegistry.fetchRoomSessions("ROOM");

        assertEquals(2, sessions.size());
        assertTrue(sessions.contains(session1));
        assertTrue(sessions.contains(session2));
    }

    @Test
    void shouldThrowWhenTrackingNullSession() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SessionRegistry.trackSessionPerRoom("ROOM", null)
        );
    }

    @Test
    void shouldThrowWhenTrackingClosedSession() {
        WebSocketSession session = mock(WebSocketSession.class);

        when(session.isOpen()).thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> SessionRegistry.trackSessionPerRoom("ROOM", session)
        );
    }

    @Test
    void shouldReturnTrackedSessions() {
        WebSocketSession session = mock(WebSocketSession.class);

        when(session.getId()).thenReturn("session-1");
        when(session.isOpen()).thenReturn(true);

        SessionRegistry.register(session);
        SessionRegistry.trackSessionPerRoom("ROOM", session);

        List<WebSocketSession> sessions = SessionRegistry.fetchRoomSessions("ROOM");

        assertEquals(1, sessions.size());
        assertSame(session, sessions.getFirst());
    }

    @Test
    void shouldIgnoreClosedSessions() {
        WebSocketSession session = mock(WebSocketSession.class);

        when(session.getId()).thenReturn("session-1");
        when(session.isOpen()).thenReturn(false);


        assertThrows(IllegalArgumentException.class, () -> SessionRegistry.register(session));
    }

    @Test
    void shouldReturnEmptyWhenRoomDoesNotExist() {
        List<WebSocketSession> sessions = SessionRegistry.fetchRoomSessions("UNKNOWN");

        assertTrue(sessions.isEmpty());
    }

    @Test
    void shouldReturnRoomIdForTrackedSession() {
        WebSocketSession session = mock(WebSocketSession.class);

        when(session.getId()).thenReturn("session-1");
        when(session.isOpen()).thenReturn(true);

        SessionRegistry.register(session);
        SessionRegistry.trackSessionPerRoom("ROOM", session);

        Optional<String> roomId = SessionRegistry.findRoomId("session-1");

        assertTrue(roomId.isPresent());
        assertEquals("ROOM", roomId.get());
    }

    @Test
    void shouldReturnEmptyForUnknownSession() {
        assertTrue(SessionRegistry.findRoomId("unknown").isEmpty());
    }

    @Test
    void shouldRemoveSessionFromRoom() {
        WebSocketSession session = mock(WebSocketSession.class);

        when(session.getId()).thenReturn("session-1");
        when(session.isOpen()).thenReturn(true);

        SessionRegistry.register(session);
        SessionRegistry.trackSessionPerRoom("ROOM", session);

        SessionRegistry.untrackSessionPerRoom("ROOM", "session-1");

        assertTrue(SessionRegistry.fetchRoomSessions("ROOM").isEmpty());
    }

    @Test
    void shouldRemoveSessionToRoomMapping() {
        WebSocketSession session = mock(WebSocketSession.class);

        when(session.getId()).thenReturn("session-1");
        when(session.isOpen()).thenReturn(true);

        SessionRegistry.register(session);
        SessionRegistry.trackSessionPerRoom("ROOM", session);

        SessionRegistry.untrackSessionPerRoom("ROOM", "session-1");

        assertTrue(SessionRegistry.findRoomId("session-1").isEmpty());
    }

    @Test
    void shouldRemoveRoomWhenLastSessionLeaves() {
        WebSocketSession session = mock(WebSocketSession.class);

        when(session.getId()).thenReturn("session-1");
        when(session.isOpen()).thenReturn(true);

        SessionRegistry.register(session);
        SessionRegistry.trackSessionPerRoom("ROOM", session);

        SessionRegistry.untrackSessionPerRoom("ROOM", "session-1");

        assertTrue(SessionRegistry.fetchRoomSessions("ROOM").isEmpty());
    }

    @Test
    void shouldThrowWhenRoomIdIsEmptyWhileUntracking() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SessionRegistry.untrackSessionPerRoom("", "session-1")
        );
    }

    @Test
    void shouldThrowWhenSessionIdIsEmptyWhileUntracking() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SessionRegistry.untrackSessionPerRoom("ROOM", "")
        );
    }

    @Test
    void shouldRemoveSessionWhenPurged() {
        WebSocketSession session = mock(WebSocketSession.class);

        when(session.getId()).thenReturn("session-1");
        when(session.isOpen()).thenReturn(true);

        SessionRegistry.register(session);

        SessionRegistry.purge("session-1");

        assertTrue(SessionRegistry.fetchSession(Set.of("session-1")).isEmpty());
    }

    @Test
    void shouldCloseSessionWhenPurged() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);

        when(session.getId()).thenReturn("session-1");
        when(session.isOpen()).thenReturn(true);

        SessionRegistry.register(session);

        SessionRegistry.purge("session-1");

        verify(session).close(any());
    }

    @Test
    void shouldRemoveRoomMappingWhenPurged() {
        WebSocketSession session = mock(WebSocketSession.class);

        when(session.getId()).thenReturn("session-1");
        when(session.isOpen()).thenReturn(true);

        SessionRegistry.register(session);
        SessionRegistry.trackSessionPerRoom("ROOM", session);

        SessionRegistry.purge("session-1");

        assertTrue(SessionRegistry.findRoomId("session-1").isEmpty());
        assertTrue(SessionRegistry.fetchRoomSessions("ROOM").isEmpty());
    }

    @Test
    void shouldDoNothingWhenPurgingUnknownSession() {
        assertDoesNotThrow(() -> SessionRegistry.purge("unknown"));
    }

    @Test
    void shouldThrowWhenPurgingEmptySessionId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SessionRegistry.purge("")
        );
    }
}