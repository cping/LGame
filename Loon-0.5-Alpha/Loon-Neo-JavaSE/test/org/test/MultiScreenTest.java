package org.test;

import loon.LSetting;
import loon.LSystem;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.ActionListener;
import loon.action.ActionTween;
import loon.component.LClickButton;
import loon.component.LComponent;
import loon.component.layout.LayoutManager;
import loon.event.ActionKey;
import loon.event.ClickListener;
import loon.event.GameTouch;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.utils.TArray;
import loon.utils.processes.RealtimeProcess;
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

			// 事件锁，让点击唯一化
			ActionKey click = new ActionKey(ActionKey.DETECT_INITIAL_PRESS_ONLY);

			@Override
			public void DoClick(LComponent comp) {
				if (!click.isPressed()) {

					// 为按钮设置一个旋转动画，每次前进36度
					set(comp).rotateTo(360, 36f).start()
							.setActionListener(new ActionListener() { // 监听动作事件

										// 事件完毕后，调用screen标记为main的
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

	// 制作一个按钮监听器
	private class MyClickListener implements ClickListener {

		@Override
		public void DoClick(LComponent comp) {

		}

		@Override
		public void DownClick(LComponent comp, float x, float y) {
			if (comp instanceof LClickButton) {
				LClickButton click = (LClickButton) comp;
				String text = click.getText();
				// 由于将按钮名与Screen名设定的一样，所以直接调用按钮名就可以运行指定Scrren了
				runScreen(text);
			}
		}

		@Override
		public void UpClick(LComponent comp, float x, float y) {

		}

		@Override
		public void DragClick(LComponent comp, float x, float y) {

		}

	}

	final String[] names = { "MessageBox", "Live2d", "Action", "Effect",
			"Stage", "TileMap", "SpriteBatch", "BatchScreen", "BMFont",
			"Layout" };

	@Override
	public void onLoad() {

		int index = 0;
		// 构建一个通用的监听器
		MyClickListener clickListener = new MyClickListener();
		// 预先设定多个Screen，并赋予名称
		addScreen("main", this);
		addScreen(names[index++], new LMessageBoxTest());
		addScreen(names[index++], new Live2dTest());
		addScreen(names[index++], new ActionEventTest());
		addScreen(names[index++], new EffectTest());
		addScreen(names[index++], new StageTest.ScreenTest());
		addScreen(names[index++], new TileMapTest());
		addScreen(names[index++], new SpriteBatchTest());
		addScreen(names[index++], new SpriteBatchScreenTest());
		addScreen(names[index++], new BMFontTest());
		addScreen(names[index++], new LayoutTest());

		// 默认按钮大小为120x30
		int btnWidth = 120;
		int btnHeight = 30;
		// 添加一组按钮布局，并返回按钮对象
		TArray<LClickButton> clicks = LayoutManager.elementButtons(this, names,
				15, 25, btnWidth, btnHeight, clickListener,
				LSystem.viewSize.getHeight() - btnHeight * 2);

		TArray<ActionTween> tweens = new TArray<ActionTween>();

		// 首先让按钮不可见
		for (LClickButton btn : clicks) {
			btn.setAlpha(0);
			// 为按钮设置事件，并加载入一个集合
			tweens.add(set(btn));
		}

		// 设定一个游戏进程，半秒后让按钮导入
		RealtimeProcess process = new RealtimeProcess() {

			@Override
			public void run(LTimerContext time) {
				// 穷举按钮事件
				for (ActionTween tween : tweens) {
					// 淡出事件，开始执行
					tween.fadeOut(10f).start();
					// 删除单独进程（否则会不断执行）
					kill();
				}

			}
		};
		// 延迟半秒执行
		process.setDelay(LSystem.SECOND / 2);
		addProcess(process);

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
