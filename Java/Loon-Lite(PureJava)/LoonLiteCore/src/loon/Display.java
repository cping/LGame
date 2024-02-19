/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon;

import java.io.OutputStream;

import loon.action.ActionControl;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.font.IFont;
import loon.opengl.GLEx;
import loon.utils.ArrayByte;
import loon.utils.ArrayByteOutput;
import loon.utils.GLUtils;
import loon.utils.GifEncoder;
import loon.utils.MathUtils;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.reply.Act;
import loon.utils.reply.Port;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

/**
 * loon的最上级显示渲染与控制用类,本地api通过与此类交互实现游戏功能
 */
public class Display extends BaseIO implements LRelease {

	// 为了方便直接转码到C#和C++，无法使用匿名内部类(也就是在构造内直接构造实现的方式)，只能都写出具体类来……
	// PS:别提delegate，委托那玩意写出来太不优雅了(对于凭空实现某接口或抽象，而非局部重载来说)，而且大多数J2C#的工具也不能直接转换过去……
	private final class PaintPort extends Port<LTimerContext> {

		private final Display _display;

		PaintPort(Display d) {
			this._display = d;
		}

		@Override
		public void onEmit(LTimerContext clock) {
			synchronized (clock) {
				if (!LSystem.PAUSED) {
					RealtimeProcessManager.get().tick(clock);
					_display.draw(clock);
				}
			}
		}

	}

	private final class PaintAllPort extends Port<LTimerContext> {

		private final Display _display;

		PaintAllPort(Display d) {
			this._display = d;
		}

		@Override
		public void onEmit(LTimerContext clock) {
			synchronized (clock) {
				if (!LSystem.PAUSED) {
					RealtimeProcessManager.get().tick(clock);
					ActionControl.get().call(clock.timeSinceLastUpdate);
					_display.draw(clock);
				}
			}
		}

	}

	private final class UpdatePort extends Port<LTimerContext> {

		UpdatePort() {
		}

		@Override
		public void onEmit(LTimerContext clock) {
			synchronized (clock) {
				if (!LSystem.PAUSED) {
					ActionControl.get().call(clock.timeSinceLastUpdate);
				}
			}
		}
	}

	private final class Logo implements LRelease {

		private int centerX = 0, centerY = 0;

		private float alpha = 0f;

		private float curFrame, curTime;

		boolean finish, inToOut;

		LTexture logo;

		public Logo(LTexture texture) {
			this.logo = texture;
			this.curTime = 60;
			this.curFrame = 0;
			this.inToOut = true;
		}

		public void draw(final GLEx gl) {
			if (logo == null || finish) {
				return;
			}
			if (!logo.isLoaded()) {
				this.logo.loadTexture();
			}
			if (centerX == 0 || centerY == 0) {
				this.centerX = (LSystem.viewSize.getWidth()) / 2 - logo.getWidth() / 2;
				this.centerY = (LSystem.viewSize.getHeight()) / 2 - logo.getHeight() / 2;
			}
			if (logo == null || !logo.isLoaded()) {
				return;
			}
			alpha = (curFrame / curTime);
			if (inToOut) {
				curFrame++;
				if (curFrame == curTime) {
					alpha = 1f;
					inToOut = false;
				}
			} else if (!inToOut) {
				curFrame--;
				if (curFrame == 0) {
					alpha = 0f;
					finish = true;
				}
			}
			gl.setAlpha(MathUtils.clamp(alpha, 0f, 0.98f));
			gl.draw(logo, centerX, centerY);
		}

		@Override
		public void close() {
			if (logo != null) {
				logo.close();
				logo = null;
			}
		}
	}

	public final Act<LTimerContext> update = Act.create();

	public final Act<LTimerContext> paint = Act.create();

	private final LTimerContext updateClock = new LTimerContext();

	private final LTimerContext paintClock = new LTimerContext();

	private final LGame _game;

	private boolean _closed, _autoRepaint;

	private final long updateRate;

	private long nextUpdate;

	private final static String FPS_STR = "FPS:";

	private final static String MEMORY_STR = "MEMORY:";

	private final static String SPRITE_STR = "SPRITE:";

	private final static String DESKTOP_STR = "DESKTOP:";

	private String displayMemony = MEMORY_STR;

	private String displaySprites = SPRITE_STR;

	private StrBuilder displayMessage = new StrBuilder(32);

	private GifEncoder gifEncoder;

	private boolean videoScreenToGif;

	private boolean memorySelf;

	private ArrayByteOutput videoCache;

	private final LTimer videoDelay = new LTimer();

	private Runtime runtime;

	private long frameCount = 0l;

	private long frameDelta = 0l;

	private int frameRate = 0;

	private IFont displayFont;

	private float cred, cgreen, cblue, calpha;

	private LogDisplay _logDisplay;

	private final GLEx _glEx;

