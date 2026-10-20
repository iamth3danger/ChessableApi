package com.chessable.stockfish.util;

import java.util.Iterator;
import java.util.Arrays;
import com.chessable.stockfish.util.UciEvent.*;

public class UciParser {
   
    public UciEvent parse(String line){
        if (line.startsWith("info depth")){
            return parseLine(line);
        }

        if (line.equals("readyok")){
            return new UciEvent.ReadyOk();
        }

        if (line.equals("uciok")){
            return new UciEvent.UciOk();
        }

        if (line.startsWith("bestmove")){
            return new UciEvent.BestMove("move");
        }

        // System.out.println(line);

        return new UciEvent.Thinking();
    }

    private Info parseLine(String line){
        EvalScore eval = null;
        Integer d = null;
        String move = "";
        Integer seldepth = null;
        Integer multipv = null;

        Iterator<String> it = Arrays.asList(line.split(" ")).listIterator();

        while (it.hasNext()){
            switch (it.next()){
                case "mate" -> {
                    int value = Integer.parseInt(it.next());
                    eval = new EvalScore.Mate(value);
                }
                case "depth" -> {
                    d = Integer.parseInt(it.next());
                }
                case "cp" -> {
                    int value = Integer.parseInt(it.next());
                    eval = new EvalScore.Cp(value);
                }
                case "pv" -> {
                    move = it.next();
                }
                case "seldepth" -> {
                    seldepth = Integer.parseInt(it.next());
                }
                case "multipv" -> {
                    multipv = Integer.parseInt(it.next());
                }
            }
        }
        
        if (d == null || multipv == null || seldepth == null){
            throw new Error();
        }

        return new UciEvent.Info(multipv, d, seldepth, eval, move);
    }
}
