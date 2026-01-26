/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.cport;

import loon.Accelerometer;
import loon.Assets;
import loon.Asyn;
import loon.Clipboard;
import loon.Graphics;
import loon.LGame;
import loon.LSetting;
import loon.LSystem;
import loon.LazyLoading;
import loon.Log;
import loon.Save;
import loon.Support;
import loon.cport.bridge.NativeSupport;
import loon.cport.bridge.SDLCall;
import loon.cport.bridge.SDLSurface;
import loon.cport.bridge.SDLWindowFlags;
import loon.events.InputMake;
import loon.geom.RectF;
import loon.utils.MathUtils;

public final class CGame extends LGame {

	private final static int SMOOTH_SAMPLES = 15;

	private final static int QUALITY_HIGH = 0;

	private final static int QUALITY_LOW = 1;

	public final static class FPSRepaintController {
		int[] frameTimes = new int[SMOOTH_SAMPLES];
		int targetFPS;
		int baseFPS;
		int lastTick;
		int delayAdjust;
		int fpsMeasure;
		int frameIndex;
		int frameCount;
		int quality;
	}

	public static enum GameSysPlatform {
		NONE, WIN, MAC, LINUX, XBOX, SWITCH, STREAM, PS, PSV, LUNA
	}

	public final static class CSetting extends LSetting {

		public boolean hidden = false;

		public boolean resizable = false;

		public boolean maximized = false;

		public boolean minimized = false;

		public boolean fullscreen = false;

		public boolean fullscreenDesktop = false;

		public boolean singleAppInstance = true;

		public boolean allowWindowClose = true;

		public boolean allowGamePad = true;

		public boolean vsync = true;

		public boolean powerOfTwoTexture = false;

		public boolean onlyOpenGL = false;

		public boolean onlyGamepad = false;

		public boolean convertGamepadToKeys = true;

		public int qualityModel = QUALITY_HIGH;

		public String iconPath = null;

		public String storageFileName = null;

		public String title = "";

		public GameSysPlatform gamePlatform = GameSysPlatform.NONE;

		public boolean isGamePlatform() {
			return (GameSysPlatform.XBOX == gamePlatform || GameSysPlatform.SWITCH == gamePlatform
					|| GameSysPlatform.STREAM == gamePlatform || GameSysPlatform.PS == gamePlatform
					|| GameSysPlatform.PSV == gamePlatform || GameSysPlatform.LUNA == gamePlatform);
		}

	}

	private final static FPSRepaintController _fpsRepaintController = new FPSRepaintController();
	private final static Support _support = new NativeSupport();
	private final SDLWindowFlags _flags = new SDLWindowFlags();
	protected final CSetting _csetting;
	protected final CLog _log;
	protected final Asyn _syn;
	protected final CAccelerometer _accelerometer;
	protected final CAssets _assets;
	protected final CGraphics _graphics;
	protected final CInputMake _input;
	protected final CSave _save;
	protected final CClipboard _clipboard;
	private final int _startTime;
	private final int[] _screenSize = new int[2];
	private final float[] _screenScale = new float[2];
	private final LazyLoading.Data _mainData;
	private final boolean _gamePlatform;
	private final RectF _renderScale = new RectF();
	private final int _fixedDelayValue;
	private final boolean _appLocked;
	private int _qualityLowValue;
	private int _qualityHighValue;
	private int _lastEventCall;

	public CGame(Loon loon, CSetting config, LazyLoading.Data mainData) {
		super(config, loon);
		setting = config;
		setting.updateScale();
		this._csetting = config;
		this._mainData = mainData;
		this._startTime = SDLCall.getTicks();
		if (_appLocked = _csetting.singleAppInstance) {
			boolean notexists = SDLCall.createAppLock();
			if (!notexists) {
				throw new RuntimeException("The App has been forcibly interrupted !");
			}
		}
		setWindowFlags(_csetting);
		SDLCall.screenInit(_csetting.title, _csetting.getShowWidth(), _csetting.getShowHeight(),
				_csetting.fps <= 60 ? _csetting.vsync : false, _csetting.emulateTouch, _flags.getValue(),
				_csetting.isDebug);
		setWindowIcon(_csetting);
		SDLCall.setAllowExit(_csetting.allowWindowClose);
		SDLCall.getRenderScale(_screenScale);
		this._fixedDelayValue = _csetting.fps_time_fixed_min_value;
		this._log = new CLog();
		this._syn = new Asyn.Default(_log, frame);
		this._accelerometer = new CAccelerometer();
		this._gamePlatform = _csetting.isGamePlatform();
		try {
			_graphics = new CGraphics(this);
			_assets = new CAssets(this);
			_clipboard = new CClipboard();
			_input = new CInputMake(this);
			_save = new CSave(_log, _csetting.storageFileName);
		} catch (Throwable e) {
			_log.error("init()", e);
			throw new RuntimeException(e);
		}
	}

