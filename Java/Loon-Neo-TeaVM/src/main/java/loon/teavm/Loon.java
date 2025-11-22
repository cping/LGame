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
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLElement;

import loon.LGame;
import loon.LSetting;
import loon.LazyLoading;
import loon.Platform;
import loon.events.KeyMake.TextType;
import loon.events.SysInput.ClickEvent;
import loon.events.SysInput.TextEvent;
import loon.teavm.TeaGame.TeaSetting;
import loon.teavm.assets.AssetPreloader;

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

	protected static Loon self;

	private LazyLoading.Data mainData = null;

	private LSetting setting = null;

	private TeaSetting config = null;

	private Loon(LSetting setting, LazyLoading.Data lazy) {
		this.setting = setting;
		this.mainData = lazy;
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
		if (this.setting instanceof TeaSetting) {
			config = (TeaSetting) this.setting;
		} else {
			config = new TeaSetting();
			config.copy(this.setting);
		}
		this.setting = config;
		if (config.fps != 60 && config.repaint == TeaGame.Repaint.AnimationScheduler) {
			config.repaint = TeaGame.Repaint.RequestAnimationFrame;
		}
	}

	public AssetPreloader getPreloader() {
		return null;
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

	@JSBody(params = "msg", script = "if (typeof (window.alert) === \"function\") {\r\n"
			+ "        window.alert.call(null, msg); \r\n" + "    }\r\n" + "    else {\r\n"
			+ "        console.warn(\"alert is not a function\");\r\n" + "    };")
	private native void alert(String msg);

	@JSBody(script = "window.close();")
	private static native void closeImpl();

	@JSBody(script = "console.log(\"TeaVM: \" + message);")
	public native static void consoleLog(String message);

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

	@Override
	public LGame getGame() {
		return null;
	}

	@Override
	public void sysText(TextEvent event, TextType textType, String label, String initialValue) {

	}

	@Override
	public void sysDialog(ClickEvent event, String title, String text, String ok, String cancel) {

	}
}
