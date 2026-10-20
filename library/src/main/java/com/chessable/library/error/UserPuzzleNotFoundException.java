package com.chessable.library.error;

public class UserPuzzleNotFoundException extends RuntimeException {
    public UserPuzzleNotFoundException(String message) {
        super(message);
    }

    public UserPuzzleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserPuzzleNotFoundException(Throwable cause) {
        super(cause);
    }
}
