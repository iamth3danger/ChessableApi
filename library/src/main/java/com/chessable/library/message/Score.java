package com.chessable.library.message;

public class Score {

    private ScoreType type;
    private int value;
    private String fen;
    private Integer moveIndex;
    private String move;

    public Score(ScoreType type, int value, String move, String fen) {
        this.type = type;
        this.value = value;
        this.move = move;
        this.fen = fen;
    }

    public Score(ScoreType type, int value, int moveIndex, String fen) {
        this.type = type;
        this.value = value;
        this.moveIndex = moveIndex;
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

    public void setFen(String fen) {
        this.fen = fen;
    }
    
    public String getFen() {
        return fen;
    }

    public Integer getMoveIndex() {
        return moveIndex;
    }

    public void setMoveIndex(Integer moveIndex) {
        this.moveIndex = moveIndex;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public String getMove() {
        return move;
    }

    @Override
    public String toString() {
        return "Score [type=" + type + ", value=" + value + ", fen=" + fen + "]";
    }
    
}
