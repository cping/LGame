package org.test.lianliankan;

import loon.LSetting;
import loon.LazyLoading;
import loon.Screen;
import loon.javase.Loon;

public class JavaSEMain {
	public static void main(String[] args) {

		//此示例使用了线程，暂时无法使用在HTML5环境下（准备开TeaVM版，所以最终是能跑的）
		LSetting setting = new LSetting();
		setting.isFPS = true;
		setting.isLogo = false;
		setting.logoPath = "loon_logo.png";
		setting.width = 480;
		setting.height = 320;
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "连连看";
		setting.emulateTouch = false;
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new LLKScreen();
			}
		});

	}
}
