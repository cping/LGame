package org.test.rosengles;

import loon.LGame;

import org.test.base.GameScreen;


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
		setting.fps = 30;
		setting.title = "Rosen";
		setting.landscape = true;
		register(setting, GameScreen.class);

	}

}
