package com.chessable.library.message;

import java.util.List;
import java.util.UUID;

public interface BusMessage<TPayload> {
    UUID getUserId();
    UUID getGameId();
    List<TPayload> getPayload();
}
