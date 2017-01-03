package loon.action.page;

import loon.Screen;
import loon.utils.MathUtils;

public class BTFPage extends BasePage{

	public void onTransform(Screen screen, float position) {
		final float width = screen.getWidth();
		final float scale = MathUtils.min(
				position < 0 ? 1f : MathUtils.abs(1f - position), 0.5f);
		screen.setScaleX(scale);
		screen.setScaleY(scale);
		screen.setX(position < 0 ? width * position : -width * position * 0.25f);
	}
}
