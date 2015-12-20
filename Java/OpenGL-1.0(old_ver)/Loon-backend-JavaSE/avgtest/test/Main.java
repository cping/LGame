package test;

import loon.LGame;
import loon.LSetting;
import loon.core.graphics.opengl.LTexture;

public class Main {

	public static void main(String[]args) {
		LTexture.ALL_LINEAR = true;
		LSetting setting = new LSetting();
		setting.width = 480;
		setting.height = 320;
		setting.fps = 60;
		setting.showFPS = true;
		LGame.register(setting, TitleScreen.class);
	}


}