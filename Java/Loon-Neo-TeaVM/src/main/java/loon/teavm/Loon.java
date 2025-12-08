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

import java.util.HashMap;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;
import org.teavm.jso.browser.Location;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.css.CSSStyleDeclaration;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLImageElement;
import org.teavm.jso.dom.xml.Element;

import loon.LGame;
import loon.LSetting;
import loon.LSystem;
import loon.LazyLoading;
import loon.Platform;
import loon.canvas.LColor;
import loon.events.Updateable;
import loon.events.KeyMake.TextType;
import loon.events.SysInput.ClickEvent;
import loon.events.SysInput.TextEvent;
import loon.teavm.TeaGame.TeaSetting;
import loon.teavm.assets.AssetDownloadImpl;
import loon.teavm.assets.AssetDownloader;
import loon.teavm.assets.AssetLoadImpl;
import loon.teavm.assets.AssetLoader;
import loon.teavm.assets.AssetLoaderListener;
import loon.teavm.assets.AssetPreloader;
import loon.teavm.dom.HTMLDocumentExt;
import loon.utils.PathUtils;
import loon.utils.StringUtils;

public class Loon implements Platform {

	public enum OrientationLockType {
		LANDSCAPE("landscape"), PORTRAIT("portrait"), PORTRAIT_PRIMARY("portrait-primary"),
		PORTRAIT_SECONDARY("portrait-secondary"), LANDSCAPE_PRIMARY("landscape-primary"),
		LANDSCAPE_SECONDARY("landscape-secondary");

		private final String name;

		private OrientationLockType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	};

	public interface OrientationChangedHandler {

		void onChanged(Orientation newOrientation);

	}

	public static Loon register(LSetting setting, LazyLoading.Data lazy) {
		final Loon mainApp = new Loon(setting, lazy);
		mainApp.onMainLoop();
		return mainApp;
	}

	private static String cur_language = null;

	private static String cur_browserType = null;

	protected static Loon self;

	private HashMap<String, OrientationChangedHandler> _handlers = new HashMap<String, OrientationChangedHandler>();

	private Orientation _orientation;

	private int _currentHandlerId = 1;

	private HTMLCanvasElement _mainCanvasElement;

	protected TeaProgress _progress;

	protected AssetDownloadImpl _assetDownloader;

	protected AssetLoadImpl _assetLoader;

	protected AssetPreloader _preloader;

	protected TeaBase _baseWindow;

	protected LSetting _setting = null;

	protected TeaSetting _config = null;

	protected int _frameId = 0;

	private int _assetCount = 0;

	private TeaGame _game;

	private LazyLoading.Data _mainData = null;

	private Loon(LSetting setting, LazyLoading.Data lazy) {
		this._setting = setting;
		this._mainData = lazy;
	}

	protected void onMainLoop() {
		initTime();
		initRequestAnimFrame();
		_orientation = calculateScreenOrientation();
		try {
			TeaBase.get().addEventListener("orientationchange", new EventListener<Event>() {
				@Override
				public void handleEvent(Event evt) {

					_orientation = calculateScreenOrientation();
					for (OrientationChangedHandler handler : _handlers.values()) {
						handler.onChanged(getOrientation());
					}

				}
			});
		} catch (Exception e) {
			consoleLog("Does not support gets screen orientation .");
			_orientation = Orientation.Landscape;
		}
		if (this._setting instanceof TeaSetting) {
			_config = (TeaSetting) this._setting;
		} else {
			_config = new TeaSetting();
			_config.copy(this._setting);
		}
		_setting = _config;
		_baseWindow = TeaBase.get();
		_baseWindow.setTitle(_config.appName);
		_mainCanvasElement = createCanvas();
		setMainCanvasElement(_mainCanvasElement);
		setCanvasSize(_config.getShowWidth(), _config.getShowHeight(), _config.usePhysicalPixels);
		initProgress("assets.txt");
	}

