package org.test;

import loon.Stage;
import loon.canvas.LColor;
import loon.component.LClickButton;
import loon.component.LSelectorIcon;
import loon.events.LTouchArea;

public class SelectIconTest extends Stage {

	@Override
	public void create() {

		final LClickButton back = MultiScreenTest.getBackButton(this,0);

		registerTouchArea(new LTouchArea() {

			@Override
			public void onAreaTouched(Event e, float touchX, float touchY) {
				if (e == Event.DOWN) {
					LSelectorIcon selectIcon = new LSelectorIcon(touchX,
							touchY, 48);
					selectIcon.setBackgroundColor(LColor.blue);
					selectIcon.setBorderColor(LColor.red);
					add(selectIcon);
				}
			}

			// 只要不是点中back按钮
			@Override
			public boolean contains(float x, float y) {
				return !back.contains(x, y);
			}
		});

		add(back);
	
	}
	
}
