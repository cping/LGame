package loon.action.page;

import loon.Screen;
import loon.utils.MathUtils;

public class DepthPage extends BasePage{

	private static final float MIN_SCALE = 0.75f;

	public void onTransform(Screen screen, float position) {
		if (position <= 0f) {
			screen.setX(0f);
			screen.setScaleX(1f);
			screen.setScaleY(1f);
		} else if (position <= 1f) {
			final float scaleFactor = MIN_SCALE + (1 - MIN_SCALE)
					* (1 - MathUtils.abs(position));
			screen.setAlpha(1 - position);
			screen.setX(screen.getWidth() * -position);
			screen.setScaleX(scaleFactor);
			screen.setScaleY(scaleFactor);
		}
	}

	public boolean isPagingEnabled() {
		return true;
	}
}
