package com.chessable.library.message;

public class Evaluation {

    private ScoreType type;
    private int value;
    private String fen;
    private String bestMove;
    private String moveMade;
    private int moveIndex;

    public Evaluation(ScoreType type, int value, String fen) {
        this.type = type;
        this.value = value;
        this.fen = fen;
    }
    
    public ScoreType getType() {
        return type;
    }

    public void setType(ScoreType type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
    public String getFen() {
        return fen;
    }

    public void setFen(String fen) {
        this.fen = fen;
    }

    public void setMoveIndex(int moveIndex) {
        this.moveIndex = moveIndex;
    }

    public int getMoveIndex() {
        return moveIndex;
    }

    public void setBestMove(String bestMove) {
        this.bestMove = bestMove;
    }

    public String getBestMove() {
        return bestMove;
    }

    public void setMoveMade(String moveMade) {
        this.moveMade = moveMade;
    }

    public String getMoveMade() {
        return moveMade;
    }

    @Override
    public String toString() {
        return "Evaluation [type=" + type + ", value=" + value + ", fen=" + fen + ", bestMove=" + bestMove != null ? bestMove : "null" + "]";
    }
    
}
