package com.chessable.library.service;

import java.util.List;
import java.util.UUID;
import java.util.Set;

import com.chessable.instance.TacticInstance;

public interface TacticInstanceService {
    List<TacticInstance> findByAccountGameId(UUID accountGameId);
    void deleteByAccountGameId(UUID accountGameId);
    TacticInstance save(TacticInstance tacticInstance);
    List<TacticInstance> saveAll(List<TacticInstance> tacticInstances);
    List<TacticInstance> findUnvisitedTacticsByAccountId(UUID accountId);
    Set<UUID> findUnvisitedAccountIds();
    List<TacticInstance> findByAccountId(UUID accountId);
    void deleteByAccountGameIds(List<UUID> gameIds);
    TacticInstance findNextTactic(UUID accountId);
    void deleteOrphanedTactics();
    void deleteOldTacticsById(UUID accountId);
}
