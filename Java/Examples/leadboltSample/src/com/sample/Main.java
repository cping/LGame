
package com.sample;

import com.brqylpxergjmuimbvnka.AdController;
import com.brqylpxergjmuimbvnka.AdListener;

import loon.LGame;
import loon.LSetting;
import android.app.Activity;
import android.os.Bundle;

public class Main extends LGame {

	@Override
	public void onGamePaused () {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGameResumed () {
		// TODO Auto-generated method stub

	}

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

			}
		};
		register(setting, GameScreen.class);
	
	}

}
