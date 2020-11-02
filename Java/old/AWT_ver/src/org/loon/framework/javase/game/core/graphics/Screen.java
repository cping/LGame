package org.loon.framework.javase.game.core.graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.loon.framework.javase.game.action.sprite.ISprite;
import org.loon.framework.javase.game.action.sprite.Sprites;
import org.loon.framework.javase.game.action.sprite.Sprites.SpriteListener;
import org.loon.framework.javase.game.core.EmulatorButtons;
import org.loon.framework.javase.game.core.EmulatorListener;
import org.loon.framework.javase.game.core.LHandler;
import org.loon.framework.javase.game.core.LInput;
import org.loon.framework.javase.game.core.LObject;
import org.loon.framework.javase.game.core.LRelease;
import org.loon.framework.javase.game.core.LSystem;
import org.loon.framework.javase.game.core.LTransition;
import org.loon.framework.javase.game.core.geom.RectBox;
import org.loon.framework.javase.game.core.graphics.component.LLayer;
import org.loon.framework.javase.game.core.graphics.component.awt.AWTInputDialog;
import org.loon.framework.javase.game.core.graphics.component.awt.AWTMessageDialog;
import org.loon.framework.javase.game.core.graphics.component.awt.AWTOpenDialog;
import org.loon.framework.javase.game.core.graphics.component.awt.AWTYesNoCancelDialog;
import org.loon.framework.javase.game.core.graphics.device.LGraphics;
import org.loon.framework.javase.game.core.timer.LTimerContext;
import org.loon.framework.javase.game.media.SoundBox;
import org.loon.framework.javase.game.utils.FileUtils;
import org.loon.framework.javase.game.utils.GraphicsUtils;
import org.loon.framework.javase.game.utils.log.Level;
import org.loon.framework.javase.game.utils.log.Log;
import org.loon.framework.javase.game.utils.log.LogFactory;

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
 * @email ceponline@yahoo.com.cn
 * @version 0.1.2
 */
