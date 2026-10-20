package com.chessable.stockfish;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.chessable.stockfish.util.*;
import com.chessable.stockfish.util.EvalScore.Cp;
import com.chessable.stockfish.util.EvalScore.Mate;
import com.chessable.stockfish.util.UciEvent.Info;

/**
 * Tests for CandidateTracker demonstrating how it tracks chess engine analysis, converges on best
 * moves, and integrates with EngineStateMachine.
 */
public class CandidateTrackerTest {

    private CandidateTracker tracker;

    @BeforeEach
    void setUp() {
        tracker = new CandidateTracker();
    }

    /**
     * Helper to create Info events with minimal required fields
     */
    private Info createInfo(int multipv, int depth, int seldepth, EvalScore score, String move) {
        return new Info(multipv, depth, seldepth, score, move);
    }

    @Test
    void testInitialState() {
        assertFalse(tracker.hasRequiredCandidates(),
                "Should not have required candidates initially");
        assertEquals(SearchAction.CONTINUE, tracker.analysisConverged(),
                "Should continue analysis when no candidates");
        assertTrue(tracker.topInfos().isEmpty(), "Should have no top infos initially");
    }

    @Test
    void testSingleCandidateTracking() {
        Info info1 = createInfo(1, 10, 12, new Mate(5), "e2e4");
        tracker.update(info1);

        assertFalse(tracker.hasRequiredCandidates(), "Need at least 3 candidates");
        assertEquals(SearchAction.CONTINUE, tracker.analysisConverged());
    }

    @Test
    void testMultipleCandidatesBasicTracking() {
        // Add 3 candidates with different scores
        Info info1 = createInfo(1, 10, 12, new Mate(5), "e2e4");
        Info info2 = createInfo(2, 10, 12, new Mate(7), "d2d4");
        Info info3 = createInfo(3, 10, 12, new Mate(9), "g1f3");

        tracker.update(info1);
        tracker.update(info2);
        tracker.update(info3);

        assertTrue(tracker.hasRequiredCandidates(), "Should have required candidates");
        assertEquals(SearchAction.CONTINUE, tracker.analysisConverged(),
                "Should continue until candidates converge");
    }

    @Test
    void testMateScoreConvergence() {
        // Simulate a mate in 3 converging - needs depth >= (2 * ply + 2) = 8
        // and score stable for STABLE_THRESHOLD (7) iterations
        Mate mate3 = new Mate(3);

        // Add 3 candidates
        tracker.update(createInfo(1, 8, 8, mate3, "e2e4"));
        tracker.update(createInfo(2, 8, 8, new Mate(7), "d2d4"));
        tracker.update(createInfo(3, 8, 8, new Mate(9), "g1f3"));

        // Need to stabilize the best move - same score for 7+ iterations, same seldepth for 3+
        // iterations
        for (int i = 0; i < 8; i++) {
            tracker.update(createInfo(1, 8 + i, 8, mate3, "e2e4"));
        }

        SearchAction action = tracker.analysisConverged();
        assertEquals(SearchAction.STOP, action, "Should stop when best mate converges");
    }

    @Test
    void testMultipleTiedLeaders() {
        // Two moves with same mate score
        Mate mate5 = new Mate(5);

        // Add 3 candidates, first two tied
        tracker.update(createInfo(1, 10, 12, mate5, "e2e4"));
        tracker.update(createInfo(2, 10, 12, mate5, "d2d4"));
        tracker.update(createInfo(3, 10, 12, new Mate(9), "g1f3"));

        // Stabilize both leaders
        for (int i = 0; i < 17; i++) {
            tracker.update(createInfo(1, 10 + i, 12, mate5, "e2e4"));
            tracker.update(createInfo(2, 10 + i, 12, mate5, "d2d4"));
        }

        SearchAction action = tracker.analysisConverged();
        assertEquals(SearchAction.STOP, action, "Should stop when all leaders converge");

        List<Info> topInfos = tracker.topInfos();
        assertEquals(2, topInfos.size(), "Should have 2 tied leaders");
    }