	private void setWindowIcon(CSetting config) {
		final String path = config.iconPath;
		if (path != null && path.length() > 0) {
			try {
				String newPath = CAssets.requirePath(path);
				if (CAssets.existsPath(newPath)) {
					SDLSurface surface = SDLSurface.create(newPath);
					SDLCall.setWindowIcon(surface.getHandle());
					surface.close();
					surface = null;
				}
			} catch (Exception e) {
			}
		}
	}

	private void setWindowFlags(CSetting config) {
		if (config.onlyOpenGL) {
			_flags.onlyOpenGL();
			return;
		}
		if (config.resizable) {
			_flags.resize();
		}
		if (config.fullscreen) {
			_flags.full();
		}
		if (config.fullscreenDesktop) {
			_flags.fullDesktop();
		}
		if (config.minimized) {
			_flags.min();
		}
		if (config.maximized) {
			_flags.max();
		}
		if (config.hidden) {
			_flags.hide();
		} else {
			_flags.show();
		}
	}

	private static int getSmoothedFPS(FPSRepaintController fps) {
		int sum = 0;
		int count = fps.frameCount < SMOOTH_SAMPLES ? fps.frameCount : SMOOTH_SAMPLES;
		for (int i = 0; i < count; i++) {
			sum += fps.frameTimes[i];
		}
		if (sum == 0) {
			return fps.baseFPS;
		}
		return 1000 * count / sum;
	}

	public void setFPS(int baseFPS) {
		initFPSRepaintController(_fpsRepaintController, _csetting.qualityModel, baseFPS);
	}

	private void initFPSRepaintController(FPSRepaintController fps, int quality, int baseFPS) {
		fps.baseFPS = baseFPS;
		fps.targetFPS = baseFPS;
		fps.lastTick = SDLCall.getTicks();
		fps.delayAdjust = 0;
		fps.fpsMeasure = baseFPS;
		fps.frameIndex = 0;
		fps.frameCount = 0;
		fps.quality = quality;
		for (int i = 0; i < fps.frameTimes.length; i++) {
			fps.frameTimes[i] = 1000 / baseFPS;
		}
		this._qualityLowValue = MathUtils.iceil(baseFPS * 0.65f);
		this._qualityHighValue = MathUtils.iceil(baseFPS * 0.9f);
	}

	public void setSize(int width, int height) {
		_graphics.onSizeChanged(width, height);
	}

	private void initScreen() {
		if (!isRunning()) {
			initProcess();
			register(_mainData.onScreen());
			final int[] size = getScreenSize();
			setSize(size[0], size[1]);
			LSystem.PAUSED = false;
		}
	}

	public RectF getRenderScale() {
		if (!_renderScale.isEmpty()) {
			return _renderScale;
		}
		_renderScale.width = _screenScale[0];
		_renderScale.height = _screenScale[1];
		return _renderScale;
	}

	public int[] getScreenSize() {
		SDLCall.getDrawableSize(_screenSize);
		if (_screenSize[0] == 0 || _screenSize[1] == 0) {
			SDLCall.getCurrentWindowSize(_screenSize);
			if (_screenSize[0] == 0 || _screenSize[1] == 0) {
				SDLCall.getCurrentScreenSize(_screenSize);
			}
		}
		return _screenSize;
	}

