package com.example.jewelcraftgles;

import org.test.MainGame;

import loon.LGame;
import loon.core.LSystem;
import loon.core.graphics.opengl.LTexture;

public class MainActivity extends LGame {

	public void onGamePaused() {

	}

	public void onGameResumed() {

	}

	public void onMain() {
		LSystem.isBackLocked = true;
		LTexture.ALL_LINEAR = true;
		LSetting setting = new LSetting();
		setting.width = 480;
		setting.height = 800;
		setting.fps = 40;
		setting.showFPS = false;
		setting.landscape = false;
		register(setting, MainGame.class);
	}

}
