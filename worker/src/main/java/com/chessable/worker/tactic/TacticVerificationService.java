package com.chessable.worker.tactic;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.chessable.instance.TacticInstance;

public interface TacticVerificationService {
    CompletableFuture<Void> verifyTactics(List<TacticInstance> tactics);
}
