package com.chessable.tactic_lib.rayservice;

import com.chessable.tactic_lib.enumerate.Direction;
import com.github.bhlangonijr.chesslib.Square;

public class HorizontalGenerator extends AbstractRayGenerator{
    
    public HorizontalGenerator(Square square){ super(square); }
    
    @Override
    protected int[] getAllModifiers() {
        return new int[]{1, -1};
    }
    
    @Override
    public Direction getDirection() {
        return Direction.HORIZONTAL;
    }
}
