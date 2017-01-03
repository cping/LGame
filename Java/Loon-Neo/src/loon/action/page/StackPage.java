package loon.action.page;

import loon.Screen;

public class StackPage extends BasePage{

	public void onTransform(Screen screen, float position) {
		screen.setX(position < 0 ? 0 : screen.getWidth() * position);
	}
}
