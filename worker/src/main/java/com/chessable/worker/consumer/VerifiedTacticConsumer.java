package com.chessable.worker.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.chessable.library.service.TacticInstanceService;
import com.chessable.library.message.StockMessage;
import com.chessable.worker.config.RabbitConfig;

@Service
public class VerifiedTacticConsumer {

    private final TacticInstanceService tacticInstanceService;

    public VerifiedTacticConsumer(TacticInstanceService tacticInstanceService) {
        this.tacticInstanceService = tacticInstanceService;
    }

    @RabbitListener(queues = RabbitConfig.TACTIC_VERIFICATION_QUEUE)
    public void handleVerifiedTactic(StockMessage stockMessage) {
        tacticInstanceService.saveAll(stockMessage.getPayload());
    }
}
