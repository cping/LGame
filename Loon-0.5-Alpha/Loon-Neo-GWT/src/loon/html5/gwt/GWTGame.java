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
package loon.html5.gwt;

import loon.Asyn;
import loon.Json;
import loon.LGame;
import loon.LSetting;
import loon.Log;
import loon.Save;
import loon.Support;
import loon.event.InputMake;
import loon.html5.gwt.preloader.LocalAssetResources;
import loon.jni.NativeSupport;
import loon.jni.TimerCallback;
import loon.utils.reply.Act;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;

public class GWTGame extends LGame {

	private static String cur_language = null;
	
	private static String cur_browserType = null;
	
	public static class GWTSetting extends LSetting {

		// 是否支持使用flash加载资源（如果要做成静态文件包，涉及跨域问题(也就是非服务器端运行时)，所以需要禁止此项）
		public boolean preferFlash = false;

		// 当前浏览器的渲染模式
		public Mode mode = GWTUrl.Renderer.requestedMode();
		
		//当此项存在时，会尝试加载内部资源
		public LocalAssetResources internalRes = null;
		
		public boolean transparentCanvas = false;

		public boolean antiAliasing = true;

		public boolean stencil = false;

		public boolean premultipliedAlpha = false;

		public boolean preserveDrawingBuffer = false;

		public float scaleFactor = devicePixelRatio();

		public String rootId = "loon-root";

	}

	public static enum Mode {
		WEBGL, CANVAS, AUTODETECT;
	}

	public static class AgentInfo extends JavaScriptObject {
		public final native boolean isFirefox() /*-{
			return this.isFirefox;
		}-*/;

		public final native boolean isChrome() /*-{
			return this.isChrome;
		}-*/;

		public final native boolean isSafari() /*-{
			return this.isSafari;
		}-*/;

		public final native boolean isOpera() /*-{
			return this.isOpera;
		}-*/;

		public final native boolean isIE() /*-{
			return this.isIE;
		}-*/;

		public final native boolean isMacOS() /*-{
			return this.isMacOS;
		}-*/;

		public final native boolean isLinux() /*-{
			return this.isLinux;
		}-*/;

		public final native boolean isWindows() /*-{
			return this.isWindows;
		}-*/;

		protected AgentInfo() {
		}
	}

	public void setTitle(String title) {
		Window.setTitle(title);
	}

	public void setCursor(Cursor cursor) {
		Element rootElement = graphics.rootElement;
		if (cursor == null) {
			rootElement.getStyle().setProperty("cursor", "none");
		} else {
			rootElement.getStyle().setCursor(cursor);
		}
	}

	public void disableRightClickContextMenu() {
		disableRightClickImpl(graphics.rootElement);
	}

	native static float devicePixelRatio() /*-{
		return $wnd.devicePixelRatio || 1;
	}-*/;

	native static float backingStorePixelRatio() /*-{
		return $wnd.webkitBackingStorePixelRatio || 1;
	}-*/;

	private final static Support support = new NativeSupport();

	static final AgentInfo agentInfo = computeAgentInfo();

	private final double start = initNow();

	public Act<LGame> frame = Act.create();
	private final GWTLog log = GWT.create(GWTLog.class);
	private final Asyn syn = new Asyn.Default(log, frame);
	private final GWTAssets assets;

	private final GWTGraphics graphics;
	private final GWTInputMake input;
	private final GWTJson json = new GWTJson();
	private final GWTSave save;

	private final Loon game;

