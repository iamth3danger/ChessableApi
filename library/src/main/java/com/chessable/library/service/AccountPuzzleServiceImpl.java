package com.chessable.library.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import com.chessable.library.repository.AccountPuzzleRepo;
import com.chessable.library.entity.AccountPuzzle;
import com.chessable.library.error.UserPuzzleNotFoundException;

@Service
public class AccountPuzzleServiceImpl implements AccountPuzzleService {
    @Autowired
    private AccountPuzzleRepo accountPuzzleRepo;   

    public AccountPuzzleServiceImpl(AccountPuzzleRepo accountPuzzleRepo) {
        this.accountPuzzleRepo = accountPuzzleRepo;
    }

    @Transactional
    @Override
    public AccountPuzzle save(AccountPuzzle accountPuzzle) {
        return accountPuzzleRepo.save(accountPuzzle);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        if (!accountPuzzleRepo.existsById(id)) {
            throw new UserPuzzleNotFoundException("User puzzle not found with id: " + id);
        }
        accountPuzzleRepo.deleteById(id);
    }

    @Override
    public AccountPuzzle findById(UUID id) {
        return accountPuzzleRepo.findById(id).orElseThrow(() -> new UserPuzzleNotFoundException("User puzzle not found with id: " + id));
    }

    @Override
    public List<AccountPuzzle> saveAll(List<AccountPuzzle> accountPuzzles) {
        return accountPuzzleRepo.saveAll(accountPuzzles);
    }

    @Override
    public List<UUID> findAccountsWithoutUnsolvedPuzzles() {
        return accountPuzzleRepo.findAccountsWithoutUnsolvedPuzzles();
    }

    @Override
    public AccountPuzzle findNextPuzzle(UUID userId) {
        return accountPuzzleRepo.findNextPuzzle(userId);
    }

    @Override
    public Integer findUnsolvedPuzzlesCount(UUID userId) {
        return accountPuzzleRepo.findUnsolvedPuzzlesCount(userId);
    }

    @Override
    @Transactional
    public void setSolvedTrue(UUID puzzleId) {
        accountPuzzleRepo.setSolvedTrue(puzzleId);
    }
    
}
