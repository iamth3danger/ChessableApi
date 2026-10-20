package com.chessable.tactic_lib.rayservice;

import java.util.ArrayList;
import java.util.List;
import com.github.bhlangonijr.chesslib.Square;

public class RayPiece {

    public static List<AbstractRayGenerator> getAllDirectionRays(Square square){
        List<AbstractRayGenerator> rayPieces = new ArrayList<>();
        rayPieces.add(new HorizontalGenerator(square));
        rayPieces.add(new VerticalGenerator(square));
        rayPieces.add(new DiagonalGenerator(square));
        return rayPieces;
    }
    
}
