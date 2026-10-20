package com.chessable.library.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import jakarta.persistence.Index;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
@Entity
@Table(
    name = "account_game",
    indexes = {
        @Index(name = "idx_account_game_user_timestamp", columnList = "user_id, timestamp DESC")
    }
)
public class AccountGame {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String moves;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlayerSide playerSide;

    private String result;

    private String timeControl;

    private Timestamp timestamp;

    private String opponent;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissedStatus missedStatus;

    public AccountGame() {
        this.missedStatus = MissedStatus.WAITING;
    }

    public AccountGame(UUID id, UUID userId, String moves, String result, String timeControl, Timestamp timestamp,
            String opponent) {
        this.id = id;
        this.userId = userId;
        this.moves = moves;
        this.result = result;
        this.timeControl = timeControl;
        this.timestamp = timestamp;
        this.opponent = opponent;
        this.missedStatus = MissedStatus.WAITING;
    }

    

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

    public String getMoves() {
        return moves;
    }

    public void setMoves(String moves) {
        this.moves = moves;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTimeControl() {
        return timeControl;
    }

    public void setTimeControl(String timeControl) {
        this.timeControl = timeControl;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public MissedStatus getMissedStatus() {
        return missedStatus;
    }

    public void setMissedStatus(MissedStatus missedStatus) {
        this.missedStatus = missedStatus;
    }

    public PlayerSide getPlayerSide() {
        return playerSide;
    }

    public void setPlayerSide(PlayerSide playerSide) {
        this.playerSide = playerSide;
    }
}
