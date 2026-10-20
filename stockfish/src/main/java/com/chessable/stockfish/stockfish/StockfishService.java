package com.chessable.stockfish.stockfish;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.chessable.library.message.Evaluation;

@Service
public class StockfishService {
   

    public StockfishService() {
    }

    @Async
    public CompletableFuture<List<Evaluation>> evaluateFenList(List<String> fenList, int depth, Stockfish stockfish) {
        List<Evaluation> scores = new ArrayList<>();
        for (String fen : fenList) {
            scores.add(stockfish.evaluate(fen, depth));
        }
        return CompletableFuture.completedFuture(scores);
    }
}