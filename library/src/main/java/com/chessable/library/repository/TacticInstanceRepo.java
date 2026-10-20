package com.chessable.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import com.chessable.instance.TacticInstance;

public interface TacticInstanceRepo extends JpaRepository<TacticInstance, Integer> {
    List<TacticInstance> findByAccountGameId(UUID accountGameId);
    void deleteByAccountGameId(UUID accountGameId);

    @Query(value = """
            SELECT DISTINCT account_id
            FROM tactic_instance
            WHERE visited = false
            """, nativeQuery = true)
    Set<UUID> findUnvisitedAccountIds();

    @Query(value = """
            SELECT * FROM tactic_instance
            WHERE account_id = :accountId
            AND visited = false
            """, nativeQuery = true)
    List<TacticInstance> findUnvisitedTacticsByAccountId(@Param("accountId") UUID accountId);

    @Modifying
    @Query(value = """
            DELETE FROM tactic_instance
            WHERE account_game_id IN :gameIds
            """, nativeQuery = true)
    void deleteByAccountGameIds(@Param("gameIds") List<UUID> gameIds);

    List<TacticInstance> findByAccountId(UUID accountId);

    @Query(value = """
            SELECT *
            FROM tactic_instance
            WHERE account_id = :accountId
            AND visited = false
            LIMIT 1
            """, nativeQuery = true)
    TacticInstance findNextTactic(@Param("accountId") UUID accountId);

    @Modifying
    @Transactional
    @Query(value = """
        DELETE FROM tactic_instance ti
        WHERE ti.account_game_id IS NULL
           OR NOT EXISTS (
               SELECT 1
               FROM account_game ag
               WHERE ag.id = ti.account_game_id
           )
        """, nativeQuery = true)
    int deleteOrphanedTactics();

    @Modifying
    @Transactional
    @Query(value = """
        DELETE FROM tactic_instance ti
        WHERE ti.account_id = :accountId
        AND NOT EXISTS (
                SELECT 1
                FROM account_game ag
                WHERE ag.id = ti.account_game_id 
        )
        """, nativeQuery = true)
    int deleteOrphanedTacticsById(@Param("accountId") UUID accountId);



                        
}