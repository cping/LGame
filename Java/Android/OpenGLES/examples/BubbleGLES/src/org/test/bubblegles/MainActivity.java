package org.test.bubblegles;

import org.test.Main;

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
		setting.width = 480;
		setting.height = 800;
		setting.showFPS = false;
		setting.fps = 30;
		register(setting, Main.class);
	}

}