	private final LProcess _process;

	private LSetting _setting;

	protected boolean showLogo = false, initDrawConfig = false;

	private boolean logDisplayCreated = false;

	private Logo logoTex;

	private PaintAllPort paintAllPort;

	private PaintPort paintPort;

	private UpdatePort updatePort;

	public Display(LGame g, long updateRate) {
		this.updateRate = updateRate;
		this._game = g;
		this._game.checkBaseGame(g);
		this._setting = _game.setting;
		this._process = _game.process();
		this.memorySelf = _game.isBrowser();
		Graphics graphics = _game.graphics();
		_glEx = new GLEx(graphics);
		_glEx.update();
		updateSyncTween(_setting.isSyncTween);
		this.displayMemony = MEMORY_STR + "0";
		this.displaySprites = SPRITE_STR + "0 " + DESKTOP_STR + "0";
		if (!_setting.isLogo) {
			_process.start();
		}
		_game.addStatus(new Port<LGame>() {
			@Override
			public void onEmit(LGame game) {
				onFrame();
			}
		});
		this._autoRepaint = true;
	}

	protected void newDefView(boolean show) {
		if (show && displayFont == null) {
			this.displayFont = LSystem.getSystemLogFont();
		}
		if (show && _setting.isDisplayLog) {
			if (displayFont != null) {
				_logDisplay = new LogDisplay(displayFont);
			} else {
				_logDisplay = new LogDisplay();
			}
			logDisplayCreated = true;
		}
		showLogo = _setting.isLogo;
		if (showLogo && !StringUtils.isEmpty(_setting.logoPath)) {
			logoTex = new Logo(newTexture(_setting.logoPath));
		}
	}

	public boolean isLogDisplay() {
		return logDisplayCreated;
	}

	public void clearLog() {
		if (logDisplayCreated) {
			_logDisplay.clear();
		}
	}

	public void addLog(String mes, LColor col) {
		if (!logDisplayCreated) {
			return;
		}
		_logDisplay.addText(mes, col);
	}

	public void addLog(String mes) {
		if (!logDisplayCreated) {
			return;
		}
		_logDisplay.addText(mes);
	}

	public LogDisplay getLogDisplay() {
		return _logDisplay;
	}

	protected void paintLog(final GLEx g, int x, int y) {
		if (!logDisplayCreated) {
			return;
		}
		_logDisplay.paint(g, x, y);
	}

	public void updateSyncTween(boolean sync) {
		if (paintAllPort != null) {
			paint.disconnect(paintAllPort);
		}
		if (paintPort != null) {
			paint.disconnect(paintPort);
		}
		if (update != null) {
			update.disconnect(updatePort);
		}
		if (sync) {
			paint.connect(paintAllPort = new PaintAllPort(this));
		} else {
			paint.connect(paintPort = new PaintPort(this));
			update.connect(updatePort = new UpdatePort());
		}
	}

	/**
	 * 清空当前游戏窗体内容为指定色彩
	 *
	 * @param red
	 * @param green
	 * @param blue
	 * @param alpha
	 */
	public void clearColor(float red, float green, float blue, float alpha) {
		cred = red;
		cgreen = green;
		cblue = blue;
		calpha = alpha;
	}

	/**
	 * 清空当前游戏窗体内容为指定色彩
	 *
	 * @param color
	 */
	public void clearColor(LColor color) {
		this.clearColor(color.r, color.g, color.b, color.a);
	}

	/**
	 * 清空当前游戏窗体内容为纯黑色
	 */
	public void clearColor() {
		this.clearColor(0, 0, 0, 0);
	}

	public void update(LTimerContext clock) {
		update.emit(clock);
	}

	public void paint(LTimerContext clock) {
		paint.emit(clock);
	}

