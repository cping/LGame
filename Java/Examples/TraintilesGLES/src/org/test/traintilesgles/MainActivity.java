package org.test.traintilesgles;

import loon.LGame;
import loon.core.graphics.opengl.LTexture;

public class MainActivity extends LGame {

	@Override
	public void onGamePaused() {

	}

	@Override
	public void onGameResumed() {

	}

	@Override
	public void onMain() {
		LTexture.ALL_LINEAR=true;
		LSetting setting = new LSetting();
		setting.width = 800;
		setting.height = 480;
		setting.showFPS = false;
		setting.landscape = true;
		setting.fps = 30;
		register(setting, GameMain.class);
	}

}
