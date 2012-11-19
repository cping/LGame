package game.test;

import loon.LGame;
import loon.core.graphics.opengl.LTexture;

public class LLK_GLESActivity extends LGame {

	public void onGamePaused() {

	}

	public void onGameResumed() {

	}

	public void onMain() {
		LTexture.ALL_LINEAR = true;
		LSetting setting = new LSetting();
		setting.width = 480;
		setting.height = 320;
		setting.showFPS = true;
		setting.landscape = true;
		setting.fps = 60;
		register(setting, LLKScreen.class);
	}

}