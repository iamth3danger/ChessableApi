package com.chessable.library.message;

public class MoveFen {
    
    private String move;
    private int moveIndex;
    private String fen;

    public MoveFen(String move, int moveIndex, String fen) {
        this.move = move;
        this.moveIndex = moveIndex;
        this.fen = fen;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public int getMoveIndex() {
        return moveIndex;
    }

    public void setMoveIndex(int moveIndex) {
        this.moveIndex = moveIndex;
    }

    public String getFen() {
        return fen;
    }

    public void setFen(String fen) {
        this.fen = fen;
    }

}
