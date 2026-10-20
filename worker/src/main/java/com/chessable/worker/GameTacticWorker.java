package com.chessable.worker;

import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.chessable.library.entity.Account;
import com.chessable.library.entity.AccountGame;
import com.chessable.instance.TacticInstance;
import com.chessable.library.entity.FetchGameLog;
import com.chessable.worker.fetch_game.FetchGameService;
import com.chessable.library.service.FetchGameLogService;
import com.chessable.worker.tactic.TacticGeneratorService;


import com.chessable.library.service.TacticInstanceService;
import com.chessable.worker.game.GamePersistanceService;
import com.chessable.worker.tactic.TacticVerificationService;

@Service
public class GameTacticWorker {
   
    private final FetchGameLogService fetchGameLogService;
    private final FetchGameService fetchGameService;
    private final TacticGeneratorService tacticGeneratorService;
    private final TacticInstanceService tacticInstanceService;
    private final TacticVerificationService tacticVerificationService;
    private final BlockingQueue gameMateQueue;
    private final GamePersistanceService gamePersistanceService;


    public GameTacticWorker(FetchGameLogService fetchGameLogService, FetchGameService fetchGameService, 
                           TacticGeneratorService tacticGeneratorService, TacticInstanceService tacticInstanceService,
                           GamePersistanceService gamePersistanceService, TacticVerificationService tacticVerificationService, BlockingQueue gameMateQueue) {
        this.fetchGameLogService = fetchGameLogService;
        this.fetchGameService = fetchGameService;
        this.tacticGeneratorService = tacticGeneratorService;
        this.tacticInstanceService = tacticInstanceService;
        this.tacticVerificationService = tacticVerificationService;
        this.gamePersistanceService = gamePersistanceService;
        this.gameMateQueue = gameMateQueue;
    }

    

    public void gameTacticProcessor(Account account) {

        System.out.println("Processing account: " + account.getId() + " at thread: " + Thread.currentThread().getName());

        FetchGameLog fetchGameLog = fetchGameLogService.findByUserId(account.getId());

        fetchGameService.fetchGamesForUser(account, fetchGameLog)
                    .thenCompose(games -> {
                        // Save games first
                        gamePersistanceService.saveGamesForAccount(fetchGameLog, games, account);
                        gameMateQueue.addAll(games);
                        // Then generate tactics
                        return tacticGeneratorService.generateTacticsAsync(games);
                    })
                    .thenCompose(tactics -> {
                        return tacticVerificationService.verifyTactics(tactics);
                    })
                    .exceptionally(e -> {
                        System.err.println("Error generating tactics for user " + account.getId() + ": " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    })
                    .join();
    }

    
}
