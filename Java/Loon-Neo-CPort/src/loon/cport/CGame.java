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
import loon.Log;
import loon.Save;
import loon.Support;
import loon.cport.bridge.NativeSupport;
import loon.cport.bridge.SDLCall;
import loon.cport.bridge.SDLSurface;
import loon.cport.bridge.SDLWindowFlags;
import loon.events.InputMake;
import loon.utils.MathUtils;

public class CGame extends LGame {

	public static enum GameSysPlatform {
		NONE, WIN, MAC, LINUX, XBOX, SWITCH, STREAM, PS
	}

	public static class CSetting extends LSetting {

		public boolean resizable = false;

		public boolean maximized = false;

		public boolean minimized = false;

		public boolean fullscreen = false;

		public boolean fullscreenDesktop = false;

		public boolean autoIconify = true;

		public boolean vsync = true;

		public String iconPath = null;

		public String storageFileName = null;

		public String title = "";

		public GameSysPlatform gamePlatform = GameSysPlatform.NONE;

		public boolean isGamePlatform() {
			return (GameSysPlatform.XBOX == gamePlatform || GameSysPlatform.SWITCH == gamePlatform
					|| GameSysPlatform.STREAM == gamePlatform || GameSysPlatform.PS == gamePlatform);
		}

	}

	private final static Support _support = new NativeSupport();

	private final SDLWindowFlags _flags = new SDLWindowFlags();
	private final CSetting _csetting;
	private final CLog _log;
	private final Asyn _syn;
	private final CAccelerometer _accelerometer;
	private final CAssets _assets;
	private final CGraphics _graphics;
	private final CInputMake _input;
	private final CSave _save;
	private final CClipboard _clipboard;
	private final int _startTime;

	private boolean _running;

	public CGame(Loon loon, CSetting config) {
		super(config, loon);
		setting = config;
		setting.updateScale();
		setWindowFlags(_csetting = config);
		SDLCall.screenInit(_csetting.title, _csetting.getShowWidth(), _csetting.getShowHeight(), _csetting.vsync,
				_flags.getValue(), _csetting.isDebug);
		setWindodIcon(_csetting);
		this._startTime = SDLCall.getTicks();
		this._log = new CLog();
		this._syn = new Asyn.Default(_log, frame);
		this._accelerometer = new CAccelerometer();
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

	private void setWindodIcon(CSetting config) {
		final String path = config.iconPath;
		if (path != null && path.length() > 0) {
			try {
				SDLSurface surface = SDLSurface.create(path);
				SDLCall.setWindowIcon(surface.getHandle());
				surface.close();
				surface = null;
			} catch (Exception e) {
			}
		}
	}

	private void setWindowFlags(CSetting config) {
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
	}

	public void start() {
		_running = true;
		final boolean gamePlatform = _csetting.isGamePlatform();
		final int targetFps = MathUtils.iceil(LSystem.getFPS());
		final int frameDelay = 1000 / targetFps;
		initProcess();
		try {
			int width = _csetting.getShowWidth(), height = _csetting.getShowHeight();
			boolean resize = _csetting.fullscreen || _csetting.resizable;
			boolean paused = false;
			while (_running) {
				final int frameStart = SDLCall.getTicks();
				if (resize) {
					final int[] size = gamePlatform ? SDLCall.getCurrentScreenSize() : SDLCall.getCurrentWindowSize();
					if (size[0] != 0 && size[1] != 0) {
						final int currentWidth = width;
						final int currentHeight = height;
						if ((currentWidth != size[0] || currentHeight != size[1])) {
							width = currentWidth;
							height = currentHeight;
							_graphics.onSizeChanged(width, height);
						}
					}
				} else {
					final int currentWidth = width;
					final int currentHeight = height;
					if ((currentWidth != LSystem.viewSize.getZoomWidth()
							|| currentHeight != LSystem.viewSize.getZoomHeight())) {
						width = currentWidth;
						height = currentHeight;
						_graphics.onSizeChanged(width, height);
					}
				}
				final boolean currentPaused = SDLCall.isPaused();
				if (paused && !currentPaused) {
					status.emit(Status.RESUME);
				} else if (!paused && currentPaused) {
					status.emit(Status.PAUSE);
				}
				paused = currentPaused;
				_input.update();
				emitFrame();
				int frameTime = SDLCall.getTicks() - frameStart;
				if (frameTime < frameDelay) {
					SDLCall.delay(frameDelay - frameTime);
				}
			}
		} catch (Exception e) {
			System.out.println("Loon Exception:");
			e.printStackTrace();
			System.err.println("Message: " + e.getMessage());
			Throwable cause = e.getCause();
			while (cause != null) {
				System.err.println("Message: " + cause.getMessage());
				cause = cause.getCause();
			}
		}
		shutdown();
	}

	@Override
	public void shutdown() {
		super.shutdown();
		_running = false;
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
