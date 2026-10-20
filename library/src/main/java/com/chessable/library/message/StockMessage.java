package com.chessable.library.message;

import java.util.List;
import java.util.UUID;

import com.chessable.instance.TacticInstance;


public class StockMessage extends MessageEnvelope<TacticInstance> {
    
    public StockMessage(UUID userId, List<TacticInstance> tacticInstances) {
        super(userId, null, tacticInstances);
    }

}