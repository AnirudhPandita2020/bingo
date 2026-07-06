package com.anirudh.bingo.messaging.websocket;

import lombok.SneakyThrows;
import org.springframework.util.Assert;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A utility class to manage active WebSocket sessions across the application.
 * Provides basic operations to register, purge, and fetch WebSocket sessions using session IDs.
 */
public final class SessionRegistry {

    private static final Map<String, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();
    private static final Map<String, Set<String>> ROOM_SESSIONS = new ConcurrentHashMap<>();
    private static final Map<String, String> SESSION_TO_ROOM = new ConcurrentHashMap<>();

    private SessionRegistry() {
        throw new IllegalStateException("Registry is not instantiable");
    }

    /**
     * Registers a new WebSocketSession into the registry.
     *
     * @param webSocketSession the WebSocketSession to be registered
     * @throws IllegalArgumentException if the session is null or not open
     */
    public static void register(WebSocketSession webSocketSession) {
        Assert.notNull(webSocketSession, "WebSocketSession must not be null");
        Assert.isTrue(webSocketSession.isOpen(), "WebSocketSession must be open");
        SESSIONS.put(webSocketSession.getId(), webSocketSession);
    }

    public static void trackSessionPerRoom(String roomId, WebSocketSession webSocketSession) {
        Assert.notNull(webSocketSession, "WebSocketSession must not be null");
        Assert.isTrue(webSocketSession.isOpen(), "WebSocketSession must be open");
        ROOM_SESSIONS.computeIfAbsent(roomId, ignored -> ConcurrentHashMap.newKeySet()).add(webSocketSession.getId());
        SESSION_TO_ROOM.put(webSocketSession.getId(), roomId);
    }

    /**
     * Removes a session from the registry and closes it with normal status.
     *
     * @param sessionID the ID of the session to purge
     * @throws IllegalArgumentException if the session ID is null or empty
     */
    @SneakyThrows
    public static void purge(String sessionID) {
        Assert.hasText(sessionID, "Session id cannot be empty or null");
        var session = SESSIONS.remove(sessionID);
        if (session != null) {
            session.close(CloseStatus.NORMAL);
        }
        var room = SESSION_TO_ROOM.remove(sessionID);
        if (room != null) {
            ROOM_SESSIONS.getOrDefault(room, Set.of()).remove(sessionID);
        }
    }


    /**
     * Retrieves a list of sessions by a given set of session IDs.
     * Filters out sessions that are not open.
     *
     * @param sessionIDs the set of session IDs to fetch
     * @return a list of sessions that are *not open*
     */
    public static List<WebSocketSession> fetchSession(Set<String> sessionIDs) {
        return sessionIDs.stream()
                .map(SESSIONS::get)
                .filter(Objects::nonNull)
                .filter(WebSocketSession::isOpen)
                .toList();
    }

    public static Optional<String> findRoomId(String sessionID) {
        return Optional.ofNullable(SESSION_TO_ROOM.get(sessionID));
    }

    public static List<WebSocketSession> fetchRoomSessions(String roomId) {
        var activeSessions = ROOM_SESSIONS.getOrDefault(roomId, Set.of())
                .stream()
                .map(SESSIONS::get)
                .filter(Objects::nonNull)
                .filter(WebSocketSession::isOpen)
                .toList();
        Set<String> sessions = ConcurrentHashMap.newKeySet();
        activeSessions.stream().map(WebSocketSession::getId).forEach(sessions::add);
        ROOM_SESSIONS.put(roomId, sessions);
        return activeSessions;
    }

    public static void untrackSessionPerRoom(String roomId, String sessionId) {
        Assert.hasText(roomId, "Room id must not be null or empty");
        Assert.hasText(sessionId, "Session id must not be null or empty");

        ROOM_SESSIONS.computeIfPresent(roomId, (id, sessions) -> {
            sessions.remove(sessionId);
            return sessions.isEmpty() ? null : sessions;
        });

        SESSION_TO_ROOM.remove(sessionId);
    }

    public static void clear() {
        SESSIONS.clear();
        ROOM_SESSIONS.clear();
        SESSION_TO_ROOM.clear();
    }
}