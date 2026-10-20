package com.chessable.library.entity;

import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Column;

@Entity
@Table(name = "missed_mate")
public class MissedMate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID accountID;
    private UUID gameId;
    
    private String fen;
    private int mateIn;
    private int moveIndex;
    private String incorrectMove;
    private int incorrectMoveIndex;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public MissedMate() {
        this.status = Status.PENDING;
    }

    public MissedMate(String fen, int mateIn, UUID accountID, UUID gameId, int moveIndex, String incorrectMove, int incorrectMoveIndex) {
        this.fen = fen;
        this.mateIn = mateIn;
        this.accountID = accountID;
        this.gameId = gameId;
        this.incorrectMove = incorrectMove;
        this.incorrectMoveIndex = incorrectMoveIndex;
        this.status = Status.PENDING;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFen() {
        return fen;
    }
    public void setFen(String fen) {
        this.fen = fen;
    }
    public int getMateIn() {
        return mateIn;
    }
    public void setMateIn(int mateIn) {
        this.mateIn = mateIn;
    }
    public UUID getAccountId() {
        return accountID;
    }
    public void setAccountId(UUID accountID) {
        this.accountID = accountID;
    }
    public UUID getGameId() {
        return gameId;
    }
    public void setGameId(UUID gameId) {
       this.gameId = gameId;
    }
    public String getIncorrectMove() {
        return incorrectMove;
    }
    public void setIncorrectMove(String incorrectMove) {
        this.incorrectMove = incorrectMove;
    }
    public int getMoveIndex() {
        return moveIndex;
    }
    public void setMoveIndex(int moveIndex) {
        this.moveIndex = moveIndex;
    }
    public int getIncorrectMoveIndex() {
        return incorrectMoveIndex;
    }
    public void setIncorrectMoveIndex(int incorrectMoveIndex) {
        this.incorrectMoveIndex = incorrectMoveIndex;
    }
}
