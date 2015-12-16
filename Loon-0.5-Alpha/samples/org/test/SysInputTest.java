package org.test;

import loon.LSystem;
import loon.LTransition;
import loon.Screen;
import loon.component.LClickButton;
import loon.event.GameTouch;
import loon.event.KeyMake.TextType;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class SysInputTest extends Screen {

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {
		// 设置默认字体大小为20
		LFont.setDefaultFont(LFont.getFont(20));

		LClickButton click = new LClickButton("Input", 66, 66, 150, 30) {

			@Override
			public void upClick() {

				LSystem.sysText(new TextEvent() {

					@Override
					public void input(String text) {

					}

					@Override
					public void cancel() {

					}
				}, TextType.DEFAULT, "测试", "什么也不说");

			}
		};

		add(click);

		LClickButton click2 = new LClickButton("Dialog", 66, 166, 150, 30) {

			@Override
			public void upClick() {
				LSystem.sysDialog(new ClickEvent() {

					@Override
					public void clicked() {

					}

					@Override
					public void cancel() {

					}
				}, "什么也不提", "提示", "是", "否");

			}
		};

		add(click2);

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
