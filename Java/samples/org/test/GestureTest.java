package org.test;

import loon.LTransition;
import loon.Screen;
import loon.canvas.LColor;
import loon.component.LGesture;
import loon.component.LLabel;
import loon.event.GameTouch;
import loon.event.Touched;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class GestureTest extends Screen {

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {

		final LGesture g = new LGesture();
		g.setColor(LColor.red);
		add(g);

		final LLabel label = addLabel("简单手势识别");
		// 居中控件
		centerOn(label);

		g.up(new Touched() {

			@Override
			public void on(float x, float y) {
				// 分析手势(手势识别需要采样,默认只能识别非常简单的几种,有需要可以自行导入采样数据)
				label.setText(g.getRecognizer().getName());
				// 自行导入
				//label.setText(g.getRecognizer("assets/rftemplates.txt",true).getName());
			}
		});

		add(MultiScreenTest.getBackButton(this, 0));
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
