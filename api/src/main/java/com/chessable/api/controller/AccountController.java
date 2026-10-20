package com.chessable.api.controller;

import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.chessable.library.service.AccountService;
import com.chessable.library.entity.Account;
import com.chessable.api.producer.RabbitProducer;


/*
 * TODO add security to this controller
 */

@RestController
@CrossOrigin
@RequestMapping("/chessable")
public class AccountController {
    private final AccountService accountService;
    private final RabbitProducer rabbitProducer;

    public AccountController(AccountService accountService, RabbitProducer rabbitProducer) {
        this.accountService = accountService;
        this.rabbitProducer = rabbitProducer;
    }

    /*
     * get all accounts
     * TODO figure out how to not make this public
     */

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.findAll();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable UUID id) {
        Account account = accountService.findById(id);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        System.out.println("Creating account: " + account.getUsername());
        if (account.getId() != null) {
            account.setId(null);
        }
        rabbitProducer.sendAccount(account);
        return ResponseEntity.ok(account);
    }
    
    @PutMapping("/accounts")
    public ResponseEntity<Account> updateAccount(@RequestBody Account account) {
        Account savedAccount = accountService.save(account);
        return ResponseEntity.ok(savedAccount);
    }

    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
        accountService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
