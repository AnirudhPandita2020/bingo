package com.anirudh.bingo.core.board;

import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Represents a standard 5×5 Bingo board.
 * <p>
 * Maintains the board layout, supports constant-time number lookups,
 * tracks marked cells using a bit mask, and evaluates winning patterns.
 * </p>
 */
public final class BingoBoard {

    /**
     * Number of rows and columns in the board.
     */
    private static final int SIZE = 5;

    /**
     * Number of lines need to complete the board.
     */
    private static final int WINNING_SCORE = 5;

    /**
     * Total number of cells on the board.
     */
    private static final int CELLS = SIZE * SIZE;
    /**
     * Bit masks representing every winning pattern
     * (rows, columns, and diagonals).
     */
    private static final int[] WINNING_MASKS = new int[(2 * SIZE) + 2];

    static {
        generateWinningMasks();
    }

    /**
     * Maps each board number to its corresponding bit position.
     */
    private final Map<Integer, Integer> numberToPositionLookup = new HashMap<>(CELLS);
    /**
     * Numbers arranged in row-major order.
     */

    private final int[][] grid = new int[SIZE][SIZE];
    /**
     * Bit mask indicating which board positions have been marked.
     */
    private int markedCells = 0;

    /**
     * Creates a board from the supplied numbers.
     *
     * @param numbers numbers used to populate the board
     */
    private BingoBoard(List<Integer> numbers) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int index = (i * SIZE) + j;
                int number = numbers.get(index);
                grid[i][j] = number;
                numberToPositionLookup.put(number, index);
            }
        }
    }

    /**
     * Precomputes the bit masks for every winning pattern on the board.
     * The generated masks include all rows, columns, and both diagonals.
     */
    private static void generateWinningMasks() {
        for (int i = 0; i < SIZE; i++) {
            int rowScore = 0;
            for (int j = 0; j < SIZE; j++) {
                rowScore |= 1 << (i * SIZE + j);
            }
            WINNING_MASKS[i] = rowScore;
        }
        for (int i = 0; i < SIZE; i++) {
            int columnScore = 0;
            for (int j = 0; j < SIZE; j++) {
                columnScore |= 1 << (j * SIZE + i);
            }
            WINNING_MASKS[SIZE + i] = columnScore;
        }
        int diagonal1Score = 0;
        int diagonal2Score = 0;
        for (int k = 0; k < SIZE; k++) {
            diagonal1Score |= 1 << (k * SIZE + k);
            diagonal2Score |= 1 << (k * SIZE + (SIZE - 1 - k));
        }
        WINNING_MASKS[2 * SIZE] = diagonal1Score;
        WINNING_MASKS[2 * SIZE + 1] = diagonal2Score;
    }

    /**
     * Creates a board with a randomized layout.
     */
    public static BingoBoard create() {
        var numbers = IntStream.range(1, CELLS + 1).boxed().collect(Collectors.toList());
        Collections.shuffle(numbers);
        return new BingoBoard(numbers);
    }

    /**
     * Creates a Bingo board from the supplied numbers.
     * The supplied numbers must represent a valid board layout.
     *
     * @param numbers numbers used to populate the board
     * @return a Bingo board containing the supplied numbers
     * @throws IllegalArgumentException if the supplied numbers do not form a valid board
     */
    public static BingoBoard create(List<Integer> numbers) {
        Assert.notEmpty(numbers, "numbers must not be empty");
        if (numbers.size() != CELLS) {
            throw new IllegalArgumentException("Exactly " + CELLS + " numbers are required.");
        }
        if (new HashSet<>(numbers).size() != CELLS) {
            throw new IllegalArgumentException("Board numbers must be unique.");
        }
        boolean allNumbersWithinLimit = numbers.stream().allMatch(number -> number > 0 && number <= 25);
        if (!allNumbersWithinLimit) {
            throw new IllegalArgumentException("All numbers must be between 1 and 25");
        }
        return new BingoBoard(numbers);
    }

    private int getCellBit(int number) {
        int index = numberToPositionLookup.get(number);
        return 1 << index;
    }

    public boolean isMarked(int number) {
        int bit = getCellBit(number);
        return (markedCells & bit) != 0;
    }

    /**
     * Marks the specified number if it exists on the board and has not
     * already been marked.
     *
     * @param number the number to mark
     * @return {@code true} if the board state changed; {@code false} otherwise
     */
    public boolean mark(int number) {
        if (number <= 0 || number > CELLS) {
            return false;
        }
        if (isMarked(number)) {
            return false;
        }
        int bit = getCellBit(number);
        markedCells |= bit;
        return true;
    }

    /**
     * Determines whether the board satisfies the winning condition.
     *
     * @return {@code true} if the board has accumulated the required number
     * of completed lines; {@code false} otherwise
     */
    public boolean hasWon() {
        int completedLines = 0;
        for (int winningMask : WINNING_MASKS) {
            if ((markedCells & winningMask) == winningMask) {
                completedLines++;
            }
        }
        return completedLines >= WINNING_SCORE;
    }

    /**
     * Returns a defensive copy of the board layout.
     *
     * @return a copy of the board grid
     */
    public int[][] getGrid() {
        int[][] copyGrid = new int[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            copyGrid[row] = Arrays.copyOf(grid[row], SIZE);
        }
        return copyGrid;
    }
}