	protected void initProgress(final String assetPath) {
		_preloader = new AssetPreloader();
		_assetDownloader = new AssetDownloadImpl(_config.showDownloadLog);
		_assetLoader = new AssetLoadImpl(_preloader, getBaseUrl(), this, _assetDownloader);
		initHowlerScript();
		_assetLoader.setupFileDrop(_mainCanvasElement, this);
		_assetLoader.preload(assetPath, new AssetLoaderListener<Void>() {
			@Override
			public void onSuccess(String url, Void result) {
				_assetCount = _assetLoader.getQueue();
				if (_assetCount > 0) {
					consoleLog("There are a total of " + _assetCount + " resource files.");
					_progress = new TeaProgress(getBaseUrl(), _config, 100);
					_progress.startTime();
					_frameId = _baseWindow.setInterval(new Runnable() {

						@Override
						public void run() {
							final int queue = _assetLoader.getQueue();
							_progress.update(_mainCanvasElement, ((float) (_assetCount - queue) / _assetCount));
							if (_progress.isCompleted() && !_assetLoader.isDownloading()) {
								_baseWindow.cancelInterval(_frameId);
								if (_mainCanvasElement != null) {
									mainLoop();
								}
							}
						}
					}, 16.67f);
				} else {
					mainLoop();
				}
			}

			@Override
			public void onFailure(String url) {
				consoleLog("The resource file " + assetPath + " does not exist.");
				mainLoop();
			}

		});

	}

	public HTMLCanvasElement getMainCanvas() {
		return _mainCanvasElement;
	}

	protected void setMainCanvasElement(HTMLCanvasElement canvas) {
		if (canvas != null) {
			_mainCanvasElement = canvas;
			setImageCanvasRendering(_mainCanvasElement);
		}
	}

	protected void setImageCanvasRendering(HTMLCanvasElement canvas) {
		if ("auto".equals(_config.canvasImageRendering)) {
			canvas.getStyle().setProperty("imageRendering", "auto");
		} else {
			canvas.getStyle().setProperty("imageRendering", "pixelated");
			if (StringUtils.isEmpty(canvas.getStyle().getPropertyValue("imageRendering"))) {
				canvas.getStyle().setProperty("imageRendering", "crisp-edges");
			}
		}
	}

	protected HTMLCanvasElement createCanvas() {
		HTMLDocumentExt document = _baseWindow.getDocument();
		HTMLElement canvasElement = document.getElementById(_config.canvasID);
		if (canvasElement != null) {
			return (HTMLCanvasElement) canvasElement;
		} else {
			HTMLElement div = TeaCanvasUtils.createDiv(document);
			canvasElement = document.createCanvasElement();
			canvasElement.setId(_config.canvasID);
			div.appendChild(canvasElement);
			document.getBody().appendChild(div);
			return (HTMLCanvasElement) canvasElement;
		}
	}

	protected void mainLoop() {
		TeaCanvasUtils.fillRect(_mainCanvasElement, LColor.black);
		createGame().start();
	}

	protected TeaGame createGame() {
		LSystem.freeStaticObject();
		Loon.self = this;
		return _game = new TeaGame(this, _config);
	}

	protected TeaProgress getProgress() {
		return _progress;
	}

	protected TeaGame initialize() {
		if (_game != null) {
			_game.register(_mainData.onScreen());
		}
		return _game;
	}

	public LSetting getSetting() {
		return _setting;
	}

	public TeaSetting getConfig() {
		return _config;
	}

	public AssetPreloader getPreloader() {
		return _preloader;
	}

	public AssetDownloader getAssetDownloader() {
		return _assetDownloader;
	}

	public AssetLoader getAssetLoader() {
		return _assetLoader;
	}

