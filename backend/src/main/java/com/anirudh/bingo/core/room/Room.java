package com.anirudh.bingo.core.room;

import com.anirudh.bingo.core.board.BingoBoard;
import com.anirudh.bingo.core.game.BingoGame;
import com.anirudh.bingo.core.player.GamePlayer;
import com.anirudh.bingo.core.player.Player;
import com.anirudh.bingo.exception.room.*;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.*;

public final class Room {
    @Getter
    private final String id;
    private final Map<String, Player> players = new LinkedHashMap<>();
    @Getter
    private final int maxPlayers;
    private String hostPlayerId;
    @Getter
    private RoomStatus roomStatus = RoomStatus.OPEN;
    @Getter
    private BingoGame bingoGame;

    private Room(String id, int maxPlayers) {
        this.id = id;
        this.maxPlayers = maxPlayers;
    }

    /**
     * Creates a new room with the specified identifier and player capacity.
     *
     * @param id         unique identifier of the room
     * @param maxPlayers maximum number of players allowed in the room
     * @return a newly created room
     * @throws IllegalArgumentException if the id is null or the maximum player
     *                                  count is less than two
     */
    public static Room create(String id, int maxPlayers) {
        Assert.notNull(id, "id must not be null");
        Assert.isTrue(maxPlayers > 1, "A minimum of two players is required to create a room");
        return new Room(id, maxPlayers);
    }

    /**
     * Adds a player to the room.
     * <p>
     * The first player to join automatically becomes the room host.
     * Players can only join while the room is open.
     * </p>
     *
     * @param player player joining the room
     * @throws RoomClosedException          Thrown when a player try's to join after the room got closed
     * @throws RoomFullException            Thrown when room's maximum capacity is reached and cannot allow more players to join.
     * @throws PlayerAlreadyInRoomException Thrown when a player try's to join again
     */
    public synchronized void join(Player player) {
        Assert.notNull(player, "player must not be null");
        if (roomStatus != RoomStatus.OPEN) {
            throw new RoomClosedException();
        }
        if (players.size() >= maxPlayers) {
            throw new RoomFullException();
        }
        if (players.containsKey(player.id())) {
            throw new PlayerAlreadyInRoomException("Player has already joined the room");
        }
        if (hostPlayerId == null) {
            hostPlayerId = player.id();
        }
        players.put(player.id(), player);
    }

    /**
     * Removes a player from the room.
     * <p>
     * If the host leaves, ownership is transferred to the next available player.
     * If the room becomes empty, it is marked as closed.
     * </p>
     *
     * @param playerId player leaving the room
     */
    public synchronized Player leave(String playerId) {
        Assert.notNull(playerId, "playerId must not be null");
        if (!players.containsKey(playerId)) {
            return null;
        }

        var player = players.remove(playerId);
        if (players.size() == 1) {
            // Minimum number of players required
            bingoGame = null;
            roomStatus = RoomStatus.OPEN;
            return player;
        }

        if (players.isEmpty()) {
            hostPlayerId = null;
            roomStatus = RoomStatus.CLOSED;
            return player;
        }

        if (playerId.equals(hostPlayerId)) {
            hostPlayerId = players.keySet().iterator().next();
        }
        return player;
    }

    /**
     * Starts a new Bingo game for all players currently in the room.
     * <p>
     * Each participant is assigned a new board before the game begins.
     * Only the room host may start the game.
     * </p>
     *
     * @param callerPlayerId identifier of the player attempting to start the game
     * @throws RoomClosedException           Thrown when the start is called after the room has already transitioned to {@link RoomStatus#IN_GAME}
     * @throws NotRoomHostException          Thrown when the {@code callerPlayerId} is not the host of the game
     * @throws MinimumPlayersNotMetException Thrown when start game is called without the minimum number of players
     */
    public synchronized void startGame(String callerPlayerId) {
        Assert.notNull(callerPlayerId, "hostPlayer must not be null");
        if (RoomStatus.OPEN != roomStatus) {
            throw new RoomClosedException();
        }
        if (!callerPlayerId.equals(hostPlayerId)) {
            throw new NotRoomHostException();
        }
        if (players.size() < 2) {
            throw new MinimumPlayersNotMetException();
        }

        List<GamePlayer> gamePlayers = players.values()
                .stream()
                .map(player -> GamePlayer.create(player, BingoBoard.create()))
                .toList();

        bingoGame = BingoGame.create(UUID.randomUUID().toString(), gamePlayers);
        bingoGame.start();
        roomStatus = RoomStatus.IN_GAME;
    }

    /**
     * Returns the current host of the room.
     *
     * @return the host if one exists
     */
    public Optional<Player> host() {
        return Optional.ofNullable(players.get(hostPlayerId));
    }

    /**
     * Returns an immutable snapshot of the players currently in the room.
     *
     * @return players in join order
     */
    public List<Player> getPlayers() {
        return List.copyOf(players.values());
    }

    /**
     * Calls the specified number on behalf of a player.
     *
     * @param playerId identifier of the player calling the number
     * @param number   number being called
     * @throws PlayerNotInRoomException Thrown when the player is not part of the room.
     */
    public synchronized void callNumber(String playerId, int number) {
        ensureGameInProgress();
        if (!players.containsKey(playerId)) {
            throw new PlayerNotInRoomException();
        }
        bingoGame.callNumber(players.get(playerId), number);
    }

    /**
     * Attempts to claim Bingo for the specified player.
     *
     * @param playerId identifier of the player claiming Bingo
     * @return {@code true} if the claim is valid; otherwise {@code false}
     */
    public synchronized boolean claimBingo(String playerId) {
        ensureGameInProgress();
        if (!players.containsKey(playerId)) {
            throw new PlayerNotInRoomException();
        }
        return bingoGame.claimBingo(players.get(playerId));
    }

    /**
     * Returns the player whose turn it is.
     *
     * @return current player
     */
    public synchronized GamePlayer currentPlayer() {
        ensureGameInProgress();
        return bingoGame.currentPlayer();
    }

    /**
     * Returns the winner of the game, if one exists.
     *
     * @return winning player
     */
    public synchronized Optional<GamePlayer> winner() {
        ensureGameInProgress();
        return bingoGame.winner();
    }

    /**
     * Indicates whether a game is currently active in this room.
     *
     * @return {@code true} if a game is in progress
     */
    public boolean hasActiveGame() {
        return roomStatus == RoomStatus.IN_GAME;
    }

    /**
     * Ensures that a game is currently in progress.
     *
     * @throws RoomNotInProgressException if no active game exists
     */
    private void ensureGameInProgress() {
        if (roomStatus != RoomStatus.IN_GAME) {
            throw new RoomNotInProgressException();
        }
    }

}
