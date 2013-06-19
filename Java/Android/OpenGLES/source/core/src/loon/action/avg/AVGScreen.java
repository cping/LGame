package loon.action.avg;

import java.io.InputStream;
import java.util.List;

import loon.action.avg.drama.Command;
import loon.action.avg.drama.CommandType;
import loon.action.avg.drama.Conversion;
import loon.action.sprite.ISprite;
import loon.action.sprite.Sprites;
import loon.action.sprite.effect.FadeEffect;
import loon.action.sprite.effect.FreedomEffect;
import loon.action.sprite.effect.PetalKernel;
import loon.action.sprite.effect.RainKernel;
import loon.action.sprite.effect.SnowKernel;
import loon.core.LSystem;
import loon.core.graphics.Desktop;
import loon.core.graphics.LColor;
import loon.core.graphics.LComponent;
import loon.core.graphics.LImage;
import loon.core.graphics.Screen;
import loon.core.graphics.component.LButton;
import loon.core.graphics.component.LMessage;
import loon.core.graphics.component.LPaper;
import loon.core.graphics.component.LSelect;
import loon.core.graphics.device.LGraphics;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.GLLoader;
import loon.core.graphics.opengl.LTexture;
import loon.core.input.LKey;
import loon.core.input.LTouch;
import loon.core.timer.LTimer;
import loon.core.timer.LTimerContext;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

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
public abstract class AVGScreen extends Screen implements Runnable {

	private Object synch = new Object();

	private boolean isSelectMessage, scrFlag, isRunning, running;

	private int delay;

	private String scriptName;

	private String selectMessage;

	private String dialogFileName;

	private boolean locked;

	private LColor color;

	protected Command command;

	protected LTexture dialog;

	protected AVGCG scrCG;

	protected LSelect select;

	protected LMessage message;

	protected Desktop desktop;

	protected Sprites sprites;

	private Thread avgThread;

	private boolean autoPlay;

	public AVGScreen(final String initscript, final String initdialog) {
		this(initscript, new LTexture(initdialog));
	}

