package com.mygame;

import loon.LGame;
import loon.LSetting;
import loon.core.graphics.opengl.LTexture;

public class Main extends LGame {

	@Override
	public void onMain() {
		LTexture.ALL_LINEAR = true;
		LSetting setting = new LSetting();
		setting.width = 800;
		setting.height = 480;
		setting.showFPS = false;
		setting.landscape = true;
		setting.fps = 30;
		register(setting, GameMain.class);
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
