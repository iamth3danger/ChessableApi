package com.chessable.worker.game;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chessable.library.entity.AccountGame;
import com.chessable.library.entity.Account;
import com.chessable.library.service.AccountGameService;
import com.chessable.library.service.FetchGameLogService;
import com.chessable.library.service.TacticInstanceService;
import com.chessable.library.entity.FetchGameLog;

/*
 * This service may be excessive but keeping it for now
 */

@Service
public class GamePersistanceService {

    private final AccountGameService accountGameService;
    private final FetchGameLogService fetchGameLogService;
    private final TacticInstanceService tacticInstanceService;

    public GamePersistanceService(AccountGameService accountGameService, FetchGameLogService fetchGameLogService, TacticInstanceService tacticInstanceService) {
        this.accountGameService = accountGameService;
        this.tacticInstanceService = tacticInstanceService;
        this.fetchGameLogService = fetchGameLogService;
    }

    /*
     * saves games and deletes old games and tactics
     * used in BackgroundGameWorker.java
     */


    public void saveGamesForAccount(FetchGameLog fetchGameLog, List<AccountGame> accountGames, Account account) {
        fetchGameLogService.update(fetchGameLog);
        accountGameService.saveAll(accountGames);
        List<UUID> oldGameIds = accountGameService.findOldGameIdsByUserId(account.getId());
        accountGameService.deleteByIds(oldGameIds);
        tacticInstanceService.deleteOldTacticsById(account.getId());
    }

    @Transactional
    public void saveGames(List<FetchGameLog> fetchGameLogs, List<AccountGame> accountGames, List<UUID> userIds) {

        fetchGameLogService.updateAll(fetchGameLogs);

        accountGameService.saveAll(accountGames);

        List<UUID> oldGameIds = accountGameService.findOldGameIds(userIds);

        accountGameService.deleteByIds(oldGameIds);
        tacticInstanceService.deleteOrphanedTactics();
    }
}
