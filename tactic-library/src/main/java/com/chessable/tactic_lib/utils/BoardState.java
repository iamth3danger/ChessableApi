package com.chessable.tactic_lib.utils;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.PieceType;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.Bitboard;
import com.github.bhlangonijr.chesslib.move.Move;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

enum Direction {
    VERTICAL,
    HORIZONTAL,
    DIAGONAL,
}

public class BoardState {
    private Board board;
    private Side currentSide;
    private Side opponentSide;

    public BoardState(Board board) {
        this.board = board;
        this.currentSide = board.getSideToMove();
        this.opponentSide = currentSide.flip();
    }

    // private BoardState(BoardState boardState){
    //     this.board = boardState.getBoard();
    //     this.defendingSide = boardState.getDefendingSide();
    //     this.attackingSide = boardState.getAttackingSide();
    // }

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

    public boolean isDefended(Square square) {
        return !(getDefenders(square).isEmpty());
    }

    public boolean isHanging(Square square){

        if (getPiece(square).getPieceType() == PieceType.QUEEN){
            return !getAttackers(square).isEmpty();
        }
        else{
            return getAttackers(square).size() - getDefenders(square).size() > 0;
        }
    }
    
    public Square getKing(Side side) {
        return board.getKingSquare(side);
    }

    public List<Square> getPieces(Side side) {
        return getPieces(side, List.of(PieceType.PAWN, PieceType.KNIGHT, PieceType.BISHOP, PieceType.ROOK, PieceType.QUEEN, PieceType.KING));
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

    public boolean isLesserAttacker(Square square){
        List<Square> attackers = getAttackers(square);
        for (Square attacker : attackers){
            if (PieceEval.worthCapturing(getPiece(attacker), getPiece(square))){
                return true;
            }
        }
        return false;
    }

    public List<Square> squaresAttackedByPiece(Square square){
        Piece piece = getPiece(square);
        return attacksByPieceType(square, piece.getPieceSide().flip(), piece.getPieceType());
    }

    public List<Square> piecesAttackedByPiece(Square square){
        Piece piece = getPiece(square);
        List<Square> defendingSquares = getPieces(piece.getPieceSide().flip());
        List<Square> attackedSquares = squaresAttackedByPiece(square);

        Set<Square> defendingSet = new HashSet<>();
        for (Square defendingSquare : defendingSquares){
            defendingSet.add(defendingSquare);
        }

        List<Square> piecesAttacked = new ArrayList<>();

        for (Square attackedSquare : attackedSquares){
            if (defendingSet.contains(attackedSquare)){
                piecesAttacked.add(attackedSquare);
            }
        }
        return piecesAttacked;
    }

    public List<Square> piecesDefendedByPiece(Square square){
        Piece piece = getPiece(square);
        List<Square> defendingSquares = getPieces(piece.getPieceSide());
        List<Square> attackedSquares = squaresAttackedByPiece(square);

        Set<Square> defendingSet = new HashSet<>();
        for (Square defendingSquare : defendingSquares){
            defendingSet.add(defendingSquare);
        }

        List<Square> piecesAttacked = new ArrayList<>();

        for (Square attackedSquare : attackedSquares){
            if (defendingSet.contains(attackedSquare)){
                piecesAttacked.add(attackedSquare);
            }
        }
        return piecesAttacked;
    }

    public List<Square> piecesAttackedByPiece(Square square, List<PieceType> excludedPieceTypes){
        List<PieceType> pieceTypes = new ArrayList<>(List.of(PieceType.PAWN, PieceType.KNIGHT, PieceType.BISHOP, PieceType.ROOK, PieceType.QUEEN, PieceType.KING));

        pieceTypes.removeAll(excludedPieceTypes);
        Piece piece = getPiece(square);

        List<Square> defendingSquares = getPieces(piece.getPieceSide().flip(), pieceTypes);
        List<Square> attackedSquares = squaresAttackedByPiece(square);

        Set<Square> defendingSet = new HashSet<>();
        for (Square defendingSquare : defendingSquares){
            defendingSet.add(defendingSquare);
        }

        List<Square> piecesAttacked = new ArrayList<>();

        for (Square attackedSquare : attackedSquares){
            if (defendingSet.contains(attackedSquare)){
                piecesAttacked.add(attackedSquare);
            }
        }
        return piecesAttacked;
    }



    public List<Move> moveSubList(List<Square> squares){
        return board.legalMoves().stream()
                .filter(move -> squares.contains(move.getFrom()))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Move> moveSubList(Square square){
        return board.legalMoves().stream()
                .filter(move -> move.getFrom() == square)
                .collect(java.util.stream.Collectors.toList());
    }
    

    public boolean isDoubleAttack(Square attackingSquare,  Square movingSquare, Square targetSquare){

        boolean isDefended = isDefended(targetSquare);
        
        
        if (isPieceChecking(movingSquare) && !isDefended) {
            boolean worthCapturingIfHanging = !isHanging(movingSquare) || 
                                            PieceEval.worthCapturing(getPiece(movingSquare), getPiece(targetSquare));
            
            if (worthCapturingIfHanging && !getAttackers(movingSquare).contains(targetSquare)) {
                //System.out.println("first attacking " + getPiece(attackingSquare).getPieceType() + " second attacking " + getPiece(movingSquare).getPieceType() + " defending " + getPiece(targetSquare).getPieceType());
                return true;
            }

        }
           
        if (isHanging(attackingSquare)){
            return false;
        }
        
        if (isPieceChecking(attackingSquare) && (PieceEval.worthCapturing(getPiece(attackingSquare), getPiece(targetSquare)))){
            return true;
            
        }

        if (isDefended){
            return false;
        }

        List<Square> defendingSquareAttackers = getAttackers(targetSquare);
        
        boolean isAttackingDefendingSquare = defendingSquareAttackers.contains(attackingSquare) 
                                          ^ defendingSquareAttackers.contains(movingSquare);

        boolean isSecondAttackedByDefender = getAttackers(movingSquare).contains(targetSquare);


        if (!isAttackingDefendingSquare || isSecondAttackedByDefender){
            return false;
        }

        boolean secondHasTargets = piecesAttackedByPiece(movingSquare, List.of(PieceType.PAWN)).isEmpty();

        if (secondHasTargets){
            return false;
        }

        return (PieceEval.worthCapturing(getPiece(movingSquare), getPiece(targetSquare)) && PieceEval.worthCapturing(getPiece(attackingSquare), getPiece(targetSquare)));

    }

    public boolean isPiece(Piece piece){
        return (piece.getPieceType() != PieceType.NONE && piece.getPieceType() != null);
    }

    public boolean isPieceChecking(Square checkingSquare){
        return getAttackers(getKing(getCurrentSide())).contains(checkingSquare);
    }

    public boolean attackerContainsPawn(Square square){
        List<Square> attackers = getAttackers(square);
        for (Square attacker : attackers){
            if (getPiece(attacker).getPieceType() == PieceType.PAWN){
                return true;
            }
        }
        return false;
    }

    public void resetBoard(){
        board = new Board();
        this.currentSide = board.getSideToMove();
        this.opponentSide = currentSide.flip();
    }

    public void doMove(Move move){
        board.doMove(move);
        this.currentSide = board.getSideToMove();
        this.opponentSide = currentSide.flip();
    }

    public void doMove(String moveStr){
        Move move = new Move(moveStr, board.getSideToMove());
        doMove(move);
    }

    public void undoMove(){
        board.undoMove();
    }

    public Side getCurrentSide(){
        return board.getSideToMove();
    }

    public List<Move> getLegalMoves(){
        return board.legalMoves();
    }

    public boolean isInCheck(){
        return !getAttackers(getKing(getCurrentSide())).isEmpty();
    }


    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }


    public Side getOpponentSide() {
        return opponentSide;
    }

    public Piece getPiece(Square square) {
        return board.getPiece(square);
    }

    public boolean isMated() {
        return board.isMated();
    }

    public String getFen() {
        return board.getFen();
    }

    // public BoardState copy(){
    //     return new BoardState(this);
    // }

    public BoardState copy(){
        return new BoardState(board.clone());
    }

}
