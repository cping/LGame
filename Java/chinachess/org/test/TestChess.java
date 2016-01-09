package org.test;

import loon.LSetting;
import loon.LazyLoading;
import loon.Screen;
import loon.javase.Loon;

public class TestChess {

	public static void main(String[]args){

		LSetting setting = new LSetting();
		setting.isFPS = true;
		setting.isLogo = false;
		setting.logoPath = "loon_logo.png";
		// 原始大小(这是一个第三方的示例，我修改成了loon中使用，所以初始大小不太合适手机环境……)
		setting.width = 700;
		setting.height = 712;
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "test";
		setting.emulateTouch = false;
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new GameView();
			}
		});
	
	}
	
}
