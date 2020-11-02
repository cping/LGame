package org.loon.framework.android.game;

import org.loon.framework.android.game.action.ActionControl;
import org.loon.framework.android.game.core.EmulatorButtons;
import org.loon.framework.android.game.core.EmulatorListener;
import org.loon.framework.android.game.core.LHandler;
import org.loon.framework.android.game.core.LSystem;
import org.loon.framework.android.game.core.graphics.LColor;
import org.loon.framework.android.game.core.graphics.LFont;
import org.loon.framework.android.game.core.graphics.LImage;
import org.loon.framework.android.game.core.graphics.Screen;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.core.timer.LTimerContext;
import org.loon.framework.android.game.core.timer.SystemTimer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 
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
public class LGameAndroid2DView extends SurfaceView implements
		SurfaceHolder.Callback {

	final static private DrawFilter zoomFilter = new PaintFlagsDrawFilter(Paint.DITHER_FLAG,
            Paint.FILTER_BITMAP_FLAG);

	final static private Matrix tmp_matrix = new Matrix();

	final static private LFont fpsFont = LFont
			.getFont(LSystem.FONT_NAME, 0, 20);

	private boolean isFPS, isMemory, isRunning;

	private int width, height, drawPriority;

	private long maxFrames, curFPS;

	private Bitmap currentScreen;

	private SurfaceHolder surfaceHolder;

	private CanvasThread mainLoop;

	private volatile LImage logo;

	private LGraphics gl;

	private LHandler handler;

	private LGameAndroid2DActivity activity;

	private Canvas canvas;

	private Paint resizePaint;

	public LImage canvasImage;

	private int repaintMode;

	private EmulatorListener emulatorListener;

	private EmulatorButtons emulatorButtons;

	public LGameAndroid2DView(LGameAndroid2DActivity activity,
			boolean isLandscape, LMode mode) {
		super(activity.getApplicationContext());
		try {
			LSystem.setupHandler(activity, this, isLandscape, mode);
			this.handler = LSystem.getSystemHandler();
			this.handler.initScreen();
			this.activity = handler.getLGameActivity();
			this.setFPS(LSystem.DEFAULT_MAX_FPS);
			this.createScreen();
		} catch (Exception e) {
		}
	}

	public void setPaused(boolean paused) {
		LSystem.isPaused = paused;
	}

	public boolean isPaused() {
		return LSystem.isPaused;
	}

	/**
	 * 创建游戏窗体载体
	 */
	private void createScreen() {
		this.canvasImage = new LImage(width = handler.getWidth(),
				height = handler.getHeight(), false);
		this.gl = canvasImage.getLGraphics();
		this.currentScreen = canvasImage.getBitmap();
		if (LSystem.isLowerVer()) {
			this.surfaceHolder = getHolder();
			this.surfaceHolder.addCallback(this);
		} else {
			this.surfaceHolder = getHolder();
			int mode = 0;
			try {
				mode = 1;
				surfaceHolder
						.setType(android.view.SurfaceHolder.SURFACE_TYPE_HARDWARE);
			} catch (Exception e) {
				try {
					mode = 2;
					surfaceHolder
							.setType(android.view.SurfaceHolder.SURFACE_TYPE_GPU);
				} catch (Exception e2) {
					surfaceHolder
							.setType(android.view.SurfaceHolder.SURFACE_TYPE_NORMAL);
				}
			}
			switch (mode) {
			case 1:
				Log.i("Android2DView", "Hardware surface");
				break;
			case 2:
				Log.i("Android2DView", "GPU surface");
				break;
			default:
				Log.i("Android2DView", "No hardware acceleration available");
			}
			this.surfaceHolder.addCallback(this);
			this.surfaceHolder.setFormat(PixelFormat.RGB_565);
		}
		this.setClickable(false);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.setKeepScreenOn(true);
		this.setLongClickable(false);
		this.destroyDrawingCache();
		this.setDrawingCacheBackgroundColor(0);
		this.setDrawingCacheEnabled(false);
		if (LSystem.isHTC()) {
			this.setWillNotCacheDrawing(false);
			this.setWillNotDraw(false);
		} else {
			this.setWillNotCacheDrawing(true);
			this.setWillNotDraw(true);
		}
		this.requestFocus();
		this.requestFocusFromTouch();
	}

	/**
	 * 返回当前正在使用的游戏画布
	 * 
	 * @return
	 */
	public LGraphics getLGraphics() {
		return gl;
	}

	/**
	 * 设定模拟按钮监听器
	 * 
	 * @param emulatorListener
	 */
	public void setEmulatorListener(EmulatorListener emulator) {
		this.emulatorListener = emulator;
		if (emulatorListener != null) {
			if (emulatorButtons == null) {
				emulatorButtons = new EmulatorButtons(emulatorListener, width,
						height);
			} else {
				emulatorButtons.setEmulatorListener(emulator);
			}
		} else {
			emulatorButtons = null;
		}
	}

	/**
	 * 获得模拟器监听
	 * 
	 * @return
	 */
	public EmulatorListener getEmulatorListener() {
		return emulatorListener;
	}

	/**
	 * 获得模拟器按钮
	 * 
	 * @return
	 */
	public EmulatorButtons getEmulatorButtons() {
		return emulatorButtons;
	}

	public void setScreen(Screen screen) {
		this.handler.setScreen(screen);
	}

	public final Bitmap getImage() {
		if (handler != null && !LSystem.AUTO_REPAINT
				&& handler.getRepaintMode() == Screen.SCREEN_NOT_REPAINT) {
			return currentScreen;
		} else {
			if (mainLoop != null) {
				mainLoop.updateCapture(currentScreen);
			}
			return currentScreen;
		}
	}

	public final LImage getAndroid2DImage() {
		return new LImage(getImage());
	}

	public void destroyView() {
		try {
			synchronized (this) {
				handler.getAssetsSound().stopSoundAll();
				handler.getPlaySound().stopSoundAll();
				handler.destroy();
				ActionControl.getInstance().stopAll();
				LSystem.destroy();
				releaseResources();
				notifyAll();
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 更新游戏画布
	 * 
	 */
	public void update() {
		try {
			if (!isRunning) {
				return;
			}
			canvas = surfaceHolder.lockCanvas(null);
			if (canvas != null) {
					synchronized (surfaceHolder) {
						gl.update(canvas);
					}
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 刷新图像到指定位置
	 * 
	 * @param bit
	 * @param x
	 * @param y
	 */
	public void updateLocation(Bitmap bit, int x, int y) {
		try {
			if (!isRunning) {
				return;
			}
			if (bit == null) {
				return;
			}
			canvas = surfaceHolder.lockCanvas(null);
			if (canvas != null) {
				synchronized (surfaceHolder) {
					canvas.drawBitmap(bit, x, y, null);
					if (emulatorButtons != null) {
						emulatorButtons.draw(canvas);
					}
				}
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 刷新图像到指定位置
	 * 
	 * @param img
	 * @param x
	 * @param y
	 */
	public void updateLocation(LImage img, int x, int y) {
		if (img == null) {
			return;
		}
		updateLocation(img.getBitmap(), x, y);
	}

	/**
	 * 更新游戏画布
	 * 
	 * @param img
	 */
	public void update(LImage img) {
		if (img == null) {
			return;
		}
		update(img.getBitmap());
	}

	/**
	 * 更新游戏画布
	 * 
	 * @param img
	 */
	public void update(Bitmap bit) {
		try {
			if (!isRunning) {
				return;
			}
			if (bit == null) {
				return;
			}
			canvas = surfaceHolder.lockCanvas(null);
			if (canvas != null) {
				synchronized (surfaceHolder) {
					canvas.drawBitmap(bit, 0, 0, null);
					if (emulatorButtons != null) {
						emulatorButtons.draw(canvas);
					}
				}
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 更新游戏画布
	 * 
	 * @param img
	 * @param w
	 * @param h
	 */
	public void update(LImage img, int w, int h) {
		if (img == null) {
			return;
		}
		update(img.getBitmap(), w, h);
	}

	/**
	 * 更新游戏画布
	 * 
	 * @param bit
	 * @param w
	 * @param h
	 */
	public void update(Bitmap bit, int w, int h) {
		if (!isRunning) {
			return;
		}
		if (bit == null) {
			return;
		}
		canvas = surfaceHolder.lockCanvas(null);
		if (canvas != null) {
			synchronized (surfaceHolder) {
				canvas.drawBitmap(bit, width / 2 - w / 2, height / 2 - h / 2,
						null);
				if (emulatorButtons != null) {
					emulatorButtons.draw(canvas);
				}
			}
			surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}

	/**
	 * 更新游戏画布，成比例调整显示位置（画面变更为指定大小）
	 * 
	 * @param img
	 * @param w
	 * @param h
	 */
	public void updateFull(LImage img, int w, int h) {
		if (img == null) {
			return;
		}
		updateFull(img.getBitmap(), w, h);
	}

	/**
	 * 更新游戏画布，成比例调整显示位置（画面变更为指定大小）
	 * 
	 * @param img
	 * @param w
	 * @param h
	 */
	public void updateFull(Bitmap bit, int w, int h) {
		if (!isRunning) {
			return;
		}
		if (bit == null) {
			return;
		}
		canvas = surfaceHolder.lockCanvas(null);
		if (canvas != null) {
			int nw = bit.getWidth();
			int nh = bit.getHeight();
			if (nw == w && nh == h) {
				synchronized (surfaceHolder) {
					canvas.drawBitmap(bit, width / 2 - w / 2, height / 2 - h
							/ 2, null);
					if (emulatorButtons != null) {
						emulatorButtons.draw(canvas);
					}
				}
				surfaceHolder.unlockCanvasAndPost(canvas);
				return;
			}
			if (LSystem.isOverrunOS21()) {
				float scaleWidth = ((float) w) / nw;
				float scaleHeight = ((float) h) / nh;
				tmp_matrix.reset();
				tmp_matrix.postScale(scaleWidth, scaleHeight);
				tmp_matrix.postTranslate(width / 2 - w / 2, height / 2 - h / 2);
				synchronized (surfaceHolder) {
					canvas.drawBitmap(bit, tmp_matrix, null);
					if (emulatorButtons != null) {
						emulatorButtons.draw(canvas);
					}
				}
				surfaceHolder.unlockCanvasAndPost(canvas);
			} else {
				int x = width / 2 - w / 2;
				int y = height / 2 - h / 2;
				Rect srcR = new Rect(0, 0, w, h);
				Rect dstR = new Rect(x, y, x + w, y + h);
				synchronized (surfaceHolder) {
					canvas.drawBitmap(bit, srcR, dstR, null);
					if (emulatorButtons != null) {
						emulatorButtons.draw(canvas);
					}
				}
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}

	/**
	 * 更有游戏画布为指定大小
	 * 
	 * @param img
	 * @param w
	 * @param h
	 */
	public void updateResize(LImage img, int w, int h) {
		if (img == null) {
			return;
		}
		updateResize(img.getBitmap(), w, h);
	}

	/**
	 * 更有游戏画布为指定大小
	 * 
	 * @param bit
	 * @param w
	 * @param h
	 */
	public void updateResize(Bitmap bit, int w, int h) {
		if (!isRunning) {
			return;
		}
		if (bit == null) {
			return;
		}
		canvas = surfaceHolder.lockCanvas(null);
		if (canvas != null) {
			int nw = bit.getWidth();
			int nh = bit.getHeight();
			float scaleWidth = ((float) w) / nw;
			float scaleHeight = ((float) h) / nh;
			tmp_matrix.reset();
			tmp_matrix.postScale(scaleWidth, scaleHeight);
			tmp_matrix.postTranslate(width / 2 - w / 2, height / 2 - h / 2);
			synchronized (surfaceHolder) {
				if (resizePaint == null) {
					resizePaint = new Paint();
				}
				resizePaint.setFilterBitmap(true);
				canvas.drawBitmap(bit, tmp_matrix, resizePaint);
				resizePaint.setFilterBitmap(false);
				if (emulatorButtons != null) {
					emulatorButtons.draw(canvas);
				}
			}
			surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}

	final private class CanvasThread extends Thread {

		final LTimerContext timerContext = new LTimerContext();

		final SystemTimer timer = LSystem.getSystemTimer();

		private long lastTimeMicros, currTimeMicros, goalTimeMicros,
				elapsedTimeMicros, remainderMicros, elapsedTime, frameCount,
				frames;

		public CanvasThread() {
			isRunning = true;
			setName("CanvasThread");
		}

		synchronized void updateCapture(final Bitmap bit) {
			if (!handler.next()) {
				return;
			}
			handler.calls();

			handler.runTimer(timerContext);

			if (LSystem.AUTO_REPAINT) {
				canvas = surfaceHolder.lockCanvas(null);
				if (canvas == null) {
					return;
				}
				synchronized (surfaceHolder) {
					gl.update(bit);
					repaintMode = handler.getRepaintMode();
					switch (repaintMode) {
					case Screen.SCREEN_BITMAP_REPAINT:
						gl.drawBitmap(handler.getBackground(), 0, 0);
						break;
					case Screen.SCREEN_CANVAS_REPAINT:
						gl.drawClear();
						break;
					case Screen.SCREEN_NOT_REPAINT:
						break;
					default:
						gl.drawBitmap(handler.getBackground(), repaintMode / 2
								- LSystem.random.nextInt(repaintMode),
								repaintMode / 2
										- LSystem.random.nextInt(repaintMode));
						break;
					}

					handler.draw(gl);

					if (isFPS) {
						tickFrames();
						gl.setFont(fpsFont);
						gl.setColor(LColor.white);
						gl.drawString("FPS:" + curFPS, 5, 20);
					}

					if (isMemory) {
						Runtime runtime = Runtime.getRuntime();
						long totalMemory = runtime.totalMemory();
						long currentMemory = totalMemory - runtime.freeMemory();

						String memory = ((float) ((currentMemory * 10) >> 20) / 10)
								+ " of "
								+ ((float) ((totalMemory * 10) >> 20) / 10)
								+ " MB";

						gl.setFont(fpsFont);
						gl.setColor(LColor.white);
						gl.drawString("MEMORY:" + memory, 5, 45);
					}
					if (emulatorButtons != null) {
						emulatorButtons.draw(gl);
					}
				}
				surfaceHolder.unlockCanvasAndPost(canvas);
			}

		}


		/**
		 * 游戏窗体主循环
		 */
		public void run() {
	
			boolean isScale = handler.isScale();

			Thread currentThread = Thread.currentThread();
			int restoreCount = 0;
			try {
				do {
					if (LSystem.isPaused || !isFocusable()) {
						pause(500);
						lastTimeMicros = timer.getTimeMicros();
						elapsedTime = 0;
						remainderMicros = 0;
						continue;
					}
					if (!handler.next()) {
						continue;
					}
					handler.calls();

					goalTimeMicros = lastTimeMicros + 1000000L / maxFrames;
					currTimeMicros = timer.sleepTimeMicros(goalTimeMicros);
					elapsedTimeMicros = currTimeMicros - lastTimeMicros
							+ remainderMicros;
					elapsedTime = Math.max(0, (int) (elapsedTimeMicros / 1000));
					remainderMicros = elapsedTimeMicros - elapsedTime * 1000;
					lastTimeMicros = currTimeMicros;
					timerContext.millisSleepTime = remainderMicros;
					timerContext.timeSinceLastUpdate = elapsedTime;

					handler.runTimer(timerContext);

					if (LSystem.AUTO_REPAINT) {
						canvas = surfaceHolder.lockCanvas(null);
						if (canvas == null) {
							continue;
						}
						synchronized (surfaceHolder) {
							if (isScale) {
								
								canvas.setDrawFilter(zoomFilter);
								canvas.scale(LSystem.scaleWidth,
										LSystem.scaleHeight);
								restoreCount = canvas.save();
							}
							gl.update(canvas);

							repaintMode = handler.getRepaintMode();
							switch (repaintMode) {
							case Screen.SCREEN_BITMAP_REPAINT:
								if (handler.getX() == 0 && handler.getY() == 0) {
									gl
											.drawBitmap(
													handler.getBackground(), 0,
													0);
								} else {
									gl.drawClear();
									gl.drawBitmap(handler.getBackground(),
											handler.getX(), handler.getY());
								}
								break;
							case Screen.SCREEN_CANVAS_REPAINT:
								gl.drawClear();
								break;
							case Screen.SCREEN_NOT_REPAINT:
								break;
							default:
								if (handler.getX() == 0 && handler.getY() == 0) {
									gl
											.drawBitmap(
													handler.getBackground(),
													repaintMode
															/ 2
															- LSystem.random
																	.nextInt(repaintMode),
													repaintMode
															/ 2
															- LSystem.random
																	.nextInt(repaintMode));
								} else {
									gl.drawClear();
									gl.drawBitmap(
													handler.getBackground(),
													handler.getX()
															+ repaintMode
															/ 2
															- LSystem.random
																	.nextInt(repaintMode),
													handler.getY()
															+ repaintMode
															/ 2
															- LSystem.random
																	.nextInt(repaintMode));
								}
								break;
							}

							handler.draw(gl);

							if (isFPS) {
								tickFrames();
								gl.setFont(fpsFont);
								gl.setColor(LColor.white);
								gl.drawString("FPS:" + curFPS, 5, 20);
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

								gl.setFont(fpsFont);
								gl.setColor(LColor.white);
								gl.drawString("MEMORY:" + memory, 5, 45);
							}
							if (emulatorButtons != null) {
								emulatorButtons.draw(gl);
							}
							if (isScale) {
								canvas.restoreToCount(restoreCount);
							}
						}
						surfaceHolder.unlockCanvasAndPost(canvas);
					}

				} while (isRunning && mainLoop == currentThread);
			} catch (Exception ex) {
				Log.d("Android2DView", "LGame 2D View Error :", ex);
			} finally {
				destroyView();
			}

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
				curFPS = Math.min(maxFrames, frames);
				frames = 0;
				frameCount = time;
			}
			frames++;
		}
	}

	public void setLogo(LImage img) {
		logo = img;
	}

	public LImage getLogo() {
		return logo;
	}

	public Thread getMainLoop() {
		return mainLoop;
	}

	public void setFPS(long frames) {
		this.maxFrames = frames;
	}

	public long getMaxFPS() {
		return this.maxFrames;
	}

	public long getCurrentFPS() {
		return this.curFPS;
	}

	public boolean isShowFPS() {
		return isFPS;
	}

	public void setShowFPS(boolean isFPS) {
		this.isFPS = isFPS;
	}

	public boolean isShowMemory() {
		return isMemory;
	}

	public void setShowMemory(boolean isMemory) {
		this.isMemory = isMemory;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			if (!isRunning) {
				this.setRunning(true);
				if (mainLoop == null) {
					this.mainLoop = new CanvasThread();
					this.drawPriority = Thread.NORM_PRIORITY;
					this.mainLoop.setPriority(drawPriority);
					this.mainLoop.start();
				}
			}
		} catch (Exception e) {

		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			if (!LSystem.isPaused) {
				if (mainLoop != null) {
					this.setRunning(false);
					this.mainLoop = null;
				}
			}
		} catch (Exception e) {

		}
	}

	private void stopThread() {
		try {
			if (mainLoop != null) {
				boolean result = true;
				setRunning(false);
				while (result) {
					try {
						mainLoop.join();
						result = false;
					} catch (InterruptedException e) {
					}
				}

			}
		} catch (Exception e) {

		}
	}

	private void releaseResources() {
		try {
			if (surfaceHolder != null) {
				surfaceHolder.removeCallback(this);
				surfaceHolder = null;
			}
			stopThread();
		} catch (Exception e) {

		}
	}

	public int getDrawPriority() {
		return drawPriority;
	}

	public void setDrawPriority(int drawPriority) {
		if (drawPriority >= 0 && drawPriority <= 10) {
			this.drawPriority = drawPriority;
			if (mainLoop != null) {
				try {
					mainLoop.setPriority(drawPriority);
				} catch (Exception e) {
				}
			}
		}
	}

	public boolean onTouchEvent(MotionEvent e) {
		if (emulatorButtons != null) {
			emulatorButtons.onEmulatorButtonEvent(e);
		}
		activity.onTouchEvent(e);
		try {
			Thread.sleep(16);
		} catch (Exception ex) {
		}
		return true;
	}

	public LHandler getGameHandler() {
		return handler;
	}

}
