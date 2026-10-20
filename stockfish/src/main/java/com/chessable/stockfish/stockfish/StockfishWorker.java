package com.chessable.stockfish.stockfish;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;

import java.util.concurrent.BlockingQueue;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import com.chessable.stockfish.stockfish.config.RabbitConfig;

import com.chessable.library.message.StockMessage;
import com.chessable.library.message.Evaluation;
import com.chessable.library.message.MateReq;
import com.chessable.library.message.MateRes;
import com.chessable.library.message.MoveFen;


import com.chessable.stockfish.stockfish.rabbit.RabbitProducer;
import com.chessable.stockfish.stockfish.Stockfish;

import com.chessable.instance.TacticInstance;
import com.rabbitmq.client.Channel;
import java.util.concurrent.TimeUnit;

@Service
public class StockfishWorker {
    
    private static final int DEPTH = 10;

    private BlockingQueue<Stockfish> stockfishPool;
    private RabbitTemplate rabbitTemplate;
    private RabbitProducer rabbitProducer;
    public StockfishWorker(BlockingQueue<Stockfish> stockfishPool, RabbitTemplate rabbitTemplate, RabbitProducer rabbitProducer) {
        this.stockfishPool = stockfishPool;
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitProducer = rabbitProducer;
    }

    @RabbitListener(queues = RabbitConfig.TACTIC_QUEUE, concurrency = "10")
    public void receiveTactic(StockMessage request, Channel channel) throws InterruptedException {
        Stockfish stockfish = stockfishPool.poll(3, TimeUnit.SECONDS);
        List<TacticInstance> tacticInstances = request.getPayload();
        List<TacticInstance> verifiedInstances = new ArrayList<>();

        try {
            if (stockfish == null) {
                channel.basicNack(0, false, true);
                return;
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        try {
            for (TacticInstance tacticInstance : tacticInstances) {
                Evaluation evaluation = stockfish.evaluate(tacticInstance.getFen(), DEPTH);
                if(evaluation.getBestMove() == tacticInstance.getMoveString()){
                    verifiedInstances.add(tacticInstance);
                }
            }
            StockMessage response = new StockMessage(request.getUserId(), verifiedInstances);
            rabbitTemplate.convertAndSend(RabbitConfig.TACTIC_VERIFICATION_QUEUE, response);
            } finally {
                stockfishPool.offer(stockfish);
        }

    }

    @RabbitListener(queues = RabbitConfig.MATE_QUEUE, concurrency = "10")
    public void receiveMate(MateReq request, Channel channel) throws InterruptedException {
        Stockfish stockfish = stockfishPool.poll(3, TimeUnit.SECONDS);
        List<MoveFen> moveFenList = request.getPayload();
        List<Evaluation> evaluations = new ArrayList<>();
        try {
            if (stockfish == null) {
                channel.basicNack(0, false, true);
                return;
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        try {
            for (int i = 0; i < moveFenList.size(); i++) {
                MoveFen moveFen = moveFenList.get(i);
                Evaluation evaluation = stockfish.evaluate(moveFen.getFen(), DEPTH);
                evaluation.setMoveIndex(i);
                evaluation.setMoveMade(moveFen.getMove());
                evaluations.add(evaluation);
            }
            MateRes mateRes = new MateRes(request.getUserId(), request.getGameId(), evaluations);
            rabbitProducer.sendMateRes(mateRes);
        } finally {
            stockfishPool.offer(stockfish);
        }
    }
    
}