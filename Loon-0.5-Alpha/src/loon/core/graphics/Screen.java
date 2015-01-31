package loon.core.graphics;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import loon.LInput;
import loon.LKey;
import loon.LProcess;
import loon.LSystem;
import loon.LTouch;
import loon.LTransition;
import loon.JavaSEInputFactory.Touch;
import loon.action.ActionBind;
import loon.action.ActionControl;
import loon.action.ActionEvent;
import loon.action.collision.GravityHandler;
import loon.action.sprite.ISprite;
import loon.action.sprite.Sprites;
import loon.action.sprite.Sprites.SpriteListener;
import loon.core.EmptyObject;
import loon.core.EmulatorButtons;
import loon.core.EmulatorListener;
import loon.core.LObject;
import loon.core.LRelease;
import loon.core.event.Drawable;
import loon.core.event.ScreenListener;
import loon.core.event.Updateable;
import loon.core.geom.RectBox;
import loon.core.geom.Point.Point2i;
import loon.core.graphics.component.LLayer;
import loon.core.graphics.device.LColor;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTexture.Format;
import loon.core.processes.RealtimeProcess;
import loon.core.processes.RealtimeProcessManager;
import loon.core.timer.LTimer;
import loon.core.timer.LTimerContext;
import loon.media.SoundBox;

