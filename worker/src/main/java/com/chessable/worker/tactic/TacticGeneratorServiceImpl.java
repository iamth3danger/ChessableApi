package com.chessable.worker.tactic;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.chessable.library.entity.AccountGame;
import com.chessable.library.entity.MissedStatus;
import com.chessable.library.entity.PlayerSide;
import com.chessable.instance.TacticInstance;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.Side;

import com.chessable.library.service.TacticInstanceService;
import com.chessable.library.service.AccountGameService;

import com.chessable.tactic_lib.tactic.Skewer;

import com.chessable.tactic_lib.tactic.Fork;
import com.chessable.tactic_lib.tactic.Pin;
import com.chessable.tactic_lib.tactic.Discovered;
import com.chessable.tactic_lib.inter.Tactic;
import com.chessable.tactic_lib.utils.BoardState;
import com.chessable.tactic_lib.utils.MoveFilterUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class TacticGeneratorServiceImpl implements TacticGeneratorService {

    private final TacticInstanceService tacticInstanceService;
    private final AccountGameService accountGameService;

    public TacticGeneratorServiceImpl(TacticInstanceService tacticInstanceService, AccountGameService accountGameService) {
        this.tacticInstanceService = tacticInstanceService;
        this.accountGameService = accountGameService;
    }

    private boolean playersTurn(Side side, PlayerSide playerSide) {
        if (side == Side.WHITE && playerSide.equals(PlayerSide.WHITE)) {
            return true;
        } else if (side == Side.BLACK && playerSide.equals(PlayerSide.BLACK)) {
            return true;
        }
        return false;
    }

    /*
     * Generates tactics for a list of account games
     */
    @Override
    public CompletableFuture<List<TacticInstance>> generateTacticsAsync(List<AccountGame> accountGames) {
        try {
            List<TacticInstance> foundTactics = generateTactics(accountGames);
            return CompletableFuture.completedFuture(foundTactics);
        } catch (Exception e) {
            System.err.println("Error generating tactics: " + e.getMessage());
            e.printStackTrace();
            return CompletableFuture.failedFuture(e);
        }
    }

    private List<TacticInstance> generateTactics(List<AccountGame> accountGames) {
        List<TacticInstance> foundTactics = new ArrayList<>();
        for (AccountGame accountGame : accountGames) {
            accountGame.setMissedStatus(MissedStatus.WAITING);
            String moves = accountGame.getMoves();
            List<Move> moveList = generateMoves(moves);
            Board board = new Board();
            BoardState boardState = new BoardState(board);
            Tactic [] tactics = {new Skewer(boardState), new Fork(boardState), new Pin(boardState), new Discovered(boardState)};
            for (Move move : moveList) {
                for (Move potentialMove : MoveFilterUtils.filterCastles(boardState.getLegalMoves())){
                    for (Tactic tactic : tactics) {
                        try {
                            Optional<TacticInstance> tacticInstance = tactic.evaluate(potentialMove);
                            if (tacticInstance.isPresent()) {
                                TacticInstance instance = tacticInstance.get();
                                Move instanceMove = instance.getMove();
                                if (playersTurn(boardState.getCurrentSide(), accountGame.getPlayerSide())) {
                                    if (!move.getFrom().equals(instanceMove.getFrom()) || !move.getTo().equals(instanceMove.getTo())) {
                                        instance.setAccountGameId(accountGame.getId());
                                        instance.setAccountId(accountGame.getUserId());
                                        foundTactics.add(instance);
                                    }
                                }
                                else {
                                    if (move.getFrom().equals(instanceMove.getFrom()) && move.getTo().equals(instanceMove.getTo())) {
                                        instance.setAccountGameId(accountGame.getId());
                                        instance.setAccountId(accountGame.getUserId());
                                        foundTactics.add(instance);
                                    }
                                }
                            }
                        } catch (NullPointerException e) {
                            System.err.println("NullPointerException caught: " + e.getMessage());
                            System.err.println("Tactic: " + tactic.toString());
                            System.err.println("FEN at error: " + board.getFen());
                            System.err.println("Move: " + potentialMove.toString());
                            e.printStackTrace();
                        }
                    }
                }
                board.doMove(move);
            }
        }
        return foundTactics;
    }

    private List<Move> generateMoves(String moves) {
        List<Move> moveList = new ArrayList<>();
        String[] movesArray = moves.split(" ");
        for (String move : movesArray) {
            Square from = Square.valueOf(move.substring(0, 2).toUpperCase());
            Square to = Square.valueOf(move.substring(2, 4).toUpperCase());
            Move moveObject = new Move(from, to);
            moveList.add(moveObject);
        }
        return moveList;
    }
}
