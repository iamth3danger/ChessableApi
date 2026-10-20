package com.chessable.library.service;

import java.util.List;
import com.chessable.library.repository.MissedMateRepo;
import com.chessable.library.entity.MissedMate;
import java.util.UUID;
import com.chessable.library.entity.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MissedMateServiceImpl implements MissedMateService {

    private final MissedMateRepo missedMateRepo;

    public MissedMateServiceImpl(MissedMateRepo missedMateRepo) {
        this.missedMateRepo = missedMateRepo;
    }

    @Override
    public void saveAll(List<MissedMate> missedMates) {
        missedMateRepo.saveAll(missedMates);
    }

    @Override
    public List<MissedMate> findByAccountIdAndStatusPending(UUID accountID) {
        return missedMateRepo.findByAccountIdAndStatusPending(accountID);
    }

    @Override
    public List<MissedMate> findByAccountIdAndStatusMissed(UUID accountID) {
        return missedMateRepo.findByAccountIdAndStatusMissed(accountID);
    }

    @Override
    public void setStatusMissed(Long id) {
        missedMateRepo.setStatusMissed(id);
    }

    @Override
    public void setStatusSolved(Long id) {
        missedMateRepo.setStatusSolved(id);
    }

    @Override
    public void updateStatus(Long id, Status status){
        missedMateRepo.setStatus(id, status);
    }
}
