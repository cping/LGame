package org.test;

import loon.LTransition;
import loon.Screen;
import loon.component.LControl;
import loon.component.LControl.DigitalListener;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class ControlTest extends Screen {

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

		LControl c = new LControl(66, 66);

		c.setControl(new DigitalListener() {

			@Override
			public void up45() {

			}

			@Override
			public void up() {

			}

			@Override
			public void right45() {

			}

			@Override
			public void right() {

			}

			@Override
			public void left45() {

			}

			@Override
			public void left() {

			}

			@Override
			public void down45() {

			}

			@Override
			public void down() {

			}
		});
		add(c);

		add(MultiScreenTest.getBackButton(this,0));
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
