package com.example.caveracegamegles;

import loon.LGame;

import org.test.GameMain;

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
		setting.width = 800;
		setting.height = 480;
		setting.fps = 40;
		setting.showFPS = false;
		setting.landscape = true;
		register(setting, GameMain.class);

	}

}
