package com.example.minidefence;

import loon.LGame;

import org.test.KingdomDefence;


public class MainActivity extends LGame {

	public void onGamePaused() {

	}

	public void onGameResumed() {

	}

	public void onMain() {
		LSetting setting = new LSetting();
		setting.width = 800;
		setting.height = 480;
		setting.fps = 30;
		setting.showFPS = false;
		setting.landscape = true;
		register(setting, KingdomDefence.class);
	}

}
