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
import org.teavm.jso.dom.html.HTMLCanvasElement;

import loon.Asyn;
import loon.LGame;
import loon.LSetting;
import loon.LSystem;
import loon.Support;
import loon.jni.NativeSupport;

public class TeaGame extends LGame {

	private static final int MIN_DELAY = 5;

	public static class TeaSetting extends LSetting {

		public String divName = "div";
		
		public String imageName = "img";

		public String canvasName = "canvas";

		public String canvasMethod = "2d";

		public String webglMethod = "webgl";

		public String canvasID = "maincanvas";

		public String powerPreference = "high-performance";

		public String urlBase = null;

		public boolean showDownloadLog = false;

		public boolean usePhysicalPixels = false;

		// 当前浏览器的渲染模式
		public Mode mode = Mode.AUTODETECT;

		// 当此项存在时，会尝试加载内部资源
		// public LocalAssetResources internalRes = null;

		// 当此项存在时，同样会尝试加载内部资源
		public boolean jsloadRes = false;

		public boolean transparentCanvas = false;

		public boolean antiAliasing = true;

		public boolean stencil = false;

		public boolean premultipliedAlpha = false;

		public boolean preserveDrawingBuffer = false;

		// 如果此项开启，按照屏幕大小等比缩放
		public boolean useRatioScaleFactor = false;

		// 如果此项为true,则仅以异步加载资源
		public boolean asynResource = false;
		public TeaWindowListener windowListener;
	}

	public static enum Mode {
		WEBGL, CANVAS, AUTODETECT;
	}

	private final static Support support = new NativeSupport();

	static final TeaAgentInfo agentInfo = TeaWebAgent.computeAgentInfo();

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

	private final Loon loonApp;
	private boolean initTea = false;
	private TeaBase teaWindow;

	private int _frameID = 0;

	public TeaGame(Loon loon, TeaSetting config) {
		super(config, loon);
		this.loonApp = loon;
		this.teaWindow = loonApp._baseWindow;
		this.teaconfig = config;
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
			final HTMLCanvasElement rootCanvas = loonApp.getMainCanvas();
			graphics = new TeaGraphics(rootCanvas, this, config);
			assets = new TeaAssets(this, syn);
			clipboard = new TeaClipboard();
			input = new TeaInputMake(this, rootCanvas);
			save = new TeaSave(this);
		} catch (Throwable e) {
			log.error("init()", e);
			Window.alert("failed to init(): " + e.getMessage());
			throw new RuntimeException(e);
		}
		this.initProcess();
	}

	public TeaSetting getSetting() {
		return teaconfig;
	}

	public void init() {
		if (!initTea) {
			if (loonApp != null) {
				loonApp.initialize();
				LSystem.PAUSED = false;
				initTea = true;
			}
		}
	}

	public void start() {
		init();
		_frameID = requestAnimationFrame(loonApp._setting.fps, new Runnable() {

			@Override
			public void run() {
				_frameID = requestAnimationFrame(loonApp._setting.fps, this);
				emitFrame();
			}
		});
	}

	private int requestAnimationFrame(float frameRate, Runnable callback) {
		if (frameRate < 60) {
			return teaWindow.setTimeout(callback, 1000 / frameRate);
		} else {
			return teaWindow.requestAnimationFrame(callback);
		}
	}

	private void cancelLoop() {
		teaWindow.cancelLoop(_frameID);
	}

	protected void onError(Throwable error) {
		ArrayList<JSObject> errors = new ArrayList<JSObject>();
		ArrayList<String> throwables = new ArrayList<String>();
		Throwable root = error;
		while (root != null) {
			JSObject jsException = JSExceptions.getJSException(root);
			errors.add(jsException);
			String msg = root.getMessage();
			if (msg == null)
				msg = "";
			throwables.add(root.getClass().getSimpleName() + " " + msg);
			root = root.getCause();
		}
		JSObject[] errorsJS = new JSObject[errors.size()];
		String[] exceptions = new String[errors.size()];
		errors.toArray(errorsJS);
		throwables.toArray(exceptions);
		printStack(errorsJS, exceptions);
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

	@Override
	public boolean isMobile() {
		if (loonApp == null) {
			return false;
		}
		return super.isMobile() || isAndroid() || isIOS() || isBlackBerry()
				|| agentInfo.getUserAgent().contains("mobile");
	}

	public boolean isAndroid() {
		return isAndroidPhone() || isAndroidTablet();
	}

	public boolean isIPhone() {
		String userAgent = agentInfo.getUserAgent();
		if (userAgent.contains("iphone") && loonApp.getNativeScreenDensity() < 2) {
			return true;
		}
		return false;
	}

	public boolean isIPad() {
		String userAgent = agentInfo.getUserAgent();
		if (userAgent.contains("ipad") && loonApp.getNativeScreenDensity() < 2) {
			return true;
		}
		return false;
	}

	public boolean isIOS() {
		return isIPad() || isIPadRetina() || isIPhone() || isRetina();
	}

	public boolean isRetina() {
		String userAgent = agentInfo.getUserAgent();
		if (userAgent.contains("iphone") && loonApp.getNativeScreenDensity() >= 2) {
			return true;
		}
		return false;
	}

	public boolean isIPadRetina() {
		String userAgent = agentInfo.getUserAgent();
		if (userAgent.contains("ipad") && loonApp.getNativeScreenDensity() >= 2) {
			return true;
		}
		return false;
	}

	public boolean isDesktop() {
		return !isIOS() && !isAndroid() && !isBlackBerry() && !agentInfo.getUserAgent().contains("mobile");
	}

	public boolean isTablet() {
		return isIPad() || isIPadRetina() || isAndroidTablet();
	}

	public boolean isAndroidTablet() {
		String userAgent = agentInfo.getUserAgent();
		if (userAgent.contains("android") && !userAgent.contains("mobile")) {
			return true;
		}
		return false;
	}

	public boolean isAndroidPhone() {
		String userAgent = agentInfo.getUserAgent();
		if (userAgent.contains("android") && userAgent.contains("mobile")) {
			return true;
		}
		return false;
	}

	public boolean isPhone() {
		return isIPhone() || isRetina() || isAndroidPhone();
	}

	public boolean isBlackBerry() {
		String userAgent = agentInfo.getUserAgent();
		if (userAgent.contains("blackberry")) {
			return true;
		}
		return false;
	}

}
