package com.chessable.stockfish.util;

public record MoveScore(
    String move,
    String fen,
    EvalScore eval
) {}

