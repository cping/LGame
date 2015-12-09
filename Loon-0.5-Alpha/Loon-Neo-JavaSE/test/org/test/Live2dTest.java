package org.test;

import loon.LSetting;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.canvas.LColor;
import loon.event.GameTouch;
import loon.javase.Loon;
import loon.live2d.framework.LAppLive2DManager;
import loon.live2d.framework.LAppModel;
import loon.opengl.GLEx;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public class Live2dTest extends Screen {

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
				return new Live2dTest();
			}
		});

	}

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
