package com.mygame;

import loon.LGame;
import loon.LSetting;
import loon.core.graphics.opengl.LTexture;

public class Main extends LGame {

	@Override
	public void onMain() {
		LTexture.ALL_LINEAR = true;
		LSetting setting = new LSetting();
		setting.width = 320;
		setting.height = 480;
		setting.fps = 60;
		setting.showFPS = true;
		setting.landscape = false;
		register(setting, Test.class, "assets/stage1.txt");
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
