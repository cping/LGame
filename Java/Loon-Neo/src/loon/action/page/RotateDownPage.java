package loon.action.page;

import loon.Screen;

public class RotateDownPage extends BasePage {

	private static final float ROT_MOD = -15f;

	public void onTransform(Screen screen, float position) {
		final float width = screen.getWidth();
		final float height = screen.getHeight();
		final float rotation = (float) (ROT_MOD * position * -12.25);
		screen.setPivotX(width * 0.5f);
		screen.setPivotY(height);
		screen.setRotation(rotation);
	}

	public boolean isPagingEnabled() {
		return true;
	}
}
