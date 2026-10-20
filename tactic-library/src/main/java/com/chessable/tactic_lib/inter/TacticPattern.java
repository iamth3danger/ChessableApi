package com.chessable.tactic_lib.inter;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
public interface TacticPattern {
    public boolean matches(Piece attacker, Piece piece);
    public boolean shouldCapture(Square squareInfront, Square attacker, Square importantSquare);
}
