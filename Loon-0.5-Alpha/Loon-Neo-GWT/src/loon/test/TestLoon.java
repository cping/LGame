package loon.test;

import loon.Screen;
import loon.html5.gwt.GWTGame.GWTSetting;
import loon.html5.gwt.Loon;

public class TestLoon extends Loon {

	@Override
	public void onMain() {
	
		GWTSetting setting = new GWTSetting();
		setting.fps = 60;
		//original size
		setting.width  = 480;
		setting.height = 320;
		//show size
		setting.width_zoom  = 640;
		setting.height_zoom = 480;
		setting.isFPS = true;
		setting.fontName = "黑体";
		setting.rootId = "embed-loon.test.TestLoon";
	
		register(setting, new Data() {
			
			@Override
			public Screen onScreen() {
				return new TitleScreen();
			}
		});
		
	}
	
}
