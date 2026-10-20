package com.chessable.tactic_lib.tactic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.chessable.instance.TacticInstance;
import com.chessable.tactic_lib.enumerate.Direction;
import com.chessable.tactic_lib.inter.RayBehavior;
import com.chessable.tactic_lib.inter.Tactic;
import com.chessable.tactic_lib.rayservice.RayFactory;
import com.chessable.tactic_lib.utils.BoardState;
import com.chessable.tactic_lib.utils.MoveEval;
import com.chessable.tactic_lib.utils.MoveFilterUtils;
import com.chessable.tactic_lib.utils.PieceEval;
import com.chessable.tactic_lib.utils.RankFileUtils;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.PieceType;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

public class Pin implements Tactic{

    private BoardState boardState;
    private static final int NUM_PIN_IN_RAY = 1;
    private static final int NUM_DEFENDERS = 1;

    public Pin(BoardState boardState) {
        this.boardState = boardState;
    }

    private boolean doesPieceMoveInRay(Direction rayDirection, Piece rayPiece){
        List<Direction> pieceDirections = PieceEval.getPieceDirections(rayPiece.getPieceType());
        if (pieceDirections == null || pieceDirections.isEmpty()) {
            return false;
        }
        return pieceDirections.contains(rayDirection);
    }

    @Override
    public void setBoardState(BoardState boardState) {
        this.boardState = boardState;
    }

    private boolean moveIsInRay(Move move, Square kingSquare){
        Square fromSquare = move.getFrom();
        Square toSquare = move.getTo();

        boolean isSameRank = RankFileUtils.isSameRank(fromSquare, kingSquare) && RankFileUtils.isSameRank(toSquare, kingSquare);
        boolean isSameFile = RankFileUtils.isSameFile(fromSquare, kingSquare) && RankFileUtils.isSameFile(toSquare, kingSquare);
        boolean isSameDiagonal = RankFileUtils.isSameDiagonal(fromSquare, kingSquare) && RankFileUtils.isSameDiagonal(toSquare, kingSquare);

        return isSameRank || isSameFile || isSameDiagonal;
    }


    public Optional<TacticInstance> canMovePin(Move move){

        Square square = move.getTo();
        Piece attackerPiece = boardState.getPiece(square);
        List<Square> attackers = boardState.getAttackers(square);
        List<Square> defenders = boardState.getDefenders(square);
        Square kingSquare = boardState.getKing(boardState.getCurrentSide());
        Piece kingPiece = boardState.getPiece(kingSquare);

        RayBehavior rayBehavior = RayFactory.getRayBehavior(square, kingSquare);
        if (rayBehavior == null 
        || (!attackers.isEmpty() && defenders.isEmpty()) 
        || boardState.attackerContainsPawn(square)
        || !doesPieceMoveInRay(rayBehavior.getDirection(), attackerPiece)
        || moveIsInRay(move, kingSquare)){
            return Optional.empty();
        }
        List<Square> ray = rayBehavior.getRayBetween();


        if (ray.isEmpty()){
            return Optional.empty();
        }

        List<Square> pinnedSquares = new ArrayList<>();

        for (Square raySquare : ray){
            Piece piece = boardState.getPiece(raySquare);

            if (!boardState.isPiece(piece)){
                continue;
            }

            if (piece.getPieceSide() == boardState.getOpponentSide() || piece.getPieceType() == PieceType.PAWN){
                return Optional.empty();
            }

            pinnedSquares.add(raySquare);
        }

        if (pinnedSquares.size() != NUM_PIN_IN_RAY){
            return Optional.empty();
        }

        Square pinnedSquare = pinnedSquares.get(0);
        Piece pinnedPiece = boardState.getPiece(pinnedSquare);
        
        List<Square> pinnedDefenders = boardState.getDefenders(pinnedSquare);

        if (MoveEval.canMoveDefend(boardState, pinnedSquare) && !PieceEval.worthCapturing(attackerPiece, pinnedPiece)){
            //System.out.println("pinned defended");
            return Optional.empty();
        }

        if (pinnedDefenders.isEmpty() || (PieceEval.worthCapturing(attackerPiece, pinnedPiece) && (attackers.isEmpty() || !defenders.isEmpty()))){
            TacticInstance tacticInstance = new TacticInstance("Pin", move, boardState.getFen(), List.of(attackerPiece.getPieceType(), pinnedPiece.getPieceType(), kingPiece.getPieceType()));
            return Optional.of(tacticInstance);
        }
        

        return Optional.empty();
    }

    public Optional<TacticInstance> captureFromPin(Move move){
        Piece capturedPiece = boardState.getPiece(move.getTo());
        if (!boardState.isPiece(capturedPiece) || capturedPiece.getPieceType() == PieceType.PAWN){
            return Optional.empty();
        }
        List<Square> defenders = boardState.getDefenders(move.getTo());
        if (defenders.size() == NUM_DEFENDERS){
            Square defenderSquare = defenders.get(0);
            if (boardState.getPiece(defenderSquare).getPieceType() == PieceType.KING){
                return Optional.empty();
            }
            boardState.doMove(move);
            if (boardState.isInCheck()){
                boardState.undoMove();
                return Optional.empty();
            }
            List<Move> legalMoves = boardState.getLegalMoves();
            List<Move> legalMovesDefender = MoveFilterUtils.filterCaptures(legalMoves, move.getTo());
            if (legalMovesDefender.isEmpty()){
                boardState.undoMove();
                return Optional.of(new TacticInstance("Pin", move, boardState.getFen(), List.of(capturedPiece.getPieceType())));
            }
            boardState.undoMove();
        }
        return Optional.empty();

    }
        

    @Override
    public Optional<TacticInstance> evaluate(Move move) {
        boardState.doMove(move);
        Optional<TacticInstance> tacticInstance = canMovePin(move);
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
        return "Pin";
    }

    @Override
    public String toString() {
        return "Pin";
    }

    public static void main(String[] args) {
        Board board = new Board();
        board.loadFromFen("8/6R1/p7/P7/1P3kp1/5p2/2r3pK/8 b - - 3 45");
        BoardState boardState = new BoardState(board);
        Pin pin = new Pin(boardState);
        System.out.println(pin.canMovePin(new Move(Square.H1, Square.F1)));
    }

}
