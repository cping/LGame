package org.test;

import com.mygame.GameMapTest;

import loon.LSetting;
import loon.LazyLoading;
import loon.Screen;
import loon.javase.Loon;
import test.TitleScreen;

public class TestACT {

	public static void main(String[] args) {
		LSetting setting = new LSetting();
		// 原始大小
		setting.width = 480;
		setting.height = 320;
		// 要求显示的大小
		setting.width_zoom = 640;
		setting.height_zoom = 480;
		setting.logoPath = "loon_logo.png";
		setting.isFPS = true;
		//默认字体
		setting.fontName = "黑体";
		/**
		 * 载入初始化的Screen(此处最初版本都是走反射实现，但为了移植方便，所以基本放弃反射……)
		 */
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new GameMapTest();
			}
		});
	}

}