	public GWTGame(Loon game, Panel panel, GWTSetting config) {
		super(config, game);
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable e) {
				reportError("Uncaught Exception: ", e);
			}
		});

		this.game = game;

		log.info("devicePixelRatio: " + devicePixelRatio()
				+ " backingStorePixelRatio: " + backingStorePixelRatio());

		try {
			graphics = new GWTGraphics(panel, this, config);
			input = new GWTInputMake(this, graphics.rootElement);
			assets = new GWTAssets(this);
			save = new GWTSave(this);

		} catch (Throwable e) {
			log.error("init()", e);
			Window.alert("failed to init(): " + e.getMessage());
			throw new RuntimeException(e);
		}
		if (setting != null && setting.appName != null) {
			setTitle(setting.appName);
		}
		initProcess();

	}

	private boolean initGwt = false;

	public void start() {
		if (!initGwt) {
			game.initialize();
			initGwt = true;
		}
		requestAnimationFrame(new TimerCallback() {
			@Override
			public void fire() {
				requestAnimationFrame(this);
				emitFrame();
			}
		});
	}

	@Override
	public Type type() {
		return Type.HTML5;
	}

	@Override
	public double time() {
		return now();
	}

	@Override
	public int tick() {
		return (int) (now() - start);
	}

	@Override
	public void openURL(String url) {
		Window.open(url, "_blank", "");
	}

	@Override
	public GWTAssets assets() {
		return assets;
	}

	@Override
	public GWTGraphics graphics() {
		return graphics;
	}

	@Override
	public Asyn asyn() {
		return syn;
	}

	@Override
	public InputMake input() {
		return input;
	}

	@Override
	public Json json() {
		return json;
	}

	@Override
	public Log log() {
		return log;
	}

	@Override
	public Save save() {
		return save;
	}

	@Override
	public Support support() {
		return support;
	}

	private native JavaScriptObject getWindow() /*-{
		return $wnd;
	}-*/;

	private native void requestAnimationFrame(TimerCallback callback) /*-{
		var fn = function() {
			callback.@loon.jni.TimerCallback::fire()();
		};
		if ($wnd.requestAnimationFrame) {
			$wnd.requestAnimationFrame(fn);
		} else if ($wnd.mozRequestAnimationFrame) {
			$wnd.mozRequestAnimationFrame(fn);
		} else if ($wnd.webkitRequestAnimationFrame) {
			$wnd.webkitRequestAnimationFrame(fn);
		} else if ($wnd.oRequestAnimationFrame) {
			$wnd.oRequestAnimationFrame(fn);
		} else if ($wnd.msRequestAnimationFrame) {
			$wnd.msRequestAnimationFrame(fn);
		} else {
			$wnd.setTimeout(fn, 16);
		}
	}-*/;

	private static native AgentInfo computeAgentInfo() /*-{
		var userAgent = navigator.userAgent.toLowerCase();
		return {
			isFirefox : userAgent.indexOf("firefox") != -1,
			isChrome : userAgent.indexOf("chrome") != -1,
			isSafari : userAgent.indexOf("safari") != -1,
			isOpera : userAgent.indexOf("opera") != -1,
			isIE : userAgent.indexOf("msie") != -1,
			isMacOS : userAgent.indexOf("mac") != -1,
			isLinux : userAgent.indexOf("linux") != -1,
			isWindows : userAgent.indexOf("win") != -1
		};
	}-*/;

	private static native void disableRightClickImpl(JavaScriptObject target) /*-{
		target.oncontextmenu = function() {
			return false;
		};
	}-*/;

	private static native double initNow() /*-{
		if (!Date.now)
			Date.now = function now() {
				return +(new Date);
			};
		return Date.now();
	}-*/;

	private static native double now() /*-{
		return Date.now();
	}-*/;

	native static public void consoleLog(String message) /*-{
		console.log("GWT: " + message);
	}-*/;

	private native String getUserAgent() /*-{
		return $wnd.navigator.userAgent.toLowerCase();
	}-*/;

	@Override
	public boolean isMobile() {
		return super.isMobile()
				|| isAndroid() || isIOs() || getUserAgent().contains("mobile");
	}

	public boolean isAndroid() {
		return isAndroidPhone() || isAndroidTablet();
	}

	public boolean isIPhone() {
		String userAgent = getUserAgent();
		if (userAgent.contains("iphone") && devicePixelRatio() < 2) {
			return true;
		}
		return false;
	}

	public boolean isIPad() {
		String userAgent = getUserAgent();
		if (userAgent.contains("ipad") && devicePixelRatio() < 2) {
			return true;
		}
		return false;
	}

	public boolean isIOs() {
		return isIPad() || isIPadRetina() || isIPhone() || isRetina();
	}

	public boolean isRetina() {
		String userAgent = getUserAgent();
		if (userAgent.contains("iphone") && devicePixelRatio() >= 2) {
			return true;
		}
		return false;
	}

	public boolean isIPadRetina() {
		String userAgent = getUserAgent();
		if (userAgent.contains("ipad") && devicePixelRatio() >= 2) {
			return true;
		}
		return false;
	}

	public boolean isDesktop() {
		return !isIOs() && !isAndroid();
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
		return false;
	}

	public boolean isAndroid4_4_OrHigher() {
		String userAgent = getUserAgent();
		if (userAgent.contains("android") && userAgent.contains("chrome")) {
			return true;
		}
		return false;
	}

	public boolean isAndroid2x() {
		String userAgent = getUserAgent();
		if (userAgent.contains("android 2.")) {
			return true;
		}
		return false;
	}

	public boolean isAndroid4_3_orLower() {
		if (isAndroid4_4_OrHigher()) {
			return false;
		}
		String userAgent = getUserAgent();
		if (userAgent.contains("android")) {
			return true;
		}
		return false;
	}

	public boolean isIOS6() {
		if (!isIOs()) {
			return false;
		}
		String userAgent = getUserAgent();
		if (userAgent.contains("os 6_")) {
			return true;
		}
		return false;
	}

	public boolean isIOS9() {
		if (!isIOs()) {
			return false;
		}
		String userAgent = getUserAgent();
		if (userAgent.contains("os 9_")) {
			return true;
		}
		return false;
	}

	public native boolean isStandalone() /*-{
		return $wnd.navigator.standalone;
	}-*/;
	

	private static native String languageImpl()
	/*-{
		var nav = $wnd.navigator;
		var curLanguage = nav.language;
		curLanguage = curLanguage ? curLanguage : nav.browserLanguage;
		curLanguage = curLanguage ? curLanguage.split("-")[0] : "en";
		return curLanguage;
	}-*/;

	public static String language() {
		if (cur_language == null) {
			cur_language = languageImpl();
		}
		return cur_language;
	}

	private static native String browserTypeImpl()
	/*-{
		var ua = $wnd.navigator.userAgent;
		var BROWSER_TYPE_WECHAT = "wechat";
		var BROWSER_TYPE_ANDROID = "androidbrowser";
		var BROWSER_TYPE_IE = "ie";
		var BROWSER_TYPE_360 = "360browser";
		var BROWSER_TYPE_MAXTHON = "maxthon";
		var BROWSER_TYPE_OPERA = "opera";
		var BROWSER_TYPE_UNKNOWN = "unknown";
		var typeReg1 = /sogou|qzone|liebao|micromessenger|ucbrowser|360 aphone|360browser|baiduboxapp|baidubrowser|maxthon|mxbrowser|trident|miuibrowser/i;
		var typeReg2 = /qqbrowser|chrome|safari|firefox|opr|oupeng|opera/i;
		var browserTypes = typeReg1.exec(ua);
		if (!browserTypes) {
			browserTypes = typeReg2.exec(ua);
		}
		var browserType = browserTypes ? browserTypes[0] : BROWSER_TYPE_UNKNOWN;
		if (browserType === "micromessenger") {
			browserType = BROWSER_TYPE_WECHAT;
		} else if (browserType === "safari"
				&& (ua.match(/android.*applewebkit/))) {
			browserType = BROWSER_TYPE_ANDROID;
		} else if (browserType === "trident") {
			browserType = BROWSER_TYPE_IE;
		} else if (browserType === "360 aphone") {
			browserType = BROWSER_TYPE_360;
		} else if (browserType === "mxbrowser") {
			browserType = BROWSER_TYPE_MAXTHON;
		} else if (browserType === "opr") {
			browserType = BROWSER_TYPE_OPERA;
		}
		return browserType;
	}-*/;
	
	public static String browserType() {
		if (cur_browserType == null) {
			cur_browserType = browserTypeImpl();
		}
		return cur_browserType;
	}

}
