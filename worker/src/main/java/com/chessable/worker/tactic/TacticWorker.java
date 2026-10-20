package com.chessable.worker.tactic;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.Set;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.chessable.library.service.AccountService;
import com.chessable.library.service.AccountPuzzleService;
import com.chessable.worker.fetch_game.FetchGameService;
import com.chessable.library.service.AccountGameService;
import com.chessable.library.service.TacticInstanceService;

import com.chessable.library.entity.Account;
import com.chessable.instance.TacticInstance;

@Service
public class TacticWorker {

    private final ConcurrentLinkedQueue<UUID> userQueue = new ConcurrentLinkedQueue<>();    

    private final AccountService accountService;
    private final AccountPuzzleService accountPuzzleService;
    private final TacticGeneratorService accountPuzzleGeneratorService;
    private final FetchGameService fetchGameService;
    private final AccountGameService accountGameService;
    private final TacticInstanceService tacticInstanceService;

    private final TacticGeneratorService tacticGeneratorService;

    private final int THREAD_POOL_SIZE = 10;

    /* 
     * TODO:
     * - Set account_games visited to true or find where

     */

    @Autowired
    public TacticWorker(
        AccountService accountService,
        FetchGameService fetchGameService,
        TacticGeneratorService accountPuzzleGeneratorService,
        AccountPuzzleService accountPuzzleService,
        AccountGameService accountGameService,
        TacticInstanceService tacticInstanceService,
        TacticGeneratorService tacticGeneratorService
    ) {
        this.accountService = accountService;
        this.fetchGameService = fetchGameService;
        this.accountPuzzleGeneratorService = accountPuzzleGeneratorService;
        this.accountPuzzleService = accountPuzzleService;
        this.accountGameService = accountGameService;
        this.tacticInstanceService = tacticInstanceService;
        this.tacticGeneratorService = tacticGeneratorService;
    }

    //@Scheduled(fixedDelay = 10000)
    public void enqueueUserIds() {
        //System.out.println("Enqueuing user IDs for tactics");
        Set<UUID> userIds = accountGameService.findAccountsToUpdateTactics();
        //System.out.println("Enqueuing " + userIds.size() + " user IDs");
        for (UUID userId : userIds) {
            userQueue.offer(userId);
        }
    }

    //@Scheduled(fixedDelay = 2000)
    public void generateTacticsForUsers() {
        List<UUID> userIds = new ArrayList<>();
        for (int i = 0; i < THREAD_POOL_SIZE; i++) {
            UUID userId = userQueue.poll();
            if (userId != null) {
                userIds.add(userId);
            } else {
                break;
            }
        }

        List<CompletableFuture<List<TacticInstance>>> tacticFutures = userIds.stream()
            .map(userId -> 
                accountGameService.findUnvisitedGamesAsync(userId)
                    .thenCompose(games -> 
                        tacticGeneratorService.generateTacticsAsync(games)
                    )
                    .exceptionally(e -> {
                        System.err.println("Error generating tactics for user " + userId + ": " + e.getMessage());
                        e.printStackTrace();
                        userQueue.offer(userId);
                        return List.of();
                    })
            )
                .toList();

        List<List<TacticInstance>> tactics = tacticFutures.stream()
                .map(CompletableFuture::join)
                .toList();

        for (List<TacticInstance> tacticInstances : tactics) {
            tacticInstanceService.saveAll(tacticInstances);
        }
    }
}
