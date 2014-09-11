package com.example.lnodetest;

import loon.LGame;

public class MainActivity extends LGame {

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
		setting.width = 480;
		setting.height = 320;
		setting.fps = 60;
		setting.showFPS = true;
		register(setting, Test.class);
	}

    
}
