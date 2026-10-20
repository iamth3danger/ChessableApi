package com.chessable.tactic_lib.rayservice;

import com.chessable.tactic_lib.inter.RayBehavior;
import com.chessable.tactic_lib.utils.RankFileUtils;
import com.github.bhlangonijr.chesslib.Square;

/**
 * Factory for creating appropriate RayBehavior instances.
 */
public class RayFactory {

    private RayFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Returns the appropriate RayBehavior for the given squares.
     * 
     * @param attacker The starting square
     * @param importantSquare The target square
     * @return The appropriate RayBehavior, or null if no ray exists between the squares
     */
    public static RayBehavior getRayBehavior(Square attacker, Square target) {
        if (attacker.getRank() == target.getRank()) {
            return new Horizontal(attacker, target);
        }
        
        if (attacker.getFile() == target.getFile()) {
            return new Vertical(attacker, target);
        }

        int fileDifference = RankFileUtils.getFileDifference(attacker, target);
        int rankDifference = RankFileUtils.getRankDifference(attacker, target);

        if (fileDifference == rankDifference) {
            return new Diagonal(attacker, target);
        }
        
        return null;
    }

}