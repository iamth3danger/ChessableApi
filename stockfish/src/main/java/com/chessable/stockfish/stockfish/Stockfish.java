package com.chessable.stockfish.stockfish;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import com.chessable.library.message.Evaluation;
import com.chessable.library.message.ScoreType;
import com.chessable.stockfish.util.CandidateTracker;
import com.chessable.stockfish.util.EngineStateMachine;
import com.chessable.stockfish.util.EvalScore;
import com.chessable.stockfish.util.MoveScore;
import com.chessable.stockfish.util.PositionAnalyzer;
import com.chessable.stockfish.util.StockfishMessager;
import com.chessable.stockfish.util.UciEvent;
import com.chessable.stockfish.util.UciEvent.Info;
import com.chessable.stockfish.util.UciParser;

record MateResult(boolean requireDeepEval, List<MoveScore> scores) {}

public class Stockfish {

    private Process process;
    private BufferedWriter writer;
    private BufferedReader reader;
    private boolean initialized = false;

    private final static int DEPTH = 10;
    private final static String ENGINE_PATH = "/usr/games/stockfish";
    private final static int MULTIPV = 10;

    public Stockfish(){
        try {
            process = startStockfish();
            writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            initializeEngine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeEngine() throws IOException {
        synchronized (this){
            if (initialized) return;
            send("uci");
            waitFor("uciok");
            send("isready");
            waitFor("readyok");
        }
    }

    private void clearState() throws IOException {
        synchronized (this){
            if (initialized) return;
            send("stop");
            send("isready");
            waitFor("readyok");
        }
    }

    private void send(String cmd) throws IOException{
        writer.write(cmd);
        writer.newLine();
        writer.flush();
    }

    private void waitFor(String token) throws IOException {
        String line;
        long start = System.currentTimeMillis();

        while ((line = reader.readLine()) != null) {
            if (line.contains(token)) return;

            // ⛑ safety timeout
            if (System.currentTimeMillis() - start > 5_000) {
                throw new IllegalStateException(
                    "Timeout waiting for " + token + ", last line: " + line
                );
            }
        }
    }

    public void syncReady() throws IOException{
        send("isready");
        waitFor("readyok");
    }


    public List<Info> analyzePos(String fen){

        CandidateTracker tracker = new CandidateTracker();
        StockfishMessager message = new StockfishMessager(writer);
        EngineStateMachine stateMachine = new EngineStateMachine(message, tracker, fen);
        UciParser parser = new UciParser();

        String line;
        Long start = System.currentTimeMillis();

        while(true){
            try {
                line = reader.readLine();
                if (line.contains("multipv 1"))
                    System.out.println(line);
                UciEvent event = parser.parse(line);
                stateMachine.onEvent(event);

                if (stateMachine.isIdle()){
                    break;
                }
                if ((System.currentTimeMillis() - start) > 2000){
                    System.out.println("Reached timeout, returning partial results");
                    send("stop");
                    break;
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        Long end = System.currentTimeMillis();
        System.err.println("time taken " + (end - start) / (double) 1000);

        return tracker.topInfos();
    }

    // public List<Line> analyzePos(String fen){
    //     try {
    //         send("ucinewgame");
    //         syncReady();
            
    //         setMultiPV(3);
    //         send("setoption name Hash value 512");
    //         send("setoption name Threads value 1");
    //         syncReady();

    //         send("position fen " + fen);
    //         send("go infinite");

    //         String line;

    //         long start = System.currentTimeMillis(); 
    //         CandidateTracker tracker = new CandidateTracker();

    //         while ((line = reader.readLine()) != null) {
    //             // System.out.println(line);
    //             if (line.startsWith("info depth")){
    //                 Line currLine = parseLine(line);
    //                 tracker.update(currLine);

    //                 if (currLine.multipv() == 1){
    //                     if (tracker.analysisConverged()){
    //                         System.out.println("convergence found!!!");
    //                         send("stop");
    //                         break;
    //                     }

    //                 }
    //             }

    //             if ((System.currentTimeMillis() - start) > 1000){
    //                 System.out.println("Reached timeout, returning partial results");
    //                 send("stop");
    //                 break;
    //             }
    //             }

    //         syncReady();
    //         return tracker.topLines();
    //         } catch (IOException e) {
    //             e.printStackTrace();
    //             return List.of();
    //         }
    // }
    public void setMultiPV(int multipv) {
        try {
            writer.write("setoption name MultiPV value " + multipv + "\n");
            writer.write("isready\n");
            writer.flush();
            String line;
            while (!(line = reader.readLine()).contains("readyok")) {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MateResult findMoveScores(String fen, List<String> moves, int depth) {
        ArrayList<String> moveCommands = new ArrayList<>();

        int loopCount = (int) Math.ceil(moves.size() / 10.0);
        int remainder = moves.size() % 10;
        
        int batchSize = 10;

        for (int i = 0; i < moves.size(); i += batchSize) {
            int to = Math.min(i + batchSize, moves.size());

            String command = "searchmoves " +
                String.join(" ", moves.subList(i, to)) +
                "\n";

            moveCommands.add(command);
        }

        PositionAnalyzer analyzer = new PositionAnalyzer(depth, fen);


        // System.out.println(moveCommands);

        try {

            setMultiPV(MULTIPV);
            
            for (int i = 0; i < loopCount; i++) {

                if (i == loopCount - 1 && remainder > 0) {
                    setMultiPV(remainder);
                }


                writer.write("position fen " + fen + "\n");
                writer.write("go depth " + depth + " ");
                writer.write(moveCommands.get(i));
                writer.flush();

                // Read engine output
                String line;

                while (!(line = reader.readLine()).contains("bestmove")) {
                    if (line.contains("pv e5e3"))
                        System.out.println(line);
                    if (line.startsWith("info depth")){
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
                    analyzer.register(move, d, eval, multipv, seldepth);

                    }
                }
            }

            List<MoveScore> scores = analyzer.getTopMoves();
            boolean requireDeepEval = analyzer.requireDeepEval();
            return new MateResult(requireDeepEval, scores);
       
    } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<MoveScore> findMate(String fen, List<String> moves, int depth) {

        List<MoveScore> topMoves = new ArrayList<MoveScore>();

        try {
            if (!initialized) {
                initializeEngine();
            }

            MateResult result = findMoveScores(fen, moves, depth);

            List<String> resultMoves = result.scores().stream().map(e -> e.move()).toList();
            int nextDepth = result.requireDeepEval() ? depth + 15 : depth + 5;
            MateResult nextResult = findMoveScores(fen, resultMoves, nextDepth);

            OptionalInt minValue = nextResult.scores().stream().map(e -> e.eval())
                                                      .filter(EvalScore.Mate.class::isInstance)
                                                      .map(EvalScore.Mate.class::cast)
                                                      .mapToInt(EvalScore.Mate::ply).min();
            if (minValue.isEmpty()){
                return List.of();
            }

            int min = minValue.getAsInt();

            for (var score : nextResult.scores()){
                if (score.eval() instanceof EvalScore.Mate){
                    EvalScore.Mate eval = (EvalScore.Mate) score.eval();
                    if (eval.ply() == min){
                        topMoves.add(score);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            // Try to reinitialize if there's an error
            try {
                cleanup();
                process = startStockfish();
                writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                initialized = false;
                initializeEngine();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return topMoves;
    }


    public Evaluation evaluate(String fen, int depth) {
        Evaluation result = null;
        try {
            if (!initialized) {
                initializeEngine();
            }

            // Send a position
            writer.write("position fen " + fen + "\n");
            writer.write("go depth " + depth + "\n");
            writer.flush();

            // Read engine output
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.contains("depth " + depth)) {
                    if (line.contains("mate")) {
                        System.out.println("Mate found " + line + " " + fen);
                        result = new Evaluation(ScoreType.MATE, Integer.parseInt(line.split(" ")[9]), fen);
                    } else if (line.contains("cp")) {
                        result = new Evaluation(ScoreType.CENTIPAWN, Integer.parseInt(line.split(" ")[9]), fen);
                    }
                }

                if (result != null && line.startsWith("bestmove")) {
                    result.setBestMove(line.split(" ")[1]);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Try to reinitialize if there's an error
            try {
                cleanup();
                process = startStockfish();
                writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                initialized = false;
                initializeEngine();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }


    public void cleanup() {
        try {
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (process != null && process.isAlive()) {
                process.destroy();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String extractStockfishBinary() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(ENGINE_PATH);
        File tempFile = File.createTempFile("stockfish", null);
        tempFile.deleteOnExit();
    
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1025];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    
        // Make it executable (for Unix-based systems)
        tempFile.setExecutable(true);
        return tempFile.getAbsolutePath();
    }

    public Process startStockfish() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(ENGINE_PATH);
        return pb.start();
    }


    public static void main(String[] args) {

       Stockfish sf = new Stockfish();
      
    //    String fen = "8/2Q5/8/3K4/1k6/8/8/8 w - - 11 74";
    //    List<String> moves = List.of("c7d8", "c7c8", "c7b8", "c7h7", "c7g7", "c7f7", "c7e7", "c7d7", "c7b7", "c7a7", "c7d6", "c7c6", "c7b6", "c7e5", "c7c5", "c7a5", "c7f4", "c7c4", "c7g3", "c7c3", "c7h2", "c7c2", "c7c1", "d5e6", "d5d6", "d5c6", "d5e5", "d5e4", "d5d4");
    
        String fen = "2r5/4k2p/4p2P/8/2p2K2/4R3/P1P1q3/8 b - - 1 44";
        // List<Info> bestLines = sf.analyzePos(fen);
        // bestLines.stream().map(Info::move).forEach(System.out::println);

        // fen = "2r3k1/6pp/p7/4p3/6PP/2p5/PP4PK/3q4 b - - 0 38";
        // bestLines = sf.analyzePos(fen);
        // bestLines.stream().map(Info::move).forEach(System.out::println);

        // fen =  "1r6/5k1p/5P2/8/2P1p3/8/q6P/6RK b - - 0 34";
        // bestLines = sf.analyzePos(fen);
        // bestLines.stream().map(Info::move).forEach(System.out::println);

        // fen = "2r5/4k2p/4p2P/8/P1p2K2/4R3/2q5/8 b - a3 0 45";
        fen = "r6k/pp4b1/2pN1q1p/2P3p1/3P4/5P1P/PP2Q1P1/3R1RK1 b - - 0 26";
        List<Info> bestLines = sf.analyzePos(fen);
        bestLines.stream().map(Info::move).forEach(System.out::println);
    }

}

