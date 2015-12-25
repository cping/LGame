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
import loon.Screen;
import loon.action.avg.drama.Command;
import loon.action.avg.drama.CommandType;
import loon.action.avg.drama.Conversion;
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
import loon.event.ClickListener;
import loon.event.GameKey;
import loon.event.GameTouch;
import loon.event.Updateable;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public abstract class AVGScreen extends Screen {

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

	private SpeedMode speedMode = SpeedMode.Normal;

	private int clickcount = 0;

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
			if (_items != null && command != null && comp instanceof LSelect) {
				if ((LSystem.base() != null && LSystem.base().isMobile()) ? clickcount++ >= 1
						: clickcount > -1) {
					LSelect select = (LSelect) comp;
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
						clickcount = 0;
						nextScript();
					}
				}
			}
		}

		@Override
		public void DragClick(LComponent comp, float x, float y) {

		}

	}

	private boolean isSelectMessage, scrFlag, isRunning, running;

	private int delay;

	private String scriptName;

	private String selectMessage;

	private String dialogFileName;

	// 若需要任意处点击皆可继续脚本，此标记应为true
	private boolean screenClick = false;

	// 若需要点击触发脚本继续的功能暂时失效，此处应为true
	private boolean limitClick = false;

	private LTimer autoTimer = new LTimer(LSystem.SECOND);

	private LColor color;

	protected Command command;

	protected LTexture dialog;

	protected AVGCG scrCG;

	protected LSelect select;

	protected LMessage message;

	protected Desktop desktop;

	protected Sprites sprites;

	private RealtimeProcess avgProcess;

	private boolean autoPlay;

	public AVGScreen(final String initscript, final String initdialog) {
		this(initscript, LTextures.loadTexture(initdialog));
	}

	public AVGScreen(final String initscript, final LTexture img) {
		if (initscript == null) {
			return;
		}
		this.scriptName = initscript;
		if (img != null) {
			this.dialogFileName = img.getSource();
			this.dialog = img;
		}
	}

	public AVGScreen(final String initscript) {
		if (initscript == null) {
			return;
		}
		this.scriptName = initscript;
	}

	@Override
	public void onCreate(int width, int height) {
		super.onCreate(width, height);
		this.setRepaintMode(Screen.SCREEN_NOT_REPAINT);
		this.delay = 30;
		if (dialog == null && dialogFileName != null) {
			this.dialog = LTextures.loadTexture(dialogFileName);
		}
		this.running = true;
	}

	private final void setOpt(LClickButton click, String order) {
		int startFlag = order.indexOf('{');
		int endFlag = order.lastIndexOf('}');

		if (startFlag != -1 && endFlag != -1 && endFlag > startFlag) {

			String gotoMes = order.substring(startFlag + 1, endFlag).trim();

			final String[] result = StringUtils.split(gotoMes, ',');
			if (result.length > 1) {
				click.setTexture(LTextures.loadTexture(result[1]));
				click.setGrayButton(true);
			}
			click.SetClick(new ClickListener() {

				@Override
				public void UpClick(LComponent comp, float x, float y) {
					if (command != null) {
						command.gotoIndex(result[0]);
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
			});

		} else {

			click.setTexture(LTextures.loadTexture(order));
			click.setGrayButton(true);
		}
	}

	@Override
	public final void onLoad() {

	}

	@Override
	public final void onLoaded() {
		LSystem.load(new Updateable() {

			@Override
			public void action(Object a) {
				initAVG();
				onLoading();
			}
		});
		this.avgProcess = new RealtimeProcess() {

			@Override
			public void run(LTimerContext time) {
				if (running) {
					if (desktop != null) {
						switch (speedMode) {
						default:
						case Normal:
							desktop.update(LSystem.SECOND / 2);
							break;
						case SuperSlow:
							desktop.update(LSystem.MSEC * 20);
							break;
						case Slow:
							desktop.update(LSystem.MSEC * 40);
							break;
						case FewSlow:
							desktop.update(LSystem.MSEC * 60);
							break;
						case Fast:
							desktop.update(LSystem.SECOND);
							break;
						case Quickly:
							for (int i = 0; i < 2; i++) {
								desktop.update(LSystem.SECOND);
							}
							break;
						case Flash:
							for (int i = 0; i < 3; i++) {
								desktop.update(LSystem.SECOND);
							}
							break;
						}
					}
					if (sprites != null) {
						sprites.update(time.timeSinceLastUpdate);
					}
					if (autoPlay) {
						playAutoNext();
					}
				}
			}
		};
		setSpeedMode(speedMode);
		avgProcess.setDelay(delay);
		RealtimeProcessManager.get().addProcess(avgProcess);
	}

	private synchronized void initDesktop() {
		if (desktop != null && sprites != null) {
			return;
		}
		this.desktop = new Desktop(this, getWidth(), getHeight());
		this.sprites = new Sprites(this, getWidth(), getHeight());
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
		this.message = new LMessage(dialog, 0, 0);
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
		this.select = new LSelect(dialog, message.x(), message.y());
		this.select.setTopOffset(5);
		this.scrCG = new AVGCG();
		this.desktop.add(message);
		this.desktop.add(select);
		this.select.setVisible(false);
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
		if (desktop == null) {
			initDesktop();
		}
		desktop.add(c);
		return this;
	}

	@Override
	public Screen add(ISprite s) {
		if (sprites == null) {
			initDesktop();
		}
		sprites.add(s);
		return this;
	}

	@Override
	public Screen remove(ISprite sprite) {
		sprites.remove(sprite);
		return this;
	}

	@Override
	public Screen remove(LComponent comp) {
		desktop.remove(comp);
		return this;
	}

	@Override
	public Screen removeAll() {
		sprites.removeAll();
		desktop.getContentPane().clear();
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
			if (desktop != null) {
				desktop.createUI(g);
			}
			if (sprites != null) {
				sprites.createUI(g);
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
				if (cmdFlag.equalsIgnoreCase(CommandType.L_SPEED)) {
					if (mesFlag != null) {
						setSpeedMode(toSpeedMode(mesFlag));
					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_OPTION)) {
					if (mesFlag != null) {
						if ("clear".equalsIgnoreCase(mesFlag)) {
							if (orderFlag == null) {
								desktop.removeUIName("ClickButton");
							} else {
								LComponent[] comps = desktop.getContentPane()
										.getComponents();
								if (comps != null) {
									desktop.removeTag("opt" + orderFlag);
								}
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
								click.Tag = "opt"
										+ (click.getText() == null ? command
												.getIndex() : click.getText());
								desktop.add(click);
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
					if (sprites != null) {
						boolean flag = false;
						ISprite[] ss = sprites.getSprites();

						for (int i = 0; i < ss.length; i++) {
							ISprite s = ss[i];
							if (s instanceof NaturalEffect) {
								flag = true;
								break;
							}
						}
						if (!flag) {
							if (cmdFlag.equalsIgnoreCase(CommandType.L_SNOW)) {
								sprites.add(NaturalEffect.getSnowEffect());
							} else if (cmdFlag
									.equalsIgnoreCase(CommandType.L_RAIN)) {
								sprites.add(NaturalEffect.getRainEffect());
							} else if (cmdFlag
									.equalsIgnoreCase(CommandType.L_PETAL)) {
								sprites.add(NaturalEffect.getPetalEffect());
							}
						}

					}
					continue;
				}
				if (cmdFlag.equalsIgnoreCase(CommandType.L_SNOWSTOP)
						|| cmdFlag.equalsIgnoreCase(CommandType.L_RAINSTOP)
						|| cmdFlag.equalsIgnoreCase(CommandType.L_PETALSTOP)) {
					if (sprites != null) {
						ISprite[] ss = sprites.getSprites();

						for (int i = 0; i < ss.length; i++) {
							ISprite s = ss[i];
							if (s instanceof NaturalEffect) {
								if (cmdFlag
										.equalsIgnoreCase(CommandType.L_SNOWSTOP)) {
									if (((NaturalEffect) s).getKernels()[0] instanceof SnowKernel) {
										sprites.remove(s);
									}
								} else if (cmdFlag
										.equalsIgnoreCase(CommandType.L_RAINSTOP)) {
									if (((NaturalEffect) s).getKernels()[0] instanceof RainKernel) {
										sprites.remove(s);
									}
								} else if (cmdFlag
										.equalsIgnoreCase(CommandType.L_PETALSTOP)) {
									if (((NaturalEffect) s).getKernels()[0] instanceof PetalKernel) {
										sprites.remove(s);
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
					scrFlag = true;
					LColor color = LColor.black;
					if (mesFlag.equalsIgnoreCase("red")) {
						color = LColor.red;
					} else if (mesFlag.equalsIgnoreCase("yellow")) {
						color = LColor.yellow;
					} else if (mesFlag.equalsIgnoreCase("white")) {
						color = LColor.white;
					} else if (mesFlag.equalsIgnoreCase("black")) {
						color = LColor.black;
					} else if (mesFlag.equalsIgnoreCase("cyan")) {
						color = LColor.cyan;
					} else if (mesFlag.equalsIgnoreCase("green")) {
						color = LColor.green;
					} else if (mesFlag.equalsIgnoreCase("orange")) {
						color = LColor.orange;
					} else if (mesFlag.equalsIgnoreCase("pink")) {
						color = LColor.pink;
					} else if (mesFlag.equalsIgnoreCase("magenta")) {
						color = LColor.magenta;
					} else {
						color = new LColor(mesFlag);
					}
					if (sprites != null) {
						sprites.removeAll();
						if (cmdFlag.equalsIgnoreCase(CommandType.L_FADEIN)) {
							sprites.add(FadeEffect.getInstance(
									ISprite.TYPE_FADE_IN, 30, color));
						} else {
							sprites.add(FadeEffect.getInstance(
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
						if (mesFlag.equalsIgnoreCase("red")) {
							message.setFontColor(LColor.red);
						} else if (mesFlag.equalsIgnoreCase("yellow")) {
							message.setFontColor(LColor.yellow);
						} else if (mesFlag.equalsIgnoreCase("white")) {
							message.setFontColor(LColor.white);
						} else if (mesFlag.equalsIgnoreCase("black")) {
							message.setFontColor(LColor.black);
						} else if (mesFlag.equalsIgnoreCase("cyan")) {
							message.setFontColor(LColor.cyan);
						} else if (mesFlag.equalsIgnoreCase("green")) {
							message.setFontColor(LColor.green);
						} else if (mesFlag.equalsIgnoreCase("orange")) {
							message.setFontColor(LColor.orange);
						} else if (mesFlag.equalsIgnoreCase("pink")) {
							message.setFontColor(LColor.pink);
						}
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

	public void click() {
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
			if ((LSystem.base() != null && LSystem.base().isMobile()) ? clickcount++ >= 1
					: clickcount > -1) {
				message.setVisible(false);
				select.setVisible(false);
				isSelectMessage = false;
				selectMessage = null;
				clickcount = 0;
			}
		}
		if (isNext && !isSelectMessage) {
			nextScript();
		}
	}

	protected boolean initNextScript = true;

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
		initCommandConfig(command);
		if (initNextScript) {
			nextScript();
		}
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
		this.initCommandConfig(scriptName);
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
		if (speedMode == SpeedMode.Flash || speedMode == SpeedMode.Quickly
				|| speedMode == SpeedMode.Fast) {
			delay = 0;
		}
		if (avgProcess != null) {
			avgProcess.setDelay(delay);
		}
	}

	@Override
	public Desktop getDesktop() {
		return desktop;
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
		return scriptName;
	}

	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
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

	@Override
	public Sprites getSprites() {
		return sprites;
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

	}

	@Override
	public void onKeyDown(GameKey e) {

	}

	@Override
	public void onKeyUp(GameKey e) {

	}

	@Override
	public void touchDown(GameTouch touch) {
		if (desktop != null) {
			desktop.processEvents();
		}
		click();
	}

	@Override
	public void touchMove(GameTouch e) {
		if (desktop != null) {
			desktop.processEvents();
		}
	}

	@Override
	public void touchUp(GameTouch e) {
		if (desktop != null) {
			desktop.processEvents();
		}
	}

	@Override
	public void touchDrag(GameTouch e) {
		if (desktop != null) {
			desktop.processEvents();
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

	public void setLimitClick(boolean limitClick) {
		this.limitClick = limitClick;
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
		return speedMode;
	}

	/**
	 * 设置当前AVG的文字显示速度默认
	 * 
	 * @param m
	 */
	public void setSpeedMode(SpeedMode m) {
		this.speedMode = m;
		if (speedMode == SpeedMode.Flash || speedMode == SpeedMode.Quickly
				|| speedMode == SpeedMode.Fast) {
			delay = 0;
		}
	}

	@Override
	public void close() {
		running = false;
		if (avgProcess != null) {
			avgProcess.kill();
			avgProcess = null;
		}
		if (desktop != null) {
			desktop.close();
			desktop = null;
		}
		if (sprites != null) {
			sprites.close();
			sprites = null;
		}
		if (command != null) {
			command = null;
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
	}

}
