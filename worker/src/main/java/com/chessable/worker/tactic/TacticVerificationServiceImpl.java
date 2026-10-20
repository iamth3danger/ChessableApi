package com.chessable.worker.tactic;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.chessable.instance.TacticInstance;
import com.chessable.instance.StockfishStatus;
import com.chessable.library.message.TacticMessage;
import java.util.ArrayList;
import com.chessable.worker.rabbit.RabbitProducer;
import com.chessable.library.message.StockMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TacticVerificationServiceImpl implements TacticVerificationService {
    
    
    private static final String TACTIC_QUEUE = "tactic.queue";
    private static final String TACTIC_EXCHANGE = "tactic.exchange";
    private static final String TACTIC_ROUTING_KEY = "routing.key.tactic";

    @Autowired
    private RabbitProducer rabbitProducer;

    @Override
    public CompletableFuture<Void> verifyTactics(List<TacticInstance> tactics) {
        StockMessage stockMessage = new StockMessage(tactics.get(0).getAccountId(), tactics);
        rabbitProducer.sendTacticInstances(stockMessage);
        return CompletableFuture.completedFuture(null); 
    }
}