	public String getBaseUrl() {
		if (!StringUtils.isEmpty(_config.urlBase)) {
			return _config.urlBase;
		}
		Location location = _baseWindow.getLocation();
		String hostPageBaseURL = location.getFullURL();
		String ext = PathUtils.getExtension(hostPageBaseURL);
		if (StringUtils.isEmpty(ext)) {
			ext = "html";
		}
		if (hostPageBaseURL.contains("." + ext)) {
			hostPageBaseURL = hostPageBaseURL.replace("index." + ext, "");
			hostPageBaseURL = hostPageBaseURL.replace("index-wasm." + ext, "");
			hostPageBaseURL = hostPageBaseURL.replace("index-debug." + ext, "");
		}
		int indexQM = hostPageBaseURL.indexOf('?');
		if (indexQM >= 0) {
			hostPageBaseURL = hostPageBaseURL.substring(0, indexQM);
		}
		return hostPageBaseURL;
	}

	@JSBody(script = "var nav = window.navigator;\r\n" + "		var curLanguage = nav.language;\r\n"
			+ "		curLanguage = curLanguage ? curLanguage : nav.browserLanguage;\r\n"
			+ "		curLanguage = curLanguage ? curLanguage.split(\"-\")[0] : \"en\";\r\n"
			+ "		return curLanguage;")
	private static native String languageImpl();

	public static String language() {
		if (cur_language == null) {
			cur_language = languageImpl();
		}
		return cur_language;
	}

	@JSBody(script = "var ua = window.navigator.userAgent;\r\n" + "		var BROWSER_TYPE_WECHAT = \"wechat\";\r\n"
			+ "		var BROWSER_TYPE_ANDROID = \"androidbrowser\";\r\n" + "		var BROWSER_TYPE_IE = \"ie\";\r\n"
			+ "		var BROWSER_TYPE_360 = \"360browser\";\r\n" + "		var BROWSER_TYPE_MAXTHON = \"maxthon\";\r\n"
			+ "		var BROWSER_TYPE_OPERA = \"opera\";\r\n" + "		var BROWSER_TYPE_UNKNOWN = \"unknown\";\r\n"
			+ "		var typeReg1 = /sogou|qzone|liebao|micromessenger|ucbrowser|360 aphone|360browser|baiduboxapp|baidubrowser|maxthon|mxbrowser|trident|miuibrowser/i;\r\n"
			+ "		var typeReg2 = /qqbrowser|chrome|safari|firefox|opr|oupeng|opera/i;\r\n"
			+ "		var browserTypes = typeReg1.exec(ua);\r\n" + "		if (!browserTypes) {\r\n"
			+ "			browserTypes = typeReg2.exec(ua);\r\n" + "		}\r\n"
			+ "		var browserType = browserTypes ? browserTypes[0] : BROWSER_TYPE_UNKNOWN;\r\n"
			+ "		if (browserType === \"micromessenger\") {\r\n" + "			browserType = BROWSER_TYPE_WECHAT;\r\n"
			+ "		} else if (browserType === \"safari\"\r\n"
			+ "				&& (ua.match(/android.*applewebkit/))) {\r\n"
			+ "			browserType = BROWSER_TYPE_ANDROID;\r\n"
			+ "		} else if (browserType === \"trident\") {\r\n" + "			browserType = BROWSER_TYPE_IE;\r\n"
			+ "		} else if (browserType === \"360 aphone\") {\r\n" + "			browserType = BROWSER_TYPE_360;\r\n"
			+ "		} else if (browserType === \"mxbrowser\") {\r\n"
			+ "			browserType = BROWSER_TYPE_MAXTHON;\r\n" + "		} else if (browserType === \"opr\") {\r\n"
			+ "			browserType = BROWSER_TYPE_OPERA;\r\n" + "		}\r\n" + "		return browserType;")
	private static native String browserTypeImpl();

	public static String browserType() {
		if (cur_browserType == null) {
			cur_browserType = browserTypeImpl();
		}
		return cur_browserType;
	}

	@Override
	public int getContainerWidth() {
		int width = getAvailWidthJSNI() <= 0 ? getScreenWidthJSNI() : getAvailWidthJSNI();
		return width;
	}

	@Override
	public int getContainerHeight() {
		int height = getAvailHeightJSNI() <= 0 ? getScreenHeightJSNI() : getAvailHeightJSNI();
		return height;
	}

