package com.chessable.library.service;

import java.util.List;
import java.util.UUID;

import com.chessable.library.entity.AccountPuzzle;

public interface AccountPuzzleService {
    AccountPuzzle findById(UUID id);
    AccountPuzzle save(AccountPuzzle accountPuzzle);
    List<AccountPuzzle> saveAll(List<AccountPuzzle> accountPuzzles);
    void delete(UUID id);
    List<UUID> findAccountsWithoutUnsolvedPuzzles();
    AccountPuzzle findNextPuzzle(UUID userId);
    Integer findUnsolvedPuzzlesCount(UUID userId);
    void setSolvedTrue(UUID puzzleId);
}
