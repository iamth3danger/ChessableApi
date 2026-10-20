package com.chessable.stockfish.util;

import java.util.OptionalInt;

public sealed interface EvalScore permits EvalScore.Mate, EvalScore.Cp {

    record Mate(int ply) implements EvalScore {

        MateOutcome outcome() {
            return ply > 0 ? MateOutcome.WIN : MateOutcome.LOSS;
        }

        EvalCompare compare(Mate other){
            if (this.ply() > other.ply() && other.outcome() == MateOutcome.WIN){return EvalCompare.BETTER;}
            if (this.ply() == other.ply()){return EvalCompare.EQUAL;}
            return EvalCompare.WORSE;
        }
    }

    record Cp(int value) implements EvalScore {
        EvalCompare compare(Cp other){
            if (this.value() < other.value()){return EvalCompare.BETTER;}
            if (this.value() == other.value()){return EvalCompare.EQUAL;}
            else {return EvalCompare.WORSE;}
        }
    }

    default boolean isMate() {
        return this instanceof Mate;
    }

    default boolean sameValue(EvalScore other) {
        if (other == null)
            return false;
        return switch (this) {
            case Mate m1 -> other instanceof Mate m2 && m1.ply() == m2.ply();
            case Cp c1 -> other instanceof Cp c2 && c1.value() == c2.value();
        };
    }

    default OptionalInt scoreDiff(EvalScore other) {
        if (other == null)
            return null;
        return switch (this) {
            case Mate m1 -> other instanceof Mate m2 ? OptionalInt.of(m1.ply() - m2.ply())
                    : OptionalInt.empty();
            case Cp c1 -> other instanceof Cp c2 ? OptionalInt.of(c1.value() - c2.value())
                    : OptionalInt.empty();
        };
    }

    default EvalCompare compareEval(EvalScore other){
        return switch(this){
            case Mate m1 -> other instanceof Mate m2 ? m1.compare(m2) : EvalCompare.WORSE;
            case Cp c1 -> other instanceof Cp c2 ?  c1.compare(c2) : EvalCompare.BETTER;
        };
    }
}
