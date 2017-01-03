package loon.action.page;

import loon.Screen;
import loon.utils.MathUtils;

public class ZoomInPage extends BasePage{

	public void onTransform(Screen screen, float position) {
		final float scale = position < 0 ? position + 1f : MathUtils.abs(1f - position);
		screen.setScaleX(scale);
		screen.setScaleY(scale);
		screen.setPivotX(screen.getWidth() * 0.5f);
		screen.setPivotY(screen.getHeight() * 0.5f);
		screen.setAlpha(position < -1f || position > 1f ? 0f : 1f - (scale - 1f));
	}
}
