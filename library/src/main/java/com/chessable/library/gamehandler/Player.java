package com.chessable.library.gamehandler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Player{
	private String id;
	private int rating;
	private String result;
	private String username;
	
	public String getId() {
		return id;
	}


	public int getRating() {
		return rating;
	}


	public String getResult() {
		return result;
	}


	public String getUsername() {
		return username;
	}


	public String getUuid() {
		return uuid;
	}


	String uuid;
	
	@Override
	public String toString() {
		return "Player [id=" + id + ", rating=" + rating + ", result=" + result + ", username=" + username + ", uuid="
				+ uuid + "]";
	}
	
	
}
