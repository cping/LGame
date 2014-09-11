package com.example.ptest;

import loon.LGameAndroid2DActivity;

public class MainActivity extends LGameAndroid2DActivity {

	public void onMain() {

		LSetting setting = new LSetting();
		setting.width = 480;
		setting.height = 320;
		setting.showFPS = true;
		setting.landscape = false;
		setting.fps = 60;
		
		register(setting, Test1.class);
		//register(setting, Test2.class);

	}

	@Override
	public void onGamePaused() {

	}

	@Override
	public void onGameResumed() {

	}

}