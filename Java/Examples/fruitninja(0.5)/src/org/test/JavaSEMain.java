package org.test;

import org.test.fruitninja.MainMenuScreen;

import loon.LSetting;
import loon.LSystem;
import loon.LazyLoading;
import loon.Screen;
import loon.javase.Loon;

public class JavaSEMain {

	public static void main(String[] args) {
		LSetting setting = new LSetting();
		setting.isFPS = true;
		setting.isLogo = false;
		setting.logoPath = "loon_logo.png";
		// 原始大小
		setting.width = 640;
		setting.height = 480;
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "切水果游戏";
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new MainMenuScreen();
			}
		});

	}

}
