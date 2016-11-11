package org.test;

import loon.LSetting;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.action.sprite.Sprite;
import loon.event.FrameLoopEvent;
import loon.event.GameTouch;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.timer.LTimerContext;

public class FrameLoopTest extends Screen {

	@Override
	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {
		

	}

	@Override
	public void onLoad() {

		float layoutRadius = 70;
		float radianUnit = MathUtils.PI / 2;

		final Sprite role = new Sprite();
		add(role);

		// 添加4张人头图片
		for (int i = 0; i < 4; i++) {
			Sprite s = new Sprite("assets/ccc.png");

			// 以圆周排列人头
			s.setLocation(MathUtils.cos(radianUnit * i) * layoutRadius,
					MathUtils.sin(radianUnit * i) * layoutRadius);

			role.addChild(s);
		}

		role.setPivot(55, 72);
		role.setLocation(175, 75);

		// 添加一个事件到循环中
		addFrameLoop(new FrameLoopEvent() {

			// 递增旋转角度
			@Override
			public void invoke(long elapsedTime, Screen e) {
				role.setRotation(role.getRotation() + 2);
			}

			@Override
			public void completed() {

			}
		});
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
