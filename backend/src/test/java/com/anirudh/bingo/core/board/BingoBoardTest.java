package com.anirudh.bingo.core.board;

import com.anirudh.bingo.utils.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static com.anirudh.bingo.utils.GameTestUtils.validNumberSequence;
import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class BingoBoardTest {

    @Test
    public void testWhenNewRandomBoardIsCreatedThenBoardIsCreated() {
        BingoBoard bingoBoard = BingoBoard.create();
        assertNotNull(bingoBoard);
    }

    @Test
    public void testWhenRandomBoardIsCreatedThenContainsUniqueNumbersBetweenOneAndTwentyFive() {
        BingoBoard bingoBoard = BingoBoard.create();

        int[][] grid = bingoBoard.getGrid();

        Set<Integer> numbers = new HashSet<>();
        for (int[] row : grid) {
            for (int number : row) {
                assertTrue(number >= 1 && number <= 25);
                numbers.add(number);
            }
        }

        assertEquals(25, numbers.size());
    }

    @Test
    public void testWhenNullNumbersAreProvidedThenBoardIsNotCreated() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> BingoBoard.create(null));
        assertEquals("numbers must not be empty", exception.getMessage());
    }

    @Test
    public void testWhenNewBoardWithPrefilledNumberIsCreatedThenBoardIsCreated() {
        BingoBoard bingoBoard = BingoBoard.create(validNumberSequence());
        assertNotNull(bingoBoard);
    }

    @Test
    public void testWhenNewBoardWithPrefilledNumberContainsDuplicateThenBoardIsNotCreated() {
        var numbers = validNumberSequence();
        numbers.set(24, 1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> BingoBoard.create(numbers));
        assertEquals("Board numbers must be unique.", exception.getMessage());
    }

    @Test
    public void testWhenNewBoardWithPrefilledNumberLessThanMaxThenBoardIsNotCreated() {
        var numbers = validNumberSequence();
        numbers.remove(24);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> BingoBoard.create(numbers));
        assertEquals("Exactly 25 numbers are required.", exception.getMessage());
    }

    @Test
    public void testWhenNewBoardWithPrefilledNumberHasInvalidNumberGreaterThanMaxThenBoardIsNotCreated() {
        var numbers = validNumberSequence();
        numbers.set(24, 26);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> BingoBoard.create(numbers));
        assertEquals("All numbers must be between 1 and 25", exception.getMessage());
    }

    @Test
    public void testWhenNewBoardWithPrefilledNumberHasInvalidNumberLesserThanMinThenBoardIsNotCreated() {
        var numbers = validNumberSequence();
        numbers.set(24, -1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> BingoBoard.create(numbers));
        assertEquals("All numbers must be between 1 and 25", exception.getMessage());
    }

    @Test
    public void testWhenValidBoardIsCreatedAndANumberIsMarkedThenReturnsTrue() {
        BingoBoard bingoBoard = BingoBoard.create();
        assertTrue(bingoBoard.mark(1));
    }

    @Test
    public void testWhenValidBoardIsCreatedAndNumberIsMarkedTwiceThenReturnsFalse() {
        BingoBoard bingoBoard = BingoBoard.create();
        bingoBoard.mark(1);
        assertFalse(bingoBoard.mark(1));
    }

    @Test
    public void testWhenValidBoardIsCreatedAndInvalidNumberIsMarkedThenReturnsFalse() {
        BingoBoard bingoBoard = BingoBoard.create();
        assertFalse(bingoBoard.mark(0));
        assertFalse(bingoBoard.mark(26));
    }

    @Test
    public void testWhenFiveRowsAreCompletedThenBoardHasWon() {
        BingoBoard bingoBoard = BingoBoard.create(validNumberSequence());
        for (int i = 1; i <= 25; i++) {
            bingoBoard.mark(i);
            if (i < 21) {
                assertFalse(bingoBoard.hasWon());
            }
        }
        assertTrue(bingoBoard.hasWon());
    }

    @Test
    public void testWhenValidBoardIsCreatedAndBingoIsNotAchievedThenBoardStillNotWon() {
        var numbers = validNumberSequence();
        BingoBoard bingoBoard = BingoBoard.create(numbers);
        for (int i = 1; i <= 15; i++) bingoBoard.mark(i);
        assertFalse(bingoBoard::hasWon);
    }

    @Test
    public void testWhenFiveColumnsAreCompletedThenBoardHasWon() {
        BingoBoard bingoBoard = BingoBoard.create(validNumberSequence());
        int[][] columns = {
                {1, 6, 11, 16, 21},
                {2, 7, 12, 17, 22},
                {3, 8, 13, 18, 23},
                {4, 9, 14, 19, 24},
                {5, 10, 15, 20, 25}
        };
        for (int i = 0; i < columns.length; i++) {
            for (int number : columns[i]) {
                bingoBoard.mark(number);
            }
            if (i < 4) {
                assertFalse(bingoBoard.hasWon());
            }
        }
        assertTrue(bingoBoard.hasWon());
    }

    @Test
    public void testWhenFiveLinesIncludingDiagonalsAreCompletedThenBoardHasWon() {
        BingoBoard bingoBoard = BingoBoard.create(validNumberSequence());
        int[][] lines = {
                {1, 2, 3, 4, 5},
                {6, 7, 8, 9, 10},
                {11, 12, 13, 14, 15},
                {1, 7, 13, 19, 25},
                {5, 9, 13, 17, 21}
        };
        for (int i = 0; i < lines.length; i++) {
            for (int number : lines[i]) {
                bingoBoard.mark(number);
            }
            if (i < 4) {
                assertFalse(bingoBoard.hasWon());
            }
        }
        assertTrue(bingoBoard.hasWon());
    }

    @Test
    public void testWhenGetGridIsCalledThenGridIsReturned() {
        BingoBoard bingoBoard = BingoBoard.create();
        int[][] grid = bingoBoard.getGrid();
        assertNotNull(grid);
    }

    @Test
    public void testWhenGetGridIsCalledAndValueIsChangedThenGridShouldBeAffected() {
        BingoBoard bingoBoard = BingoBoard.create();
        int[][] firstGrid = bingoBoard.getGrid();
        firstGrid[0][0] = -1;
        int[][] secondGrid = bingoBoard.getGrid();
        assertTrue(firstGrid[0][0] != secondGrid[0][0]);
    }
}
