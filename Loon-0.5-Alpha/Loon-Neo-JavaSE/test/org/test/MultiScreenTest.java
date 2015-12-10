package org.test;

import loon.LSetting;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.ActionListener;
import loon.component.LClickButton;
import loon.component.LComponent;
import loon.event.ActionKey;
import loon.event.ClickListener;
import loon.event.GameTouch;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class MultiScreenTest extends Screen {

	@Override
	public void draw(GLEx g) {

	}

	public static LClickButton getBackButton(final Screen screen) {
		LClickButton back = new LClickButton("Back", screen.getWidth() - 100,
				screen.getHeight() - 70, 80, 50);
		back.SetClick(new ClickListener() {

			@Override
			public void UpClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DragClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DownClick(LComponent comp, float x, float y) {

			}

			//事件锁，让点击唯一化
			ActionKey click = new ActionKey(ActionKey.DETECT_INITIAL_PRESS_ONLY);

			@Override
			public void DoClick(LComponent comp) {
				if (!click.isPressed()) {

					// 为按钮设置一个旋转动画，每次前进36度
					set(comp).rotateTo(360, 36f).start()
							.setActionListener(new ActionListener() { //监听动作事件

								//事件完毕后，调用screen标记为main的
								@Override
								public void stop(ActionBind o) {
									screen.runScreen("main");
								}

								@Override
								public void start(ActionBind o) {

								}

								@Override
								public void process(ActionBind o) {

								}
							});
					click.press();
				}

			}
		});
		return back;
	}

	@Override
	public void onLoad() {
		// 预先设定多个Screen，并赋予名称
		addScreen("main", this);
		addScreen("messagebox", new LMessageBoxTest());
		addScreen("live2d", new Live2dTest());

		// 增加按钮与监听
		LClickButton click = new LClickButton("MessageBox", 100, 50, 150, 50);
		click.SetClick(new ClickListener() {

			@Override
			public void UpClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DragClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DownClick(LComponent comp, float x, float y) {
				runScreen("messagebox");
			}

			@Override
			public void DoClick(LComponent comp) {

			}
		});
		add(click);

		click = new LClickButton("live2d", 100, 120, 150, 50);
		click.SetClick(new ClickListener() {

			@Override
			public void UpClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DragClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DownClick(LComponent comp, float x, float y) {
				runScreen("live2d");
			}

			@Override
			public void DoClick(LComponent comp) {

			}
		});
		add(click);
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

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	public static void main(String[] args) {
		LSetting setting = new LSetting();
		setting.isFPS = true;
		setting.isLogo = false;
		setting.logoPath = "loon_logo.png";
		setting.width_zoom = 640;
		setting.height_zoom = 480;
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "test";
		setting.emulateTouch = false;
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new MultiScreenTest();
			}
		});
	}
}
