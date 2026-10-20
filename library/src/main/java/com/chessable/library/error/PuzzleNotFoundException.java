package com.chessable.library.error;

public class PuzzleNotFoundException extends RuntimeException {

    public PuzzleNotFoundException(String message) {
        super(message);
    }

    public PuzzleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PuzzleNotFoundException(Throwable cause) {
        super(cause);
    }
    
}
