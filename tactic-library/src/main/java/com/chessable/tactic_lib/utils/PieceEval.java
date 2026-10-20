package com.chessable.tactic_lib.utils;

import com.chessable.tactic_lib.enumerate.Direction;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.PieceType;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class PieceEval {
    private static final Map<PieceType, Integer> MATERIAL_PIECE_VALUE =
    Map.of(
        PieceType.PAWN, 1,
        PieceType.KNIGHT, 3,
        PieceType.BISHOP, 3,
        PieceType.ROOK, 5,
        PieceType.QUEEN, 9,
        PieceType.KING, 100
    );

    private static final Map<PieceType, ArrayList<Direction>> PIECE_DIRECTIONS =
    Map.of(
        PieceType.BISHOP, new ArrayList<>(Arrays.asList(Direction.DIAGONAL)),
        PieceType.ROOK, new ArrayList<>(Arrays.asList(Direction.HORIZONTAL, Direction.VERTICAL)),
        PieceType.QUEEN, new ArrayList<>(Arrays.asList(Direction.HORIZONTAL, Direction.VERTICAL, Direction.DIAGONAL))
    );

    private PieceEval(){}

    /*
     * @param attackingPiece: The piece that is attacking
     * @param defendingPiece: The piece that is defending
     * @return true if the attacking piece is worth capturing, false otherwise
     * 
     */

    public static boolean worthCapturing(Piece attackingPiece, Piece defendingPiece) {
        if (attackingPiece == null || defendingPiece == null) {
            return false;
        }
        
        PieceType attackingType = attackingPiece.getPieceType();
        PieceType defendingType = defendingPiece.getPieceType();
        
        
        // Check if both piece types are valid and in the value map
        if (attackingType == null || defendingType == null || 
            attackingType == PieceType.NONE || defendingType == PieceType.NONE ||
            !MATERIAL_PIECE_VALUE.containsKey(attackingType) || 
            !MATERIAL_PIECE_VALUE.containsKey(defendingType)) {
            return false;
        }
        
        // Now we can safely get the values from the map
        return MATERIAL_PIECE_VALUE.get(attackingType) < MATERIAL_PIECE_VALUE.get(defendingType);
    }

    public static boolean atLeastEqualValue(Piece attackingPiece, Piece defendingPiece) {
        if (attackingPiece == null || defendingPiece == null) {
            return false;
        }

        PieceType attackingType = attackingPiece.getPieceType();
        PieceType defendingType = defendingPiece.getPieceType();
        
        return MATERIAL_PIECE_VALUE.get(attackingType) <= MATERIAL_PIECE_VALUE.get(defendingType);
    }

    public static List<Direction> getPieceDirections(PieceType pieceType) {
        return PIECE_DIRECTIONS.get(pieceType);
    }

    public static int getPieceValue(Piece piece) {
        return MATERIAL_PIECE_VALUE.get(piece.getPieceType());
    }
}
