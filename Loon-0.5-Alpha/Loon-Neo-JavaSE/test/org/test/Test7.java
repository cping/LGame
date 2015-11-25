package org.test;

import loon.LSetting;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.ActionListener;
import loon.action.ActionTween;
import loon.action.ActionType;
import loon.action.sprite.Sprite;
import loon.event.GameTouch;
import loon.event.LTouchArea;
import loon.event.Updateable;
import loon.geom.BooleanValue;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.utils.Easing;
import loon.utils.processes.WaitProcess;
import loon.utils.processes.WaitProcess.WaitEvent;
import loon.utils.timer.LTimerContext;

public class Test7 extends Screen {

	@Override
	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {
		
	}

	@Override
	public void alter(LTimerContext timer) {

	}

	@Override
	public void resize(int width, int height) {

	}
	
	BooleanValue value;

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
		setting.width = 640;
		setting.height = 480;

		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new Test7();
			}
		});
	}

}
