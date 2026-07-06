package com.anirudh.bingo.utils;

import com.anirudh.bingo.core.board.BingoBoard;
import com.anirudh.bingo.core.game.BingoGame;
import com.anirudh.bingo.core.player.GamePlayer;
import com.anirudh.bingo.core.player.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class GameTestUtils {

    private static final List<Integer> VALID_SEQUENCE = IntStream.range(1, 26).boxed().toList();

    private GameTestUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static List<Integer> validNumberSequence() {
        return IntStream.range(1, 26).boxed().collect(Collectors.toList());
    }

    public static Player validPlayer() {
        return new Player(UUID.randomUUID().toString(), "Player %d".formatted(System.currentTimeMillis()));
    }

    public static List<GamePlayer> validPlayers(int size, boolean randomize) {
        return IntStream.range(1, size + 1)
                .mapToObj(i -> {
                    Player player = new Player(UUID.randomUUID().toString(), "Player %d".formatted(i));
                    return GamePlayer.create(player, randomize ? BingoBoard.create() : BingoBoard.create(VALID_SEQUENCE));
                }).toList();
    }

    public static void makeAllPlayersWin(BingoGame bingoGame) {
        for (int number = 1; number <= 21; number++) {
            bingoGame.callNumber(bingoGame.currentPlayer().getPlayer(), number);
        }
    }


}
