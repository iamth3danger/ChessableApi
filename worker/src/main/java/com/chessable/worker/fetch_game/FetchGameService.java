package com.chessable.worker.fetch_game;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.chessable.library.entity.AccountGame;
import com.chessable.library.entity.Account;
import com.chessable.library.entity.FetchGameLog;

public interface FetchGameService {
    List<AccountGame> fetchGames(UUID userId, String username, int month, int year);
    List<AccountGame> fetchMultipleGames(Account account, FetchGameLog fetchGameLog, LocalDate startDate);
    //List<AccountGame> fetchMultipleGamesPreviousMonth(UUID userId, String username);
    List<AccountGame> fetchGamesCurrentMonth(UUID userId, String username);
    List<AccountGame> fetchGamesTotal(UUID userId);
    CompletableFuture<List<AccountGame>> fetchGamesForUser(Account account, FetchGameLog fetchGameLog);
}