	@Override
	public Orientation getOrientation() {
		if (_orientation == Orientation.Landscape) {
			return Orientation.Landscape;
		}
		if (getScreenHeightJSNI() > getScreenWidthJSNI()) {
			return Orientation.Portrait;
		} else {
			return Orientation.Landscape;
		}
	}

	public TeaGame getHTMLGame() {
		return _game;
	}

	public boolean isFirefox() {
		return _game != null && _game.isFirefox();
	}

	public boolean isChrome() {
		return _game != null && _game.isChrome();
	}

	public boolean isEdge() {
		return _game != null && _game.isEdge();
	}

	public boolean isDesktop() {
		if (_game != null) {
			return _game.isDesktop();
		}
		return false;
	}

	public boolean isMobile() {
		if (_game != null) {
			return _game.isMobile();
		}
		return false;
	}

	@JSBody(script = "try {\r\n" + "const noop = () => {\r\n" + "return;\r\n" + "		    };\r\n"
			+ "window.top.addEventListener('blur', noop);\r\n"
			+ "		    window.top.removeEventListener('blur', noop);\r\n" + "} catch(ex) {\r\n"
			+ "		    return true;\r\n" + "}\r\n" + "return false;")
	public static native boolean isCrossOriginIframe();

	@JSBody(script = "return window !== window.top;")
	public static native boolean isIframe();

	protected void setCanvasSize(int width, int height, boolean usePhysicalPixels) {
		double density = 1;
		if (usePhysicalPixels) {
			density = getNativeScreenDensity();
		}
		int w = (int) (width * density);
		int h = (int) (height * density);
		_mainCanvasElement.setWidth(w);
		_mainCanvasElement.setHeight(h);
		if (usePhysicalPixels) {
			CSSStyleDeclaration style = _mainCanvasElement.getStyle();
			style.setProperty("width", width + "px");
			style.setProperty("height", height + "px");
		}
	}

	@JSBody(params = "jsObject", script = "return jsObject;")
	public static native String toString(JSObject jsObject);

	@JSBody(params = "object", script = "return URL.createObjectURL(object);")
	public static native String createObjectURL(JSObject object);

	@JSFunctor
	public interface RenderFrameHandler extends JSObject {
		void renderFrame(int delta);
	}

	@JSBody(script = "window.requestAnimFrame = (function() {\r\n" + "return window.requestAnimationFrame\r\n"
			+ "					|| window.webkitRequestAnimationFrame\r\n"
			+ "					|| window.mozRequestAnimationFrame\r\n"
			+ "					|| window.oRequestAnimationFrame\r\n"
			+ "					|| window.msRequestAnimationFrame\r\n"
			+ "					|| function stTime(callback, element) {\r\n"
			+ "						window.setTimeout(callback, 16);\r\n" + "					};\r\n"
			+ "		})();\r\n" + "	\r\n" + "		window.cancelAnimationFrame = (function() {\r\n"
			+ "			return window.cancelAnimationFrame = window.cancelAnimationFrame\r\n"
			+ "					|| window.cancelRequestAnimationFrame\r\n"
			+ "					|| window.msCancelRequestAnimationFrame\r\n"
			+ "					|| window.mozCancelRequestAnimationFrame\r\n"
			+ "					|| window.oCancelRequestAnimationFrame\r\n"
			+ "					|| window.webkitCancelRequestAnimationFrame\r\n"
			+ "					|| window.msCancelAnimationFrame\r\n"
			+ "					|| window.mozCancelAnimationFrame\r\n"
			+ "					|| window.webkitCancelAnimationFrame\r\n"
			+ "					|| window.oCancelAnimationFrame || function etime(id) {\r\n"
			+ "						window.clearTimeout(id);\r\n" + "					};\r\n" + "})();")
	private native static void initRequestAnimFrame();

	@JSBody(params = { "thandler" }, script = "window.requestAnimationFrame(thandler);")
	public native static void requestAnimationFrame(RenderFrameHandler aHandler);

	@JSBody(params = { "telement" }, script = "fullscreen(telement);")
	public native static void requestFullScreen(HTMLElement aElement);

