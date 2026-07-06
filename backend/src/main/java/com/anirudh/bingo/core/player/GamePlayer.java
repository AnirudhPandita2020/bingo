package com.anirudh.bingo.core.player;

import com.anirudh.bingo.core.board.BingoBoard;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.util.Assert;


/**
 * Represents a player's participation in a Bingo game.
 * <p>
 * Associates a player with their assigned board and tracks
 * game-specific state.
 */
@EqualsAndHashCode(callSuper = false)
public final class GamePlayer {

    /**
     * The participating player.
     */
    @Getter
    private final Player player;

    /**
     * The board assigned to the player for this game.
     */
    @Getter
    private final BingoBoard bingoBoard;

    /**
     * Indicates whether the player has claimed Bingo.
     */
    private boolean bingoClaimed = false;

    /**
     * Creates a game participant.
     *
     * @param player     participating player
     * @param bingoBoard board assigned to the player
     */
    private GamePlayer(Player player, BingoBoard bingoBoard) {
        this.player = player;
        this.bingoBoard = bingoBoard;
    }

    /**
     * Creates a game participant after validating the supplied arguments.
     *
     * @param player     participating player
     * @param bingoBoard board assigned to the player
     * @return a new game participant
     */
    public static GamePlayer create(Player player, BingoBoard bingoBoard) {
        Assert.notNull(player, "player must not be null");
        Assert.notNull(bingoBoard, "bingoBoard must not be null");
        return new GamePlayer(player, bingoBoard);
    }

    /**
     * Marks the specified number on the player's board.
     *
     * @param number number to mark
     * @return {@code true} if the board state changed; {@code false} otherwise
     */
    public boolean mark(int number) {
        return bingoBoard.mark(number);
    }

    /**
     * Determines whether the player's board satisfies the winning condition.
     *
     * @return {@code true} if the player has won; {@code false} otherwise
     */
    public boolean hasWon() {
        return bingoBoard.hasWon();
    }

    /**
     * Records the player's Bingo claim.
     *
     * @return {@code true} if this is the first claim; {@code false} if the
     * player has already claimed Bingo
     */
    public boolean claimBingo() {
        if (bingoClaimed || !hasWon()) {
            return false;
        }
        bingoClaimed = true;
        return true;
    }
}