	public AVGScreen(final String initscript, final LTexture img) {
		if (initscript == null) {
			return;
		}
		this.scriptName = initscript;
		if (img != null) {
			this.dialogFileName = img.getFileName();
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
		LTexture.AUTO_LINEAR();
		this.setRepaintMode(Screen.SCREEN_NOT_REPAINT);
		this.delay = 30;
		if (dialog == null && dialogFileName != null) {
			this.dialog = new LTexture(dialogFileName);
		}
		this.running = true;
	}

	@Override
	public final void onLoad() {

	}

	@Override
	public final void onLoaded() {
		this.avgThread = new Thread(this, "AVGThread");
		this.avgThread.setPriority(Thread.NORM_PRIORITY);
		this.avgThread.start();
	}

	private synchronized void initDesktop() {
		if (desktop != null && sprites != null) {
			return;
		}
		this.desktop = new Desktop(this, getWidth(), getHeight());
		this.sprites = new Sprites(getWidth(), getHeight());
		if (dialog == null) {
			LImage tmp = LImage.createImage(getWidth() - 20,
					getHeight() / 2 - 20, true);
			LGraphics g = tmp.getLGraphics();
			g.setColor(0, 0, 0, 125);
			g.fillRect(0, 0, tmp.getWidth(), tmp.getHeight());
			g.dispose();
			g = null;
			dialog = new LTexture(GLLoader.getTextureData(tmp));
			if (tmp != null) {
				tmp.dispose();
				tmp = null;
			}
		}
		this.message = new LMessage(dialog, 0, 0);
		this.message.setFontColor(LColor.white);
		int size = message.getWidth() / (message.getMessageFont().getSize());
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
		this.select = new LSelect(dialog, 0, 0);
		this.select.setLocation(message.x(), message.y());
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
	public void add(LComponent c) {
		if (desktop == null) {
			initDesktop();
		}
		desktop.add(c);

	}

	@Override
	public void add(ISprite s) {
		if (sprites == null) {
			initDesktop();
		}
		sprites.add(s);
	}

	@Override
	public void remove(ISprite sprite) {
		sprites.remove(sprite);

	}

	@Override
	public void remove(LComponent comp) {
		desktop.remove(comp);
	}

	@Override
	public void removeAll() {
		sprites.removeAll();
		desktop.getContentPane().clear();
	}

	@Override
	final public synchronized void draw(GLEx g) {
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
						g.drawTexture(scrCG.getBackgroundCG(), 0, 0);
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
		synchronized (synch) {
			if (command != null && !isClose() && running) {
				for (; isRunning = command.next();) {
					String result = command.doExecute();
					if (result == null) {
						continue;
					}
					if (!nextScript(result)) {
						break;
					}
					List<?> commands = Conversion.splitToList(result, " ");
					int size = commands.size();
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
								if (s instanceof FreedomEffect) {
									flag = true;
									break;
								}
							}
							if (!flag) {
								if (cmdFlag
										.equalsIgnoreCase(CommandType.L_SNOW)) {
									sprites.add(FreedomEffect.getSnowEffect());
								} else if (cmdFlag
										.equalsIgnoreCase(CommandType.L_RAIN)) {
									sprites.add(FreedomEffect.getRainEffect());
								} else if (cmdFlag
										.equalsIgnoreCase(CommandType.L_PETAL)) {
									sprites.add(FreedomEffect.getPetalEffect());
								}
							}

						}
						continue;
					}
					if (cmdFlag.equalsIgnoreCase(CommandType.L_SNOWSTOP)
							|| cmdFlag.equalsIgnoreCase(CommandType.L_RAINSTOP)
							|| cmdFlag
									.equalsIgnoreCase(CommandType.L_PETALSTOP)) {
						if (sprites != null) {
							ISprite[] ss = sprites.getSprites();

							for (int i = 0; i < ss.length; i++) {
								ISprite s = ss[i];
								if (s instanceof FreedomEffect) {
									if (cmdFlag
											.equalsIgnoreCase(CommandType.L_SNOWSTOP)) {
										if (((FreedomEffect) s).getKernels()[0] instanceof SnowKernel) {
											sprites.remove(s);
										}
									} else if (cmdFlag
											.equalsIgnoreCase(CommandType.L_RAINSTOP)) {
										if (((FreedomEffect) s).getKernels()[0] instanceof RainKernel) {
											sprites.remove(s);
										}
									} else if (cmdFlag
											.equalsIgnoreCase(CommandType.L_PETALSTOP)) {
										if (((FreedomEffect) s).getKernels()[0] instanceof PetalKernel) {
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
						}
						if (sprites != null) {
							sprites.removeAll();
							if (cmdFlag.equalsIgnoreCase(CommandType.L_FADEIN)) {
								sprites.add(FadeEffect.getInstance(
										ISprite.TYPE_FADE_IN, color));
							} else {
								sprites.add(FadeEffect.getInstance(
										ISprite.TYPE_FADE_OUT, color));
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
								message.setMessageLength(Integer
										.parseInt(mesFlag));
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
						message.setMessage(StringUtils.replace(nMessage, "&",
								" "));
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
						selectMessage = mesFlag;
						continue;
					}
					if (cmdFlag.equalsIgnoreCase(CommandType.L_SELECTS)) {
						if (message.isVisible()) {
							message.setVisible(false);
						}
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
						if (color == null && colors != null
								&& colors.length == 3) {
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
							scrCG.dispose();
						}
						if (mesFlag.equalsIgnoreCase(CommandType.L_DEL)) {
							if (orderFlag != null) {
								scrCG.remove(orderFlag);
							} else {
								scrCG.dispose();
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
	}

	public abstract void onExit();

	private int count = 0;

	private LTimer autoTimer = new LTimer(LSystem.SECOND);

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
		if (!running) {
			return;
		}
		if (locked) {
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
			if (message.isVisible()) {
				isNext = message.intersects(getTouchX(), getTouchY());
			} else {
				isNext = true;
			}
		} else if (scrFlag && select.getResultIndex() != -1) {
			onSelect(selectMessage, select.getResultIndex());
			isNext = select.intersects(getTouchX(), getTouchY());
			if (isNext) {
				if (count++ >= 1) {
					message.setVisible(false);
					select.setVisible(false);
					isSelectMessage = false;
					selectMessage = null;
					count = 0;
					return;
				}
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

	public void initCommandConfig(InputStream in) {
		if (in == null) {
			return;
		}
		Command.resetCache();
		if (command == null) {
			command = new Command(in);
		} else {
			command.formatCommand(in);
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

	@Override
	public void run() {
		initAVG();
		onLoading();
		for (; running;) {
			if (desktop != null) {
				desktop.update(delay);
			}
			if (sprites != null) {
				sprites.update(delay);
			}
			sleep(delay);
			if (autoPlay) {
				playAutoNext();
			}
		}

	}

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
		this.delay = pause;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
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
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	final private LTimer autoUpdate = new LTimer(3 * LSystem.MINUTE);

	@Override
	public void alter(LTimerContext timer) {
		synchronized (AVGScreen.class) {
			if (autoUpdate.action(timer)) {
				System.gc();
			}
		}
	}

	@Override
	public void onKeyDown(LKey e) {

	}

	@Override
	public void onKeyUp(LKey e) {

	}

	@Override
	public void touchDown(LTouch touch) {
		if (desktop != null) {
			LComponent[] cs = desktop.getContentPane().getComponents();
			for (int i = 0; i < cs.length; i++) {
				if (cs[i] instanceof LButton) {
					LButton btn = ((LButton) cs[i]);
					if (btn != null && btn.isVisible()) {
						if (btn.intersects(touch.x(), touch.y())) {
							btn.doClick();
						}
					}
				} else if (cs[i] instanceof LPaper) {
					LPaper paper = ((LPaper) cs[i]);
					if (paper != null && paper.isVisible()) {
						if (paper.intersects(touch.x(), touch.y())) {
							paper.doClick();
						}
					}
				}
			}
		}
		click();
	}

	@Override
	public void touchMove(LTouch e) {

	}

	@Override
	public void touchUp(LTouch touch) {

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

	@Override
	public void dispose() {
		running = false;
		try {
			if (avgThread != null) {
				avgThread.interrupt();
				avgThread = null;
			}
		} catch (Exception e) {
		}
		if (desktop != null) {
			desktop.dispose();
			desktop = null;
		}
		if (sprites != null) {
			sprites.dispose();
			sprites = null;
		}
		if (command != null) {
			command = null;
		}
		if (scrCG != null) {
			scrCG.dispose();
			scrCG = null;
		}
		if (dialog != null) {
			if (dialog.getFileName() != null) {
				dialog.destroy();
				dialog = null;
			}
		}
		super.dispose();
	}

}
