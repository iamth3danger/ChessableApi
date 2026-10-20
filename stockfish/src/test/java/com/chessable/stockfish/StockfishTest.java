package com.chessable.stockfish;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.chessable.library.message.Score;
import com.chessable.stockfish.stockfish.Stockfish;
import com.chessable.stockfish.util.UciEvent.*;

@Tag("engine")
public class StockfishTest {

    static void assertMovesMatch(Stockfish sf, String fen, List<String> expectedMoves) {

        List<String> bestMoves = sf.analyzePos(fen).stream().map(Info::move).toList();

        assertEquals(expectedMoves.size(), bestMoves.size(), "Number of returned moves differs");

        assertTrue(bestMoves.containsAll(expectedMoves),
                "Returned moves do not match expected moves");
    }

    @Test
    void validateMoveScores() {
        Stockfish sf = new Stockfish();

        String fen = "2r3k1/6pp/p7/4p3/6PP/2p5/PP4PK/3q4 b - - 0 38";

        List<String> correctMoves = List.of("d1g4", "c3b2", "c3c2");

        assertMovesMatch(sf, fen, correctMoves);


        fen = "2r5/4k2p/4p2P/8/2p2K2/4R3/P1P1q3/8 b - - 1 44";

        correctMoves = List.of("c8f8");

        assertMovesMatch(sf, fen, correctMoves);

        fen = "1r6/5k1p/5P2/8/2P1p3/8/q6P/6RK b - - 0 34";

        correctMoves = List.of("a2f2", "a2e2");

        assertMovesMatch(sf, fen, correctMoves);

        fen = "6k1/pp3p1p/4p3/3pqp2/8/2r3P1/3K3R/7R b - - 11 41";

        correctMoves = List.of("e5e3");

        assertMovesMatch(sf, fen, correctMoves);


        // correctMoves = List.of("g3b3");

        // assertMovesMatch(sf, fen, correctMoves);

        fen = "6k1/pp3p1p/3qp3/3p1p2/8/2r3P1/4K2R/7R b - - 9 40";


        correctMoves = List.of("d6a6");

        assertMovesMatch(sf, fen, correctMoves);
        fen = "2r5/4k2p/4p2P/8/P1p2K2/4R3/2q5/8 b - a3 0 45";

        correctMoves = List.of("c2f5", "c8f8", "c2d2", "c8g8", "c2f2");

        assertMovesMatch(sf, fen, correctMoves);
        fen = "2r5/2bR1r1p/4p1k1/2p4P/5P2/5RK1/P1P1q3/8 b - - 0 39";

        correctMoves = List.of("g6h6", "g6f6", "g6h5");

        assertMovesMatch(sf, fen, correctMoves);

        fen = "1r6/1n4k1/p1p2p1p/8/P1BP1N2/8/1P3PPP/4R1K1 w - - 1 28";

        correctMoves = List.of("e1e7");

        assertMovesMatch(sf, fen, correctMoves);

        fen = "4r1k1/pp3p1p/4p3/3p1pK1/8/q5P1/7R/4R3 b - - 1 36";


        correctMoves = List.of("a3g3");

        assertMovesMatch(sf, fen, correctMoves);
        fen = "1r6/7R/5k2/8/2q1p3/8/7P/7K b - - 0 36";

        correctMoves = List.of("c4f1");

        assertMovesMatch(sf, fen, correctMoves);
        // I don't think this should happen...
        // fen = "6k1/6pp/p7/4p3/2r3PP/2p3P1/PP5K/3q4 b - - 0 39";
        // moves = List.of("g8h8", "g8f8", "g8f7", "c4c8", "c4c7", "c4c6", "c4c5", "c4g4", "c4f4",
        // "c4e4", "c4d4", "c4b4", "c4a4", "d1d8", "d1d7", "d1d6", "d1d5", "d1g4", "d1d4", "d1a4",
        // "d1f3", "d1d3", "d1b3", "d1e2", "d1d2", "d1c2", "d1h1", "d1g1", "d1f1", "d1e1", "d1c1",
        // "d1b1", "d1a1", "c3b2", "h7h6", "g7g6", "a6a5", "e5e4", "c3c2", "h7h5", "g7g5");


        // correctMoves = List.of("c3b2", "c3c2", "d1e2", "d1d2", "c4d4");

        // assertMovesMatch(sf, fen, correctMoves);
        fen = "2r5/4k2p/4p2P/6K1/2p1R3/8/8/q7 b - - 3 47";


        correctMoves = List.of("a1f6", "a1g1", "c8g8");

        assertMovesMatch(sf, fen, correctMoves);
        fen = "8/4k2p/4p2P/2r5/q7/4K3/2p5/8 b - - 1 53";


        correctMoves = List.of("a4a3", "c2c1q", "a4a3", "c5c3", "c2c1r");

        assertMovesMatch(sf, fen, correctMoves);

        fen = "1r5r/3Q3p/1b2pPk1/2p5/4q3/5n1P/P1P2PK1/3R1R2 b - - 0 31";

        correctMoves = List.of("f3e1", "f3h4");

        assertMovesMatch(sf, fen, correctMoves);

        fen = "2r5/2b1kr1p/4p2P/2p5/5P2/3R1RK1/P1P1q3/8 b - - 0 41";


        correctMoves = List.of("c8g8");

        assertMovesMatch(sf, fen, correctMoves);

        fen = "8/2Q5/8/3K4/1k6/8/8/8 w - - 11 74";


        correctMoves = List.of("c7c2", "c7g3");

        assertMovesMatch(sf, fen, correctMoves);

        fen = "8/8/8/6k1/8/5Q1P/6P1/6K1 w - - 3 55";


        correctMoves = List.of("f3f7");

        assertMovesMatch(sf, fen, correctMoves);
        fen = "r2r1k2/pp3p1Q/4p2p/3pb3/2q5/2N2P1P/PPP2PR1/1R1K4 b - - 2 23";

        correctMoves = List.of("c4f1");

        assertMovesMatch(sf, fen, correctMoves);
    }
}
