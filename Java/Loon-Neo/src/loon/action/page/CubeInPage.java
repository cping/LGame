package loon.action.page;

import loon.Screen;

public class CubeInPage extends BasePage{

	public void onTransform(Screen screen, float position) {
		screen.setPivotX(position > 0 ? -1 : screen.getWidth());
		screen.setPivotY(-1);
		screen.setRotation(-90f * position);
	}

	public boolean isPagingEnabled() {
		return true;
	}
}
