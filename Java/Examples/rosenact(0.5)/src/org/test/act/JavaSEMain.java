package org.test.act;

import org.test.act.base.GameScreen;

import loon.LSetting;
import loon.LazyLoading;
import loon.Screen;
import loon.javase.Loon;

public class JavaSEMain {

	public static void main(String[] args) {
		LSetting setting = new LSetting();
		// 原始大小
		setting.width = 800;
		setting.height = 480;
		setting.logoPath = "loon_logo.png";
		setting.isFPS = true;
		//默认字体
		setting.fontName = "黑体";
		setting.appName = "动作游戏测试";
		/**
		 * 载入初始化的Screen(此处最初版本都是走反射实现，但为了移植方便，所以基本放弃反射……)
		 */
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new GameScreen();
			}
		});
	}
}
