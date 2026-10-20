package com.chessable.library.gamehandler;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class SingletonObjectMapper {

    private static ObjectMapper instance = null;

    public static ObjectMapper getInstance() {
        if (instance == null) {
            instance = new ObjectMapper();
        }
        return new ObjectMapper();
    }
}



public class ChessGameFetcher {

	public String fetchData(String username, String year, String month) {
		final StringBuffer sb = new StringBuffer();
		
		try {
			
			
            URL url = new URL("https://api.chess.com/pub/player/" + username + "/games/" + year + "/" + month);


            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();


            int responsecode = conn.getResponseCode();


            if (responsecode != 200) {
                throw new RuntimeException("url " + url.toString() + "\nHttpResponseCode: " + responsecode);
            } else {

                final InputStream is = conn.getInputStream();
                final Reader reader = new InputStreamReader(is);
                final char[] buf = new char[1024];
                int read;
                
                while((read = reader.read(buf)) > 0) {
                	sb.append(buf, 0, read);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
		
		return sb.toString();
    }
	
	public Games parseJson(String response) throws JsonMappingException, JsonProcessingException {
		
		ObjectMapper objectMapper = SingletonObjectMapper.getInstance();
		Games game = objectMapper.readValue(response, Games.class);
		
		return game;
	}
	
	public static void main(String[] args) {
		
	}
 }


