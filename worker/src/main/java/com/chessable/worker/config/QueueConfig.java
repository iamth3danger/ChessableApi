package com.chessable.worker.config;

import com.chessable.library.entity.AccountGame;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.chessable.library.entity.Account;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.Comparator;

@Configuration
public class QueueConfig {

   @Bean 
   public BlockingQueue<Account> gameTacticQueue() {
    return new PriorityBlockingQueue<Account>(2000, Comparator.comparing(Account::getAccessedAt).reversed());
   }

   @Bean
   public BlockingQueue<AccountGame> gameMateQueue(){
      return new LinkedBlockingQueue<>();
   }

   
}
