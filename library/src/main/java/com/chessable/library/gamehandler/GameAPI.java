package com.chessable.library.gamehandler;

import com.github.bhlangonijr.chesslib.pgn.PgnIterator;
import com.github.bhlangonijr.chesslib.game.Game;
import java.util.ArrayList;
import java.util.Arrays;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.chessable.library.entity.AccountGame;
import java.util.Date;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.chessable.library.entity.PlayerSide;
import com.chessable.library.entity.MissedStatus;


@JsonIgnoreProperties(ignoreUnknown = true)
class Accuracy{
	private double white;
	private double black;
	
	@Override
	public String toString() {
		return "Accuracy [white=" + white + ", black=" + black + "]";
	}
	public double getWhite() {
		return white;
	}
	public void setWhite(double white) {
		this.white = white;
	}
	public double getBlack() {
		return black;
	}
	public void setBlack(double black) {
		this.black = black;
	}
	
	
}




@JsonIgnoreProperties(ignoreUnknown = true)
public class GameAPI {
	
	private String url;
	private String pgn;
	private String timeControl;
	
	@JsonProperty("end_time")
	private long endTime;
	
	private Accuracy accuracies;
	private Player white;
	private Player black;
	
	@Override
	public String toString() {
		return "GameAPI [url=" + url + ", pgn=" + pgn + ", time_control=" + timeControl + ", end_time=" + endTime
				+ ", accuracies=" + (accuracies != null ? accuracies.toString() : "null") + ", white=" + (white != null ? white.toString() : "null") + ", black=" + (black != null ? black.toString() : "null") + "]";
	}
	public String getUrl() {
		return url;
	}

	public String getPgn() {
		return pgn;
	}

	public String getTimeControl() {
		return timeControl;
	}

	public long getEndTime() {
		return endTime;
	}

	public Accuracy getAccuracies() {
		return accuracies;
	}

	public Player getWhite() {
		return white;
	}

	public Player getBlack() {
		return black;
	}

	public AccountGame toAccountGame(String username, UUID userId) {
        if (pgn == null) return null;
        AccountGame accountGame = new AccountGame();
        Player player = new Player();
        if (getWhite().getUsername().equals(username)) {
            player = getWhite();
            accountGame.setPlayerSide(PlayerSide.WHITE);
            accountGame.setOpponent(getBlack().getUsername());
        } 
        else if (getBlack().getUsername().equals(username)) {
            player = getBlack();
            accountGame.setPlayerSide(PlayerSide.BLACK);
            accountGame.setOpponent(getWhite().getUsername());
        }
        else {
            throw new IllegalArgumentException("Invalid username");
        }
		accountGame.setUserId(userId);
        accountGame.setMoves(pgnToMoves());
        accountGame.setResult(player.getResult());
        accountGame.setTimeControl(getTimeControl());
        accountGame.setTimestamp(new Timestamp(endTime * 1000));
        accountGame.setMissedStatus(MissedStatus.WAITING);
        return accountGame;
    }
	
	// public List<PgnFile> parseJson(File jsonFile) throws IOException {
	// 	ObjectMapper mapper = new ObjectMapper();
	// 	try{
	// 		Games wrapper = mapper.readValue(jsonFile, Games.class);
	// 		return wrapper.getGames();
	// 	} catch (IOException e) {
	// 		e.printStackTrace();
	// 	}
	// 	return new ArrayList<>();
	// }
	
	public String pgnToMoves() {
        Iterable<String> iterable = Arrays.asList(pgn.split("\n"));
		
        try (PgnIterator gameList = new PgnIterator(iterable.iterator())) {
				MoveList moves = new MoveList();
			    for (Game game : gameList) {
					moves = game.getHalfMoves();
				}
                return moves.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

	// public List<Game> getGames() {
	// 	return new ArrayList<>(games);
	// }
}
