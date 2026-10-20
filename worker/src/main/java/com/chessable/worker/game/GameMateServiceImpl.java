package com.chessable.worker.game;

import com.chessable.library.entity.AccountGame;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.chessable.library.entity.PlayerSide;
import java.util.ArrayList;
import java.util.List;
import com.chessable.library.message.MateReq;
import com.chessable.library.message.MoveFen;
import com.chessable.worker.rabbit.RabbitProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class GameMateServiceImpl implements GameMateService {

    @Autowired
    private RabbitProducer rabbitProducer;


    public GameMateServiceImpl(RabbitProducer rabbitProducer) {
        this.rabbitProducer = rabbitProducer;
    }
   
    @Override
    public void processGame(AccountGame game) {
        Long startTime = System.currentTimeMillis();
        Board board = new Board();
        // List<Score> scores = new ArrayList<>();
        // List<MissedMate> missedMates = new ArrayList<>(); 
        String[] moves = game.getMoves().split(" ");
        PlayerSide playerSide = game.getPlayerSide();
        int startIndex = playerSide.equals(PlayerSide.WHITE) ? 0 : 1;

        List<MoveFen> moveFenList = new ArrayList<>();

        if (playerSide.equals(PlayerSide.BLACK)){
            board.doMove(moves[0]);
        }

        for (int i = 0; i < moves.length; i++) {

            if (i % 2 == 0 && playerSide.equals(PlayerSide.WHITE)) {
                moveFenList.add(new MoveFen(moves[i], (i / 2), board.getFen()));
            }
            else if (i % 2 == 1 && playerSide.equals(PlayerSide.BLACK)) {
                moveFenList.add(new MoveFen(moves[i], (i / 2), board.getFen()));
            }
            board.doMove(moves[i]);

        }

        rabbitProducer.sendMateReq(new MateReq(game.getUserId(), game.getId(), moveFenList));
    }
}
