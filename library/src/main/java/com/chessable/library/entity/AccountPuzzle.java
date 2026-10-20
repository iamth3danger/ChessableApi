package com.chessable.library.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Index;
import java.util.UUID;
import java.sql.Timestamp;

@Entity
@Table(
    name = "account_puzzle",
    indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_puzzle_id", columnList = "puzzle_id"),
        @Index(name = "idx_user_puzzle", columnList = "user_id, puzzle_id")
    }
)
public class AccountPuzzle {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "puzzle_id", nullable = false)
    private int puzzleId;

    @Column(name = "solved")
    private boolean solved;

    @Column(name = "solved_at")
    private Timestamp solvedAt;
    
    
    public AccountPuzzle(UUID userId, int puzzleId) {
        this.userId = userId;
        this.puzzleId = puzzleId;
        this.solved = false;
    }
    
    public AccountPuzzle() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public int getPuzzleId() {
        return puzzleId;
    }

    public void setPuzzleId(int puzzleId) {
        this.puzzleId = puzzleId;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public Timestamp getSolvedAt() {
        return solvedAt;
    }

    public void setSolvedAt(Timestamp solvedAt) {
        this.solvedAt = solvedAt;
    }
    
}
