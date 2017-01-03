package loon.action.page;

import loon.Screen;

public class RotateUpPage extends BasePage{

	private static final float ROT_MOD = -15f;

	public void onTransform(Screen screen, float position) {
		final float width = screen.getWidth();
		final float rotation = ROT_MOD * position * 12.25f;
		screen.setPivotX(width * 0.5f);
		screen.setPivotY(0);
		screen.setX(0f);
		screen.setRotation(rotation);
	}
	
	public boolean isPagingEnabled() {
		return true;
	}

}
