package com.anirudh.bingo.core.game;

import com.anirudh.bingo.core.player.GamePlayer;
import com.anirudh.bingo.core.player.Player;
import com.anirudh.bingo.exception.game.*;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Represents an active Bingo game.
 * <p>
 * A game coordinates player turns, tracks the numbers that have been called,
 * propagates called numbers to every player's board, validates Bingo claims,
 * and determines the winner.
 * </p>
 * <p>
 * Once created, the set of participating players is immutable. Player
 * registration and game creation are expected to be managed externally
 * (for example, by a Room).
 * </p>
 */
public final class BingoGame {
    @Getter
    private final String id;
    @Getter
    private final List<GamePlayer> players;
    private final Set<Integer> calledNumbers = new HashSet<>();
    private int currentTurn = 0;
    @Getter
    private GameStatus status = GameStatus.WAITING;
    private GamePlayer winner;

    private BingoGame(String id, List<GamePlayer> players) {
        this.id = id;
        this.players = players;
    }

    /**
     * Creates a new Bingo game with the supplied participants.
     *
     * @param id      unique identifier of the game
     * @param players players participating in the game
     * @return a new Bingo game
     * @throws IllegalArgumentException if the identifier is null or no players are supplied
     */
    public static BingoGame create(String id, List<GamePlayer> players) {
        Assert.notNull(id, "id must not be null");
        Assert.notEmpty(players, "players must not be empty");
        return new BingoGame(id, players);
    }

    /**
     * Starts the game.
     *
     * @throws GameAlreadyStartedException if start is called after the game has already started
     */
    public synchronized void start() {
        if (status != GameStatus.WAITING) {
            throw new GameAlreadyStartedException();
        }
        status = GameStatus.IN_PROGRESS;
    }

    /**
     * Calls the next number on behalf of the current player.
     * <p>
     * The number is validated, recorded as called, marked on every player's
     * board, and the turn advances to the next player.
     * </p>
     *
     * @param callerPlayer player calling the number
     * @param number       number to be called
     * @throws GameNotStartedException      if the game is not in progress
     * @throws NotPlayersTurnException      if the caller is not the current player
     * @throws NumberAlreadyCalledException if the number has already been called
     * @throws InvalidNumberException       if the supplied number is outside the
     *                                      supported range
     */
    public synchronized void callNumber(Player callerPlayer, int number) {
        if (status != GameStatus.IN_PROGRESS) {
            throw new GameNotStartedException();
        }
        if (number < 1 || number > 25) {
            throw new InvalidNumberException();
        }

        GamePlayer callerGamePlayer = findPlayer(callerPlayer.id());
        if (!callerGamePlayer.equals(currentPlayer())) {
            throw new NotPlayersTurnException();
        }

        if (!calledNumbers.add(number)) {
            throw new NumberAlreadyCalledException();
        }

        for (GamePlayer player : players) player.mark(number);
        advanceTurn();
    }

    /**
     * Processes a Bingo claim made by a player.
     *
     * @param player player claiming Bingo
     * @return {@code true} if the claim is valid and the player wins the game;
     * {@code false} otherwise
     */
    public synchronized boolean claimBingo(Player player) {
        if (status != GameStatus.IN_PROGRESS) {
            return false;
        }
        GamePlayer gamePlayer = findPlayer(player.id());
        if (!gamePlayer.claimBingo()) {
            return false;
        }
        finish(gamePlayer);
        return true;
    }

    /**
     * Determines whether the game has finished.
     *
     * @return {@code true} if the game has ended; {@code false} otherwise
     */
    public synchronized boolean isFinished() {
        return status == GameStatus.FINISHED;
    }

    /**
     * Returns the player whose turn it is to call the next number.
     *
     * @return the current player
     */
    public GamePlayer currentPlayer() {
        return players.get(currentTurn);
    }

    /**
     * Returns the winner of the game, if one has been declared.
     *
     * @return an {@link Optional} containing the winner, or an empty
     * {@link Optional} if the game has not yet been won
     */
    public Optional<GamePlayer> winner() {
        return Optional.ofNullable(winner);
    }

    /**
     * Returns an immutable view of the numbers that have been called.
     *
     * @return the called numbers
     */
    public Set<Integer> calledNumbers() {
        return Collections.unmodifiableSet(calledNumbers);
    }

    /**
     * Returns the players participating in the game.
     *
     * @return an immutable list of game participants
     */
    public List<GamePlayer> players() {
        return List.copyOf(players);
    }

    /**
     * Advances the turn to the next player.
     */
    private void advanceTurn() {
        currentTurn = (currentTurn + 1) % players.size();
    }

    /**
     * Finds the game participant associated with the supplied player identifier.
     *
     * @param playerId identifier of the player
     * @return the matching game participant
     * @throws IllegalArgumentException if the player is not part of the game
     */
    private GamePlayer findPlayer(String playerId) {
        return players.stream()
                .filter(gamePlayer -> gamePlayer.getPlayer().id().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player is not part of the game"));
    }

    /**
     * Marks the specified player as the winner and completes the game.
     *
     * @param winner winning player
     */
    private void finish(GamePlayer winner) {
        this.winner = winner;
        this.status = GameStatus.FINISHED;
    }
}