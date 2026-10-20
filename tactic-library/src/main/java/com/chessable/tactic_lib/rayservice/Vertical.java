package com.chessable.tactic_lib.rayservice;
import com.chessable.tactic_lib.enumerate.Direction;
import com.github.bhlangonijr.chesslib.Square;

public class Vertical extends AbstractRayBehavior {

    public Vertical(Square attacker, Square target){
        super(attacker, target);
    }

    @Override
    public int getModifier() {
        return 10;
    }

    @Override
    public Direction getDirection() {
        return Direction.VERTICAL;
    }

    
}