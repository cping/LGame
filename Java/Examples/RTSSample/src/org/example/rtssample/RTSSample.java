package org.example.rtssample;

import org.test.GameMain;

import loon.LGame;
import loon.core.graphics.opengl.LTexture;

public class RTSSample extends LGame {

	@Override
	public void onGamePaused() {

	}

	@Override
	public void onGameResumed() {

	}

	@Override
	public void onMain() {
		LTexture.ALL_LINEAR = true;
		LSetting setting = new LSetting();
		setting.showFPS = false;
		setting.landscape = true;
		setting.fps = 30;
		setting.width = 480;
		setting.height = 360;
		register(setting, GameMain.class);
	}

}
