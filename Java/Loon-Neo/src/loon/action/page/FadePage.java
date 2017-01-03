package loon.action.page;

import loon.Screen;

public class FadePage extends BasePage {

	public void onTransform(Screen screen, float position) {
		if (position < -1 || position > 1) {
			screen.setAlpha(0.6f);
		} else if (position <= 0 || position <= 1) {
			float alpha = (position <= 0) ? position + 1 : 1 - position;
			screen.setAlpha(alpha);
		} else if (position == 0) {
			screen.setAlpha(1f);
		}
	}
}