	@Override
	public void close() {
		closeImpl();
	}

	private void fullscreenChanged() {
		if (!isFullscreen()) {
			if (_config.isFixedSize()) {
				_game.graphics().restoreSize();
			}
			if (_config.fullscreenOrientation != null)
				unlockOrientationJSNI();
		} else {
			if (_config.fullscreenOrientation != null) {
				lockOrientationJSNI(_config.fullscreenOrientation.getName());
			}
		}
	}

	public void setFullscreen(boolean f) {
		if (f) {
			enterFullscreen(getMainCanvas(), getScreenWidthJSNI(), getScreenHeightJSNI());
		} else {
			exitFullscreen();
		}
	}

	@JSBody(params = "orientationEnumValue", script = "\r\n" + "		var screen = window.screen;\r\n"
			+ "	screen.newLockOrientation = screen.lockOrientation\r\n"
			+ "|| screen.mozLockOrientation || screen.msLockOrientation\r\n" + "|| screen.webkitLockOrientation;\r\n"
			+ "		if (screen.newLockOrientation) {\r\n"
			+ "return screen.newLockOrientation(orientationEnumValue);\r\n"
			+ "} else if (screen.orientation && screen.orientation.lock) {\r\n"
			+ "screen.orientation.lock(orientationEnumValue);\r\n" + "			return true;\r\n" + "}\r\n"
			+ "		return false;")
	protected static native boolean lockOrientationJSNI(String orientationEnumValue);

	@JSBody(script = "var screen = window.screen;\r\n"
			+ "		screen.newUnlockOrientation = screen.unlockOrientation\r\n"
			+ "				|| screen.mozUnlockOrientation || screen.msUnlockOrientation\r\n"
			+ "				|| screen.webkitUnlockOrientation;\r\n" + "		if (screen.newUnlockOrientation) {\r\n"
			+ "			return screen.newUnlockOrientation();\r\n"
			+ "		} else if (screen.orientation && screen.orientation.unlock) {\r\n"
			+ "			screen.orientation.unlock();\r\n" + "			return true;\r\n" + "}\r\n"
			+ "		return false;")
	protected static native boolean unlockOrientationJSNI();

	public boolean isFocused() {
		return TeaBase.get().getDocument().getActiveElement() == _mainCanvasElement;
	}

	@JSBody(script = "return (\"ontouchstart\" in window) || navigator.maxTouchPoints > 0;")
	protected static native boolean isTouchScreen();

	@JSBody(script = "if (!Date.now) {\r\n" + "Date.now = function now() {\r\n" + "return +(new Date);\r\n" + "};\r\n"
			+ "}\r\n" + "return Date.now();")
	protected static native double startNow();

	@JSBody(script = "return Date.now();")
	protected static native double nowTime();

	@JSBody(params = { "elem", "state" }, script = "if ('crossOrigin' in elem)\r\n"
			+ "elem.setAttribute('crossOrigin', state);")
	protected static native void setCrossOrigin(HTMLElement elem, String state);

	@JSBody(params = "img", script = "return img.complete;")
	protected static native boolean isComplete(HTMLImageElement img);

	@JSBody(params = "msg", script = "if (typeof (window.alert) === \"function\") {\r\n"
			+ "        window.alert.call(null, msg); \r\n" + "    }\r\n" + "    else {\r\n"
			+ "        console.warn(\"alert is not a function\");\r\n" + "    };")
	private native static void alert(String msg);

	@JSBody(script = "window.close();")
	private native static void closeImpl();

	@JSBody(params = "msg", script = "if (window.console) {\r\n" + "window.console.log(msg);\r\n" + "} else {\r\n"
			+ "document.title = \"TeaVM Log:\" + msg;\r\n}")
	public native static void consoleLog(String msg);

	@JSBody(script = "Date.now = Date.now || function() {\r\n" + "			return new Date().getTime();\r\n"
			+ "		};\r\n" + "		window.performance = window.performance || {};\r\n"
			+ "		performance.now = (function() {\r\n"
			+ "			return performance.now || performance.mozNow || performance.msNow\r\n"
			+ "					|| performance.oNow || performance.webkitNow || Date.now;\r\n" + "		})();")
	private native static void initTime();

