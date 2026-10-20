package com.chessable.stockfish.util;

import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
public class MoveTracker {
    private String move;
    private int maxDepth;
    private boolean containsMate;
    private static final int SELF_DEPTH_LIMIT = 2;
    private static final int SELF_DELTA_LIMIT = 2;

    private NavigableMap<Integer, EvalScore> scoreByDepth = new TreeMap<>();    
    private NavigableMap<Integer, Integer> multiPvByDepth = new TreeMap<>();    
    private NavigableMap<Integer, Integer> selDepthByDepth = new TreeMap<>();    

    private EvalScore maxDepthEval;

    public MoveTracker(String move, int maxDepth){
        this.move = move;
        this.containsMate = false;
        this.maxDepth = maxDepth;
    }

    public void insertEval(int depth, EvalScore eval, int multipv, int selDepth){
        if (!containsMate && eval.isMate()){
            containsMate = true;
        }

        if (depth == maxDepth){
            this.maxDepthEval = eval;
        }
        scoreByDepth.put(depth, eval);
        multiPvByDepth.put(depth, multipv);
        selDepthByDepth.put(depth, selDepth);
    }

    public int firstMate(){
        return scoreByDepth.entrySet().stream()
                            .filter(e -> e.getValue().isMate())
                            .mapToInt(Map.Entry::getKey)
                            .min()
                            .orElse(0);
    }

    public int mateFlips(){
        int flips = 0;
        EvalScore prev = null;

        for (EvalScore curr : scoreByDepth.values()){
            if (prev != null && curr.isMate() != prev.isMate()){
                flips++;
            }

            prev = curr;
        }

        return flips;
    }

    public int mateDelta(){
        int sum = 0;
        Integer prevPly = null;

        for (EvalScore curr : scoreByDepth.values()){
            if (curr instanceof EvalScore.Mate m){
                if (prevPly != null){
                    sum += Math.abs(m.ply() - prevPly);
                }

                prevPly = m.ply();
            }
        }

        return sum;
    }

    public EvalScore getEvalMaxDepth(){
        return maxDepthEval;
    }

    public EvalScore getEval(int depth){
        return scoreByDepth.get(depth);
    }

    public String getMove(){
        return move;
    }

    public boolean getContainsMate(){
        return containsMate;
    }

    public boolean multipvIncreasing(){
        ArrayList<Integer> keys = new ArrayList<>(multiPvByDepth.descendingKeySet());
        int halfKeys = keys.size() / 3;

        int firstKey = keys.get(0);
        int firstVal = multiPvByDepth.get(firstKey);

        for (int i = 1; i < halfKeys; i++){
            int val = keys.get(i);
            if (firstVal > multiPvByDepth.get(val)){
                return true;
            }
        }

        return false;
    }
    
    public boolean highSelfDepth(){

        Integer maxSelDepth = selDepthByDepth.get(maxDepth);
        if (maxSelDepth == null) return false;

        boolean smallDepthDiff = (maxDepth - maxSelDepth) <= SELF_DEPTH_LIMIT;
        if (smallDepthDiff) return true;

        ArrayList<Integer> keys = new ArrayList<>(selDepthByDepth.navigableKeySet());
        
        if (keys.size() < 2) return false;
        int start = Math.max((2 * keys.size()) / 3, 1);
        int sum = 0;

        for (int i = start; i < keys.size(); i++){
            int currKey = keys.get(i);
            int prevKey = keys.get(i - 1);

            sum += Math.abs(selDepthByDepth.get(currKey) - selDepthByDepth.get(prevKey));
        }

        boolean increasingSelfDepth = sum > SELF_DELTA_LIMIT;
        
        return increasingSelfDepth;
    }

}