package com.rob2d.android.framework.impl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.rob2d.android.chinesepoker.Assets;
import com.rob2d.android.framework.Audio;
import com.rob2d.android.framework.FileIO;
import com.rob2d.android.framework.Game;
import com.rob2d.android.framework.Graphics;
import com.rob2d.android.framework.Input;
import com.rob2d.android.framework.Music;
import com.rob2d.android.framework.GameSettings;
import com.rob2d.android.framework.Screen;
import com.rob2d.android.textui.Prompter;
import com.rob2d.android.textui.PrompterAndroid;

/** Copyright 2011 Robert Concepcion III */
public abstract class AndroidGame extends Activity implements Game
{
	public final AndroidGame game = this;
	AndroidFastRenderView renderView;
	Graphics graphics;
	Audio audio;
	Input input;
	FileIO fileIO;
	Screen screen;
	WakeLock wakeLock;
	Music musicHandler = new AndroidMusic(this);
	Thread mainUIThread;
	ProgressDialog progressDialog;
	PrompterAndroid prompter;
	protected GameSettings options;
	
	public Handler handler;
	public boolean mainRunning;
	public boolean paused;
	
	public SharedPreferences gamePrefs;
	public SharedPreferences.Editor prefEditor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		boolean isLandscape = getResources().getConfiguration().orientation ==
			Configuration.ORIENTATION_LANDSCAPE;
		
		prompter = new PrompterAndroid(this);
		
		int SCREENWIDTH = 800;
		int SCREENHEIGHT = 480;
		
		//determine the virtual size of the game screen
		int frameBufferWidth = isLandscape ? SCREENWIDTH : SCREENHEIGHT;
		int frameBufferHeight = isLandscape ? SCREENHEIGHT : SCREENWIDTH;
		//create the frame buffer to draw on
		Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth,
				frameBufferHeight, Config.RGB_565);
		
		float gameScreenRatio = frameBufferWidth/frameBufferHeight;
		
		//get the phone's screen dimensions
		float screenAspectRatio = getWindowManager().getDefaultDisplay().getWidth() /
							getWindowManager().getDefaultDisplay().getHeight();
		
		float scaleX, scaleY;
		//if the device screen is wider than the game's screen ratio, 
		//base scaling on Y(so that X will give horizontal black bars)
			scaleY = (float) frameBufferHeight
			/ getWindowManager().getDefaultDisplay().getHeight();

			scaleX = (float) frameBufferWidth
			/ getWindowManager().getDefaultDisplay().getWidth();
		
		musicHandler = new AndroidMusic(this);
		renderView = new AndroidFastRenderView(this, frameBuffer);
		graphics = new AndroidGraphics(getAssets(), frameBuffer);
		fileIO = new AndroidFileIO(getAssets());
		audio = new AndroidAudio(this, this);
		input = new AndroidInput(this, renderView, scaleX, scaleY);
		screen = getStartScreen();
		setContentView(renderView);
		
		PowerManager powerManager = (PowerManager)
			getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GLGame");
		
	    // preparing a looper on current thread
	    // the current thread is being detected implicitly
		//Looper.prepare();
		// Create the Handler. It will implicitly bind to the Looper
	    // that is internally created for this thread (since it is the UI thread)
	    handler = new Handler();
	    
		//load preferences to work with
		gamePrefs = getSharedPreferences(Assets.PREFERENCE_FILENAME, MODE_APPEND);
		prefEditor = gamePrefs.edit();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();

		musicHandler.resume();
		wakeLock.acquire();
		screen.resume();
		renderView.resume();	
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		musicHandler.pause();
		
		if(isFinishing())
			musicHandler.dispose();
		
		wakeLock.release();
		renderView.pause();
		screen.pause();
		
		if(isFinishing())
			screen.dispose();
	}
	
	@Override
	public Input getInput()
	{
		return input;
	}
	
	@Override
	public FileIO getFileIO()
	{
		return fileIO;
	}
	
	@Override
	public Graphics getGraphics()
	{
		return graphics;
	}
	
	@Override
	public Audio getAudio()
	{
		return audio;
	}
	
	@Override
	public void setScreen(Screen screen)
	{
		if(screen == null)
			throw new IllegalArgumentException("Screen must not be null");
		
		this.screen.pause();
		this.screen.dispose();
		this.screen = null;
		screen.resume();
		screen.update(0);	//update the new screen w no delay before we set the current screen
		this.screen = screen;
		Log.d("CSDEBUG", "just changed the screen");
	}
	
	public SurfaceView getRenderView()
	{
		return renderView;
	}
	
	@Override
	public Screen getCurrentScreen()
	{
		return screen;
	}
	
	@Override
	public Music getMusicHandler()
	{
		return musicHandler;
	}
	
	/* check whether the game sequence is running or not */
	@Override
	public boolean isMainRunning()
	{
		return mainRunning;
	}
	/* check whether the game is paused or not */
	public boolean isPaused()
	{
		return paused;
	}
	
	@Override
	public void launchWebsite(String url)
	{
		Intent i = new Intent(Intent.ACTION_VIEW);  
		i.setData(Uri.parse(url));  
		this.startActivityFromChild(this, i, 0);
	}
	
	@Override
	public void startLoadingDialog(final String msg, final String title, final boolean cancelable)
	{
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				if(!cancelable)
					progressDialog= new ProgressDialog(game);
				else
					progressDialog = new ProgressDialog(game)
				{
					@Override
					public void onBackPressed()
					{
						getCurrentScreen().onLoadingDialogCanceled();
					}
				};
				progressDialog.setIndeterminate(true);
				if(!cancelable)
					progressDialog.setCancelable(false);
				else
					progressDialog.setCancelable(true);
				progressDialog.setTitle(title);
				progressDialog.setMessage(msg);
				progressDialog.show();
			}
			
		});
	}
	
	@Override
	public void stopLoadingDialog()
	{
		progressDialog.dismiss();
	}
	
	@Override
	public Prompter getPrompter()
	{
		return prompter;
	}
}	