/**
 * 
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
public abstract class Screen extends SoundBox implements LInput, LRelease {

	private ArrayList<ScreenListener> screens;

	private boolean useScreenListener;

	public void addScreenListener(ScreenListener l) {
		if (l != null) {
			if (screens == null) {
				screens = new ArrayList<ScreenListener>(10);
			}
			screens.add(l);
		}
		useScreenListener = (screens != null && screens.size() > 0);
	}

	public void removeScreenListener(ScreenListener l) {
		if (screens == null) {
			return;
		}
		if (l != null) {
			screens.remove(l);
		}
		useScreenListener = (screens != null && screens.size() > 0);
	}

	private ArrayList<LRelease> releases;

	public void putRelease(LRelease r) {
		if (releases == null) {
			releases = new ArrayList<LRelease>(10);
		}
		releases.add(r);
	}

	public void putReleases(LRelease... rs) {
		if (releases == null) {
			releases = new ArrayList<LRelease>(10);
		}
		final int size = rs.length;
		for (int i = 0; i < size; i++) {
			releases.add(rs[i]);
		}
	}

	public void addAction(ActionEvent e, ActionBind act) {
		ActionControl.getInstance().addAction(e, act);
	}

	public void removeAction(ActionEvent e) {
		ActionControl.getInstance().removeAction(e);
	}

	public void removeAction(Object tag, ActionBind act) {
		ActionControl.getInstance().removeAction(tag, act);
	}

	public void removeAllActions(ActionBind act) {
		ActionControl.getInstance().removeAllActions(act);
	}

	public float getDeltaTime() {
		return elapsedTime / 1000f;
	}

	protected static Screen StaticCurrentSceen;

	public final static byte DRAW_USER = 0;

	public final static byte DRAW_SPRITE = 1;

	public final static byte DRAW_DESKTOP = 2;

	public final class PaintOrder {

		private byte type;

		private Screen screen;

		public PaintOrder(byte t, Screen s) {
			this.type = t;
			this.screen = s;
		}

		void paint(GLEx g) {
			switch (type) {
			case DRAW_USER:
				synchronized (this) {
					screen.draw(g);
				}
				break;
			case DRAW_SPRITE:
				synchronized (this) {
					if (spriteRun) {
						sprites.createUI(g);
					} else if (spriteRun = (sprites != null && sprites.size() > 0)) {
						sprites.createUI(g);
					}
				}
				break;
			case DRAW_DESKTOP:
				synchronized (this) {
					if (desktopRun) {
						desktop.createUI(g);
					} else if (desktopRun = (desktop != null && desktop.size() > 0)) {
						desktop.createUI(g);
					}
				}
				break;
			}
		}

		void update(LTimerContext c) {
			switch (type) {
			case DRAW_USER:
				synchronized (this) {
					screen.alter(c);
				}
				break;
			case DRAW_SPRITE:
				synchronized (this) {
					spriteRun = (sprites != null && sprites.size() > 0);
					if (spriteRun) {
						sprites.update(c.timeSinceLastUpdate);
					}
				}
				break;
			case DRAW_DESKTOP:
				synchronized (this) {
					desktopRun = (desktop != null && desktop.size() > 0);
					if (desktopRun) {
						desktop.update(c.timeSinceLastUpdate);
					}
				}
				break;
			}
		}

	}

	private boolean isDrawing;

	public void yieldDraw() {
		notifyDraw();
		waitUpdate();
	}

	public void yieldUpdate() {
		notifyUpdate();
		waitDraw();
	}

	public synchronized void notifyDraw() {
		this.isDrawing = true;
		this.notifyAll();
	}

	public synchronized void notifyUpdate() {
		this.isDrawing = false;
		this.notifyAll();
	}

	public synchronized void waitDraw() {
		for (; !isDrawing;) {
			try {
				this.wait();
			} catch (InterruptedException ex) {
			}
		}
	}

	public synchronized void waitUpdate() {
		for (; isDrawing;) {
			try {
				this.wait();
			} catch (InterruptedException ex) {
			}
		}
	}

	private boolean spriteRun, desktopRun;

	private boolean fristPaintFlag;

	private boolean secondPaintFlag;

	private boolean lastPaintFlag;

	public static enum SensorDirection {
		NONE, LEFT, RIGHT, UP, DOWN;
	}

	public abstract void draw(GLEx g);

	public final static int SCREEN_NOT_REPAINT = 0;

	public final static int SCREEN_BITMAP_REPAINT = -1;

	public final static int SCREEN_CANVAS_REPAINT = -2;

	public final static int SCREEN_COLOR_REPAINT = -3;

	// 0.3.2版新增的简易重力控制接口
	private GravityHandler gravityHandler;

	private LColor color;

	private int touchX, touchY, lastTouchX, lastTouchY, touchDX, touchDY;

	public long elapsedTime;

	private final static boolean[] touchType, keyType;

	private int touchButtonPressed = LInput.NO_BUTTON,
			touchButtonReleased = LInput.NO_BUTTON;

	private int keyButtonPressed = LInput.NO_KEY,
			keyButtonReleased = LInput.NO_KEY;

	boolean isNext;

	private int mode, frame;

	private LTexture currentScreen;

	protected LProcess handler;

	private int width, height, halfWidth, halfHeight;

	private SensorDirection direction = SensorDirection.NONE;

	private LInput baseInput;

	// 精灵集合
	private Sprites sprites;

	// 桌面集合
	private Desktop desktop;

	private Point2i touch = new Point2i(0, 0);

	private boolean isLoad, isLock, isClose, isTranslate, isGravity;

	private float tx, ty;

	// 首先绘制的对象
	private PaintOrder fristOrder;

	// 其次绘制的对象
	private PaintOrder secondOrder;

	// 最后绘制的对象
	private PaintOrder lastOrder;

	private PaintOrder userOrder, spriteOrder, desktopOrder;

	private ArrayList<RectBox> limits = new ArrayList<RectBox>(10);

	private boolean replaceLoading;

	private int replaceScreenSpeed = 8;

	private LTimer replaceDelay = new LTimer(0);

	private Screen replaceDstScreen;

	private EmptyObject dstPos = new EmptyObject();

	private MoveMethod replaceMethod = MoveMethod.FROM_LEFT;

	// Screen切换方式
	public static enum MoveMethod {
		FROM_LEFT, FROM_UP, FROM_DOWN, FROM_RIGHT, FROM_UPPER_LEFT, FROM_UPPER_RIGHT, FROM_LOWER_LEFT, FROM_LOWER_RIGHT, OUT_LEFT, OUT_UP, OUT_DOWN, OUT_RIGHT, OUT_UPPER_LEFT, OUT_UPPER_RIGHT, OUT_LOWER_LEFT, OUT_LOWER_RIGHT
	}

	private boolean isScreenFrom = false;

	public void replaceScreen(final Screen screen, MoveMethod m) {
		if (screen != null && screen != this) {
			screen.setOnLoadState(false);
			setLock(true);
			screen.setLock(true);
			this.replaceMethod = m;
			this.replaceDstScreen = screen;

			screen.setRepaintMode(SCREEN_CANVAS_REPAINT);
			switch (m) {
			case FROM_LEFT:
				dstPos.setLocation(-getWidth(), 0);
				isScreenFrom = true;
				break;
			case FROM_RIGHT:
				dstPos.setLocation(getWidth(), 0);
				isScreenFrom = true;
				break;
			case FROM_UP:
				dstPos.setLocation(0, -getHeight());
				isScreenFrom = true;
				break;
			case FROM_DOWN:
				dstPos.setLocation(0, getHeight());
				isScreenFrom = true;
				break;
			case FROM_UPPER_LEFT:
				dstPos.setLocation(-getWidth(), -getHeight());
				isScreenFrom = true;
				break;
			case FROM_UPPER_RIGHT:
				dstPos.setLocation(getWidth(), -getHeight());
				isScreenFrom = true;
				break;
			case FROM_LOWER_LEFT:
				dstPos.setLocation(-getWidth(), getHeight());
				isScreenFrom = true;
				break;
			case FROM_LOWER_RIGHT:
				dstPos.setLocation(getWidth(), getHeight());
				isScreenFrom = true;
				break;
			default:
				dstPos.setLocation(0, 0);
				isScreenFrom = false;
				break;
			}

			RealtimeProcessManager.get().addProcess(new RealtimeProcess() {
				
				@Override
				public void run() {
					screen.onCreate(LSystem.screenRect.width,
							LSystem.screenRect.height);
					screen.setClose(false);
					screen.onLoad();
					screen.setRepaintMode(SCREEN_CANVAS_REPAINT);
					screen.onLoaded();
					screen.setOnLoadState(true);
					kill();
				}
			});
			
			replaceLoading = true;
		}
	}

	public int getReplaceScreenSpeed() {
		return replaceScreenSpeed;
	}

	public void setReplaceScreenSpeed(int s) {
		this.replaceScreenSpeed = s;
	}

	public void setReplaceScreenDelay(long d) {
		replaceDelay.setDelay(d);
	}

	public long getReplaceScreenDelay() {
		return replaceDelay.getDelay();
	}

	private void submitReplaceScreen() {
		if (handler != null) {
			handler.setCurrentScreen(replaceDstScreen);
		}
		replaceLoading = false;
	}

	public void addTouchLimit(LObject c) {
		if (c != null) {
			limits.add(c.getCollisionArea());
		}
	}

	public void addTouchLimit(RectBox r) {
		if (r != null) {
			limits.add(r);
		}
	}

	public boolean isClickLimit(LTouch e) {
		return isClickLimit(e.x(), e.y());
	}

	public boolean isClickLimit(int x, int y) {
		if (limits.size() == 0) {
			return false;
		}
		for (RectBox rect : limits) {
			if (rect.contains(x, y)) {
				return true;
			}
		}
		return false;
	}

	protected final PaintOrder DRAW_USER_PAINT() {
		if (userOrder == null) {
			userOrder = new PaintOrder(DRAW_USER, this);
		}
		return userOrder;
	}

	protected final PaintOrder DRAW_SPRITE_PAINT() {
		if (spriteOrder == null) {
			spriteOrder = new PaintOrder(DRAW_SPRITE, this);
		}
		return spriteOrder;
	}

	protected final PaintOrder DRAW_DESKTOP_PAINT() {
		if (desktopOrder == null) {
			desktopOrder = new PaintOrder(DRAW_DESKTOP, this);
		}
		return desktopOrder;
	}

	static {
		keyType = new boolean[15];
		touchType = new boolean[15];
	}

	public Screen() {
		LSystem.AUTO_REPAINT = true;
		Screen.StaticCurrentSceen = this;
		this.handler = LSystem.screenProcess;
		this.width = LSystem.screenRect.width;
		this.height = LSystem.screenRect.height;
		this.halfWidth = width / 2;
		this.halfHeight = height / 2;
		this.fristOrder = DRAW_USER_PAINT();
		this.secondOrder = DRAW_SPRITE_PAINT();
		this.lastOrder = DRAW_DESKTOP_PAINT();
		this.fristPaintFlag = true;
		this.secondPaintFlag = true;
		this.lastPaintFlag = true;
	}

	public boolean contains(float x, float y) {
		return LSystem.screenRect.contains(x, y);
	}

	public boolean contains(float x, float y, float w, float h) {
		return LSystem.screenRect.contains(x, y, w, h);
	}

	/**
	 * 当Screen被创建(或再次加载)时将调用此函数
	 * 
	 * @param width
	 * @param height
	 */
	public void onCreate(int width, int height) {
		this.mode = SCREEN_CANVAS_REPAINT;
		this.baseInput = this;
		this.width = width;
		this.height = height;
		this.halfWidth = width / 2;
		this.halfHeight = height / 2;
		this.touchX = touchY = lastTouchX = lastTouchY = touchDX = touchDY = 0;
		this.isLoad = isLock = isClose = isTranslate = isGravity = false;
		if (sprites != null) {
			sprites.dispose();
			sprites = null;
		}
		this.sprites = new Sprites(width, height);
		if (desktop != null) {
			desktop.dispose();
			desktop = null;
		}
		this.desktop = new Desktop(baseInput, width, height);
		this.isNext = true;
	}

	public void addLoad(Updateable u) {
		if (handler != null) {
			handler.addLoad(u);
		}
	}

	public void removeLoad(Updateable u) {
		if (handler != null) {
			handler.removeLoad(u);
		}
	}

	public void removeAllLoad() {
		if (handler != null) {
			handler.removeAllLoad();
		}
	}

	public void addUnLoad(Updateable u) {
		if (handler != null) {
			handler.addUnLoad(u);
		}
	}

	public void removeUnLoad(Updateable u) {
		if (handler != null) {
			handler.removeUnLoad(u);
		}
	}

	public void removeAllUnLoad() {
		if (handler != null) {
			handler.removeAllUnLoad();
		}
	}

	public void addDrawing(Drawable d) {
		if (handler != null) {
			handler.addDrawing(d);
		}
	}

	public void removeDrawing(Drawable d) {
		if (handler != null) {
			handler.removeDrawing(d);
		}
	}

	public void removeAllDrawing() {
		if (handler != null) {
			handler.removeAllDrawing();
		}
	}

	/**
	 * 当执行Screen转换时将调用此函数(如果返回的LTransition不为null，则渐变效果会被执行)
	 * 
	 * @return
	 */
	public LTransition onTransition() {
		return null;
	}

	/**
	 * 设定重力系统是否启动
	 * 
	 * @param g
	 * @return
	 */
	public synchronized GravityHandler setGravity(boolean g) {
		if (g && gravityHandler == null) {
			gravityHandler = new GravityHandler();
		}
		this.isGravity = g;
		return gravityHandler;
	}

	/**
	 * 判断重力系统是否启动
	 * 
	 * @return
	 */
	public synchronized boolean isGravity() {
		return this.isGravity;
	}

	/**
	 * 获得当前重力器句柄
	 * 
	 * @return
	 */
	public GravityHandler getGravityHandler() {
		return setGravity(true);
	}

	/**
	 * 获得当前游戏事务运算时间是否被锁定
	 * 
	 * @return
	 */
	public boolean isLock() {
		return isLock;
	}

	/**
	 * 锁定游戏事务运算时间
	 * 
	 * @param lock
	 */
	public void setLock(boolean lock) {
		this.isLock = lock;
	}

	/**
	 * 关闭游戏
	 * 
	 * @param close
	 */
	public void setClose(boolean close) {
		this.isClose = close;
	}

	/**
	 * 判断游戏是否被关闭
	 * 
	 * @return
	 */
	public boolean isClose() {
		return isClose;
	}

	/**
	 * 设定当前帧
	 * 
	 * @param frame
	 */
	public void setFrame(int frame) {
		this.frame = frame;
	}

	/**
	 * 返回当前帧
	 * 
	 * @return
	 */
	public int getFrame() {
		return frame;
	}

	/**
	 * 移动当前帧
	 * 
	 * @return
	 */
	public synchronized boolean next() {
		this.frame++;
		return isNext;
	}

	/**
	 * 暂停当前Screen指定活动帧数
	 * 
	 * @param i
	 */
	public synchronized void waitFrame(int i) {
		for (int wait = frame + i; frame < wait;) {
			try {
				super.wait();
			} catch (Exception ex) {
			}
		}
	}

	/**
	 * 暂停当前Screen指定时间
	 * 
	 * @param i
	 */
	public synchronized void waitTime(long i) {
		for (long time = System.currentTimeMillis() + i; System
				.currentTimeMillis() < time;)
			try {
				super.wait(time - System.currentTimeMillis());
			} catch (Exception ex) {
			}
	}

	/**
	 * 初始化时加载的数据
	 */
	public void onLoad() {

	}

	/**
	 * 初始化加载完毕
	 * 
	 */
	public void onLoaded() {

	}

	/**
	 * 改变资源加载状态
	 */
	public void setOnLoadState(boolean flag) {
		this.isLoad = flag;
	}

	/**
	 * 获得当前资源加载是否完成
	 */
	public boolean isOnLoadComplete() {
		return isLoad;
	}

	/**
	 * 取出第一个Screen并执行
	 * 
	 */
	public void runFirstScreen() {
		if (handler != null) {
			handler.runFirstScreen();
		}
	}

	/**
	 * 取出最后一个Screen并执行
	 */
	public void runLastScreen() {
		if (handler != null) {
			handler.runLastScreen();
		}
	}

	/**
	 * 运行指定位置的Screen
	 * 
	 * @param index
	 */
	public void runIndexScreen(int index) {
		if (handler != null) {
			handler.runIndexScreen(index);
		}
	}

	/**
	 * 运行自当前Screen起的上一个Screen
	 */
	public void runPreviousScreen() {
		if (handler != null) {
			handler.runPreviousScreen();
		}
	}

	/**
	 * 运行自当前Screen起的下一个Screen
	 */
	public void runNextScreen() {
		if (handler != null) {
			handler.runNextScreen();
		}
	}

	/**
	 * 向缓存中添加Screen数据，但是不立即执行
	 * 
	 * @param screen
	 */
	public void addScreen(Screen screen) {
		if (handler != null) {
			handler.addScreen(screen);
		}
	}

	/**
	 * 获得保存的Screen列表
	 * 
	 * @return
	 */
	public LinkedList<Screen> getScreens() {
		if (handler != null) {
			return handler.getScreens();
		}
		return null;
	}

	/**
	 * 获得缓存的Screen总数
	 */
	public int getScreenCount() {
		if (handler != null) {
			return handler.getScreenCount();
		}
		return 0;
	}

	/**
	 * 返回精灵监听
	 * 
	 * @return
	 */

	public SpriteListener getSprListerner() {
		if (sprites == null) {
			return null;
		}
		return sprites.getSprListerner();
	}

	/**
	 * 监听Screen中精灵
	 * 
	 * @param sprListerner
	 */

	public void setSprListerner(SpriteListener sprListerner) {
		if (sprites == null) {
			return;
		}
		sprites.setSprListerner(sprListerner);
	}

	/**
	 * 获得当前Screen类名
	 */
	public String getName() {
		return getClass().getSimpleName();
	}

	public void sohwAwtTextInput(final TextEvent listener, final String title,
			final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final String output = JOptionPane.showInputDialog(null, title,
						text);
				if (output != null)
					LSystem.load(new Updateable() {
						
						@Override
						public void action(Object a) {
							listener.input(output);
						}
					});
				else
					LSystem.load(new Updateable() {
						
						@Override
						public void action(Object a) {
							listener.cancel();
						}
					});
			}
		});
	}

	public void showAwtTextInput(final TextEvent listener, final String title,
			final String placeholder) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				JPanel panel = new JPanel(new FlowLayout());

				JPanel textPanel = new JPanel() {

					private static final long serialVersionUID = 1L;

					public boolean isOptimizedDrawingEnabled() {
						return false;
					};
				};

				textPanel.setLayout(new OverlayLayout(textPanel));
				panel.add(textPanel);

				final JTextField textField = new JTextField(20);
				textField.setAlignmentX(0.0f);
				textPanel.add(textField);

				final JLabel placeholderLabel = new JLabel(placeholder);
				placeholderLabel.setForeground(Color.GRAY);
				placeholderLabel.setAlignmentX(0.0f);
				textPanel.add(placeholderLabel, 0);

				textField.getDocument().addDocumentListener(
						new DocumentListener() {

							@Override
							public void removeUpdate(DocumentEvent arg0) {
								this.updated();
							}

							@Override
							public void insertUpdate(DocumentEvent arg0) {
								this.updated();
							}

							@Override
							public void changedUpdate(DocumentEvent arg0) {
								this.updated();
							}

							private void updated() {
								if (textField.getText().length() == 0)
									placeholderLabel.setVisible(true);
								else
									placeholderLabel.setVisible(false);
							}
						});

				JOptionPane pane = new JOptionPane(panel,
						JOptionPane.QUESTION_MESSAGE,
						JOptionPane.OK_CANCEL_OPTION, null, null, null);

				pane.setInitialValue(null);
				pane.setComponentOrientation(JOptionPane.getRootFrame()
						.getComponentOrientation());

				Border border = textField.getBorder();
				placeholderLabel.setBorder(new EmptyBorder(border
						.getBorderInsets(textField)));

				JDialog dialog = pane.createDialog(null, title);
				pane.selectInitialValue();

				dialog.addWindowFocusListener(new WindowFocusListener() {

					@Override
					public void windowLostFocus(WindowEvent arg0) {
					}

					@Override
					public void windowGainedFocus(WindowEvent arg0) {
						textField.requestFocusInWindow();
					}
				});

				dialog.setVisible(true);
				dialog.dispose();

				Object selectedValue = pane.getValue();

				if (selectedValue != null
						&& (selectedValue instanceof Integer)
						&& ((Integer) selectedValue).intValue() == JOptionPane.OK_OPTION) {
					listener.input(textField.getText());
				} else {
					listener.cancel();
				}

			}
		});
	}

	/**
	 * 设定模拟按钮监听器
	 */

	public void setEmulatorListener(EmulatorListener emulator) {
		if (LSystem.screenProcess != null) {
			LSystem.screenProcess.setEmulatorListener(emulator);
		}
	}

	/**
	 * 返回模拟按钮集合
	 * 
	 * @return
	 */

	public EmulatorButtons getEmulatorButtons() {
		if (LSystem.screenProcess != null) {
			return LSystem.screenProcess.getEmulatorButtons();
		}
		return null;
	}

	/**
	 * 设定模拟按钮组是否显示
	 * 
	 * @param visible
	 */

	public void emulatorButtonsVisible(boolean visible) {
		if (LSystem.screenProcess != null) {
			try {
				EmulatorButtons es = LSystem.screenProcess.getEmulatorButtons();
				es.setVisible(visible);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 设定背景图像
	 * 
	 * @param screen
	 */
	public void setBackground(LTexture background) {
		if (background != null) {
			setRepaintMode(SCREEN_BITMAP_REPAINT);
			LTexture screen = null;
			if (background.getWidth() != getWidth()
					|| background.getHeight() != getHeight()) {
				screen = background.scale(getWidth(), getHeight());
			} else {
				screen = background;
			}
			LTexture tmp = currentScreen;
			currentScreen = screen;
			if (tmp != null) {
				tmp.destroy();
				tmp = null;
			}
		} else {
			setRepaintMode(SCREEN_CANVAS_REPAINT);
		}
	}

	/**
	 * 设定背景图像
	 */
	public void setBackground(String fileName) {
		this.setBackground(new LTexture(fileName, Format.STATIC));
	}

	public void setBackground(Color c) {
		setBackground(new LColor(c.getRed(), c.getGreen(), c.getBlue(),
				c.getAlpha()));
	}

	/**
	 * 设定背景颜色
	 * 
	 * @param c
	 */
	public void setBackground(LColor c) {
		setRepaintMode(SCREEN_COLOR_REPAINT);
		if (color == null) {
			color = new LColor(c);
		} else {
			color.setColor(c.r, c.g, c.b, c.a);
		}
	}

	public LColor getColor() {
		return color;
	}

	/**
	 * 返回背景图像
	 * 
	 * @return
	 */
	public LTexture getBackground() {
		return currentScreen;
	}

	public Desktop getDesktop() {
		return desktop;
	}

	public Sprites getSprites() {
		return sprites;
	}

	/**
	 * 返回与指定类匹配的组件
	 */

	public ArrayList<LComponent> getComponents(Class<? extends LComponent> clazz) {
		if (desktop != null) {
			return desktop.getComponents(clazz);
		}
		return null;
	}

	/**
	 * 返回位于屏幕顶部的组件
	 * 
	 * @return
	 */

	public LComponent getTopComponent() {
		if (desktop != null) {
			return desktop.getTopComponent();
		}
		return null;
	}

	/**
	 * 返回位于屏幕底部的组件
	 * 
	 * @return
	 */

	public LComponent getBottomComponent() {
		if (desktop != null) {
			return desktop.getBottomComponent();
		}
		return null;
	}

	/**
	 * 返回位于屏幕顶部的图层
	 */

	public LLayer getTopLayer() {
		if (desktop != null) {
			return desktop.getTopLayer();
		}
		return null;
	}

	/**
	 * 返回位于屏幕底部的图层
	 */

	public LLayer getBottomLayer() {
		if (desktop != null) {
			return desktop.getBottomLayer();
		}
		return null;
	}

	/**
	 * 返回所有指定类产生的精灵
	 * 
	 */

	public ArrayList<ISprite> getSprites(Class<? extends ISprite> clazz) {
		if (sprites != null) {
			return sprites.getSprites(clazz);
		}
		return null;
	}

	/**
	 * 返回位于数据顶部的精灵
	 * 
	 */

	public ISprite getTopSprite() {
		if (sprites != null) {
			return sprites.getTopSprite();
		}
		return null;
	}

	/**
	 * 返回位于数据底部的精灵
	 * 
	 */

	public ISprite getBottomSprite() {
		if (sprites != null) {
			return sprites.getBottomSprite();
		}
		return null;
	}

	/**
	 * 添加游戏精灵
	 * 
	 * @param sprite
	 */

	public void add(ISprite sprite) {
		if (sprites != null) {
			sprites.add(sprite);
		}
	}

	/**
	 * 添加游戏组件
	 * 
	 * @param comp
	 */

	public void add(LComponent comp) {
		if (desktop != null) {
			desktop.add(comp);
		}
	}

	public void remove(ISprite sprite) {
		if (sprites != null) {
			sprites.remove(sprite);
		}
	}

	public void removeSprite(Class<? extends ISprite> clazz) {
		if (sprites != null) {
			sprites.remove(clazz);
		}
	}

	public void remove(LComponent comp) {
		if (desktop != null) {
			desktop.remove(comp);
		}
	}

	public void removeComponent(Class<? extends LComponent> clazz) {
		if (desktop != null) {
			desktop.remove(clazz);
		}
	}

	public void removeAll() {
		if (sprites != null) {
			sprites.removeAll();
		}
		if (desktop != null) {
			desktop.getContentPane().clear();
		}
	}

	/**
	 * 判断是否点中指定精灵
	 * 
	 * @param sprite
	 * @return
	 */

	public boolean onClick(ISprite sprite) {
		if (sprite == null) {
			return false;
		}
		if (sprite.isVisible()) {
			RectBox rect = sprite.getCollisionBox();
			if (rect.contains(touchX, touchY)
					|| rect.intersects(touchX, touchY)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否点中指定组件
	 * 
	 * @param component
	 * @return
	 */

	public boolean onClick(LComponent component) {
		if (component == null) {
			return false;
		}
		if (component.isVisible()) {
			RectBox rect = component.getCollisionBox();
			if (rect.contains(touchX, touchY)
					|| rect.intersects(touchX, touchY)) {
				return true;
			}
		}
		return false;
	}

	public void centerOn(final LObject object) {
		LObject.centerOn(object, getWidth(), getHeight());
	}

	public void topOn(final LObject object) {
		LObject.topOn(object, getWidth(), getHeight());
	}

	public void leftOn(final LObject object) {
		LObject.leftOn(object, getWidth(), getHeight());
	}

	public void rightOn(final LObject object) {
		LObject.rightOn(object, getWidth(), getHeight());
	}

	public void bottomOn(final LObject object) {
		LObject.bottomOn(object, getWidth(), getHeight());
	}

	/**
	 * 获得背景显示模式
	 */
	public int getRepaintMode() {
		return mode;
	}

	/**
	 * 设定背景刷新模式
	 * 
	 * @param mode
	 */
	public void setRepaintMode(int mode) {
		this.mode = mode;
	}

	public void setLocation(float x, float y) {
		this.tx = x;
		this.ty = y;
		this.isTranslate = (tx != 0 || ty != 0);
	}

	public void setX(float x) {
		setLocation(x, ty);
	}

	public void setY(float y) {
		setLocation(tx, y);
	}

	public float getX() {
		return this.tx;
	}

	public float getY() {
		return this.ty;
	}

	protected void afterUI(GLEx g) {

	}

	protected void beforeUI(GLEx g) {

	}

	private final void repaint(GLEx g) {
		if (!isClose) {
			if (isTranslate) {
				g.translate(tx, ty);
			}
			afterUI(g);
			if (fristPaintFlag) {
				fristOrder.paint(g);
			}
			if (secondPaintFlag) {
				secondOrder.paint(g);
			}
			if (lastPaintFlag) {
				lastOrder.paint(g);
			}
			beforeUI(g);
			if (useScreenListener) {
				for (ScreenListener t : screens) {
					t.draw(g);
				}
			}
			if (isTranslate) {
				g.translate(-tx, -ty);
			}
		}
	}

	public synchronized void createUI(GLEx g) {
		if (isClose) {
			return;
		}
		if (replaceLoading) {
			if (replaceDstScreen == null
					|| !replaceDstScreen.isOnLoadComplete()) {
				repaint(g);
			} else if (replaceDstScreen.isOnLoadComplete()) {
				if (isScreenFrom) {
					repaint(g);
					if (replaceDstScreen.color != null) {
						g.setColor(replaceDstScreen.color);
						g.fillRect(dstPos.x(), dstPos.y(), getWidth(),
								getHeight());
						g.resetColor();
					}
					if (replaceDstScreen.currentScreen != null) {
						g.drawTexture(replaceDstScreen.currentScreen,
								dstPos.x(), dstPos.y(), getWidth(), getHeight());
					}
					if (dstPos.x() != 0 || dstPos.y() != 0) {
						g.setClip(dstPos.x(), dstPos.y(), getWidth(),
								getHeight());
						g.translate(dstPos.x(), dstPos.y());
					}
					replaceDstScreen.createUI(g);
					if (dstPos.x() != 0 || dstPos.y() != 0) {
						g.translate(-dstPos.x(), -dstPos.y());
						g.clearClip();
					}
				} else {
					if (replaceDstScreen.color != null) {
						g.setColor(replaceDstScreen.color);
						g.fillRect(0, 0, getWidth(), getHeight());
						g.resetColor();
					}
					if (replaceDstScreen.currentScreen != null) {
						g.drawTexture(replaceDstScreen.currentScreen, 0, 0,
								getWidth(), getHeight());
					}
					replaceDstScreen.createUI(g);
					if (color != null) {
						g.setColor(color);
						g.fillRect(dstPos.x(), dstPos.y(), getWidth(),
								getHeight());
						g.resetColor();
					}
					if (getBackground() != null) {
						g.drawTexture(currentScreen, dstPos.x(), dstPos.y(),
								getWidth(), getHeight());
					}
					if (dstPos.x() != 0 || dstPos.y() != 0) {
						g.setClip(dstPos.x(), dstPos.y(), getWidth(),
								getHeight());
						g.translate(dstPos.x(), dstPos.y());
					}
					repaint(g);
					if (dstPos.x() != 0 || dstPos.y() != 0) {
						g.translate(-dstPos.x(), -dstPos.y());
						g.clearClip();
					}
				}
			}
		} else {
			repaint(g);
		}
	}

	private final void process(final LTimerContext timer) {
		this.elapsedTime = timer.getTimeSinceLastUpdate();
		if (!isClose) {
			if (isGravity) {
				gravityHandler.update(elapsedTime);
			}
			if (fristPaintFlag) {
				fristOrder.update(timer);
			}
			if (secondPaintFlag) {
				secondOrder.update(timer);
			}
			if (lastPaintFlag) {
				lastOrder.update(timer);
			}
			if (useScreenListener) {
				for (ScreenListener t : screens) {
					t.update(elapsedTime);
				}
			}
		}
		this.touchDX = touchX - lastTouchX;
		this.touchDY = touchY - lastTouchY;
		this.lastTouchX = touchX;
		this.lastTouchY = touchY;
		this.touchButtonReleased = NO_BUTTON;
	}

	public void runTimer(final LTimerContext timer) {
		if (isClose) {
			return;
		}
		if (replaceLoading) {
			if (replaceDstScreen == null
					|| !replaceDstScreen.isOnLoadComplete()) {
				process(timer);
			} else if (replaceDstScreen.isOnLoadComplete()) {
				process(timer);
				if (replaceDelay.action(timer)) {
					switch (replaceMethod) {
					case FROM_LEFT:
						dstPos.move_right(replaceScreenSpeed);
						if (dstPos.x() >= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case FROM_RIGHT:
						dstPos.move_left(replaceScreenSpeed);
						if (dstPos.x() <= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case FROM_UP:
						dstPos.move_down(replaceScreenSpeed);
						if (dstPos.y() >= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case FROM_DOWN:
						dstPos.move_up(replaceScreenSpeed);
						if (dstPos.y() <= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_LEFT:
						dstPos.move_left(replaceScreenSpeed);
						if (dstPos.x() < -getWidth()) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_RIGHT:
						dstPos.move_right(replaceScreenSpeed);
						if (dstPos.x() > getWidth()) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_UP:
						dstPos.move_up(replaceScreenSpeed);
						if (dstPos.y() < -getHeight()) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_DOWN:
						dstPos.move_down(replaceScreenSpeed);
						if (dstPos.y() > getHeight()) {
							submitReplaceScreen();
							return;
						}
						break;
					case FROM_UPPER_LEFT:
						if (dstPos.y() < 0) {
							dstPos.move_45D_right(replaceScreenSpeed);
						} else {
							dstPos.move_right(replaceScreenSpeed);
						}
						if (dstPos.y() >= 0 && dstPos.x() >= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case FROM_UPPER_RIGHT:
						if (dstPos.y() < 0) {
							dstPos.move_45D_down(replaceScreenSpeed);
						} else {
							dstPos.move_left(replaceScreenSpeed);
						}
						if (dstPos.y() >= 0 && dstPos.x() <= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case FROM_LOWER_LEFT:
						if (dstPos.y() > 0) {
							dstPos.move_45D_up(replaceScreenSpeed);
						} else {
							dstPos.move_right(replaceScreenSpeed);
						}
						if (dstPos.y() <= 0 && dstPos.x() >= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case FROM_LOWER_RIGHT:
						if (dstPos.y() > 0) {
							dstPos.move_45D_left(replaceScreenSpeed);
						} else {
							dstPos.move_left(replaceScreenSpeed);
						}
						if (dstPos.y() <= 0 && dstPos.x() <= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_UPPER_LEFT:
						dstPos.move_45D_left(replaceScreenSpeed);
						if (dstPos.x() < -getWidth()
								|| dstPos.y() <= -getHeight()) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_UPPER_RIGHT:
						dstPos.move_45D_up(replaceScreenSpeed);
						if (dstPos.x() > getWidth()
								|| dstPos.y() < -getHeight()) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_LOWER_LEFT:
						dstPos.move_45D_down(replaceScreenSpeed);
						if (dstPos.x() < -getWidth()
								|| dstPos.y() > getHeight()) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_LOWER_RIGHT:
						dstPos.move_45D_right(replaceScreenSpeed);
						if (dstPos.x() > getWidth() || dstPos.y() > getHeight()) {
							submitReplaceScreen();
							return;
						}
						break;
					default:
						break;
					}
					replaceDstScreen.runTimer(timer);
				}
			}
		} else {
			process(timer);
		}
	}

	public LInput getInput() {
		return baseInput;
	}

	public void setNext(boolean next) {
		this.isNext = next;
	}

	public abstract void alter(LTimerContext timer);

	/**
	 * 设定游戏窗体
	 */
	public void setScreen(Screen screen) {
		if (handler != null) {
			this.handler.setScreen(screen);
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * 刷新基础设置
	 */
	public void refresh() {
		for (int i = 0; i < touchType.length; i++) {
			touchType[i] = false;
		}
		touchDX = touchDY = 0;
		for (int i = 0; i < keyType.length; i++) {
			keyType[i] = false;
		}
	}

	public void resize(int width, int height) {
		this.touchX = touchY = lastTouchX = lastTouchY = touchDX = touchDY = 0;
	}

	public Point2i getTouch() {
		touch.set(touchX, touchY);
		return touch;
	}

	public boolean isPaused() {
		return LSystem.isPaused;
	}

	public int getTouchPressed() {
		return touchButtonPressed > LInput.NO_BUTTON ? touchButtonPressed
				: LInput.NO_BUTTON;
	}

	public int getTouchReleased() {
		return touchButtonReleased > LInput.NO_BUTTON ? touchButtonReleased
				: LInput.NO_BUTTON;
	}

	public boolean isTouchPressed(int button) {
		return touchButtonPressed == button;
	}

	public boolean isTouchReleased(int button) {
		return touchButtonReleased == button;
	}

	public int getTouchX() {
		return touchX;
	}

	public int getTouchY() {
		return touchY;
	}

	public int getTouchDX() {
		return touchDX;
	}

	public int getTouchDY() {
		return touchDY;
	}

	public boolean isTouchType(int type) {
		return touchType[type];
	}

	public int getKeyPressed() {
		return keyButtonPressed > LInput.NO_KEY ? keyButtonPressed
				: LInput.NO_KEY;
	}

	public boolean isKeyPressed(int keyCode) {
		return keyButtonPressed == keyCode;
	}

	public int getKeyReleased() {
		return keyButtonReleased > LInput.NO_KEY ? keyButtonReleased
				: LInput.NO_KEY;
	}

	public boolean isKeyReleased(int keyCode) {
		return keyButtonReleased == keyCode;
	}

	public boolean isKeyType(int type) {
		return keyType[type];
	}

	public void onResume() {

	}

	public void onPause() {

	}

	public final void keyPressed(LKey e) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		int type = e.getType();
		int code = e.getKeyCode();
		try {
			if (useScreenListener) {
				for (ScreenListener t : screens) {
					t.pressed(e);
				}
			}
			this.onKeyDown(e);
			keyType[type] = true;
			keyButtonPressed = code;
			keyButtonReleased = LInput.NO_KEY;
		} catch (Exception ex) {
			keyButtonPressed = LInput.NO_KEY;
			keyButtonReleased = LInput.NO_KEY;
			ex.printStackTrace();
		}
	}

	/**
	 * 设置键盘按下事件
	 * 
	 * @param code
	 */
	public void setKeyDown(int button) {
		try {
			keyButtonPressed = button;
			keyButtonReleased = LInput.NO_KEY;
		} catch (Exception e) {
		}
	}

	public final void keyReleased(LKey e) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		int type = e.getType();
		int code = e.getKeyCode();
		try {
			if (useScreenListener) {
				for (ScreenListener t : screens) {
					t.released(e);
				}
			}
			this.onKeyUp(e);
			keyType[type] = false;
			keyButtonReleased = code;
			keyButtonPressed = LInput.NO_KEY;
		} catch (Exception ex) {
			keyButtonPressed = LInput.NO_KEY;
			keyButtonReleased = LInput.NO_KEY;
			ex.printStackTrace();
		}
	}

	public void setKeyUp(int button) {
		try {
			keyButtonReleased = button;
			keyButtonPressed = LInput.NO_KEY;
		} catch (Exception e) {
		}
	}

	public void keyTyped(LKey e) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		onKeyTyped(e);
	}

	public void onKeyDown(LKey e) {

	}

	public void onKeyUp(LKey e) {

	}

	public void onKeyTyped(LKey e) {

	}

	public final void mousePressed(LTouch e) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		int type = e.getType();
		int button = e.getButton();
		try {
			touchType[type] = true;
			touchButtonPressed = button;
			touchButtonReleased = LInput.NO_BUTTON;
			if (useScreenListener) {
				for (ScreenListener t : screens) {
					t.pressed(e);
				}
			}
			if (!isClickLimit(e)) {
				touchDown(e);
			}
		} catch (Exception ex) {
			touchButtonPressed = LInput.NO_BUTTON;
			touchButtonReleased = LInput.NO_BUTTON;
			ex.printStackTrace();
		}
	}

	public abstract void touchDown(LTouch e);

	public void mouseReleased(LTouch e) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		if (isTranslate) {
			e.offset(tx, ty);
		}
		int type = e.getType();
		int button = e.getButton();
		try {
			touchType[type] = false;
			touchButtonReleased = button;
			touchButtonPressed = LInput.NO_BUTTON;
			if (useScreenListener) {
				for (ScreenListener t : screens) {
					t.released(e);
				}
			}
			if (!isClickLimit(e)) {
				touchUp(e);
			}
		} catch (Exception ex) {
			touchButtonPressed = LInput.NO_BUTTON;
			touchButtonReleased = LInput.NO_BUTTON;
			ex.printStackTrace();
		}
	}

	public abstract void touchUp(LTouch e);

	public void mouseMoved(LTouch e) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		if (isTranslate) {
			e.offset(tx, ty);
		}
		if (useScreenListener) {
			for (ScreenListener t : screens) {
				t.move(e);
			}
		}
		this.touchX = e.x();
		this.touchY = e.y();
		if (!isClickLimit(e)) {
			touchMove(e);
		}
	}

	public abstract void touchMove(LTouch e);

	public void mouseDragged(LTouch e) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		if (isTranslate) {
			e.offset(tx, ty);
		}
		if (useScreenListener) {
			for (ScreenListener t : screens) {
				t.drag(e);
			}
		}
		this.touchX = e.x();
		this.touchY = e.y();
		if (!isClickLimit(e)) {
			touchDrag(e);
		}
	}

	public abstract void touchDrag(LTouch e);

	public void move(double x, double y) {
		this.touchX = (int) x;
		this.touchY = (int) y;
	}

	public boolean isMoving() {
		return Touch.isDrag();
	}

	public int getHalfWidth() {
		return halfWidth;
	}

	public int getHalfHeight() {
		return halfHeight;
	}

	public SensorDirection getSensorDirection() {
		return direction;
	}

	public PaintOrder getFristOrder() {
		return fristOrder;
	}

	public void setFristOrder(PaintOrder fristOrder) {
		if (fristOrder == null) {
			this.fristPaintFlag = false;
		} else {
			this.fristPaintFlag = true;
			this.fristOrder = fristOrder;
		}
	}

	public PaintOrder getSecondOrder() {
		return secondOrder;
	}

	public void setSecondOrder(PaintOrder secondOrder) {
		if (secondOrder == null) {
			this.secondPaintFlag = false;
		} else {
			this.secondPaintFlag = true;
			this.secondOrder = secondOrder;
		}
	}

	public PaintOrder getLastOrder() {
		return lastOrder;
	}

	public void setLastOrder(PaintOrder lastOrder) {
		if (lastOrder == null) {
			this.lastPaintFlag = false;
		} else {
			this.lastPaintFlag = true;
			this.lastOrder = lastOrder;
		}
	}

	public final void destroy() {
		synchronized (this) {
			if (useScreenListener) {
				for (ScreenListener t : screens) {
					t.dispose();
				}
			}
			useScreenListener = false;
			replaceLoading = false;
			replaceDelay.setDelay(10);
			tx = ty = 0;
			isClose = true;
			isTranslate = false;
			isNext = false;
			isGravity = false;
			isLock = true;
			if (screens != null) {
				screens.clear();
				screens = null;
			}
			if (sprites != null) {
				sprites.dispose();
				sprites = null;
			}
			if (desktop != null) {
				desktop.dispose();
				desktop = null;
			}
			if (currentScreen != null) {
				LTexture parent = currentScreen.getParent();
				if (parent != null) {
					parent.closeChildAll();
					parent.destroy();
				} else {
					currentScreen.destroy();
				}
				currentScreen = null;
			}
			if (gravityHandler != null) {
				gravityHandler.dispose();
				gravityHandler = null;
			}
			if (releases != null) {
				for (LRelease r : releases) {
					if (r != null) {
						r.dispose();
					}
				}
				releases.clear();
			}
			dispose();
		}
	}

	public void setAutoDestory(final boolean a) {
		if (desktop != null) {
			desktop.setAutoDestory(a);
		}
	}

	public boolean isAutoDestory() {
		if (desktop != null) {
			return desktop.isAutoDestory();
		}
		return false;
	}

	/**
	 * 释放函数内资源
	 * 
	 */
	public void dispose() {

	}
}
