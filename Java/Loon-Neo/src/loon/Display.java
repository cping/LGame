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
import loon.opengl.ShaderSource;
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
public final class Display extends BaseIO implements LRelease {

	// 为了方便直接转码到C#和C++，无法使用匿名内部类(也就是在构造内直接构造实现的方式)，只能都写出具体类来……
	// PS:别提delegate，委托那玩意写出来太不优雅了(对于凭空实现某接口或抽象，而非局部重载来说)，而且大多数J2C#的工具也不能直接转换过去……
	private final class PaintPort extends Port<LTimerContext> {

		private final Display _display;

		PaintPort(final Display d) {
			this._display = d;
		}

		@Override
		public void onEmit(final LTimerContext clock) {
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

		PaintAllPort(final Display d) {
			this._display = d;
		}

		@Override
		public void onEmit(final LTimerContext clock) {
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
		public void onEmit(final LTimerContext clock) {
			synchronized (clock) {
				if (!LSystem.PAUSED) {
					ActionControl.get().call(clock.timeSinceLastUpdate);
				}
			}
		}
	}

	private final class Logo implements LRelease {

		private int _centerX = 0, _centerY = 0;

		private float _alpha = 0f;

		private float _curFrame, _curTime;

		boolean _finish, _inToOut;

		LTexture _logo;

		public Logo(final LTexture texture) {
			this._logo = texture;
			this._curTime = 60;
			this._curFrame = 0;
			this._inToOut = true;
		}

		public void draw(final GLEx gl) {
			if (_logo == null || _finish) {
				return;
			}
			if (!_logo.isLoaded()) {
				this._logo.loadTexture();
			}
			if (_centerX == 0 || _centerY == 0) {
				this._centerX = (LSystem.viewSize.getWidth()) / 2 - _logo.getWidth() / 2;
				this._centerY = (LSystem.viewSize.getHeight()) / 2 - _logo.getHeight() / 2;
			}
			if (_logo == null || !_logo.isLoaded()) {
				return;
			}
			_alpha = (_curFrame / _curTime);
			if (_inToOut) {
				_curFrame++;
				if (_curFrame == _curTime) {
					_alpha = 1f;
					_inToOut = false;
				}
			} else if (!_inToOut) {
				_curFrame--;
				if (_curFrame == 0) {
					_alpha = 0f;
					_finish = true;
				}
			}
			gl.setAlpha(MathUtils.clamp(_alpha, 0f, 0.98f));
			gl.draw(_logo, _centerX, _centerY);
		}

		@Override
		public void close() {
			if (_logo != null) {
				_logo.close();
				_logo = null;
			}
		}
	}

	private final static String FPS_STR = "FPS:";

	private final static String MEMORY_STR = "MEMORY:";

	private final static String SPRITE_STR = "SPRITE:";

	private final static String DESKTOP_STR = "DESKTOP:";

	private final static String DRAWCALL_STR = "DRAWCALL:";

	public final Act<LTimerContext> update = Act.create();

	public final Act<LTimerContext> paint = Act.create();

	private final LTimerContext _updateClock = new LTimerContext();

	private final LTimerContext _paintClock = new LTimerContext();

	private final LTimer _videoDelay = new LTimer();

	private final LGame _game;

	private final GLEx _glEx;

	private final LProcess _process;

	private final long _updateRate;

	private float _tempMaxMemory = -1;

	protected boolean _showLogo = false, _initDrawConfig = false;

	private LColor _debugFontColor = LColor.white;

	private boolean _closed, _autoUpdate, _autoRepaint;

	private long _nextUpdate;

	private String _displayMemony = MEMORY_STR;

	private String _displaySprites = SPRITE_STR;

	private String _displayDrawCall = DRAWCALL_STR;

	private StrBuilder _displayMessage = new StrBuilder(LSystem.DEFAULT_MAX_CACHE_SIZE);

	private GifEncoder _gifEncoder;

	private boolean _videoScreenToGif;

	private boolean _memorySelf;

	private ArrayByteOutput _videoCache;

	private Runtime _runtime;

	private long _frameCount = 0l;

	private long _frameDelta = 0l;

	private long _sinceRefreshMaxInterval = 0l;

	private int _frameRate = 0;

	private int _debugTextSpace = 0;

	private IFont _displayFont;

	private float _cred, _cgreen, _cblue, _calpha;

	private LogDisplay _logDisplay;

	private LSetting _setting;

	private int _displayTop;

	private boolean _logDisplayCreated = false;

	private Logo _logoTex;

	private PaintAllPort _paintAllPort;

	private PaintPort _paintPort;

	private UpdatePort _updatePort;

	public Display(final LGame g, final long updateRate) {
		this._updateRate = updateRate;
		this._game = g;
		this._game.checkBaseGame(g);
		this._setting = _game.setting;
		this._process = _game.process();
		this._sinceRefreshMaxInterval = LSystem.SECOND;
		this._debugTextSpace = 5;
		this._memorySelf = _game.isHTML5() || _game.isCPort() || _game.isCSharpPort();
		Graphics graphics = _game.graphics();
		this._glEx = new GLEx(graphics, graphics.defaultRenderTarget, graphics.gl);
		this._glEx.update();
		this.initGameDisplay(g);
	}

	protected void initGameDisplay(final LGame game) {
		this.updateSyncTween(game.setting.isSyncTween);
		this.initDebugString();
		this.autoDisplay();
		game.setupDisplay(this);
	}

	protected void initDebugString() {
		this._displayMemony = MEMORY_STR + "0";
		this._displaySprites = SPRITE_STR + "0 " + DESKTOP_STR + "0";
		this._displayDrawCall = DRAWCALL_STR + "0";
	}

	public Display autoDisplay() {
		this._autoUpdate = this._autoRepaint = true;
		return this;
	}

	public Display stopAutoDisplay() {
		this._autoUpdate = this._autoRepaint = false;
		return this;
	}

	public LColor getDebugFontColor() {
		return this._debugFontColor;
	}

	public Display setDebugFontColor(final LColor fc) {
		this._debugFontColor = fc;
		return this;
	}

	public int getDebugTextSpace() {
		return this._debugTextSpace;
	}

	public Display setDebugTextSpace(final int s) {
		this._debugTextSpace = s;
		return this;
	}

	public long getSinceRefreshMaxInterval() {
		return this._sinceRefreshMaxInterval;
	}

	public Display setSinceRefreshMaxInterval(final long s) {
		this._sinceRefreshMaxInterval = s;
		return this;
	}

	protected void newDefView(final boolean show) {
		if (show && _displayFont == null) {
			this._displayFont = LSystem.getSystemLogFont();
		}
		if (show && _setting.isDisplayLog) {
			if (_displayFont != null) {
				_logDisplay = new LogDisplay(_displayFont);
			} else {
				_logDisplay = new LogDisplay();
			}
			_logDisplayCreated = true;
		}
		_showLogo = _setting.isLogo;
		if (_showLogo && !StringUtils.isEmpty(_setting.logoPath)) {
			_logoTex = new Logo(newTexture(_setting.logoPath));
		}
	}

	public boolean isLogDisplay() {
		return _logDisplayCreated;
	}

	public void clearLog() {
		if (_logDisplayCreated) {
			_logDisplay.clear();
		}
	}

	public void addLog(final String mes, final LColor col) {
		if (!_logDisplayCreated) {
			return;
		}
		_logDisplay.addText(mes, col);
	}

	public void addLog(final String mes) {
		if (!_logDisplayCreated) {
			return;
		}
		_logDisplay.addText(mes);
	}

	public LogDisplay getLogDisplay() {
		return _logDisplay;
	}

	protected void paintLog(final GLEx g, final int x, final int y) {
		if (!_logDisplayCreated) {
			return;
		}
		_logDisplay.paint(g, x, y);
	}

	public void updateSyncTween(final boolean sync) {
		if (_paintAllPort != null) {
			paint.disconnect(_paintAllPort);
		}
		if (_paintPort != null) {
			paint.disconnect(_paintPort);
		}
		if (update != null) {
			update.disconnect(_updatePort);
		}
		if (sync) {
			paint.connect(_paintAllPort = new PaintAllPort(this));
		} else {
			paint.connect(_paintPort = new PaintPort(this));
			update.connect(_updatePort = new UpdatePort());
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
	public void clearColor(final float red, final float green, final float blue, final float alpha) {
		_cred = red;
		_cgreen = green;
		_cblue = blue;
		_calpha = alpha;
	}

	/**
	 * 清空当前游戏窗体内容为指定色彩
	 * 
	 * @param color
	 */
	public void clearColor(final LColor color) {
		this.clearColor(color.r, color.g, color.b, color.a);
	}

	/**
	 * 清空当前游戏窗体内容为纯黑色
	 */
	public void clearColor() {
		this.clearColor(0, 0, 0, 0);
	}

	/**
	 * 启用GLEx的全局渲染FrameBuffer(全局缓存渲染到纹理中去)
	 */
	public void enableFrameBuffer() {
		if (_glEx != null) {
			_glEx.enableFrameBuffer();
		}
	}

	/**
	 * 关闭GLEx的全局渲染FrameBuffer
	 */
	public void disableFrameBuffer() {
		if (_glEx != null) {
			_glEx.disableFrameBuffer();
		}
	}

	public void update(final LTimerContext clock) {
		update.emit(clock);
	}

	public void paint(final LTimerContext clock) {
		paint.emit(clock);
	}

	protected void draw(final LTimerContext clock) {
		if (_closed) {
			return;
		}
		// fix渲染时机，避免调用渲染在纹理构造前
		if (!_initDrawConfig) {
			newDefView(
					_setting.isFPS || _setting.isLogo || _setting.isMemory || _setting.isSprites || _setting.isDebug);
			_initDrawConfig = true;
		}

		if (_showLogo) {
			boolean saveBuffer = _glEx.isSaveFrameBuffer();
			try {
				if (saveBuffer) {
					_glEx.disableFrameBuffer();
				}
				_glEx.save();
				_glEx.begin();
				_glEx.clear(_cred, _cgreen, _cblue, _calpha);
				if (_logoTex == null || _logoTex._finish || _logoTex._logo.disposed()) {
					_showLogo = false;
					return;
				}
				_logoTex.draw(_glEx);
				if (_logoTex._finish) {
					_showLogo = false;
					_logoTex.close();
					_logoTex = null;
				}
				if (saveBuffer) {
					_glEx.enableFrameBuffer();
				}
			} finally {
				_glEx.end();
				_glEx.restore();
				if (!_showLogo) {
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

			// 在某些情况下,比如存在全局背景时，因为旧有画面已被遮挡，不必全局刷新Screen画面,应禁止全局刷新画布内容
			if (_setting.allScreenRefresh) {
				_glEx.reset(_cred, _cgreen, _cblue, _calpha);
			} else {
				_glEx.resetConfig();
			}

			_glEx.begin();

			// 最初渲染的内容
			_process.drawFrist(_glEx);
			_process.load();
			_process.runTimer(clock);

			_process.draw(_glEx);

			// 渲染debug信息
			drawDebug(_glEx, _setting, clock.unscaledTimeSinceLastUpdate);

			_process.drawEmulator(_glEx);
			// 最后渲染的内容
			_process.drawLast(_glEx);

			_process.unload();

			// 如果存在屏幕录像设置
			if (_videoScreenToGif && !LSystem.PAUSED && _gifEncoder != null) {
				if (_videoDelay.action(clock)) {
					Image tmp = GLUtils.getScreenshot();
					Image image = null;
					if (LSystem.isDesktop()) {
						image = tmp;
					} else {
						// 因为内存和速度关系,考虑到全平台录制,因此默认只录屏幕大小的一半(否则在手机上绝对抗不了5分钟以上……)
						image = Image.getResize(tmp, MathUtils.iceil(_process.getWidth() * 0.5f),
								MathUtils.iceil(_process.getHeight() * 0.5f));
					}
					_gifEncoder.addFrame(image);
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
			_glEx.clearFrame();
			_process.resetTouch();
			GraphicsDrawCall.clear();
		}
	}

	protected void onFrame() {
		if (_closed) {
			return;
		}
		final LSetting setting = _game.setting;
		final float fpsScale = setting.getScaleFPS();
		if (_autoUpdate) {
			final int updateTick = _game.tick();
			final long updateLoop = setting.fixedUpdateLoopTime;
			long nextUpdate = this._nextUpdate;
			if (updateTick >= nextUpdate) {
				final long updateRate = this._updateRate;
				long updates = 0;
				while (updateTick >= nextUpdate) {
					nextUpdate += updateRate;
					updates++;
				}
				this._nextUpdate = nextUpdate;
				final long updateDt = updates * updateRate;
				if (updateLoop == -1) {
					_updateClock.timeSinceLastUpdate = (long) (updateDt * fpsScale);
					_updateClock.unscaledTimeSinceLastUpdate = updateDt;
				} else {
					_updateClock.timeSinceLastUpdate = updateLoop;
					_updateClock.unscaledTimeSinceLastUpdate = updateLoop;
				}
				if (_updateClock.timeSinceLastUpdate > _sinceRefreshMaxInterval) {
					_updateClock.timeSinceLastUpdate = 0;
					_updateClock.unscaledTimeSinceLastUpdate = 0;
				}
				_updateClock.tick += _updateClock.timeSinceLastUpdate;
				update(_updateClock);
			}
		}
		if (_autoRepaint) {
			final long paintLoop = setting.fixedPaintLoopTime;
			final long paintTick = _game.tick();
			if (paintLoop == -1) {
				final long clock = paintTick - _paintClock.tick;
				_paintClock.timeSinceLastUpdate = (long) (clock * fpsScale);
				_paintClock.unscaledTimeSinceLastUpdate = clock;
			} else {
				_paintClock.timeSinceLastUpdate = paintLoop;
				_paintClock.unscaledTimeSinceLastUpdate = paintLoop;
			}
			if (_paintClock.timeSinceLastUpdate > _sinceRefreshMaxInterval) {
				_paintClock.timeSinceLastUpdate = 0;
				_paintClock.unscaledTimeSinceLastUpdate = 0;
			}
			_paintClock.tick = paintTick;
			_paintClock.alpha = 1f - (_nextUpdate - paintTick) / (float) _updateRate;
			paint(_paintClock);
		}
	}

	private final float getMaxMemory(final Runtime r) {
		if (_tempMaxMemory != -1) {
			return _tempMaxMemory;
		}
		try {
			_tempMaxMemory = MathUtils.abs((r.maxMemory() * 10) >> 20) / 10f;
		} catch (Exception e) {
			_tempMaxMemory = 0;
		}
		return _tempMaxMemory;
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

		if (debug || setting.isFPS || setting.isMemory || setting.isSprites || setting.isDrawCall) {

			this._frameCount++;
			this._frameDelta += delta;

			if (_frameCount % 60 == 0 && _frameDelta != 0) {
				final int dstFPS = setting.fps;
				final int newFps = MathUtils
						.round((_sinceRefreshMaxInterval * _frameCount * setting.getScaleFPS()) / _frameDelta) + 1;
				this._frameRate = MathUtils.clamp(newFps, 0, dstFPS);
				if (_frameRate == dstFPS - 1) {
					_frameRate = MathUtils.max(dstFPS, _frameRate);
				}
				this._frameDelta = this._frameCount = 0;

				if (this._memorySelf) {
					_displayMessage.setLength(0);
					_displayMessage.append(MEMORY_STR);
					_displayMessage.append(MathUtils.abs(((LTextures.getMemSize() * 100) >> 20) / 10f));
					_displayMessage.append(" of ");
					_displayMessage.append('?');
					_displayMessage.append(" MB");
				} else {
					if (_runtime == null) {
						_runtime = Runtime.getRuntime();
					}
					final long totalMemory = _runtime.totalMemory();
					final long currentMemory = totalMemory - _runtime.freeMemory();
					_displayMessage.setLength(0);
					_displayMessage.append(MEMORY_STR);
					_displayMessage.append(MathUtils.abs((currentMemory * 10) >> 20) / 10f);
					_displayMessage.append(" of ");
					_displayMessage.append(getMaxMemory(_runtime));
					_displayMessage.append(" MB");
				}
				_displayMemony = _displayMessage.toString();

				final LGame game = getGame();

				_displayMessage.setLength(0);
				_displayMessage.append(SPRITE_STR);
				_displayMessage.append(game.allSpritesCount());
				_displayMessage.append(" ");
				_displayMessage.append(DESKTOP_STR);
				_displayMessage.append(game.allDesktopCount());

				_displaySprites = _displayMessage.toString();

				_displayMessage.setLength(0);
				_displayMessage.append(DRAWCALL_STR);
				_displayMessage.append(GraphicsDrawCall.getCount() + gl.getDrawCallCount());

				_displayDrawCall = _displayMessage.toString();

			}
			if (_displayFont != null) {

				final int maxHeight = MathUtils.max(10, _displayFont.getSize()) + 2;

				// 显示fps速度
				if (debug || setting.isFPS) {
					_displayFont.drawString(gl, FPS_STR + _frameRate, _debugTextSpace, _displayTop += _debugTextSpace,
							0, _debugFontColor);
				}
				// 显示内存占用
				if (debug || setting.isMemory) {
					_displayFont.drawString(gl, _displayMemony, _debugTextSpace, _displayTop += maxHeight, 0,
							_debugFontColor);
				}
				// 显示精灵与组件数量
				if (debug || setting.isSprites) {
					_displayFont.drawString(gl, _displaySprites, _debugTextSpace, _displayTop += maxHeight, 0,
							_debugFontColor);
				}
				// 显示渲染次数
				if (debug || setting.isDrawCall) {
					_displayFont.drawString(gl, _displayDrawCall, _debugTextSpace, _displayTop += maxHeight, 0,
							_debugFontColor);
				}
				// 若打印日志到界面,很可能挡住游戏界面内容,所以isDisplayLog为true并且debug才显示
				if (debug && setting.isDisplayLog) {
					paintLog(gl, _debugTextSpace, _displayTop += maxHeight);
				}
				_displayTop = 0;
			}
		}

	}

	public boolean isRunning() {
		return _initDrawConfig;
	}

	public boolean isAutoRepaint() {
		return _autoRepaint;
	}

	public Display setAutoRepaint(final boolean r) {
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

	public boolean isAutoUpdate() {
		return _autoUpdate;
	}

	public Display setAutoUpdate(final boolean u) {
		this._autoUpdate = u;
		return this;
	}

	public Display stopUpdate() {
		this._autoUpdate = false;
		return this;
	}

	public Display startUpdate() {
		this._autoUpdate = true;
		return this;
	}

	public int getFPS() {
		return _frameRate;
	}

	public float getAlpha() {
		return _calpha;
	}

	public float getRed() {
		return _cred;
	}

	public float getGreen() {
		return _cgreen;
	}

	public float getBlue() {
		return _cblue;
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
		return _videoCache.getArrayByte();
	}

	/**
	 * 开始录像(默认使用ArrayByte缓存录像结果到内存中)
	 * 
	 * @return
	 */
	public GifEncoder startVideo() {
		return startVideo(_videoCache = new ArrayByteOutput());
	}

	/**
	 * 开始录像(指定一个OutputStream对象,比如FileOutputStream 输出录像结果到指定硬盘位置)
	 * 
	 * @param output
	 * @return
	 */
	public GifEncoder startVideo(final OutputStream output) {
		return startVideo(output, LSystem.isDesktop() ? _sinceRefreshMaxInterval
				: _sinceRefreshMaxInterval + _sinceRefreshMaxInterval / 2);
	}

	/**
	 * 开始录像(指定一个OutputStream对象,比如FileOutputStream 输出录像结果到指定硬盘位置)
	 * 
	 * @param output
	 * @param delay
	 * @return
	 */
	public GifEncoder startVideo(final OutputStream output, final long delay) {
		stopVideo();
		_videoDelay.setDelay(delay);
		_gifEncoder = new GifEncoder();
		_gifEncoder.start(output);
		_gifEncoder.setDelay((int) delay);
		_videoScreenToGif = true;
		return _gifEncoder;
	}

	/**
	 * 结束录像
	 * 
	 * @return
	 */
	public GifEncoder stopVideo() {
		if (_gifEncoder != null) {
			_gifEncoder.finish();
		}
		_videoScreenToGif = false;
		return _gifEncoder;
	}

	public final LTimerContext getUpdate() {
		return _updateClock;
	}

	public final LTimerContext getPaint() {
		return _paintClock;
	}

	public void setShaderSource(final ShaderSource src) {
		if (_glEx != null && src != null) {
			_glEx.setShaderSource(src);
		}
	}

	public ShaderSource getShaderSource() {
		if (_glEx != null) {
			return _glEx.getShaderSource();
		}
		return LSystem.DEF_SOURCE;
	}

	public Display resize(final int viewWidth, final int viewHeight) {
		if (_closed) {
			return this;
		}
		if (_logDisplay != null) {
			_logDisplay.setSize(viewWidth, viewHeight);
		}
		_process.resize(viewWidth, viewHeight);
		if (_glEx != null) {
			_glEx.resize();
		}
		return this;
	}

	public Display setScreen(final Screen screen) {
		if (_closed) {
			return this;
		}
		_process.setScreen(screen);
		return this;
	}

	public Display resume() {
		if (_closed) {
			return this;
		}
		_process.resume();
		return this;
	}

	public Display pause() {
		if (_closed) {
			return this;
		}
		_process.pause();
		return this;
	}

	public IFont getDisplayFont() {
		return _displayFont;
	}

	public Display setDisplayFont(final IFont displayFont) {
		this._displayFont = displayFont;
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
		this.stopAutoDisplay();
		if (this._displayFont != null) {
			this._displayFont.close();
			this._displayFont = null;
		}
		if (this._logoTex != null) {
			this._logoTex.close();
			this._logoTex = null;
		}
		if (this._process != null) {
			_process.close();
		}
		this._initDrawConfig = _logDisplayCreated = false;
	}

}
