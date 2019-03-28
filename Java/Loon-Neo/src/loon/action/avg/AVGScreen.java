/**
 * Copyright 2008 - 2011
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
package loon.action.avg;

import loon.LObject;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.LTransition;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.ActionListener;
import loon.action.avg.drama.Command;
import loon.action.avg.drama.CommandType;
import loon.action.avg.drama.Conversion;
import loon.action.sprite.Entity;
import loon.action.sprite.ISprite;
import loon.action.sprite.Sprites;
import loon.action.sprite.effect.FadeEffect;
import loon.action.sprite.effect.NaturalEffect;
import loon.action.sprite.effect.NaturalEffect.NaturalType;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.component.Desktop;
import loon.component.LButton;
import loon.component.LClickButton;
import loon.component.LComponent;
import loon.component.LMessage;
import loon.component.LSelect;
import loon.component.LToast;
import loon.component.LToast.Style;
import loon.event.ClickListener;
import loon.event.GameKey;
import loon.event.GameTouch;
import loon.event.Updateable;
import loon.font.FontSet;
import loon.font.IFont;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.utils.Array;
import loon.utils.ListMap;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.processes.GameProcess;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

/**
 * Loon默认提供的AVG模板，在此Screen中可以直接实现一些简单的AVG游戏操作，并且更容易扩展出自己的AVG游戏系统。
 * 
 * @see CommandType
 */
public abstract class AVGScreen extends Screen implements FontSet<AVGScreen> {

	// 文字显示速度
	private SpeedMode _speedMode = SpeedMode.Normal;

	// 屏幕点击计数(为了手机环境选择时防止误点的计数器)
	private int _clickcount = 0;

	// 手机屏幕手机遇到选择框时，需要执行的点击次数限制(0时无限制，此参数的作用在于防止触屏误点选项)
	private int _mobile_select_valid_limit = 0;

	// 当前任务集合
	private Array<Task> _currentTasks = new Array<AVGScreen.Task>();

	// 任务集合
	private ListMap<String, AVGScreen.Task> _tasks = new ListMap<String, AVGScreen.Task>(20);

	private IFont _font;

	private int _plapDelay;

	private String _scriptName;

	private String _selectMessage;

	private String _dialogFileName;

	private LTexture _dialogTexture;

	private boolean isSelectMessage, isScriptRunning, isGameRunning, scrFlag;

	// 若需要任意处点击皆可继续脚本，此标记应为true
	private boolean screenClickd = false;

	// 若需要点击触发脚本继续的功能暂时失效，此处应为true
	private boolean limitClickd = false;

	// 自动播放的延迟时间
	private LTimer autoTimer = new LTimer(LSystem.SECOND);

	private int clickButtonLayer = 200;

	private LColor fontColor = LColor.white.cpy();

	private LColor gameColor;

	protected Command command;

	protected AVGCG scrCG;

	protected LSelect selectUI;

	protected LMessage messageUI;

	protected Desktop messageDesktop;

	protected Sprites effectSprites;

	private RealtimeProcess avgProcess;

	private boolean autoPlay;

	/**
	 * 此为AVG中特定任务接口，此接口需要实现使用，共有三个API，一个用来从脚本注入参数,
	 * 一个调用具体任务，一个返回是否完成的状态，只有任务完成后才能继续脚本。
	 * 
	 * PS : 再次强调，Task不执行完，是不能继续触发脚本的
	 *
	 */
	public interface Task {

		void parameters(String[] pars);

		/**
		 * 此处传参与执行并未设定在一起，方便用户异步调用.
		 */
		void call();

		boolean completed();
	}

	// AVG文字显示速度
	public enum SpeedMode {
		SuperSlow, // 超级慢
		Slow, // 慢
		FewSlow, // 慢一点点
		Normal, // 普通
		Fast, // 快
		Quickly, // 很快
		Flash, // 神速
	}

	/**
	 * 选择UI监听
	 *
	 */
	private class SelectClick implements ClickListener {

		private TArray<String> _items;

		public SelectClick(TArray<String> items) {
			_items = items;
		}

		@Override
		public void DoClick(LComponent comp) {

		}

		@Override
		public void DownClick(LComponent comp, float x, float y) {
		}

		@Override
		public void UpClick(LComponent comp, float x, float y) {
			if (tasking()) {
				return;
			}
			if (_items != null && command != null && selectUI != null) {
				if (((LSystem.base() != null && (LSystem.base().isMobile() || LSystem.base().setting.emulateTouch))
						? _clickcount++ >= _mobile_select_valid_limit : _clickcount > -1) && selectUI.isClick()) {
					int idx = selectUI.getResultIndex();
					if (idx != -1) {
						String gotoFlag = _items.get(idx);
						if (MathUtils.isNan(gotoFlag)) {
							command.gotoIndex((int) Double.parseDouble(gotoFlag));
						} else {
							command.gotoIndex(gotoFlag);
						}
						clearSelectMessage();
						nextScript();
					}
				}
			}
		}

		@Override
		public void DragClick(LComponent comp, float x, float y) {

		}

	}

