package com.mygame;

import org.test.base.GameScreen;

import loon.LGame;
import loon.LSetting;

public class Main extends LGame{

	@Override
	public void onGamePaused() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGameResumed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMain() {
		LSetting setting = new LSetting();
		setting.width = 800;
		setting.height = 480;
		setting.fps = 30;
		setting.landscape = true;
		register(setting, GameScreen.class);
	
		
	}

}
