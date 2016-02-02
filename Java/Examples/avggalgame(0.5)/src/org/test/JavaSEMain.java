package org.test;

import org.test.avg.TitleScreen;

import loon.LSetting;
import loon.LazyLoading;
import loon.Screen;
import loon.javase.Loon;

public class JavaSEMain {

	public static void main(String[] args) {
		LSetting setting = new LSetting();
		// 原始大小
		setting.width = 480;
		setting.height = 320;
		// 缩放大小
		setting.width_zoom = 640;
		setting.height_zoom = 480;
		setting.logoPath = "loon_logo.png";
		setting.isFPS = true;
		//默认字体
		setting.fontName = "黑体";
		setting.appName = "AVG游戏测试";
		/**
		 * 载入初始化的Screen(此处最初版本都是走反射实现，但为了移植方便，所以基本放弃反射……)
		 */
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new TitleScreen();
			}
		});
	}
}
