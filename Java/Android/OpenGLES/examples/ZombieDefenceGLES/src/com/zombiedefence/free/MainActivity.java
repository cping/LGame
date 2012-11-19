package com.zombiedefence.free;

import loon.LGame;
import loon.core.LSystem;
import loon.core.graphics.opengl.LTexture;

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
		LSystem.isBackLocked = true;
		LTexture.ALL_LINEAR = true;
		LSetting setting = new LSetting();
		setting.width = 800;
		setting.height = 480;
		setting.fps = 30;
		setting.showFPS = false;
		setting.landscape = true;
		register(setting, MainGame.class);

	}

}
