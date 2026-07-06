package com.anirudh.bingo.core.room;

import com.anirudh.bingo.core.player.Player;
import com.anirudh.bingo.exception.room.RoomClosedException;
import com.anirudh.bingo.exception.room.RoomFullException;
import com.anirudh.bingo.utils.CamelCaseDisplayNameGenerator;
import com.anirudh.bingo.utils.GameTestUtils;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.*;

import static com.anirudh.bingo.utils.ConcurrencyTestUtils.await;
import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class RoomConcurrencyTest {

    @Test
    public void testWhenRoomHasOnlyOneSlotLeftAndTwoPlayersTryToJoinThenOnlyOnePlayerIsSlotted() throws InterruptedException, ExecutionException {
        Room room = Room.create(UUID.randomUUID().toString(), 3);
        Player firstPlayer = GameTestUtils.validPlayer();
        Player secondPlayer = GameTestUtils.validPlayer();

        room.join(firstPlayer);
        room.join(secondPlayer);

        try (final ExecutorService executorService = Executors.newFixedThreadPool(2)) {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            Callable<Boolean> thirdPlayerJoining = () -> {
                try {
                    await(countDownLatch);
                    Player player = GameTestUtils.validPlayer();
                    room.join(player);
                    return true;
                } catch (RoomFullException e) {
                    return false;
                }
            };

            Callable<Boolean> fourthPlayerJoining = () -> {
                try {
                    await(countDownLatch);
                    Player player = GameTestUtils.validPlayer();
                    room.join(player);
                    return true;
                } catch (RoomFullException e) {
                    return false;
                }
            };

            var thirdPlayerFuture = executorService.submit(thirdPlayerJoining);
            var fourthPlayerFuture = executorService.submit(fourthPlayerJoining);
            countDownLatch.countDown();
            thirdPlayerFuture.get();
            fourthPlayerFuture.get();

            assertEquals(3, room.getPlayers().size());
        }
    }

    @Test
    public void testWhenSamePlayerTriesToJoinTheSameRoomTwiceThenPlayerIsOnlyJoinedOnce() throws InterruptedException, ExecutionException {
        Room room = Room.create(UUID.randomUUID().toString(), 3);
        Player firstPlayer = GameTestUtils.validPlayer();
        Player secondPlayer = GameTestUtils.validPlayer();
        room.join(firstPlayer);
        room.join(secondPlayer);

        try (final ExecutorService executorService = Executors.newFixedThreadPool(2)) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            Player thirdPlayer = GameTestUtils.validPlayer();

            Callable<Boolean> thirdPlayerJoining = () -> {
                try {
                    await(countDownLatch);
                    room.join(thirdPlayer);
                    return true;
                } catch (RoomFullException e) {
                    return false;
                }
            };

            Callable<Boolean> duplicateThirdPlayerJoining = () -> {
                try {
                    await(countDownLatch);
                    room.join(thirdPlayer);
                    return true;
                } catch (RoomFullException e) {
                    return false;
                }
            };

            var thirdPlayerJoiningFuture = executorService.submit(thirdPlayerJoining);
            var duplicateThirdPlayerJoiningFuture = executorService.submit(duplicateThirdPlayerJoining);
            countDownLatch.countDown();
            thirdPlayerJoiningFuture.get();
            duplicateThirdPlayerJoiningFuture.get();

            assertEquals(3, room.getPlayers().size());
            assertEquals(1, room.getPlayers().stream().filter(p -> p.equals(thirdPlayer)).count());
        }
    }

    @Test
    public void testWhenHostLeavesAndAtTheSameTimeAnotherPlayerJoinsThenNextInLinePlayerShouldBeHostAndThirdPlayerShouldJoin() throws InterruptedException, ExecutionException {
        Room room = Room.create(UUID.randomUUID().toString(), 3);
        Player hostPlayer = GameTestUtils.validPlayer();
        Player secondPlayer = GameTestUtils.validPlayer();
        room.join(hostPlayer);
        room.join(secondPlayer);

        try (final ExecutorService executorService = Executors.newFixedThreadPool(2)) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            Player thirdPlayer = GameTestUtils.validPlayer();

            Runnable thirdPlayerJoining = () -> {
                await(countDownLatch);
                room.join(thirdPlayer);
            };

            Runnable hostPlayerLeaving = () -> {
                await(countDownLatch);
                room.leave(hostPlayer.id());
            };

            var thirdPlayerJoiningFuture = executorService.submit(thirdPlayerJoining);
            var hostPlayerLeavingFuture = executorService.submit(hostPlayerLeaving);
            countDownLatch.countDown();
            thirdPlayerJoiningFuture.get();
            hostPlayerLeavingFuture.get();

            assertEquals(2, room.getPlayers().size());
            assertEquals(room.getPlayers().getFirst(), secondPlayer);
            assertEquals(room.getPlayers().getLast(), thirdPlayer);
        }
    }

    @Test
    public void testWhenAllPlayersLeaveAtTheSameTimeThenRoomGetsClosed() throws InterruptedException, ExecutionException {
        Room room = Room.create(UUID.randomUUID().toString(), 2);
        Player firstPlayer = GameTestUtils.validPlayer();
        Player secondPlayer = GameTestUtils.validPlayer();
        room.join(firstPlayer);
        room.join(secondPlayer);

        try (final ExecutorService executorService = Executors.newFixedThreadPool(2)) {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            Runnable firstPlayerLeaving = () -> {
                await(countDownLatch);
                room.leave(firstPlayer.id());
            };

            Runnable secondPlayerLeaving = () -> {
                await(countDownLatch);
                room.leave(secondPlayer.id());
            };

            var firstPlayerLeavingFuture = executorService.submit(firstPlayerLeaving);
            var secondPlayerLeavingFuture = executorService.submit(secondPlayerLeaving);
            countDownLatch.countDown();
            firstPlayerLeavingFuture.get();
            secondPlayerLeavingFuture.get();
            assertTrue(room.getPlayers().isEmpty());
            assertEquals(RoomStatus.CLOSED, room.getRoomStatus());
            assertTrue(room.host().isEmpty());
        }
    }

    @Test
    public void testWhenPlayerJoinsConcurrentlyWithGameStartThenRoomEndsInAConsistentState()
            throws Exception {

        Room room = Room.create(UUID.randomUUID().toString(), 3);

        Player host = GameTestUtils.validPlayer();
        Player secondPlayer = GameTestUtils.validPlayer();
        Player thirdPlayer = GameTestUtils.validPlayer();

        room.join(host);
        room.join(secondPlayer);

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {

            CountDownLatch startLatch = new CountDownLatch(1);

            Callable<Boolean> joinTask = () -> {
                await(startLatch);
                try {
                    room.join(thirdPlayer);
                    return true;
                } catch (IllegalStateException ex) {
                    return false;
                }
            };

            Callable<Boolean> startTask = () -> {
                await(startLatch);
                room.startGame(host.id());
                return true;
            };

            Future<Boolean> joinFuture = executor.submit(joinTask);
            Future<Boolean> startFuture = executor.submit(startTask);

            startLatch.countDown();

            boolean joined = joinFuture.get();
            boolean started = startFuture.get();

            assertTrue(started);

            assertEquals(RoomStatus.IN_GAME, room.getRoomStatus());

            if (joined) {
                assertEquals(3, room.getPlayers().size());
                assertTrue(room.getPlayers().contains(thirdPlayer));
            } else {
                assertEquals(2, room.getPlayers().size());
                assertFalse(room.getPlayers().contains(thirdPlayer));
            }

            assertTrue(room.hasActiveGame());
        }
    }

    @Test
    public void testWhenHostStartsTheGameTwiceThenGameOnlyStartsForOneOfTheCalls() throws InterruptedException, ExecutionException {
        Room room = Room.create(UUID.randomUUID().toString(), 3);
        Player host = GameTestUtils.validPlayer();
        Player secondPlayer = GameTestUtils.validPlayer();
        room.join(host);
        room.join(secondPlayer);

        try (final ExecutorService executorService = Executors.newFixedThreadPool(2)) {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            Callable<Boolean> startGame = () -> {
                try {
                    await(countDownLatch);
                    room.startGame(host.id());
                    return true;
                } catch (RoomClosedException e) {
                    return false;
                }
            };

            var firstStartGameFuture = executorService.submit(startGame);
            var secondStartGameFuture = executorService.submit(startGame);
            countDownLatch.countDown();
            int success = 0;
            boolean firstStarted = firstStartGameFuture.get();
            boolean secondStarted = secondStartGameFuture.get();
            if (firstStarted) success++;
            if (secondStarted) success++;

            assertEquals(1, success);
            assertEquals(RoomStatus.IN_GAME, room.getRoomStatus());
            assertTrue(room.hasActiveGame());
        }
    }

}
