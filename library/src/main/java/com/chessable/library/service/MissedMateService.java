package com.chessable.library.service;

import java.util.List;
import com.chessable.library.entity.MissedMate;
import java.util.UUID;
import com.chessable.library.entity.Status;

public interface MissedMateService {
    public void saveAll(List<MissedMate> missedMates);
    public List<MissedMate> findByAccountIdAndStatusPending(UUID accountID);
    public List<MissedMate> findByAccountIdAndStatusMissed(UUID accountID);
    public void setStatusMissed(Long id);
    public void setStatusSolved(Long id);
    public void updateStatus(Long id, Status status);
}