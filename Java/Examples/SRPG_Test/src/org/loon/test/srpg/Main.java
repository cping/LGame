package org.loon.test.srpg;

import loon.JavaApp.LSetting;
import loon.LGame;
import loon.core.graphics.opengl.LTexture;

public class Main {
	
	public static void main(String[] args) {
		LTexture.ALL_LINEAR = true;
		LSetting setting = new LSetting();
		setting.width = 480;
		setting.height = 320;
		setting.showFPS = true;
		setting.resizable = false;
		setting.fps = 60;
		LGame.register(setting, MySRPGScreen.class);
	}
	
}