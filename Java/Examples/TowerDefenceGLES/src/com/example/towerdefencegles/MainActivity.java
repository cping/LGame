package com.example.towerdefencegles;

import org.test.MainGame;

import loon.LGame;
import loon.core.graphics.opengl.LTexture;


public class MainActivity extends LGame {

	@Override
	public void onGamePaused() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGameResumed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMain() {
		LTexture.ALL_LINEAR = true;
		LSetting setting = new LSetting();
		setting.width = 320;
		setting.height = 480;
		setting.showFPS = false;
		setting.fps = 30;
		setting.landscape = false;
		register(setting, MainGame.class);
		
	}

}
