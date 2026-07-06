package com.anirudh.bingo.matchmaking.service;

import com.anirudh.bingo.core.player.Player;
import com.anirudh.bingo.core.room.Room;
import com.anirudh.bingo.core.room.RoomStatus;
import com.anirudh.bingo.event.game.*;
import com.anirudh.bingo.event.publish.BingoEventPublisher;
import com.anirudh.bingo.event.room.PlayerJoinedEvent;
import com.anirudh.bingo.event.room.PlayerLeftRoomEvent;
import com.anirudh.bingo.event.room.RoomClosedEvent;
import com.anirudh.bingo.exception.room.RoomNotFoundException;
import com.anirudh.bingo.matchmaking.repository.RoomRepository;
import com.anirudh.bingo.matchmaking.util.RoomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final BingoEventPublisher eventPublisher;

    public Room createRoom(int maxPlayers) {
        log.info("RoomService: createRoom: Creating a new room with max {} player capacity", maxPlayers);
        var newRoom = Room.create(RoomUtil.generateRoomId(), maxPlayers);
        Room room = roomRepository.save(newRoom);
        log.info("RoomService: createRoom: Room created with roomId {}", room.getId());
        return room;
    }

    public Optional<Room> getRoom(String roomId) {
        log.info("RoomService: getRoom: Getting room with roomId {}", roomId);
        return roomRepository.findById(roomId);
    }

    public void joinRoom(String roomId, Player player) {
        log.info("RoomService: joinRoom: Player {} joining room with roomId {}", player.id(), roomId);
        var room = getRoomOrThrow(roomId);
        room.join(player);
        log.info("RoomService: joinRoom: Player {} joined room with roomId {}", player.id(), roomId);
        roomRepository.save(room);
        eventPublisher.publish(new PlayerJoinedEvent(roomId, player));
    }

    public void leaveRoom(String roomId, String playerId) {
        log.info("RoomService: leaveRoom: Player {} leaving room with roomId {}", playerId, roomId);
        var room = getRoomOrThrow(roomId);
        var leftPlayer = room.leave(playerId);
        log.info("RoomService: leaveRoom: Player {} left room with roomId {}", playerId, roomId);
        if (RoomStatus.CLOSED == room.getRoomStatus()) {
            log.info("RoomService: leaveRoom: Room status CLOSED. Deleting room with roomId {}", roomId);
            roomRepository.delete(roomId);
            eventPublisher.publish(new PlayerLeftRoomEvent(roomId, leftPlayer));
            eventPublisher.publish(new RoomClosedEvent(roomId));
            return;
        }
        roomRepository.save(room);
        eventPublisher.publish(new PlayerLeftRoomEvent(roomId, leftPlayer));
    }

    public void startGame(String roomId, String hostPlayerId) {
        log.info("RoomService: startGame: Starting game with roomId {}", roomId);
        var room = getRoomOrThrow(roomId);
        room.startGame(hostPlayerId);
        log.info("RoomService: startGame: Game started. Room with roomId {}", roomId);
        roomRepository.save(room);
        eventPublisher.publish(new GameStartedEvent(roomId, room));
    }

    public void callNumber(String roomId, String playerId, int number) {
        var room = getRoomOrThrow(roomId);
        room.callNumber(playerId, number);
        roomRepository.save(room);
        eventPublisher.publish(new NumberCalledEvent(roomId, number, playerId));
        eventPublisher.publish(new TurnChangedEvent(roomId, room.currentPlayer().getPlayer().id()));
    }

    public boolean claimBingo(String roomId, String playerId) {
        log.info("RoomService: claimBingo: Claiming Bingo with roomId {} by player: {}", roomId, playerId);
        var room = getRoomOrThrow(roomId);
        boolean accepted = room.claimBingo(playerId);
        roomRepository.save(room);
        eventPublisher.publish(new BingoClaimEvent(roomId, playerId, accepted));
        if (accepted) {
            eventPublisher.publish(new GameWonEvent(roomId, playerId));
            eventPublisher.publish(new GameEndedEvent(roomId));
        }
        return accepted;
    }

    private Room getRoomOrThrow(String roomId) {
        return roomRepository.findById(roomId).orElseThrow(() -> new RoomNotFoundException(roomId));
    }
}
