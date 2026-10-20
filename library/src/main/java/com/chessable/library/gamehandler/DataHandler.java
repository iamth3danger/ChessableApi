package com.chessable.library.gamehandler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

public class DataHandler {
	ChessGameFetcher fetcher = new ChessGameFetcher();
	String username;
	Games game = new Games();
	String response = "";
	
	public DataHandler(String username) {
		this.username = username;
	}
	
	public List<GameAPI> findMonth(int month, int year) {
		String yearStr = String.valueOf(year);
		String monthStr = month < 10 ? "0" + String.valueOf(month) : String.valueOf(month);
		response = fetcher.fetchData(username, yearStr, monthStr);
        if (response == null){
            return List.of();
        }
		try {
			Games gameInst = fetcher.parseJson(response);
			game.getGames().addAll(gameInst.getGames());
			return gameInst.getGames();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return List.of();
	}

    public void getHundredGames() {
        YearMonth yearMonth = YearMonth.now();
        int month = yearMonth.getMonthValue();
        int year = yearMonth.getYear();
        while(game.getGames().size() < 100) {
            findMonth(month, year);
            month--;
            if(month == 0) {
                month = 12;
                year--;
            }
        }
    }

    public List<GameAPI> getGames() {
        return game.getGames();
    }
	
	// public void setResults(int month, int year) {
	// 	String yearStr = String.valueOf(year);
	// 	String monthStr = month < 10 ? "0" + String.valueOf(month) : String.valueOf(month);
	// 	LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.systemDefault());  
	//     int currentMonth = currentDateTime.getMonth().getValue();
	    
	// 	for (GameAPI api : game.getGames()) {
	// 		Entry entry = api.createEntry(monthStr, yearStr, username);
	// 		LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(api.getEnd_time()), ZoneId.systemDefault());
	// 		int gameMonth = dateTime.getMonth().getValue();
	// 		if (currentMonth == gameMonth)
	// 			entry.recent();        
	// 	}
	// }
	
	// public void findOpeningData() {
		
	// 	LocalDateTime date = LocalDateTime.now();
		
	// 	int year = date.getYear();
	// 	int month = date.getMonthValue();
	// 	int i = 0;
	// 	while(!response.equals("{\"games\":[]}")) {
	// 		findMonth(month, year);
	// 		month--;
	// 		i++;
	// 		if(month == 0) {
	// 			month = 12;
	// 			year -= 1;
	// 		}
	// 	}
		
		
		
		
	// 	//System.out.println("Time taken to parse response " + ((double)(end - start) / 1000));
	
	// 	Long start = System.currentTimeMillis();
		
	// 	setResults(year, month);
	
	// 	Long end = System.currentTimeMillis();
		
	// 	System.out.println("Time taken to set all opening results " + ((double)(end - start) / 1000));
	// }
	
	// public void logOpenings(int endMonth, FirstLogin firstLogin) {
		
	// 	LocalDateTime date = LocalDateTime.now();
		
	// 	int year = date.getYear();
	// 	int month = date.getMonthValue();
	// 	int i = 0;
	// 	while(month > endMonth - 1) {
	// 		System.out.println(month);
	// 		findMonth(month, year);
	// 		month--;
	// 		i++;
	// 		if(month == 0) {
	// 			month = 12;
	// 			year -= 1;
	// 		}
	// 	}
		
	// 	Long start = System.currentTimeMillis();
		
	// 	setResults(firstLogin.getYear(), firstLogin.getMonth());
		
	// 	Long end = System.currentTimeMillis();
		
	// 	System.out.println("Time taken to set all opening results " + ((double)(end - start) / 1000));
	// }

}