	/**
	 * 默认任务（同时也是任务接口实现示例,Task主要就是给用户自行扩展的,以下实现本质上只是示例……）
	 */
	private void defTask() {
		/**
		 * 展示一个简易消息框
		 * 
		 * 使用方式，脚本中调用: task toast 字符串
		 * 
		 * @example task toast 你获得了500万
		 */
		putTask("toast", new Task() {

			private String parameter;

			private LToast toast;

			@Override
			public boolean completed() {
				boolean stop = toast.isStop() && (toast.getOpacity() <= 0.1f || !toast.isVisible());
				if (stop) {
					getDesktop().remove(toast);
				}
				return stop;
			}

			@Override
			public void call() {
				toast = LToast.makeText(parameter, Style.ERROR);
				getDesktop().add(toast);
			}

			@Override
			public void parameters(String[] pars) {
				parameter = StringUtils.replace(pars[0], "\"", "");
			}
		});
		/**
		 * 添加一个指定的过渡效果.
		 * 
		 * 使用方式，脚本中调用: task trans 渐变效果名 颜色
		 * 
		 * @example task trans fadein black
		 * @see LTransition
		 */
		putTask("trans", new Task() {

			private String transStr, colorStr;

			private LTransition transition;

			@Override
			public boolean completed() {
				boolean stop = transition.getTransitionListener().completed();
				if (stop) {
					getSprites().remove(transition.getTransitionListener().getSprite());
				}
				return stop;
			}

			@Override
			public void call() {
				transition = LTransition.newTransition(transStr, colorStr);
				getSprites().add(transition.getTransitionListener().getSprite());
			}

			@Override
			public void parameters(String[] pars) {
				if (pars.length >= 2) {
					transStr = pars[0];
					colorStr = pars[1];
				} else if (pars.length == 1) {
					transStr = pars[0];
					if (transStr.indexOf(',') != -1) {
						String[] list = StringUtils.split(transStr, ',');
						transStr = list[0];
						colorStr = list[1];
					} else {
						colorStr = null;
					}
				} else {
					// 字符串为null时，loon会调用默认特效设置FadeIn
					transStr = null;
					colorStr = null;
				}
			}
		});
		/**
		 * 添加一个精灵 (精灵可以用[clear 精灵名]或者[del 精灵名]方式删除)
		 * 
		 * 使用方式，脚本中调用(不必全部填写): task sprite {图像来源,精灵名称,x坐标,y坐标} {loon的action脚本命令}
		 * 
		 * @example task sprite {assets/c.png,55,55}
		 *          {move(155,155,true,16)->delay(3f)->move(25,125,true)}
		 */
		putTask("sprite", new Task() {

			private float x, y;

			private String _scriptName, _scriptSource, _scripteContext;

			private boolean isCompleted;

			@Override
			public boolean completed() {
				return isCompleted;
			}

			@Override
			public void call() {
				Entity entity = Entity.make(_scriptSource, x, y);
				if (_scriptName != null) {
					entity.setName(_scriptName);
				} else {
					entity.setName(_scriptSource);
				}
				if (_scripteContext != null) {
					act(entity, _scripteContext).start().setActionListener(new ActionListener() {

						@Override
						public void stop(ActionBind o) {
							isCompleted = true;
						}

						@Override
						public void start(ActionBind o) {

						}

						@Override
						public void process(ActionBind o) {

						}
					});
				} else {
					isCompleted = true;
				}
				scrCG.actionRole.add(entity);
			}

			@Override
			public void parameters(String[] pars) {
				String scriptInfo = pars[0].trim();
				int start = scriptInfo.indexOf('{');
				int end = scriptInfo.lastIndexOf('}');
				if (start != -1 && end != -1 && end > start) {
					scriptInfo = scriptInfo.substring(start + 1, end);
				}
				String[] list = StringUtils.split(scriptInfo, ',');
				if (list.length == 1) {
					_scriptSource = list[0];
				} else if (list.length == 2) {
					_scriptSource = list[0];
					if (MathUtils.isNan(list[1])) {
						x = y = Float.parseFloat(list[1]);
					}
				} else if (list.length == 3) {
					_scriptSource = list[0];
					if (MathUtils.isNan(list[1])) {
						x = Float.parseFloat(list[1]);
					}
					if (MathUtils.isNan(list[2])) {
						y = Float.parseFloat(list[2]);
					}
				} else if (list.length == 4) {
					_scriptSource = list[0];
					_scriptName = list[1];
					if (MathUtils.isNan(list[2])) {
						x = Float.parseFloat(list[2]);
					}
					if (MathUtils.isNan(list[3])) {
						y = Float.parseFloat(list[3]);
					}
				}
				if (pars.length > 1) {
					scriptInfo = pars[1].trim();
					start = scriptInfo.indexOf('{');
					end = scriptInfo.lastIndexOf('}');
					if (start != -1 && end != -1 && end > start) {
						scriptInfo = scriptInfo.substring(start + 1, end);
					}
					_scripteContext = scriptInfo;
				}

			}
		});
	}

	/**
	 * 以键值对方式添加任务（主要是方便脚本调用）
	 * 
	 * @param key
	 * @param value
	 */
	public void putTask(String key, Task value) {
		_tasks.put(key, value);
	}

	/**
	 * 返回指定键值对应的具体任务
	 * 
	 * @param key
	 * @return
	 */
	public Task getTask(String key) {
		return _tasks.get(key);
	}

	/**
	 * 删除指定任务
	 * 
	 * @param key
	 * @return
	 */
	public Task removeTask(String key) {
		return _tasks.removeKey(key);
	}

	/**
	 * 清空任务
	 */
	public void clearTask() {
		_tasks.clear();
	}

	/**
	 * 返回当前字符串的文字显示速度模式
	 * 
	 * @param name
	 * @return
	 */
	public SpeedMode toSpeedMode(String name) {
		String key = name.trim().toLowerCase();
		if ("superslow".equals(key)) {
			return SpeedMode.SuperSlow;
		} else if ("slow".equals(key)) {
			return SpeedMode.Slow;
		} else if ("fewslow".equals(key)) {
			return SpeedMode.FewSlow;
		} else if ("normal".equals(key)) {
			return SpeedMode.Normal;
		} else if ("fast".equals(key)) {
			return SpeedMode.Fast;
		} else if ("quickly".equals(key)) {
			return SpeedMode.Quickly;
		} else if ("flash".equals(key)) {
			return SpeedMode.Flash;
		} else {
			return SpeedMode.Normal;
		}
	}

	public AVGScreen(final String initscript, final String initdialog) {
		this(initscript, LTextures.loadTexture(initdialog));
	}

	public AVGScreen(final String initscript, final LTexture img) {
		this._font = LSystem.getSystemGameFont();
		this._scriptName = initscript;
		if (initscript == null) {
			return;
		}
		if (img != null) {
			this._dialogFileName = img.getSource();
			this._dialogTexture = img;
		}
	}

	public AVGScreen(final String initscript) {
		this._font = LSystem.getSystemGameFont();
		this._scriptName = initscript;
		if (initscript == null) {
			return;
		}
	}

