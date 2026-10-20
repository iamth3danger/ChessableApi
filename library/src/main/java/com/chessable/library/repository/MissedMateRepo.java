package com.chessable.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import com.chessable.library.entity.MissedMate;
import com.chessable.library.entity.Status;

public interface MissedMateRepo extends JpaRepository<MissedMate, Long> {

    @Query(value = """
            SELECT *
            FROM missed_mate
            WHERE account_id = :accountID
            AND UPPER(status) = 'PENDING'
            LIMIT 5
            """, nativeQuery = true)
    List<MissedMate> findByAccountIdAndStatusPending(@Param("accountID") UUID accountID);


    
    @Query(value = """
            SELECT *
            FROM missed_mate
            WHERE account_id = :accountID
            AND UPPER(status) = 'MISSED'
            LIMIT 5
            """, nativeQuery = true)
    List<MissedMate> findByAccountIdAndStatusMissed(@Param("accountID") UUID accountID);

    @Query(value = """
    UPDATE missed_mate
    SET status = 'MISSED'
    WHERE id = :id
    """, nativeQuery = true)
    void setStatusMissed(@Param("id") Long id);


    @Query(value = """
    UPDATE missed_mate
    SET status = 'SOLVED'
    WHERE id = :id
    """, nativeQuery = true)
    void setStatusSolved(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = """
    UPDATE missed_mate
    SET status = :status
    WHERE id = :id
    """, nativeQuery = true)
    void setStatus(@Param("id") Long id, @Param("status") Status status);
            
}
