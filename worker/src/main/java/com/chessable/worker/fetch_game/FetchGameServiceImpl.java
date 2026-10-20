package com.chessable.worker.fetch_game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;
import java.sql.Timestamp;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.time.YearMonth;
import java.util.concurrent.CompletableFuture;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chessable.library.entity.Account;
import com.chessable.library.entity.AccountGame;
import com.chessable.library.entity.FetchGameLog;
import com.chessable.library.gamehandler.DataHandler;
import com.chessable.library.gamehandler.GameAPI;
import com.chessable.library.repository.AccountGameRepo;
import com.chessable.library.service.AccountGameService;
import com.chessable.library.service.FetchGameLogService;
import com.chessable.library.service.AccountService;



@Service
public class FetchGameServiceImpl implements FetchGameService {

    private final AccountGameService accountGameService;
    private final FetchGameLogService fetchGameLogService;
    private final AccountService accountService;

    private final int MAX_GAMES = 100;
    private final int MAX_ITERATIONS = 6;

    public FetchGameServiceImpl(AccountGameService accountGameService,
            FetchGameLogService fetchGameLogService, AccountService accountService) {
        this.accountGameService = accountGameService;
        this.fetchGameLogService = fetchGameLogService;
        this.accountService = accountService;
    }

    @Override
    public List<AccountGame> fetchGames(UUID userId, String username, int month, int year) {

        DataHandler dataHandler = new DataHandler(username);
        List<GameAPI> games = dataHandler.findMonth(month, year);
        List<AccountGame> accountGames = new ArrayList<>();
        for (GameAPI game : games) {
            accountGames.add(game.toAccountGame(username, userId));
        }
        return accountGames;
    }

    @Override
    public List<AccountGame> fetchGamesCurrentMonth(UUID userId, String username) {
        LocalDate currentDate = LocalDate.now();
        int month = currentDate.getMonthValue();
        int year = currentDate.getYear();
        List<AccountGame> accountGames = fetchGames(userId, username, month, year);
        return accountGames;
    }

    // @Override
    // @Transactional
    // public List<AccountGame> fetchMultipleGames(Account account, FetchGameLog fetchGameLog) {

    // String username = account.getUsername();
    // UUID userId = account.getId();

    // if (fetchGameLog == null) {
    // return fetchGamesNewCurrentMonth(userId, username);
    // }
    // else {

    // return fetchGamesLogCurrentMonth(userId, username, fetchGameLog);
    // }

    // }

    // public List<AccountGame> fetchMultipleGamesPreviousMonth(UUID userId, String username) {
    // FetchGameLog fetchGameLog = fetchGameLogService.findByUserId(userId);

    // if (fetchGameLog == null) {
    // return fetchGamesNewPreviousMonth(userId, username);
    // }
    // else {
    // return fetchGamesLogPreviousMonth(userId, username, fetchGameLog);
    // }
    // }

    /*
     * TODO: accountGames.size() + games.size() > MAX_GAMES add only the latest games
     */

    public List<AccountGame> fetchMultipleGames(Account account, FetchGameLog fetchGameLog,
            LocalDate startDate) {
        List<AccountGame> accountGames = new ArrayList<>();
        int month = startDate.getMonthValue();
        int year = startDate.getYear();
        UUID userId = account.getId();
        String username = account.getUsername();


        Timestamp lastFetch = null;
        LocalDate lastFetchDate = null;
        int endMonth = -1;
        int endYear = -1;

        if (fetchGameLog != null) {
            lastFetch = fetchGameLog.getTimestamp();
            lastFetchDate = lastFetch.toLocalDateTime().toLocalDate();
            endMonth = lastFetchDate.getMonthValue();
            endYear = lastFetchDate.getYear();
        }

        int iterator = 0;
        while (accountGames.size() < MAX_GAMES) {

            List<AccountGame> games = fetchGames(userId, username, month, year);



            if (fetchGameLog != null && month == endMonth && year == endYear) {
                Timestamp latestTimestamp = accountGameService.findLatestTimestamp(userId);
                if (latestTimestamp != null) {
                    games = games.stream()
                            .filter(game -> game.getTimestamp().after(latestTimestamp))
                            .collect(Collectors.toList());
                    System.out.println("Fetching " + games.size() + " games");
                }
                accountGames.addAll(games);
                break;
            }
            accountGames.addAll(games);
            month--;
            if (month == 0) {
                month = 12;
                year--;
            }
            iterator++;
            if (iterator > MAX_ITERATIONS) {
                break;
            }
        }

        return accountGames;
    }

    private LocalDate monthBefore(LocalDate date) {
        return date.minusMonths(1);
    }

    /*
     * I think all this class needs is the fetchGamesForUser function
     */

    public CompletableFuture<List<AccountGame>> fetchGamesForUser(Account account,
            FetchGameLog fetchGameLog) {
        try {
            List<AccountGame> games = fetchMultipleGames(account, fetchGameLog, LocalDate.now());
            return CompletableFuture.completedFuture(games);
        } catch (Exception e) {
            System.err.println(
                    "Error fetching games for user " + account.getId() + ": " + e.getMessage());
            e.printStackTrace();
            CompletableFuture<List<AccountGame>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
    }

    @Override
    public List<AccountGame> fetchGamesTotal(UUID userId) {
        Account account = accountService.findById(userId);
        String username = account.getUsername();
        List<AccountGame> accountGames = new ArrayList<>();

        YearMonth current = YearMonth.now();
        int i = 0;

        while (accountGames.size() < 300) {
            YearMonth target = current.minusMonths(i);
            int year = target.getYear();
            int month = target.getMonthValue();
            accountGames.addAll(fetchGames(userId, username, month, year));
            i++;
        }
        System.out.println("Call number: " + i);
        return accountGames;
    }

    // private List<AccountGame> fetchGamesInternal(UUID userId, String username, int iterator,
    // FetchGameLog fetchGameLog) {
    // LocalDate currentDate = LocalDate.now();
    // int month = currentDate.getMonthValue() - iterator;
    // int year = currentDate.getYear();
    // if (month == 0) {
    // month = 12;
    // year--;
    // }
    // return fetchMultipleGamesGeneral(userId, username, month, year, fetchGameLog);
    // }

    /*
     * Are any of these functions necessary? So this is to handle processing a small batch of games
     * for new users for a faster start
     * 
     */

    /*
     * Fetch games for a user with a non null fetchGameLog
     */

    // private List<AccountGame> fetchGamesLogCurrentMonth(UUID userId, String username,
    // FetchGameLog fetchGameLog) {
    // return fetchGamesInternal(userId, username, 0, fetchGameLog);
    // }

    // /*
    // * Fetch games for a user with a non null fetchGameLog
    // */

    // private List<AccountGame> fetchGamesLogPreviousMonth(UUID userId, String username,
    // FetchGameLog fetchGameLog) {
    // return fetchGamesInternal(userId, username, 1, fetchGameLog);
    // }

    // /*
    // * Fetch games for a user with a null fetchGameLog
    // */

    // private List<AccountGame> fetchGamesNewCurrentMonth(UUID userId, String username) {
    // return fetchGamesInternal(userId, username, 0, null);
    // }

    // /*
    // * Fetch games for a user with a null fetchGameLog
    // */

    // private List<AccountGame> fetchGamesNewPreviousMonth(UUID userId, String username) {
    // return fetchGamesInternal(userId, username, 1, null);
    // }


}


