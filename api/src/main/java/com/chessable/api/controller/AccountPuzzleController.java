package com.chessable.api.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chessable.library.entity.Puzzle;
import com.chessable.library.service.AccountPuzzleService;

@RestController
@RequestMapping("/chessable/users/{userId}/puzzles")
public class AccountPuzzleController {

    private final AccountPuzzleService accountPuzzleService;

    public AccountPuzzleController(AccountPuzzleService accountPuzzleService) {
        this.accountPuzzleService = accountPuzzleService;
    }

    /*
     * Find next puzzle given the user's rating range and the next unvisited tactic instance 
     */

    @GetMapping("/next")
    public ResponseEntity<Puzzle> getAccountPuzzles(@PathVariable UUID userId) {
        return ResponseEntity.ok(null);
    }

    /*
     * get's number of remaining puzzles for the users
     * TODO change this to num available tactics
     */

    @GetMapping("/count")
    public ResponseEntity<Integer> getAccountPuzzlesCount(@PathVariable UUID userId) {
        Integer count = accountPuzzleService.findUnsolvedPuzzlesCount(userId);
        return ResponseEntity.ok(count);
    }

    /*
     * settings puzzle as solved
     * maybe depricated to new system
     */

    @PutMapping("/{puzzleId}/solve")
    public ResponseEntity<Void> solveAccountPuzzle(@PathVariable UUID puzzleId) {
        accountPuzzleService.setSolvedTrue(puzzleId);
        return ResponseEntity.noContent().build();
    }

    /*
     * adds user to queue to generate new puzzles
     * depricated to new system
     */

    @PostMapping("/enqueue")
    public ResponseEntity<Void> enqueuePuzzle(@PathVariable UUID userId) {
        try {
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}   
