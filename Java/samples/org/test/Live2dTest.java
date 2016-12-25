package org.test;

import loon.LTransition;
import loon.Screen;
import loon.canvas.LColor;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.live2d.framework.LAppLive2DManager;
import loon.live2d.framework.LAppModel;
import loon.opengl.GLEx;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public class Live2dTest extends Screen {


	public LTransition onTransition() {
		return LTransition.newPixelWind(LColor.white);
	}

	LTimer timer = new LTimer(200);

	@Override
	public void draw(GLEx g) {
		if (isTransitionCompleted() && isOnLoadComplete()) {
			if (model != null) {
				if (timer.action(elapsedTime)) {
					model.update();
				}
				// g.scale(0.5f, 0.5f);
				model.draw(g);
			}
		}
	}

	LAppModel model;

	@Override
	public void onLoad() {

		// haru
		// shizuku
		// wanko
		String modelSettingPath = "assets/live2d/haru/haru.model.json";

		LAppLive2DManager manager = new LAppLive2DManager();

		model = manager.loadModel(modelSettingPath);
		// model.getLive2DModel().setScale(0.2f, 0.2f);
		// model.draw();

		LFont.setDefaultFont(LFont.getFont(20));
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
		model.onDrag(e.getX(), e.getY());
	}

	@Override
	public void touchUp(GameTouch e) {
		model.setDrag(0, 0);
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
