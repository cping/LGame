package com.mygame;

import loon.LGame;
import loon.LSetting;

public class Main extends LGame {

	@Override
	public void onMain() {
		LSetting setting = new LSetting();
		setting.showFPS = false;
		setting.landscape = true;
		setting.fps = 30;
		setting.width = 480;
		setting.height = 360;
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
