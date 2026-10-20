package com.chessable.worker.game;

import java.util.List;
import com.chessable.library.entity.Account;

public interface AccountGameAnalyzerService {

    public List<Account> getAccountsToUpdateGames();
}
