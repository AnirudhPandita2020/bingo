package com.anirudh.bingo.core.room;

import com.anirudh.bingo.core.player.GamePlayer;
import com.anirudh.bingo.core.player.Player;
import com.anirudh.bingo.exception.room.*;
import com.anirudh.bingo.utils.CamelCaseDisplayNameGenerator;
import com.anirudh.bingo.utils.GameTestUtils;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class RoomTest {

    @Test
    public void testWhenValidRoomDetailsAreProvidedThenRoomIsCreated() {
        Room room = Room.create("room-1", 4);
        assertNotNull(room);
        assertEquals("room-1", room.getId());
        assertEquals(4, room.getMaxPlayers());
        assertEquals(RoomStatus.OPEN, room.getRoomStatus());
        assertFalse(room.hasActiveGame());
        assertTrue(room.getPlayers().isEmpty());
        assertTrue(room.host().isEmpty());
    }

    @Test
    public void testWhenRoomIsCreatedWithNullIdThenExceptionIsThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Room.create(null, 4));
        assertEquals("id must not be null", exception.getMessage());
    }

    @Test
    public void testWhenRoomIsCreatedWithLessThanTwoPlayersThenExceptionIsThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Room.create("room-1", 1));
        assertEquals("A minimum of two players is required to create a room", exception.getMessage());
    }

    @Test
    public void testWhenAPlayerJoinsThenPlayerIsAddedToTheRoom() {
        Room room = Room.create("room-1", 4);

        Player player = GameTestUtils.validPlayer();

        room.join(player);

        assertEquals(1, room.getPlayers().size());
        assertTrue(room.getPlayers().contains(player));
    }

    @Test
    public void testWhenFirstPlayerJoinsThenPlayerBecomesHost() {
        Room room = Room.create("room-1", 4);

        Player player = GameTestUtils.validPlayer();

        room.join(player);

        assertTrue(room.host().isPresent());
        assertEquals(player, room.host().orElseThrow());
    }

    @Test
    public void testWhenMultiplePlayersJoinThenJoinOrderIsPreserved() {
        Room room = Room.create("room-1", 4);

        Player first = GameTestUtils.validPlayer();
        Player second = GameTestUtils.validPlayer();
        Player third = GameTestUtils.validPlayer();

        room.join(first);
        room.join(second);
        room.join(third);
        assertEquals(List.of(first, second, third), room.getPlayers());
    }

    @Test
    public void testWhenSamePlayerJoinsTwiceThenExceptionIsThrown() {
        Room room = Room.create("room-1", 4);

        Player player = GameTestUtils.validPlayer();

        room.join(player);

        var exception = assertThrows(PlayerAlreadyInRoomException.class, () -> room.join(player));
        assertEquals("Player has already joined the room", exception.getMessage());
    }

    @Test
    public void testWhenRoomIsFullThenPlayerCannotJoin() {
        Room room = Room.create("room-1", 2);

        room.join(GameTestUtils.validPlayer());
        room.join(GameTestUtils.validPlayer());

        var exception = assertThrows(RoomFullException.class, () -> room.join(GameTestUtils.validPlayer()));
        assertEquals("Room is full", exception.getMessage());
    }

    @Test
    public void testWhenRoomIsClosedThenPlayerCannotJoin() {
        Room room = Room.create("room-1", 2);

        Player player = GameTestUtils.validPlayer();

        room.join(player);
        room.leave(player.id());

        RoomClosedException exception = assertThrows(RoomClosedException.class, () -> room.join(GameTestUtils.validPlayer()));
        assertEquals("Room is not accepting players", exception.getMessage());
    }

    @Test
    public void testWhenGameIsAlreadyInProgressThenPlayerCannotJoin() {
        Room room = Room.create("room-1", 2);

        Player host = GameTestUtils.validPlayer();
        Player second = GameTestUtils.validPlayer();

        room.join(host);
        room.join(second);

        room.startGame(host.id());

        var exception = assertThrows(RoomClosedException.class, () -> room.join(GameTestUtils.validPlayer()));
        assertEquals("Room is not accepting players", exception.getMessage());
    }

    @Test
    public void testWhenAPlayerLeavesThenPlayerIsRemovedFromRoom() {
        Room room = Room.create("room-1", 3);

        Player first = GameTestUtils.validPlayer();
        Player second = GameTestUtils.validPlayer();

        room.join(first);
        room.join(second);

        room.leave(second.id());

        assertEquals(1, room.getPlayers().size());
        assertFalse(room.getPlayers().contains(second));
        assertTrue(room.getPlayers().contains(first));
    }

    @Test
    public void testWhenUnknownPlayerLeavesThenNothingHappens() {
        Room room = Room.create("room-1", 3);

        Player player = GameTestUtils.validPlayer();
        Player unknown = GameTestUtils.validPlayer();

        room.join(player);

        room.leave(unknown.id());

        assertEquals(1, room.getPlayers().size());
        assertEquals(player, room.host().orElseThrow());
    }

    @Test
    public void testWhenHostLeavesThenHostIsTransferredToNextPlayer() {
        Room room = Room.create("room-1", 3);

        Player host = GameTestUtils.validPlayer();
        Player second = GameTestUtils.validPlayer();
        Player third = GameTestUtils.validPlayer();

        room.join(host);
        room.join(second);
        room.join(third);

        room.leave(host.id());

        assertEquals(second, room.host().orElseThrow());
        assertEquals(List.of(second, third), room.getPlayers());
    }

    @Test
    public void testWhenNonHostLeavesThenHostRemainsUnchanged() {
        Room room = Room.create("room-1", 3);

        Player host = GameTestUtils.validPlayer();
        Player second = GameTestUtils.validPlayer();

        room.join(host);
        room.join(second);

        room.leave(second.id());

        assertEquals(host, room.host().orElseThrow());
        assertEquals(List.of(host), room.getPlayers());
    }

    @Test
    public void testWhenLastPlayerLeavesThenRoomIsClosed() {
        Room room = Room.create("room-1", 2);

        Player host = GameTestUtils.validPlayer();

        room.join(host);

        room.leave(host.id());

        assertEquals(RoomStatus.CLOSED, room.getRoomStatus());
        assertTrue(room.getPlayers().isEmpty());
        assertTrue(room.host().isEmpty());
        assertFalse(room.hasActiveGame());
    }

    @Test
    public void testWhenNullPlayerLeavesThenExceptionIsThrown() {
        Room room = Room.create("room-1", 2);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> room.leave(null));
        assertEquals("playerId must not be null", exception.getMessage());
    }

    @Test
    public void testWhenHostStartsGameThenGameStartsSuccessfully() {
        Room room = Room.create("room-1", 2);

        Player host = GameTestUtils.validPlayer();
        Player second = GameTestUtils.validPlayer();

        room.join(host);
        room.join(second);

        room.startGame(host.id());

        assertEquals(RoomStatus.IN_GAME, room.getRoomStatus());
        assertTrue(room.hasActiveGame());
        assertNotNull(room.currentPlayer());
    }

    @Test
    public void testWhenNullHostStartsGameThenExceptionIsThrown() {
        Room room = Room.create("room-1", 2);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> room.startGame(null));
        assertEquals("hostPlayer must not be null", exception.getMessage());
    }

    @Test
    public void testWhenNonHostStartsGameThenExceptionIsThrown() {
        Room room = Room.create("room-1", 2);

        Player host = GameTestUtils.validPlayer();
        Player second = GameTestUtils.validPlayer();

        room.join(host);
        room.join(second);

        var exception = assertThrows(NotRoomHostException.class, () -> room.startGame(second.id()));
        assertEquals("Only the host can start the game", exception.getMessage());
    }

    @Test
    public void testWhenOnlyOnePlayerExistsThenGameCannotStart() {
        Room room = Room.create("room-1", 2);

        Player host = GameTestUtils.validPlayer();

        room.join(host);

        var exception = assertThrows(MinimumPlayersNotMetException.class, () -> room.startGame(host.id()));
        assertEquals("At least two players are required to start the game", exception.getMessage());
    }

    @Test
    public void testWhenGameHasAlreadyStartedThenGameCannotBeStartedAgain() {
        Room room = Room.create("room-1", 2);

        Player host = GameTestUtils.validPlayer();
        Player second = GameTestUtils.validPlayer();

        room.join(host);
        room.join(second);

        room.startGame(host.id());

        var exception = assertThrows(RoomClosedException.class, () -> room.startGame(host.id()));
        assertEquals("Room is not accepting players", exception.getMessage());
    }

    @Test
    public void testWhenGameStartsThenCurrentPlayerBelongsToTheRoom() {
        Room room = Room.create("room-1", 2);

        Player host = GameTestUtils.validPlayer();
        Player second = GameTestUtils.validPlayer();

        room.join(host);
        room.join(second);

        room.startGame(host.id());

        GamePlayer currentPlayer = room.currentPlayer();
        assertTrue(room.getPlayers().contains(currentPlayer.getPlayer()));
    }

    @Test
    public void testWhenCurrentPlayerCallsNumberThenTurnAdvances() {
        Room room = Room.create("room-1", 2);
        Player host = GameTestUtils.validPlayer();
        Player second = GameTestUtils.validPlayer();

        room.join(host);
        room.join(second);

        room.startGame(host.id());

        GamePlayer currentPlayer = room.currentPlayer();

        room.callNumber(currentPlayer.getPlayer().id(), 5);

        GamePlayer nextPlayer = room.currentPlayer();

        assertNotEquals(currentPlayer.getPlayer(), nextPlayer.getPlayer());
    }

    @Test
    public void testWhenCallNumberIsInvokedWithoutAnActiveGameThenExceptionIsThrown() {
        Room room = Room.create("room-1", 2);

        var exception = assertThrows(RoomNotInProgressException.class, () -> room.callNumber("player", 5));
        assertEquals("No game is currently in progress", exception.getMessage());
    }

    @Test
    public void testWhenUnknownPlayerCallsNumberThenExceptionIsThrown() {
        Room room = Room.create("room-1", 2);
        Player host = GameTestUtils.validPlayer();
        Player second = GameTestUtils.validPlayer();

        room.join(host);
        room.join(second);

        room.startGame(host.id());

        var exception = assertThrows(PlayerNotInRoomException.class, () -> room.callNumber("unknown", 5));
        assertEquals("Player is not part of the room", exception.getMessage());
    }

    @Test
    public void testWhenPlayerClaimsBingoWithoutWinningThenClaimFails() {
        Room room = Room.create("room-1", 2);
        Player host = GameTestUtils.validPlayer();
        Player second = GameTestUtils.validPlayer();

        room.join(host);
        room.join(second);

        room.startGame(host.id());

        assertFalse(room.claimBingo(host.id()));
    }

    @Test
    public void testWhenClaimBingoIsCalledWithoutAnActiveGameThenExceptionIsThrown() {
        Room room = Room.create("room-1", 2);

        var exception = assertThrows(RoomNotInProgressException.class, () -> room.claimBingo("player"));
        assertEquals("No game is currently in progress", exception.getMessage());
    }

    @Test
    public void testWhenUnknownPlayerClaimsBingoThenExceptionIsThrown() {
        Room room = Room.create("room-1", 2);
        Player host = GameTestUtils.validPlayer();
        Player second = GameTestUtils.validPlayer();

        room.join(host);
        room.join(second);

        room.startGame(host.id());

        var exception = assertThrows(PlayerNotInRoomException.class, () -> room.claimBingo("unknown"));
        assertEquals("Player is not part of the room", exception.getMessage());
    }

    @Test
    public void testWhenGameIsRunningThenCurrentPlayerIsReturned() {
        Room room = Room.create("room-1", 2);
        Player host = GameTestUtils.validPlayer();
        Player second = GameTestUtils.validPlayer();

        room.join(host);
        room.join(second);

        room.startGame(host.id());

        GamePlayer currentPlayer = room.currentPlayer();

        assertNotNull(currentPlayer);
        assertTrue(room.getPlayers().contains(currentPlayer.getPlayer()));
    }

    @Test
    public void testWhenCurrentPlayerIsRequestedWithoutAnActiveGameThenExceptionIsThrown() {
        Room room = Room.create("room-1", 2);

        var exception = assertThrows(RoomNotInProgressException.class, room::currentPlayer);
        assertEquals("No game is currently in progress", exception.getMessage());
    }

    @Test
    public void testWhenWinnerIsRequestedBeforeGameEndsThenWinnerIsEmpty() {
        Room room = Room.create("room-1", 2);
        Player host = GameTestUtils.validPlayer();
        Player second = GameTestUtils.validPlayer();

        room.join(host);
        room.join(second);

        room.startGame(host.id());
        assertTrue(room.winner().isEmpty());
    }

    @Test
    public void testWhenWinnerIsRequestedWithoutAnActiveGameThenExceptionIsThrown() {
        Room room = Room.create("room-1", 2);

        RoomNotInProgressException exception = assertThrows(RoomNotInProgressException.class, room::winner);
        assertEquals("No game is currently in progress", exception.getMessage());
    }

    @Test
    public void testWhenRoomIsCreatedThenThereIsNoActiveGame() {
        Room room = Room.create("room-1", 2);
        assertFalse(room.hasActiveGame());
    }

    @Test
    public void testWhenGameStartsThenRoomHasAnActiveGame() {
        Room room = Room.create("room-1", 2);
        Player host = GameTestUtils.validPlayer();
        Player second = GameTestUtils.validPlayer();

        room.join(host);
        room.join(second);

        room.startGame(host.id());
        assertTrue(room.hasActiveGame());
    }

}