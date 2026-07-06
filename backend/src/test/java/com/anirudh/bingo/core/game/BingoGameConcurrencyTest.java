package com.anirudh.bingo.core.game;

import com.anirudh.bingo.utils.CamelCaseDisplayNameGenerator;
import com.anirudh.bingo.utils.GameTestUtils;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.*;

import static com.anirudh.bingo.utils.ConcurrencyTestUtils.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class BingoGameConcurrencyTest {

    private BingoGame startedGame() {
        BingoGame bingoGame = BingoGame.create(UUID.randomUUID().toString(), GameTestUtils.validPlayers(2, true));
        bingoGame.start();
        return bingoGame;
    }

    @Test
    public void testWhenTwoPlayersClaimBingoSimultaneouslyThenOnlyOnePlayerWins() throws InterruptedException, ExecutionException {
        BingoGame bingoGame = startedGame();
        GameTestUtils.makeAllPlayersWin(bingoGame);

        try (final ExecutorService executorService = Executors.newFixedThreadPool(2)) {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            Callable<Boolean> playerOneClaim = () -> {
                await(countDownLatch);
                return bingoGame.claimBingo(bingoGame.players().getFirst().getPlayer());
            };

            Callable<Boolean> playerTwoClaim = () -> {
                await(countDownLatch);
                return bingoGame.claimBingo(bingoGame.players().get(1).getPlayer());
            };

            var first = executorService.submit(playerOneClaim);
            var second = executorService.submit(playerTwoClaim);
            countDownLatch.countDown();
            int successfulClaims = 0;
            if (first.get()) successfulClaims++;
            if (second.get()) successfulClaims++;

            assertEquals(1, successfulClaims);
            assertTrue(bingoGame.isFinished());
            assertTrue(bingoGame.winner().isPresent());
        }
    }
}
