package com.chessable.library.service;

import java.util.List;
import java.util.UUID;
import com.chessable.library.entity.Account;

public interface AccountService {
    public List<Account> findAll();
    public Account findById(UUID id);
    public Account save(Account user);
    public void deleteById(UUID id);
    public List<UUID> findAllNewUsers();
    public List<Account> getAccountsByIds(List<UUID> ids);
}
