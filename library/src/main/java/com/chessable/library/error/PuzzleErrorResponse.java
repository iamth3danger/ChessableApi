package com.chessable.library.error;

public class PuzzleErrorResponse extends ErrorResponse {
    
    public PuzzleErrorResponse() {}

    public PuzzleErrorResponse(int status, String message, long timeStamp) {
        super(status, message, timeStamp);
    }
    
}
