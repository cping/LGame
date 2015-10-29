package loon.test;

import loon.Screen;
import loon.html5.gwt.GWTGame.Config;
import loon.html5.gwt.Loon;

public class TestLoon extends Loon {

	@Override
	public void onMain() {
	
		Config setting = new Config();
		setting.fps = 60;
		setting.width  = 480;
		setting.height = 320;
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
