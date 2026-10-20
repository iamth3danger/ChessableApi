package com.chessable.stockfish.util;

public sealed interface UciEvent permits UciEvent.ReadyOk, UciEvent.UciOk, UciEvent.Info,
        UciEvent.Thinking, UciEvent.BestMove {

    record ReadyOk() implements UciEvent {
    }
    record UciOk() implements UciEvent {
    }
    record Info(int multipv, int depth, int seldepth, EvalScore score, String move) implements UciEvent {
        boolean deepEnough(){
            return switch(score){
                case EvalScore.Mate m -> depth >= ((2 * m.ply()) + 2);
                case EvalScore.Cp _ -> false;
            };
        }

        EvalCompare compareScore(Info info){
            return this.score().compareEval(info.score());
        }

        boolean sameScore(Info info){
            return this.score.sameValue(info.score());
        }

        boolean isClose(Info info){
            return this.score()
                        .scoreDiff(info.score())
                        .orElse(Integer.MAX_VALUE) < 3;
        }
    }
    record Thinking() implements UciEvent {
    }
    record BestMove(String move) implements UciEvent {
    }
}
