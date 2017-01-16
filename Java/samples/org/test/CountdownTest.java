package org.test;

import loon.Screen;
import loon.action.sprite.NumberSprite;
import loon.canvas.LColor;
import loon.component.LClickButton;
import loon.event.GameTouch;
import loon.event.Touched;
import loon.opengl.GLEx;
import loon.utils.timer.CountdownTimer;
import loon.utils.timer.LTimerContext;

public class CountdownTest extends Screen {

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {

		add(MultiScreenTest.getBackButton(this, 1));
		//设置默认倒计时器,倒数30秒
		final CountdownTimer timer = new CountdownTimer(30);
		// 以CountdownTimer设置NumberSprite内容,显示色彩白色,构成数字的每块小格像素大小5（渲染为3x6的像素块）
		NumberSprite sprite = new NumberSprite(timer, LColor.white, 5);
		centerOn(sprite);
		add(sprite);
		add(new LClickButton("30 Play", 50, 50, 100, 50).up(new Touched() {

			@Override
			public void on(float x, float y) {
				timer.play(30);
			}
		}));
		add(new LClickButton("60 Play", 50, 180, 100, 50).up(new Touched() {

			@Override
			public void on(float x, float y) {
				timer.play(60);
			}
		}));
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
