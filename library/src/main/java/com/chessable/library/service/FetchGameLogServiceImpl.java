package com.chessable.library.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.chessable.library.entity.FetchGameLog;
import com.chessable.library.repository.FetchGameLogRepo;

@Service
public class FetchGameLogServiceImpl implements FetchGameLogService {

    private final FetchGameLogRepo fetchGameLogRepo;

    public FetchGameLogServiceImpl(FetchGameLogRepo fetchGameLogRepo) {
        this.fetchGameLogRepo = fetchGameLogRepo;
    }

    @Override
    public FetchGameLog save(FetchGameLog fetchGameLog) {
        return fetchGameLogRepo.save(fetchGameLog);
    }

    @Override
    public FetchGameLog delete(int id) {
        FetchGameLog fetchGameLog = findById(id);
        fetchGameLogRepo.delete(fetchGameLog);
        return fetchGameLog;
    }

    @Override
    public FetchGameLog findById(int id) {
        return fetchGameLogRepo.findById(id).orElse(null);
    }

    @Override
    public FetchGameLog findByUserId(UUID userId) {
        return fetchGameLogRepo.findByUserId(userId);
    }

    @Override
    public FetchGameLog update(FetchGameLog fetchGameLog) {
        //System.out.println("Updating fetch game log for user ID: " + fetchGameLog.getUserId());
        if (fetchGameLog == null || fetchGameLog.getId() == 0) {
            return null;
        }
        fetchGameLog.setTimestamp(new Timestamp(System.currentTimeMillis()));
        return fetchGameLogRepo.save(fetchGameLog);
    }

    @Override
    public FetchGameLog update(UUID userId) {
        FetchGameLog fetchGameLog = findByUserId(userId);
        if (fetchGameLog == null) {
            return createFromUserId(userId);
        }
        return update(fetchGameLog);
    }
    
    @Override
    public List<FetchGameLog> updateAll(List<FetchGameLog> fetchGameLogs) {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        for (FetchGameLog fetchGameLog : fetchGameLogs) {
            fetchGameLog.setTimestamp(timestamp);
        }
        return fetchGameLogRepo.saveAll(fetchGameLogs);
    }

    @Override
    public FetchGameLog createFromUserId(UUID userId) {
        return fetchGameLogRepo.save(new FetchGameLog(userId));
    }

    @Override
    public Set<UUID> findUserIdsToUpdateGames() {
        return fetchGameLogRepo.findUserIdsToUpdateGames();
    }

    @Override
    public List<FetchGameLog> findByUserIdIn(List<UUID> userIds) {
        return fetchGameLogRepo.findByUserIdIn(userIds);
    }

    
}
