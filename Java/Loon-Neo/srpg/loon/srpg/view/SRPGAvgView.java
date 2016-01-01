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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
package loon.srpg.view;

import java.util.List;

import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.action.avg.AVGCG;
import loon.action.avg.drama.Command;
import loon.action.avg.drama.CommandType;
import loon.action.avg.drama.Conversion;
import loon.canvas.LColor;
import loon.component.LMessage;
import loon.component.LSelect;
import loon.event.SysTouch;
import loon.event.Updateable;
import loon.opengl.GLEx;
import loon.srpg.SRPGScreen;
import loon.srpg.SRPGType;
import loon.srpg.actor.SRPGActor;
import loon.srpg.effect.SRPGEffectFactory;
import loon.utils.ArrayMap;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class SRPGAvgView extends SRPGView implements LRelease {

	private LSelect select;

	private LMessage message;

	private boolean isRunning, isSelectMessage, scrFlag;

	private boolean isLock, isNext, isClose;

	private int width, height, sleep, sleepMax;

	private AVGCG scrCG;

	private String scriptName;

	private String selectMessage;

	private Command command;

	private LTexture mesImage;

	private LColor color;

	private SRPGScreen screen;

	public void setCommand(String fileName, ArrayMap map) {
		if (fileName == null) {
			return;
		}
		this.scriptName = fileName;
		Command.resetCache();
		if (command == null) {
			command = new Command(fileName);
		} else {
			command.formatCommand(fileName);
		}
		if (map != null) {
			for (int i = 0; i < map.size(); i++) {
				command.setVariable((String) map.getKey(i), map.get(i));
			}
		}
		this.reset();
		this.setExist(true);
		this.nextScript();
	}

	public SRPGAvgView(final SRPGScreen screen, final LTexture img,
			final int w, final int h) {
		this.screen = screen;
		this.width = w;
		this.height = h;
		this.mesImage = img;
		this.scrCG = new AVGCG(screen);
		this.setMessageImage(img, false);
		this.screen.add(message);
		this.reset();
	}

	public void setMessageImage(LTexture img, boolean flag) {
		if (img != null) {
			if (message == null) {
				this.message = new LMessage(img, 0, 0);
			} else {
				this.message.setBackground(img);
			}
			this.message.setFontColor(LColor.white);
			int size = message.width() / (message.getMessageFont().getSize());
			if (size % 2 != 0) {
				size = size - 3;
			} else {
				size = size - 4;
			}
			this.message.setMessageLength(size);
			this.message.setLocation((width - message.getWidth()) / 2, height
					- message.getHeight() - 10);
			this.message.setDelay(180);
			this.message.setTopOffset(-5);
			this.message.setVisible(flag);
		}
	}

	public void setLock(boolean flag) {
		this.isLock = flag;
	}

	public boolean isLock() {
		if (isExist()) {
			return isLock;
		} else {
			return false;
		}
	}

	public void setDelay(long d) {
		this.message.setDelay(d);
	}

	public void reset() {
		if (select != null) {
			this.select.setVisible(false);
		}
		this.message.setVisible(false);
		this.message.setDelay(180);
		this.isRunning = false;
		this.isLock = false;
		this.isNext = false;
		this.exist = false;
	}

	public void onSelect(String message, int type) {
		if (screen != null) {
			screen.onAvgViewSelect(message, type);
		}
	}

	public void onNext(String command) {
		if (screen != null) {
			screen.onAvgViewNext(command);
		}
	}

	@Override
	public boolean isExist() {
		return super.exist && isRunning && !isClose;
	}

	private int count = 0;

	public void onMouse(int x, int y) {
		if (!exist && isRunning) {
			return;
		}
		isNext = message.isComplete();
		if (message.isVisible() && !isNext) {
			return;
		}
		if (!isSelectMessage && sleep <= 0) {
			if (!scrFlag) {
				scrFlag = true;
			}
			if (message.isVisible()) {
				isNext = SysTouch.isDown();
			} else {
				isNext = true;
			}
		} else if (scrFlag && select != null && select.getResultIndex() != -1) {
			onSelect(selectMessage, select.getResultIndex());
			isNext = select.intersects(screen.getTouchX(), screen.getTouchY());
			if (isNext) {
				if (count >= 1) {
					message.setVisible(false);
					select.setVisible(false);
					isSelectMessage = false;
					selectMessage = null;
					count = 0;
					return;
				}
				count++;
			}
		}
	}

	public void draw(GLEx g) {
		if (!exist) {
			return;
		}
		if (sleep == 0) {
			if (scrCG == null) {
				return;
			}
			scrCG.paint(g);
		} else {
			sleep--;
			if (color != null) {
				double alpha = (double) (sleepMax - sleep) / sleepMax;
				if (alpha < 1.0) {
					LColor c = g.getColor();
					g.setColor(color.getRed(), color.getGreen(),
							color.getBlue(), (int) (255 * alpha));
					g.fillRect(0, 0, width, height);
					g.setColor(c);
				}
			}
			if (sleep <= 0) {
				sleep = 0;
				color = null;
			}
		}
	}

	public synchronized void nextScript() {
		try {
			if (isClose) {
				return;
			}
			if (command != null) {
				String result;
				for (; isRunning = command.next();) {
					isNext = false;
					result = command.doExecute();
					if (result == null) {
						continue;
					}
					onNext(result);
					TArray<String> commands = Conversion.splitToList(result,
							' ');
					int size = commands.size;
					String cmdFlag = (String) commands.get(0);
					String mesFlag = null, orderFlag = null, lastFlag = null, otherFlag = null;
					if (size == 2) {
						mesFlag = (String) commands.get(1);
					} else if (size == 3) {
						mesFlag = (String) commands.get(1);
						orderFlag = (String) commands.get(2);
					} else if (size == 4) {
						mesFlag = (String) commands.get(1);
						orderFlag = (String) commands.get(2);
						lastFlag = (String) commands.get(3);
					} else if (size == 5) {
						mesFlag = (String) commands.get(1);
						orderFlag = (String) commands.get(2);
						lastFlag = (String) commands.get(3);
						otherFlag = (String) commands.get(4);
					}
					if (cmdFlag.equalsIgnoreCase(CommandType.L_WAIT)) {
						scrFlag = true;
						break;
					}
					if (cmdFlag.equalsIgnoreCase(CommandType.L_SELLEN)) {
						if (mesFlag != null && select != null) {
							if (MathUtils.isNan(mesFlag)) {
								select.setLeftOffset(Integer.parseInt(mesFlag));
							}
						}
						continue;
					}
					if (cmdFlag.equalsIgnoreCase(CommandType.L_SELTOP)) {
						if (mesFlag != null && select != null) {
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
						if (select != null && select.isVisible()) {
							select.setVisible(false);
						}
						scrFlag = true;
						final String nMessage = mesFlag;
						message.setMessage(StringUtils.replace(nMessage, "&",
								" "));
						message.setVisible(true);
						break;
					}
					if (cmdFlag.equalsIgnoreCase(CommandType.L_MESSTOP)) {
						scrFlag = true;
						message.setVisible(false);
						if (select != null) {
							select.setVisible(false);
						}
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
						if (select == null) {
							this.select = new LSelect(mesImage, 0, 0);
							this.select.setLocation(message.x(), message.y());
							this.screen.add(select);
						}
						scrFlag = true;
						isSelectMessage = true;
						String[] selects = command.getReads();
						select.setMessage(selectMessage, selects);
						select.setVisible(true);
						break;
					}
					if (cmdFlag.equalsIgnoreCase(CommandType.L_CGWAIT)) {
						scrFlag = false;
						break;
					}
					if (cmdFlag.equalsIgnoreCase(CommandType.L_SLEEP)) {
						sleep = Integer.valueOf(mesFlag).intValue();
						sleepMax = Integer.valueOf(mesFlag).intValue();
						scrFlag = false;
						break;
					}
					if (cmdFlag.equalsIgnoreCase(CommandType.L_FLASH)) {
						scrFlag = true;
						String[] colors = StringUtils.split(mesFlag, ',');
						if (color == null && colors != null
								&& colors.length == 3) {
							color = new LColor(Integer.valueOf(colors[0])
									.intValue(), Integer.valueOf(colors[1])
									.intValue(), Integer.valueOf(colors[2])
									.intValue());
							sleep = 20;
							sleepMax = sleep;
							scrFlag = false;
						} else {
							color = null;
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
					if (screen != null) {
						if ("center".equalsIgnoreCase(cmdFlag)) {
							if (MathUtils.isNan(mesFlag)) {
								int id = Integer.parseInt(mesFlag);
								screen.setCenterActor(id);
							}
							continue;
						}
						if ("move".equalsIgnoreCase(cmdFlag)) {
							if (MathUtils.isNan(mesFlag)) {
								if (size == 3) {
									move(mesFlag, orderFlag, "60", "0", false);
								} else {
									move(mesFlag, orderFlag, lastFlag,
											otherFlag, false);
								}
							}
							continue;
						}
						if ("movestop".equalsIgnoreCase(cmdFlag)) {
							if (MathUtils.isNan(mesFlag)) {
								if (size == 3) {
									move(mesFlag, orderFlag, "60", "0", true);
								} else {
									move(mesFlag, orderFlag, lastFlag,
											otherFlag, true);
								}
							}
							continue;
						}
						if ("movecamera".equalsIgnoreCase(cmdFlag)) {
							if (mesFlag != null) {
								TArray<String> moves = Conversion.splitToList(
										mesFlag, ',');
								if (moves.size == 3) {
									setExist(false);
									String x = (String) moves.get(0);
									String y = (String) moves.get(1);
									String sleep = (String) moves.get(2);
									try {
										int xv = Integer.valueOf(x).intValue();
										int yv = Integer.valueOf(y).intValue();
										int i = Integer.valueOf(sleep)
												.intValue();
										moveCamera(xv, yv, i);
									} catch (Exception e) {
									}
									setExist(true);
									continue;
								}
							}
						} else if ("moveactor".equalsIgnoreCase(cmdFlag)) {
							if (mesFlag != null) {
								TArray<String> moves = Conversion.splitToList(
										mesFlag, ',');
								if (moves.size == 2) {
									setExist(false);
									String id = (String) moves.get(0);
									String i = (String) moves.get(1);
									try {
										int idv = Integer.valueOf(id)
												.intValue();
										int iv = Integer.valueOf(i).intValue();
										moveActor(idv, iv);
									} catch (Exception e) {
									}
									setExist(true);
									continue;
								}
							}
						}
						if ("rotate".equalsIgnoreCase(cmdFlag)) {
							if (MathUtils.isNan(mesFlag)) {
								int id = Integer.parseInt(mesFlag);
								setExist(false);
								SRPGActor actor = screen.findActor(id);
								int d = actor.getDirection();
								int[] moving = null;
								switch (d) {
								case SRPGType.MOVE_RIGHT:
									moving = new int[] { SRPGType.MOVE_RIGHT,
											SRPGType.MOVE_UP,
											SRPGType.MOVE_LEFT,
											SRPGType.MOVE_DOWN,
											SRPGType.MOVE_RIGHT };
									break;
								case SRPGType.MOVE_LEFT:
									moving = new int[] { SRPGType.MOVE_LEFT,
											SRPGType.MOVE_UP,
											SRPGType.MOVE_RIGHT,
											SRPGType.MOVE_DOWN,
											SRPGType.MOVE_LEFT };
									break;
								case SRPGType.MOVE_UP:
									moving = new int[] { SRPGType.MOVE_UP,
											SRPGType.MOVE_LEFT,
											SRPGType.MOVE_DOWN,
											SRPGType.MOVE_RIGHT,
											SRPGType.MOVE_UP };
									break;
								default:
								case SRPGType.MOVE_DOWN:
									moving = new int[] { SRPGType.MOVE_DOWN,
											SRPGType.MOVE_LEFT,
											SRPGType.MOVE_UP,
											SRPGType.MOVE_RIGHT,
											SRPGType.MOVE_DOWN };
									break;
								}
								int time = 0;
								try {
									time = Integer.valueOf(orderFlag)
											.intValue();
								} catch (Exception e) {
								}
								for (int i = 0; i < moving.length; i++) {
									actor.setDirection(moving[i]);
									if (time == 0) {
										screen.waitFrame(30);
									} else {
										screen.waitFrame(time);
									}
								}
								actor.setDirection(d);
								setExist(true);
							}
							continue;
						}
						if ("dir".equalsIgnoreCase(cmdFlag)) {
							if (MathUtils.isNan(mesFlag)) {
								int id = Integer.parseInt(mesFlag);
								SRPGActor actor = screen.findActor(id);
								int d = SRPGType.MOVE_DOWN;
								if (orderFlag.equalsIgnoreCase("up")) {
									d = SRPGType.MOVE_UP;
								} else if (orderFlag.equalsIgnoreCase("down")) {
									d = SRPGType.MOVE_DOWN;
								} else if (orderFlag.equalsIgnoreCase("right")) {
									d = SRPGType.MOVE_RIGHT;
								} else if (orderFlag.equalsIgnoreCase("left")) {
									d = SRPGType.MOVE_LEFT;
								}
								actor.setDirection(d);
								setExist(true);
							}
							continue;
						}
						if ("chop".equalsIgnoreCase(cmdFlag)) {
							if (MathUtils.isNan(mesFlag)) {
								int id = Integer.parseInt(mesFlag);
								setExist(false);
								SRPGActor actor = screen.findActor(id);
								screen.setEffect(SRPGEffectFactory
										.getAbilityEffect(
												SRPGEffectFactory.EFFECT_CHOP,
												actor));
								screen.waitTime(300);
								screen.setEffect(SRPGEffectFactory
										.getAbilityEffect(
												SRPGEffectFactory.EFFECT_BURST,
												actor));
								setExist(true);
							}
							continue;
						}
						if ("help".equalsIgnoreCase(cmdFlag)) {
							if (mesFlag != null) {
								setExist(false);
								TArray<String> mes = Conversion.splitToList(
										mesFlag, '|');
								if (orderFlag != null) {
									TArray<String> location = Conversion
											.splitToList(orderFlag, ',');
									if (location.size == 2) {
										String x = (String) location.get(0);
										String y = (String) location.get(1);
										try {
											int nx = Integer.valueOf(x)
													.intValue();
											int ny = Integer.valueOf(y)
													.intValue();
											screen.setHelper(
													(String[]) mes.toArray(),
													nx, ny, true);
										} catch (Exception e) {
										}
									}
								} else {
									screen.setHelper((String[]) mes.toArray(),
											true);
								}
								setExist(true);
								continue;
							}
						}
						if ("hide".equalsIgnoreCase(cmdFlag)) {
							if (MathUtils.isNan(mesFlag)) {
								int id = Integer.parseInt(mesFlag);
								SRPGActor actor = screen.findActor(id);
								actor.setVisible(false);
							}
							continue;
						}
						if ("show".equalsIgnoreCase(cmdFlag)) {
							if (MathUtils.isNan(mesFlag)) {
								int id = Integer.parseInt(mesFlag);
								SRPGActor actor = screen.findActor(id);
								actor.setVisible(true);
								actor.setExist(true);
							}
							continue;
						}
						if ("wind".equalsIgnoreCase(cmdFlag)) {
							setExist(false);
							screen.setEffect(SRPGEffectFactory
									.getAbilityEffect(
											SRPGEffectFactory.EFFECT_WIND, 0, 0));
							screen.waitTime(300);
							setExist(true);
							continue;
						}
						if ("gas".equalsIgnoreCase(cmdFlag)) {
							if (MathUtils.isNan(mesFlag)) {
								int id = Integer.parseInt(mesFlag);
								setExist(false);
								SRPGActor actor = screen.findActor(id);
								screen.setEffect(SRPGEffectFactory
										.getAbilityEffect(
												SRPGEffectFactory.EFFECT_C,
												actor, LColor.black));
								screen.waitTime(100);
								screen.setEffect(SRPGEffectFactory
										.getAbilityEffect(
												SRPGEffectFactory.EFFECT_RC,
												actor, LColor.black));
								screen.waitTime(100);
								screen.setEffect(SRPGEffectFactory
										.getAbilityEffect(
												SRPGEffectFactory.EFFECT_BLAST,
												actor, LColor.black));
								setExist(true);
							}
							continue;
						} else if ("blow".equalsIgnoreCase(cmdFlag)) {
							if (MathUtils.isNan(mesFlag)) {
								int id = Integer.parseInt(mesFlag);
								setExist(false);
								SRPGActor actor = screen.findActor(id);
								screen.setEffect(SRPGEffectFactory
										.getAbilityEffect(
												SRPGEffectFactory.EFFECT_FADE,
												actor));
								screen.waitTime(100);
								screen.setEffect(SRPGEffectFactory
										.getAbilityEffect(
												SRPGEffectFactory.EFFECT_BLAST,
												actor));
								screen.waitTime(100);
								screen.setEffect(SRPGEffectFactory
										.getAbilityEffect(
												SRPGEffectFactory.EFFECT_BURST,
												actor));
								setExist(true);
							}
							continue;
						} else if ("thunder".equalsIgnoreCase(cmdFlag)) {
							if (MathUtils.isNan(mesFlag)) {
								int id = Integer.parseInt(mesFlag);
								setExist(false);
								SRPGActor actor = screen.findActor(id);
								screen.setEffect(SRPGEffectFactory
										.getAbilityEffect(
												SRPGEffectFactory.EFFECT_T,
												actor));
								screen.waitTime(100);
								setExist(true);
							}
							continue;
						}
					}
					if (cmdFlag.equalsIgnoreCase(CommandType.L_EXIT)) {
						scrFlag = true;
						break;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage());
		}
	}

	private synchronized void move(String mesFlag, String orderFlag,
			String lastFlag, String otherFlag, boolean flag) {
		setExist(false);
		int id = Integer.parseInt(mesFlag);
		SRPGActor actor = screen.findActor(id);
		int d = SRPGType.MOVE_DOWN;
		if (orderFlag.equalsIgnoreCase("up")) {
			d = SRPGType.MOVE_UP;
		} else if (orderFlag.equalsIgnoreCase("down")) {
			d = SRPGType.MOVE_DOWN;
		} else if (orderFlag.equalsIgnoreCase("right")) {
			d = SRPGType.MOVE_RIGHT;
		} else if (orderFlag.equalsIgnoreCase("left")) {
			d = SRPGType.MOVE_LEFT;
		}
		int stepValue = 0;
		try {
			stepValue = Integer.valueOf(otherFlag).intValue();
		} catch (Exception e) {
		}
		int old = actor.getDirection();
		if (stepValue > 0) {
			try {
				int s = Integer.valueOf(lastFlag).intValue();
				for (int i = 0; i < stepValue; i++) {
					actor.moveActorShow(d, s);
					if (flag) {
						actor.setDirection(old);
					}
					actor.waitMove(screen);
				}
			} catch (Exception e) {
			}
		} else {
			try {
				int s = Integer.valueOf(lastFlag).intValue();
				actor.moveActorShow(d, s);
				if (flag) {
					actor.setDirection(old);
				}

				actor.waitMove(screen);
			} catch (Exception e) {
			}
		}
		scrFlag = true;
		setExist(true);

	}

	private synchronized void moveCamera(int x, int y, int sleep) {
		if (screen != null) {
			screen.moveCamera(screen.touchXTilesToPixels(x),
					screen.touchYTilesToPixels(y), sleep);
		}
	}

	private synchronized void moveActor(int id, int sleep) {
		if (screen != null) {
			screen.moveCamera(id, sleep);
		}
	}

	@Override
	public void setExist(boolean flag) {
		if (message != null) {
			message.setVisible(false);
		}
		if (select != null) {
			select.setVisible(false);
		}
		super.setExist(flag);
	}

	public synchronized void printWait() {
		this.isLock = true;
		for (; isExist();) {
			if (screen.getDesktop() != null) {
				screen.getDesktop().update(0);
			}
			isNext = message.isComplete();
			if (message.isVisible() && !isNext) {
				continue;
			}
			if (!isSelectMessage) {
				if (isNext) {
					nextScript();
				} else if (sleep <= 0) {
					nextScript();
				}
			}
			try {
				Thread.sleep(LSystem.SECOND);
			} catch (InterruptedException e) {
			}
		}
		setExist(false);
	}

	public synchronized void printWaitBattle() {
		this.isLock = true;
		for (; isExist();) {
			if (screen.getDesktop() != null) {
				screen.getDesktop().update(0);
			}
			isNext = message.isComplete();
			if (message.isVisible() && !isNext) {
				try {
					screen.wait(LSystem.SECOND);
				} catch (Exception ex) {
				}
				continue;
			}
			if (!isSelectMessage) {
				if (isNext) {
					nextScript();
				} else if (sleep <= 0) {
					nextScript();
				}
			}
			try {
				screen.wait(LSystem.SECOND);
			} catch (Exception ex) {
			}
		}
		setExist(false);
	}

	public boolean isRunning() {
		return isRunning;
	}

	public Command getCommand() {
		return command;
	}

	public int getHeight() {
		return height;
	}

	public LMessage getMessage() {
		return message;
	}

	public LSelect getSelect() {
		return select;
	}

	public int getWidth() {
		return width;
	}

	public String getScriptName() {
		return scriptName;
	}

	@Override
	public void close() {
		isRunning = false;
		isClose = true;
		if (scrCG != null) {
			scrCG.close();
			scrCG = null;
		}
		if (select != null) {
			select.close();
		}
		if (message != null) {
			message.close();
		}
	}

}
