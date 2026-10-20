package com.chessable.library.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.chessable.library.entity.Puzzle;

public interface PuzzleRepo extends JpaRepository<Puzzle, Integer> {
    @Query(
        value = "SELECT id FROM puzzle WHERE piece_types = :pieceTypes AND tactic = :tactic ORDER BY RANDOM() LIMIT :limit",
        nativeQuery = true
    )
    List<Integer> findByTacticAndPieceTypes(
        @Param("tactic") String tactic,
        @Param("pieceTypes") int pieceTypes,
        @Param("limit") int limit
    );

    @Query("SELECT p FROM Puzzle p LEFT JOIN AccountPuzzle ap ON p.id = ap.puzzleId AND ap.userId = :userId " +
       "WHERE p.pieceTypes = :pieceTypes AND p.rating BETWEEN :ratingLow AND :ratingHigh AND ap.puzzleId IS NULL")
    List<Puzzle> findNewPuzzlesForUser(@Param("userId") UUID userId, 
        @Param("pieceTypes") int pieceTypes, 
        @Param("ratingLow") int ratingLow, 
        @Param("ratingHigh") int ratingHigh
    );
    
}