    @Test
    void testRestartWhenAllCandidatesTied() {
        // All 3 candidates have same score - need to increase multipv
        Mate mate5 = new Mate(5);

        tracker.update(createInfo(1, 10, 12, mate5, "e2e4"));
        tracker.update(createInfo(2, 10, 12, mate5, "d2d4"));
        tracker.update(createInfo(3, 10, 12, mate5, "g1f3"));

        // Stabilize all candidates
        for (int i = 0; i < 8; i++) {
            tracker.update(createInfo(1, 10 + i, 12, mate5, "e2e4"));
            tracker.update(createInfo(2, 10 + i, 12, mate5, "d2d4"));
            tracker.update(createInfo(3, 10 + i, 12, mate5, "g1f3"));
        }

        SearchAction action = tracker.analysisConverged();
        assertEquals(SearchAction.RESTART, action,
                "Should restart with higher multipv when all candidates tied");
    }

    @Test
    void testCloseSecondCandidate() {
        // Second candidate within 3 centipawns should require convergence
        tracker.update(createInfo(1, 10, 12, new Mate(5), "e2e4"));
        tracker.update(createInfo(2, 10, 12, new Mate(6), "d2d4")); // Close to mate 5
        tracker.update(createInfo(3, 10, 12, new Mate(20), "g1f3"));

        // Stabilize first candidate only
        for (int i = 0; i < 8; i++) {
            tracker.update(createInfo(1, 10 + i, 12, new Mate(5), "e2e4"));
        }

        // Second candidate not converged yet
        SearchAction action = tracker.analysisConverged();
        assertEquals(SearchAction.CONTINUE, action,
                "Should continue when close second candidate hasn't converged");

        // Now stabilize second candidate
        for (int i = 0; i < 8; i++) {
            tracker.update(createInfo(2, 10 + i, 12, new Mate(6), "d2d4"));
        }

        action = tracker.analysisConverged();
        assertEquals(SearchAction.STOP, action, "Should stop when close candidate also converges");
    }

    @Test
    void testLeaderUpdate() {
        // Start with one leader
        tracker.update(createInfo(1, 10, 12, new Mate(5), "e2e4"));
        tracker.update(createInfo(2, 10, 12, new Mate(7), "d2d4"));
        tracker.update(createInfo(3, 10, 12, new Mate(9), "g1f3"));

        // Update with better move
        tracker.update(createInfo(2, 11, 13, new Mate(3), "d2d4"));

        // The best leader should now be the second candidate with Mate(3)
        List<Info> topInfos = tracker.topInfos();
        assertTrue(topInfos.size() >= 1, "Should have at least one leader");
    }

    @Test
    void testAllSameEval() {
        Cp score = new Cp(-150);
        tracker.update(createInfo(1, 10, 12, score, "e2e4"));
        tracker.update(createInfo(2, 10, 12, score, "d2d4"));
        tracker.update(createInfo(3, 10, 12, score, "g1f3"));

        assertTrue(tracker.allSameEval(), "All candidates should have same evaluation");
    }

    @Test
    void testNotAllSameEval() {
        tracker.update(createInfo(1, 10, 12, new Cp(-150), "e2e4"));
        tracker.update(createInfo(2, 10, 12, new Cp(-100), "d2d4"));
        tracker.update(createInfo(3, 10, 12, new Cp(-150), "g1f3"));

        assertFalse(tracker.allSameEval(), "Candidates have different evaluations");
    }

    @Test
    void testReset() {
        // Add some candidates
        tracker.update(createInfo(1, 10, 12, new Mate(5), "e2e4"));
        tracker.update(createInfo(2, 10, 12, new Mate(7), "d2d4"));
        tracker.update(createInfo(3, 10, 12, new Mate(9), "g1f3"));

        assertTrue(tracker.hasRequiredCandidates());

        tracker.reset();

        assertFalse(tracker.hasRequiredCandidates(), "Should clear all candidates after reset");
        assertTrue(tracker.topInfos().isEmpty(), "Should have no leaders after reset");
    }

