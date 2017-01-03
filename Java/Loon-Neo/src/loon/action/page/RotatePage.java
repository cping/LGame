package loon.action.page;

import loon.Screen;
import loon.utils.MathUtils;

public class RotatePage extends BasePage {

	private float lastX = 0;

	@Override
	public void onTransform(Screen screen, float position) {
		float percentage = 1f - MathUtils.abs(position);
		setVisible(screen, position);
		setTranslation(screen);
		setSize(screen, position, percentage);
		setRotation(screen, position, percentage);
	}

	private void setVisible(Screen screen, float position) {
		if (position < 0.5 && position > -0.5) {
			screen.setVisible(true);
		} else {
			screen.setVisible(false);
		}
	}

	private void setTranslation(Screen screen) {
		float scroll = lastX - screen.getX();
		screen.setX(scroll);
		lastX = screen.getX();
	}

	private void setSize(Screen screen, float position, float percentage) {
		screen.setScaleX((position != 0 && position != 1) ? percentage : 1);
		screen.setScaleY((position != 0 && position != 1) ? percentage : 1);
	}

	private void setRotation(Screen screen, float position, float percentage) {
		if (position > 0) {
			screen.setRotation(-180 * (percentage + 1));
		} else {
			screen.setRotation(180 * (percentage + 1));
		}
	}
}
