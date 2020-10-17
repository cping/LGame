package org.test;

import loon.LSetting;
import loon.LazyLoading;
import loon.Screen;
import loon.javase.JavaSEGame.JavaSetting;
import loon.javase.Loon;

public class TestTD {

	public static void main(String[] args) {
		JavaSetting setting = new JavaSetting();
		// 原始大小
		setting.width = 480;
		setting.height = 320;
		setting.logoPath = "loon_logo.png";
		setting.isFPS = true;
	      setting.iconPaths = new String[]{"l.png"};
		//默认字体
		setting.fontName = "黑体";
		setting.emulateTouch  = true;
		/**
		 * 载入初始化的Screen(此处最初版本都是走反射实现，但为了移植方便，所以基本放弃反射……)
		 */
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new TDTest();
			}
		});
	}
}
