package com.chessable.tactic_lib.utils;

import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import java.util.List;
import java.util.function.Predicate;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.PieceType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import com.github.bhlangonijr.chesslib.Board;
import com.chessable.instance.TacticInstance;
import com.chessable.tactic_lib.inter.RayBehavior;
import com.chessable.tactic_lib.tactic.Discovered;
import com.chessable.tactic_lib.tactic.Pin;

public class MoveEval {
    private BoardState boardState;
    private final static ArrayList<String> KING_CASTLE_MOVES = new ArrayList<>(Arrays.asList("e1g1", "e1c1", "e8c8", "e8g8"));

    private MoveEval(){}
    
    /**
     * Generic method to evaluate if any move satisfies a given condition
     * @param moves List of moves to evaluate
     * @param condition Predicate that evaluates a board position after a move
     * @return true if any move satisfies the condition
     */
    private static boolean canMoveGeneric(BoardState boardState, List<Move> moves, Predicate<Move> condition) {
        for (Move move : moves) {
            boardState.doMove(move);
            boolean result = condition.test(move);
            boardState.undoMove();
            if (result) {
                //System.out.println("Move: " + move.toString() + " satisfies the condition");
                return true;
            }
        }
        return false;
    }

    private static Optional<Move> returnMoveGeneric(BoardState boardState, List<Move> moves, Predicate<Move> condition) {
        for (Move move : moves) {
            boardState.doMove(move);
            boolean result = condition.test(move);
            boardState.undoMove();
            if (result) {
                //System.out.println("Move: " + move.toString() + " satisfies the condition");
                return Optional.of(move);
            }
        }
        return Optional.empty();
    }

    public static Optional<TacticInstance> canMovePattern(BoardState boardState, List<Move> moves, Function<Move, Optional<TacticInstance>> condition){
        for (Move move : moves) {
            boardState.doMove(move);
            Optional<TacticInstance> result = condition.apply(move);
            boardState.undoMove();
            if (result.isPresent()) {
                result.get().setMove(move);
                return result;
            }
        }
        return Optional.empty();
    }

    public static boolean canMoveCheck(BoardState boardState, Square movingSquare, Square checkingSquare) {
        return canMoveGeneric(boardState, boardState.getLegalMoves(), 
            move -> boardState.isPieceChecking(checkingSquare));
    }

    public static boolean canMoveDoubleAttack(BoardState boardState, Square movingSquare, Square attackedSquare, Square attackingSquare) {
        return canMoveGeneric(boardState, MoveFilterUtils.filterMoves(boardState.getLegalMoves(), movingSquare), 
            move -> boardState.isDoubleAttack(attackingSquare, move.getTo(), attackedSquare));
    }

    public static Optional<Move> canMoveCreateDiscovered(BoardState boardState, Square attackingSquare, Square movingSquare, Square targetSquare,  RayBehavior rayBehavior) {

        return returnMoveGeneric(boardState,
            MoveFilterUtils.filterMovesDiscovered(boardState.getLegalMoves(), movingSquare, boardState.getPiece(movingSquare).getPieceType(), rayBehavior), 
            move -> boardState.isDoubleAttack(attackingSquare, move.getTo(), targetSquare));
    }

   


    
    public static boolean canMoveDefend(BoardState boardState, Square attackedSquare, Square movingSquare) {
        return canMoveGeneric(boardState, MoveFilterUtils.filterMoves(boardState.getLegalMoves(), movingSquare), 
            move -> (!boardState.getDefenders(attackedSquare).isEmpty() &&   
                    boardState.getAttackers(move.getTo()).isEmpty()));
    }

    public static boolean canMoveDefend(BoardState boardState, Square attackedSquare){
        return canMoveGeneric(boardState, MoveFilterUtils.removeMoves(boardState.getLegalMoves(), attackedSquare), 
            move -> (!boardState.getDefenders(attackedSquare).isEmpty()));
    }

    public static boolean canMoveDefend(BoardState boardState, List<Square> squares) {
        return canMoveGeneric(boardState, MoveFilterUtils.filterCastles(boardState.getLegalMoves()), move -> {
            //System.out.println("Move: " + move.toString());
            boolean forkDefended = true;
            for (Square square : squares) {
                Square currentSquare = square;
                if (move.getFrom() == square) {
                    currentSquare = move.getTo();
                }
                if (!(boardState.isDefended(currentSquare) || boardState.getAttackers(currentSquare).isEmpty())) {
                    forkDefended = false;
                    break;
                }
                
            }
            return forkDefended;
        });
    }

    public static boolean canPawnMove(Square moveFrom, Square moveTo, RayBehavior rayBehavior) {
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

    public static boolean doesMoveHangPiece(BoardState boardState, Move move, Square hangingSquare) {
        boardState.doMove(move);
        boolean isHanging = boardState.isHanging(hangingSquare);
        boardState.undoMove();
        return isHanging;
    }

}