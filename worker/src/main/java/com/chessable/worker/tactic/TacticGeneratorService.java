package com.chessable.worker.tactic;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.chessable.library.entity.AccountGame;
import com.chessable.instance.TacticInstance;

public interface TacticGeneratorService {
    CompletableFuture<List<TacticInstance>> generateTacticsAsync(List<AccountGame> accountGames);
}
