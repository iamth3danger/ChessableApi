package com.chessable.tactic_lib.rayservice;
import com.chessable.tactic_lib.enumerate.Direction;
import com.github.bhlangonijr.chesslib.Square;

public class Horizontal extends AbstractRayBehavior {
    
    
    public Horizontal(Square attacker, Square target){
        super(attacker, target);
    }

    @Override
    protected int getModifier() {
        return 1;
    }


    @Override
    public Direction getDirection() {
        return Direction.HORIZONTAL;
    }

}
