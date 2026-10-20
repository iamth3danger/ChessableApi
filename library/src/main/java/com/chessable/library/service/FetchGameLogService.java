package com.chessable.library.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.chessable.library.entity.FetchGameLog;

public interface FetchGameLogService {
    FetchGameLog save(FetchGameLog fetchGameLog);
    FetchGameLog findByUserId(UUID userId);
    FetchGameLog findById(int id);
    FetchGameLog delete(int id);  
    FetchGameLog update(FetchGameLog fetchGameLog);
    FetchGameLog update(UUID userId);
    List<FetchGameLog> updateAll(List<FetchGameLog> fetchGameLogs);
    FetchGameLog createFromUserId(UUID userId);
    Set<UUID> findUserIdsToUpdateGames();
    List<FetchGameLog> findByUserIdIn(List<UUID> userIds);
}
