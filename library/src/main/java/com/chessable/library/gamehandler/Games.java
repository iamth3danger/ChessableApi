package com.chessable.library.gamehandler;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Games{
	private ArrayList<GameAPI> games = new ArrayList<>();

	public ArrayList<GameAPI> getGames() {
		return new ArrayList<GameAPI>(games);
	}

	public void setGames(ArrayList<GameAPI> games) {
		this.games = games;
	}
	
	public void addGames(ArrayList<GameAPI> games) {
		this.games.addAll(games);
	}
}