	protected void draw(LTimerContext clock) {
		if (_closed) {
			return;
		}
		// fix渲染时机，避免调用渲染在纹理构造前
		if (!initDrawConfig) {
			newDefView(
					_setting.isFPS || _setting.isLogo || _setting.isMemory || _setting.isSprites || _setting.isDebug);
			initDrawConfig = true;
		}

		if (showLogo) {
			try {
				_glEx.save();
				_glEx.begin();
				_glEx.clear(cred, cgreen, cblue, calpha);
				if (logoTex == null || logoTex.finish || logoTex.logo.disposed()) {
					showLogo = false;
					return;
				}
				logoTex.draw(_glEx);
				if (logoTex.finish) {
					showLogo = false;
					logoTex.close();
					logoTex = null;
				}
			} finally {
				_glEx.end();
				_glEx.restore();
				if (!showLogo) {
					_process.start();
				}
			}
			return;
		}

		if (!_process.next()) {
			return;
		}
		try {
			_glEx.saveTx();
			if (_setting.allScreenRefresh) {
				_glEx.reset();
			}
			_glEx.begin();

			_process.load();
			_process.runTimer(clock);
			_process.draw(_glEx);

			// 渲染debug信息
			drawDebug(_glEx, _setting, clock.timeSinceLastUpdate);

			_process.drawEmulator(_glEx);
			_process.unload();

			// 如果存在屏幕录像设置
			if (videoScreenToGif && !LSystem.PAUSED && gifEncoder != null) {
				if (videoDelay.action(clock)) {
					Image tmp = GLUtils.getScreenshot();
					Image image = null;
					if (LSystem.isDesktop()) {
						image = tmp;
					} else {
						// 因为内存和速度关系,考虑到全平台录制,因此默认只录屏幕大小的一半(否则在手机上绝对抗不了5分钟以上……)
						image = Image.getResize(tmp, (int) (_process.getWidth() * 0.5f),
								(int) (_process.getHeight() * 0.5f));
					}
					gifEncoder.addFrame(image);
					if (tmp != null) {
						tmp.close();
						tmp = null;
					}
					if (image != null) {
						image.close();
						image = null;
					}
				}
			}

		} finally {
			_glEx.end();
			_glEx.restoreTx();
			_process.resetTouch();
		}

	}

	private void onFrame() {
		if (_closed) {
			return;
		}
		if (!_autoRepaint) {
			return;
		}
		final int updateTick = _game.tick();
		final LSetting setting = _game.setting;
		final long paintLoop = setting.fixedPaintLoopTime;
		final long updateLoop = setting.fixedUpdateLoopTime;
		final float fpsScale = setting.getScaleFPS();
		long nextUpdate = this.nextUpdate;
		if (updateTick >= nextUpdate) {
			final long updateRate = this.updateRate;
			long updates = 0;
			while (updateTick >= nextUpdate) {
				nextUpdate += updateRate;
				updates++;
			}
			this.nextUpdate = nextUpdate;
			final long updateDt = updates * updateRate;
			updateClock.tick += updateDt;
			if (updateLoop == -1) {
				updateClock.timeSinceLastUpdate = (long) (updateDt * fpsScale);
			} else {
				updateClock.timeSinceLastUpdate = updateLoop;
			}
			if (updateClock.timeSinceLastUpdate > LSystem.SECOND) {
				updateClock.timeSinceLastUpdate = 0;
			}
			update(updateClock);
		}
		final long paintTick = _game.tick();
		if (paintLoop == -1) {
			paintClock.timeSinceLastUpdate = (long) ((paintTick - paintClock.tick) * fpsScale);
		} else {
			paintClock.timeSinceLastUpdate = paintLoop;
		}
		if (paintClock.timeSinceLastUpdate > LSystem.SECOND) {
			paintClock.timeSinceLastUpdate = 0;
		}
		paintClock.tick = paintTick;
		paintClock.alpha = 1f - (nextUpdate - paintTick) / (float) updateRate;
		paint(paintClock);
	}

	/**
	 * 渲染debug信息到游戏画面
	 *
	 * @param gl
	 * @param setting
	 * @param delta
	 */
	private final void drawDebug(final GLEx gl, final LSetting setting, final long delta) {
		if (_closed) {
			return;
		}
		final boolean debug = setting.isDebug;

		if (debug || setting.isFPS || setting.isMemory || setting.isSprites) {
			this.frameCount++;
			this.frameDelta += delta;

			if (frameCount % 60 == 0 && frameDelta != 0) {
				final int dstFPS = setting.fps;
				final int newFps = MathUtils.round((LSystem.SECOND * frameCount * setting.getScaleFPS()) / frameDelta)
						+ 1;
				this.frameRate = MathUtils.clamp(newFps, 0, dstFPS);
				if (frameRate == dstFPS - 1) {
					frameRate = MathUtils.max(dstFPS, frameRate);
				}
				this.frameDelta = 0;
				this.frameCount = 0;

				if (this.memorySelf) {
					displayMessage.setLength(0);
					displayMessage.append(MEMORY_STR);
					displayMessage.append(((float) ((LTextures.getMemSize() * 100) >> 20) / 10f));
					displayMessage.append(" of ");
					displayMessage.append('?');
					displayMessage.append(" MB");
				} else {
					if (runtime == null) {
						runtime = Runtime.getRuntime();
					}
					long totalMemory = runtime.totalMemory();
					long currentMemory = totalMemory - runtime.freeMemory();
					displayMessage.setLength(0);
					displayMessage.append(MEMORY_STR);
					displayMessage.append(((float) ((currentMemory * 10) >> 20) / 10f));
					displayMessage.append(" of ");
					displayMessage.append(((float) ((runtime.maxMemory() * 10) >> 20) / 10f));
					displayMessage.append(" MB");
				}
				displayMemony = displayMessage.toString();

				LGame game = getGame();

				displayMessage.setLength(0);
				displayMessage.append(SPRITE_STR);
				displayMessage.append(game.allSpritesCount());
				displayMessage.append(" ");
				displayMessage.append(DESKTOP_STR);
				displayMessage.append(game.allDesktopCount());

				displaySprites = displayMessage.toString();

			}
			if (displayFont != null) {
				// 显示fps速度
				if (debug || setting.isFPS) {
					displayFont.drawString(gl, FPS_STR + frameRate, 5, 5, 0, LColor.white);
				}
				// 显示内存占用
				if (debug || setting.isMemory) {
					displayFont.drawString(gl, displayMemony, 5, 25, 0, LColor.white);
				}
				// 显示精灵与组件数量
				if (debug || setting.isSprites) {
					displayFont.drawString(gl, displaySprites, 5, 45, 0, LColor.white);
				}
				// 若打印日志到界面,很可能挡住游戏界面内容,所以isDisplayLog为true并且debug才显示
				if (debug && setting.isDisplayLog) {
					paintLog(gl, 5, 65);
				}
			}
		}
	}

