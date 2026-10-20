package com.chessable.library.service;

import java.util.List;
import java.util.UUID;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.chessable.instance.TacticInstance;
import com.chessable.library.repository.TacticInstanceRepo;

@Service
public class TacticInstanceServiceImpl implements TacticInstanceService {
    private final TacticInstanceRepo tacticInstanceRepo;

    public TacticInstanceServiceImpl(TacticInstanceRepo tacticInstanceRepo) {
        this.tacticInstanceRepo = tacticInstanceRepo;
    }

    @Override
    public void deleteByAccountGameId(UUID accountGameId) {
        tacticInstanceRepo.deleteByAccountGameId(accountGameId);
    }

    @Override
    public List<TacticInstance> findByAccountGameId(UUID accountGameId) {
        return tacticInstanceRepo.findByAccountGameId(accountGameId);
    }

    @Override
    public TacticInstance save(TacticInstance tacticInstance) {
        return tacticInstanceRepo.save(tacticInstance);
    }

    @Override
    public List<TacticInstance> saveAll(List<TacticInstance> tacticInstances) {
        return tacticInstanceRepo.saveAll(tacticInstances);
    }

    @Override
    public List<TacticInstance> findUnvisitedTacticsByAccountId(UUID accountId) {
        return tacticInstanceRepo.findUnvisitedTacticsByAccountId(accountId);
    }

    @Override
    public Set<UUID> findUnvisitedAccountIds() {
        return tacticInstanceRepo.findUnvisitedAccountIds();
    }

    @Override
    public List<TacticInstance> findByAccountId(UUID accountId) {
        return tacticInstanceRepo.findByAccountId(accountId);
    }

    @Override
    public void deleteByAccountGameIds(List<UUID> gameIds) {
        tacticInstanceRepo.deleteByAccountGameIds(gameIds);
    }

    @Override
    public TacticInstance findNextTactic(UUID accountId) {
        return tacticInstanceRepo.findNextTactic(accountId);
    }

    @Override
    public void deleteOrphanedTactics() {
        tacticInstanceRepo.deleteOrphanedTactics();
    }

    @Override
    public void deleteOldTacticsById(UUID accountId) {
        tacticInstanceRepo.deleteOrphanedTacticsById(accountId);
    }

}
