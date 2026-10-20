package com.chessable.library.service;

import java.util.List;
import java.util.UUID;
import java.util.Queue;

import org.springframework.stereotype.Service;

import com.chessable.library.entity.Account;
import com.chessable.library.repository.AccountRepo;
import com.chessable.library.error.UserNotFoundException;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepo accountRepo;

    public AccountServiceImpl(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    @Override
    public void deleteById(UUID id) {
        if (!accountRepo.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        
        accountRepo.deleteById(id);
    }

    @Override
    public List<Account> findAll() {
        return accountRepo.findAll();
    }

    @Override
    public Account findById(UUID id) {
        return accountRepo.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @Override
    public Account save(Account account) {

        Account savedUser = accountRepo.save(account);

        System.out.println("Saving account: " + savedUser.getUsername());
        return savedUser;
    }

    @Override
    public List<UUID> findAllNewUsers() {
        return accountRepo.findAllByNewUserTrue();
    }

    @Override
    public List<Account> getAccountsByIds(List<UUID> ids) {
        return accountRepo.findAllById(ids);
    }
}
