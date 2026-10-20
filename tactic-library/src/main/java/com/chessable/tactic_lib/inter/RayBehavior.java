package com.chessable.tactic_lib.inter;

import com.chessable.tactic_lib.enumerate.Direction;
import com.github.bhlangonijr.chesslib.Square;

import java.util.List;

/**
 * Defines behavior for calculating rays (lines of squares) on a chess board.
 */
public interface RayBehavior {
    /**
     * Gets all squares in the ray extending from the target in the direction away from the attacker.
     * 
     * @param attacker The starting square
     * @param target The target square that determines the direction
     * @return List of squares in the ray
     */
    List<Square> getRay();
    
    /**
     * Gets all squares between the attacker and target squares.
     * 
     * @param attacker The starting square
     * @param target The ending square
     * @return List of squares between attacker and target
     */
    List<Square> getRayBetween();

    List<Square> getRayBefore();

    
    /**
     * Gets the direction type of this ray.
     * 
     * @return The direction enum value
     */
    Direction getDirection();
}