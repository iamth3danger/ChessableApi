package com.chessable.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;
import com.chessable.library.entity.AccountPuzzle;

public interface AccountPuzzleRepo extends JpaRepository<AccountPuzzle, UUID> {

    @Query(value = """
            SELECT user_id
            FROM account_puzzle
            WHERE solved  = false
            GROUP BY user_id
            HAVING COUNT(*) <= 5
            """, nativeQuery = true)
    List<UUID> findAccountsWithoutUnsolvedPuzzles();

    @Query(value = """
            SELECT *
            FROM account_puzzle
            WHERE user_id = :userId
            AND solved = false
            LIMIT 1
            """, nativeQuery = true)
    AccountPuzzle findNextPuzzle(@Param("userId") UUID userId);

    @Query(value = """
            SELECT COUNT(*)
            FROM account_puzzle
            WHERE user_id = :userId
            AND solved = false
            """, nativeQuery = true)
    Integer findUnsolvedPuzzlesCount(@Param("userId") UUID userId);

    @Modifying
    @Query(value = """
            UPDATE account_puzzle
            SET solved = true
            WHERE id = :puzzleId
            """, nativeQuery = true)
    void setSolvedTrue(@Param("puzzleId") UUID puzzleId);
}
