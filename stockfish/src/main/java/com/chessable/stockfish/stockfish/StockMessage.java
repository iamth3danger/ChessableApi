package com.chessable.stockfish.stockfish;

import java.util.List;
import java.util.UUID;

import com.chessable.instance.TacticInstance;


public class StockMessage {
    
    private UUID userId;

    private List<TacticInstance> tacticInstances;

    public StockMessage(UUID userId, List<TacticInstance> tacticInstances) {
        this.userId = userId;
        this.tacticInstances = tacticInstances;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }


    public List<TacticInstance> getTacticInstances() {
        return tacticInstances;
    }

    public void setTacticInstances(List<TacticInstance> tacticInstances) {
        this.tacticInstances = tacticInstances;
    }



}
