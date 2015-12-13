package org.test;

import loon.LSetting;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.component.LControl;
import loon.component.LControl.DigitalListener;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.javase.Loon;
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

		add(MultiScreenTest.getBackButton(this));
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

	public static void main(String[] args) {

		LSetting setting = new LSetting();
		setting.isFPS = true;
		setting.isLogo = false;
		setting.logoPath = "loon_logo.png";
		setting.width = 480;
		setting.height = 320;
		setting.width_zoom = 640;
		setting.height_zoom = 480;
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "Live2dTest";
		setting.emulateTouch = false;
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new ControlTest();
			}
		});

	}
}
