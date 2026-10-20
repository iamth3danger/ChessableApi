package com.chessable.instance;


import java.time.Instant;
import java.util.UUID;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.PieceType;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Transient;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Entity
@Table(name = "tactic_instance")
public class TacticInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "account_game_id")
    private UUID accountGameId;
    
    @Column(name = "account_id")
    private UUID accountId;
    
    private String tacticName;

    @Transient
    private Move move;

    private String moveString;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockfishStatus stockfishStatus;


    private Instant accessedAt;
    
    private String fen;

    @Transient
    private List<PieceType> pieceTypes = new ArrayList<>();
    
    private int pieceTypesMask;
    
    public TacticInstance() {
        this.status = TaskStatus.PENDING;
        this.stockfishStatus = StockfishStatus.UNVISITED;
    }
    
    public TacticInstance(String tacticName, Move move, String fen, List<PieceType> pieceTypes) {
        this.tacticName = tacticName;
        this.move = move;
        this.moveString = move.toString();
        this.fen = fen;
        this.pieceTypes = pieceTypes;
        this.pieceTypesMask = PieceMask.getPieceMask(pieceTypes);
        this.status = TaskStatus.PENDING;
        this.stockfishStatus = StockfishStatus.UNVISITED;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }
    public void setTacticName(String tacticName) {
        this.tacticName = tacticName;
    }
    
    public String getTacticName() {
        return tacticName;
    }
    public String getMoveString() {
        return moveString;
    }
    
    public void setMoveString(String moveString) {
        this.moveString = moveString;
    }
    
    public Move getMove() {
        return move;
    }
    
    public void setMove(Move move) {
        this.move = move;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getFen() {
        return fen;
    }
    
    public void setFen(String fen) {
        this.fen = fen;
    }
    

    public void setPieceTypes(List<PieceType> pieceTypes) {
        this.pieceTypes = pieceTypes;
    }

    public void setPieceTypesMask(int pieceTypesMask) {
        this.pieceTypesMask = pieceTypesMask;
    }
    
    public UUID getAccountGameId() {
        return accountGameId;
    }

    public void setAccountGameId(UUID accountGameId) {
        this.accountGameId = accountGameId;
    }
    
    public List<PieceType> getPieceTypes() {
        return new ArrayList<>(pieceTypes);
    }

    public int getPieceTypesMask() {
        return pieceTypesMask;
    }

    public String toString() {
        return "TacticInstance: " + (move != null ? move.toString() : "null") + " Fen: " + fen + " Piece Types: " + pieceTypes.toString();
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Instant getAccessedAt() {
        return accessedAt;
    }

    public void setAccessedAt(Instant accessedAt) {
        this.accessedAt = accessedAt;
    }
    
    public StockfishStatus getStockfishStatus() {
        return stockfishStatus;
    }

    public void setStockfishStatus(StockfishStatus stockfishStatus) {
        this.stockfishStatus = stockfishStatus;
    }
}
