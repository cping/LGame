package com.example.arpg_gles;

import loon.LGame;
import loon.core.graphics.opengl.LTexture;

public class MainActivity extends LGame {

	public void onGamePaused() {

	}

	public void onGameResumed() {

	}

	public void onMain() {
		LTexture.ALL_LINEAR = true;
		LSetting setting = new LSetting();
		setting.width = 800;
		setting.height = 600;
		setting.fps = 60;
		setting.landscape = true;
		setting.showFPS = false;
		register(setting, Game1.class);
	}

}
