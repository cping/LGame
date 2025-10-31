package org.test;

import org.test.cat.puzzle.game.Puzzle;

import loon.LSetting;
import loon.LazyLoading;
import loon.Screen;
import loon.javase.Loon;

public class JavaSEMain {

	public static void main(String[]args){

		LSetting setting = new LSetting();
		setting.isFPS = true;
		setting.isLogo = false;
		setting.logoPath = "loon_logo.png";
		// 原始大小
		setting.width = 640;
		setting.height = 480;
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "拼图游戏";
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				//此Screen位于sample文件夹下，引入资源即可加载
				return new Puzzle();
			}
		});
	
	}
	
}
