package com.chessable.stockfish.util;

import java.io.BufferedWriter;
import java.io.IOException;

public class StockfishMessager implements UciSender {
    private BufferedWriter writer;

    public StockfishMessager(BufferedWriter writer) {
        this.writer = writer;
    }

    @Override
    public UciSender send(UciCommand cmd){
        write(cmd);
        return this;
    }

    public void write(UciCommand cmd) {
        try{
            writer.write(cmd.uci());
            writer.newLine();
            writer.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