	@JSBody(script = "return window.screen.availWidth || 0;")
	protected native static int getAvailWidthJSNI();

	@JSBody(script = "return window.screen.availHeight || 0;")
	protected native static int getAvailHeightJSNI();

	public double getNativeScreenDensity() {
		return getNativeScreenDensityJSNI();
	}

	@JSBody(script = "var result = false;\r\n" + "	if (window.orientation != null && window.orientation == 0) {\r\n"
			+ "	result = true;\r\n" + "	}\r\n" + "	return result;")
	protected native static boolean isPortraitJSNI();

	@JSBody(script = "var result = false;\r\n" + "	if (window.orientation != null\r\n"
			+ "	&& (window.orientation == 90 || orientation == -90)) {\r\n" + "	result = true;\r\n" + "	}\r\n"
			+ "	return result;")
	protected native static boolean isLandscapeJSNI();

	public boolean noSupportOrientation() {
		return !isPortraitJSNI() && !isLandscapeJSNI();
	}

	private Orientation calculateScreenOrientation() {
		return isPortraitJSNI() ? Orientation.Portrait : Orientation.Landscape;
	}

	public String addHandler(OrientationChangedHandler handler) {
		int newHandlerIdValue = _currentHandlerId++;
		String newHandlerId = String.valueOf(newHandlerIdValue);
		_handlers.put(newHandlerId, handler);
		return newHandlerId;
	}

	@JSBody(script = "return window.webkitBackingStorePixelRatio || 1;")
	public native static float backingStorePixelRatio();

	public boolean supportsDisplayModeChange() {
		return supportsFullscreenJSNI();
	}

	@JSBody(script = "if (\"fullscreenEnabled\" in document) {\r\n" + "return document.fullscreenEnabled;\r\n" + "}\r\n"
			+ "if (\"webkitFullscreenEnabled\" in document) {\r\n" + "return document.webkitFullscreenEnabled;\r\n"
			+ "}\r\n" + "if (\"mozFullScreenEnabled\" in document) {\r\n" + "return document.mozFullScreenEnabled;\r\n"
			+ "}\r\n" + "if (\"msFullscreenEnabled\" in document) {\r\n" + "return document.msFullscreenEnabled;\r\n"
			+ "}\r\n" + "return false;")
	private native static boolean supportsFullscreenJSNI();

	public boolean isHdpi() {
		return getNativeScreenDensityJSNI() == 1.5;
	}

	public boolean isXhdpi() {
		return getNativeScreenDensityJSNI() > 1.5;
	}

	@JSBody(script = "return window.orientation || 0;")
	public native static float getOrientationValue();

	@JSBody(script = "return window.devicePixelRatio || 1;")
	private native static int getNativeScreenDensityJSNI();

	@JSBody(script = "return window.screen.width;")
	public native static int getScreenWidthJSNI();

	@JSBody(script = "return window.screen.height;")
	public native static int getScreenHeightJSNI();

	@JSBody(params = { "element", "fullscreenChanged" }, script = "" + "if (element.requestFullscreen) {\n"
			+ "   document.addEventListener(\"fullscreenchange\", fullscreenChanged, false);\n" + "}\n"
			+ "if (element.webkitRequestFullScreen) {\n"
			+ "   document.addEventListener(\"webkitfullscreenchange\", fullscreenChanged, false);\n" + "}\n"
			+ "if (element.mozRequestFullScreen) {\n"
			+ "   document.addEventListener(\"mozfullscreenchange\", fullscreenChanged, false);\n" + "}\n"
			+ "if (element.msRequestFullscreen) {\n"
			+ "   document.addEventListener(\"msfullscreenchange\", fullscreenChanged, false);\n" + "}")
	protected native static void addFullscreenChangeListener(HTMLCanvasElement element,
			FullscreenChanged fullscreenChanged);

