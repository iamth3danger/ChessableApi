package com.chessable.worker.game;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.chessable.library.entity.Account;
import com.chessable.library.service.AccountService;
import com.chessable.library.service.AccountGameService;
import com.chessable.library.service.FetchGameLogService;


@Service
public class AccountGameAnalyzerServiceImpl implements AccountGameAnalyzerService {

    private final AccountService accountService;
    private final AccountGameService accountGameService;
    private final FetchGameLogService fetchGameLogService;

    public AccountGameAnalyzerServiceImpl(AccountService accountService, AccountGameService accountGameService, FetchGameLogService fetchGameLogService) {
        this.accountService = accountService;
        this.accountGameService = accountGameService;
        this.fetchGameLogService = fetchGameLogService;
    }

    @Override
    public List<Account> getAccountsToUpdateGames() {
        Set<UUID> userIds = fetchGameLogService.findUserIdsToUpdateGames();
        List<Account> accounts = accountService.getAccountsByIds(userIds.stream().toList());
        return accounts;
    }
    
    
}
