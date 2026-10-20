package com.chessable.stockfish.util;

import java.util.NavigableMap;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Stream;
import java.util.stream.IntStream;
import java.util.ArrayList;

public class PositionAnalyzer {
    private int maxDepth;
    private String fen;
    
    private final Map<String, MoveTracker> moveTrackers = new HashMap<>();

    public PositionAnalyzer(int maxDepth, String fen){
        this.maxDepth = maxDepth;
        this.fen = fen;
    }

    public Map<String, EvalScore> getFinalMates(){
        Map<String, EvalScore> mateTrackers = new HashMap<>();
        moveTrackers.forEach((key, tracker) -> {
            EvalScore eval = tracker.getEvalMaxDepth();
            if (eval.isMate()){
                mateTrackers.put(key, eval);
            }
        });

        return mateTrackers;
    }

    public Map<String, EvalScore> getFinalCp(){
        Map<String, EvalScore> mateTrackers = new HashMap<>();
        moveTrackers.forEach((key, tracker) -> {
            EvalScore eval = tracker.getEvalMaxDepth();
            if (!eval.isMate()){
                mateTrackers.put(key, eval);
            }
        });

        return mateTrackers;
    }


    public boolean requireDeepEval(){
        List<MoveTracker> mates = moveTrackers.values()
                                              .stream()
                                              .filter(mt -> mt.getEvalMaxDepth() instanceof EvalScore.Mate).toList();

        for (var mate : mates){
            if (mate.highSelfDepth() || mate.multipvIncreasing()){
                System.out.println("high depth");
                return true;
            }
        }
        return false;
    }

    public List<MoveScore> getTopMoves(){
        Map<String, EvalScore> topMoves = new HashMap<>();
        Map<String, EvalScore> centi = new HashMap<>();

        moveTrackers.forEach((key, tracker) -> {
            EvalScore eval = tracker.getEvalMaxDepth();
            if (key.equals("d2b1")){
                System.out.println(eval);
            }
            if (eval.isMate()){
                EvalScore.Mate mate = (EvalScore.Mate) eval;
                if (mate.outcome() == MateOutcome.WIN)
                    topMoves.put(key, eval);
            }
            else{
                centi.put(key, eval);
            }
        });

        List<MoveScore> scores = new ArrayList<>();

        if (topMoves.size() < 5){
           ArrayList<Entry<String, EvalScore>> entryList = new ArrayList<>(centi.entrySet());
           int limit = Math.min(5 - topMoves.size(), entryList.size());
           entryList.sort((a, b) -> {
                EvalScore.Cp ea = (EvalScore.Cp) a.getValue();
                EvalScore.Cp eb = (EvalScore.Cp) b.getValue();

                return Integer.compare(eb.value(), ea.value());
           });

           for (int i = 0; i < limit; i++){
            topMoves.put(entryList.get(i).getKey(), entryList.get(i).getValue());
           }
        }

        for (var moves : topMoves.keySet()){
            scores.add(toMoveScore(moves));
        }

        return scores;
    }

    public void register(String move, int depth, EvalScore eval, int multipv, int selDepth){
        MoveTracker moveTracker = moveTrackers.get(move);
        if (moveTracker == null){
            moveTracker = new MoveTracker(move, maxDepth);
            moveTrackers.put(move, moveTracker);
        }
        moveTracker.insertEval(depth, eval, multipv, selDepth);
    }

    private Stream<EvalScore> finalEvals(){
        return moveTrackers.values().stream()
                            .map(MoveTracker::getEvalMaxDepth);
    }

    private IntStream finalMatePly(){
        return finalEvals().filter(EvalScore.Mate.class::isInstance)
                            .map(EvalScore.Mate.class::cast)
                            .mapToInt(EvalScore.Mate::ply);
    }


    private IntStream finalCentiPawnVals(){
        return finalEvals().filter(EvalScore.Cp.class::isInstance)
                            .map(EvalScore.Cp.class::cast)
                            .mapToInt(EvalScore.Cp::value);
    }

    public long countFinalMateNumber(){
        return finalMatePly().count();
    }

    public List<Integer> finalMates(){
        return finalMatePly().boxed().toList();
    }

    public OptionalInt bestMate(){
        return finalMatePly().min();
    }

    public OptionalInt maxCentiPawn(){
        return finalCentiPawnVals().max();
    }

    private boolean isBestMate(EvalScore eval, int bestPly){
        return eval instanceof EvalScore.Mate m && m.ply() == bestPly;
    }

    public List<String> bestMoves(){
        OptionalInt minMate = bestMate();

        if (minMate.isEmpty()){
            return List.of();
        }

        int bestPly = minMate.getAsInt();

        return moveTrackers.entrySet().stream()
                                      .filter(e -> isBestMate(e.getValue().getEvalMaxDepth(), bestPly))
                                      .map(Map.Entry::getKey)
                                      .toList();

    }


    
    public MoveScore toMoveScore(String move){
        MoveTracker tracker = moveTrackers.get(move);
        return new MoveScore(move, this.fen, tracker.getEvalMaxDepth());
    }
    

    

}