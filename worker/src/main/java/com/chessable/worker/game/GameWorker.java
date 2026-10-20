package com.chessable.worker.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import com.chessable.library.service.AccountService;
import com.chessable.worker.fetch_game.FetchGameService;

import java.util.Set;
import java.util.UUID;
import com.chessable.library.entity.AccountGame;
import com.chessable.library.service.AccountGameService;
import com.chessable.library.service.FetchGameLogService;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import jakarta.annotation.PostConstruct;
import com.chessable.library.entity.Account;
import com.chessable.library.entity.FetchGameLog;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentLinkedQueue;

class UserId {
    private final UUID userId;
    private final long timestamp;
    private final int queueNum;

    public UserId(UUID userId, int queueNum) {
        this.userId = userId;
        this.queueNum = queueNum;
        this.timestamp = System.currentTimeMillis();
    }

    public UUID getUserId() {
        return userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getQueueNum() {
        return queueNum;
    }

}

/* 
 * TODO:
 * - Add a controller to add a user to the queue
 * - New User Worker grabs one month of games
 * - if games are less than 100, need to go back to previous months
 * - it might work to just have a query games < 100 and then go back to previous months
 */

@Service
public class GameWorker {

    private final ConcurrentLinkedQueue<UUID> userQueue = new ConcurrentLinkedQueue<>();
    private final AccountService accountService;
    private final FetchGameService fetchGameService;
    private final AccountGameAnalyzerService accountGameAnalyzerService;
    private final AccountGameService accountGameService;
    private final GamePersistanceService gamePersistanceService;
    private final FetchGameLogService fetchGameLogService;
    private final int THREAD_POOL_SIZE = 10;


    @Autowired
    public GameWorker(AccountService accountService, FetchGameService fetchGameService, AccountGameAnalyzerService accountGameAnalyzerService, AccountGameService accountGameService, GamePersistanceService gamePersistanceService, FetchGameLogService fetchGameLogService) {
        this.accountService = accountService;
        this.fetchGameService = fetchGameService;
        this.accountGameAnalyzerService = accountGameAnalyzerService;
        this.accountGameService = accountGameService;
        this.gamePersistanceService = gamePersistanceService;
        this.fetchGameLogService = fetchGameLogService;
        }


    /*
     * Fetches games for users in the userQueue
     * 
     */

    // @Scheduled(fixedDelay = 2000)
    // public void fetchGamesForQueueUsers() {
    //     System.out.println("Fetching games for random users");

    //     List<UUID> userIds = new ArrayList<>();
    //     for (int i = 0; i < THREAD_POOL_SIZE; i++) {
    //         UUID userId = userQueue.poll();
    //         if (userId != null) {
    //             userIds.add(userId);
    //         } else {
    //             break;
    //         }
    //     }
        
    //     List<Account> accounts = accountService.findByIdInAccounts(userIds);

    //     List<FetchGameLog> fetchGameLogs = fetchGameLogService.findByUserIdIn(userIds);

        
    //     Map<UUID, Account> accountMap = accounts.stream()
    //             .collect(Collectors.toMap(Account::getId, account -> account));


    //     Map<UUID, FetchGameLog> fetchGameLogMap = fetchGameLogs.stream()
    //             .collect(Collectors.toMap(FetchGameLog::getUserId, fetchGameLog -> fetchGameLog));

    //     List<CompletableFuture<List<AccountGame>>> futures = userIds.stream()
    //             .map(userId -> fetchGameService.fetchGamesForUser(accountMap.get(userId), fetchGameLogMap.get(userId)))
    //             .toList();


    //     List<AccountGame> accountGames = futures.stream()
    //             .map(CompletableFuture::join)
    //             .flatMap(List::stream)
    //             .toList();



    //     gamePersistanceService.saveGames(fetchGameLogs, accountGames, userIds);

    // }


    
}
