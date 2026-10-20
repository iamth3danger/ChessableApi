package com.chessable.api.request;
import com.chessable.library.entity.Status;

public record UpdateStatusRequest(Status status) {
    public Status getStatus() {
        return status;
    }
}