public abstract class Screen extends SoundBox implements MouseListener,
		MouseMotionListener, KeyListener, FocusListener, LInput, LRelease {

	public void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
		}
	}

	public static final int ACTION_DOWN = 0;

	public static final int ACTION_UP = 1;

	public static final int ACTION_MOVE = 2;

	public class LKey {

		int type;

		int keyCode;

		char keyChar;

		LKey() {

		}

		LKey(LKey key) {
			this.type = key.type;
			this.keyCode = key.keyCode;
			this.keyChar = key.keyChar;
		}

		public boolean equals(LKey e) {
			if (e == null) {
				return false;
			}
			if (e == this) {
				return true;
			}
			if (e.type == type && e.keyCode == keyCode && e.keyChar == keyChar) {
				return true;
			}
			return false;
		}

		public char getKeyChar() {
			return keyChar;
		}

		public int getKeyCode() {
			return keyCode;
		}

		public int getType() {
			return type;
		}

	}

	public static class LTouch {

		int type;

		float x, y;

		int action;

		int pointer;

		LTouch() {

		}

		LTouch(LTouch touch) {
			this.type = touch.type;
			this.x = touch.x;
			this.y = touch.y;
			this.action = touch.action;
			this.pointer = touch.pointer;
		}

		public boolean equals(LTouch e) {
			if (e == null) {
				return false;
			}
			if (e == this) {
				return true;
			}
			if (e.type == type && e.x == x && e.y == y && e.action == action
					&& e.pointer == pointer) {
				return true;
			}
			return false;
		}

		public int getAction() {
			return action;
		}

		public int getPointer() {
			return pointer;
		}

		public int getType() {
			return type;
		}

		public int x() {
			return (int) x;
		}

		public int y() {
			return (int) y;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}

	}

	public final static int SCREEN_NOT_REPAINT = 0;

	public final static int SCREEN_BITMAP_REPAINT = -1;

	public final static int SCREEN_CANVAS_REPAINT = -2;

	private int mode, frame;

	private boolean mouseExists, isNext, isDraging, isComponents;

	public long fps, elapsedTime;

	private Point mouse = new Point(0, 0);

	private int touchX, touchY, lastTouchX, lastTouchY, touchDX, touchDY;

	private final static boolean[] touchType, keyType;

	private int touchButtonPressed = LInput.NO_BUTTON,
			touchButtonReleased = LInput.NO_BUTTON;

	private int keyButtonPressed = LInput.NO_KEY,
			keyButtonReleased = LInput.NO_KEY;

	private LInput baseInput;

	private LHandler handler;

	// 精灵集合
	private Sprites sprites;

	// 桌面集合
	private Desktop desktop;

	// 背景屏幕
	private BufferedImage currentScreen;

	// 线程事件集合
	private final ArrayList<Runnable> runnables;

	private final Log log;

	private int id;

	private int width, height, halfWidth, halfHeight;

	private boolean isLoad, isLock, isClose, isTranslate;

	private float tx, ty;

	private static class ThreadID {

		private static int nextThreadID = 1;

		private static ThreadLocal<Object> threadID = new ThreadLocal<Object>() {
			protected synchronized Object initialValue() {
				return new Integer(nextThreadID++);
			}
		};

		public static int get() {
			return ((Integer) (threadID.get())).intValue();
		}

	}

	static {
		keyType = new boolean[15];
		touchType = new boolean[15];
	}

	/**
	 * 构造函数，初始化游戏屏幕
	 * 
	 */
	public Screen() {
		LSystem.AUTO_REPAINT = true;
		this.handler = LSystem.getSystemHandler();
		this.log = LogFactory.getInstance(this.getClass());
		this.runnables = new ArrayList<Runnable>(1);
		this.width = LSystem.screenRect.width;
		this.height = LSystem.screenRect.height;
		this.halfWidth = width / 2;
		this.halfHeight = height / 2;
		this.setFPS(getMaxFPS());
	}

	/**
	 * 当Screen被创建(或再次加载)时将调用此函数
	 * 
	 * @param width
	 * @param height
	 */
	public void onCreate(int width, int height) {
		this.mode = SCREEN_CANVAS_REPAINT;
		this.width = width;
		this.height = height;
		this.halfWidth = width / 2;
		this.halfHeight = height / 2;
		this.baseInput = this;
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
		this.mouseExists = true;
		this.touchX = touchY = lastTouchX = lastTouchY = touchDX = touchDY = 0;
		this.isDraging = isTranslate = isComponents = isLoad = isLock = isClose = false;
		this.isNext = true;
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
	 * 锁定设备交互
	 * 
	 * @param lock
	 */
	public void setLock(boolean lock) {
		this.isLock = lock;
	}

	/**
	 * 判断设备交互是否被锁定
	 * 
	 * @return
	 */
	public boolean isLock() {
		return isLock;
	}

	public void setClose(boolean close) {
		this.isClose = close;
	}

	public boolean isClose() {
		return isClose;
	}

	/**
	 * 设定当前帧
	 * 
	 * @param frame
	 */
	public synchronized void setFrame(int frame) {
		this.frame = frame;
	}

	/**
	 * 返回当前帧
	 * 
	 * @return
	 */
	public synchronized int getFrame() {
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
				super.wait(0L, 1);
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
	 * 设定模拟按钮监听器
	 */
	public void setEmulatorListener(EmulatorListener emulator) {
		if (handler.getDeploy() != null) {
			handler.getDeploy().setEmulatorListener(emulator);
		}
	}

	/**
	 * 返回模拟按钮集合
	 * 
	 * @return
	 */
	public EmulatorButtons getEmulatorButtons() {
		if (handler.getDeploy() != null) {
			return handler.getDeploy().getEmulatorButtons();
		}
		return null;
	}

	/**
	 * 设定模拟按钮组是否显示
	 * 
	 * @param visible
	 */
	public void emulatorButtonsVisible(boolean visible) {
		if (handler.getDeploy() != null) {
			try {
				EmulatorButtons es = handler.getDeploy().getEmulatorButtons();
				es.setVisible(visible);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 增减一个线程事件
	 * 
	 * @param runnable
	 */
	public final void callEvent(Runnable runnable) {
		synchronized (runnables) {
			runnables.add(runnable);
		}
	}

	/**
	 * 暂停指定的线程事件
	 * 
	 * @param runnable
	 */
	public final void callEventWait(Runnable runnable) {
		synchronized (runnable) {
			synchronized (runnables) {
				runnables.add(runnable);
			}
			try {
				runnable.wait();
			} catch (InterruptedException ex) {
			}
		}
	}

	/**
	 * 中断所有线程事件
	 * 
	 */
	public final void callEventInterrupt() {
		synchronized (runnables) {
			for (Iterator<Runnable> it = runnables.iterator(); it.hasNext();) {
				Object running = it.next();
				synchronized (running) {
					if (running instanceof Thread) {
						((Thread) running).setPriority(Thread.MIN_PRIORITY);
						((Thread) running).interrupt();
					}
				}
			}
		}
	}

	/**
	 * 运行线程事件
	 * 
	 */
	public final void callEvents() {
		callEvents(true);
	}

	/**
	 * 执行或中断指定的线程事件
	 * 
	 * @param execute
	 */
	private final void callEvents(boolean execute) {
		if (!execute) {
			synchronized (runnables) {
				runnables.clear();
			}
			return;
		}
		if (runnables.size() == 0) {
			return;
		}
		ArrayList<Runnable> runnableList;
		synchronized (runnables) {
			runnableList = new ArrayList<Runnable>(runnables);
			runnables.clear();
		}
		for (Iterator<Runnable> it = runnableList.iterator(); it.hasNext();) {
			Object running = it.next();
			synchronized (running) {
				try {
					if (running instanceof Thread) {
						Thread thread = (Thread) running;
						if (!thread.isAlive()) {
							thread.start();
						}

					} else {
						((Runnable) running).run();
					}
				} catch (Exception ex) {
				}
				running.notifyAll();
			}
		}
		runnableList = null;
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
	 * 获得当前Screen类名
	 */
	public String getName() {
		return FileUtils.getExtension(getClass().getName());
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

	public void runPreviousScreen() {
		if (handler != null) {
			handler.runPreviousScreen();
		}
	}

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
	 * 返回当前窗体线程ID
	 * 
	 * @return
	 */
	public int getID() {
		return id;
	}

	/**
	 * 变更窗体匹配的图像组件大小
	 * 
	 * @param w
	 * @param h
	 */
	public void resize() {
		this.id = ThreadID.get();
		if (handler != null) {
			int w = handler.getWidth(), h = handler.getHeight();
			if (w < 1 || h < 1) {
				w = h = 1;
			}
			if (w != width || h != height) {
				width = w;
				height = h;
			} else {
				Thread.yield();
				return;
			}
		}
		this.setBackground(GraphicsUtils.createIntdexedImage(width, height));
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
	 * 释放函数内资源
	 * 
	 */
	public void dispose() {

	}

	/**
	 * 记录info信息
	 * 
	 * @param message
	 */
	public void info(String message) {
		log.info(message);
	}

	/**
	 * 记录info信息
	 * 
	 * @param message
	 * @param tw
	 */
	public void info(String message, Throwable tw) {
		log.info(message, tw);
	}

	/**
	 * 记录debug信息
	 * 
	 * @param message
	 */
	public void debug(String message) {
		log.debug(message);
	}

	/**
	 * 记录debug信息
	 * 
	 * @param message
	 * @param tw
	 */
	public void debug(String message, Throwable tw) {
		log.debug(message, tw);
	}

	/**
	 * 记录warn信息
	 * 
	 * @param message
	 */
	public void warn(String message) {
		log.warn(message);
	}

	/**
	 * 记录warn信息
	 * 
	 * @param message
	 * @param tw
	 */
	public void warn(String message, Throwable tw) {
		log.warn(message, tw);
	}

	/**
	 * 记录error信息
	 * 
	 * @param message
	 */
	public void error(String message) {
		log.error(message);
	}

	/**
	 * 记录error信息
	 * 
	 * @param message
	 * @param tw
	 */
	public void error(String message, Throwable tw) {
		log.error(message, tw);
	}

	/**
	 * 输出日志
	 * 
	 * @param message
	 */
	public void log(String message) {
		log.log(message);
	}

	/**
	 * 输出日志
	 * 
	 * @param message
	 * @param tw
	 */
	public void log(String message, Throwable tw) {
		log.log(message, tw);
	}

	/**
	 * 设定是否在控制台显示日志
	 * 
	 * @param show
	 */
	public void logShow(boolean show) {
		log.setVisible(show);
	}

	/**
	 * 设定日志是否保存为文件
	 * 
	 * @param save
	 */
	public void logSave(boolean save) {
		log.setSave(save);
	}

	/**
	 * 日志保存地点
	 * 
	 * @param fileName
	 */
	public void logFileName(String fileName) {
		log.setFileName(fileName);
	}

	/**
	 * 设定日志等级
	 * 
	 * @param level
	 */
	public void logLevel(int level) {
		log.setLevel(level);
	}

	/**
	 * 设定日志等级
	 * 
	 * @param level
	 */
	public void logLevel(Level level) {
		log.setLevel(level);
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

	/**
	 * 将鼠标居中
	 * 
	 */
	public void mouseCenter() {
		try {
			GraphicsDevice device = GraphicsEnvironment
					.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			DisplayMode mode = device.getDisplayMode();
			this.touchX = this.lastTouchX = (mode.getWidth() / 2) - 10;
			this.touchY = this.lastTouchY = (mode.getHeight() / 2) - 10;
			LSystem.RO_BOT.mouseMove(this.touchX, this.touchY);
		} catch (Exception e) {
		}
	}

	/**
	 * 弹出一个AWT输入框
	 * 
	 * @param title
	 * @param message
	 */
	public AWTInputDialog showAWTInputDialog(final String title,
			final String message) {
		final AWTInputDialog dialog = new AWTInputDialog(title, message);
		callEvent(new Thread() {
			public void run() {
				dialog.setVisible(true);
			}
		});
		return dialog;
	}

	/**
	 * 弹出一个AWT提示框
	 * 
	 * @param title
	 * @param message
	 */
	public AWTMessageDialog showAWTMessageDialog(final String title,
			final String message) {
		final AWTMessageDialog dialog = new AWTMessageDialog(title, message);
		callEvent(new Thread() {
			public void run() {
				dialog.setVisible(true);
			}
		});
		return dialog;
	}

	/**
	 * 弹出一个AWT选择框
	 * 
	 * @param title
	 * @param message
	 */
	public AWTYesNoCancelDialog showAWTYesNoCancelDialog(String title,
			String message) {
		final AWTYesNoCancelDialog dialog = new AWTYesNoCancelDialog(title,
				message);
		callEvent(new Thread() {
			public void run() {
				dialog.setVisible(true);
			}
		});
		return dialog;
	}

	/**
	 * 弹出一个AWT文件选择框
	 * 
	 * @param title
	 * @param message
	 * @return
	 */
	public AWTOpenDialog showAWTOpenDialog(String title, String path) {
		final AWTOpenDialog dialog = new AWTOpenDialog(title, path);
		callEvent(new Thread() {
			public void run() {
				dialog.setVisible(true);
			}
		});
		return dialog;
	}

	/**
	 * 设定游戏句柄
	 */
	public synchronized void setupHandler(LHandler handler) {
		this.handler = handler;
	}

	/**
	 * 返回当前游戏句柄
	 * 
	 * @return
	 */
	public synchronized LHandler getHandler() {
		return handler;
	}

	/**
	 * 设置主窗体小图标
	 * 
	 * @param image
	 */
	public void setFrameIcon(Image icon) {
		if (handler != null) {
			handler.getScene().setIconImage(icon);
		}
	}

	/**
	 * 设置主窗体小图标
	 */
	public void setFrameIcon(String fileName) {
		if (handler != null) {
			handler.getScene().setIconImage(fileName);
		}
	}

	/**
	 * 设置主窗口标题
	 * 
	 * @param title
	 */
	public void setFrameTitle(String title) {
		if (handler != null) {
			handler.getScene().setTitle(title);
		}
	}

	/**
	 * 设定游戏屏幕
	 * 
	 * @param screen
	 */
	public synchronized void setScreen(Screen screen) {
		if (handler != null) {
			screen.setupHandler(handler);
			this.handler.setScreen(screen);
		}
	}

	/**
	 * 设定刷新率
	 * 
	 * @param fps
	 */
	public void setFPS(long fps) {
		if (handler != null) {
			handler.getDeploy().getView().setFPS(fps);
		}
	}

	/**
	 * 返回刷新率
	 */
	public long getFPS() {
		if (handler != null) {
			return handler.getDeploy().getView().getCurrentFPS();
		}
		return 0;
	}

	/**
	 * 返回最大刷新率
	 */
	public long getMaxFPS() {
		if (handler != null) {
			return handler.getDeploy().getView().getMaxFPS();
		}
		return 0;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getHalfWidth() {
		return halfWidth;
	}

	public int getHalfHeight() {
		return halfHeight;
	}

	public LInput getInput() {
		return baseInput;
	}

	public void setInput(LInput input) {
		this.baseInput = input;
	}

	public Point getTouch() {
		mouse.setLocation(touchX, touchY);
		return mouse;
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
	public ArrayList<?> getComponents(Class<? extends LComponent> clazz) {
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
	 * 添加游戏组件
	 * 
	 * @param comp
	 */
	public void add(LComponent comp) {
		if (desktop != null) {
			desktop.add(comp);
		}
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

	public synchronized void remove(LComponent comp) {
		if (desktop != null) {
			desktop.remove(comp);
		}
	}

	public synchronized void remove(Class<? extends LComponent> comp) {
		if (desktop != null) {
			desktop.remove(comp);
		}
	}

	public synchronized void removeComponent(Class<? extends LComponent> clazz) {
		if (desktop != null) {
			desktop.remove(clazz);
		}
	}

	public synchronized void remove(ISprite sprite) {
		if (sprites != null) {
			sprites.remove(sprite);
		}
	}

	public synchronized void removeSprite(Class<? extends ISprite> clazz) {
		if (sprites != null) {
			sprites.remove(clazz);
		}
	}

	public synchronized void removeAll() {
		if (sprites != null) {
			sprites.removeAll();
		}
		if (desktop != null) {
			desktop.getContentPane().clear();
		}
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

	public boolean openBrowser(String url) {
		return LSystem.openBrowser(url);
	}

	public int getRepaintMode() {
		return mode;
	}

	public void setRepaintMode(int mode) {
		this.mode = mode;
	}

	/**
	 * 刷新鼠标及键盘数据
	 */
	public void update(long timer) {
		this.touchDX = touchX - lastTouchX;
		this.touchDY = touchY - lastTouchY;
		this.lastTouchX = touchX;
		this.lastTouchY = touchY;
		this.keyButtonReleased = NO_KEY;
		this.touchButtonReleased = NO_BUTTON;
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

	/**
	 * 鼠标移动
	 */
	public synchronized void mouseMove(int x, int y) {
		LSystem.RO_BOT.mouseMove(x, y);
	}

	public boolean isMouseExists() {
		return this.mouseExists;
	}

	public boolean isTouchClick() {
		return touchButtonPressed == MouseEvent.BUTTON1;
	}

	public boolean isTouchClickUp() {
		return touchButtonReleased == MouseEvent.BUTTON3;
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

	public boolean isMoving() {
		return isDraging;
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

	/**
	 * 设定背景颜色
	 * 
	 * @param color
	 */
	public void setBackground(Color color) {
		int w = getWidth(), h = getHeight();
		BufferedImage image = GraphicsUtils.createIntdexedImage(w, h);
		Graphics2D g = image.createGraphics();
		g.setColor(color);
		g.fillRect(0, 0, w, h);
		g.dispose();
		this.setBackground(image);
	}

	/**
	 * 设定背景图像
	 * 
	 * @param screen
	 */
	public void setBackground(BufferedImage screen) {
		if (screen != null) {
			if (screen.getWidth() != getWidth()
					|| screen.getHeight() != getHeight()) {
				screen = GraphicsUtils.getResize(screen, getWidth(),
						getHeight());
			}
			BufferedImage tmp = currentScreen;
			this.currentScreen = screen;
			if (tmp != null) {
				tmp.flush();
				tmp = null;
			}
			this.setRepaintMode(SCREEN_BITMAP_REPAINT);
		} else {
			this.setRepaintMode(SCREEN_CANVAS_REPAINT);
		}
	}

	/**
	 * 设定背景图像
	 * 
	 * @param screen
	 */
	public void setBackground(Image screen) {
		this.setBackground(GraphicsUtils.getBufferImage(screen));
	}

	/**
	 * 设定背景图像
	 * 
	 * @param fileName
	 */
	public void setBackground(String fileName) {
		this.setBackground(GraphicsUtils.loadBufferedImage(fileName));
	}

	/**
	 * 根据运行时间进行事务刷新
	 */
	public void runTimer(LTimerContext timer) {
		if (isClose) {
			return;
		}
		this.elapsedTime = timer.getTimeSinceLastUpdate();
		if (sprites != null && this.sprites.size() > 0) {
			this.sprites.update(elapsedTime);
		}
		if (desktop != null && this.desktop.size() > 0) {
			this.desktop.update(elapsedTime);
		}
		this.baseInput.update(elapsedTime);
		this.alter(timer);
	}

	/**
	 * 绘图器抽象接口
	 * 
	 * @param g
	 */
	public abstract void draw(LGraphics g);

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

	/**
	 * 创建程序UI
	 */
	public synchronized void createUI(final LGraphics g) {
		if (isClose) {
			return;
		}
		if (isTranslate) {
			g.translate(tx, ty);
		}
		draw(g);
		if (sprites != null && this.sprites.size() > 0) {
			sprites.createUI(g);
		}
		if (desktop != null && this.desktop.size() > 0) {
			desktop.createUI(g);
		}
		if (isTranslate) {
			g.translate(-tx, -ty);
		}
	}

	/**
	 * 保存当前窗体显示图像到指定位置
	 * 
	 * @param fileName
	 */
	public void saveScreenImage(String fileName) {
		GraphicsUtils.saveImage(GraphicsUtils.getBufferImage(getScreenImage()),
				fileName);
	}

	/**
	 * 保存当前窗体显示图像
	 * 
	 */
	public void saveScreenImage() {
		GraphicsUtils.saveImage(GraphicsUtils.getBufferImage(getScreenImage()),
				LSystem.getLScreenFile());
	}

	/**
	 * 返回当前窗体画面图
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public Image getScreenImage() {
		if (handler == null) {
			return null;
		}
		return handler.getDeploy().getView().getAwtImage();
	}

	/**
	 * 返回指定大小的当前窗体画面图
	 * 
	 * @param w
	 * @param h
	 * @return
	 */
	public Image getScreenImage(int w, int h) {
		return GraphicsUtils.getResize(getScreenImage(), w, h);
	}

	/**
	 * 获得Screen的画面边界
	 * 
	 * @return
	 */
	public Rectangle getBounds() {
		if (handler == null) {
			return null;
		}
		Window window = handler.getScene().getWindow();
		Rectangle bounds = window.getBounds();
		Insets insets = window.getInsets();
		return new Rectangle(bounds.x + insets.left, bounds.y + insets.top,
				bounds.width - (insets.left + insets.top), bounds.height
						- (insets.top + insets.bottom));
	}

	/**
	 * 验证是否使用了非框架组建,此情况下不能全屏
	 * 
	 */
	private void checkFullScreen() {
		if (isComponents) {
			throw new RuntimeException(
					"Using the AWT/Swing components can not be changed to full screen !");
		}
	}

	/**
	 * 全屏窗体为指定大小
	 * 
	 * @param d
	 */
	public void updateFullScreen(Dimension d) {
		updateFullScreen((int) d.getWidth(), (int) d.getHeight());
	}

	/**
	 * 全屏窗体为指定大小
	 * 
	 */
	public void updateFullScreen(int w, int h) {
		checkFullScreen();
		if (handler != null) {
			handler.getScene().updateFullScreen(w, h);
		}
	}

	/**
	 * 全屏窗体
	 */
	public void updateFullScreen() {
		checkFullScreen();
		if (handler != null) {
			handler.getScene().updateFullScreen();
		}
	}

	/**
	 * 还原窗体
	 * 
	 */
	public void updateNormalScreen() {
		checkFullScreen();
		if (handler != null) {
			handler.getScene().updateNormalScreen();
		}
	}

	/**
	 * 检查窗体默认对象中是否包含指定精灵
	 * 
	 */
	public boolean contains(ISprite sprite) {
		return sprites.contains(sprite);
	}

	/**
	 * 检查窗体默认对象中是否包含指定组件
	 * 
	 * @param comp
	 * @return
	 */
	public boolean contains(LComponent comp) {
		return desktop.getContentPane().contains(comp);
	}

	/**
	 * 设定指定精灵到图层最前
	 * 
	 * @param sprite
	 */
	public void sendSpriteToFront(ISprite sprite) {
		sprites.sendToFront(sprite);
	}

	/**
	 * 设定指定精灵到图层最后
	 * 
	 * @param sprite
	 */
	public void sendSpriteToBack(ISprite sprite) {
		sprites.sendToBack(sprite);
	}

	/**
	 * 设定是否允许进行下一步
	 * 
	 */
	public void setNext(boolean next) {
		this.isNext = next;
	}

	/**
	 * 返回背景图片
	 */
	public Image getBackground() {
		return currentScreen;
	}

	/**
	 * 对外的线程暂停器
	 * 
	 * @param timeMillis
	 */
	public void pause(long timeMillis) {
		try {
			Thread.sleep(timeMillis);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * 对外的刷新器
	 * 
	 * @param timer
	 */
	public abstract void alter(LTimerContext timer);

	/**
	 * 点击鼠标左键
	 * 
	 * @return
	 */
	public boolean leftClick() {
		return this.baseInput.isTouchPressed(MouseEvent.BUTTON1);
	}

	/**
	 * 点击鼠标中间键(滚轴)
	 * 
	 * @return
	 */
	public boolean middleClick() {
		return this.baseInput.isTouchPressed(MouseEvent.BUTTON2);
	}

	/**
	 * 点击鼠标右键
	 * 
	 * @return
	 */
	public boolean rightClick() {
		return this.baseInput.isTouchPressed(MouseEvent.BUTTON3);
	}

	public void keyTyped(KeyEvent e) {
		e.consume();
	}

	final LKey key = new LKey();

	/**
	 * 键盘按下
	 */
	public void keyPressed(KeyEvent e) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		int code = e.getKeyCode();
		if (e.getModifiers() == InputEvent.ALT_MASK
				&& e.getKeyCode() == KeyEvent.VK_F4) {
			if (handler != null) {
				handler.getScene().close();
			}
		}
		int type = ACTION_DOWN;
		key.keyChar = e.getKeyChar();
		key.keyCode = e.getKeyCode();
		key.type = type;
		try {
			this.onKeyDown(key);
			keyType[type] = true;
			keyButtonPressed = code;
			keyButtonReleased = LInput.NO_KEY;
		} catch (Exception ex) {
			keyButtonPressed = LInput.NO_KEY;
			keyButtonReleased = LInput.NO_KEY;
			ex.printStackTrace();
		}

	}

	public void onKeyDown(LKey e) {

	}

	/**
	 * 设置键盘按下事件
	 * 
	 * @param code
	 */
	public void setKeyDown(int code) {
		try {
			keyButtonPressed = code;
			keyButtonReleased = LInput.NO_KEY;
		} catch (Exception e) {
		}
	}

	/**
	 * 键盘放开
	 */
	public void keyReleased(KeyEvent e) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		int type = ACTION_UP;
		int code = e.getKeyCode();
		key.keyChar = e.getKeyChar();
		key.keyCode = e.getKeyCode();
		key.type = type;
		try {
			this.onKeyUp(key);
			keyType[type] = false;
			keyButtonReleased = code;
			keyButtonPressed = LInput.NO_KEY;
		} catch (Exception ex) {
			keyButtonPressed = LInput.NO_KEY;
			keyButtonReleased = LInput.NO_KEY;
			ex.printStackTrace();
		}
	}

	public void onKeyUp(LKey e) {

	}

	/**
	 * 设置键盘放开事件
	 * 
	 * @param code
	 */
	public void setKeyUp(int code) {
		try {
			keyButtonReleased = code;
			keyButtonPressed = LInput.NO_KEY;
		} catch (Exception e) {
		}
	}

	/**
	 * 鼠标左键按下抽象接口
	 * 
	 * @param e
	 */
	public void leftClick(MouseEvent e) {

	}

	/**
	 * 鼠标中间键按下抽象接口
	 * 
	 * @param e
	 */
	public void middleClick(MouseEvent e) {

	}

	/**
	 * 鼠标右键按下抽象接口
	 * 
	 * @param e
	 */
	public void rightClick(MouseEvent e) {

	}

	final LTouch touch = new LTouch();

	/**
	 * 鼠标按下
	 * 
	 * @param e
	 */
	public abstract void onTouchDown(LTouch e);

	/**
	 * 鼠标按下
	 */
	public void mousePressed(MouseEvent e) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		try {
			if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
				leftClick(e);
			}
			if ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0) {
				middleClick(e);
			}
			if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
				rightClick(e);
			}
			int type = ACTION_DOWN;
			int button = e.getButton();
			touch.action = type;
			touch.type = button;
			touch.pointer = 1;
			touch.x = e.getX() - tx;
			touch.y = e.getY() - ty;
			this.touchX = (int) touch.x;
			this.touchY = (int) touch.y;
			this.isDraging = false;
			try {
				touchType[type] = true;
				touchButtonPressed = button;
				touchButtonReleased = LInput.NO_BUTTON;
				onTouchDown(touch);
			} catch (Exception ex) {
				touchButtonPressed = LInput.NO_BUTTON;
				touchButtonReleased = LInput.NO_BUTTON;
				ex.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * 鼠标放开
	 * 
	 * @param e
	 */
	public abstract void onTouchUp(LTouch e);

	/**
	 * 鼠标放开
	 */
	public void mouseReleased(MouseEvent e) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		int type = ACTION_UP;
		int button = e.getButton();
		touch.action = type;
		touch.type = button;
		touch.pointer = 1;
		touch.x = e.getX() - tx;
		touch.y = e.getY() - ty;
		this.touchX = (int) touch.x;
		this.touchY = (int) touch.y;
		this.isDraging = false;
		try {
			touchType[type] = false;
			touchButtonReleased = button;
			touchButtonPressed = LInput.NO_BUTTON;
			onTouchUp(touch);
		} catch (Exception ex) {
			touchButtonPressed = LInput.NO_BUTTON;
			touchButtonReleased = LInput.NO_BUTTON;
			ex.printStackTrace();
		}
	}

	public abstract void onTouchMove(LTouch e);

	public synchronized void mouseDragged(MouseEvent e) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		int type = ACTION_MOVE;
		int button = e.getButton();
		touch.action = type;
		touch.type = button;
		touch.pointer = 1;
		touch.x = e.getX() - tx;
		touch.y = e.getY() - ty;
		this.touchX = (int) touch.x;
		this.touchY = (int) touch.y;
		onTouchMove(touch);
		this.isDraging = true;
	}

	public synchronized void mouseMoved(MouseEvent e) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		if (!isDraging) {
			int type = ACTION_MOVE;
			int button = e.getButton();
			touch.action = type;
			touch.type = button;
			touch.pointer = 1;
			touch.x = e.getX() - tx;
			touch.y = e.getY() - ty;
			this.touchX = (int) touch.x;
			this.touchY = (int) touch.y;
			onTouchMove(touch);
		}
	}

	public synchronized void mouseClicked(MouseEvent e) {
	}

	public synchronized void mouseEntered(MouseEvent e) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		this.mouseExists = true;
	}

	public synchronized void mouseExited(MouseEvent e) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		this.mouseExists = false;
	}

	public void move(double x, double y) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		this.touchX = (int) x;
		this.touchY = (int) y;
	}

	public void focusGained(FocusEvent e) {
		this.isNext = true;
	}

	public void focusLost(FocusEvent e) {
		this.isNext = false;
		this.refresh();
	}

	public void addComponent(final Component component, final int x,
			final int y, final int w, final int h) {
		if (handler != null) {
			if (handler.getDeploy().addComponent(x, y, w, h, component)) {
				isComponents = true;
			}
		} else {
			Thread componentThread = new Thread(new Runnable() {
				public void run() {
					while (handler == null) {
						Thread.yield();
					}
					if (handler != null) {
						if (handler.getDeploy().addComponent(x, y, w, h,
								component)) {
							isComponents = true;
						}
					}
				}
			});
			componentThread.start();
		}
	}

	public void addComponent(Component component, int x, int y) {
		addComponent(component, x, y, component.getWidth(),
				component.getHeight());
	}

	public void addComponent(Component component) {
		addComponent(component, 0, 0);
	}

	public void removeComponent(Component component) {
		if (handler != null) {
			handler.getDeploy().removeComponent(component);
		}
	}

	public void removeComponent(int index) {
		if (handler != null) {
			this.handler.getDeploy().removeComponent(index);
		}
	}

	/**
	 * 注销占用的资源
	 * 
	 */
	public void destroy() {
		synchronized (this) {
			tx = ty = 0;
			isClose = true;
			callEvents(false);
			isTranslate = false;
			isNext = false;
			isDraging = false;
			isLock = true;
			isComponents = false;
			if (sprites != null) {
				sprites.dispose();
				sprites = null;
			}
			if (desktop != null) {
				desktop.dispose();
				desktop = null;
			}
			if (currentScreen != null) {
				currentScreen.flush();
				currentScreen = null;
			}
			dispose();
		}
	}
}
