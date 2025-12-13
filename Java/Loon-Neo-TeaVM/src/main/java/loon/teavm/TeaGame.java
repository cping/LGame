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
package loon.teavm;

import java.util.ArrayList;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSExceptions;
import org.teavm.jso.JSObject;
import org.teavm.jso.browser.Window;

import loon.Asyn;
import loon.LGame;
import loon.LSetting;
import loon.LSystem;
import loon.Support;
import loon.geom.Vector2f;
import loon.jni.NativeSupport;
import loon.teavm.Loon.OrientationLockType;
import loon.utils.StringUtils;

public class TeaGame extends LGame {

	private static final int MIN_DELAY = 5;

	public static class TeaSetting extends LSetting {

		public String canvasID = "maincanvas";

		public String powerPreference = "high-performance";

		public String canvasImageRendering = "auto";

		public String urlBase = null;

		public boolean showDownloadLog = true;

		public boolean usePhysicalPixels = false;

		public boolean transparentCanvas = false;

		public boolean antiAliasing = true;

		public boolean stencil = false;

		public boolean premultipliedAlpha = false;

		public boolean preserveDrawingBuffer = false;

		public boolean useRatioScaleFactor = false;

		public OrientationLockType fullscreenOrientation;

		public TeaWindowListener windowListener;

		public boolean isFixedSize() {
			return width <= 0 && height <= 0;
		}

		public void notAllowResize() {
			fullscreen = allowScreenResize = false;
		}
	}

	public static enum Mode {
		WEBGL, CANVAS, AUTODETECT;
	}

	private final static Support support = new NativeSupport();

	private static final TeaAgentInfo agentInfo = TeaWebAgent.computeAgentInfo();

	private final double start;

	private final TeaSetting teaconfig;

	private final TeaLog log;
	private final Asyn syn;
	private final TeaAccelerometer accelerometer;
	private final TeaAssets assets;

	private final TeaGraphics graphics;
	private final TeaInputMake input;
	private final TeaSave save;
	private final TeaClipboard clipboard;

	private final Vector2f offsetPos = new Vector2f();

	private final Loon loonApp;

	private boolean _initTea = false;

	private TeaBase _teaWindow;

	private String _userAgent;

	private int _frameID = -1;

	public TeaGame(Loon loon, TeaSetting config) {
		super(config, loon);
		this.loonApp = loon;
		this._teaWindow = loonApp._baseWindow;
		this.teaconfig = config;
		this.setting = config;
		this.start = initNow();
		this.log = new TeaLog(this);
		this.syn = new Asyn.Default(log, frame);
		this.accelerometer = new TeaAccelerometer();
		log.info("Browser orientation: " + loonApp.getOrientation());
		log.info("Browser screen width: " + loonApp.getContainerWidth() + ", screen height: "
				+ loonApp.getContainerHeight());
		log.info("devicePixelRatio: " + loonApp.getNativeScreenDensity() + " backingStorePixelRatio: "
				+ Loon.backingStorePixelRatio());
		if (config.useRatioScaleFactor) {
			int width = setting.width;
			int height = setting.height;
			double scale = loonApp.getNativeScreenDensity();
			width *= scale;
			height *= scale;
			setting.width_zoom = width;
			setting.height_zoom = height;
			setting.updateScale();
		} else {
			setting.updateScale();
		}
		try {
			graphics = new TeaGraphics(loon, this, config);
			assets = new TeaAssets(this, syn);
			clipboard = new TeaClipboard();
			input = new TeaInputMake(this, graphics.getCanvas());
			save = new TeaSave(this);
		} catch (Throwable e) {
			log.error("init()", e);
			Loon.alert("failed to init(): " + e.getMessage());
			throw new RuntimeException(e);
		}
		this.initProcess();
	}

	public Vector2f getOffsetPos() {
		return offsetPos;
	}

	public TeaGame setOffsetPos(float x, float y) {
		offsetPos.set(x, y);
		return this;
	}

