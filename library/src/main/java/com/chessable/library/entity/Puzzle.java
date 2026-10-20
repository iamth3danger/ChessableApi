package com.chessable.library.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Index;

@Entity
@Table(
    name = "puzzle",
    indexes = {
        @Index(name = "idx_tactic_pieceTypes", columnList = "tactic, pieceTypes"),
        @Index(name = "idx_pieceTypes", columnList = "pieceTypes"),
        @Index(name = "idx_rating", columnList = "rating")
    }
)
public class Puzzle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String fen;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String moves;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String tactic;

    @Column(nullable = false, columnDefinition = "INT")
    private int pieceTypes;

    @Column(nullable = false, columnDefinition = "INT")
    private int rating;
    

    // Constructors
    public Puzzle() {}

    public Puzzle(String fen, String moves, String tactic, int pieceTypes) {
        this.fen = fen;
        this.moves = moves;
        this.tactic = tactic;
        this.pieceTypes = pieceTypes;
    }
    

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFen() { return fen; }
    public void setFen(String fen) { this.fen = fen; }

    public String getMoves() { return moves; }
    public void setMoves(String moves) { this.moves = moves; }

    public String getTactic() { return tactic; }
    public void setTactic(String tactic) { this.tactic = tactic; }

    public int getPieceTypes() { return pieceTypes; }
    public void setPieceTypes(int pieceTypes) { this.pieceTypes = pieceTypes; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }


}
