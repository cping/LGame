package org.test;

import loon.LTransition;
import loon.Screen;
import loon.canvas.LColor;
import loon.component.LProgress;
import loon.component.LProgress.ProgressType;
import loon.events.GameTouch;
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

		LProgress progress = new LProgress(ProgressType.UI, LColor.red, 55, 55,
				100, 15);
		progress.setMaxValue(800f);
		// 进度一半
		progress.setPercentage(0.5f);
		//System.out.println(progress.getValue());
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
		//progress4.setValue(65f);
		add(progress4);

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
