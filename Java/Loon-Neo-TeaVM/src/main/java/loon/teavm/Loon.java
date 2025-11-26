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

import loon.LGame;
import loon.LSetting;
import loon.LSystem;
import loon.LazyLoading;
import loon.Platform;
import loon.events.Updateable;
import loon.events.KeyMake.TextType;
import loon.events.SysInput.ClickEvent;
import loon.events.SysInput.TextEvent;
import loon.teavm.TeaGame.TeaSetting;
import loon.teavm.assets.AssetDownloadImpl;
import loon.teavm.assets.AssetDownloader;
import loon.teavm.assets.AssetLoadImpl;
import loon.teavm.assets.AssetLoader;
import loon.teavm.assets.AssetPreloader;
import loon.teavm.dom.HTMLDocumentExt;
import loon.utils.PathUtils;
import loon.utils.StringUtils;

public class Loon implements Platform {

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

	private HashMap<String, OrientationChangedHandler> _handlers = new HashMap<String, OrientationChangedHandler>();

	private Orientation _orientation;

	private int _currentHandlerId = 1;

	private HTMLCanvasElement _mainCanvasElement;

	private AssetDownloadImpl _assetDownloader;
	private AssetLoadImpl _assetLoader;
	private AssetPreloader _preloader;

	protected TeaProgress _progress;

	protected static Loon self;

	private TeaBase _baseWindow;

	private TeaGame _game;

	private LazyLoading.Data _mainData = null;

	protected LSetting _setting = null;

