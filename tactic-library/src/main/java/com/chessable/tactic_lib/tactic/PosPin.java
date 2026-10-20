package com.chessable.tactic_lib.tactic;

import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.PieceType;
import com.github.bhlangonijr.chesslib.move.Move;
import java.util.List;
import java.util.Optional;

import com.chessable.instance.TacticInstance;
import com.chessable.tactic_lib.enumerate.Direction;
import com.chessable.tactic_lib.rayservice.AbstractRayGenerator;
import com.chessable.tactic_lib.rayservice.RayPiece;
import com.chessable.tactic_lib.utils.BoardState;
import com.chessable.tactic_lib.utils.MoveEval;
import com.chessable.tactic_lib.utils.PieceEval;
import com.github.bhlangonijr.chesslib.Side;

public class PosPin {

    private BoardState boardState;

    public PosPin(BoardState boardState) {
        this.boardState = boardState;
    }
    
    public void setBoardState(BoardState boardState) {
        this.boardState = boardState;
    }

    private boolean doesPieceMoveInRay(Direction rayDirection, Piece rayPiece){
        List<Direction> pieceDirections = PieceEval.getPieceDirections(rayPiece.getPieceType());
        if (pieceDirections == null || pieceDirections.isEmpty()) {
            return false;
        }
        return pieceDirections.contains(rayDirection);
    }

    private boolean isPinnedHanging(Direction rayDirection, Square pinnedSquare, Square pinningSquare) {
        List<Square> defenders = boardState.getDefenders(pinnedSquare);
        
       
        
        Piece pinnedPiece = boardState.getPiece(pinnedSquare);
        Piece pinningPiece = boardState.getPiece(pinningSquare);
        
        // Handle current side pieces
        if (pinnedPiece.getPieceSide() == boardState.getCurrentSide()) {

            if (!(boardState.getAttackers(pinningSquare).isEmpty() || boardState.getAttackers(pinningSquare).contains(pinnedSquare))){
                return false;
            }

            // If pinning piece is undefended and pinned piece can move in ray, not hanging
            if (boardState.getDefenders(pinningSquare).isEmpty() && 
                doesPieceMoveInRay(rayDirection, pinnedPiece)) {
                return false;
            }
            
            // If pinned piece has defenders and is at least equal value, not hanging
            if (!defenders.isEmpty() && 
                PieceEval.atLeastEqualValue(pinnedPiece, pinningPiece)) {
                return false;
            }
            
            System.out.println("TRUE1");
            return true;
        }

         // Undefended pieces are always hanging
         if (defenders.isEmpty()) {
            System.out.println("no defenders");
            return true;
        }
        
        // Handle opponent side pieces
        return PieceEval.worthCapturing(pinningPiece, pinnedPiece);
    }

    private Optional<TacticInstance> isPieceDefendedByPinHanging(Square square, Square pinningSquare) {
        List<Square> defenders = boardState.getDefenders(square);
        List<Square> attackers = boardState.getAttackers(square);
        
        // Piece must have attackers and be defended by exactly one piece
        if (attackers.isEmpty() || defenders.size() != 1) {
            return Optional.empty();
        }
        
        Square attackerSquare = attackers.get(0);
        Piece attackerPiece = boardState.getPiece(attackerSquare);
        Piece pinnedPiece = boardState.getPiece(square);
        Piece pinningPiece = boardState.getPiece(pinningSquare);
        
        if (attackers.size() == 1) {
            Square defenderSquare = defenders.get(0);
            return !(attackers.contains(pinningSquare) || MoveEval.doesMoveHangPiece(boardState.copy(), new Move(square, defenderSquare), pinningSquare)) 
            ? Optional.of(new TacticInstance("PosPin", new Move(attackerSquare, square), boardState.getFen(), List.of(pinnedPiece.getPieceType(), pinningPiece.getPieceType(), attackerPiece.getPieceType()))) : Optional.empty();
        }
        else {
            return Optional.of(new TacticInstance("PosPin", new Move(attackerSquare, square), boardState.getFen(), List.of(pinnedPiece.getPieceType(), pinningPiece.getPieceType(), attackerPiece.getPieceType())));
        }
        
    }

    public Optional<TacticInstance> pinChecks(Piece kingPiece, Direction rayDirection, Square pinnedSquare, Square attackingSquare){
        Piece pieceBetween = boardState.getPiece(pinnedSquare);
        Piece piece = boardState.getPiece(attackingSquare);

        if (kingPiece.getPieceSide() == boardState.getCurrentSide()){
            if (doesPieceMoveInRay(rayDirection, pieceBetween)){
                if (boardState.isHanging(attackingSquare) && PieceEval.worthCapturing(pieceBetween, piece)){
                    System.out.println("pinned hanging");
                    return Optional.empty();
                }
            }
        }

        if (isPinnedHanging(rayDirection, pinnedSquare, attackingSquare)) {
            System.out.println("pinned hanging");
            System.out.println(pinnedSquare.toString() + " " + attackingSquare.toString());
            TacticInstance tacticInstance = new TacticInstance("PosPin", new Move(attackingSquare, pinnedSquare), boardState.getFen(), List.of(pieceBetween.getPieceType(), piece.getPieceType(), kingPiece.getPieceType()));
            return Optional.of(tacticInstance);
        }

        if (kingPiece.getPieceSide() == boardState.getOpponentSide()){

            List<Square> pinnedDefenders = boardState.piecesDefendedByPiece(pinnedSquare);

            for (Square pinnedDefender : pinnedDefenders) {
                Piece pinnedDefenderPiece = boardState.getPiece(pinnedDefender);
                if (pinnedDefenderPiece.getPieceType() == PieceType.PAWN){
                    continue;
                }
                Optional<TacticInstance> tacticInstance = isPieceDefendedByPinHanging(pinnedDefender, attackingSquare);
                if (tacticInstance.isPresent()) {
                    return tacticInstance;
                }
            }
        }
        System.out.println("no pin");

        return Optional.empty();
    }


    public Optional<TacticInstance> pin(){

        Square[] kingSquares = {boardState.getKing(boardState.getOpponentSide()), boardState.getKing(boardState.getCurrentSide())};

        for (Square kingSquare : kingSquares) {
            Piece kingPiece = boardState.getPiece(kingSquare);
            Side kingSide = kingPiece.getPieceSide();
            List<AbstractRayGenerator> directional = RayPiece.getAllDirectionRays(kingSquare);
            for (AbstractRayGenerator direction : directional) {
                List<List<Square>> totalRay = direction.getTotalRay();
                for (List<Square> ray : totalRay) {
                    Square pinnedSquare = null;
                    Square attackerSquare = null;
                    for (Square square : ray) {
                        Piece piece = boardState.getPiece(square);
                        if (!boardState.isPiece(piece)){
                            continue;
                        }

                        if (pinnedSquare == null){
                            if (piece.getPieceSide() == kingSide && piece.getPieceType() != PieceType.PAWN){
                                pinnedSquare = square;
                            }
                            else {
                                break;
                            }
                        }
                        else {
                            
                            if (piece.getPieceSide() != kingSide && doesPieceMoveInRay(direction.getDirection(), piece) && attackerSquare == null){
                                attackerSquare = square;
                                return pinChecks(kingPiece, direction.getDirection(), pinnedSquare, attackerSquare);
                            }
                            else {
                                break;
                            }
                        }
                    }

                    System.out.println();
                }
            }
        }

        return Optional.empty();
    }
    
}
