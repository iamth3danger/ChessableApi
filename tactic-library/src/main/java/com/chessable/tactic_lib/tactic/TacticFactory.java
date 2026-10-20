package com.chessable.tactic_lib.tactic;

import com.chessable.tactic_lib.inter.Tactic;
import com.chessable.tactic_lib.tactic.Discovered;
import com.chessable.tactic_lib.tactic.Fork;
import com.chessable.tactic_lib.tactic.Skewer;
import com.chessable.tactic_lib.utils.BoardState;

public class TacticFactory {
    private TacticFactory(){}
    
    /*
     * Creates a Tactic instance based on the given name
     * @param tacticName Name of the tactic (case-insensitive)
     * @param boardState Initial BoardState for the tactic
     * @return Tactic instance
     * @throws IllegalArgumentException if tactic name is unknown
     */
    public static Tactic createTactic(String tacticName, BoardState boardState) {
        switch (tacticName.toLowerCase()) {
            case "skewer":
                return new Skewer(boardState);
            case "fork":
                return new Fork(boardState);
            case "discovered":
                return new Discovered(boardState);
            case "pin":
                return new Pin(boardState);
            default:
                throw new IllegalArgumentException("Unknown tactic: " + tacticName);
        }
    }
}
