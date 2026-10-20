package com.chessable.library.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import com.chessable.library.entity.AccountGame;

public interface AccountGameRepo extends JpaRepository<AccountGame, UUID> {

    /*
     * returns number of games for a user
     * Used in AccountGameServiceImpl.java and then in TacticInstanceServiceImpl.java
     * Important for deleting old games and tactics
     */

    long countByUserId(UUID userId);


    /*
     * returns the earliest games for a user
     * Used in AccountGameServiceImpl.java and then in TacticInstanceServiceImpl.java
     * Important for deleting old games and tactics
     */

    @Query(value = """
            SELECT a.id FROM account_game a
            WHERE a.user_id = :userId
            ORDER BY a.timestamp ASC
            LIMIT :limit
            """, nativeQuery = true)
    List<UUID> findEarliestGames(@Param("userId") UUID userId, @Param("limit") int limit);


    /*
     * Why am I using this?
     * I think it's used in FetchGameServiceImpl.java
     * TODO check if this is used
     */

    @Query(value = """
            SELECT MAX(timestamp) FROM account_game
            WHERE user_id = :userId
            """, nativeQuery = true)
    Timestamp findLatestTimestamp(@Param("userId") UUID userId);

    @Query(value = """
            SELECT user_id FROM account_game
            GROUP BY user_id
            HAVING COUNT(*) = COUNT(CASE WHEN visited THEN 1 END)
            """, nativeQuery = true)
    Set<UUID> findAccountsToUpdateGames();

    @Query(value = """
            SELECT DISTINCT user_id 
            FROM account_game
            WHERE visited = false
            """, nativeQuery = true)
    Set<UUID> findAccountsToUpdateTactics();

    @Query(value = """
            SELECT * FROM account_game
            WHERE user_id = :userId
            AND NOT visited
            ORDER BY timestamp DESC
            LIMIT 10
            """, nativeQuery = true)
    List<AccountGame> findTenUnvisitedGames(@Param("userId") UUID userId);

    @Query(value = """
            SELECT * FROM account_game
            WHERE user_id = :userId
            AND NOT visited
            """, nativeQuery = true)
    List<AccountGame> findUnvisitedGames(@Param("userId") UUID userId);

    @Transactional
    @Modifying
    @Query(value = """
            DELETE FROM account_game
            WHERE id IN :gameIds
            """, nativeQuery = true)
    void deleteByIds(@Param("gameIds") List<UUID> gameIds);

    @Query(value = """
        SELECT id
        FROM (
            SELECT id,
                   ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY timestamp DESC) AS rn
            FROM account_game
            WHERE user_id IN :userIds
        ) AS sub
        WHERE sub.rn > 100
        """, nativeQuery = true)
    List<UUID> findOldGameIds(@Param("userIds") List<UUID> userIds);

    @Query(value = """
        SELECT id
        FROM (
            SELECT id,
                   ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY timestamp DESC) AS rn
            FROM account_game
            WHERE user_id = :userId
        ) AS sub
        WHERE sub.rn > 100
        """, nativeQuery = true)
    List<UUID> findOldGameIdsByUserId(@Param("userId") UUID userId);

    @Query(value = """
            SELECT * FROM account_game
            WHERE missed_status = 'WAITING'
            """, nativeQuery = true)
    List<AccountGame> findByMissedStatusWaiting();


}
