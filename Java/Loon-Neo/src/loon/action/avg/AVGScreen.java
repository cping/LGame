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
import loon.action.sprite.effect.PetalKernel;
import loon.action.sprite.effect.RainKernel;
import loon.action.sprite.effect.SnowKernel;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.component.Desktop;
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

	// 手机屏幕手机遇到选择框时，需要点击的次数(归0则无限制，此参数的作用在于防止触屏误点选项)
	private int _mobile_select_valid_limit = 1;

	// 当前任务集合
	private Array<Task> _currentTasks = new Array<AVGScreen.Task>();

	// 任务集合
	private ListMap<String, AVGScreen.Task> _tasks = new ListMap<String, AVGScreen.Task>(
			20);

	private boolean isSelectMessage, scrFlag, isRunning, running;

	private IFont _font;

	private int delay;

	private String _scriptName;

	private String selectMessage;

	private String dialogFileName;

	// 若需要任意处点击皆可继续脚本，此标记应为true
	private boolean screenClick = false;

	// 若需要点击触发脚本继续的功能暂时失效，此处应为true
	private boolean limitClick = false;

	// 自动播放的延迟时间
	private LTimer autoTimer = new LTimer(LSystem.SECOND);

	private LColor color;

	protected Command command;

	protected LTexture dialog;

	protected AVGCG scrCG;

	protected LSelect select;

	protected LMessage message;

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
			if (_items != null && command != null && select != null) {
				if ((LSystem.base() != null && (LSystem.base().isMobile() || LSystem
						.base().setting.emulateTouch)) ? _clickcount++ >= _mobile_select_valid_limit
						: _clickcount > -1) {
					int idx = select.getResultIndex();
					if (idx != -1) {
						String gotoFlag = _items.get(idx);
						if (MathUtils.isNan(gotoFlag)) {
							command.gotoIndex((int) Double
									.parseDouble(gotoFlag));
						} else {
							command.gotoIndex(gotoFlag);
						}
						select.SetClick(null);
						select.setVisible(false);
						limitClick = false;
						scrFlag = false;
						isSelectMessage = false;
						_clickcount = 0;
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
	 * 默认任务（同时也是任务接口实现示例，Task主要就是给用户自行扩展的）
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
				boolean stop = toast.isStop()
						&& (toast.getOpacity() <= 0.1f || !toast.isVisible());
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
					getSprites().remove(
							transition.getTransitionListener().getSprite());
				}
				return stop;
			}

			@Override
			public void call() {
				transition = LTransition.newTransition(transStr, colorStr);
				getSprites()
						.add(transition.getTransitionListener().getSprite());
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

			private String _scriptName, scriptSource, scripteContext;

			private boolean isCompleted;

			@Override
			public boolean completed() {
				return isCompleted;
			}

			@Override
			public void call() {
				Entity entity = Entity.make(scriptSource, x, y);
				if (_scriptName != null) {
					entity.setName(_scriptName);
				} else {
					entity.setName(scriptSource);
				}
				if (scripteContext != null) {
					act(entity, scripteContext).start().setActionListener(
							new ActionListener() {

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
					scriptSource = list[0];
				} else if (list.length == 2) {
					scriptSource = list[0];
					if (MathUtils.isNan(list[1])) {
						x = y = Float.parseFloat(list[1]);
					}
				} else if (list.length == 3) {
					scriptSource = list[0];
					if (MathUtils.isNan(list[1])) {
						x = Float.parseFloat(list[1]);
					}
					if (MathUtils.isNan(list[2])) {
						y = Float.parseFloat(list[2]);
					}
				} else if (list.length == 4) {
					scriptSource = list[0];
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
					scripteContext = scriptInfo;
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
			this.dialogFileName = img.getSource();
			this.dialog = img;
		}
	}

	public AVGScreen(final String initscript) {
		this._font = LSystem.getSystemGameFont();
		this._scriptName = initscript;
		if (initscript == null) {
			return;
		}
	}

	private class OptClick implements ClickListener {

		private String label;

		public OptClick(String gotoFlag) {
			label = gotoFlag;
		}

		@Override
		public void UpClick(LComponent comp, float x, float y) {
			if (command != null && label != null) {
				command.gotoIndex(label);
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
	 * 添加选项按钮到游戏中
	 * 
	 * @param click
	 */
	private void addOpt(final LClickButton click) {
		if (command != null) {
			GameProcess process = new RealtimeProcess() {

				@Override
				public void run(LTimerContext time) {
					click.Tag = CommandType.L_OPTION
							+ (click.getText() == null ? command.getIndex()
									: click.getText());
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
		this.delay = 60;
		if (dialog == null && dialogFileName != null) {
			this.dialog = LTextures.loadTexture(dialogFileName);
		}
		this.running = true;
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
				if (running) {
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
		avgProcess.setDelay(delay);
		RealtimeProcessManager.get().addProcess(avgProcess);

	}

	private synchronized void initDesktop() {
		if (messageDesktop != null && effectSprites != null) {
			return;
		}
		this.messageDesktop = new Desktop(this, getWidth(), getHeight());
		this.effectSprites = new Sprites(this, getWidth(), getHeight());
		if (dialog == null) {
			Image tmp = Image
					.createImage(getWidth() - 20, getHeight() / 2 - 20);
			Canvas g = tmp.getCanvas();
			g.setColor(0, 0, 0, 125);
			g.fillRect(0, 0, tmp.getWidth(), tmp.getHeight());
			g = null;
			dialog = tmp.texture();
			if (tmp != null) {
				tmp.close();
				tmp = null;
			}
		}
		this.message = new LMessage(_font, dialog, 0, 0);
		this.message.setFontColor(LColor.white);
		int size = (int) (message.getWidth() / (message.getMessageFont()
				.getSize()));
		if (size % 2 != 0) {
			size = size - 3;
		} else {
			size = size - 4;
		}
		this.message.setMessageLength(size);
		this.message.setLocation((getWidth() - message.getWidth()) / 2,
				getHeight() - message.getHeight() - 10);
		this.message.setTopOffset(-5);
		this.message.setVisible(false);
		this.select = new LSelect(_font, dialog, message.x(), message.y());
		this.select.setTopOffset(5);
		this.scrCG = new AVGCG(this);
		this.messageDesktop.add(message);
		this.messageDesktop.add(select);
		this.select.setVisible(false);
	}

	/**
	 * 设置一个实现了IFont的字体到AVG系统中(一般使用LFont或BMFont)
	 * 
	 * @param font
	 */
	public AVGScreen setFont(final IFont font) {
		this._font = font;
		if (message != null) {
			message.setMessageFont(font);
		}
		if (select != null) {
			select.setMessageFont(font);
		}
		return this;
	}

	public IFont getFont() {
		if (message != null) {
			return message.getMessageFont();
		}
		return this._font;
	}

	public abstract boolean nextScript(String message);

	public abstract void onSelect(String message, int type);

	public abstract void initMessageConfig(final LMessage message);

	public abstract void initSelectConfig(final LSelect select);

	public abstract void initCommandConfig(final Command command);

	final public void select(int type) {
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
		messageDesktop.add(c);
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
		if (!running || !isOnLoadComplete() || isClose()) {
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
			if (color != null) {
				float alpha = (float) (scrCG.sleepMax - scrCG.sleep)
						/ scrCG.sleepMax;
				if (alpha > 0.1f && alpha < 1.0f) {
					if (scrCG.getBackgroundCG() != null) {
						g.draw(scrCG.getBackgroundCG(), 0, 0);
					}
					LColor c = g.getColor();
					g.setColor(color.r, color.g, color.b, alpha);
					g.fillRect(0, 0, getWidth(), getHeight());
					g.setColor(c);
				} else {
					LColor c = g.getColor();
					g.setColor(color);
					g.fillRect(0, 0, getWidth(), getHeight());
					g.setColor(c);
				}
			}
			if (scrCG.sleep <= 0) {
				scrCG.sleep = 0;
				color = null;
			}
			g.setAlpha(1.0f);
		}
	}

	public abstract void drawScreen(GLEx g);

	public void nextScript() {
		if (command != null && !isClose() && running) {
			for (; isRunning = command.next();) {
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
				if (cmdFlag.equalsIgnoreCase(CommandType.L_CLEAR)
						|| cmdFlag.equalsIgnoreCase(CommandType.L_DEL)) {
					if (orderFlag == null) {
						if (messageDesktop != null) {
							message.setVisible(false);
							select.setVisible(false);
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
								super.getDesktop().removeTag(
										CommandType.L_OPTION + orderFlag);
							}
						} else {
							String text = null;
							LClickButton click = null;
							if (mesFlag.indexOf(',') != -1) {
								String[] optSize = StringUtils.split(mesFlag,
										',');
								if (optSize.length == 4) {
									click = new LClickButton(text,
											(int) Float.parseFloat(optSize[0]
													.trim()),
											(int) Float.parseFloat(optSize[1]
													.trim()),
											(int) Float.parseFloat(optSize[2]
													.trim()),
											(int) Float.parseFloat(optSize[3]
													.trim()));
									// 载入跳转地址与图片
									if (orderFlag != null) {
										setOpt(click, orderFlag);
									}
								}
							} else {
								text = StringUtils.replace(mesFlag, "\"", "");
								if (orderFlag != null) {
									String[] optSize = StringUtils.split(
											orderFlag, ',');
									if (optSize.length == 4) {
										click = new LClickButton(text,
												(int) Float
														.parseFloat(optSize[0]
																.trim()),
												(int) Float
														.parseFloat(optSize[1]
																.trim()),
												(int) Float
														.parseFloat(optSize[2]
																.trim()),
												(int) Float
														.parseFloat(optSize[3]
																.trim()));
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
						int mesSize = (int) (message.getWidth() / (message
								.getMessageFont().getSize()));
						if (mesSize % 2 != 0) {
							mesSize = mesSize - 3;
						} else {
							mesSize = mesSize - 4;
						}
						this.message.setMessageLength(mesSize);
						this.message.setLocation(
								(getWidth() - message.getWidth()) / 2,
								getHeight() - message.getHeight() - 10);
						this.message.setTopOffset(-5);
						this.select.setTopOffset(5);
					} else if (mesFlag != null) {
						if (orderFlag != null) {
							if (MathUtils.isNan(mesFlag)
									&& (MathUtils.isNan(orderFlag))) {
								float x = Float.parseFloat(mesFlag);
								float y = Float.parseFloat(orderFlag);
								message.setLocation(x, y);
								select.setLocation(x, y);
							}
						} else {
							if (mesFlag.indexOf(',') == -1
									&& MathUtils.isNan(mesFlag)) {
								float v = Float.parseFloat(mesFlag);
								message.setX(v);
								select.setX(v);
							} else {
								String[] res = StringUtils.split(mesFlag, ',');
								String v1 = res[0].trim();
								String v2 = res[1].trim();
								if (res.length == 1 && MathUtils.isNan(v1)) {
									float v = Float.parseFloat(v1);
									message.setX(v);
									select.setX(v);
								} else if (res.length == 2
										&& MathUtils.isNan(v1)
										&& MathUtils.isNan(v2)) {
									float x = Float.parseFloat(v1);
									float y = Float.parseFloat(v2);
									message.setLocation(x, y);
									select.setLocation(x, y);
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
				if (cmdFlag.equalsIgnoreCase(CommandType.L_SNOW)
						|| cmdFlag.equalsIgnoreCase(CommandType.L_RAIN)
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
								effectSprites
										.add(NaturalEffect.getSnowEffect());
							} else if (cmdFlag
									.equalsIgnoreCase(CommandType.L_RAIN)) {
								effectSprites
										.add(NaturalEffect.getRainEffect());
							} else if (cmdFlag
									.equalsIgnoreCase(CommandType.L_PETAL)) {
								effectSprites.add(NaturalEffect
										.getPetalEffect());
							}
						}

					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_SNOWSTOP)
						|| cmdFlag.equalsIgnoreCase(CommandType.L_RAINSTOP)
						|| cmdFlag.equalsIgnoreCase(CommandType.L_PETALSTOP)) {
					if (effectSprites != null) {
						ISprite[] ss = effectSprites.getSprites();
						for (int i = 0; i < ss.length; i++) {
							ISprite s = ss[i];
							if (s instanceof NaturalEffect) {
								if (cmdFlag
										.equalsIgnoreCase(CommandType.L_SNOWSTOP)) {
									if (((NaturalEffect) s).getKernels()[0] instanceof SnowKernel) {
										effectSprites.remove(s);
									}
								} else if (cmdFlag
										.equalsIgnoreCase(CommandType.L_RAINSTOP)) {
									if (((NaturalEffect) s).getKernels()[0] instanceof RainKernel) {
										effectSprites.remove(s);
									}
								} else if (cmdFlag
										.equalsIgnoreCase(CommandType.L_PETALSTOP)) {
									if (((NaturalEffect) s).getKernels()[0] instanceof PetalKernel) {
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

				if (cmdFlag.equalsIgnoreCase(CommandType.L_FADEOUT)
						|| cmdFlag.equalsIgnoreCase(CommandType.L_FADEIN)) {
					this.scrFlag = true;
					this.color = new LColor(mesFlag);
					if (effectSprites != null) {
						effectSprites.removeAll();
						if (cmdFlag.equalsIgnoreCase(CommandType.L_FADEIN)) {
							effectSprites.add(FadeEffect.getInstance(
									ISprite.TYPE_FADE_IN, 30, color));
						} else {
							effectSprites.add(FadeEffect.getInstance(
									ISprite.TYPE_FADE_OUT, 30, color));
						}
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_SELLEN)) {
					if (mesFlag != null) {
						if (MathUtils.isNan(mesFlag)) {
							select.setLeftOffset(Integer.parseInt(mesFlag));
						}
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_SELTOP)) {
					if (mesFlag != null) {
						if (MathUtils.isNan(mesFlag)) {
							select.setTopOffset(Integer.parseInt(mesFlag));
						}
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_MESLEN)) {
					if (mesFlag != null) {
						if (MathUtils.isNan(mesFlag)) {
							message.setMessageLength(Integer.parseInt(mesFlag));
						}
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_MESTOP)) {
					if (mesFlag != null) {
						if (MathUtils.isNan(mesFlag)) {
							message.setTopOffset(Integer.parseInt(mesFlag));
						}
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_MESLEFT)) {
					if (mesFlag != null) {
						if (MathUtils.isNan(mesFlag)) {
							message.setLeftOffset(Integer.parseInt(mesFlag));
						}
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_MESCOLOR)) {
					if (mesFlag != null) {
						message.setFontColor(new LColor(mesFlag));
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_MES)) {
					if (select.isVisible()) {
						select.setVisible(false);
					}
					scrFlag = true;
					String nMessage = mesFlag;
					message.setMessage(StringUtils.replace(nMessage, "&", " "));
					message.setVisible(true);
					break;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_MESSTOP)) {
					scrFlag = true;
					message.setVisible(false);
					select.setVisible(false);
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
								if (startFlag != -1 && endFlag != -1
										&& endFlag > startFlag) {
									messageInfo = mesFlag.substring(
											startFlag + 1, endFlag);
								}
							}
							final String selectList = mesFlag.substring(
									selectStart + 1, selectEnd).trim();
							if (message.isVisible()) {
								message.setVisible(false);
							}
							select.setVisible(true);
							scrFlag = true;
							isSelectMessage = true;
							limitClick = true;
							String[] list = StringUtils.split(selectList, ',');
							final int selectLength = list.length;
							final int len = selectLength / 2;
							final TArray<String> selects = new TArray<String>(
									len);
							final TArray<String> items = new TArray<String>(len);
							for (int i = 0; i < selectLength; i++) {
								if (i % 2 == 0) {
									selects.add(list[i]);
								} else {
									items.add(list[i]);
								}
							}
							select.setMessage(messageInfo, selects);
							addProcess(new RealtimeProcess() {

								@Override
								public void run(LTimerContext time) {
									select.SetClick(new SelectClick(items));
									kill();
								}
							});
							break;
						} else {
							selectMessage = mesFlag;
						}
					} else {
						selectMessage = mesFlag;
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_SELECTS)) {
					if (message.isVisible()) {
						message.setVisible(false);
					}
					select.SetClick(null);
					select.setVisible(true);
					scrFlag = true;
					isSelectMessage = true;
					String[] selects = command.getReads();
					select.setMessage(selectMessage, selects);
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
					if (color == null && colors != null && colors.length == 3) {
						color = new LColor(Integer.valueOf(colors[0])
								.intValue(), Integer.valueOf(colors[1])
								.intValue(), Integer.valueOf(colors[2])
								.intValue());
						scrCG.sleep = 20;
						scrCG.sleepMax = scrCG.sleep;
						scrFlag = false;
					} else {
						color = null;
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_GB)) {
					if (mesFlag == null) {
						return;
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
						return;
					}
					if (scrCG != null
							&& scrCG.count() > LSystem.DEFAULT_MAX_CACHE_SIZE) {
						scrCG.close();
					}
					if (mesFlag.equalsIgnoreCase(CommandType.L_DEL)) {
						if (orderFlag != null) {
							scrCG.remove(orderFlag);
						} else {
							scrCG.close();
						}
					} else if (lastFlag != null
							&& CommandType.L_TO.equalsIgnoreCase(orderFlag)) {
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
					running = false;
					onExit();
					break;
				}
			}
		}
	}

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
		if (message.isVisible() && !message.isComplete()) {
			return;
		}
		nextScript();
	}

	private boolean tasking() {
		return _currentTasks.size() > 0;
	}

	public void click() {
		// 如果存在未完成任务，则不允许继续脚本
		if (tasking()) {
			return;
		}
		if (limitClick) {
			return;
		}
		if (!running) {
			return;
		}
		if (message.isVisible() && !message.isComplete()) {
			return;
		}
		boolean isNext = false;
		if (!isSelectMessage && scrCG.sleep <= 0) {
			if (!scrFlag) {
				scrFlag = true;
			}
			if (!screenClick && message.isVisible()) {
				isNext = message.intersects(getTouchX(), getTouchY());
			} else {
				isNext = true;
			}
		} else if (scrFlag && select.getResultIndex() != -1) {
			onSelect(selectMessage, select.getResultIndex());
			isNext = select.intersects(getTouchX(), getTouchY());
			if ((LSystem.base() != null && LSystem.base().isMobile() || LSystem
					.base().setting.emulateTouch) ? _clickcount++ >= _mobile_select_valid_limit
					: _clickcount > -1) {
				message.setVisible(false);
				select.setVisible(false);
				isSelectMessage = false;
				selectMessage = null;
				_clickcount = 0;
			}
		}
		if (isNext && !isSelectMessage) {
			nextScript();
		}
	}

	public void initCommandConfig(String fileName) {
		if (fileName == null) {
			return;
		}
		Command.resetCache();
		if (command == null) {
			command = new Command(fileName);
		} else {
			command.formatCommand(fileName);
		}
		if (message.getFont() instanceof LFont) {
			LSTRDictionary.get().bind((LFont) message.getFont(),
					command.getCommands());
		}
		initCommandConfig(command);
		nextScript();
	}

	public boolean isScrFlag() {
		return scrFlag;
	}

	public String getSelectMessage() {
		return selectMessage;
	}

	private void initAVG() {
		this.initDesktop();
		this.initMessageConfig(message);
		this.initSelectConfig(select);
		this.initCommandConfig(_scriptName);
	}

	public abstract void onLoading();

	public boolean isCommandGo() {
		return isRunning;
	}

	public LMessage messageConfig() {
		return message;
	}

	public void setDialogImage(LTexture dialog) {
		this.dialog = dialog;
	}

	public LTexture getDialogImage() {
		return dialog;
	}

	public int getPause() {
		return delay;
	}

	public void setPause(int pause) {
		setDelay(pause);
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int d) {
		this.delay = d;
		if (_speedMode == SpeedMode.Flash || _speedMode == SpeedMode.Quickly
				|| _speedMode == SpeedMode.Fast) {
			delay = 0;
		}
		if (avgProcess != null) {
			avgProcess.setDelay(delay);
		}
	}

	public Desktop getAvgDesktop() {
		return messageDesktop;
	}

	public LTexture getDialog() {
		return dialog;
	}

	public void setDialog(LTexture dialog) {
		this.dialog = dialog;
	}

	public LMessage getMessage() {
		return message;
	}

	public void setMessage(LMessage message) {
		this.message = message;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public AVGCG getScrCG() {
		return scrCG;
	}

	public void setScrCG(AVGCG scrCG) {
		this.scrCG = scrCG;
	}

	public String getScriptName() {
		return _scriptName;
	}

	public void setScriptName(String name) {
		this._scriptName = name;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public boolean isSelectMessage() {
		return isSelectMessage;
	}

	public LSelect getLSelect() {
		return select;
	}

	public int getSleep() {
		return scrCG.sleep;
	}

	public void setSleep(int sleep) {
		scrCG.sleep = sleep;
	}

	public int getSleepMax() {
		return scrCG.sleepMax;
	}

	public void setSleepMax(int sleepMax) {
		scrCG.sleepMax = sleepMax;
	}

	public Sprites getAvgSprites() {
		return effectSprites;
	}

	public void setCommandGo(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public void setScrFlag(boolean scrFlag) {
		this.scrFlag = scrFlag;
	}

	public boolean isLocked() {
		return limitClick;
	}

	public void setLocked(boolean locked) {
		this.limitClick = locked;
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

	public void setAutoPlay(boolean autoPlay) {
		this.autoPlay = autoPlay;
	}

	public void setAutoDelay(long d) {
		autoTimer.setDelay(d);
	}

	public long getAutoDelay() {
		return autoTimer.getDelay();
	}

	public LTimer getAutoTimer() {
		return autoTimer;
	}

	public void setAutoTimer(LTimer autoTimer) {
		this.autoTimer = autoTimer;
	}

	public boolean isLimitClick() {
		return limitClick;
	}

	/**
	 * 档次参数为true时，将无法点击消息框或画面触发脚本继续
	 * 
	 * @param limitClick
	 */
	public void setLimitClick(boolean lc) {
		this.limitClick = lc;
	}

	public boolean isScreenClick() {
		return screenClick;
	}

	/**
	 * 此标记用于判断是否next仅在点击message时继续（true不是，false是）
	 * 
	 * @param screenClick
	 */
	public void setScreenClick(boolean screenClick) {
		this.screenClick = screenClick;
	}

	public SpeedMode getSpeedMode() {
		return _speedMode;
	}

	/**
	 * 设置当前AVG的文字显示速度默认
	 * 
	 * @param m
	 */
	public void setSpeedMode(SpeedMode m) {
		this._speedMode = m;
		if (_speedMode == SpeedMode.Flash || _speedMode == SpeedMode.Quickly
				|| _speedMode == SpeedMode.Fast) {
			delay = 0;
		}
	}

	/**
	 * 设置当前AVG的文字显示速度默认
	 * 
	 * @param speedName
	 */
	public void setSpeedMode(String speedName) {
		setSpeedMode(toSpeedMode(speedName));
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
	public void setMobileSelectValidLimit(int v) {
		this._mobile_select_valid_limit = v;
	}

	/**
	 * 清空当前任务
	 */
	public void clearCurrentTasks() {
		_currentTasks.clear();
	}

	public Array<Task> getCurrentTasks() {
		return _currentTasks;
	}

	public ListMap<String, AVGScreen.Task> getTasks() {
		return _tasks;
	}

	@Override
	public void close() {
		running = false;
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
		if (message != null) {
			message.close();
			message = null;
		}
		if (scrCG != null) {
			scrCG.close();
			scrCG = null;
		}
		if (dialog != null) {
			if (dialog.getSource() != null) {
				dialog.close();
				dialog = null;
			}
		}
		_currentTasks.clear();
		_tasks.clear();
	}

}