	/**
	 * 选项监听
	 *
	 */
	private class OptClick implements ClickListener {

		private String label;

		public OptClick(String gotoFlag) {
			label = gotoFlag;
		}

		@Override
		public void UpClick(LComponent comp, float x, float y) {
			if (command != null && label != null) {
				command.gotoIndex(label);
				// 跳转后清除选择框,防止opt命令和select命令产生组件重叠时产生重复点击
				clearSelectMessage();
				nextScript();
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

	}

	/**
	 * 清除默认的选择框标记
	 */
	private void clearSelectMessage() {
		boolean selecting = (selectUI == null);
		if (!selecting) {
			selectUI.SetClick(null);
			selectUI.setVisible(false);
			limitClickd = false;
			scrFlag = false;
			isSelectMessage = false;
			_clickcount = 0;
		}
	}

	/**
	 * 添加选项按钮到游戏中
	 * 
	 * @param click
	 */
	private void addOpt(final LClickButton click) {
		if (command != null) {
			GameProcess process = new RealtimeProcess() {

				@Override
				public void run(LTimerContext time) {
					click.setFontColor(fontColor);
					click.Tag = CommandType.L_OPTION + (click.getText() == null ? command.getIndex() : click.getText());
					click.setLayer(clickButtonLayer);
					getDesktop().add(click);
					kill();
				}
			};
			addProcess(process);
		}
	}

	/**
	 * 设置选项按钮为脚本中参数
	 * 
	 * @param click
	 * @param order
	 */
	private final void setOpt(LClickButton click, String order) {
		click.setFontColor(fontColor);
		int startFlag = order.indexOf('{');
		int endFlag = order.lastIndexOf('}');
		if (startFlag != -1 && endFlag != -1 && endFlag > startFlag) {
			String gotoMes = order.substring(startFlag + 1, endFlag).trim();
			final String[] result = StringUtils.split(gotoMes, ',');
			if (result.length > 1) {
				// 只有单图时,全部按钮为一张图片(索引0存放跳转点，非按钮图)
				if (result.length == 2) {
					click.setTexture(LTextures.loadTexture(result[1]));
					click.setGrayButton(true);
				} else if (result.length == 3) { // 有两张图时
					LTexture texIdle = LTextures.loadTexture(result[1]);
					LTexture texClick = LTextures.loadTexture(result[2]);
					// 空闲时
					click.setIdleClick(texIdle);
					// 鼠标徘徊
					click.setHoverClick(texClick);
					// 鼠标按下
					click.setClickedClick(texClick);
				} else if (result.length == 4) { // 有三张图时
					LTexture texIdle = LTextures.loadTexture(result[1]);
					LTexture texHover = LTextures.loadTexture(result[2]);
					LTexture texClick = LTextures.loadTexture(result[3]);
					// 空闲时
					click.setIdleClick(texIdle);
					// 鼠标徘徊
					click.setHoverClick(texHover);
					// 鼠标按下
					click.setClickedClick(texClick);
				}
			}
			click.SetClick(new OptClick(result[0]));
		} else {
			click.setTexture(LTextures.loadTexture(order));
			click.setGrayButton(true);
		}
	}

	@Override
	public final void onLoad() {
		this.setRepaintMode(Screen.SCREEN_NOT_REPAINT);
		this._plapDelay = 60;
		if (_dialogTexture == null && _dialogFileName != null) {
			this._dialogTexture = LTextures.loadTexture(_dialogFileName);
		}
		this.isGameRunning = true;
	}

	@Override
	public final void onLoaded() {
		// 不同场合需要不同的渲染策略，此处将用户渲染置于底层
		// 最先绘制用户画面
		setFristOrder(DRAW_USER_PAINT());
		// 其次绘制精灵
		setSecondOrder(DRAW_SPRITE_PAINT());
		// 最后绘制桌面
		setLastOrder(DRAW_DESKTOP_PAINT());

		LSystem.load(new Updateable() {

			@Override
			public void action(Object a) {
				defTask();
				initAVG();
				onLoading();
			}
		});
		this.avgProcess = new RealtimeProcess() {

			@Override
			public void run(LTimerContext time) {
				if (isGameRunning) {
					if (messageDesktop != null) {
						switch (_speedMode) {
						default:
						case Normal:
							messageDesktop.update(LSystem.SECOND / 2);
							break;
						case SuperSlow:
							messageDesktop.update(LSystem.MSEC * 20);
							break;
						case Slow:
							messageDesktop.update(LSystem.MSEC * 40);
							break;
						case FewSlow:
							messageDesktop.update(LSystem.MSEC * 60);
							break;
						case Fast:
							messageDesktop.update(LSystem.SECOND);
							break;
						case Quickly:
							for (int i = 0; i < 2; i++) {
								messageDesktop.update(LSystem.SECOND);
							}
							break;
						case Flash:
							for (int i = 0; i < 3; i++) {
								messageDesktop.update(LSystem.SECOND);
							}
							break;
						}
					}
					if (effectSprites != null) {
						effectSprites.update(time.timeSinceLastUpdate);
					}
					if (autoPlay) {
						playAutoNext();
					}
				}
			}
		};
		setSpeedMode(_speedMode);
		avgProcess.setDelay(_plapDelay);
		RealtimeProcessManager.get().addProcess(avgProcess);

	}

	private synchronized void initDesktop() {
		if (messageDesktop != null && effectSprites != null) {
			return;
		}
		this.messageDesktop = new Desktop(this, getWidth(), getHeight());
		this.effectSprites = new Sprites(this, getWidth(), getHeight());
		if (_dialogTexture == null) {
			Image tmp = Image.createImage(getWidth() - 20, getHeight() / 2 - 20);
			Canvas g = tmp.getCanvas();
			g.setColor(0, 0, 0, 125);
			g.fillRect(0, 0, tmp.getWidth(), tmp.getHeight());
			g = null;
			_dialogTexture = tmp.texture();
			if (tmp != null) {
				tmp.close();
				tmp = null;
			}
		}
		this.messageUI = new LMessage(_font, _dialogTexture, 0, 0);
		this.messageUI.setFontColor(fontColor);
		int size = (int) (messageUI.getWidth() / (messageUI.getMessageFont().getSize()));
		if (size % 2 != 0) {
			size = size - 3;
		} else {
			size = size - 4;
		}
		this.messageUI.setMessageLength(size);
		this.messageUI.setLocation((getWidth() - messageUI.getWidth()) / 2, getHeight() - messageUI.getHeight() - 10);
		this.messageUI.setTopOffset(-5);
		this.messageUI.setVisible(false);
		this.selectUI = new LSelect(_font, _dialogTexture, messageUI.x(), messageUI.y());
		this.selectUI.setFontColor(fontColor);
		this.selectUI.setTopOffset(5);
		this.scrCG = new AVGCG(this);
		this.messageDesktop.add(messageUI);
		this.messageDesktop.add(selectUI);
		this.selectUI.setVisible(false);
	}

	/**
	 * 设置一个实现了IFont的字体到AVG系统中(一般使用LFont或BMFont)
	 * 
	 * @param font
	 */
	public AVGScreen setFont(final IFont font) {
		this._font = font;
		if (messageUI != null) {
			messageUI.setMessageFont(font);
		}
		if (selectUI != null) {
			selectUI.setMessageFont(font);
		}
		return this;
	}

	public IFont getFont() {
		if (messageUI != null) {
			return messageUI.getMessageFont();
		}
		return this._font;
	}

	public abstract boolean nextScript(String message);

	public abstract void onSelect(String message, int type);

	public abstract void initMessageConfig(final LMessage message);

	public abstract void initSelectConfig(final LSelect select);

	public abstract void initCommandConfig(final Command command);

	final public void selectUI(int type) {
		if (command != null) {
			command.select(type);
			isSelectMessage = false;
		}
	}

	final public String getSelect() {
		if (command != null) {
			return command.getSelect();
		}
		return null;
	}

	@Override
	public Screen add(LComponent c) {
		if (messageDesktop == null) {
			initDesktop();
		}
		if ((c instanceof LClickButton) || (c instanceof LButton)) {
			c.setLayer(clickButtonLayer);
		}
		messageDesktop.add(c);
		return this;
	}

	@Override
	public Screen addTouchLimit(LObject<?> c) {
		if (messageDesktop == null) {
			initDesktop();
		}
		if (!(c instanceof LClickButton)) {
			super.addTouchLimit(c);
		}
		return this;
	}

	@Override
	public Screen add(ISprite s) {
		if (effectSprites == null) {
			initDesktop();
		}
		effectSprites.add(s);
		return this;
	}

	@Override
	public Screen remove(ISprite sprite) {
		effectSprites.remove(sprite);
		return this;
	}

	@Override
	public Screen remove(LComponent comp) {
		messageDesktop.remove(comp);
		return this;
	}

	@Override
	public Screen removeAll() {
		super.removeAll();
		effectSprites.removeAll();
		messageDesktop.clear();
		_currentTasks.clear();
		return this;
	}

	@Override
	final public void draw(GLEx g) {
		if (!isGameRunning || !isOnLoadComplete() || isClosed()) {
			return;
		}
		if (scrCG == null) {
			return;
		}
		if (scrCG.sleep == 0) {
			scrCG.paint(g);
			drawScreen(g);
			if (messageDesktop != null) {
				messageDesktop.createUI(g);
			}
			if (effectSprites != null) {
				effectSprites.createUI(g);
			}
		} else {
			scrCG.sleep--;
			if (gameColor != null) {
				float alpha = (float) (scrCG.sleepMax - scrCG.sleep) / scrCG.sleepMax;
				if (alpha > 0.1f && alpha < 1.0f) {
					if (scrCG.getBackgroundCG() != null) {
						g.draw(scrCG.getBackgroundCG(), 0, 0);
					}
					LColor c = g.getColor();
					g.setColor(gameColor.r, gameColor.g, gameColor.b, alpha);
					g.fillRect(0, 0, getWidth(), getHeight());
					g.setColor(c);
				} else {
					LColor c = g.getColor();
					g.setColor(gameColor);
					g.fillRect(0, 0, getWidth(), getHeight());
					g.setColor(c);
				}
			}
			if (scrCG.sleep <= 0) {
				scrCG.sleep = 0;
				gameColor = null;
			}
			g.setAlpha(1.0f);
		}
	}

	public abstract void drawScreen(GLEx g);

	/**
	 * 读取一行AVG脚本命令
	 */
	public AVGScreen nextScript() {
		if (command != null && !isClosed() && isGameRunning) {
			for (; isScriptRunning = command.next();) {
				if (isSelectMessage) {
					continue;
				}
				String result = command.doExecute();
				if (result == null) {
					continue;
				}
				if (!nextScript(result)) {
					break;
				}

				TArray<String> commands = Conversion.splitToList(result, ' ');
				int size = commands.size;
				String cmdFlag = (String) commands.get(0);

				String mesFlag = null, orderFlag = null, lastFlag = null;
				if (size == 2) {
					mesFlag = (String) commands.get(1);
				} else if (size == 3) {
					mesFlag = (String) commands.get(1);
					orderFlag = (String) commands.get(2);
				} else if (size == 4) {
					mesFlag = (String) commands.get(1);
					orderFlag = (String) commands.get(2);
					lastFlag = (String) commands.get(3);
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_CLEAR) || cmdFlag.equalsIgnoreCase(CommandType.L_DEL)) {
					if (orderFlag == null) {
						if (messageDesktop != null) {
							messageUI.setVisible(false);
							selectUI.setVisible(false);
							effectSprites.clear();
							scrCG.clear();
							getSprites().removeAll();
							getDesktop().removeAll();
							_currentTasks.clear();
						}
					} else {
						effectSprites.removeName(orderFlag);
						messageDesktop.removeName(orderFlag);
						scrCG.remove(orderFlag);
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_TASK)) {
					if (mesFlag != null) {
						mesFlag = mesFlag.trim();
						// 如果是clear或del则清除任务
						if (CommandType.L_CLEAR.equalsIgnoreCase(mesFlag)
								|| CommandType.L_DEL.equalsIgnoreCase(mesFlag)) {
							_currentTasks.clear();
						} else {
							Task task = getTask(mesFlag);
							if (task != null) {
								// 注入参数
								int len = commands.size - 2;
								String[] args = new String[len];
								for (int i = 0; i < len; i++) {
									args[i] = commands.get(i + 2);
								}
								// 注入参数
								task.parameters(args);
								// 执行任务
								task.call();
								// 添加到当前任务集合中
								_currentTasks.add(task);
							}
						}
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_SPEED)) {
					if (mesFlag != null) {
						setSpeedMode(mesFlag);
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_LOCK)) {
					setLimitClick(true);
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_UNLOCK)) {
					setLimitClick(false);
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_OPTION)) {
					if (mesFlag != null) {
						if (CommandType.L_CLEAR.equalsIgnoreCase(mesFlag)
								|| CommandType.L_DEL.equalsIgnoreCase(mesFlag)) {
							if (orderFlag == null) {
								super.getDesktop().removeUIName("ClickButton");
							} else {
								super.getDesktop().removeTag(CommandType.L_OPTION + orderFlag);
							}
						} else {
							String text = null;
							LClickButton click = null;
							if (mesFlag.indexOf(',') != -1) {
								String[] optSize = StringUtils.split(mesFlag, ',');
								if (optSize.length == 4) {
									click = new LClickButton(text, (int) Float.parseFloat(optSize[0].trim()),
											(int) Float.parseFloat(optSize[1].trim()),
											(int) Float.parseFloat(optSize[2].trim()),
											(int) Float.parseFloat(optSize[3].trim()));
									// 载入跳转地址与图片
									if (orderFlag != null) {
										setOpt(click, orderFlag);
									}
								}
							} else {
								text = StringUtils.replace(mesFlag, "\"", "");
								if (orderFlag != null) {
									String[] optSize = StringUtils.split(orderFlag, ',');
									if (optSize.length == 4) {
										click = new LClickButton(text, (int) Float.parseFloat(optSize[0].trim()),
												(int) Float.parseFloat(optSize[1].trim()),
												(int) Float.parseFloat(optSize[2].trim()),
												(int) Float.parseFloat(optSize[3].trim()));
										// 载入图片
										if (lastFlag != null) {
											setOpt(click, lastFlag);
										}
									}
								}
							}
							if (click != null) {
								addOpt(click);
							}
						}
					}

					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_MESMOVE)) {
					// 空值时复位
					if (mesFlag == null) {
						int mesSize = (int) (messageUI.getWidth() / (messageUI.getMessageFont().getSize()));
						if (mesSize % 2 != 0) {
							mesSize = mesSize - 3;
						} else {
							mesSize = mesSize - 4;
						}
						this.messageUI.setMessageLength(mesSize);
						this.messageUI.setLocation((getWidth() - messageUI.getWidth()) / 2,
								getHeight() - messageUI.getHeight() - 10);
						this.messageUI.setTopOffset(-5);
						this.selectUI.setTopOffset(5);
					} else if (mesFlag != null) {
						if (orderFlag != null) {
							if (MathUtils.isNan(mesFlag) && (MathUtils.isNan(orderFlag))) {
								float x = Float.parseFloat(mesFlag);
								float y = Float.parseFloat(orderFlag);
								messageUI.setLocation(x, y);
								selectUI.setLocation(x, y);
							}
						} else {
							if (mesFlag.indexOf(',') == -1 && MathUtils.isNan(mesFlag)) {
								float v = Float.parseFloat(mesFlag);
								messageUI.setX(v);
								selectUI.setX(v);
							} else {
								String[] res = StringUtils.split(mesFlag, ',');
								String v1 = res[0].trim();
								String v2 = res[1].trim();
								if (res.length == 1 && MathUtils.isNan(v1)) {
									float v = Float.parseFloat(v1);
									messageUI.setX(v);
									selectUI.setX(v);
								} else if (res.length == 2 && MathUtils.isNan(v1) && MathUtils.isNan(v2)) {
									float x = Float.parseFloat(v1);
									float y = Float.parseFloat(v2);
									messageUI.setLocation(x, y);
									selectUI.setLocation(x, y);
								}
							}
						}
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_APLAY)) {
					autoPlay = true;
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_ASTOP)) {
					autoPlay = false;
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_ADELAY)) {
					if (mesFlag != null) {
						if (MathUtils.isNan(mesFlag)) {
							autoTimer.setDelay(Integer.parseInt(mesFlag));
						}
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_WAIT)) {
					scrFlag = true;
					break;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_SNOW) || cmdFlag.equalsIgnoreCase(CommandType.L_RAIN)
						|| cmdFlag.equalsIgnoreCase(CommandType.L_PETAL)) {
					if (effectSprites != null) {
						boolean flag = false;
						ISprite[] ss = effectSprites.getSprites();
						for (int i = 0; i < ss.length; i++) {
							ISprite s = ss[i];
							if (s instanceof NaturalEffect) {
								flag = true;
								break;
							}
						}
						if (!flag) {
							if (cmdFlag.equalsIgnoreCase(CommandType.L_SNOW)) {
								effectSprites.add(NaturalEffect.getSnowEffect());
							} else if (cmdFlag.equalsIgnoreCase(CommandType.L_RAIN)) {
								effectSprites.add(NaturalEffect.getRainEffect());
							} else if (cmdFlag.equalsIgnoreCase(CommandType.L_PETAL)) {
								effectSprites.add(NaturalEffect.getPetalEffect());
							} else if (cmdFlag.equalsIgnoreCase(CommandType.L_THUNDER)) {
								effectSprites.add(NaturalEffect.getThunderEffect());
							}
						}

					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_SNOWSTOP) || cmdFlag.equalsIgnoreCase(CommandType.L_RAINSTOP)
						|| cmdFlag.equalsIgnoreCase(CommandType.L_PETALSTOP)) {
					if (effectSprites != null) {
						ISprite[] ss = effectSprites.getSprites();
						for (int i = 0; i < ss.length; i++) {
							ISprite s = ss[i];
							if (s instanceof NaturalEffect) {
								NaturalType naturalType = ((NaturalEffect) s).getNaturalType();
								if (cmdFlag.equalsIgnoreCase(CommandType.L_SNOWSTOP)) {
									if (naturalType == NaturalType.Snow) {
										effectSprites.remove(s);
									}
								} else if (cmdFlag.equalsIgnoreCase(CommandType.L_RAINSTOP)) {
									if (naturalType == NaturalType.Rain) {
										effectSprites.remove(s);
									}
								} else if (cmdFlag.equalsIgnoreCase(CommandType.L_PETALSTOP)) {
									if (naturalType == NaturalType.Petal) {
										effectSprites.remove(s);
									}
								} else if (cmdFlag.equalsIgnoreCase(CommandType.L_THUNDER)) {
									if (naturalType == NaturalType.Thunder) {
										effectSprites.remove(s);
									}
								}
							}
						}

					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_PLAY)) {
					playSound(mesFlag, false);
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_PLAYLOOP)) {
					playSound(mesFlag, true);
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_PLAYSTOP)) {
					if (mesFlag != null && mesFlag.length() > 0) {
						stopSound(mesFlag);
					} else {
						stopSound();
					}
					continue;
				}

				if (cmdFlag.equalsIgnoreCase(CommandType.L_FADEOUT) || cmdFlag.equalsIgnoreCase(CommandType.L_FADEIN)) {
					this.scrFlag = true;
					this.gameColor = new LColor(mesFlag);
					if (effectSprites != null) {
						effectSprites.removeAll();
						if (cmdFlag.equalsIgnoreCase(CommandType.L_FADEIN)) {
							effectSprites.add(FadeEffect.getInstance(ISprite.TYPE_FADE_IN, 30, gameColor));
						} else {
							effectSprites.add(FadeEffect.getInstance(ISprite.TYPE_FADE_OUT, 30, gameColor));
						}
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_SELLEN)) {
					if (mesFlag != null) {
						if (MathUtils.isNan(mesFlag)) {
							selectUI.setLeftOffset(Integer.parseInt(mesFlag));
						}
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_SELTOP)) {
					if (mesFlag != null) {
						if (MathUtils.isNan(mesFlag)) {
							selectUI.setTopOffset(Integer.parseInt(mesFlag));
						}
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_MESLEN)) {
					if (mesFlag != null) {
						if (MathUtils.isNan(mesFlag)) {
							messageUI.setMessageLength(Integer.parseInt(mesFlag));
						}
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_MESTOP)) {
					if (mesFlag != null) {
						if (MathUtils.isNan(mesFlag)) {
							messageUI.setTopOffset(Integer.parseInt(mesFlag));
						}
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_MESLEFT)) {
					if (mesFlag != null) {
						if (MathUtils.isNan(mesFlag)) {
							messageUI.setLeftOffset(Integer.parseInt(mesFlag));
						}
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_MESCOLOR)) {
					if (mesFlag != null) {
						messageUI.setFontColor(new LColor(mesFlag));
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_MES)) {
					if (selectUI.isVisible()) {
						selectUI.setVisible(false);
					}
					scrFlag = true;
					String nMessage = mesFlag;
					messageUI.setMessage(StringUtils.replace(nMessage, "&", " "));
					messageUI.setVisible(true);
					break;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_MESSTOP)) {
					scrFlag = true;
					messageUI.setVisible(false);
					selectUI.setVisible(false);
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_SELECT)) {
					if (mesFlag != null) {
						mesFlag = mesFlag.trim();
						int selectStart = mesFlag.indexOf("{");
						int selectEnd = mesFlag.lastIndexOf("}");
						if (selectStart != -1 && selectEnd != -1) {
							String messageInfo = null;
							if (mesFlag.startsWith("\"")) {
								int startFlag = mesFlag.indexOf('"');
								int endFlag = mesFlag.lastIndexOf('"');
								if (startFlag != -1 && endFlag != -1 && endFlag > startFlag) {
									messageInfo = mesFlag.substring(startFlag + 1, endFlag);
								}
							}
							final String selectList = mesFlag.substring(selectStart + 1, selectEnd).trim();
							if (messageUI.isVisible()) {
								messageUI.setVisible(false);
							}
							selectUI.setVisible(true);
							scrFlag = true;
							isSelectMessage = true;
							limitClickd = true;
							String[] list = StringUtils.split(selectList, ',');
							final int selectLength = list.length;
							final int len = selectLength / 2;
							final TArray<String> selects = new TArray<String>(len);
							final TArray<String> items = new TArray<String>(len);
							for (int i = 0; i < selectLength; i++) {
								if (i % 2 == 0) {
									selects.add(list[i]);
								} else {
									items.add(list[i]);
								}
							}
							selectUI.setMessage(messageInfo, selects);
							addProcess(new RealtimeProcess() {

								@Override
								public void run(LTimerContext time) {
									selectUI.SetClick(new SelectClick(items));
									kill();
								}
							});
							break;
						} else {
							_selectMessage = mesFlag;
						}
					} else {
						_selectMessage = mesFlag;
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_SELECTS)) {
					if (messageUI.isVisible()) {
						messageUI.setVisible(false);
					}
					selectUI.SetClick(null);
					selectUI.setVisible(true);
					scrFlag = true;
					isSelectMessage = true;
					String[] selects = command.getReads();
					selectUI.setMessage(_selectMessage, selects);
					break;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_SHAKE)) {
					scrCG.shakeNumber = Integer.valueOf(mesFlag).intValue();
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_CGWAIT)) {
					scrFlag = false;
					break;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_SLEEP)) {
					scrCG.sleep = Integer.valueOf(mesFlag).intValue();
					scrCG.sleepMax = Integer.valueOf(mesFlag).intValue();
					scrFlag = false;
					break;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_FLASH)) {
					scrFlag = true;
					String[] colors = null;
					if (mesFlag != null) {
						colors = mesFlag.split(",");
					} else {
						colors = new String[] { "0", "0", "0" };
					}
					if (gameColor == null && colors != null && colors.length == 3) {
						gameColor = new LColor(Integer.valueOf(colors[0]).intValue(),
								Integer.valueOf(colors[1]).intValue(), Integer.valueOf(colors[2]).intValue());
						scrCG.sleep = 20;
						scrCG.sleepMax = scrCG.sleep;
						scrFlag = false;
					} else {
						gameColor = null;
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_GB)) {
					if (mesFlag == null) {
						return this;
					}
					if (mesFlag.equalsIgnoreCase("none")) {
						scrCG.noneBackgroundCG();
					} else {
						scrCG.setBackgroundCG(mesFlag);
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_CG)) {

					if (mesFlag == null) {
						return this;
					}
					if (scrCG != null && scrCG.count() > LSystem.DEFAULT_MAX_CACHE_SIZE) {
						scrCG.close();
					}
					if (mesFlag.equalsIgnoreCase(CommandType.L_DEL)) {
						if (orderFlag != null) {
							scrCG.remove(orderFlag);
						} else {
							scrCG.close();
						}
					} else if (lastFlag != null && CommandType.L_TO.equalsIgnoreCase(orderFlag)) {
						scrCG.replace(mesFlag, lastFlag);
					} else {
						int x = 0, y = 0;
						if (orderFlag != null) {
							x = Integer.parseInt(orderFlag);
						}
						if (size >= 4) {
							y = Integer.parseInt((String) commands.get(3));
						}
						final int tx = x;
						final int ty = y;
						final String name = mesFlag;
						scrCG.add(name, tx, ty, getWidth(), getHeight());
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_EXIT)) {
					scrFlag = true;
					isGameRunning = false;
					// 用户的锅
					onExit();
					break;
				}
			}
		}
		return this;
	}

	// todo
	public abstract void onExit();

	private void playAutoNext() {
		if (tasking()) {
			return;
		}
		if (!autoTimer.action(elapsedTime)) {
			return;
		}
		if (scrCG.sleep != 0) {
			return;
		}
		if (isSelectMessage) {
			return;
		}
		if (messageUI.isVisible() && !messageUI.isComplete()) {
			return;
		}
		nextScript();
	}

	private boolean tasking() {
		return _currentTasks.size() > 0;
	}

	public AVGScreen click() {
		// 如果存在未完成任务，则不允许继续脚本
		if (tasking()) {
			return this;
		}
		if (limitClickd) {
			return this;
		}
		if (!isGameRunning) {
			return this;
		}
		if (messageUI.isVisible() && !messageUI.isComplete()) {
			return this;
		}
		boolean isNext = false;
		if (!isSelectMessage && scrCG.sleep <= 0) {
			if (!scrFlag) {
				scrFlag = true;
			}
			if (!screenClickd && messageUI.isVisible()) {
				isNext = messageUI.intersects(getTouchX(), getTouchY());
			} else {
				isNext = true;
			}
		} else if ((scrFlag && selectUI.getResultIndex() != -1) && selectUI.isClick()) {
			onSelect(_selectMessage, selectUI.getResultIndex());
			isNext = selectUI.intersects(getTouchX(), getTouchY());
			if ((LSystem.base() != null && LSystem.base().isMobile() || LSystem.base().setting.emulateTouch)
					? _clickcount++ >= _mobile_select_valid_limit : _clickcount > -1) {
				messageUI.setVisible(false);
				clearSelectMessage();
			}
		}
		if (isNext && !isSelectMessage) {
			nextScript();
		}
		return this;
	}

	public AVGScreen initCommandConfig(String fileName) {
		if (fileName == null) {
			return this;
		}
		Command.resetCache();
		if (command == null) {
			command = new Command(fileName);
		} else {
			command.formatCommand(fileName);
		}
		if (messageUI.getFont() instanceof LFont) {
			LSTRDictionary.get().bind((LFont) messageUI.getFont(), command.getCommands());
		}
		initCommandConfig(command);
		nextScript();
		return this;
	}

	public boolean isScrFlag() {
		return scrFlag;
	}

	public String getSelectMessage() {
		return _selectMessage;
	}

	private void initAVG() {
		this.initDesktop();
		this.initMessageConfig(messageUI);
		this.initSelectConfig(selectUI);
		this.initCommandConfig(_scriptName);
	}

	public abstract void onLoading();

	public boolean isCommandGo() {
		return isScriptRunning;
	}

	public LMessage messageConfig() {
		return messageUI;
	}

	public AVGScreen setDialogImage(LTexture dialog) {
		this._dialogTexture = dialog;
		return this;
	}

	public LTexture getDialogImage() {
		return _dialogTexture;
	}

	public int getPause() {
		return _plapDelay;
	}

	public AVGScreen setPause(int pause) {
		return setDelay(pause);
	}

	public int getDelay() {
		return _plapDelay;
	}

	public AVGScreen setDelay(int d) {
		this._plapDelay = d;
		if (_speedMode == SpeedMode.Flash || _speedMode == SpeedMode.Quickly || _speedMode == SpeedMode.Fast) {
			_plapDelay = 0;
		}
		if (avgProcess != null) {
			avgProcess.setDelay(_plapDelay);
		}
		return this;
	}

	public Desktop getAvgDesktop() {
		return messageDesktop;
	}

	public LTexture getDialog() {
		return this.getDialogImage();
	}

	public AVGScreen setDialog(LTexture dialog) {
		this.setDialogImage(dialog);
		return this;
	}

	public LMessage getMessage() {
		return messageUI;
	}

	public AVGScreen setMessage(LMessage message) {
		this.messageUI = message;
		return this;
	}

	public boolean isRunning() {
		return isGameRunning;
	}

	public AVGScreen setRunning(boolean running) {
		this.isGameRunning = running;
		return this;
	}

	public AVGCG getScrCG() {
		return scrCG;
	}

	public AVGScreen setScrCG(AVGCG scrCG) {
		this.scrCG = scrCG;
		return this;
	}

	public String getScriptName() {
		return _scriptName;
	}

	public AVGScreen setScriptName(String name) {
		this._scriptName = name;
		return this;
	}

	public Command getCommand() {
		return command;
	}

	public AVGScreen setCommand(Command command) {
		this.command = command;
		return this;
	}

	public boolean isSelectMessage() {
		return isSelectMessage;
	}

	public LSelect getLSelect() {
		return selectUI;
	}

	public int getSleep() {
		return scrCG.sleep;
	}

	public AVGScreen setSleep(int sleep) {
		scrCG.sleep = sleep;
		return this;
	}

	public int getSleepMax() {
		return scrCG.sleepMax;
	}

	public AVGScreen setSleepMax(int sleepMax) {
		scrCG.sleepMax = sleepMax;
		return this;
	}

	public Sprites getAvgSprites() {
		return effectSprites;
	}

	public AVGScreen setCommandGo(boolean isRunning) {
		this.isScriptRunning = isRunning;
		return this;
	}

	public AVGScreen setScrFlag(boolean scrFlag) {
		this.scrFlag = scrFlag;
		return this;
	}

	public boolean isScriptLocked() {
		return limitClickd;
	}

	public AVGScreen setScriptLocked(boolean locked) {
		this.limitClickd = locked;
		return this;
	}

	@Override
	public void alter(LTimerContext timer) {
		if (_currentTasks.size() > 0) {
			for (; _currentTasks.hashNext();) {
				Task task = _currentTasks.next();
				if (task.completed()) {
					_currentTasks.remove(task);
				}
			}
			_currentTasks.stopNext();
		}
		if (scrCG != null) {
			scrCG.update(timer);
		}
	}

	@Override
	public void onKeyDown(GameKey e) {

	}

	@Override
	public void onKeyUp(GameKey e) {

	}

	@Override
	public void touchDown(GameTouch touch) {
		if (messageDesktop != null) {
			messageDesktop.processEvents();
		}
		click();
	}

	@Override
	public void touchMove(GameTouch e) {
		if (messageDesktop != null) {
			messageDesktop.processEvents();
		}
	}

	@Override
	public void touchUp(GameTouch e) {
		if (messageDesktop != null) {
			messageDesktop.processEvents();
		}
	}

	@Override
	public void touchDrag(GameTouch e) {
		if (messageDesktop != null) {
			messageDesktop.processEvents();
		}
	}

	public boolean isAutoPlay() {
		return autoPlay;
	}

	public AVGScreen setAutoPlay(boolean autoPlay) {
		this.autoPlay = autoPlay;
		return this;
	}

	public AVGScreen setAutoDelay(long d) {
		autoTimer.setDelay(d);
		return this;
	}

	public long getAutoDelay() {
		return autoTimer.getDelay();
	}

	public LTimer getAutoTimer() {
		return autoTimer;
	}

	public AVGScreen setAutoTimer(LTimer autoTimer) {
		this.autoTimer = autoTimer;
		return this;
	}

	public boolean isLimitClick() {
		return limitClickd;
	}

	/**
	 * 档次参数为true时，将无法点击消息框或画面触发脚本继续
	 * 
	 * @param limitClick
	 */
	public AVGScreen setLimitClick(boolean lc) {
		this.limitClickd = lc;
		return this;
	}

	public boolean isScreenClick() {
		return screenClickd;
	}

	/**
	 * 此标记用于判断是否next仅在点击message时继续（true不是，false是）
	 * 
	 * @param screenClick
	 */
	public AVGScreen setScreenClick(boolean screenClick) {
		this.screenClickd = screenClick;
		return this;
	}

	public SpeedMode getSpeedMode() {
		return _speedMode;
	}

	/**
	 * 设置当前AVG的文字显示速度默认
	 * 
	 * @param m
	 */
	public AVGScreen setSpeedMode(SpeedMode m) {
		this._speedMode = m;
		if (_speedMode == SpeedMode.Flash || _speedMode == SpeedMode.Quickly || _speedMode == SpeedMode.Fast) {
			_plapDelay = 0;
		}
		return this;
	}

	/**
	 * 设置当前AVG的文字显示速度默认
	 * 
	 * @param speedName
	 */
	public AVGScreen setSpeedMode(String speedName) {
		setSpeedMode(toSpeedMode(speedName));
		return this;
	}

	public int getClickcount() {
		return _clickcount;
	}

	public int getMobileSelectValidLimit() {
		return _mobile_select_valid_limit;
	}

	/**
	 * 此处限制移动环境时，遇到选择时需要点击的次数
	 * 
	 * @param v
	 */
	public AVGScreen setMobileSelectValidLimit(int v) {
		this._mobile_select_valid_limit = v;
		return this;
	}

	/**
	 * 清空当前任务
	 */
	public AVGScreen clearCurrentTasks() {
		_currentTasks.clear();
		return this;
	}

	public Array<Task> getCurrentTasks() {
		return _currentTasks;
	}

	public ListMap<String, AVGScreen.Task> getTasks() {
		return _tasks;
	}

	public int getClickButtonLayer() {
		return clickButtonLayer;
	}

	public AVGScreen setClickButtonLayer(int clickButtonLayer) {
		this.clickButtonLayer = clickButtonLayer;
		return this;
	}

	@Override
	public AVGScreen setFontColor(LColor color) {
		if (color == null) {
			return this;
		}
		this.fontColor = color;
		return this;
	}

	@Override
	public LColor getFontColor() {
		return fontColor.cpy();
	}

	@Override
	public void close() {
		isGameRunning = false;
		if (avgProcess != null) {
			avgProcess.kill();
			avgProcess = null;
		}
		if (messageDesktop != null) {
			messageDesktop.close();
			messageDesktop = null;
		}
		if (effectSprites != null) {
			effectSprites.close();
			effectSprites = null;
		}
		if (command != null) {
			command = null;
		}
		if (messageUI != null) {
			messageUI.close();
			messageUI = null;
		}
		if (scrCG != null) {
			scrCG.close();
			scrCG = null;
		}
		if (_dialogTexture != null) {
			if (_dialogTexture.getSource() != null) {
				_dialogTexture.close();
				_dialogTexture = null;
			}
		}
		_currentTasks.clear();
		_tasks.clear();
	}

}
