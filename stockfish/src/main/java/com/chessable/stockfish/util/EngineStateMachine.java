package com.chessable.stockfish.util;

import com.chessable.stockfish.util.UciCommand.*;
import com.chessable.stockfish.util.UciEvent.*;
import com.chessable.stockfish.util.EngineState.*;

public class EngineStateMachine {

    private EngineState state = new Init();
    private UciSender sender;
    private CandidateTracker tracker;
    private String position;
    private int currentPv = 3;
    private SearchAction searchAction;

    public EngineStateMachine(StockfishMessager messager, CandidateTracker tracker, String position) {
        this.sender = messager;
        this.tracker = tracker;
        this.position = position;
        init();
    }

    public void init() {
        sender.send(new Start());
    }

    public boolean isIdle() {return state instanceof Idle;}

    public void onEvent(UciEvent event) {

        state = switch (state) {
            case Init s -> switch (event)
            {
                case UciOk _ -> {
                    sender.send(new Ready());
                    yield s;
                }
                case ReadyOk _ -> {
                    sender.send(new Option("Hash", 512))
                        .send(new Option("Threads", 1))
                        .send(new Option("MultiPv", currentPv))
                        .send(new Ready());
                    yield new Go();
                }
                case Info _ -> {
                    yield s;
                }
                case Thinking _ -> {
                    yield s;
                }

                default -> {
                    throw new IllegalArgumentException("Invalid call on Init event " + event.toString());
                }
            };

            case Go _ -> switch (event)
            {
                case ReadyOk _ -> {
                    sender.send(new Position(position))
                          .send(new Infinite());
                    yield new Read();
                }
                default -> {
                    throw new IllegalArgumentException("Invalid call on Go");
                }
            };

            case Read s -> switch (event)
            {
                case Info info -> {
                    tracker.update(info);
                    searchAction = tracker.analysisConverged();
                    if (searchAction != SearchAction.CONTINUE){
                        yield new Pause();
                    }
                    yield s;
                }
                default -> s;
            };

            case Pause _ -> {
                sender.send(new Stop());
                if (searchAction != null && searchAction == SearchAction.RESTART){
                    yield new Restart();
                }
                yield new AwaitClose();
            }

            case Restart s -> switch (event){
                case BestMove _ -> {
                    currentPv++;
                    searchAction = SearchAction.CONTINUE;
                    tracker.reset();
                    sender.send(new Option("MultiPv", currentPv))
                          .send(new Ready());
                    yield new Go();
                }
                default -> s;
            };

            case AwaitClose s -> switch (event){
                
                case BestMove _ -> {
                    yield new Idle();
                }
                default -> s;
            };

            default -> state;
        };
    }
}
