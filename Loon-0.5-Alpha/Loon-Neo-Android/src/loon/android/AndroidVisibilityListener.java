package loon.android;

import loon.LSystem;
import android.view.View;

public class AndroidVisibilityListener {
	
	public void createListener (final AndroidBase application) {
		try {
			View rootView = application.getApplicationWindow().getDecorView();
			rootView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
				@Override
				public void onSystemUiVisibilityChange (int arg) {
					application.getHandler().post(new Runnable() {
						@Override
						public void run () {
							application.setImmersiveMode(true);
						}
					});
				}
			});
		} catch (Throwable t) {
			LSystem.base().log().debug("AndroidApplication", "Can't create OnSystemUiVisibilityChangeListener, unable to use immersive mode.", t);
		}
	}
	
}
