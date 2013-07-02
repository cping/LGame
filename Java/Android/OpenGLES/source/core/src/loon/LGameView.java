package loon;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import loon.LGame.LMode;
import loon.action.ActionControl;
import loon.core.Assets;
import loon.core.CallQueue;
import loon.core.LSystem;
import loon.core.event.Updateable;
import loon.core.geom.RectBox;
import loon.core.graphics.GraphicsUtils;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.Screen;
import loon.core.graphics.opengl.GL;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LSTRFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.graphics.opengl.LTexture.Format;
import loon.core.input.LProcess;
import loon.core.timer.LTimerContext;
import loon.core.timer.SystemTimer;
import loon.utils.MathUtils;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

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
 * @project loon
 * @author cping
 * @email javachenpeng@yahoo.com
 * @version 0.1.1
 */
public final class LGameView extends CallQueue implements Renderer {

	private GLMode glMode = GLMode.Default;

	public static enum GLMode {

		Default, VBO;

		private String text;

		private GLMode() {
			text = "GLMode : " + name();
		}

		@Override
		public String toString() {
			return text;
		};
	}

	private long lastTimeMicros, currTimeMicros, goalTimeMicros,
			elapsedTimeMicros, remainderMicros, elapsedTime, frameCount,
			frames;

	private long maxFrames = LSystem.DEFAULT_MAX_FPS, frameRate;

	private final Object synch = new Object();

	private final LTimerContext timerContext = new LTimerContext();

	private LGameTools.Logo logoFlag;

	private SystemTimer timer;

	private LSTRFont fpsFont;

	private boolean isFPS, isMemory;

	private boolean onRunning, onPause, onDestroy, onResume;

	private GLEx gl;

	private int maxWidth, maxHeight;

	private Context context;

	private SurfaceView surfaceView;

	private boolean supportVBO;

	private int width, height;

	private LProcess process;

	public LGameView(LGame activity, LMode mode, boolean landscape,
			boolean fullScreen) {
		this.setFPS(LSystem.DEFAULT_MAX_FPS);
		this.initScreen(activity, mode, landscape, fullScreen);
		this.surfaceView = createGLSurfaceView(activity);
		this.process = LSystem.screenProcess;
	}

	private void initScreen(LGame activity, LMode mode, boolean fullScreen,
			boolean landscape) {
		LSystem.screenActivity = activity;
		LSystem.global_queue = this;
		this.context = activity.getApplicationContext();
		this.setFullScreen(fullScreen);
		this.setLandscape(landscape, mode);
		LSystem.screenActivity.checkConfigChanges(context);
	}

	public boolean isScale() {
		return LSystem.scaleWidth != 1 || LSystem.scaleHeight != 1;
	}

