package com.chessable.tactic_lib.rayservice;

import com.chessable.tactic_lib.enumerate.Direction;
import com.github.bhlangonijr.chesslib.Square;

public class DiagonalGenerator extends AbstractRayGenerator{
    
    public DiagonalGenerator(Square square){ super(square); }
    
    @Override
    protected int[] getAllModifiers() {
        return new int[]{9, -9, 11, -11};
    }
    
    @Override
    public Direction getDirection() {
        return Direction.DIAGONAL;
    }
}
