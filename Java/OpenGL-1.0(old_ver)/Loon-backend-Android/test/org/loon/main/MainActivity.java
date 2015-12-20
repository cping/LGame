package org.loon.main;

import test.TitleScreen;
import loon.LGame;
import loon.LSetting;
import loon.LTouch;
import loon.core.graphics.Screen;
import loon.core.graphics.opengl.GLEx;
import loon.core.timer.LTimerContext;


public class MainActivity extends LGame {

	@Override
	public void onMain() {
		LSetting setting = new LSetting();
		//横屏或竖屏
		setting.landscape = true;
		setting.width = 480;
		setting.height = 320;
		setting.showFPS = true;
		setting.showLogo = false;
		//注入初始Screen
	    register(setting,TitleScreen.class);
	}

	@Override
	public void onGameResumed() {

		
	}

	@Override
	public void onGamePaused() {

	}

}
