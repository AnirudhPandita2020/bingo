package com.anirudh.bingo.core.player;

import com.anirudh.bingo.core.board.BingoBoard;
import com.anirudh.bingo.utils.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.anirudh.bingo.utils.GameTestUtils.validNumberSequence;
import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class GamePlayerTest {

    @Test
    public void testWhenAPlayerIsProvidedThenAGamePlayerIsCreated() {
        BingoBoard board = BingoBoard.create();
        Player player = new Player(UUID.randomUUID().toString(), "Anirudh");
        GamePlayer gamePlayer = GamePlayer.create(player, board);
        assertEquals(gamePlayer.getPlayer().id(), player.id());
    }

    @Test
    public void testWhenAPlayerIsNotProvidedThenAGamePlayerIsNotCreated() {
        BingoBoard board = BingoBoard.create();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> GamePlayer.create(null, board));
        assertEquals("player must not be null", exception.getMessage());
    }

    @Test
    public void testWhenABingoBoardIsNotProvidedThenAGamePlayerIsNotCreated() {
        Player player = new Player(UUID.randomUUID().toString(), "Anirudh");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> GamePlayer.create(player, null));
        assertEquals("bingoBoard must not be null", exception.getMessage());
    }

    @Test
    public void testWhenAGamePlayerMarksANumberThenTheNumberIsMarkedOnTheBoard() {
        BingoBoard board = BingoBoard.create();
        Player player = new Player(UUID.randomUUID().toString(), "Anirudh");
        GamePlayer gamePlayer = GamePlayer.create(player, board);
        assertTrue(gamePlayer.mark(1));
    }

    @Test
    public void testWhenAGamePlayerMarksAtLeastThreeLinesThenThePlayerHasNotWon() {
        BingoBoard board = BingoBoard.create(validNumberSequence());
        Player player = new Player(UUID.randomUUID().toString(), "Anirudh");
        GamePlayer gamePlayer = GamePlayer.create(player, board);
        for (int i = 1; i <= 15; i++) gamePlayer.mark(i);
        assertFalse(gamePlayer::hasWon);
    }

    @Test
    public void testWhenAGamePlayerMarksFiveLinesThenThePlayerHasWon() {
        BingoBoard board = BingoBoard.create(validNumberSequence());
        Player player = new Player(UUID.randomUUID().toString(), "Anirudh");
        GamePlayer gamePlayer = GamePlayer.create(player, board);
        for (int i = 1; i <= 25; i++) gamePlayer.mark(i);
        assertTrue(gamePlayer::hasWon);
    }

    @Test
    public void testWhenAGamePlayerMarksLessThanFiveLinesThenThePlayerCannotClaimBingo() {
        BingoBoard board = BingoBoard.create(validNumberSequence());
        Player player = new Player(UUID.randomUUID().toString(), "Anirudh");
        GamePlayer gamePlayer = GamePlayer.create(player, board);
        for (int i = 1; i <= 15; i++) gamePlayer.mark(i);
        assertFalse(gamePlayer::claimBingo);
    }

    @Test
    public void testWhenAGamePlayerMarksFiveLinesThenThePlayerCanClaimBingo() {
        BingoBoard board = BingoBoard.create(validNumberSequence());
        Player player = new Player(UUID.randomUUID().toString(), "Anirudh");
        GamePlayer gamePlayer = GamePlayer.create(player, board);
        for (int i = 1; i <= 25; i++) gamePlayer.mark(i);
        assertTrue(gamePlayer::claimBingo);
    }

    @Test
    public void testWhenAGamePlayerHasAlreadyClaimedBingoThenThePlayerCannotClaimBingo() {
        BingoBoard board = BingoBoard.create(validNumberSequence());
        Player player = new Player(UUID.randomUUID().toString(), "Anirudh");
        GamePlayer gamePlayer = GamePlayer.create(player, board);
        for (int i = 1; i <= 25; i++) gamePlayer.mark(i);
        gamePlayer.claimBingo();
        assertFalse(gamePlayer::claimBingo);
    }
}
