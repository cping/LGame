package com.sample;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import loon.LGame;
import loon.LSetting;
import android.os.Bundle;

public class Main extends LGame implements AdListener {

	@Override
	public void onGamePaused () {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGameResumed () {
		// TODO Auto-generated method stub

	}

	AdView adview;

	@Override
	public void onMain () {

		LSetting setting = new LSetting();
		setting.width = 320;
		setting.height = 480;
		setting.title = "catpuzzle";
		setting.showFPS = true;
		setting.listener = new LSetting.Listener() {

			@Override
			public void onResume () {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPause () {
				// TODO Auto-generated method stub

			}

			@Override
			public void onExit () {
				if (adview != null) {
					adview.destroy();
				}

			}
		};
		register(setting, GameScreen.class);

		AdRequest request = new AdRequest();

		request.addTestDevice(AdRequest.TEST_EMULATOR);
		request.addTestDevice("E83D20734F72FB3108F104ABC0FFC738");

		adview = new AdView(this, AdSize.BANNER, "youid");

		addView(adview, Location.BOTTOM);

		adview.loadAd(request);

	}

	@Override
	public void onDismissScreen (Ad arg0) {

	}

	@Override
	public void onFailedToReceiveAd (Ad arg0, ErrorCode arg1) {

	}

	@Override
	public void onLeaveApplication (Ad arg0) {

	}

	@Override
	public void onPresentScreen (Ad arg0) {

	}

	@Override
	public void onReceiveAd (Ad arg0) {

	}

}
