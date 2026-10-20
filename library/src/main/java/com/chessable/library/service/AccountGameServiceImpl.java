package com.chessable.library.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.chessable.library.entity.AccountGame;
import com.chessable.library.repository.AccountGameRepo;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

@Service
public class AccountGameServiceImpl implements AccountGameService {

    private final AccountGameRepo accountGameRepo;

    public AccountGameServiceImpl(AccountGameRepo accountGameRepo) {
        this.accountGameRepo = accountGameRepo;
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        if (!accountGameRepo.existsById(id)) {
            throw new RuntimeException("User game not found");
        }
        accountGameRepo.deleteById(id);
    }

    @Override
    public List<AccountGame> getAll() {
        return accountGameRepo.findAll();
    }

    @Override
    public AccountGame getById(UUID id) {
        return accountGameRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User game not found"));
    }

    @Override
    @Transactional
    public AccountGame save(AccountGame userGame) {
        return accountGameRepo.save(userGame);
    }

    @Override
    public List<AccountGame> saveAll(List<AccountGame> userGames) {
        return accountGameRepo.saveAll(userGames);
    }


    @Async
    @Override
    public CompletableFuture<List<AccountGame>> findUnvisitedGamesAsync(UUID userId) {
        return CompletableFuture.supplyAsync(() -> findUnvisitedGames(userId));
    }
    
    @Override
    public long countByUserId(UUID userId) {
        return accountGameRepo.countByUserId(userId);
    }

    @Override
    public List<UUID> findEarliestGames(UUID userId, int limit) {
        return accountGameRepo.findEarliestGames(userId, limit);
    }

    @Override
    public Timestamp findLatestTimestamp(UUID userId) {
        return accountGameRepo.findLatestTimestamp(userId);
    }   

    @Override
    public Set<UUID> findAccountsToUpdateGames() {
        return accountGameRepo.findAccountsToUpdateGames();
    }

    @Override
    public Set<UUID> findAccountsToUpdateTactics() {
        return accountGameRepo.findAccountsToUpdateTactics();
    }

    @Override
    public List<AccountGame> findUnvisitedGames(UUID userId) {
        return accountGameRepo.findUnvisitedGames(userId);
    }

    @Override
    public List<AccountGame> findTenUnvisitedGames(UUID userId) {
        return accountGameRepo.findTenUnvisitedGames(userId);
    }

    @Override
    public void deleteByIds(List<UUID> gameIds) {
        accountGameRepo.deleteByIds(gameIds);
    }

    @Override
    public List<UUID> findOldGameIds(List<UUID> userIds) {
        return accountGameRepo.findOldGameIds(userIds);
    }

    @Override
    public List<UUID> findOldGameIdsByUserId(UUID userId) {
        return accountGameRepo.findOldGameIdsByUserId(userId);
    }

    @Override
    public List<AccountGame> findByMissedStatusWaiting() {
        return accountGameRepo.findByMissedStatusWaiting();
    }
}
