/**
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
package loon;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import loon.LSetting.Listener;
import loon.action.ActionControl;
import loon.core.Assets;
import loon.core.LSystem;
import loon.core.event.Updateable;
import loon.core.geom.RectBox;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.LImage;
import loon.core.graphics.Screen;
import loon.core.graphics.opengl.GL;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LSTRFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.graphics.opengl.ScreenUtils;
import loon.core.graphics.opengl.LTexture.Format;
import loon.core.input.LProcess;
import loon.core.timer.LTimerContext;
import loon.core.timer.SystemTimer;
import loon.utils.MathUtils;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

public class LGame extends JavaApp {

	public static LGame register(LSetting setting,
			Class<? extends Screen> clazz, Object... args) {
		LGame game = new LGame(setting.title, setting.width, setting.height);
		game._listener = setting.listener;
		game._AWT_Canvas = setting.javaCanvas;
		game._x = setting.appX;
		game._y = setting.appY;
		game._resizable = setting.resizable;
		game.setShowFPS(setting.showFPS);
		game.setShowMemory(setting.showMemory);
		game.setShowLogo(setting.showLogo);
		game.setFPS(setting.fps);
		if (clazz != null) {
			if (args != null) {
				try {
					final int funs = args.length;
					if (funs == 0) {
						game.setScreen(clazz.newInstance());
						game.showScreen();
					} else {
						Class<?>[] functions = new Class<?>[funs];
						for (int i = 0; i < funs; i++) {
							functions[i] = getType(args[i]);
						}
						java.lang.reflect.Constructor<?> constructor = Class
								.forName(clazz.getName()).getConstructor(
										functions);
						Object o = constructor.newInstance(args);
						if (o != null && (o instanceof Screen)) {
							game.setScreen((Screen) o);
							game.showScreen();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return game;
	}

	private final ExecutorService _exec = Executors.newFixedThreadPool(4);

	private LSTRFont fpsFont;

	private long maxFrames = LSystem.DEFAULT_MAX_FPS, frameRate;

	private OpenGLThread mainLoop;

	private boolean isRunning, isFull, isFPS, isMemory;

	private DisplayMode displayMode;

	private int _lastWidth, _lastHeight, drawPriority;

	private int _x = -1, _y = -1;

	private boolean _resizable, _isAWTCanvas;

	private GLEx gl;

	private GLMode glMode = GLMode.Default;

	private String windowTitle;

	private RectBox bounds = new RectBox();

	private boolean fullscreen = true;

	private LTexture logo;

	private static int updateWidth = 0, updateHeight = 0;

	private static boolean updateScreen = false;

	private Listener _listener;

	public static void updateSize(int w, int h) {
		LGame.updateWidth = w;
		LGame.updateHeight = h;
		LGame.updateScreen = true;
	}

	public LGame() {
		this(null, LSystem.MAX_SCREEN_WIDTH, LSystem.MAX_SCREEN_HEIGHT);
	}

	public LGame(String titleName, int width, int height) {
		if (width < 1 || height < 1) {
			throw new RuntimeException("Width and Height must be positive !");
		}
		if (LSystem.isWindows()) {
			System.setProperty("sun.java2d.translaccel", "true");
			System.setProperty("sun.java2d.ddforcevram", "true");
		} else if (LSystem.isMacOS()) {
			System.setProperty("apple.awt.showGrowBox", "false");
			System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
			System.setProperty("apple.awt.graphics.EnableLazyDrawing", "true");
			System.setProperty(
					"apple.awt.window.position.forceSafeUserPositioning",
					"true");
			System.setProperty("apple.awt.window.position.forceSafeCreation",
					"true");
			System.setProperty("com.apple.hwaccel", "true");
			System.setProperty("com.apple.forcehwaccel", "true");
			System.setProperty("com.apple.macos.smallTabs", "true");
			System.setProperty("com.apple.macos.use-file-dialog-packages",
					"true");
		} else {
			System.setProperty("sun.java2d.opengl", "true");
		}
		if (LSystem.screenRect == null) {
			LSystem.screenRect = new RectBox(0, 0, width, height);
		} else {
			LSystem.screenRect.setBounds(0, 0, width, height);
		}
		this._lastWidth = width;
		this._lastHeight = height;
		LSystem.screenProcess = new LProcess(this, width, height);
		setFPS(LSystem.DEFAULT_MAX_FPS);
		setTitle(titleName);
		setSize(width, height);
	}

	public void setTitle(String titleName) {
		this.windowTitle = titleName;
	}

	public void setGLMode(GLMode renderMode) {
		this.glMode = renderMode;
	}

	public GLEx getGraphics() {
		return gl;
	}

	public void setScreen(Screen screen) {
		LSystem.screenProcess.setScreen(screen);
	}

	public int getHeight() {
		return bounds.height;
	}

	public int getWidth() {
		return bounds.width;
	}

	protected void setBounds(int x, int y, int width, int height) {
		bounds.setBounds(x, y, width, height);
	}

	public RectBox getBounds() {
		return bounds;
	}

	public void setViewPort(int x, int y, int width, int height) {
		setBounds(x, y, width, height);
		gl.setViewPort(x, y, width, height);
	}

	public void setViewPort(RectBox vPort) {
		setViewPort((int) vPort.x, (int) vPort.y, vPort.width, vPort.height);
	}

	public RectBox getViewPort() {
		return gl.getViewPort();
	}

	final private class OpenGLThread extends Thread {

		private long before, lastTimeMicros, currTimeMicros, goalTimeMicros,
				elapsedTimeMicros, remainderMicros, elapsedTime, frameCount,
				frames;

		public OpenGLThread() {
			isRunning = true;
			setName("OpenGLThread");
		}

		/**
		 * 显示游戏logo
		 */
		private void showLogo() {
			int number = 0;
			try {
				long elapsed;
				int cx = 0, cy = 0;
				double delay;
				if (logo == null) {
					logo = LTextures.loadTexture(LSystem.FRAMEWORK_IMG_NAME
							+ "logo.png", Format.BILINEAR);
				}
				cx = (int) (getWidth() * LSystem.scaleWidth) / 2
						- logo.getWidth() / 2;
				cy = (int) (getHeight() * LSystem.scaleHeight) / 2
						- logo.getHeight() / 2;
				float alpha = 0.0f;
				boolean firstTime = true;
				elapsed = innerClock();
				while (alpha < 1.0f) {
					gl.drawClear();
					gl.setAlpha(alpha);
					gl.drawTexture(logo, cx, cy);
					if (firstTime) {
						firstTime = false;
					}
					elapsed = innerClock();
					delay = 0.00065 * elapsed;
					if (delay > 0.22) {
						delay = 0.22 + (delay / 6);
					}
					alpha += delay;
					Display.update();
				}
				while (number < 3000) {
					number += innerClock();
					Display.update();
				}
				alpha = 1.0f;
				while (alpha > 0.0f) {
					gl.drawClear();
					gl.setAlpha(alpha);
					gl.drawTexture(logo, cx, cy);
					elapsed = innerClock();
					delay = 0.00055 * elapsed;
					if (delay > 0.15) {
						delay = 0.15 + ((delay - 0.04) / 2);
					}
					alpha -= delay;
					Display.update();
				}
				gl.setAlpha(1.0f);
				gl.setColor(LColor.white);
			} catch (Throwable e) {
			} finally {
				logo.dispose();
				logo = null;
				gl.setBlendMode(GL.MODE_NORMAL);
				LSystem.isLogo = false;
			}
		}

		private long innerClock() {
			long now = System.currentTimeMillis();
			long ret = now - before;
			before = now;
			return ret;
		}

		/**
		 * 游戏窗体主循环
		 */
		public void run() {
			createScreen();
			if (LSystem.isLogo) {
				showLogo();
			}
			boolean wasActive = Display.isActive();
			final LTimerContext timerContext = new LTimerContext();
			final SystemTimer timer = LSystem.getSystemTimer();
			final LProcess process = LSystem.screenProcess;
			Thread currentThread = Thread.currentThread();
			process.begin();
			{
				process.resize(LSystem.screenRect.width,
						LSystem.screenRect.height);
				for (; isRunning && !Display.isCloseRequested()
						&& mainLoop == currentThread;) {
					Display.processMessages();
					if (wasActive != Display.isActive()) {
						if (wasActive) {
							LSystem.isPaused = true;
							Assets.onPause();
							onPause();
							if (process != null) {
								process.onPause();
							}
							if (_listener != null) {
								_listener.onPause();
							}
							pause(500);
							LSystem.gc(1000, 1);
							lastTimeMicros = timer.getTimeMicros();
							elapsedTime = 0;
							remainderMicros = 0;
							Display.update();
						} else {
							LSystem.isPaused = false;
							Assets.onResume();
							onResume();
							if (process != null) {
								process.onResume();
							}
							if (_listener != null) {
								_listener.onResume();
							}
						}
						wasActive = Display.isActive();
						continue;
					}

					boolean lockedRender = false;
					if (_isAWTCanvas) {
						int width = _AWT_Canvas.getWidth();
						int height = _AWT_Canvas.getHeight();
						if (width != _lastWidth || height != _lastHeight) {
							LSystem.scaleWidth = ((float) width)
									/ LSystem.screenRect.width;
							LSystem.scaleHeight = ((float) height)
									/ LSystem.screenRect.height;
							if (gl != null && !gl.isClose()) {
								gl.setViewPort(0, 0, width, height);
							}
							if (process != null) {
								process.resize(width, height);
							}
							_lastWidth = width;
							_lastHeight = height;
							lockedRender = true;
						}
					} else if (_resizable) {
						int width = Display.getWidth();
						int height = Display.getHeight();
						if (Display.wasResized() || width != _lastWidth
								|| height != _lastHeight) {
							LSystem.scaleWidth = ((float) width)
									/ LSystem.screenRect.width;
							LSystem.scaleHeight = ((float) height)
									/ LSystem.screenRect.height;
							if (gl != null && !gl.isClose()) {
								gl.setViewPort(0, 0, width, height);
							}
							if (process != null) {
								process.resize(width, height);
							}
							_lastWidth = width;
							_lastHeight = height;
							requestRendering();
						}
					}

					_queue.execute();

					synchronized (gl) {

						if (!process.next()) {
							continue;
						}

						process.load();

						process.calls();

						if (!isRunning) {
							break;
						}
						goalTimeMicros = lastTimeMicros + 1000000L / maxFrames;
						currTimeMicros = timer.sleepTimeMicros(goalTimeMicros);
						elapsedTimeMicros = currTimeMicros - lastTimeMicros
								+ remainderMicros;
						elapsedTime = MathUtils.max(0,
								(elapsedTimeMicros / 1000));
						remainderMicros = elapsedTimeMicros - elapsedTime
								* 1000;
						lastTimeMicros = currTimeMicros;
						timerContext.millisSleepTime = remainderMicros;
						timerContext.timeSinceLastUpdate = elapsedTime;

						lockedRender |= shouldRender();

						process.runTimer(timerContext);

						ActionControl.update(elapsedTime);

						if (LSystem.AUTO_REPAINT && lockedRender) {

							int repaintMode = process.getRepaintMode();
							switch (repaintMode) {
							case Screen.SCREEN_BITMAP_REPAINT:
								gl.reset(true);
								if (process.getX() == 0 && process.getY() == 0) {
									gl.drawTexture(process.getBackground(), 0,
											0);
								} else {
									gl.drawTexture(process.getBackground(),
											process.getX(), process.getY());
								}
								break;
							case Screen.SCREEN_COLOR_REPAINT:
								LColor c = process.getColor();
								if (c != null) {
									gl.drawClear(c);
								}
								break;
							case Screen.SCREEN_CANVAS_REPAINT:
								gl.reset(true);
								break;
							case Screen.SCREEN_NOT_REPAINT:
								gl.reset(true);
								break;
							default:
								gl.reset(true);
								if (process.getX() == 0 && process.getY() == 0) {
									gl.drawTexture(
											process.getBackground(),
											repaintMode
													/ 2
													- LSystem.random
															.nextInt(repaintMode),
											repaintMode
													/ 2
													- LSystem.random
															.nextInt(repaintMode));
								} else {
									gl.drawTexture(
											process.getBackground(),
											process.getX()
													+ repaintMode
													/ 2
													- LSystem.random
															.nextInt(repaintMode),
											process.getY()
													+ repaintMode
													/ 2
													- LSystem.random
															.nextInt(repaintMode));
								}
								break;
							}
							gl.resetFont();

							process.draw(gl);

							process.drawable(elapsedTime);

							if (isFPS) {
								tickFrames();
								fpsFont.drawString("FPS:" + frameRate, 5, 5, 0,
										LColor.white);
							}
							if (isMemory) {
								Runtime runtime = Runtime.getRuntime();
								long totalMemory = runtime.totalMemory();
								long currentMemory = totalMemory
										- runtime.freeMemory();
								String memory = ((float) ((currentMemory * 10) >> 20) / 10)
										+ " of "
										+ ((float) ((totalMemory * 10) >> 20) / 10)
										+ " MB";
								fpsFont.drawString("MEMORY:" + memory, 5, 25,
										0, LColor.white);
							}

							process.drawEmulator(gl);

							process.unload();
							// 刷新游戏画面
							Display.update();
						} else {
							Display.sync(60);
						}
					}

					// 此版将F12设定为全屏
					if (Keyboard.isKeyDown(Keyboard.KEY_F12) && !isFull) {
						isFull = true;
						updateFullScreen();
					} else if (!Keyboard.isKeyDown(Keyboard.KEY_F12)) {
						if (updateScreen) {
							updateFullScreen(updateWidth, updateHeight, true);
							updateScreen = false;
						} else {
							isFull = false;
						}
					}
				}
			}

			process.end();
			exit();
		}

		private final void pause(long sleep) {
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException ex) {
			}
		}

		private void tickFrames() {
			long time = System.currentTimeMillis();
			if (time - frameCount > 1000L) {
				frameRate = Math.min(maxFrames, frames);
				frames = 0;
				frameCount = time;
			}
			frames++;
		}
	}

	volatile boolean _isContinuous = true;
	volatile boolean _requestRendering = false;

	public void setContinuousRendering(boolean isContinuous) {
		this._isContinuous = isContinuous;
	}

	public boolean isContinuousRendering() {
		return _isContinuous;
	}

	public void requestRendering() {
		synchronized (this) {
			_requestRendering = true;
		}
	}

	public boolean shouldRender() {
		synchronized (this) {
			boolean rq = _requestRendering;
			_requestRendering = false;
			return rq || _isContinuous || Display.isDirty();
		}
	}

	public void exit() {
		isRunning = false;
		synchronized (this) {
			if (LSystem.screenProcess != null) {
				LSystem.screenProcess.onDestroy();
			}
			if (gl != null) {
				gl.dispose();
			}
			ActionControl.getInstance().stopAll();
			Assets.onDestroy();
			LSystem.destroy();
			LSystem.gc();
			if (displayMode != null) {
				Mouse.destroy();
				Keyboard.destroy();
				Display.destroy();
			}
			notifyAll();
		}
		onExit();
		if (_listener != null) {
			_listener.onExit();
		}
		try {
			_exec.shutdown();
			_exec.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException ie) {
		}
		System.exit(0);
	}

	public void showScreen() {
		if (!isRunning) {
			isRunning = true;
			if (mainLoop == null) {
				this.mainLoop = new OpenGLThread();
				this.drawPriority = Thread.NORM_PRIORITY;
				this.mainLoop.setPriority(drawPriority);
				this.mainLoop.start();
			}
		}
	}

	private void setSize(int w, int h) {
		LSystem.screenRect.setSize(w, h);
	}

	private void createScreen() {
		try {
			DisplayMode[] ds;
			ds = Display.getAvailableDisplayModes();
			for (int i = 0; i < ds.length; i++) {
				if (ds[i].getWidth() == LSystem.screenRect.width
						&& ds[i].getHeight() == LSystem.screenRect.height
						&& ds[i].getBitsPerPixel() == 32) {
					displayMode = ds[i];
					break;
				}
			}
			if (displayMode == null) {
				displayMode = new DisplayMode(LSystem.screenRect.width,
						LSystem.screenRect.height);
			}
			if (_AWT_Canvas != null) {
				Display.setParent(_AWT_Canvas);
				_isAWTCanvas = true;
			} else {
				Display.setDisplayMode(displayMode);
				_isAWTCanvas = false;
			}

			Display.setTitle(windowTitle);
			Display.setResizable(_resizable);
			Display.setInitialBackground(0, 0, 0);
			setIcon(LSystem.FRAMEWORK_IMG_NAME + "icon.png");

			if (_x != -1 && _y != -1) {
				Display.setLocation(_x, _y);
			}
			int samples = 0;
			try {
				Display.create(new PixelFormat(8, 8, 0, samples));
			} catch (Exception e) {
				Display.destroy();
				try {
					Display.create(new PixelFormat(8, 8, 0));
				} catch (Exception ex) {
					Display.destroy();
					try {
						Display.create(new PixelFormat());
					} catch (Exception exc) {
						if (exc.getMessage().contains(
								"Pixel format not accelerated"))
							throw new RuntimeException(
									"not supported by the OpenGL driver.", exc);
					}
				}
			}

			updateScreen();

			boolean support = GLEx.checkVBO();

			if (glMode == GLMode.VBO) {
				if (support) {
					GLEx.setVbo(true);
				} else {
					GLEx.setVbo(false);
					setGLMode(GLMode.Default);
				}
			} else {
				GLEx.setVbo(false);
				setGLMode(GLMode.Default);
			}

			this.gl = new GLEx(LSystem.screenRect.width,
					LSystem.screenRect.height);
			this.setViewPort(getBounds());
			this.gl.update();

		} catch (LWJGLException ex) {
			ex.printStackTrace();
		}
	}

	public void setIcon(String path) {
		setIcon(new LImage(path));
	}

	public void setIcon(LImage icon) {
		Display.setIcon(new java.nio.ByteBuffer[] { (java.nio.ByteBuffer) icon
				.getByteBuffer() });
	}

	public boolean isClosed() {
		if (displayMode != null) {
			return Display.isCloseRequested();
		}
		return true;
	}

	public boolean isActive() {
		if (displayMode != null) {
			return Display.isActive();
		}
		return false;
	}

	public int getScreenWidth() {
		return java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
	}

	public int getScreenHeight() {
		return java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
	}

	public void updateScreen() {
		int w = 0;
		int h = 0;
		if (_AWT_Canvas == null) {
			DisplayMode dm = Display.getDisplayMode();
			w = dm.getWidth();
			h = dm.getHeight();
		} else {
			w = _AWT_Canvas.getWidth();
			h = _AWT_Canvas.getHeight();
		}
		LSystem.scaleWidth = ((float) w) / LSystem.screenRect.width;
		LSystem.scaleHeight = ((float) h) / LSystem.screenRect.height;
		this.setBounds(0, 0, w, h);
	}

	public void updateFullScreen() {
		updateFullScreen(getScreenWidth(), getScreenHeight(), true);
	}

	public void updateFullScreen(int w, int h) {
		updateFullScreen(w, h, false);
	}

	private void updateFullScreen(int w, int h, boolean limit) {
		this.fullscreen = !fullscreen;
		if (!fullscreen) {
			try {
				if (limit) {
					if (Display.isFullscreen()) {
						return;
					}
					java.awt.DisplayMode useDisplayMode = ScreenUtils
							.searchFullScreenModeDisplay(w, h);
					if (useDisplayMode == null) {
						return;
					}
				}
				DisplayMode d = new DisplayMode(w, h);
				if (gl != null && !gl.isClose()) {
					gl.setViewPort(0, 0, w, h);
				}
				Display.setDisplayModeAndFullscreen(d);
			} catch (Exception e) {
			}
		} else {
			try {
				if (gl != null && !gl.isClose()) {
					gl.setViewPort(0, 0, displayMode.getWidth(),
							displayMode.getHeight());
				}
				Display.setDisplayMode(displayMode);
			} catch (Exception e) {
			}
		}
		updateScreen();
	}

	public LTexture getLogo() {
		return logo;
	}

	public void setLogo(LTexture img) {
		logo = img;
	}

	public void setLogo(String path) {
		setLogo(LTextures.loadTexture(path));
	}

	public void setShowLogo(boolean showLogo) {
		LSystem.isLogo = showLogo;
	}

	@Override
	public void invokeAsync(final Updateable act) {
		Runnable run = new Runnable() {
			@Override
			public void run() {
				act.action();
			}
		};
		_exec.execute(run);
	}

	private final String pFontString = " MEORYFPSB0123456789:.of";

	public void setShowFPS(boolean showFps) {
		this.isFPS = showFps;
		if (showFps && fpsFont == null) {
			this.fpsFont = new LSTRFont(LFont.getDefaultFont(), pFontString);
		}
	}

	public void setShowMemory(boolean showMemory) {
		this.isMemory = showMemory;
		if (showMemory && fpsFont == null) {
			this.fpsFont = new LSTRFont(LFont.getDefaultFont(), pFontString);
		}
	}

	public int getAppX() {
		return _x;
	}

	public void seAppX(int x) {
		this._x = x;
	}

	public int getAppY() {
		return _y;
	}

	public void setAppY(int y) {
		this._y = y;
	}

	public boolean isResizable() {
		return _resizable;
	}

	public void setResizable(boolean r) {
		this._resizable = r;
	}

	public void setFPS(long frames) {
		this.maxFrames = frames;
	}

	public long getMaxFPS() {
		return this.maxFrames;
	}

	public long getCurrentFPS() {
		return this.frameRate;
	}

	public float getScalex() {
		return LSystem.scaleWidth;
	}

	public float getScaley() {
		return LSystem.scaleHeight;
	}

	@Override
	public void onPause() {

	}

	@Override
	public void onResume() {

	}

	@Override
	public void onExit() {

	}

}
