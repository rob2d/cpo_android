package com.rob2d.android.chinesepoker.network;
/*  Filename:   LoginInfo.java
 *  Package:    com.rob2d.android.chinesepoker.network
 * 	Author:     Robert Concepcion III  */


/** after a login request, this object is returned from the JSON String-formatted response */
public class LoginInfo 
{
	public static final int LOGIN_VERIFIED = 1,
					   		   LOGIN_FAILED   = 0;
	
	public int 	   	 verified;
	public int 			userId = -1;
	public String	  userName = null;
	public String[]      error;
	
	public String getErrors()
	{
		String returnStr = "";
		if(error == null)
			return "Error connecting to the network!";
		else
			for(String e: error)
				returnStr += e + "\n";
		return returnStr;
	}
	public String toString()
	{
		String returnStr = new String();
		
		returnStr += "verified: " + verified + "\n" + (error != null ? "error: " : "");
		if(userId != -1)
			returnStr += "userId: " + userId + "\n";
		//append error message
		if(error != null)
			for(String e : error )
				returnStr += e + "\n";
					
		return returnStr;
	}
}
