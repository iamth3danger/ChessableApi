package com.chessable.api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chessable.api.request.UpdateStatusRequest;
import com.chessable.library.entity.MissedMate;
import com.chessable.library.entity.Status;
import com.chessable.library.service.MissedMateService;

@RestController
@RequestMapping("/chessable")
public class MissedMateController {
    private final MissedMateService missedMateService;

    public MissedMateController(MissedMateService missedMateService) {
        this.missedMateService = missedMateService;
    }

    @GetMapping("/users/{userId}/missed-mates")
    public ResponseEntity<List<MissedMate>> getMissedMates(@PathVariable UUID userId, @RequestParam(required = false) String status) {
        if (status == null) {
            return ResponseEntity.badRequest().build();
        } 
        switch (status.toUpperCase()) {
            case "PENDING":
                return ResponseEntity.ok(missedMateService.findByAccountIdAndStatusPending(userId));
            case "MISSED":
                return ResponseEntity.ok(missedMateService.findByAccountIdAndStatusMissed(userId));
            default:
                return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/users/{userId}/missed-mates-retry")
    public ResponseEntity<List<MissedMate>> getMissedMatesRetry(@PathVariable UUID userId) {
        return ResponseEntity.ok(missedMateService.findByAccountIdAndStatusMissed(userId));
    }

    @PatchMapping("/mates/{mateId}/")
    public ResponseEntity<Void> missedMate(@PathVariable Long mateId, @RequestBody UpdateStatusRequest updateStatusRequest) {
        if (updateStatusRequest.getStatus() == null || updateStatusRequest.getStatus() == Status.PENDING) {
            return ResponseEntity.badRequest().build();
        }
        missedMateService.updateStatus(mateId, updateStatusRequest.getStatus());
        return ResponseEntity.noContent().build();
    }
}