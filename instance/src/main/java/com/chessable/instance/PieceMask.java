package com.chessable.instance;

import java.util.List;
import java.util.Map;
import com.github.bhlangonijr.chesslib.PieceType;


public class PieceMask {
    private static final int KING = 1 << 0;   // 1
    private static final int QUEEN = 1 << 1;  // 2
    private static final int ROOK = 1 << 2;   // 4
    private static final int BISHOP = 1 << 3; // 8
    private static final int KNIGHT = 1 << 4; // 16
    private static final int PAWN = 1 << 5;

    public static final Map<PieceType, Integer> PIECE_MASKS = Map.of(
        PieceType.KING, KING,
        PieceType.QUEEN, QUEEN,
        PieceType.ROOK, ROOK,
        PieceType.BISHOP, BISHOP,
        PieceType.KNIGHT, KNIGHT,
        PieceType.PAWN, PAWN
    );


    public static int getPieceMask(PieceType pieceType) {
        return PIECE_MASKS.get(pieceType);
    }

    public static int getPieceMask(List<PieceType> pieceTypes) {
        int mask = 0;
        for (PieceType pieceType : pieceTypes) {
            mask |= getPieceMask(pieceType);
        }
        return mask;
    }
}
