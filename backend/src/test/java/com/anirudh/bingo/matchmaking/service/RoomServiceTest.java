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
import com.anirudh.bingo.utils.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BingoEventPublisher bingoEventPublisher;

    @InjectMocks
    private RoomService roomService;

    private Player host;
    private Player player;

    @BeforeEach
    void setUp() {
        host = new Player("host", "Host");
        player = new Player("player", "Player");
    }

    @Test
    void shouldCreateRoom() {
        when(roomRepository.save(any())).thenReturn(Room.create("ROOM", 2));
        Room room = roomService.createRoom(2);
        assertNotNull(room);
        assertEquals(2, room.getMaxPlayers());
    }

    @Test
    void shouldGenerateUniqueRoomId() {
        when(roomRepository.save(any())).thenReturn(Room.create("ROOM1", 2));
        Room room1 = roomService.createRoom(2);
        when(roomRepository.save(any())).thenReturn(Room.create("ROOM2", 2));
        Room room2 = roomService.createRoom(2);
        assertNotEquals(room1.getId(), room2.getId());
    }

    @Test
    void shouldPersistCreatedRoom() {
        when(roomRepository.save(any())).thenReturn(Room.create("ROOM1", 2));
        roomService.createRoom(4);
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void shouldReturnRoomWhenPresent() {
        Room room = Room.create("ROOM", 2);
        when(roomRepository.findById("ROOM")).thenReturn(Optional.of(room));
        Optional<Room> result = roomService.getRoom("ROOM");
        assertTrue(result.isPresent());
        assertSame(room, result.get());
        verify(roomRepository).findById("ROOM");
    }

    @Test
    void shouldReturnEmptyWhenRoomAbsent() {
        when(roomRepository.findById("ROOM")).thenReturn(Optional.empty());
        assertTrue(roomService.getRoom("ROOM").isEmpty());
    }

    @Test
    void shouldJoinPlayerAndSaveRoom() {
        Room room = Room.create("ROOM", 2);
        when(roomRepository.findById("ROOM")).thenReturn(Optional.of(room));
        roomService.joinRoom("ROOM", host);
        assertTrue(room.getPlayers().contains(host));
        verify(roomRepository).save(room);
    }

    @Test
    void shouldThrowWhenJoiningUnknownRoom() {
        when(roomRepository.findById("ROOM")).thenReturn(Optional.empty());
        assertThrows(RoomNotFoundException.class, () -> roomService.joinRoom("ROOM", host));
        verify(roomRepository, never()).save(any());
    }

    @Test
    void shouldLeaveRoomAndSave() {
        Room room = Room.create("ROOM", 2);
        room.join(host);
        when(roomRepository.findById("ROOM")).thenReturn(Optional.of(room));
        roomService.leaveRoom("ROOM", host.id());
        assertFalse(room.getPlayers().contains(host));
        verify(roomRepository).delete(room.getId());
    }

    @Test
    void shouldDeleteRoomWhenClosed() {
        Room room = Room.create("ROOM", 2);
        room.join(host);
        when(roomRepository.findById("ROOM")).thenReturn(Optional.of(room));
        roomService.leaveRoom("ROOM", host.id());
        assertEquals(RoomStatus.CLOSED, room.getRoomStatus());
        verify(roomRepository).delete("ROOM");
    }

    @Test
    void shouldThrowWhenLeavingUnknownRoom() {
        when(roomRepository.findById("ROOM")).thenReturn(Optional.empty());
        assertThrows(RoomNotFoundException.class, () -> roomService.leaveRoom("ROOM", host.id()));
    }

    @Test
    void shouldStartGameAndSaveRoom() {
        Room room = Room.create("ROOM", 2);
        room.join(host);
        room.join(player);
        when(roomRepository.findById("ROOM")).thenReturn(Optional.of(room));
        roomService.startGame("ROOM", host.id());
        verify(roomRepository).save(room);
        assertEquals(RoomStatus.IN_GAME, room.getRoomStatus());
    }

    @Test
    void shouldThrowWhenStartingUnknownRoom() {
        when(roomRepository.findById("ROOM")).thenReturn(Optional.empty());
        assertThrows(RoomNotFoundException.class, () -> roomService.startGame("ROOM", host.id()));
    }

    @Test
    void shouldCallNumberAndSaveRoom() {
        Room room = Room.create("ROOM", 2);
        room.join(host);
        room.join(player);
        room.startGame(host.id());
        when(roomRepository.findById("ROOM")).thenReturn(Optional.of(room));
        roomService.callNumber("ROOM", host.id(), 7);
        verify(roomRepository).save(room);
    }

    @Test
    void shouldThrowWhenCallingNumberForUnknownRoom() {
        when(roomRepository.findById("ROOM")).thenReturn(Optional.empty());
        assertThrows(RoomNotFoundException.class, () -> roomService.callNumber("ROOM", host.id(), 5));
    }

    @Test
    void shouldNotSaveWhenClaimIsRejected() {
        Room room = spy(Room.create("ROOM", 2));
        when(roomRepository.findById("ROOM")).thenReturn(Optional.of(room));
        doReturn(false).when(room).claimBingo(anyString());
        roomService.claimBingo("ROOM", host.id());
        verify(roomRepository, atLeastOnce()).save(any());
    }

    @Test
    void shouldSaveWhenClaimIsAccepted() {
        Room room = spy(Room.create("ROOM", 2));
        when(roomRepository.findById("ROOM")).thenReturn(Optional.of(room));
        doReturn(true).when(room).claimBingo(anyString());
        roomService.claimBingo("ROOM", host.id());
        verify(roomRepository).save(room);
    }

    @Test
    void shouldThrowWhenClaimingBingoForUnknownRoom() {
        when(roomRepository.findById("ROOM")).thenReturn(Optional.empty());
        assertThrows(RoomNotFoundException.class, () -> roomService.claimBingo("ROOM", host.id()));
    }

    @Test
    void shouldPublishPlayerJoinedEvent() {
        Room room = Room.create("ROOM", 2);
        when(roomRepository.findById("ROOM")).thenReturn(Optional.of(room));

        roomService.joinRoom("ROOM", host);

        ArgumentCaptor<PlayerJoinedEvent> captor = ArgumentCaptor.forClass(PlayerJoinedEvent.class);

        verify(bingoEventPublisher).publish(captor.capture());

        PlayerJoinedEvent event = captor.getValue();
        assertEquals("ROOM", event.getRoomId());
        assertEquals(host, event.getPlayer());
    }

    @Test
    void shouldLeaveRoomWhenPlayersAreStillPresent() {
        Room room = Room.create("ROOM", 2);
        room.join(host);
        room.join(player);

        when(roomRepository.findById("ROOM")).thenReturn(Optional.of(room));

        roomService.leaveRoom("ROOM", player.id());

        assertEquals(1, room.getPlayers().size());
        assertTrue(room.getPlayers().contains(host));

        verify(roomRepository).save(room);
        verify(roomRepository, never()).delete(anyString());

        ArgumentCaptor<PlayerLeftRoomEvent> captor = ArgumentCaptor.forClass(PlayerLeftRoomEvent.class);

        verify(bingoEventPublisher).publish(captor.capture());

        PlayerLeftRoomEvent event = captor.getValue();
        assertEquals("ROOM", event.getRoomId());
        assertEquals(player, event.getPlayer());
    }

    @Test
    void shouldPublishPlayerLeftAndRoomClosedEventsWhenLastPlayerLeaves() {
        Room room = Room.create("ROOM", 2);
        room.join(host);

        when(roomRepository.findById("ROOM")).thenReturn(Optional.of(room));

        roomService.leaveRoom("ROOM", host.id());

        InOrder inOrder = inOrder(bingoEventPublisher);

        ArgumentCaptor<PlayerLeftRoomEvent> leftCaptor = ArgumentCaptor.forClass(PlayerLeftRoomEvent.class);
        ArgumentCaptor<RoomClosedEvent> closedCaptor = ArgumentCaptor.forClass(RoomClosedEvent.class);

        inOrder.verify(bingoEventPublisher).publish(leftCaptor.capture());
        inOrder.verify(bingoEventPublisher).publish(closedCaptor.capture());

        assertEquals("ROOM", leftCaptor.getValue().getRoomId());
        assertEquals(host, leftCaptor.getValue().getPlayer());

        assertEquals("ROOM", closedCaptor.getValue().getRoomId());
    }

    @Test
    void shouldPublishGameStartedEvent() {
        Room room = Room.create("ROOM", 2);
        room.join(host);
        room.join(player);

        when(roomRepository.findById("ROOM")).thenReturn(Optional.of(room));

        roomService.startGame("ROOM", host.id());

        ArgumentCaptor<GameStartedEvent> captor = ArgumentCaptor.forClass(GameStartedEvent.class);

        verify(bingoEventPublisher).publish(captor.capture());

        GameStartedEvent event = captor.getValue();

        assertEquals("ROOM", event.getRoomId());
        assertSame(room, event.getRoom());
    }

    @Test
    void shouldPublishNumberCalledAndTurnChangedEvents() {
        Room room = Room.create("ROOM", 2);
        room.join(host);
        room.join(player);
        room.startGame(host.id());

        when(roomRepository.findById("ROOM")).thenReturn(Optional.of(room));

        roomService.callNumber("ROOM", host.id(), 7);

        InOrder inOrder = inOrder(bingoEventPublisher);

        ArgumentCaptor<NumberCalledEvent> numberCaptor = ArgumentCaptor.forClass(NumberCalledEvent.class);
        ArgumentCaptor<TurnChangedEvent> turnCaptor = ArgumentCaptor.forClass(TurnChangedEvent.class);

        inOrder.verify(bingoEventPublisher).publish(numberCaptor.capture());
        inOrder.verify(bingoEventPublisher).publish(turnCaptor.capture());

        assertEquals("ROOM", numberCaptor.getValue().getRoomId());
        assertEquals(7, numberCaptor.getValue().getNumber());
        assertEquals(host.id(), numberCaptor.getValue().getNumberCalledBy());

        assertEquals("ROOM", turnCaptor.getValue().getRoomId());
        assertEquals(player.id(), turnCaptor.getValue().getCurrentPlayerId());
    }

    @Test
    void shouldPublishRejectedBingoClaimEvent() {
        Room room = spy(Room.create("ROOM", 2));

        when(roomRepository.findById("ROOM")).thenReturn(Optional.of(room));
        doReturn(false).when(room).claimBingo(anyString());

        roomService.claimBingo("ROOM", host.id());

        ArgumentCaptor<BingoClaimEvent> captor = ArgumentCaptor.forClass(BingoClaimEvent.class);

        verify(bingoEventPublisher).publish(captor.capture());
        verify(bingoEventPublisher, never()).publish(any(GameWonEvent.class));
        verify(bingoEventPublisher, never()).publish(any(GameEndedEvent.class));

        BingoClaimEvent event = captor.getValue();

        assertEquals("ROOM", event.getRoomId());
        assertEquals(host.id(), event.getPlayerId());
        assertFalse(event.isAccepted());
    }

    @Test
    void shouldPublishAcceptedBingoClaimGameWonAndGameEndedEvents() {
        Room room = spy(Room.create("ROOM", 2));

        when(roomRepository.findById("ROOM")).thenReturn(Optional.of(room));
        doReturn(true).when(room).claimBingo(anyString());

        roomService.claimBingo("ROOM", host.id());

        InOrder inOrder = inOrder(bingoEventPublisher);

        ArgumentCaptor<BingoClaimEvent> claimCaptor =
                ArgumentCaptor.forClass(BingoClaimEvent.class);

        ArgumentCaptor<GameWonEvent> wonCaptor =
                ArgumentCaptor.forClass(GameWonEvent.class);

        ArgumentCaptor<GameEndedEvent> endedCaptor =
                ArgumentCaptor.forClass(GameEndedEvent.class);

        inOrder.verify(bingoEventPublisher).publish(claimCaptor.capture());
        inOrder.verify(bingoEventPublisher).publish(wonCaptor.capture());
        inOrder.verify(bingoEventPublisher).publish(endedCaptor.capture());

        assertTrue(claimCaptor.getValue().isAccepted());

        assertEquals("ROOM", wonCaptor.getValue().getRoomId());
        assertEquals(host.id(), wonCaptor.getValue().getWinnerPlayerId());

        assertEquals("ROOM", endedCaptor.getValue().getRoomId());
    }


}