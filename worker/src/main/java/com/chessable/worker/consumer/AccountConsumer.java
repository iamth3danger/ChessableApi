package com.chessable.worker.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.chessable.library.entity.Account;
import com.chessable.library.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.concurrent.BlockingQueue;
import com.chessable.worker.config.RabbitConfig;

@Service
public class AccountConsumer {

    @Autowired
    private AccountService accountService;

    @Autowired
    private BlockingQueue<Account> gameTacticQueue;

    @RabbitListener(queues = RabbitConfig.ACCOUNT_QUEUE)
    public void handleAccount(Account account) {
        System.out.println("Handling account: " + account.getUsername());
        accountService.save(account);
        gameTacticQueue.offer(account);
    }
    
}

