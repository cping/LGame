package com.cat.puzzle.game_free;

import loon.LGame;

public class CatPuzzleActivity extends LGame {

	public void onMain() {
		LSetting setting = new LSetting();
		setting.width = 640;
		setting.height = 480;
		setting.landscape = true;
		setting.showFPS = false;
		register(setting, Puzzle.class);
	}

	public void onGamePaused() {

	}

	public void onGameResumed() {

	}

}