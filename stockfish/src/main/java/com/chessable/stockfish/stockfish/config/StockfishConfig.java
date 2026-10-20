package com.chessable.stockfish.stockfish.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import org.springframework.beans.factory.annotation.Value;
import com.chessable.stockfish.stockfish.Stockfish;

@Configuration
public class StockfishConfig {

    @Bean
    public BlockingQueue<Stockfish> stockfishPool(
            @Value("${stockfish.pool.size:10}") int poolSize) {
        BlockingQueue<Stockfish> pool = new ArrayBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            pool.add(new Stockfish());
        }
        return pool;
    }
}

