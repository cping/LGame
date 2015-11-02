package test;

import loon.LSetting;
import loon.LazyLoading;
import loon.Screen;
import loon.javase.Loon;

public class Main {

	public static void main(String[]args) {
		LSetting setting = new LSetting();
		//实际大小:
		setting.width = 480;
		setting.height = 320;
		//缩放显示为:
		//setting.width_zoom = 640;
		//setting.height_zoom = 480;
		//setting.fullscreen = true;
		setting.fps = 60;
		setting.isFPS = true;
		setting.fontName = "黑体";
		setting.isLogo = false;
		Loon.register(setting, new LazyLoading.Data() {
			
			@Override
			public Screen onScreen() {
				return new TitleScreen();
			}
		});
	}


}