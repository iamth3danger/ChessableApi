package com.chessable.tactic_lib.rayservice;

import java.util.ArrayList;
import java.util.List;

import com.chessable.tactic_lib.enumerate.Direction;
import com.chessable.tactic_lib.utils.RankFileUtils;
import com.github.bhlangonijr.chesslib.Square;

public abstract class AbstractRayGenerator {
    
    private List<List<Square>> totalRays;

    public AbstractRayGenerator(Square square){
        totalRays = generateTotalRay(square);
    }

    protected abstract int[] getAllModifiers();

    private List<List<Square>> generateTotalRay(Square square){
        List<List<Square>> totalRays = new ArrayList<>();
        int[] modifiers = getAllModifiers();
        for (int modifier : modifiers) {
            List<Square> ray = new ArrayList<>();
            int squareValue = RankFileUtils.squareToValue(square);
            while (true) {
                squareValue += modifier;
                Square newSquare = RankFileUtils.valueToSquare(squareValue);
                if (newSquare == null) break;
                ray.add(newSquare);
            }
            if (!ray.isEmpty()) {
                totalRays.add(ray);
            }
        }
        return totalRays;
    }

    public List<List<Square>> getTotalRay(){
        return totalRays;
    }

    public abstract Direction getDirection();
}
