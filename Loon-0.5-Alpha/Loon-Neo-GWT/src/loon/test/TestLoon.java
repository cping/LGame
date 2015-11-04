package loon.test;

import loon.Screen;
import loon.html5.gwt.GWTGame.GWTSetting;
import loon.html5.gwt.Loon;
import loon.html5.gwt.preloader.LocalAssetResources;

public class TestLoon extends Loon {

	@Override
	public void onMain() {

		GWTSetting setting = new GWTSetting();
		setting.fps = 60;
		// original size
		setting.width = 480;
		setting.height = 320;
		// show size
		setting.width_zoom = 640;
		setting.height_zoom = 480;
		setting.isFPS = true;
		setting.fontName = "黑体";
		setting.rootId = "embed-loon.test.TestLoon";
		// 如果想要完全本地运行页面，在任意浏览器都能静态运行，而不通过服务器，只能提前写好资源路径……（否则，部分浏览器不能读txt之类的本地文件，只能放在服务器才行）
	
	//	setting.internalRes = MyResource.getResource();

		register(setting, new Data() {

			@Override
			public Screen onScreen() {
				return new TitleScreen();
			}
		});

	}

}
