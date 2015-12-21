package org.test;

import loon.LTransition;
import loon.Screen;
import loon.canvas.LColor;
import loon.component.LClickButton;
import loon.component.LSelectorIcon;
import loon.event.GameTouch;
import loon.event.LTouchArea;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class SelectIconTest extends Screen {

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {
		// 设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));

		final LClickButton back = MultiScreenTest.getBackButton(this);

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

	@Override
	public void alter(LTimerContext timer) {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void touchDown(GameTouch e) {

	}

	@Override
	public void touchUp(GameTouch e) {

	}

	@Override
	public void touchMove(GameTouch e) {

	}

	@Override
	public void touchDrag(GameTouch e) {

	}

	@Override
	public void resume() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void close() {

	}
}
