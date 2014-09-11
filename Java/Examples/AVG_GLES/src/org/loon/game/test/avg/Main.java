package org.loon.game.test.avg;

import loon.LGame;
import loon.core.graphics.opengl.LTexture;

public class Main extends LGame {

	public void onMain() {
		LTexture.ALL_LINEAR = true;
		LSetting setting = new LSetting();
		setting.width = 480;
		setting.height = 320;
		setting.landscape = true;
		setting.fps = 60;
		setting.showFPS = true;
		register(setting, TitleScreen.class);
	}

	@Override
	public void onGamePaused() {

	}

	@Override
	public void onGameResumed() {

	}

}