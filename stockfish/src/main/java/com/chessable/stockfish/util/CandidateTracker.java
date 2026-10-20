package com.chessable.stockfish.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import com.chessable.stockfish.util.UciEvent.Info;
import com.chessable.stockfish.util.EvalScore.*;
// Goals
// If 1 top score -> ensure top score is stable & 2nd score <= bestScore - 2
// If 2nd top score within 1 of topscore ensure top score is also stable
// If 2 top scores ensure both stable and & 3rd <= bestScore - 2
// If all three scores are top scores restart at depth 5

public class CandidateTracker {
    private Map<Integer, Candidate> candidateMap = new HashMap<>();
    private List<Candidate> leaders = new ArrayList<>();
    private Candidate best;

    public void update(Info info) {
        Candidate c = candidateMap.computeIfAbsent(info.multipv(), k -> new Candidate(info));
        c.update(info);
        updateLeader(c);
    }

    public SearchAction analysisConverged(){
        if (!hasRequiredCandidates()) return SearchAction.CONTINUE;
        for (var leader : candidateMap.values()){
            if(!leader.scoreConverged()){
                return SearchAction.CONTINUE;
            }
        }

        if (leaders.size() == candidateMap.size()){
            return SearchAction.RESTART;
        }
        // Candidate nextBest = candidateMap.get(leaders.size() + 1);
        
        // if (nextBest == null) { 
        //     return SearchAction.STOP;
        // }
        // if (nextBest.isClose(best)){
        //     return nextBest.scoreConverged() ? SearchAction.STOP : SearchAction.CONTINUE;
        // }
        return SearchAction.STOP;
    }

    private void updateLeader(Candidate c){
        if (best == null){
            best = c;
            leaders = List.of(c);
            return;
        }

        Candidate tmp = best;
        EvalCompare cmp = best.compareTo(c);
        
        switch(cmp){
            case BETTER -> {
                best = c;
            }
            case EQUAL -> {
                best = c;
            }
            default -> {}
        }
        
        leaders = candidateMap.values().stream().filter(cand -> cand.isTiedWith(best)).toList();
    }

    public boolean hasRequiredCandidates(){
        return candidateMap.size() >= 3;
    }



    public boolean allSameEval(){
        Candidate best = candidateMap.get(1);
        for (var e : candidateMap.entrySet()){
            if (e.getKey() == 1) continue;
            if (!best.isTiedWith(e.getValue())){
                return false;
            }
        }
        return true;
    }

    public void reset(){
        candidateMap.clear();
        leaders = new ArrayList<>();
        best = null;
    }

    public List<Info> topInfos(){
        if (!hasRequiredCandidates()) return List.of();
        System.out.println("top leaders inf size!!! " + leaders.size());
        return leaders.stream().map(Candidate::getCurrInfo).toList();
    }


    // public static void main(String[] args) {

    //     CandidateTracker tracker = new CandidateTracker();
    //     Mate mate5 = new Mate(5);


    //     // Add 3 candidates, first two tied
    //     tracker.update(new Info(1, 10, 12, mate5, "e2e4"));
    //     tracker.update(new Info(2, 10, 12, mate5, "d2d4"));
    //     tracker.update(new Info(3, 10, 12, new Mate(9), "g1f3"));

    //     // Stabilize both leaders
    //     for (int i = 0; i < 17; i++) {
    //         tracker.update(new Info(1, 10 + i, 12, mate5, "e2e4"));
    //         tracker.update(new Info(2, 10 + i, 12, mate5, "d2d4"));
    //     }

    //     SearchAction action = tracker.analysisConverged();
    //     System.out.println(action);

    //     List<Info> topInfos = tracker.topInfos();
    // }
    
}
