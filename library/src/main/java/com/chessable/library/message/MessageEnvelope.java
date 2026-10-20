package com.chessable.library.message;

import java.util.List;
import java.util.UUID;

public class MessageEnvelope<TPayload> implements BusMessage<TPayload> {
    private UUID userId;
    private UUID gameId;
    private List<TPayload> payload;

    public MessageEnvelope(UUID userId, UUID gameId, List<TPayload> payload) {
        this.userId = userId;
        this.gameId = gameId;
        this.payload = payload;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public List<TPayload> getPayload() {
        return payload;
    }
    
}
