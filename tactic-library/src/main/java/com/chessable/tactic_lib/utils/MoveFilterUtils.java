package com.chessable.tactic_lib.utils;

import java.util.List;

import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.Square;
import java.util.stream.Collectors;

import com.chessable.tactic_lib.enumerate.Direction;
import com.chessable.tactic_lib.inter.RayBehavior;
import com.github.bhlangonijr.chesslib.PieceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

public class MoveFilterUtils {
    
    private static final ArrayList<String> KING_CASTLE_MOVES = new ArrayList<>(Arrays.asList("e1g1", "e1c1", "e8c8", "e8g8"));

    public static List<Move> filterMovesGeneral(List<Move> moves, Predicate<Move> condition) {
        return moves.stream()
            .filter(condition)
            .collect(Collectors.toList());
    }

    
    public static List<Move> filterMoves(List<Move> moves, Square square) {
        return filterMovesGeneral(moves, move -> move.getFrom().equals(square));
    }

    public static List<Move> filterCaptures(List<Move> moves, Square square) {
        return filterMovesGeneral(moves, move -> move.getTo().equals(square));
    }

    public static List<Move> removeMoves(List<Move> moves, Square square) {
        return filterMovesGeneral(moves, move -> !move.getFrom().equals(square));
    }

    public static List<Move> filterMovesDiscovered(List<Move> moves, Square square, PieceType pieceType, RayBehavior rayBehavior) {

        switch (pieceType){
            case PAWN:
                return filterMovesGeneral(moves, move -> move.getFrom().equals(square) && canPawnMove(move.getFrom(), move.getTo(), rayBehavior));
            case KING:
                return filterMovesGeneral(moves, move -> move.getFrom().equals(square) && !KING_CASTLE_MOVES.contains(move.toString()));
            default:
                return filterMovesGeneral(moves, move -> move.getFrom().equals(square) && isMoveInRay(move.getFrom(), move.getTo(), rayBehavior));
        }
    }

    public static List<Move> filterCastles(List<Move> moves){
        return filterMovesGeneral(moves, move -> !KING_CASTLE_MOVES.contains(move.toString()));
    }


    private static boolean canPawnMove(Square moveFrom, Square moveTo, RayBehavior rayBehavior) {
        switch (rayBehavior.getDirection()) {
            case HORIZONTAL:
                return RankFileUtils.diffRank(moveFrom, moveTo);
            case VERTICAL:
                return RankFileUtils.diffFile(moveFrom, moveTo);
            case DIAGONAL:
                return true;
            default:
                return false;
        }
    }

    private static boolean isMoveInRay(Square moveFrom, Square moveTo, RayBehavior rayBehavior) {
        switch (rayBehavior.getDirection()) {
            case HORIZONTAL:
                return RankFileUtils.diffRank(moveFrom, moveTo);
            case VERTICAL:
                return RankFileUtils.diffFile(moveFrom, moveTo);
            case DIAGONAL:
                return true;
            default:
                return false;
        }
    }
        
}


