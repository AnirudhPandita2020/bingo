package com.anirudh.bingo.matchmaking.repository;

import com.anirudh.bingo.core.room.Room;

import java.util.Optional;

public interface RoomRepository {

    Room save(Room room);

    Optional<Room> findById(String roomId);

    void delete(String roomId);
}
