package com.chessable.library.message;

import java.util.List;
import java.util.UUID;


public class MateRes extends MessageEnvelope<Evaluation> {
    
    public MateRes(UUID userId, UUID gameId, List<Evaluation> evaluations) {
        super(userId, gameId, evaluations);
    }
}