	public TeaSetting getSetting() {
		return teaconfig;
	}

	public void init() {
		if (!_initTea) {
			if (loonApp != null) {
				loonApp.initialize();
				LSystem.PAUSED = false;
				_initTea = true;
			}
		}
	}

	public void start() {
		if (this._initTea) {
			return;
		}
		init();
		final int defFps = loonApp._setting.fps;
		final Runnable gameLoop = new Runnable() {

			@Override
			public void run() {
				if (!_initTea) {
					return;
				}
				try {
					_frameID = requestAnimationFrame(defFps, this);
					emitFrame();
				} catch (Exception e) {
					_teaWindow.cancelAnimationFrame(_frameID);
					throw e;
				}
			}
		};
		_frameID = requestAnimationFrame(defFps, gameLoop);
	}

	private int requestAnimationFrame(float frameRate, Runnable callback) {
		if (frameRate < 60) {
			return _teaWindow.setTimeout(callback, 1000 / frameRate);
		} else {
			return _teaWindow.requestAnimationFrame(callback);
		}
	}

	@Override
	public boolean isRunning() {
		return this._initTea && super.isRunning();
	}

	private void cancelLoop() {
		_teaWindow.cancelLoop(_frameID);
		_initTea = false;
	}

	@Override
	public void shutdown() {
		super.shutdown();
		_initTea = false;
	}

	protected void onError(Throwable error) {
		final ArrayList<JSObject> errors = new ArrayList<JSObject>();
		final ArrayList<String> throwables = new ArrayList<String>();
		Throwable root = error;
		while (root != null) {
			JSObject jsException = JSExceptions.getJSException(root);
			errors.add(jsException);
			String msg = root.getMessage();
			if (msg == null) {
				msg = "";
			}
			throwables.add(root.getClass().getSimpleName() + " " + msg);
			root = root.getCause();
		}
		final int errsize = errors.size();
		if (errsize > 0) {
			JSObject[] errorsJS = new JSObject[errsize];
			String[] exceptions = new String[errsize];
			errors.toArray(errorsJS);
			throwables.toArray(exceptions);
			printStack(errorsJS, exceptions);
		}
	}

	@JSBody(script = "if (!Date.now)\r\n" + "Date.now = function now() {\r\n" + "return +(new Date);\r\n" + "};\r\n"
			+ "return Date.now();")
	private static native double initNow();

	@JSBody(params = { "errors", "exceptions" }, script = ""
			+ "console.groupCollapsed('%cFatal Error', 'color: #FF0000');" + "errors.forEach((error, i) => {\n"
			+ "   var count = i + 1;"
			+ "   console.log('%cException ' + count + ': ' + exceptions[i], 'color: #FF0000');"
			+ "   console.log(error);" + "});" + "console.groupEnd();")
	private static native void printStack(JSObject[] errors, String[] exceptions);

	@Override
	public Type type() {
		return Type.HTML5;
	}

	@Override
	public double time() {
		return Loon.nowTime();
	}

	@Override
	public int tick() {
		return (int) (Loon.nowTime() - start);
	}

	@Override
	public void openURL(String url) {
		Window.current().open(url, "_blank", "");
	}

	@Override
	public Asyn asyn() {
		return syn;
	}

	@Override
	public TeaAccelerometer accel() {
		return accelerometer;
	}

	@Override
	public TeaAssets assets() {
		return assets;
	}

	@Override
	public TeaGraphics graphics() {
		return graphics;
	}

	@Override
	public TeaInputMake input() {
		return input;
	}

	@Override
	public TeaLog log() {
		return log;
	}

	@Override
	public TeaSave save() {
		return save;
	}

	@Override
	public TeaClipboard clipboard() {
		return clipboard;
	}

	@Override
	public Support support() {
		return support;
	}

	public String getUserAgent() {
		if (!StringUtils.isEmpty(_userAgent)) {
			return _userAgent;
		}
		return _userAgent = agentInfo.getUserAgent().toLowerCase();
	}

