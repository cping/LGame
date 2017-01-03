package loon.action.page;

import loon.Screen;

public class AccordionPage extends BasePage {

	public void onTransform(Screen screen, float position) {
		screen.setPivotX(position < 0 ? screen.getWidth() : -1);
		screen.setScaleX(position < 0 ? 1f + position : 1f - position);
	}
}
