package org.loon.test.srpg;

import loon.LGame;
import loon.core.graphics.opengl.LTexture;

public class Main extends LGame {

	public void onMain() {
		LTexture.ALL_LINEAR = true;
		LSetting setting = new LSetting();
		setting.width = 480;
		setting.height = 320;
		setting.showFPS = true;
		setting.landscape = true;
		setting.fps = 60;
		register(setting, MySRPGScreen.class);
	}

	public void onGamePaused() {

	}

	public void onGameResumed() {

	}
}