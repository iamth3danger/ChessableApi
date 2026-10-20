package com.chessable.tactic_lib.utils;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.PieceType;
import com.github.bhlangonijr.chesslib.Rank;
import com.github.bhlangonijr.chesslib.File;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.Bitboard;
import com.github.bhlangonijr.chesslib.move.Move;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
public class TacticUtils {

    private Board board;
    private Side defendingSide;
    private Side attackingSide;

    private static final Map<PieceType, Integer> MATERIAL_PIECE_VALUE =
    Map.of(
        PieceType.PAWN, 1,
        PieceType.KNIGHT, 3,
        PieceType.BISHOP, 3,
        PieceType.ROOK, 5,
        PieceType.QUEEN, 9,
        PieceType.KING, 100
    );

    

    public TacticUtils(Board board) {
        this.board = board;
        this.defendingSide = board.getSideToMove();
        this.attackingSide = defendingSide.flip();
    }
    
    public List<Square> getAttackers(Square square) {
        Piece piece = board.getPiece(square);
        long attackers = board.squareAttackedBy(square, piece.getPieceSide().flip());
        return Bitboard.bbToSquareList(attackers);
    }


    public List<Square> getDefenders(Square square) {
        Piece piece = board.getPiece(square);
        long defenders = board.squareAttackedBy(square, piece.getPieceSide());
        return Bitboard.bbToSquareList(defenders);
    }



    /**
    @param attackingPiece
    @param defendingPiece
    @return true if the attacking piece is worth capturing
    */

