package com.chessable.worker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.jupiter.api.Test;

import com.chessable.library.entity.Account;
import com.chessable.library.message.Evaluation;
import com.chessable.library.message.MateRes;
import com.chessable.library.message.ScoreType;
import com.chessable.library.entity.MissedMate;


public class GameMateWorkerTest {

    private GameMateWorker newWorker() {
        return new GameMateWorker(
            () -> Collections.emptyList(),
            null
        );
    }

    private Evaluation mate(int value) {
        return new Evaluation(ScoreType.MATE, value, "FEN");
    }

    private Evaluation cp(int value) {
        return new Evaluation(ScoreType.CENTIPAWN, value, "FEN");
    }

    @Test
    void returnsEmptyWhenNoEvaluations() {
        GameMateWorker worker = newWorker();
        MateRes res = new MateRes(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());

        List<MissedMate> missed = worker.findMissedMate(res);

        assertTrue(missed.isEmpty());
    }

    @Test
    void recordsMissWhenMateFollowedByCentipawn() {
        GameMateWorker worker = newWorker();
        List<Evaluation> evals = Arrays.asList(
            mate(3),
            cp(0)
        );
        MateRes res = new MateRes(UUID.randomUUID(), UUID.randomUUID(), evals);

        List<MissedMate> missed = worker.findMissedMate(res);

        assertEquals(1, missed.size());
        assertEquals(evals.get(0).getValue(), missed.get(0).getMateIn());
    }

    @Test
    void noMissWhenMateSequenceStrictlyImprovesUntilEnd() {
        GameMateWorker worker = newWorker();
        List<Evaluation> evals = Arrays.asList(
            mate(5), mate(4), mate(3), mate(2), mate(1)
        );
        MateRes res = new MateRes(UUID.randomUUID(), UUID.randomUUID(), evals);

        List<MissedMate> missed = worker.findMissedMate(res);

        assertTrue(missed.isEmpty());
    }

    @Test
    void recordsMissWhenMateValueDoesNotImproveOrGetsWorse() {
        GameMateWorker worker = newWorker();
        List<Evaluation> evals = Arrays.asList(
            mate(3),
            mate(4) // worse (greater) than previous
        );
        MateRes res = new MateRes(UUID.randomUUID(), UUID.randomUUID(), evals);

        List<MissedMate> missed = worker.findMissedMate(res);

        assertEquals(1, missed.size());
        assertEquals(evals.get(0).getValue(), missed.get(0).getMateIn());
    }

    @Test
    void recordsMultipleMissedMatesAcrossSequences() {
        GameMateWorker worker = newWorker();
        List<Evaluation> evals = Arrays.asList(
            // First sequence misses due to CP
            mate(5), cp(0),
            // Second sequence misses due to non-improvement
            mate(3), mate(3),
            // Third sequence improves and then breaks via CP
            mate(4), mate(3), cp(0),
            cp(10), cp(9), cp(8),
            mate(5), mate(4), cp(10)
        );
        MateRes res = new MateRes(UUID.randomUUID(), UUID.randomUUID(), evals);

        List<MissedMate> missed = worker.findMissedMate(res);

        assertEquals(3, missed.size());
        assertEquals(evals.get(0).getValue(), missed.get(0).getMateIn());
        assertEquals(evals.get(2).getValue(), missed.get(1).getMateIn());
    }
}
