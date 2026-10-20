package com.chessable.api.worker;

import com.chessable.library.service.AccountGameService;
import com.chessable.library.entity.AccountGame;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;

import com.chessable.api.producer.RabbitProducer;
import com.chessable.library.service.FetchGameLogService;
import com.chessable.library.service.AccountService;
import com.chessable.library.entity.Account;
import java.util.UUID;
import java.util.Set;
import java.util.ArrayList;

public class QueueWorker {
    private final AccountGameService accountGameService;
    private final FetchGameLogService fetchGameLogService;
    private final RabbitProducer rabbitProducer;
    private final AccountService accountService;

    public QueueWorker(AccountGameService accountGameService, RabbitProducer rabbitProducer, FetchGameLogService fetchGameLogService, AccountService accountService) {
        this.rabbitProducer = rabbitProducer;
        this.accountGameService = accountGameService;
        this.fetchGameLogService = fetchGameLogService;
        this.accountService = accountService;
    }

   @Scheduled(fixedDelay = 3600000)
   public void enqueueMateGames() {
        List<AccountGame> accountGames = accountGameService.findByMissedStatusWaiting();

        for (AccountGame accountGame : accountGames) {
            rabbitProducer.sendUserMate(accountGame);
        }
   }

   @Scheduled(fixedDelay = 3600000)
   public void enqueueTacticGames() {
        Set<UUID> userIds = fetchGameLogService.findUserIdsToUpdateGames();
        List<UUID> userIdsList = new ArrayList<>(userIds);
        List<Account> accounts = accountService.getAccountsByIds(userIdsList);

        for (Account account : accounts) {
            rabbitProducer.sendUserTactic(account);
        }

   }
}