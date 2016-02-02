package org.test;

import org.doudizhu.test.Game;

import loon.LSetting;
import loon.LazyLoading;
import loon.Screen;
import loon.javase.Loon;

public class JavaSEMain {

	public static void main(String[] args) {

		LSetting setting = new LSetting();
		setting.isFPS = true;
		setting.isLogo = false;
		setting.logoPath = "loon_logo.png";
		setting.width = 480;
		setting.height = 320;
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "斗地主";
		setting.emulateTouch = false;
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new Game();
			}
		});

	}
}
