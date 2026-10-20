package com.chessable.stockfish.util;

public sealed interface UciCommand permits UciCommand.Ready, UciCommand.Start, UciCommand.Stop, UciCommand.NewGame,
        UciCommand.Position, UciCommand.Option, UciCommand.Infinite {

    record Ready() implements UciCommand {
        @Override
        public String uci() {
            return "isready";
        }
    }

    public record Start() implements UciCommand{
        @Override
        public String uci() {
            return "uci";
        }
    }


    record Stop() implements UciCommand {
        @Override
        public String uci() {
            return "stop";
        }
    }

    record NewGame() implements UciCommand {
        @Override
        public String uci() {
            return "ucinewgame";
        }
    }

    record Position(String fen) implements UciCommand {
        @Override
        public String uci() {
            return "position fen " + fen;
        }
    }

    record Option(String option, int value) implements UciCommand {
        @Override
        public String uci() {
            return "setoption name " + option + " value " + value;
        }
    }

    record Infinite() implements UciCommand {
        @Override
        public String uci() {
            return "go infinite";
        }
    }
    public String uci();
}
