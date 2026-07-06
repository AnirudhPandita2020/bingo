package com.anirudh.bingo.core.game;

import com.anirudh.bingo.core.player.GamePlayer;
import com.anirudh.bingo.exception.game.*;
import com.anirudh.bingo.utils.CamelCaseDisplayNameGenerator;
import com.anirudh.bingo.utils.GameTestUtils;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class BingoGameTest {

    @Test
    public void testWhenANewBingoGameIsCreatedWithValidPlayersThenGameIsCreated() {
        List<GamePlayer> players = GameTestUtils.validPlayers(4, true);
        BingoGame bingoGame = BingoGame.create(UUID.randomUUID().toString(), players);
        assertFalse(bingoGame.players().isEmpty());
    }

    @Test
    public void testWhenANewBingoGameIsCreatedWithNoIDThenGameIsNotCreated() {
        List<GamePlayer> players = GameTestUtils.validPlayers(4, true);
        var exception = assertThrows(IllegalArgumentException.class, () -> BingoGame.create(null, players));
        assertEquals("id must not be null", exception.getMessage());
    }

    @Test
    public void testWhenANewBingoGameIsCreatedWithNoPlayersThenGameIsNotCreated() {
        var exception = assertThrows(IllegalArgumentException.class, () -> BingoGame.create(UUID.randomUUID().toString(), null));
        assertEquals("players must not be empty", exception.getMessage());
    }

    @Test
    public void testWhenANewValidBingoGameIsCreatedAndStartIsInitiatedThenStatusChangesToInProgress() {
        List<GamePlayer> players = GameTestUtils.validPlayers(4, true);
        BingoGame bingoGame = BingoGame.create(UUID.randomUUID().toString(), players);
        assertEquals(GameStatus.WAITING, bingoGame.getStatus());
        bingoGame.start();
        assertEquals(GameStatus.IN_PROGRESS, bingoGame.getStatus());
    }

    @Test
    public void testWhenANewValidBingoGameIsCreatedAndGameIsInProgressAndAgainStartIsCalledThenExceptionIsThrown() {
        List<GamePlayer> players = GameTestUtils.validPlayers(4, true);
        BingoGame bingoGame = BingoGame.create(UUID.randomUUID().toString(), players);
        bingoGame.start();
        var exception = assertThrows(GameAlreadyStartedException.class, bingoGame::start);
        assertEquals("Game has already started", exception.getMessage());
    }

    @Test
    public void testWhenANewBingoGameIsCreatedAndGameIsInProgressAndAPlayerCallsANumberThenNumberGetsMarkedOnAllBoard() {
        List<GamePlayer> players = GameTestUtils.validPlayers(4, true);
        BingoGame bingoGame = BingoGame.create(UUID.randomUUID().toString(), players);
        bingoGame.start();
        bingoGame.callNumber(players.getFirst().getPlayer(), 5);
        assertTrue(bingoGame.calledNumbers().contains(5));
        bingoGame.players().stream().map(GamePlayer::getBingoBoard)
                .forEach(bingoBoard -> assertTrue(bingoBoard.isMarked(5)));
    }

    @Test
    public void testWhenANewBingoGameIsCreatedAndGameIsInProgressAndPlayerCallingTheNumberIsNotTheCurrentPlayerThenExceptionIsThrown() {
        List<GamePlayer> players = GameTestUtils.validPlayers(4, true);
        BingoGame bingoGame = BingoGame.create(UUID.randomUUID().toString(), players);
        bingoGame.start();
        assertEquals(GameStatus.IN_PROGRESS, bingoGame.getStatus());
        var exception = assertThrows(NotPlayersTurnException.class, () -> bingoGame.callNumber(players.get(1).getPlayer(), 5));
        assertEquals("Current player to play does not match with the caller", exception.getMessage());
    }

    @Test
    public void testWhenDuringABingoGameOnceAllPlayersHaveTurnedTheCurrentPlayerIsTheFirstPlayer() {
        List<GamePlayer> players = GameTestUtils.validPlayers(4, true);
        BingoGame bingoGame = BingoGame.create(UUID.randomUUID().toString(), players);
        bingoGame.start();
        for (int i = 1; i <= players.size(); i++) {
            bingoGame.callNumber(players.get(i - 1).getPlayer(), i);
        }
        assertEquals(bingoGame.currentPlayer().getPlayer(), players.getFirst().getPlayer());
    }

    @Test
    public void testWhenABingoGameIsCreatedAndTheGameIsNotYetStartedAndPlayerCallsANumberThenExceptionIsThrown() {
        List<GamePlayer> players = GameTestUtils.validPlayers(4, true);
        BingoGame bingoGame = BingoGame.create(UUID.randomUUID().toString(), players);
        var exception = assertThrows(GameNotStartedException.class, () -> bingoGame.callNumber(players.get(1).getPlayer(), 5));
        assertEquals("Game is not in progress", exception.getMessage());
    }

    @Test
    public void testWhenABingoGameIsCreatedAndStartedAndAPlayerCallsAnNumberLessThanMinimumThenExceptionIsThrown() {
        List<GamePlayer> players = GameTestUtils.validPlayers(4, true);
        BingoGame bingoGame = BingoGame.create(UUID.randomUUID().toString(), players);
        bingoGame.start();
        var exception = assertThrows(InvalidNumberException.class, () -> bingoGame.callNumber(players.get(1).getPlayer(), 0));
        assertEquals("Number must be between 1 and 25", exception.getMessage());
    }

    @Test
    public void testWhenABingoGameIsCreatedAndStartedAndAPlayerCallsAnNumberGreaterThanMaximumThenExceptionIsThrown() {
        List<GamePlayer> players = GameTestUtils.validPlayers(4, true);
        BingoGame bingoGame = BingoGame.create(UUID.randomUUID().toString(), players);
        bingoGame.start();
        var exception = assertThrows(InvalidNumberException.class, () -> bingoGame.callNumber(players.get(1).getPlayer(), 26));
        assertEquals("Number must be between 1 and 25", exception.getMessage());
    }

    @Test
    public void testWhenAPlayerCallsAnAlreadyCalledNumberThenExceptionIsThrown() {
        List<GamePlayer> players = GameTestUtils.validPlayers(4, true);
        BingoGame bingoGame = BingoGame.create(UUID.randomUUID().toString(), players);
        bingoGame.start();
        bingoGame.callNumber(players.getFirst().getPlayer(), 1);
        var exception = assertThrows(NumberAlreadyCalledException.class, () -> bingoGame.callNumber(players.get(1).getPlayer(), 1));
        assertEquals("Number has been already called", exception.getMessage());
    }

    @Test
    public void testWhenAPlayerClaimsBingoWhenBoardHasFiveLinesThenBingoIsClaimed() {
        List<GamePlayer> players = GameTestUtils.validPlayers(4, false);
        BingoGame bingoGame = BingoGame.create(UUID.randomUUID().toString(), players);
        bingoGame.start();

        // Complete all five rows.
        for (int number = 1; number <= 25; number++) {
            GamePlayer currentPlayer = bingoGame.currentPlayer();
            bingoGame.callNumber(currentPlayer.getPlayer(), number);
        }
        assertTrue(bingoGame.claimBingo(players.getFirst().getPlayer()));
        assertTrue(players.getFirst().hasWon());
        assertTrue(bingoGame.winner().isPresent());
        assertEquals(players.getFirst(), bingoGame.winner().get());
        assertTrue(bingoGame.isFinished());
    }

    @Test
    public void testWhenAPlayerClaimsBingoWhenBoardHasFourLinesThenBingoIsClaimed() {
        List<GamePlayer> players = GameTestUtils.validPlayers(4, false);
        BingoGame bingoGame = BingoGame.create(UUID.randomUUID().toString(), players);
        bingoGame.start();

        for (int number = 1; number <= 20; number++) {
            GamePlayer currentPlayer = bingoGame.currentPlayer();
            bingoGame.callNumber(currentPlayer.getPlayer(), number);
        }
        assertFalse(bingoGame.claimBingo(players.getFirst().getPlayer()));
        assertFalse(players.getFirst().hasWon());
        assertFalse(bingoGame.winner().isPresent());
        assertFalse(bingoGame.isFinished());
    }

    @Test
    public void testWhenAPlayerClaimsBingoWhenGameIsNotInProgressThenExceptionIsThrown() {
        List<GamePlayer> players = GameTestUtils.validPlayers(4, false);
        BingoGame bingoGame = BingoGame.create(UUID.randomUUID().toString(), players);
        assertFalse(bingoGame.claimBingo(players.getFirst().getPlayer()));
    }
}
