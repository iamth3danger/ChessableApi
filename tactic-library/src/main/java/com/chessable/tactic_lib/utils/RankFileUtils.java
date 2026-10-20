package com.chessable.tactic_lib.utils;
import java.util.Map;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.File;
import com.github.bhlangonijr.chesslib.Rank;

public class RankFileUtils {

    private static final Map<Rank, Integer> RANK_VALUE =
    Map.of(
        Rank.RANK_1, 1,
        Rank.RANK_2, 2,
        Rank.RANK_3, 3,
        Rank.RANK_4, 4,
        Rank.RANK_5, 5,
        Rank.RANK_6, 6,
        Rank.RANK_7, 7,
        Rank.RANK_8, 8
    );

    private static final Map<Integer, Rank> VALUE_RANK =
    Map.of(
        1, Rank.RANK_1,
        2, Rank.RANK_2,
        3, Rank.RANK_3,
        4, Rank.RANK_4,
        5, Rank.RANK_5,
        6, Rank.RANK_6,
        7, Rank.RANK_7,
        8, Rank.RANK_8
    );

    private static final Map<File, Integer> FILE_VALUE =
    Map.of(
        File.FILE_A, 1,
        File.FILE_B, 2,
        File.FILE_C, 3,
        File.FILE_D, 4,
        File.FILE_E, 5,
        File.FILE_F, 6,
        File.FILE_G, 7,
        File.FILE_H, 8
    );

    private static final Map<Integer, File> VALUE_FILE =
    Map.of(
        1, File.FILE_A,
        2, File.FILE_B,
        3, File.FILE_C,
        4, File.FILE_D,
        5, File.FILE_E,
        6, File.FILE_F,
        7, File.FILE_G,
        8, File.FILE_H
    ); 

    public static int squareToValue(Square square) {
        if (RANK_VALUE.get(square.getRank()) == null || FILE_VALUE.get(square.getFile()) == null) {
            return -1;
        }
        return (RANK_VALUE.get(square.getRank()) * 10) + FILE_VALUE.get(square.getFile());
    }

    public static Square valueToSquare(int value) {
        int rankValue = value / 10;
        int fileValue = value % 10;
        File file = VALUE_FILE.get(fileValue);
        Rank rank = VALUE_RANK.get(rankValue);
        if (file == null || rank == null) {
            return null;
        }
        return Square.encode(rank, file);
    }

    public static int getFileDifference(Square square1, Square square2) {
        return Math.abs(FILE_VALUE.get(square1.getFile()) - FILE_VALUE.get(square2.getFile()));
    }

    public static int getRankDifference(Square square1, Square square2) {
        return Math.abs(RANK_VALUE.get(square1.getRank()) - RANK_VALUE.get(square2.getRank()));
    }

    public static boolean isSameFile(Square square1, Square square2) {
        return square1.getFile().equals(square2.getFile());
    }

    public static boolean isSameRank(Square square1, Square square2) {
        return square1.getRank().equals(square2.getRank());
    }

    public static boolean isSameDiagonal(Square square1, Square square2) {
        return getRankDifference(square1, square2) == getFileDifference(square1, square2);
    }

    public static boolean diffRank(Square square1, Square square2) {
        return !isSameRank(square1, square2);
    }

    public static boolean diffFile(Square square1, Square square2) {
        return !isSameFile(square1, square2);
    }
}
