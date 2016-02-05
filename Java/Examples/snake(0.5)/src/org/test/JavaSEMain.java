package org.test;

import org.test.snake.MainMenuScreen;

import loon.LSetting;
import loon.LSystem;
import loon.LazyLoading;
import loon.Screen;
import loon.javase.Loon;

public class JavaSEMain {

	public static void main(String[]args){

		LSetting setting = new LSetting();
		setting.isFPS = true;
		setting.isLogo = false;
		setting.logoPath = "loon_logo.png";
		// 原始大小
		setting.width = 320;
		setting.height = 480;
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "贪食蛇游戏";
		LSystem.NOT_MOVE = true;
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new MainMenuScreen();
			}
		});
	
	}
	
}