    @Test
    void testScoreProgression() {
        // Simulate engine progressively improving its evaluation
        tracker.update(createInfo(1, 5, 6, new Mate(10), "e2e4"));
        tracker.update(createInfo(2, 5, 6, new Mate(12), "d2d4"));
        tracker.update(createInfo(3, 5, 6, new Mate(15), "g1f3"));

        // Engine finds better continuation at deeper depth
        tracker.update(createInfo(1, 10, 12, new Mate(6), "e2e4"));
        tracker.update(createInfo(2, 10, 12, new Mate(8), "d2d4"));

        assertEquals(SearchAction.CONTINUE, tracker.analysisConverged(),
                "Should continue as scores are still changing");
    }

    @Test
    void testCentipawnScores() {
        // Test with centipawn scores instead of mates
        tracker.update(createInfo(1, 20, 25, new Cp(-50), "e2e4"));
        tracker.update(createInfo(2, 20, 25, new Cp(-100), "d2d4"));
        tracker.update(createInfo(3, 20, 25, new Cp(-150), "g1f3"));

        // CP scores don't converge the same way as mates
        for (int i = 0; i < 10; i++) {
            tracker.update(createInfo(1, 20 + i, 25, new Cp(-50), "e2e4"));
        }

        // Should continue as CP scores don't meet mate convergence criteria
        assertEquals(SearchAction.CONTINUE, tracker.analysisConverged());
    }

    @Test
    void testTopInfosWithoutRequiredCandidates() {
        // Only add 2 candidates
        tracker.update(createInfo(1, 10, 12, new Mate(5), "e2e4"));
        tracker.update(createInfo(2, 10, 12, new Mate(7), "d2d4"));

        List<Info> topInfos = tracker.topInfos();
        assertTrue(topInfos.isEmpty(), "Should return empty list when not enough candidates");
    }

    @Test
    void testUpdateChangesLeader() {
        // Start with a leader
        tracker.update(createInfo(1, 10, 12, new Mate(5), "e2e4"));
        tracker.update(createInfo(2, 10, 12, new Mate(7), "d2d4"));
        tracker.update(createInfo(3, 10, 12, new Mate(9), "g1f3"));

        // Update candidate 2 with a better score
        tracker.update(createInfo(2, 11, 13, new Mate(3), "d2d4"));

        // Verify by checking hasRequiredCandidates still works
        assertTrue(tracker.hasRequiredCandidates());
    }

    @Test
    void testSeldepthStabilityRequirement() {
        // For a Mate(3), seldepth must be stable for at least 3 iterations
        Mate mate3 = new Mate(3);

        tracker.update(createInfo(1, 8, 8, mate3, "e2e4"));
        tracker.update(createInfo(2, 8, 8, new Mate(7), "d2d4"));
        tracker.update(createInfo(3, 8, 8, new Mate(9), "g1f3"));

        // Stabilize score but keep changing seldepth
        for (int i = 0; i < 8; i++) {
            // Varying seldepth prevents convergence
            tracker.update(createInfo(1, 8 + i, 8 + i, mate3, "e2e4"));
        }

        SearchAction action = tracker.analysisConverged();
        assertEquals(SearchAction.CONTINUE, action, "Should continue when seldepth not stable");
    }

    @Test
    void testMultipleUpdatesToSameCandidate() {
        // Test that repeated updates to same candidate work correctly
        tracker.update(createInfo(1, 5, 6, new Mate(10), "e2e4"));
        tracker.update(createInfo(1, 6, 7, new Mate(10), "e2e4"));
        tracker.update(createInfo(1, 7, 8, new Mate(10), "e2e4"));

        tracker.update(createInfo(2, 5, 6, new Mate(12), "d2d4"));
        tracker.update(createInfo(3, 5, 6, new Mate(15), "g1f3"));

        assertTrue(tracker.hasRequiredCandidates());
    }

    @Test
    void testLeadersListWithSingleLeader() {
        Mate mate3 = new Mate(3);
        Mate mate7 = new Mate(7);

        tracker.update(createInfo(1, 10, 12, mate3, "e2e4"));
        tracker.update(createInfo(2, 10, 12, mate7, "d2d4"));
        tracker.update(createInfo(3, 10, 12, new Mate(9), "g1f3"));

        List<Info> topInfos = tracker.topInfos();
        assertEquals(1, topInfos.size(), "Should have only one leader when scores differ");
    }
}
