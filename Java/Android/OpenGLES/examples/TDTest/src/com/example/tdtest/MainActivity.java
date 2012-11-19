package com.example.tdtest;

import loon.LGame;
import loon.core.graphics.opengl.LTexture;

public class MainActivity extends LGame {

	public void onMain() {
		LTexture.ALL_LINEAR = true;
		LSetting setting = new LSetting();
		setting.width = 480;
		setting.height = 320;
		setting.fps = 60;
		setting.showFPS = true;
		setting.landscape = true;
		register(setting, Test.class);

	}

	public void onGamePaused() {

	}

	public void onGameResumed() {

	}

}
