package com.chessable.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.chessable.library.entity.MissedMate;
import com.chessable.worker.config.RabbitConfig;
import com.chessable.worker.game.AccountGameAnalyzerService;
import com.chessable.library.message.MateRes;
import com.chessable.library.message.ScoreType;
import com.chessable.library.message.Evaluation;
import com.chessable.library.service.MissedMateService;
import jakarta.annotation.PostConstruct;

@Service
public class GameMateWorker {

    private final AccountGameAnalyzerService accountGameAnalyzerService;
    private final MissedMateService missedMateService;

    private static final int MAX_MATE = 5;

    public GameMateWorker(AccountGameAnalyzerService accountGameAnalyzerService, MissedMateService missedMateService) {
        this.accountGameAnalyzerService = accountGameAnalyzerService;
        this.missedMateService = missedMateService;
    }
    

    public List<MissedMate> findMissedMate(MateRes message) {

        List<Evaluation> evaluations = message.getPayload();
        List<MissedMate> missedMates = new ArrayList<>();

        Evaluation prevEval = null;
        Evaluation mateStart = null;
        boolean mateFound = false;
        boolean missedMateFound = false;

        for (Evaluation evaluation : evaluations){

            if (evaluation.getType() == ScoreType.MATE && mateFound == false && evaluation.getValue() <= MAX_MATE){
                mateFound = true;
                mateStart = evaluation;
            }
            if (mateFound == true && prevEval != null && mateStart != null && prevEval.getType() == ScoreType.MATE){

                if (evaluation.getType() == ScoreType.CENTIPAWN){
                    if (missedMateFound){
                        missedMateFound = false;
                    }
                    else {
                        mateFound = false;
                        MissedMate missed = new MissedMate(mateStart.getFen(), mateStart.getValue(), message.getUserId(), message.getGameId(), mateStart.getMoveIndex(), evaluation.getMoveMade(), evaluation.getMoveIndex());
                        missedMates.add(missed);
                        mateStart = null;
                    }
                }
                else {
                    if (evaluation.getValue() >= prevEval.getValue() && missedMateFound == false){
                        missedMateFound = true;
                        mateFound = false;
                        MissedMate missed = new MissedMate(mateStart.getFen(), mateStart.getValue(), message.getUserId(), message.getGameId(), mateStart.getMoveIndex(), evaluation.getMoveMade(), evaluation.getMoveIndex());
                        
                        missedMates.add(missed);
                        mateStart = null;
                    }
                }

            }


            prevEval = evaluation;
        }

        return missedMates;

    }

    

}