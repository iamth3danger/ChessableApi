package com.chessable.worker.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {
   private final int WORKER_THREAD_POOL = 24;
   private final int DISPATCH_THREAD_POOL = 1;

    @Bean(name = "workerExecutor")
    public Executor workerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(WORKER_THREAD_POOL);
        executor.setMaxPoolSize(WORKER_THREAD_POOL);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("game-tactic-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    } 


    @Bean(name = "dispatchExecutor")
    public Executor dispatchExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(DISPATCH_THREAD_POOL);
        executor.setMaxPoolSize(DISPATCH_THREAD_POOL);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("dispatch-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    } 

}