	protected TeaSetting _config = null;

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
		setCanvasSize(_config.getShowHeight(), _config.getShowHeight(), _config.usePhysicalPixels);
		_preloader = new AssetPreloader();
		_assetDownloader = new AssetDownloadImpl(_config.showDownloadLog);
		_assetLoader = new AssetLoadImpl(_preloader, getBaseUrl(), this, _assetDownloader);
		_progress = new TeaProgress(this, _config, 100);
		_assetLoader.setupFileDrop(_mainCanvasElement = createCanvas(), this);
	}

	public HTMLCanvasElement getMainCanvas() {
		return _mainCanvasElement;
	}

	protected HTMLCanvasElement createCanvas() {
		HTMLDocumentExt document = _baseWindow.getDocument();
		HTMLElement elementID = document.getElementById(_config.canvasID);
		return (HTMLCanvasElement) elementID;
	}

	protected void mainLoop() {
		this.createGame().start();
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
	private native void initRequestAnimFrame();

	@JSBody(params = { "thandler" }, script = "window.requestAnimationFrame(thandler);")
	public native static void requestAnimationFrame(RenderFrameHandler aHandler);

	@JSBody(params = { "telement" }, script = "fullscreen(telement);")
	public native static void requestFullScreen(HTMLElement aElement);

	@Override
	public void close() {
		closeImpl();
	}

	public void setFullscreen(boolean f) {
		if (f) {
			enterFullscreen(_mainCanvasElement, getScreenWidthJSNI(), getScreenHeightJSNI());
		} else {
			exitFullscreen();
		}
	}

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

	@JSBody(params = "img", script = "return img.complete;")
	protected static native boolean isComplete(HTMLImageElement img);

	@JSBody(params = "img", script = "img.complete = true;")
	protected static native void setComplete(HTMLImageElement img);

	@JSBody(params = "msg", script = "if (typeof (window.alert) === \"function\") {\r\n"
			+ "        window.alert.call(null, msg); \r\n" + "    }\r\n" + "    else {\r\n"
			+ "        console.warn(\"alert is not a function\");\r\n" + "    };")
	private native void alert(String msg);

	@JSBody(script = "window.close();")
	private static native void closeImpl();

	@JSBody(params = "msg", script = "if (window.console) {\r\n" + "window.console.log(msg);\r\n" + "} else {\r\n"
			+ "document.title = \"TeaVM Log:\" + msg;\r\n}")
	public native static void consoleLog(String msg);

	@JSBody(script = "Date.now = Date.now || function() {\r\n" + "			return new Date().getTime();\r\n"
			+ "		};\r\n" + "		window.performance = window.performance || {};\r\n"
			+ "		performance.now = (function() {\r\n"
			+ "			return performance.now || performance.mozNow || performance.msNow\r\n"
			+ "					|| performance.oNow || performance.webkitNow || Date.now;\r\n" + "		})();")
	private static native void initTime();

	@JSBody(script = "return window.screen.availWidth || 0;")
	protected native int getAvailWidthJSNI();

	@JSBody(script = "return window.screen.availHeight || 0;")
	protected native int getAvailHeightJSNI();

	public double getNativeScreenDensity() {
		return getNativeScreenDensityJSNI();
	}

	@JSBody(script = "var result = false;\r\n" + "	if (window.orientation != null && window.orientation == 0) {\r\n"
			+ "	result = true;\r\n" + "	}\r\n" + "	return result;")
	protected native boolean isPortraitJSNI();

	@JSBody(script = "var result = false;\r\n" + "	if (window.orientation != null\r\n"
			+ "	&& (window.orientation == 90 || orientation == -90)) {\r\n" + "	result = true;\r\n" + "	}\r\n"
			+ "	return result;")
	protected native boolean isLandscapeJSNI();

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
	private native boolean supportsFullscreenJSNI();

	public boolean isHdpi() {
		return getNativeScreenDensityJSNI() == 1.5;
	}

	public boolean isXhdpi() {
		return getNativeScreenDensityJSNI() > 1.5;
	}

	@JSBody(script = "return window.orientation || 0;")
	public native float getOrientationValue();

	@JSBody(script = "return window.devicePixelRatio || 1;")
	private static native int getNativeScreenDensityJSNI();

	@JSBody(script = "return window.screen.width;")
	public static native int getScreenWidthJSNI();

	@JSBody(script = "return window.screen.height;")
	public static native int getScreenHeightJSNI();

	@JSBody(params = { "element", "fullscreenChanged" }, script = "" + "if (element.requestFullscreen) {\n"
			+ "   document.addEventListener(\"fullscreenchange\", fullscreenChanged, false);\n" + "}\n"
			+ "// Attempt to the vendor specific variants of the API\n" + "if (element.webkitRequestFullScreen) {\n"
			+ "   document.addEventListener(\"webkitfullscreenchange\", fullscreenChanged, false);\n" + "}\n"
			+ "if (element.mozRequestFullScreen) {\n"
			+ "   document.addEventListener(\"mozfullscreenchange\", fullscreenChanged, false);\n" + "}\n"
			+ "if (element.msRequestFullscreen) {\n"
			+ "   document.addEventListener(\"msfullscreenchange\", fullscreenChanged, false);\n" + "}")
	protected static native void addFullscreenChangeListener(HTMLCanvasElement element,
			FullscreenChanged fullscreenChanged);

	@JSFunctor
	public interface FullscreenChanged extends org.teavm.jso.JSObject {
		void fullscreenChanged();
	}

	public boolean enterFullscreen(HTMLCanvasElement element, int screenWidth, int screenHeight) {
		return enterFullscreenJSNI(element, screenWidth, screenHeight);
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

	@JSBody(script = "if (\"fullscreenElement\" in document) {\r\n" + "return document.fullscreenElement != null;\r\n"
			+ "}\r\n" + "if (\"msFullscreenElement\" in document) {\r\n"
			+ "return document.msFullscreenElement != null;\r\n" + "}\r\n"
			+ "if (\"webkitFullscreenElement\" in document) {\r\n"
			+ "return document.webkitFullscreenElement != null;\r\n" + "}\r\n"
			+ "if (\"mozFullScreenElement\" in document) {\r\n" + "return document.mozFullScreenElement != null;\r\n"
			+ "}\r\n" + "if (\"webkitIsFullScreen\" in document) {\r\n" + "return document.webkitIsFullScreen;\r\n"
			+ "}\r\n" + "if (\"mozFullScreen\" in document) {\r\n" + "return document.mozFullScreen;\r\n" + "}\r\n"
			+ "return false;}\r\n")
	public static native boolean isFullscreenJSNI();

	@JSBody(params = { "element", "url", "filename" }, script = "element.href = url;\r\n"
			+ "element.download = filename;")
	private static native void downloadFile(HTMLElement element, String url, String filename);

	protected static void download(String fileName, String url) {
		HTMLElement docAhref = TeaBase.get().getDocument().createElement("a");
		downloadFile(docAhref, url, fileName);
		docAhref.click();
		docAhref.setDir(url);
	}

	protected static void downloadText(String fileName, String text) {
		download(fileName, "data:text/plain;charset=utf-8," + text);
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
