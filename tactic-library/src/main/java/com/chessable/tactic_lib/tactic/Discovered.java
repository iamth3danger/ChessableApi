package com.chessable.tactic_lib.tactic;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.Piece;
import com.chessable.instance.TacticInstance;
import com.chessable.tactic_lib.enumerate.Direction;
import com.chessable.tactic_lib.inter.RayBehavior;
import com.chessable.tactic_lib.inter.Tactic;
import com.chessable.tactic_lib.rayservice.RayFactory;
import com.chessable.tactic_lib.utils.BoardState;
import com.chessable.tactic_lib.utils.PieceEval;

import java.util.List;

import com.github.bhlangonijr.chesslib.PieceType;
import com.github.bhlangonijr.chesslib.move.Move;
import java.util.Optional;


public class Discovered implements Tactic{
    

    private BoardState boardState;

    public Discovered(BoardState boardState) {
        this.boardState = boardState;
    }

    @Override
    public void setBoardState(BoardState boardState) {
        this.boardState = boardState;
    }

    private boolean doesPieceMoveInRay(RayBehavior rayBehavior, Piece rayPiece){
        List<Direction> pieceDirections = PieceEval.getPieceDirections(rayPiece.getPieceType());
        if (pieceDirections == null || pieceDirections.isEmpty()) {
            return false;
        }
        return pieceDirections.contains(rayBehavior.getDirection());
    }

    public static Optional<TacticInstance> canMoveDiscover(BoardState boardState, Move move, Square attackingSquare, Square targetSquare){
        boardState.doMove(move);
        boolean isDoubleAttack = boardState.isDoubleAttack(attackingSquare, move.getTo(), targetSquare);
        boardState.undoMove();
        return isDoubleAttack ? Optional.of(new TacticInstance("Discovered", move, boardState.getFen(), List.of(boardState.getPiece(attackingSquare).getPieceType(), boardState.getPiece(move.getFrom()).getPieceType(), boardState.getPiece(targetSquare).getPieceType()))) : Optional.empty();
    }
    
    public Optional<TacticInstance> discoveredPattern(Move move, Square defendingSquare, Square targetSquare, RayBehavior rayBehavior) {
        Piece targetPiece = boardState.getPiece(targetSquare);
        Piece attackerPiece = boardState.getPiece(defendingSquare);
        Piece movingPiece = boardState.getPiece(move.getFrom());

        if (movingPiece.getPieceType() == PieceType.QUEEN){
            return Optional.empty();
        }

        if (!(doesPieceMoveInRay(rayBehavior, attackerPiece) && (attackerPiece.getPieceSide() != targetPiece.getPieceSide()))){
            return Optional.empty();
        }

        return canMoveDiscover(boardState.copy(), move, defendingSquare, targetSquare);
    }

    public Optional<TacticInstance> targetInRay(List<Square> defenders, Move move){
        Square movingSquare = move.getFrom();
        Piece movingPiece = boardState.getPiece(movingSquare);
        for (Square defendingSquare : defenders) {
            Piece attackerPiece = boardState.getPiece(defendingSquare);
            if (movingPiece.getPieceType() == attackerPiece.getPieceType()) {
                continue;
            }
            
            RayBehavior rayBehavior = RayFactory.getRayBehavior(defendingSquare, movingSquare);
            if (rayBehavior == null) {
                continue;
            }
            List<Square> ray = rayBehavior.getRay();
            
            for (Square targetSquare : ray) {
                Piece rayPiece = boardState.getPiece(targetSquare);

                if (!boardState.isPiece(rayPiece)) {
                    continue;
                }

                if (rayPiece.getPieceType() == PieceType.PAWN){
                    break;
                }

                Optional<TacticInstance> tacticInstance = discoveredPattern(move, defendingSquare, targetSquare, rayBehavior);
                if (tacticInstance.isPresent()) {
                    return tacticInstance;
                }
                
                break;
            }
        }
        return Optional.empty();
    }


    @Override
    public Optional<TacticInstance> evaluate(Move move){
        
        return targetInRay(boardState.getDefenders(move.getFrom()), move);
    }

    @Override
    public String getName() {
        return "Discovered";
    }

    @Override
    public String toString() {
        return "Discovered";
    }

    public static void main(String[] args) {
        Board board = new Board();
        board.loadFromFen("r3k2B/ppp1np1p/8/4Q3/8/8/PPPKBqPP/R5bR b q - 0 17");
        BoardState boardState = new BoardState(board);
        Move move = new Move(Square.valueOf("E2"), Square.valueOf("E4"));
        Discovered discovered = new Discovered(boardState);
        Optional<TacticInstance> tacticInstance = discovered.evaluate(move);
        System.out.println(tacticInstance);
    }
    
}
