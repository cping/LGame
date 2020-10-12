package loon.test;

import loon.Screen;
import loon.html5.gwt.GWTGame.GWTSetting;
import loon.html5.gwt.GWTGame.Repaint;
import loon.html5.gwt.Loon;

public class TestLoon extends Loon {

	@Override
	public void onMain() {

		GWTSetting setting = new GWTSetting();
		setting.fps = 60;
		setting.isDebug = true;
		setting.isDisplayLog = false;
		// source size
		setting.width = 480;
		setting.height = 320;
		// target size
		setting.width_zoom = 640;
		setting.height_zoom = 480;
		setting.repaint = Repaint.AnimationScheduler;
		setting.isFPS = true;
		setting.fontName = "黑体";
		setting.isConsoleLog = true;
		setting.rootId = "embed-loon.test.TestLoon";
		// 按屏幕缩放比例缩放
		// setting.useRatioScaleFactor = true;
		// 当此项开启，并且gwt.xml中设置了loon.addtojs为true,会默认从js中加载资源
		setting.jsloadRes = false;

		// 设置一个需要的初始化进度条样式（不填则默认）
		// setting.progress = GWTProgressDef.newSimpleLogoProcess(setting);

		register(setting, new Data() {

			@Override
			public Screen onScreen() {
				return new TitleScreen();
			}
		});

	}

}
