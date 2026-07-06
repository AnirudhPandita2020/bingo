package com.anirudh.bingo.matchmaking.util;

import java.security.SecureRandom;

public final class RoomUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int ROOM_ID_LENGTH = 6;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private RoomUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Generates a random room ID consisting of uppercase letters and digits.
     *
     * @return a randomly generated room ID of length 6
     */
    public static String generateRoomId() {
        StringBuilder roomId = new StringBuilder(ROOM_ID_LENGTH);
        for (int i = 0; i < ROOM_ID_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            roomId.append(CHARACTERS.charAt(index));
        }
        return roomId.toString();
    }

}
