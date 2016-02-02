package org.test;

import org.test.wuziqi.WuzhiScreen;

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
		// 原始大小(这是一个第三方的示例，我修改成了loon中使用，所以初始大小不太合适手机环境，请自行修改大小……)
		setting.width = 800;
		setting.height = 650;
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "五子棋";
		setting.emulateTouch = false;
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new WuzhiScreen();
			}
		});
	
	}
}