	@Override
	public boolean isMobile() {
		if (loonApp == null) {
			return false;
		}
		return super.isMobile() || isAndroid() || isIOS() || isOpenHarmony() || isWindowsPhone() || isWeiXin()
				|| isBlackBerry() || getUserAgent().contains("mobile");

	}

	public boolean isWindows() {
		return getUserAgent().contains("win");
	}

	public boolean isMac() {
		return getUserAgent().contains("mac");
	}

	public boolean isLinux() {
		return getUserAgent().contains("linux");
	}

	public boolean isBrowser() {
		return isChrome() || isFirefox() || isSafari() || isEdge() || isQQBrowser() || isMQQBrowser() || isWeiXin();
	}

	public boolean isChrome() {
		return getUserAgent().contains("chrome");
	}

	public boolean isFirefox() {
		return getUserAgent().contains("firefox");
	}

	public boolean isSafari() {
		return getUserAgent().contains("safari") && !isChrome();
	}

	public boolean isEdge() {
		return getUserAgent().contains("edge") || getUserAgent().contains("edg");
	}

	public boolean isWeiXin() {
		return getUserAgent().contains("micromessenger");
	}

	public boolean isOpenHarmony() {
		return getUserAgent().contains("openharmony");
	}

	public boolean isQQBrowser() {
		return getUserAgent().contains("qqbrowser");
	}

	public boolean isMQQBrowser() {
		final String result = getUserAgent();
		return result.contains("mqqbrowser") || (result.indexOf("mobile") > -1 && result.indexOf("qq") > -1);
	}

	public boolean isWindowsPhone() {
		return getUserAgent().contains("windows phone");
	}

	public boolean isAndroid() {
		return isAndroidPhone() || isAndroidTablet() || getUserAgent().contains("adr");
	}

	public boolean isIPhone() {
		String userAgent = getUserAgent();
		if (userAgent.contains("iphone") && loonApp.getNativeScreenDensity() < 2) {
			return true;
		}
		return false;
	}

	public boolean isIPad() {
		String userAgent = getUserAgent();
		if (userAgent.contains("ipad") && loonApp.getNativeScreenDensity() < 2) {
			return true;
		}
		return false;
	}

	public boolean isIOS() {
		return isIPad() || isIPadRetina() || isIPhone() || isRetina() || isIOSPhone();
	}

	public boolean isIOSPhone() {
		String userAgent = getUserAgent();
		return userAgent.matches("/\\(i[^;]+;( U;)? CPU.+Mac OS X/");
	}

	public boolean isRetina() {
		String userAgent = getUserAgent();
		if (userAgent.contains("iphone") && loonApp.getNativeScreenDensity() >= 2) {
			return true;
		}
		return false;
	}

	public boolean isIPadRetina() {
		String userAgent = getUserAgent();
		if (userAgent.contains("ipad") && loonApp.getNativeScreenDensity() >= 2) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isDesktop() {
		return (isWindows() || isMac() || isLinux()) && (!isIOS() && !isAndroid() && !isWindowsPhone()
				&& !isBlackBerry() && !isOpenHarmony() && !getUserAgent().contains("mobile"));
	}

	public boolean isTablet() {
		return isIPad() || isIPadRetina() || isAndroidTablet();
	}

	public boolean isAndroidTablet() {
		String userAgent = getUserAgent();
		if (userAgent.contains("android") && !userAgent.contains("mobile")) {
			return true;
		}
		return false;
	}

	public boolean isAndroidPhone() {
		String userAgent = getUserAgent();
		if (userAgent.contains("android") && userAgent.contains("mobile")) {
			return true;
		}
		return false;
	}

	public boolean isPhone() {
		return isIPhone() || isRetina() || isAndroidPhone();
	}

	public boolean isBlackBerry() {
		String userAgent = getUserAgent();
		if (userAgent.contains("blackberry")) {
			return true;
		}
		return false;
	}

}