	private void mainLoop(final FPSRepaintController fps, final int nowTime) {
		final int elapsed = nowTime - fps.lastTick;
		fps.lastTick = nowTime;
		fps.frameTimes[fps.frameIndex] = elapsed;
		fps.frameIndex = (fps.frameIndex + 1) % SMOOTH_SAMPLES;
		if (fps.frameCount < SMOOTH_SAMPLES) {
			fps.frameCount++;
		}
		fps.fpsMeasure = getSmoothedFPS(fps);
		if (fps.fpsMeasure < fps.targetFPS * 85 / 100) {
			fps.targetFPS -= 1;
			final int halfFPS = fps.targetFPS / 2;
			if (fps.targetFPS < halfFPS) {
				fps.targetFPS = halfFPS;
			}
		} else if (fps.fpsMeasure > fps.targetFPS * 105 / 100 && fps.targetFPS < fps.baseFPS) {
			fps.targetFPS += 1;
			if (fps.targetFPS > fps.baseFPS) {
				fps.targetFPS = fps.baseFPS;
			}
		}
		if (fps.fpsMeasure < _qualityLowValue && fps.quality == QUALITY_HIGH) {
			fps.quality = QUALITY_LOW;
		} else if (fps.fpsMeasure > _qualityHighValue && fps.quality == QUALITY_LOW) {
			fps.quality = QUALITY_HIGH;
		}
		final int targetMS = 1000 / fps.targetFPS;
		fps.delayAdjust += (targetMS - elapsed) / 4;
		if (fps.delayAdjust < 0) {
			fps.delayAdjust = 0;
		}
		emitFrame();
		if (fps.delayAdjust > 0) {
			SDLCall.delay(MathUtils.max(_fixedDelayValue, fps.delayAdjust));
		}
	}

	public void start() {
		if (isRunning()) {
			return;
		}
		initScreen();
		initFPSRepaintController(_fpsRepaintController, QUALITY_HIGH, MathUtils.iceil(LSystem.getFPS()));
		try {
			final boolean resize = _csetting.fullscreen || _csetting.resizable;
			int width = LSystem.viewSize.getZoomWidth(), height = LSystem.viewSize.getZoomHeight();
			int currentTime = 0;
			boolean paused = false;
			while (isRunning() && SDLCall.runSDLUpdate()) {
				_input.update();
				currentTime = SDLCall.getTicks();
				if (currentTime - _lastEventCall >= 1000) {
					if (resize) {
						final int newWidth = SDLCall.getWindowWidth();
						final int newHeight = SDLCall.getWindowHeight();
						if (newWidth != 0 && newHeight != 0) {
							final int currentWidth = width;
							final int currentHeight = height;
							if ((currentWidth != newWidth || currentHeight != newHeight)) {
								width = newWidth;
								height = newHeight;
								_graphics.onSizeChanged(width, height);
							}
						}
					}
					final boolean currentPaused = SDLCall.isPaused();
					if (paused && !currentPaused) {
						status.emit(Status.RESUME);
					} else if (!paused && currentPaused) {
						status.emit(Status.PAUSE);
					}
					paused = currentPaused;
					_lastEventCall = SDLCall.getTicks();
				}
				mainLoop(_fpsRepaintController, currentTime);
			}
		} catch (Exception e) {
			System.out.println("Loon Run Exception:");
			e.printStackTrace();
			System.err.println("Message: " + e.getMessage());
			Throwable cause = e.getCause();
			while (cause != null) {
				System.err.println("Message: " + cause.getMessage());
				cause = cause.getCause();
			}
		} finally {
			shutdown();
			if (_input != null) {
				_input.close();
			}
			SDLCall.cleanup();
			if (_appLocked) {
				SDLCall.freeAppLock();
			}
		}
	}

	public boolean isGamePlatform() {
		return _gamePlatform;
	}

	public boolean isSingleAppLocked() {
		return _appLocked;
	}

	@Override
	public boolean isCPort() {
		return true;
	}

	@Override
	public Type type() {
		if (_csetting != null) {
			switch (_csetting.gamePlatform) {
			case NONE:
			case WIN:
			case MAC:
			case LINUX:
			default:
				return Type.NativePC;
			case XBOX:
				return Type.NativeXBOX;
			case SWITCH:
				return Type.NativeSWITCH;
			case STREAM:
				return Type.NativeSTREAM;
			case PS:
				return Type.NativePS;
			}
		}
		return Type.STUB;
	}

	@Override
	public double time() {
		return SDLCall.getTicks();
	}

	@Override
	public int tick() {
		return (SDLCall.getTicks() - _startTime);
	}

	@Override
	public void openURL(String url) {
		SDLCall.openURL(url);
	}

	@Override
	public Assets assets() {
		return _assets;
	}

	@Override
	public Asyn asyn() {
		return _syn;
	}

	@Override
	public Graphics graphics() {
		return _graphics;
	}

	@Override
	public InputMake input() {
		return _input;
	}

	@Override
	public Clipboard clipboard() {
		return _clipboard;
	}

	@Override
	public Log log() {
		return _log;
	}

	@Override
	public Save save() {
		return _save;
	}

	@Override
	public Accelerometer accel() {
		return _accelerometer;
	}

	@Override
	public Support support() {
		return _support;
	}

}