    public boolean worthCapturing(Piece attackingPiece, Piece defendingPiece) {
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

    public boolean isDefended(Square square) {
        return !(getDefenders(square).isEmpty());
    }

    public boolean isHanging(Square square){
        return getAttackers(square).size() - getDefenders(square).size() > 0;
    }
    
    public Square getKing(Side side) {
        return board.getKingSquare(side);
    }

    public List<Square> getPieces(Side side) {
        List<PieceType> pieceTypes = List.of(PieceType.PAWN, PieceType.KNIGHT, PieceType.BISHOP, PieceType.ROOK, PieceType.QUEEN, PieceType.KING);
        List<Square> pieces = new ArrayList<>();
        for (PieceType pieceType : pieceTypes) {
            pieces.addAll(board.getPieceLocation(Piece.make(side, pieceType)));
        }
        return pieces;
    }

    public List<Square> getPieces(Side side, List<PieceType> pieceTypes) {
        List<Square> pieces = new ArrayList<>();
        for (PieceType pieceType : pieceTypes) {
            pieces.addAll(board.getPieceLocation(Piece.make(side, pieceType)));
        }
        return pieces;
    }

    public Map<Square, Piece> getPieceMap(Side side) {
        Map<Square, Piece> pieceMap = new HashMap<>();
        for (Square square : getPieces(side)) {
            pieceMap.put(square, getPiece(square));
        }
        return pieceMap;
    }

    public Map<Square, Piece> getPieceMap(Side side, List<PieceType> pieceTypes) {
        Map<Square, Piece> pieceMap = new HashMap<>();
        for (Square square : getPieces(side, pieceTypes)) {
            pieceMap.put(square, getPiece(square));
        }
        return pieceMap;
    }

    public Map<Square, List<Square>> getAttackingSquares(Side side) {
        Map<Square, List<Square>> attackingSquares = new HashMap<>();
        for (Square square : getPieces(side)) {
            List<Square> attackers = attacksByPieceType(square, side, getPiece(square).getPieceType());
            if (!attackers.isEmpty()){
                attackingSquares.put(square, attackers);
            }
        }
        return attackingSquares;
    }

    public List<Square> attacksByPieceType(Square square, Side side, PieceType type) {
        long result = 0L;
        long occ = board.getBitboard();
        switch (type) {
            case PAWN:
                result = Bitboard.getPawnAttacks(side.flip(), square);
                break;
            case KNIGHT:
                result = Bitboard.getKnightAttacks(square, occ);
                break;
            case BISHOP:
                result = Bitboard.getBishopAttacks(occ, square);
                break;
            case ROOK:
                result = Bitboard.getRookAttacks(occ, square);
                break;
            case QUEEN:
                result = Bitboard.getQueenAttacks(occ, square);
                break;
            case KING:
                result = Bitboard.getKingAttacks(square, occ);
                break;
            default:
                break;
        }
        return Bitboard.bbToSquareList(result);
    }

    public boolean canMoveCheck(Square movingSquare, Square checkingSquare) {
        for (Move move : board.legalMoves()) {
            board.doMove(move);
            if (getAttackers(getKing(board.getSideToMove())).contains(checkingSquare)){
                board.undoMove();
                return true;
            }
            board.undoMove();
        }
        return false;
    }

    public boolean canMoveDoubleAttack(Square movingSquare, Square attackedSquare, Square attackingSquare) {
        System.out.println("canMoveDoubleAttack " + movingSquare + " " + attackedSquare);
        for (Move move : moveSubList(movingSquare)) {
            board.doMove(move);
            Piece movingPiece = getPiece(move.getTo());
            if (move.toString().equals("f1g3")){
                System.out.println("movingPiece " + movingPiece.toString());
                System.out.println("attackedSquare " + attackedSquare);
                System.out.println("attackingSquare " + attackingSquare);
            }
            System.out.println(move.getFrom().toString());
            if (!getAttackers(attackedSquare).isEmpty() && !attacksByPieceType(movingSquare, movingPiece.getPieceSide().flip(), movingPiece.getPieceType()).isEmpty()
            && (!isDefended(attackedSquare) || worthCapturing(getPiece(attackingSquare), getPiece(attackedSquare)))){
                board.undoMove();
                return true;
            }
            board.undoMove();
        }
        return false;
    }
    

    public boolean canMoveDefend(Square attackedSquare, Square movingSquare) {
        
        for (Move move : board.legalMoves()) {
            if (move.getFrom() != movingSquare){
                continue;
            }
            board.doMove(move);
            int attackersOnAttackedSquare = getAttackers(attackedSquare).size();
            int attackersOnMovingSquare = getAttackers(move.getTo()).size();
            if (attackersOnAttackedSquare + attackersOnMovingSquare == 0){
                board.undoMove();
                return true;
            }
            board.undoMove();
        }
        return false;
    }

    public boolean canMoveDefend(List<Square> squares){
        for (Move move : moveSubList(squares)){
            board.doMove(move);
            boolean forkDefended = true;
            for (Square square : squares){
                Square currentSquare = square;
                if (move.getFrom() == square){
                    currentSquare = move.getTo();
                }
                Piece capturablePiece = board.getPiece(currentSquare);
                //System.out.println(capturablePiece.toString() + " " + capturablePiece.getPieceSide());
                if (!isDefended(currentSquare)){
                    forkDefended = false;
                }
            }
            board.undoMove();
            if (forkDefended){
                return true;
            }
        }
        return false;
    }

    private List<Move> moveSubList(List<Square> squares){
        return board.legalMoves().stream()
                .filter(move -> squares.contains(move.getFrom()))
                .collect(java.util.stream.Collectors.toList());
    }

    private List<Move> moveSubList(Square square){
        return board.legalMoves().stream()
                .filter(move -> move.getFrom() == square)
                .collect(java.util.stream.Collectors.toList());
    }

    public Board getBoard() {
        return board;
    }

    public Side getDefendingSide() {
        return defendingSide;
    }

    public Side getAttackingSide() {
        return attackingSide;
    }

    public Piece getPiece(Square square) {
        return board.getPiece(square);
    }

    public boolean isMated() {
        return board.isMated();
    }

    private TacticUtils copy(){
        return new TacticUtils(board.clone());
    }
}
