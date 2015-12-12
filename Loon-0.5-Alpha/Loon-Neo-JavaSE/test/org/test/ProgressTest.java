package org.test;

import loon.LSetting;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.canvas.LColor;
import loon.component.LProgress;
import loon.component.LProgress.ProgressType;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class ProgressTest extends Screen {

	@Override
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

		LProgress progress = new LProgress(ProgressType.UI, LColor.red, 55, 55,
				100, 15);
		// 进度一半
		progress.setPercentage(0.5f);
		add(progress);

		LProgress progress2 = new LProgress(ProgressType.GAME, LColor.red, 55,
				155, 100, 15);
		// 进度一半
		progress2.setPercentage(0.5f);
		add(progress2);

		LProgress progress3 = new LProgress(ProgressType.UI, LColor.red, 255,
				55, 100, 15);
		// 进度35%
		progress3.setPercentage(0.35f);
		progress3.setVertical(true);
		add(progress3);

		LProgress progress4 = new LProgress(ProgressType.GAME, LColor.red, 155,
				155, 100, 15);
		// 进度65%
		progress4.setVertical(true);
		progress4.setPercentage(0.65f);
		add(progress4);

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
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "test";
		setting.emulateTouch = false;
		setting.width = 480;
		setting.height = 320;

		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new ProgressTest();
			}
		});
	}
}
