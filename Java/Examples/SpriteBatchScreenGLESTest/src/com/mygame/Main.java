package com.mygame;

import loon.LGame;
import loon.LSetting;
import loon.core.graphics.opengl.LTexture;

public class Main extends LGame {

	@Override
	public void onMain() {
		LTexture.ALL_LINEAR = true;
		LSetting setting = new LSetting();
		setting.width = 480;
		setting.height = 320;
		setting.showFPS = true;
		setting.landscape = true;
		setting.fps = 60;
		register(setting, GameMapTest.class);
	}

	@Override
	public void onGameResumed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGamePaused() {
		// TODO Auto-generated method stub
		
	}


}
