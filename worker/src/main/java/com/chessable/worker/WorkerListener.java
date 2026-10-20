package com.chessable.worker;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chessable.library.message.MateRes;
import com.chessable.library.entity.MissedMate;
import com.chessable.library.entity.Account;
import com.chessable.library.entity.AccountGame;
import com.chessable.worker.config.RabbitConfig;
import com.chessable.worker.game.GameMateService;
import com.chessable.library.service.MissedMateService;
import java.util.List;

@Service
public class WorkerListener {

    @Autowired
    private MissedMateService missedMateService;

    @Autowired
    private GameMateWorker gameMateWorker;

    @Autowired
    private GameTacticWorker gameTacticWorker;

    @Autowired
    private GameMateService gameMateService;

    @RabbitListener(queues = RabbitConfig.MATE_RESPONSE_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleMateResponse(MateRes message) {
        List<MissedMate> missedMates = gameMateWorker.findMissedMate(message);
        missedMateService.saveAll(missedMates);
    }

    @RabbitListener(queues = RabbitConfig.TACTIC_VERIFICATION_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleTacticVerification(Account message) {
        gameTacticWorker.gameTacticProcessor(message);
    }

    @RabbitListener(queues = RabbitConfig.USER_MATE_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleUserMate(AccountGame message) {
        gameMateService.processGame(message);
    }
}
