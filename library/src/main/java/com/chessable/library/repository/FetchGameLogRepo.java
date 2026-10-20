package com.chessable.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.chessable.library.entity.FetchGameLog;

public interface FetchGameLogRepo extends JpaRepository<FetchGameLog, Integer> {
    FetchGameLog findByUserId(UUID userId); 
    @Query(value = """
            SELECT user_id 
            FROM fetch_game_log
            WHERE timestamp <= CURRENT_DATE - INTERVAL '3 days'
            """, nativeQuery = true)
    Set<UUID> findUserIdsToUpdateGames();

    @Query("SELECT f FROM FetchGameLog f WHERE f.userId IN :userIds")
    List<FetchGameLog> findByUserIdIn(@Param("userIds") List<UUID> userIds);
}