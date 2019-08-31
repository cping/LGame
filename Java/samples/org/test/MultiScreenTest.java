package org.test;

import loon.LObject;
import loon.LSystem;
import loon.LTransition;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.ActionListener;
import loon.canvas.LColor;
import loon.component.LClickButton;
import loon.component.LComponent;
import loon.component.layout.LayoutManager;
import loon.event.ActionKey;
import loon.event.ClickListener;
import loon.event.GameTouch;
import loon.event.Touched;
import loon.font.BMFont;
import loon.opengl.GLEx;
import loon.utils.TArray;
import loon.utils.processes.RealtimeProcess;
import loon.utils.timer.LTimerContext;

public class MultiScreenTest extends Screen {

	private int page = 0;

	/**
	 * Screen初始化特效使用像素风飘过
	 */
	@Override
	public LTransition onTransition() {
		return LTransition.newPixelThunder(LColor.yellow);
	}

	@Override
	public void draw(GLEx g) {
	}

	public static LClickButton getBackButton(final Screen screen, final int page) {
		return getBackButton(screen, page, screen.getWidth() - 100, screen.getHeight() - 70);
	}

	public static LClickButton getBackButton(final Screen screen, final int page, final int x, int y) {

		LClickButton back = new LClickButton("Back", x, y, 80, 50);
		screen.addTouchLimit(back);
		back.setLayer(130);
		back.S(new ClickListener() {

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
					set(comp).rotateTo(360, 36f).start().setActionListener(new ActionListener() { // 监听动作事件

						// 事件完毕后，调用screen标记为main的
						@Override
						public void stop(ActionBind o) {
							Screen s = screen.runScreen("main");
							if (s != null) {
								s.index = page;
							}
							// 还原当前动作角色旋转角度为0
							o.setRotation(0);
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
				// 查看LObject状态设置
				switch (comp.getStatus()) {
				case LObject.NOT: // 如果对象无状态设置
					LClickButton click = (LClickButton) comp;
					String text = click.getText();
					// 由于将按钮名与Screen名设定的一样，所以直接调用按钮名就可以运行指定Scrren了
					runScreen(text);
					break;
				case LObject.FALSE: // 切换新的示例Screen
					// wait code
					break;
				case LObject.TRUE:// 退出
					LSystem.exit();
					break;
				default:
					break;
				}
			}
		}

		@Override
		public void UpClick(LComponent comp, float x, float y) {

		}

		@Override
		public void DragClick(LComponent comp, float x, float y) {

		}

	}

	final String[] page0 = { "MessageBox", "Live2d", "Action", "Effect", "Emulator", "TileMap", "SpriteBatch",
			"BatchScreen", "BMFont", "Layout", "Table", "Menu", "Names", "Toast", "List", "Sprite", "TexturePack",
			"LNode", "Scroll", "Cycle", "TextArea", "Progress", "Particle", "SelectIcon", "Control", "JsonRes",
			"SheetFont", "ParConfig", "RippleTouch", "Sound", "Gesture", "Physical", "LNode2", "Input", "Depth",
			"Canvas", "GameMap", "MoveClip", "TextureImage", "Session" };

	final String[] page1 = { "Screen", "Slider", "Alert", "Animation", "FrameLoop", "Script", "SText", "Light",
			"Countdown", "AVG", "Layer", "LLK", "TextField", "SRPG", "PShadow", "Array2DMap", "Image", "Natural",
			"MenuSelect", "CheckBox", "TextTree", "SLG", "I18N", "Buttle", "HexagonMap", "Label", "Grid", "Elements",
			"QRCode", "Chop", "CachePool", "JSonView", "HtmlView", "CompNewLine", "CollWorld", "Gravity", "Jigsaw",
			"Tetris", "Explosion", "FBird" };

	final String[] page2 = { "Timer","Snake","TextEffect","Margin","DefineMove","Interval","Scheduler" };

	static BMFont info_font;

	@Override
	public void onLoad() {
		// 可以通过设置syncTween与让缓动计算与画布刷新完全同步(Loon默认异步)
		// syncTween(true);
		// 也可以设置全局的缓动动画延迟
		// delayTween(10);
		// 创建一个普通的Entity

		String[][] pages = { page0, page1, page2 };
		// 使用图片字体(如果不设置，则loon默认使用当前系统字体)
		if (info_font == null) {
			try {
				// 加载
				info_font = new BMFont("assets/info.fnt", "assets/info.png");
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 缩放为0.6倍
			info_font.setFontScale(0.6f);
		}
		int index = 0;
		// 构建一个通用的监听器
		MyClickListener clickListener = new MyClickListener();
		Screen screen = getScreen("main");
		if (screen == null) {
			// 预先设定多个Screen，并赋予名称
			addScreen("main", this);
		} else {
			page = screen.index;
		}

		if (page == 0) {
			addScreen(page0[index++], new LMessageBoxTest());
			addScreen(page0[index++], new Live2dTest());
			addScreen(page0[index++], new ActionEventTest());
			addScreen(page0[index++], new EffectTest());
			addScreen(page0[index++], new EmulatorTest());
			addScreen(page0[index++], new TileMapTest());
			addScreen(page0[index++], new SpriteBatchTest());
			addScreen(page0[index++], new SpriteBatchScreenTest());
			addScreen(page0[index++], new BMFontTest());
			addScreen(page0[index++], new LayoutTest());
			addScreen(page0[index++], new TableTest());
			addScreen(page0[index++], new MenuTest());
			addScreen(page0[index++], new DecideNameTest());
			addScreen(page0[index++], new ToastTest());
			addScreen(page0[index++], new ListTest());
			addScreen(page0[index++], new SpriteTest());
			addScreen(page0[index++], new LTexturePackTest());
			addScreen(page0[index++], new NodeTest());
			addScreen(page0[index++], new ScrollTest());
			addScreen(page0[index++], new CycleTest());
			addScreen(page0[index++], new TextAreaTest());
			addScreen(page0[index++], new ProgressTest());
			addScreen(page0[index++], new ParticleTest());
			addScreen(page0[index++], new SelectIconTest());
			addScreen(page0[index++], new ControlTest());
			addScreen(page0[index++], new JSonResTest());
			addScreen(page0[index++], new SpriteSheetFontTest());
			addScreen(page0[index++], new ParticleConfigTest());
			addScreen(page0[index++], new RippleTouchTest());
			addScreen(page0[index++], new SoundTest());
			addScreen(page0[index++], new GestureTest());
			addScreen(page0[index++], new PhysicalTest());
			addScreen(page0[index++], new Node2Test());
			addScreen(page0[index++], new SysInputTest());
			addScreen(page0[index++], new DepthTest());
			addScreen(page0[index++], new CanvasLayerTest());
			addScreen(page0[index++], new GameMapTest());
			addScreen(page0[index++], new MovieClipTest());
			addScreen(page0[index++], new TextureImageTest());
			addScreen(page0[index++], new SessionTest());
		} else if (page == 1) {
			addScreen(page1[index++], new Test());
			addScreen(page1[index++], new SliderTest());
			addScreen(page1[index++], new AlertTest());
			addScreen(page1[index++], new AnimationTest());
			addScreen(page1[index++], new FrameLoopTest());
			addScreen(page1[index++], new RocScriptTest());
			addScreen(page1[index++], new ScrollTextTest());
			addScreen(page1[index++], new LightImageTest());
			addScreen(page1[index++], new CountdownTest());
			addScreen(page1[index++], new TitleScreen());
			addScreen(page1[index++], new TDTest());
			addScreen(page1[index++], new LLKTest());
			addScreen(page1[index++], new TextFieldTest());
			addScreen(page1[index++], new SRPGTest());
			addScreen(page1[index++], new PShadowTest());
			addScreen(page1[index++], new MapTest());
			addScreen(page1[index++], new ImageTest());
			addScreen(page1[index++], new NaturalTest());
			addScreen(page1[index++], new MenuSelectTest());
			addScreen(page1[index++], new CheckBoxTest());
			addScreen(page1[index++], new TextTreeTest());
			addScreen(page1[index++], new SLGTest());
			addScreen(page1[index++], new I18NTest());
			addScreen(page1[index++], new ButtleTest());
			addScreen(page1[index++], new HexagonMapTest());
			addScreen(page1[index++], new LabelTest());
			addScreen(page1[index++], new GridTest());
			addScreen(page1[index++], new ElementsOrderTest());
			addScreen(page1[index++], new QRCodeTest());
			addScreen(page1[index++], new ChopTest());
			addScreen(page1[index++], new CachePoolTest());
			addScreen(page1[index++], new JSonViewTest());
			addScreen(page1[index++], new HtmlViewTest());
			addScreen(page1[index++], new CompNewLine());
			addScreen(page1[index++], new CollisionWorldTest());
			addScreen(page1[index++], new GravityTest());
			addScreen(page1[index++], new JigsawTest());
			addScreen(page1[index++], new TetrisTest());
			addScreen(page1[index++], new ExplosionTest());
			addScreen(page1[index++], new FlappyBirdTest());
		} else if (page == 2) {
			addScreen(page2[index++], new TimerTest());
			addScreen(page2[index++], new SnakeTest());
			addScreen(page2[index++], new TextEffectTest());
			addScreen(page2[index++], new MarginTest());
			addScreen(page2[index++], new DefineMoveTest());
			addScreen(page2[index++], new IntervalTest());
			addScreen(page2[index++], new SchedulerTest());
		}

		// 默认按钮大小为100x25
		int btnWidth = 100;
		int btnHeight = 25;
		// 添加一组按钮布局，并返回按钮对象
		TArray<LClickButton> clicks = LayoutManager.elementButtons(this, pages[page], 15, 25, btnWidth, btnHeight,
				clickListener, getHeight() - btnHeight);

		// final TArray<ActionTween> tweens = new TArray<ActionTween>();

		// 首先让按钮不可见
		for (int i = 0; i < clicks.size; i++) {
			LClickButton btn = clicks.get(i);
			// 设置按钮头alpha为0(即不显示)
			btn.setAlpha(0);
			// 设置提示信息为按钮名
			btn.setToolTipText(pages[page][i]);
			// 设置位图字体()
			btn.setFont(info_font);
			// 为按钮设置事件，并加载入一个集合
			// tweens.add(set(btn));
		}

		// 设置一个退出按钮
		LClickButton exitClick = LClickButton.make(48, 48, "cross.png", "cross_effect.png", "cross_effect.png");
		// 设定一个特殊状态为true
		exitClick.setStatus(LObject.TRUE);
		// 设置监听
		exitClick.S(clickListener);
		// 初始透明度0
		exitClick.setAlpha(0);
		// 按钮置顶
		topOn(exitClick);
		// 偏移Screen大小-按钮大小-5
		exitClick.setX(getWidth() - exitClick.getWidth() - 5);
		add(exitClick);
		// tweens.add(set(exitClick));

		if (page != 0 && page != (pages.length - 1)) {
			// 设置一个上页按钮
			LClickButton nextClick = LClickButton.make("BACK", 45, 25);
			// 设定一个特殊状态为false
			nextClick.setStatus(LObject.FALSE);
			// 设置监听
			nextClick.S(clickListener);
			// 初始透明度0
			nextClick.setAlpha(0);
			nextClick.setFont(info_font);
			// 偏移Screen大小-按钮大小-5
			nextClick.setX(getWidth() - nextClick.getWidth() - 5);
			nextClick.setY(getHeight() - nextClick.getHeight() - 55);
			// 监听next按钮
			nextClick.up(new Touched() {

				@Override
				public void on(float x, float y) {
					// 变更为page-1
					runScreen("main").index = page - 1;
				}
			});
			add(nextClick);
		}
		// 设置一个下页按钮
		LClickButton nextClick = LClickButton.make(page < (pages.length - 1) ? "NEXT" : "BACK", 45, 25);
		// 设定一个特殊状态为false
		nextClick.setStatus(LObject.FALSE);
		// 设置监听
		nextClick.S(clickListener);
		// 初始透明度0
		nextClick.setAlpha(0);
		nextClick.setFont(info_font);
		// 偏移Screen大小-按钮大小-5
		nextClick.setX(getWidth() - nextClick.getWidth() - 5);
		nextClick.setY(getHeight() - nextClick.getHeight() - 24);
		// 监听next按钮
		nextClick.S(new ClickListener() {

			@Override
			public void UpClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DragClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DownClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DoClick(LComponent comp) {
				// 如果按钮文本为next
				if (((LClickButton) comp).getText().equals("NEXT")) {
					// 索引变更为page+1
					runScreen("main").index = page + 1;
				} else {
					// 变更为page-1
					runScreen("main").index = page - 1;
				}
			}
		});
		add(nextClick);

		// tweens.add(set(nextClick));

		// 设定一个游戏进程，半秒后让按钮导入
		RealtimeProcess process = new RealtimeProcess() {

			@Override
			public void run(LTimerContext time) {
				// 当Screen过渡动画播放完毕后
				if (isTransitionCompleted()) {
					// 穷举按钮事件
					/*
					 * for (ActionTween tween : tweens) { // 淡出事件，开始执行
					 * tween.fadeOut(10f).start(); // 删除单独进程（否则会不断执行） kill(); }
					 */
					findUINames("ClickButton").// 查找出所有组件名称为
												// ClickButton
												// 的（此处可查找多个）
					// fadeIn(10).startTweens();
					fadeIn(10).delay(1f).rotateTo(360).startTweens(); // 组件淡入，速度10，间隔1秒，旋转360，开始动画
					// 杀掉这个伪进程（这是与Loon同步执行的，非真实单独线程）
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

}
