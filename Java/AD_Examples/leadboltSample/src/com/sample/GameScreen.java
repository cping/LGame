package com.sample;

import android.app.Activity;

import com.brqylpxergjmuimbvnka.AdController;
import com.brqylpxergjmuimbvnka.AdListener;

import loon.core.LSystem;
import loon.core.graphics.Screen;
import loon.core.graphics.opengl.GLEx;
import loon.core.input.LTouch;
import loon.core.timer.LTimerContext;

public class GameScreen extends Screen {
	
	public void onLoad(){
		Runnable runnable = new Runnable() {
			
			@Override
			public void run () {
				final Activity act = LSystem.screenActivity;
				AdController myController = new AdController(act, "MY_LB_SECTION_ID_STD", "MY_LB_SECTION_ID_MED", "MY_LB_SECTION_ID_LRG",
					"MY_LB_SECTION_ID_XLRG", new AdListener() {
						public void onAdLoaded () {
						}

						public void onAdClicked () {
						}

						public void onAdClosed () {
							act.finish();
						}

						public void onAdCompleted () {
							act.finish();
						}

						public void onAdFailed () {
							act.finish();
						}

						public void onAdProgress () {
						}

						public void onAdAlreadyCompleted () {
							act.finish();
						}

						public void onAdHidden () {
						}

						public void onAdPaused () {
							act.finish();
						}

						public void onAdResumed () {
						}
					});
				myController.loadAd();
				
			}
		};
		LSystem.runOnUiThread(runnable);
	}

	@Override
	public void draw (GLEx g) {
		// TODO Auto-generated method stub

	}

	@Override
	public void alter (LTimerContext timer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchDown (LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchUp (LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchMove (LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchDrag (LTouch e) {
		// TODO Auto-generated method stub

	}

}
