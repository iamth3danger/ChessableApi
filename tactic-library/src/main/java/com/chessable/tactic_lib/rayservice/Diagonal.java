package com.chessable.tactic_lib.rayservice;
import com.chessable.tactic_lib.enumerate.Direction;
import com.github.bhlangonijr.chesslib.Square;

public class Diagonal extends AbstractRayBehavior {

    private int modifier;

    
    public Diagonal(Square attacker, Square target){
        super(attacker, target);
        modifier = (attackerValue - targetValue) % 11 == 0 ? 11 : 9;
    }

    @Override
    public int getModifier() {
        return modifier;
    }
    

    @Override
    public Direction getDirection() {
        return Direction.DIAGONAL;
    }

}