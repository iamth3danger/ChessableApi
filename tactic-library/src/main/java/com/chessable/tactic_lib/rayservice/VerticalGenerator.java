package com.chessable.tactic_lib.rayservice;

import com.chessable.tactic_lib.enumerate.Direction;
import com.github.bhlangonijr.chesslib.Square;

public class VerticalGenerator extends AbstractRayGenerator{
    
    public VerticalGenerator(Square square){ super(square); }

    @Override
    protected int[] getAllModifiers() {
        return new int[]{10, -10};
    }
    
    @Override
    public Direction getDirection() {
        return Direction.VERTICAL;
    }
}
