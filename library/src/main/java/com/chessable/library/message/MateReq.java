package com.chessable.library.message;

import java.util.List;
import java.util.UUID;

public class MateReq extends MessageEnvelope<MoveFen> {

    public MateReq(UUID userId, UUID gameId, List<MoveFen> moveFenList) {
        super(userId, gameId, moveFenList);
    }
    
}