	public boolean isRunning() {
		return initDrawConfig;
	}

	public boolean isAutoRepaint() {
		return _autoRepaint;
	}

	public Display setAutoRepaint(boolean r) {
		this._autoRepaint = r;
		return this;
	}

	public Display stopRepaint() {
		this._autoRepaint = false;
		return this;
	}

	public Display startRepaint() {
		this._autoRepaint = true;
		return this;
	}

	public int getFPS() {
		return frameRate;
	}

	public float getAlpha() {
		return calpha;
	}

	public float getRed() {
		return cred;
	}

	public float getGreen() {
		return cgreen;
	}

	public float getBlue() {
		return cblue;
	}

	public GLEx GL() {
		return _glEx;
	}

	public float width() {
		return LSystem.viewSize.width();
	}

	public float height() {
		return LSystem.viewSize.height;
	}

	/**
	 * 返回video的缓存结果(不设置out对象时才会有效)
	 *
	 * @return
	 */
	public ArrayByte getVideoCache() {
		return videoCache.getArrayByte();
	}

	/**
	 * 开始录像(默认使用ArrayByte缓存录像结果到内存中)
	 *
	 * @return
	 */
	public GifEncoder startVideo() {
		return startVideo(videoCache = new ArrayByteOutput());
	}

	/**
	 * 开始录像(指定一个OutputStream对象,比如FileOutputStream 输出录像结果到指定硬盘位置)
	 *
	 * @param output
	 * @return
	 */
	public GifEncoder startVideo(OutputStream output) {
		return startVideo(output, LSystem.isDesktop() ? LSystem.SECOND : LSystem.SECOND + LSystem.SECOND / 2);
	}

	/**
	 * 开始录像(指定一个OutputStream对象,比如FileOutputStream 输出录像结果到指定硬盘位置)
	 *
	 * @param output
	 * @param delay
	 * @return
	 */
	public GifEncoder startVideo(OutputStream output, long delay) {
		stopVideo();
		videoDelay.setDelay(delay);
		gifEncoder = new GifEncoder();
		gifEncoder.start(output);
		gifEncoder.setDelay((int) delay);
		videoScreenToGif = true;
		return gifEncoder;
	}

	/**
	 * 结束录像
	 *
	 * @return
	 */
	public GifEncoder stopVideo() {
		if (gifEncoder != null) {
			gifEncoder.finish();
		}
		videoScreenToGif = false;
		return gifEncoder;
	}

	public final LTimerContext getUpdate() {
		return updateClock;
	}

	public final LTimerContext getPaint() {
		return paintClock;
	}

	public Display resize(int viewWidth, int viewHeight) {
		_process.resize(viewWidth, viewHeight);
		if (_glEx != null) {
			_glEx.resize();
		}
		return this;
	}

	public Display setScreen(Screen screen) {
		_process.setScreen(screen);
		return this;
	}

	public Display resume() {
		_process.resume();
		return this;
	}

	public Display pause() {
		_process.pause();
		return this;
	}

	public IFont getDisplayFont() {
		return displayFont;
	}

	public Display setDisplayFont(IFont displayFont) {
		this.displayFont = displayFont;
		return this;
	}

	public LProcess getProcess() {
		return _process;
	}

	public LGame getGame() {
		return _game;
	}

	public boolean isClosed() {
		return this._closed;
	}

	@Override
	public void close() {
		this._closed = true;
		this._autoRepaint = false;
		if (this.displayFont != null) {
			this.displayFont.close();
			this.displayFont = null;
		}
		if (this.logoTex != null) {
			this.logoTex.close();
			this.logoTex = null;
		}
		if (this._process != null) {
			_process.close();
		}
		this.initDrawConfig = false;
		logDisplayCreated = false;
	}

}
