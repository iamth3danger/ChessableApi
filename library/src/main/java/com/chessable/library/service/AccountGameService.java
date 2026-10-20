package com.chessable.library.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import com.chessable.library.entity.AccountGame;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public interface AccountGameService {
    AccountGame save(AccountGame userGame);
    List<AccountGame> saveAll(List<AccountGame> userGames);
    AccountGame getById(UUID id);
    List<AccountGame> getAll();
    long countByUserId(UUID userId);
    List<UUID> findEarliestGames(UUID userId, int limit);
    void deleteById(UUID id);
    Timestamp findLatestTimestamp(UUID userId);
    Set<UUID> findAccountsToUpdateGames();
    Set<UUID> findAccountsToUpdateTactics();
    List<AccountGame> findUnvisitedGames(UUID userId);
    CompletableFuture<List<AccountGame>> findUnvisitedGamesAsync(UUID userId);
    List<AccountGame> findTenUnvisitedGames(UUID userId);
    void deleteByIds(List<UUID> gameIds);
    List<UUID> findOldGameIds(List<UUID> userIds);
    List<UUID> findOldGameIdsByUserId(UUID userId);
    List<AccountGame> findByMissedStatusWaiting();
}
