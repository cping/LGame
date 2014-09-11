package org.loon.stg.sample;

import loon.LGame;
import loon.core.graphics.opengl.LTexture;

public class STG_GLESActivity extends LGame {

	public void onGamePaused() {

	}

	public void onGameResumed() {

	}

	public void onMain() {
		LTexture.ALL_LINEAR = true;
		LSetting setting = new LSetting();
		setting.width = 320;
		setting.height = 480;
		setting.fps = 60;
		setting.showFPS = true;
		setting.landscape = false;
		register(setting, Test.class, "assets/stage1.txt");
	}

}