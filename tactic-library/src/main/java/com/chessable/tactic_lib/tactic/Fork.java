package com.chessable.tactic_lib.tactic;

import com.chessable.instance.TacticInstance;
import com.chessable.tactic_lib.inter.Tactic;
import com.chessable.tactic_lib.utils.BoardState;
import com.chessable.tactic_lib.utils.MoveEval;
import com.chessable.tactic_lib.utils.PieceEval;

import java.util.List;
import java.util.ArrayList;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.PieceType;
import com.github.bhlangonijr.chesslib.move.Move;
import java.util.Optional;

public class Fork implements Tactic{

    private BoardState boardState;
    private static final int MIN_FORK_PIECES = 2;
    

    public Fork(BoardState boardState) {
        this.boardState = boardState;
    }

    @Override
    public void setBoardState(BoardState boardState) {
        this.boardState = boardState;
    }

    private boolean onlyPawns(List<Square> forkablePieces){
        int pawnCount = 0;
        for (Square forkablePiece : forkablePieces){
            if (boardState.getPiece(forkablePiece).getPieceType() == PieceType.PAWN){
                pawnCount++;
            }
        }
        return pawnCount <= forkablePieces.size() - MIN_FORK_PIECES;
    }

    private Optional<TacticInstance> canMoveFork(Move move){
        Square movingSquare = move.getTo();
        if (boardState.isMated()){
            return Optional.empty();
        }
        Piece attackingPiece = boardState.getPiece(movingSquare);
        List<Square> attackableSquares = boardState.piecesAttackedByPiece(movingSquare);
        List<Square> forkablePieces = new ArrayList<>();
        int capturingCount = 0;

        for (Square attackers : boardState.getAttackers(movingSquare)){
            if (PieceEval.atLeastEqualValue(boardState.getPiece(attackers), attackingPiece)){
                return Optional.empty();
            }
        }

        List<Square> attackers = boardState.getAttackers(movingSquare);

        for (Square attacker : attackers){
            if (boardState.getPiece(attacker).getPieceType() == PieceType.PAWN){
                return Optional.empty();
            }
        }

        List<Square> defenders = boardState.getDefenders(movingSquare);

        if (attackableSquares.size() < MIN_FORK_PIECES || attackers.size() - defenders.size() > 0){
            return Optional.empty();
        }

        for (Square attackableSquare : attackableSquares){
            Piece attackingablePiece = boardState.getPiece(attackableSquare);
            if (attackingablePiece.getPieceType() == PieceType.PAWN){
                continue;
            }
            if (PieceEval.worthCapturing(attackingPiece, attackingablePiece) 
            || !boardState.isDefended(attackableSquare)){
                forkablePieces.add(attackableSquare);
            }
            if (PieceEval.worthCapturing(attackingPiece, attackingablePiece)){
                capturingCount++;
            }
        }

        if ((
            forkablePieces.size() >= MIN_FORK_PIECES && 
            onlyPawns(forkablePieces) &&
            (!MoveEval.canMoveDefend(boardState, forkablePieces) || capturingCount >= MIN_FORK_PIECES)
        )){
            return Optional.of(new TacticInstance("Fork", move, boardState.getFen(), List.of(attackingPiece.getPieceType(), boardState.getPiece(forkablePieces.get(0)).getPieceType(), boardState.getPiece(forkablePieces.get(1)).getPieceType())));
        }
        return Optional.empty();
    }


    @Override
    public Optional<TacticInstance> evaluate(Move move) {
        boardState.doMove(move);
        Optional<TacticInstance> tacticInstance = canMoveFork(move);
        boardState.undoMove();

        if (tacticInstance.isPresent()){
            TacticInstance instance = tacticInstance.get();
            instance.setFen(boardState.getFen());
            return tacticInstance;
        }
        return Optional.empty();
    }

    @Override
    public String getName() {
        return "Fork";
    }

    @Override
    public String toString() {
        return "Fork";
    }

    public static void main(String[] args) {
        Board board = new Board();
        board.loadFromFen("r7/pQ2Nrk1/np3b1p/7K/6p1/4P3/P1P2PPP/R6R b - - 4 28");
        Fork fork = new Fork(new BoardState(board));
        System.out.println(fork.evaluate(new Move(Square.D5, Square.B7)));
    }
}