package com.chessable.stockfish.util;

public sealed interface EngineState permits EngineState.Init, EngineState.Go, EngineState.Read,
        EngineState.Pause, EngineState.Restart, EngineState.Idle, EngineState.AwaitClose{
    record Init() implements EngineState {
    }
    record Go() implements EngineState {
    }
    record Read() implements EngineState {
    }
    record Pause() implements EngineState {
    }
    record AwaitClose() implements EngineState{}
    record Idle() implements EngineState {
    }
    record Restart() implements EngineState {
    }
}
