package com.rob2d.android.chinesepoker.network;
/*  Filename:   OnlineRequests.java
 *  Package:    com.rob2d.android.chinesepoker.network
 * 	Author:     Robert Concepcion III  */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.google.gson.Gson;

/** class used to send online messages to the server */
public class OnlineRequests 
{
	final static boolean SHOW_DEBUG_MESSAGES = true;
	
	public enum LobbyStatusUpdate {   NO_UPDATES,
									  ENTERED_LOBBY, 
								  	  PLAYER_JOINED, 
								      PLAYER_LEFT,
								      KICKED,
								      GAME_STARTED,
								      DROP_GAME,
								      LOBBY_CLOSED,
								      }
	
	/** static utility class used for all Json strings */
	public static Gson gson = new Gson();
	
	/** Logs into a server. Returns the login info if successful. Otherwise returns a LoginInfo object with
	 * the error. If nothing at all works(communication breaks), a null is returned.
	 * Also, saves the login results to the OnlineHandler class if login was successful */
	public static LoginInfo login(String u, String p)
	{
		LoginInfo loginInfo = null;
		try
		{
				HttpClient client = new DefaultHttpClient();
			
				/* Sets the address the request is going to be made to for a response */
				HttpPost post = new HttpPost("http://vovkin.com/choyos/poker/app/login.php");
			
				Log.d("CPDEBUG", "\n(===   ONLINE LOG IN   ===)");
				/* Set the values that are going to be send to server */
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("user", u));
				nameValuePairs.add(new BasicNameValuePair("password", p));

			/*
			 * Not all characters are allowed in http requests (headers). This will filter
			 * all non-allowed character and give then a "code" that can be parsed later
			 * Also sets something call entity that idkwtf is T_T
			 */
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				/* Execute the code and store the response in an object */
				HttpResponse response = client.execute(post);

				/* fetch the response */
				BufferedReader rd = new BufferedReader(
						new InputStreamReader(
						response.getEntity().getContent()
								)
				);

			// gather the response from the http server
			String responseStr = new String();
			String line;
			while ((line = rd.readLine()) != null)
				responseStr += line;
		
			//save the login results from the response
			loginInfo = gson.fromJson(responseStr, LoginInfo.class);
			if(loginInfo.verified == LoginInfo.LOGIN_VERIFIED)
			{
				loginInfo.userName = u;
				Log.d("CPDEBUG", "LOGGED IN WITH USERID : " + loginInfo.userName);
			}
		}
		catch (IOException e)	/* catch IO Exception if necessary */
		{
			e.printStackTrace();
			return loginInfo;
		}
		
		Log.d("CPDEBUG", "(=============================)\n");
		
