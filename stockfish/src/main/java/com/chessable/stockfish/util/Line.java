package com.chessable.stockfish.util;

public record Line(int depth, int selDepth, int multipv, EvalScore score, String move){

    boolean deepEnough(){
        return switch(score){
            case EvalScore.Mate m -> depth >= ((2 * m.ply()) + 2);
            case EvalScore.Cp _ -> false;
        };
    }
}
