package com.chessable.stockfish.util;

import com.chessable.stockfish.util.UciEvent.Info;

public class Candidate {
    private Info currInfo;
    private int sameScoreCount = 0;
    private int sameSeldepthCount = 0;
    private int moveDeltaCount = 0;
    private static final int MIN_THRESHOLD = 5;
    private static final int STABLE_THRESHOLD = 7;

    public Candidate(Info info) {
        this.currInfo = info;
    }

    public void update(Info info) {
        compareScores(info.score());
        compareMoves(info.move());
        compareSelDepth(info.seldepth());

        this.currInfo = info;
    }

    public boolean scoreConverged(){
        return deepEnough() && meetsThreshold() && seldepthStable() && currInfo.score().isMate();
    }

    public boolean deepEnough(){
        return currInfo.deepEnough();
    }

    private void compareSelDepth(int seldepth){
        if (seldepth == currInfo.seldepth()){
            sameSeldepthCount++;
        }
        else {
            sameSeldepthCount = 0;
        }
    }

    private void compareScores(EvalScore newScore) {
        if (currInfo.score().sameValue(newScore)) {
            sameScoreCount++;
            // System.out.println("scores are equal!!! " + sameScoreCount);
        } else {
            sameScoreCount = 0;
        }
    }

    private void compareMoves(String newMove) {
        if (!currInfo.move().equals(newMove)) {
            this.moveDeltaCount++;
        }
    }

    public EvalScore latestScore(){
        return currInfo.score();
    }

    public EvalCompare compareTo(Candidate candidate){
        return this.currInfo.compareScore(candidate.currInfo);
    }

    public boolean isTiedWith(Candidate candidate){
        return this.currInfo.sameScore(candidate.currInfo);
    }

    public boolean isClose(Candidate candidate){
        return this.currInfo.isClose(candidate.currInfo);
    }

    public boolean positionAlmostStable(){
        return this.sameScoreCount > MIN_THRESHOLD;
    }

    public boolean seldepthStable(){
        return switch(currInfo.score()){
            case EvalScore.Mate(int ply) -> this.sameSeldepthCount >= (ply);
            case EvalScore.Cp _ -> false;
        };
    }

    public boolean meetsThreshold() {
        return this.sameScoreCount > STABLE_THRESHOLD;
    }

    public Info getCurrInfo(){
        return currInfo;
    }
}