		return loginInfo;
	}

	/** message to communicate with the server */
	public static String sendMsg(PlayerMessage message)
	{
		try
		{
			HttpClient client = new DefaultHttpClient();
			
			/* Sets the address the request is going to be made to for a response */
			HttpPost post = new HttpPost("http://vovkin.com/choyos/poker/app/index.php");
			
			/* Set the values that are going to be send to server */
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("DATA_REQUEST", OnlineRequests.gson.toJson(message)));

			/*
			 * Not all characters are allowed in http requests (headers). This will filter
			 * all non-allowed character and give then a "code" that can be parsed later
			 * Also sets something call entity that idkwtf is T_T
			 */
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			/* Execute the code and store the response in an object */
			HttpResponse response = client.execute(post);

			/* fetch the response */
			BufferedReader rd = new BufferedReader(
				new InputStreamReader(
					response.getEntity().getContent()
				)
			);

			// gather the response from the http server
			String responseStr = new String();
			String line;
			while ((line = rd.readLine()) != null)
				responseStr += line;
			
			//DISPLAY OUTPUT ON CONSOLE FOR DEBUG
			if(SHOW_DEBUG_MESSAGES)
			{
				System.out.println("\n(===  ONLINE COMMUNICATION  ===)");
				System.out.println("Request to the Server:");
	            System.out.println(OnlineRequests.gson.toJson(message));
				
				System.out.println("Reponse from the Server: \n" + responseStr);
				System.out.println("(=============================)\n");
			}
			
			
			return responseStr;					//return the response string if successful
		}
		catch (IOException e)	/* catch IO Exception if necessary */
		{
			e.printStackTrace();
			return null;							//otherwise, return null
		}
	}	
	
	public static PlayerMessage getLobbiesMsg(OnlineSession online)
	{
		PlayerMessage returnMsg = new PlayerMessage(online);
		returnMsg.messageType = MessageType.GET_LOBBIES;
		return returnMsg;
	}
	
	public static PlayerMessage hostLobbyMsg(OnlineSession online)
	{
		PlayerMessage returnMsg = new PlayerMessage(online);
		returnMsg.messageType = MessageType.HOST_LOBBY;
		returnMsg.gameStyle = online.gameStyle;
		return returnMsg;
	}
	
	public static PlayerMessage joinLobbyMsg(OnlineSession online, int lId)
	{
		PlayerMessage returnMsg = new PlayerMessage(online);
		returnMsg.messageType = MessageType.JOIN_LOBBY;
		returnMsg.lobbyId = lId;
		return returnMsg;
	}
	
	public static PlayerMessage lobbyStatusMsg(OnlineSession online, int lobbyId)
	{
		PlayerMessage returnMsg = new PlayerMessage(online);
		returnMsg.messageType = MessageType.LOBBY_STATUS;
		returnMsg.lobbyId = lobbyId;
		return returnMsg;
	}
	
	public static PlayerMessage startGameMsg(OnlineSession online, GameDataMessage gD)
	{
		PlayerMessage returnMsg = new PlayerMessage(online);
		returnMsg.messageType = MessageType.START_GAME;
		returnMsg.lobbyId = online.lobbyId;
		returnMsg.gameData = gD;
		return returnMsg;
	}
	
	public static PlayerMessage dropGameMsg(OnlineSession online)
	{
		PlayerMessage returnMsg = new PlayerMessage(online);
		returnMsg.messageType = MessageType.DROP_GAME;
		returnMsg.lobbyId = online.lobbyId;
		return returnMsg;
	}
	
	public static PlayerMessage rageQuitMsg(OnlineSession online)
	{
		PlayerMessage returnMsg = new PlayerMessage(online);
		returnMsg.messageType = MessageType.RAGE_QUIT;
		returnMsg.lobbyId = online.lobbyId;
		return returnMsg;
	}
	
	public static PlayerMessage continueGameMsg(OnlineSession online)
	{
		PlayerMessage returnMsg = new PlayerMessage(online);
		returnMsg.messageType = MessageType.CONTINUE_GAME;
		returnMsg.lobbyId = online.lobbyId;
		return returnMsg;
	}
	
	public static PlayerMessage playAgainMsg(OnlineSession online, boolean playAgain)
	{
		PlayerMessage returnMsg = new PlayerMessage(online);
		returnMsg.messageType = MessageType.PLAY_AGAIN;
		returnMsg.lobbyId = online.lobbyId;
		returnMsg.anotherGame = (playAgain ? 1 : 0);
		returnMsg.playerWinOrder = online.cardGame.playerWinOrder;
		return returnMsg;
	}
	
	public static PlayerMessage kickPlayerMsg(OnlineSession online, String player)
	{
		PlayerMessage returnMsg = new PlayerMessage(online);
		returnMsg.messageType = MessageType.KICK_PLAYER;
		returnMsg.playerName = player;
		return returnMsg;
	}

	public static PlayerMessage getGameDataMsg(OnlineSession online)
	{
		PlayerMessage returnMsg = new PlayerMessage(online);
		returnMsg.messageType = MessageType.GET_GAME_DATA;
		returnMsg.lobbyId = online.lobbyId;
		return returnMsg;
	}
	
	public static PlayerMessage gameDataOKMsg(OnlineSession online)
	{
		PlayerMessage returnMsg = new PlayerMessage(online);
		returnMsg.messageType = MessageType.GAME_DATA_OK;
		returnMsg.lobbyId = online.lobbyId;
		return returnMsg;
	}
	
	public static PlayerMessage setGameDataMsg(OnlineSession online, GameDataMessage gameData)
	{
		PlayerMessage returnMsg = new PlayerMessage(online);
		returnMsg.messageType = MessageType.SET_GAME_DATA;
		returnMsg.lobbyId = online.lobbyId;
		returnMsg.gameData = gameData;
		returnMsg.gameData.playerNumber = online.playerSlot;
		return returnMsg;
	}
}
