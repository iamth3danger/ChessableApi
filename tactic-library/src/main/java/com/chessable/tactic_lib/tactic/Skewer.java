package com.chessable.tactic_lib.tactic;

import java.util.List;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.PieceType;
import com.github.bhlangonijr.chesslib.Square;
import com.chessable.instance.TacticInstance;
import com.chessable.tactic_lib.enumerate.Direction;
import com.chessable.tactic_lib.inter.RayBehavior;
import com.chessable.tactic_lib.inter.Tactic;
import com.chessable.tactic_lib.rayservice.RayFactory;
import com.chessable.tactic_lib.utils.BoardState;
import com.chessable.tactic_lib.utils.MoveEval;
import com.chessable.tactic_lib.utils.PieceEval;

import java.util.Optional;
import com.github.bhlangonijr.chesslib.move.Move;


public class Skewer implements Tactic{

    private BoardState boardState;
    private MoveEval moveEval;

    public Skewer(BoardState boardState) {
        this.boardState = boardState;
    }

    @Override
    public void setBoardState(BoardState boardState) {
        this.boardState = boardState;
    }


    private boolean shouldCapture(Square squareInfront, Square attacker, Square importantSquare) {
        Piece pieceInfront = boardState.getPiece(squareInfront);
        Piece attackerPiece = boardState.getPiece(attacker);
        Piece importantPiece = boardState.getPiece(importantSquare);

        if (pieceInfront.getPieceType() == PieceType.NONE || pieceInfront.getPieceType() == null 
        || attackerPiece.getPieceType() == PieceType.NONE || attackerPiece.getPieceType() == null 
        || importantPiece.getPieceType() == PieceType.NONE || importantPiece.getPieceType() == null) {
            return false;
        }

        if (PieceEval.worthCapturing(attackerPiece, pieceInfront)) {
            return true;
        }
        
        if (!PieceEval.worthCapturing(attackerPiece, importantPiece) && boardState.isDefended(importantSquare)) {
            return false;
        }

        List<Square> defenders = boardState.getDefenders(squareInfront);
        boolean nonSkeweredDefender = false;
        for (Square defender : defenders) {
            Piece defenderPiece = boardState.getPiece(defender);
            if (defenderPiece.getPieceType() != importantPiece.getPieceType()) {
                nonSkeweredDefender = true;
            }
        }


        return !(nonSkeweredDefender || MoveEval.canMoveDefend(boardState.copy(), squareInfront, importantSquare));
    }


    private boolean doesPieceMoveInRay(RayBehavior rayBehavior, Piece rayPiece){
        List<Direction> pieceDirections = PieceEval.getPieceDirections(rayPiece.getPieceType());
        if (pieceDirections == null || pieceDirections.isEmpty()) {
            return false;
        }
        return pieceDirections.contains(rayBehavior.getDirection());
    }

    public boolean isPinned(Square square, Square kingSquare) {
        RayBehavior rayBehavior = RayFactory.getRayBehavior(kingSquare, square);
        Piece kingPiece = boardState.getPiece(kingSquare);
        if (rayBehavior == null) {
            return false;
        }
        List<Square> ray = rayBehavior.getRay();
        for (Square raySquare : ray) {
            Piece pieceInRay = boardState.getPiece(raySquare);
            if (pieceInRay.getPieceType() != PieceType.NONE && pieceInRay.getPieceType() != null 
            &&  pieceInRay.getPieceSide() != kingPiece.getPieceSide() 
            && doesPieceMoveInRay(rayBehavior, pieceInRay)) {
                return true;
            }
        }

        return false;
    }


    public Optional<TacticInstance> matchesSkewerPattern(Move move, Square importantSquare) {
        Square attackingSquare = move.getTo();
        Piece importantPiece = boardState.getPiece(importantSquare);
        List<Square> importantAttackers = boardState.getAttackers(importantSquare);
        List<Square> attackers = boardState.getAttackers(attackingSquare);
        List<Square> defenders = boardState.getDefenders(attackingSquare);
        Piece attackerPiece = boardState.getPiece(attackingSquare);



        if (
        boardState.isMated() 
        || importantPiece.getPieceType() == attackerPiece.getPieceType() 
        || importantAttackers.isEmpty()
        || attackerPiece.getPieceType() == PieceType.PAWN
        || !importantAttackers.contains(attackingSquare)
        || (attackers.contains(importantSquare) && defenders.isEmpty())) {
            return Optional.empty();
        }

        

        if (defenders.isEmpty() && !attackers.isEmpty()
        && !(attackers.size() == 1 && importantPiece.getPieceType() == PieceType.KING && isPinned(attackers.get(0), importantSquare))) {
            return Optional.empty();
        }

        for (Square attacker : attackers) {
            Piece attackerOfPiece = boardState.getPiece(attacker);
            if (attackerOfPiece.getPieceType() == PieceType.PAWN) {
                return Optional.empty();
            }
            if (PieceEval.worthCapturing(attackerOfPiece, attackerPiece) && !isPinned(attacker, importantSquare)){
                return Optional.empty();
            }
        }

        
        RayBehavior rayBehavior = RayFactory.getRayBehavior(attackingSquare, importantSquare);
        if (rayBehavior == null) {
            return Optional.empty();
        }
        List<Square> ray = rayBehavior.getRay();
        
        for (Square raySquare : ray) {
            Piece pieceInRay = boardState.getPiece(raySquare);
            if (pieceInRay.getPieceType() != PieceType.NONE && pieceInRay.getPieceType() != null ) {
                
                boolean correctPieceType = (pieceInRay.getPieceType() != PieceType.PAWN 
                && pieceInRay.getPieceType() != PieceType.KING) 
                && attackerPiece.getPieceSide() != pieceInRay.getPieceSide();
                
                if (correctPieceType && shouldCapture(raySquare, attackingSquare, importantSquare)) {
                    return Optional.of(new TacticInstance("Skewer", move, boardState.getFen(), List.of(attackerPiece.getPieceType(), importantPiece.getPieceType(), pieceInRay.getPieceType())));
                }
                else {
                    break;
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<TacticInstance> evaluate(Move move){
        if (boardState.isMated()){
            return Optional.empty();
        }

        boardState.doMove(move);
        List<Square> skewerablePieces = boardState.getPieces(boardState.getCurrentSide(), List.of(PieceType.KING, PieceType.QUEEN, PieceType.ROOK));


        for (Square piece : skewerablePieces) {
            Optional<TacticInstance> tacticInstance = matchesSkewerPattern(move, piece);
            if (tacticInstance.isPresent()) {
                boardState.undoMove();
                TacticInstance instance = tacticInstance.get();
                instance.setFen(boardState.getFen());
                return tacticInstance;
            }
        }
        boardState.undoMove();
        return Optional.empty();
    
    }

    @Override
    public String getName() {
        return "Skewer";
    }

    @Override
    public String toString() {
        return "Skewer";
    }

    public static void main(String[] args) {
        Board board = new Board();
        board.loadFromFen("3k2r1/Np1q3p/1p2p3/2bpQr2/P1p2P2/6P1/6KP/R4R2 w - - 3 33");
        BoardState boardState = new BoardState(board);
        Skewer skewer = new Skewer(boardState);
        Optional<TacticInstance> tacticInstance = skewer.evaluate(new Move(Square.E5, Square.B8));
        if (tacticInstance.isPresent()) {
        } else {
        }
    }
}

