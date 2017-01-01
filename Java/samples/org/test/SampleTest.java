package org.test;

import loon.LSetting;
import loon.LazyLoading;
import loon.Screen;
import loon.javase.Loon;

public class SampleTest  {


	public static void main(String[] args) {
		LSetting setting = new LSetting();
		setting.isDebug = true;
		setting.isDisplayLog = true;
		setting.isLogo = true;
		setting.logoPath = "loon_logo.png";
		// 原始大小
		setting.width = 480;
		setting.height = 320;
		// 缩放为
		setting.width_zoom = 640;
		setting.height_zoom = 480;
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "test";
		setting.emulateTouch = false;
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				//此Screen位于sample文件夹下，引入资源即可加载
				return new MultiScreenTest();
			}
		});
	}
}
