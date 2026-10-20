package com.chessable.tactic_lib.inter;
import com.chessable.instance.TacticInstance;
import com.chessable.tactic_lib.utils.BoardState;

import java.util.Optional;
import com.github.bhlangonijr.chesslib.move.Move;

public interface Tactic {
    Optional<TacticInstance> evaluate(Move move);
    String getName();
    void setBoardState(BoardState boardState);
}
