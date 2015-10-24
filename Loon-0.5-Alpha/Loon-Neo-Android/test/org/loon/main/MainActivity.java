package org.loon.main;

import loon.LSetting;
import loon.android.Loon;

public class MainActivity extends Loon {

	@Override
	public void onMain() {
		LSetting setting = new LSetting();
		setting.isFPS = true;
		setting.isLogo = false;
		setting.fullscreen = true;
		setting.logoPath = "loon_logo.png";
		setting.fps = 60;
		setting.fontName = "Dialog";
		setting.appName = "test";
		setting.emulateTouch = false;
		register(setting, ScreenTest.class);
	}

}