	@JSFunctor
	public interface FullscreenChanged extends org.teavm.jso.JSObject {
		void fullscreenChanged();
	}

	public boolean enterFullscreen(HTMLCanvasElement element, int screenWidth, int screenHeight) {
		boolean result = enterFullscreenJSNI(element, screenWidth, screenHeight);
		fullscreenChanged();
		return result;
	}

	@JSBody(params = { "element", "screenWidth", "screenHeight" }, script = "" + "if (element.requestFullscreen) {\n"
			+ "   element.width = screenWidth;\n" + "   element.height = screenHeight;\n"
			+ "   element.requestFullscreen();\n" + "   return true;\n" + "}\n"
			+ "// Attempt to the vendor specific variants of the API\n" + "if (element.webkitRequestFullScreen) {\n"
			+ "   element.width = screenWidth;\n" + "   element.height = screenHeight;\n"
			+ "   element.webkitRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT);\n" + "   return true;\n" + "}\n"
			+ "if (element.mozRequestFullScreen) {\n" + "   element.width = screenWidth;\n"
			+ "   element.height = screenHeight;\n" + "   element.mozRequestFullScreen();\n" + "   return true;\n"
			+ "}\n" + "if (element.msRequestFullscreen) {\n" + "   element.width = screenWidth;\n"
			+ "   element.height = screenHeight;\n" + "   element.msRequestFullscreen();\n" + "   return true;\n"
			+ "}\n" + "\n" + "return false;")
	public static native boolean enterFullscreenJSNI(HTMLCanvasElement element, int screenWidth, int screenHeight);

	public void exitFullscreen() {
		exitFullscreenJSNI();
		fullscreenChanged();
	}

	@JSBody(script = "" + "if (document.exitFullscreen)\n" + "  document.exitFullscreen();\n"
			+ "if (document.msExitFullscreen)\n" + "  document.msExitFullscreen();\n"
			+ "if (document.webkitExitFullscreen)\n" + "  document.webkitExitFullscreen();\n"
			+ "if (document.mozExitFullscreen)\n" + "  document.mozExitFullscreen();\n"
			+ "if (document.webkitCancelFullScreen) // Old WebKit\n" + "  document.webkitCancelFullScreen();")
	private static native boolean exitFullscreenJSNI();

	public boolean isFullscreen() {
		return isFullscreenJSNI();
	}

	@JSBody(script = "if (\"fullscreenElement\" in document) {\n" + "  return document.fullscreenElement != null;\n"
			+ "}" + "if (\"msFullscreenElement\" in document) {\n" + "  return document.msFullscreenElement != null;\n"
			+ "}" + "if (\"webkitFullscreenElement\" in document) {\n"
			+ "  return document.webkitFullscreenElement != null;\n" + "}"
			+ "if (\"mozFullScreenElement\" in document) {\n" + "  return document.mozFullScreenElement != null;\n"
			+ "}" + "if (\"webkitIsFullScreen\" in document) {\n" + "  return document.webkitIsFullScreen;\n" + "}"
			+ "if (\"mozFullScreen\" in document) {\n" + "  return document.mozFullScreen;\n" + "}" + "return false;")
	public static native boolean isFullscreenJSNI();

	@JSBody(params = { "element", "url", "filename" }, script = "element.href = url;\r\n"
			+ "element.download = filename;")
	private static native void downloadFile(HTMLElement element, String url, String filename);

	@JSBody(script = "!!(document.pointerLockElement || document.webkitPointerLockElement || document.mozPointerLockElement);")
	public static native boolean isMouseLockedJSNI();

	@JSBody(script = "return \r\n" + "	document.exitPointerLock = document.exitPointerLock\r\n"
			+ "	|| document.webkitExitPointerLock || document.mozExitPointerLock;\r\n"
			+ "document.exitPointerLock && document.exitPointerLock();")
	public static native void unlockImpl();

	@JSBody(params = "element", script = "if (!element.requestPointerLock) {\n"
			+ "   element.requestPointerLock = (function() {\n"
			+ "       return element.webkitRequestPointerLock || element.mozRequestPointerLock;" + "   })();\n" + "}\n"
			+ "element.requestPointerLock();")
	public static native void setCursorCatchedJSNI(HTMLElement element);

