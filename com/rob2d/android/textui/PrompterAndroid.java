package com.rob2d.android.textui;

import android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.view.ContextThemeWrapper;
import android.widget.EditText;

import com.rob2d.android.framework.impl.AndroidGame;

/** Prompt dialog for android applications*/
public class PrompterAndroid implements Prompter
{
	/** whether a prompt was given */
	public boolean promptGiven;
	/** whether the user has given input from a given prompt */
	public boolean promptCompleted;
	/** what a user has entered for a prompt */
	public String userEntry = null;
	/** what a user has entered a prompt's title */
	public String promptTitle;
	/** message to be displayed during a prompt */
	public String promptMessage;	
	/** used if we want to return a value from a method depending on a button clicked */
	public int returnedValue;
	
	/** keeps track of the application to launch the prompter from */
	public final Context context;
	
	
	public PrompterAndroid(Context c)
	{
		super();
		context = c;
	}

	public String showInputPrompt(String message, String title)
	{	
		final EditText inputTxt = new EditText(context);
		inputTxt.setLines(1);
		inputTxt.setMinHeight(30);
		promptTitle 	 = title;
		promptMessage = message;
		promptCompleted = false;
		userEntry = "";
		
		Thread t = new Thread()
		{
			@Override
			public void run()
			{
				new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.Theme_Dialog))
			    //.setTitle(promptTitle)
			    .setMessage(promptMessage)
			    .setView(inputTxt)
			    .setCancelable(false)
			    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int whichButton) 
			        {
						promptGiven = false;
						{
							userEntry 			= inputTxt.getText().toString();
							promptCompleted 	= true;
						}
			        }
			    })
			    .create().show();
				Looper.loop();
				Looper.myLooper().quit();
			}
		};
		((AndroidGame)context).handler.post(t);
		//do not allow any more processing on the main thread until a user has completed prompting sequence!
		while(!promptCompleted)
		{};
		promptCompleted = false;	
			return userEntry;
	}


	@Override
	public void showMsg(String promptMsg, String titleMsg)
	{
		final EditText inputTxt = new EditText(context);
		promptCompleted = false;
		promptTitle 	= titleMsg;
		promptMessage   = promptMsg;
		
		inputTxt.setLines(1);
		Thread t = new Thread()
		{
			@Override
			public void run()
			{
				new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.Theme_Dialog))
			    //.setTitle(promptTitle)
			    .setMessage(promptMessage)
			    .setCancelable(false)
			    .setPositiveButton("OK", new DialogInterface.OnClickListener() 
			    {
			        public void onClick(DialogInterface dialog, int whichButton) 
			        {
						promptCompleted 	= true;
			        }
			    })
			    .create().show();
				Looper.loop();
				Looper.myLooper().quit();
			}
		};
		((AndroidGame)context).handler.post(t);
		promptCompleted = false;
		//do not allow any more processing on the current thread until a user has completed prompting sequence!
		while(!promptCompleted)
		{};
	}

	/** prompt user between 2 choices. if the first is chosen, true is returned. otherwise, false is returned */
	@Override
	public boolean showDualOption(String promptMsg, String titleMsg,
			String b1T, String b2T)
	{
		promptMessage = promptMsg;
		final String button1Txt = b1T;
		final String button2Txt = b2T;
		promptCompleted = false;
		Thread t = new Thread()
		{
			@Override
			public void run()
			{
				new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.Theme_Dialog))
			    //.setTitle(promptTitle)
			    .setMessage(promptMessage)
			    .setCancelable(false)
			    .setPositiveButton(button1Txt, new DialogInterface.OnClickListener() 
			    {
			        public void onClick(DialogInterface dialog, int whichButton) 
			        {
						promptCompleted 	= true;
						returnedValue = 1;
			        }
			    })
			    .setNegativeButton(button2Txt,  new DialogInterface.OnClickListener(){
			    	public void onClick(DialogInterface dialog, int whichButton)
			    	{
			    		promptCompleted = true;
			    		returnedValue 		= 2;
			    	}
			    })
			    .create().show();
				Looper.loop();
				Looper.myLooper().quit();
			}
			};
				
			((AndroidGame)context).handler.post(t);
			//do not allow any more processing on the main thread until a user has completed prompting sequence!
			while(!promptCompleted)
			{};
			promptCompleted = false;	
	
			if(returnedValue == 1)
				return true;
			else
				return false;
	}

	@Override
	public int showTripleOption(String promptMsg, String titleMsg,
			String b1T, String b2T, String b3T)
	{
		promptMessage = promptMsg;
		final String button1Txt = b1T;
		final String button2Txt = b2T;
		final String button3Txt = b3T;
		promptCompleted = false;
		
		Thread t = new Thread()
		{
			@Override
			public void run()
			{
				new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.Theme_Dialog))
			    //.setTitle(promptTitle)
			    .setMessage(promptMessage)
			    .setCancelable(false)
			    .setPositiveButton(button1Txt, new DialogInterface.OnClickListener() 
			    {
			        public void onClick(DialogInterface dialog, int whichButton) 
			        {
						promptCompleted 	= true;
						returnedValue = 1;
			        }
			    })
			    .setNeutralButton(button2Txt, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
			    		promptCompleted = true;
			    		returnedValue = 2;
					}
			    })
			    .setNegativeButton(button3Txt,  new DialogInterface.OnClickListener(){
			    	public void onClick(DialogInterface dialog, int whichButton)
			    	{
			    		promptCompleted = true;
			    		returnedValue = 3;
			    	}
			    })
			    .create().show();
				Looper.loop();
				Looper.myLooper().quit();
			}
			};
				
			((AndroidGame)context).handler.post(t);
			//do not allow any more processing on the main thread until a user has completed prompting sequence!
			while(!promptCompleted)
			{};
			promptCompleted = false;	
	
			return returnedValue;
	}
}
