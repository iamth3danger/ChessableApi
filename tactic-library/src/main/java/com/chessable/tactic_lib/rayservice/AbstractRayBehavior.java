package com.chessable.tactic_lib.rayservice;

import com.chessable.tactic_lib.enumerate.Direction;
import com.chessable.tactic_lib.inter.RayBehavior;
import com.chessable.tactic_lib.utils.RankFileUtils;
import com.github.bhlangonijr.chesslib.Square;

import java.util.List;
import java.util.ArrayList;


public abstract class AbstractRayBehavior implements RayBehavior {

    protected int attackerValue;
    protected int targetValue;


    protected abstract int getModifier();

    public AbstractRayBehavior(Square attacker, Square target){
        this.attackerValue = RankFileUtils.squareToValue(attacker);
        this.targetValue = RankFileUtils.squareToValue(target);
    }

    private List<Square> getRayGeneric(int value, boolean condition){
        ArrayList<Square> ray = new ArrayList<>();

        int modifier = getModifier();
        int squareValue = value;
        int step = condition ? -modifier : modifier;

        while (true) {
            squareValue += step; 
            Square square = RankFileUtils.valueToSquare(squareValue);
            if (square == null) break;
            ray.add(square);
        }
        
        return ray;
    }
    

    @Override
    public List<Square> getRay(){
        return getRayGeneric(targetValue, targetValue < attackerValue);
    }

    @Override
    public List<Square> getRayBefore(){
        return getRayGeneric(attackerValue, attackerValue < targetValue);
    }
    
    @Override
    public List<Square> getRayBetween(){
        ArrayList<Square> ray = new ArrayList<>();
        int modifier = getModifier();
        int step = attackerValue < targetValue ? modifier : -modifier;
        int i = attackerValue + step;

        while (i != targetValue){
            ray.add(RankFileUtils.valueToSquare(i));
            i += step;
        }

        return ray;
    }

    public abstract Direction getDirection();
    
}
