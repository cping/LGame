package org.test;

import loon.LTransition;
import loon.Screen;
import loon.action.sprite.effect.RippleEffect;
import loon.event.GameTouch;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class RippleTouchTest extends Screen {

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {
		// 改变默认渲染顺序
		// 首先绘制桌面组件
		setSecondOrder(DRAW_DESKTOP_PAINT());
		// 其次绘制用户界面
		setFristOrder(DRAW_USER_PAINT());
		// 最后绘制精灵
		setLastOrder(DRAW_SPRITE_PAINT());
		// 构建Ripple特效并注入Screen
		RippleEffect ripple = new RippleEffect();
		add(ripple);
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

}
