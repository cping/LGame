package org.test;

import loon.LSetting;
import loon.LazyLoading;
import loon.Screen;
import loon.component.LClickButton;
import loon.component.LComponent;
import loon.event.ClickListener;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class Test extends Screen {

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {
		// 设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));

		add(MultiScreenTest.getBackButton(this, 1));
		setBackground("back1.png");
		add(new LClickButton("Scale", 66, 66, 120, 50).S(new ClickListener() {

			@Override
			public void UpClick(LComponent comp, float x, float y) {
				if (isActionCompleted()) { //如果Screen动画执行完毕则执行(改变Screen会影响全局，所以最好检查下是否有动画在播放，以免某些动画中途停止，导致Screen混乱
					//影响整个布局)
					selfAction().scaleTo(0.6f).start(); //缩放为60%
				}
			}

			@Override
			public void DragClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DownClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DoClick(LComponent comp) {

			}
		}));
		add(new LClickButton("Shake", 256, 66, 120, 50).S(new ClickListener() {

			@Override
			public void UpClick(LComponent comp, float x, float y) {
				if (isActionCompleted()) {
					selfAction().shakeTo(2f).start();
				}
			}

			@Override
			public void DragClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DownClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DoClick(LComponent comp) {

			}
		}));
		add(new LClickButton("Rotate", 66, 166, 120, 50).S(new ClickListener() {

			@Override
			public void UpClick(LComponent comp, float x, float y) {
				if (isActionCompleted()) {
					selfAction().rotateTo(-180).scaleTo(0.6f).start();
				}
			}

			@Override
			public void DragClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DownClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DoClick(LComponent comp) {

			}
		}));
		add(new LClickButton("Reset", 256, 166, 120, 50).S(new ClickListener() {

			@Override
			public void UpClick(LComponent comp, float x, float y) {
				if (isActionCompleted()) {
					selfAction().rotateTo(0).scaleTo(1f).start();
				}
			}

			@Override
			public void DragClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DownClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DoClick(LComponent comp) {

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