	@JSBody(script = "document.exitPointerLock();")
	public static native void exitCursorCatchedJSNI();

	@JSBody(params = "canvas", script = "if (document.pointerLockElement === canvas || document.mozPointerLockElement === canvas) {\n"
			+ "   return true;\n" + "}\n" + "return false;")
	public static native boolean isCursorCatchedJSNI(HTMLElement canvas);

	@JSBody(script = "return ('ontouchstart' in document.documentElement)\r\n"
			+ "				|| (window.navigator.userAgent.match(/ipad|iphone|android/i) != null);")
	public static native boolean hasTouchJSNI();

	@JSBody(script = "return ('onmousedown' in document.documentElement)\r\n"
			+ "				&& (window.navigator.userAgent.match(/ipad|iphone|android/i) == null);")
	public static native boolean hasMouseJSNI();

	@JSBody(script = "return !!(document.body.requestPointerLock\r\n"
			+ "				|| document.body.webkitRequestPointerLock || document.body.mozRequestPointerLock);")
	public static native boolean hasMouseLockJSNI();

	@JSBody(params = "element", script = "element.requestPointerLock = (element.requestPointerLock\r\n"
			+ "	|| element.webkitRequestPointerLock || element.mozRequestPointerLock);\r\n"
			+ "	if (element.requestPointerLock)\r\n" + "	element.requestPointerLock();")
	public static native void requestMouseLockImplJSNI(Element element);

	@JSBody(params = "evt", script = "return !!evt.altKey;")
	public static native boolean eventGetAltKeyJSNI(Event evt);

	@JSBody(params = "evt", script = "return !!evt.ctrlKey;")
	public static native boolean eventGetCtrlKeyJSNI(Event evt);

	@JSBody(params = "evt", script = "return !!evt.shiftKey;")
	public static native boolean eventGetShiftKeyJSNI(Event evt);

	@JSBody(params = "evt", script = "return !!evt.metaKey;")
	public static native boolean eventGetMetaKeyJSNI(Event evt);

	protected static void download(String fileName, String url) {
		HTMLElement docAhref = TeaBase.get().getDocument().createElement("a");
		downloadFile(docAhref, url, fileName);
		docAhref.click();
		docAhref.setDir(url);
	}

	protected static void downloadText(String fileName, String text) {
		download(fileName, "data:text/plain;charset=utf-8," + text);
	}

	protected void initHowlerScript() {
		_assetLoader.loadScript("howler.js", new AssetLoaderListener<String>() {
			@Override
			public void onSuccess(String url, String result) {
				consoleLog("JavaScript loaded success :" + url);
			}
		});
	}

	@Override
	public LGame getGame() {
		return _game;
	}

	@Override
	public void sysText(final TextEvent event, final TextType textType, final String label, final String initialValue) {
		if (_game == null) {
			event.cancel();
			return;
		}
		LSystem.load(new Updateable() {

			@Override
			public void action(Object a) {

				String result = Window.prompt(label, initialValue);
				if (_game.input() instanceof TeaInputMake) {
					((TeaInputMake) _game.input()).emitFakeMouseUp();
				}
				if (result != null) {
					event.input(result);
				} else {
					event.cancel();
				}
			}
		});

	}

	@Override
	public void sysDialog(final ClickEvent event, final String title, final String text, final String ok,
			final String cancel) {
		if (_game == null) {
			event.cancel();
			return;
		}
		LSystem.load(new Updateable() {

			@Override
			public void action(Object a) {
				boolean result;
				if (cancel != null)
					result = Window.confirm(text);
				else {
					Window.alert(text);
					result = true;
				}
				if (_game.input() instanceof TeaInputMake) {
					((TeaInputMake) _game.input()).emitFakeMouseUp();
				}
				if (result) {
					event.clicked();
				} else {
					event.cancel();
				}
			}
		});

	}
}
