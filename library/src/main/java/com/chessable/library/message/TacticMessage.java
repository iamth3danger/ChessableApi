package com.chessable.library.message;

public class TacticMessage {
    private int id;
    private String fen;
    private String move;
    private String verification;

    public TacticMessage(int id, String fen, String move) {
        this.id = id;
        this.fen = fen;
        this.move = move;
        this.verification = null;
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
    public String getmove() {
        return move;
    }
    public void setmove(String move) {
        this.move = move;
    }
    public String getVerification() {
        return verification;
    }
    public void setVerification(String verification) {
        this.verification = verification;
    }

}