	protected void setLandscape(boolean landscape, LMode mode) {
		try {
			if (landscape) {
				if (LSystem.screenActivity.getRequestedOrientation() != android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
					LSystem.screenActivity
							.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				}
			} else {
				if (LSystem.screenActivity.getRequestedOrientation() != android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
					LSystem.screenActivity
							.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}
			}

			RectBox d = LSystem.screenActivity.getScreenDimension();

			LSystem.SCREEN_LANDSCAPE = landscape;

			this.maxWidth = (int) d.getWidth();
			this.maxHeight = (int) d.getHeight();

			if (landscape && (d.getWidth() > d.getHeight())) {
				maxWidth = (int) d.getWidth();
				maxHeight = (int) d.getHeight();
			} else if (landscape && (d.getWidth() < d.getHeight())) {
				maxHeight = (int) d.getWidth();
				maxWidth = (int) d.getHeight();
			} else if (!landscape && (d.getWidth() < d.getHeight())) {
				maxWidth = (int) d.getWidth();
				maxHeight = (int) d.getHeight();
			} else if (!landscape && (d.getWidth() > d.getHeight())) {
				maxHeight = (int) d.getWidth();
				maxWidth = (int) d.getHeight();
			}

			if (mode != LMode.Max) {
				if (landscape) {
					this.width = LSystem.MAX_SCREEN_WIDTH;
					this.height = LSystem.MAX_SCREEN_HEIGHT;
				} else {
					this.width = LSystem.MAX_SCREEN_HEIGHT;
					this.height = LSystem.MAX_SCREEN_WIDTH;
				}
			} else {
				if (landscape) {
					this.width = maxWidth >= LSystem.MAX_SCREEN_WIDTH ? LSystem.MAX_SCREEN_WIDTH
							: maxWidth;
					this.height = maxHeight >= LSystem.MAX_SCREEN_HEIGHT ? LSystem.MAX_SCREEN_HEIGHT
							: maxHeight;
				} else {
					this.width = maxWidth >= LSystem.MAX_SCREEN_HEIGHT ? LSystem.MAX_SCREEN_HEIGHT
							: maxWidth;
					this.height = maxHeight >= LSystem.MAX_SCREEN_WIDTH ? LSystem.MAX_SCREEN_WIDTH
							: maxHeight;
				}
			}

			if (mode == LMode.Fill) {

				LSystem.scaleWidth = ((float) maxWidth) / width;
				LSystem.scaleHeight = ((float) maxHeight) / height;

			} else if (mode == LMode.FitFill) {

				RectBox res = GraphicsUtils.fitLimitSize(width, height,
						maxWidth, maxHeight);
				maxWidth = res.width;
				maxHeight = res.height;
				LSystem.scaleWidth = ((float) maxWidth) / width;
				LSystem.scaleHeight = ((float) maxHeight) / height;

			} else if (mode == LMode.Ratio) {

				maxWidth = View.MeasureSpec.getSize(maxWidth);
				maxHeight = View.MeasureSpec.getSize(maxHeight);

				float userAspect = (float) width / (float) height;
				float realAspect = (float) maxWidth / (float) maxHeight;

				if (realAspect < userAspect) {
					maxHeight = Math.round(maxWidth / userAspect);
				} else {
					maxWidth = Math.round(maxHeight * userAspect);
				}

				LSystem.scaleWidth = ((float) maxWidth) / width;
				LSystem.scaleHeight = ((float) maxHeight) / height;

			} else if (mode == LMode.MaxRatio) {

				maxWidth = View.MeasureSpec.getSize(maxWidth);
				maxHeight = View.MeasureSpec.getSize(maxHeight);

				float userAspect = (float) width / (float) height;
				float realAspect = (float) maxWidth / (float) maxHeight;

				if ((realAspect < 1 && userAspect > 1)
						|| (realAspect > 1 && userAspect < 1)) {
					userAspect = (float) height / (float) width;
				}

				if (realAspect < userAspect) {
					maxHeight = Math.round(maxWidth / userAspect);
				} else {
					maxWidth = Math.round(maxHeight * userAspect);
				}

				LSystem.scaleWidth = ((float) maxWidth) / width;
				LSystem.scaleHeight = ((float) maxHeight) / height;

			} else {

				LSystem.scaleWidth = 1;
				LSystem.scaleHeight = 1;

			}

			if (LSystem.screenRect == null) {
				LSystem.screenRect = new RectBox(0, 0, width, height);
			} else {
				LSystem.screenRect.setBounds(0, 0, width, height);
			}

			StringBuffer sbr = new StringBuffer();
			sbr.append("Mode:").append(mode);
			sbr.append("\nWidth:").append(width).append(",Height:" + height);
			sbr.append("\nMaxWidth:").append(maxWidth)
					.append(",MaxHeight:" + maxHeight);
			sbr.append("\nScale:").append(isScale());
			Log.i("Android2DSize", sbr.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public Context getContext() {
		return context;
	}

	private SurfaceView createGLSurfaceView(LGame activity) {
		android.opengl.GLSurfaceView.EGLConfigChooser configChooser = getEglConfigChooser();
		GLSurfaceViewCupcake view = new GLSurfaceViewCupcake(activity);
		if (configChooser != null) {
			view.setEGLConfigChooser(configChooser);
		} else {
			view.setEGLConfigChooser(5, 6, 5, 0, 16, 0);
		}
		view.setRenderer(this);
		surfaceView = view;
		try {
			surfaceView.setFocusable(true);
			surfaceView.setFocusableInTouchMode(true);
		} catch (Exception ex) {

		} finally {
			LSystem.screenProcess = new LProcess(surfaceView, width, height);
		}
		return surfaceView;
	}

	private android.opengl.GLSurfaceView.EGLConfigChooser getEglConfigChooser() {
		if (LSystem.isSamsung7500()) {
			return new android.opengl.GLSurfaceView.EGLConfigChooser() {
				@Override
				public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
					int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 16,
							EGL10.EGL_NONE };
					EGLConfig[] configs = new EGLConfig[1];
					int[] result = new int[1];
					egl.eglChooseConfig(display, attributes, configs, 1, result);
					return configs[0];
				}
			};
		} else {
			return new LGameEglConfigChooser(5, 6, 5, 0, 16, 0, 0);
		}
	}

	final void resume() {
		synchronized (synch) {
			LSystem.isRunning = true;
			LSystem.isResume = true;
			timer = LSystem.getSystemTimer();
			LTextures.reload();
			Assets.onResume();
		}
	}

	final void pause() {
		synchronized (synch) {
			if (!LSystem.isRunning) {
				return;
			}
			LSystem.isRunning = false;
			LSystem.isPaused = true;
			Assets.onPause();
			while (LSystem.isPaused) {
				try {
					synch.wait();
				} catch (InterruptedException ignored) {
				}
			}
		}
	}

	final void destroy() {
		synchronized (synch) {
			LSystem.isRunning = false;
			LSystem.isDestroy = true;
			if (LSystem.screenProcess != null) {
				LSystem.screenProcess.onDestroy();
				ActionControl.getInstance().stopAll();
				Assets.onDestroy();
				LSystem.destroy();
				LSystem.gc();
			}
			while (LSystem.isDestroy) {
				try {
					synch.wait();
				} catch (InterruptedException ex) {
				}
			}
		}
	}

	@Override
	public void onDrawFrame(javax.microedition.khronos.opengles.GL10 gl10) {

		this.onRunning = false;
		this.onPause = false;
		this.onDestroy = false;
		this.onResume = false;

		synchronized (synch) {
			onRunning = LSystem.isRunning;
			onPause = LSystem.isPaused;
			onDestroy = LSystem.isDestroy;
			onResume = LSystem.isResume;

			if (LSystem.isResume) {
				LSystem.isResume = false;
			}

			if (LSystem.isPaused) {
				LSystem.isPaused = false;
				synch.notifyAll();
			}

			if (LSystem.isDestroy) {
				LSystem.isDestroy = false;
				synch.notifyAll();
			}
		}

		if (onResume) {
			Log.i("Android2DView", "onResume");
			timer = LSystem.getSystemTimer();
			lastTimeMicros = timer.getTimeMicros();
			elapsedTime = 0;
			remainderMicros = 0;
			process.onResume();
		}

		_queue.execute();
		
		if (onRunning) {

			if (LSystem.isLogo) {
				synchronized (synch) {
					if (logoFlag == null) {
						LSystem.isLogo = false;
						return;
					}
					logoFlag.draw(gl);
					if (logoFlag.finish) {
						gl.setAlpha(1.0f);
						gl.setBlendMode(GL.MODE_NORMAL);
						gl.drawClear();
						LSystem.isLogo = false;
						logoFlag.dispose();
						logoFlag = null;
						return;
					}
				}
				return;
			}

			if (!process.next()) {
				return;
			}

			process.load();

			process.calls();

			goalTimeMicros = lastTimeMicros + 1000000L / maxFrames;
			currTimeMicros = timer.sleepTimeMicros(goalTimeMicros);
			elapsedTimeMicros = currTimeMicros - lastTimeMicros
					+ remainderMicros;
			elapsedTime = MathUtils.max(0, (elapsedTimeMicros / 1000));
			remainderMicros = elapsedTimeMicros - elapsedTime * 1000;
			lastTimeMicros = currTimeMicros;
			timerContext.millisSleepTime = remainderMicros;
			timerContext.timeSinceLastUpdate = elapsedTime;

			ActionControl.update(elapsedTime);

			process.runTimer(timerContext);

			if (LSystem.AUTO_REPAINT) {

				int repaintMode = process.getRepaintMode();
				switch (repaintMode) {
				case Screen.SCREEN_BITMAP_REPAINT:
					gl.reset(true);
					if (process.getX() == 0 && process.getY() == 0) {
						gl.drawTexture(process.getBackground(), 0, 0);
					} else {
						gl.drawTexture(process.getBackground(), process.getX(),
								process.getY());
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
								repaintMode / 2
										- LSystem.random.nextInt(repaintMode),
								repaintMode / 2
										- LSystem.random.nextInt(repaintMode));
					} else {
						gl.drawTexture(process.getBackground(),
								process.getX() + repaintMode / 2
										- LSystem.random.nextInt(repaintMode),
								process.getY() + repaintMode / 2
										- LSystem.random.nextInt(repaintMode));
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
					long currentMemory = totalMemory - runtime.freeMemory();
					String memory = ((float) ((currentMemory * 10) >> 20) / 10)
							+ " of "
							+ ((float) ((totalMemory * 10) >> 20) / 10) + " MB";
					fpsFont.drawString("MEMORY:" + memory, 5, 25, 0,
							LColor.white);
				}

				process.drawEmulator(gl);

				process.unload();

			}

		}

		if (onPause) {
			Log.i("Android2DView", "onPause");
			pause(500);
			process.onPause();
		}

		if (onDestroy) {
			Log.i("Android2DView", "onDestroy");
			if (process != null) {
				process.end();
			}
			process.onDestroy();
		}

	}

	@Override
	public void invokeAsync(final Updateable act) {
		LSystem.getOSHandler().post(new Runnable() {
			@Override
			public void run() {
				act.action();
			}
		});
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

	@Override
	public void onSurfaceChanged(GL10 gl10, int width, int height) {
		if (gl != null) {
			Log.i("Android2DView", "onSurfaceChangedUpdate");
			this.width = width;
			this.height = height;
			gl.setViewPort(0, 0, width, height);
			if (process != null) {
				process.resize(width, height);
			}
		} else if (gl == null || !gl.equals(gl10, width, height)) {
			Log.i("Android2DView", "onSurfaceChangedCreate");
			createGL(gl10);
		}
	}

	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig config) {
		createGL(gl10);
	}

	private void createGL(GL10 gl10) {
		if (gl == null || !gl.equals(gl10, width, height)) {
			Log.i("Android2DView", "onSurfaceCreated");
			this.gl = new GLEx(gl10, LSystem.screenRect.width,
					LSystem.screenRect.height);
			this.supportVBO = GLEx.checkVBO();
			if (glMode == GLMode.VBO) {
				if (supportVBO) {
					GLEx.setVbo(true);
				} else {
					GLEx.setVbo(false);
					setGLMode(GLMode.Default);
				}
			} else {
				GLEx.setVbo(false);
				setGLMode(GLMode.Default);
			}
			gl.update();
			gl.setViewPort(0, 0, LSystem.screenRect.width,
					LSystem.screenRect.height);
			if (!LSystem.isCreated) {
				if (process != null) {
					process.begin();
					process.resize(LSystem.screenRect.width,
							LSystem.screenRect.height);
				}
				LSystem.isCreated = true;
				synchronized (this) {
					LSystem.isRunning = true;
				}
			}
		}

	}

	public void setFullScreen(boolean fullScreen) {
		Window win = LSystem.screenActivity.getWindow();
		if (LSystem.isAndroidVersionHigher(11)) {
			int flagHardwareAccelerated = 0x1000000;
			win.setFlags(flagHardwareAccelerated, flagHardwareAccelerated);
		}
		if (fullScreen) {
			win.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			win.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			win.requestFeature(android.view.Window.FEATURE_NO_TITLE);
		} else {
			win.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
		try {
			win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			win.setBackgroundDrawable(null);
		} catch (Exception e) {
		}
	}

	public void setGLMode(GLMode mode) {
		this.glMode = mode;
	}

	public boolean isSupportVBO() {
		return supportVBO;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public LTexture getLogo() {
		return logoFlag.logo;
	}

	public void setLogo(LTexture img) {
		if (logoFlag == null) {
			this.logoFlag = new LGameTools.Logo(new LTexture(img));
		}
	}

	public void setLogo(String path) {
		setLogo(LTextures.loadTexture(path, Format.BILINEAR));
	}

	public void setShowLogo(boolean showLogo) {
		LSystem.isLogo = showLogo;
		if (logoFlag == null) {
			setLogo(LSystem.FRAMEWORK_IMG_NAME + "logo.png");
		}
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

	public View getView() {
		return surfaceView;
	}

	public boolean isRunning() {
		return onRunning;
	}

	public boolean isPause() {
		return onPause;
	}

	public boolean isResume() {
		return onResume;
	}

	public boolean isDestroy() {
		return onDestroy;
	}

}
