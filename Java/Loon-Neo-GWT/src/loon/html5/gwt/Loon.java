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

import java.util.HashMap;

import loon.LGame;
import loon.LSetting;
import loon.LSystem;
import loon.LazyLoading;
import loon.Platform;
import loon.events.KeyMake;
import loon.events.SysInput;
import loon.events.Updateable;
import loon.geom.Vector2f;
import loon.html5.gwt.GWTGame.GWTSetting;
import loon.html5.gwt.GWTGame.Repaint;
import loon.html5.gwt.soundmanager2.SoundManager;
import loon.html5.gwt.preloader.LocalAssetResources;
import loon.html5.gwt.preloader.Preloader;
import loon.html5.gwt.preloader.Preloader.PreloaderCallback;
import loon.html5.gwt.preloader.Preloader.PreloaderState;
import loon.utils.MathUtils;
import loon.utils.PathUtils;
import loon.utils.StringUtils;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class Loon implements Platform, EntryPoint, LazyLoading {

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

	private static String cur_language = null;

	private static String cur_browserType = null;

	private HashMap<String, OrientationChangedHandler> _handlers = new HashMap<String, OrientationChangedHandler>();

	private Orientation _orientation;

	private int _currentHandlerId = 1;

	static final class JsMap extends JavaScriptObject {
		protected JsMap() {
		}

		public native String getKey() /*-{
			return this["k"];
		}-*/;

		public native String getValue() /*-{
			return this["v"];
		}-*/;
	}

	public interface OrientationChangedEvent {
		void onOrientationChanged();
	}

	public interface LoadingListener {

		public void beforeSetup();

		public void afterSetup();
	}

	public interface OrientationChangedHandler {

		void onChanged(Orientation newOrientation);

	}

	protected LoadingListener loadingListener;

	protected Preloader preloader;

	protected Panel root;

	protected static Loon self;

	private LazyLoading.Data mainData = null;

	private String userAgent = null;

	protected GWTResources resources = null;

	protected GWTProgress progress = null;

	LSetting setting = null;

	GWTSetting config = null;

	public String getBaseUrl() {
		return preloader.baseUrl;
	}

	public Preloader getPreloader() {
		return preloader;
	}

	@Override
	public void onModuleLoad() {
		initTime();
		initRequestAnimFrame();

		_orientation = calculateScreenOrientation();
		try {
			this.registerOrientationChangedHandler(new OrientationChangedEvent() {

				@Override
				public void onOrientationChanged() {
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

		this.onMain();

		if (this.setting instanceof GWTSetting) {
			config = (GWTSetting) this.setting;
		} else {
			config = new GWTSetting();
			config.copy(this.setting);
		}
		this.setting = config;
		// gwt中提供的AnimationScheduler默认不能设置间隔，所以转个能设置的默认（走setTimeout）
		if (config.fps < 60 && config.repaint == Repaint.AnimationScheduler) {
			config.repaint = Repaint.RequestAnimationFrame;
		}
		this.progress = config.progress;
		if (this.progress == null) {
			boolean flag = (config.internalRes == null && !config.jsloadRes);
			if (flag) {
				int rad = MathUtils.random(0, 1);
				switch (rad) {
				case 0:
					this.progress = GWTProgressDef.newSimpleLogoProcess(config);
					break;
				case 1:
				default:
					this.progress = GWTProgressDef.newLogoProcess();
					break;
				}
			} else {
				this.progress = GWTProgressDef.newSimpleLogoProcess(config);
			}
		}
		Element element = Document.get().getElementById(config.rootId);
		if (element == null) {
			VerticalPanel panel = new VerticalPanel();
			panel.setWidth(config.getShowWidth() + "px");
			panel.setHeight(config.getShowHeight() + "px");
			panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			RootPanel.get().add(panel);
			RootPanel.get().setWidth(config.getShowWidth() + "px");
			RootPanel.get().setHeight(config.getShowHeight() + "px");
			this.root = panel;
		} else {
			VerticalPanel panel = new VerticalPanel();
			panel.setWidth(config.getShowWidth() + "px");
			panel.setHeight(config.getShowHeight() + "px");
			panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			element.appendChild(panel.getElement());
			this.root = panel;
		}

		Vector2f pos = GWTCanvasUtils.getPosition(root.getElement());

		if (pos.isEmpty()) {
			GWTCanvasUtils.setPosition(root.getElement(), (getClientWidth() - config.getShowWidth()) / 2f,
					(getClientHeight() - config.getShowHeight()) / 2f);
		}

		SoundManager.init(GWT.getModuleBaseURL(), 9, config.preferFlash, new SoundManager.SoundManagerCallback() {

			@Override
			public void onready() {
				loadResources();
			}

			@Override
			public void ontimeout(String status, String errorType) {
				consoleLog("SoundManager:" + status + " " + errorType);
				// 音频环境目前浏览器支持有限，并非所有浏览器都完美支持音频播放，但是——
				// 无论音频载入成败与否，都尝试加载其它资源。
				loadResources();
			}

		});

	}

	private final void loadResources() {
		boolean internalExist = config.internalRes != null;
		boolean jsres = config.jsloadRes || internalExist;
		if (jsres) {
			final LocalAssetResources localRes = internalExist ? config.internalRes : new LocalAssetResources();
			ScriptInjector.fromUrl("assets/resources.js").setCallback(new Callback<Void, Exception>() {
				public void onFailure(Exception reason) {
					consoleLog("resources script load failed.");
				}

				public void onSuccess(Void result) {
					JsArray<JsMap> list = loadJavaScriptResources(localRes).cast();

					int size = list.length();

					for (int i = 0; i < size; i++) {
						JsMap res = list.get(i);
						String key = res.getKey();
						String value = res.getValue();
						String ext = LSystem.getExtension(key);

						if (LSystem.isText(ext)) {
							localRes.putText(key, value);
						} else if (LSystem.isImage(ext)) {
							localRes.putImage(key, value);
						} else if (LSystem.isAudio(ext)) {
							// noop
						} else {
							localRes.putBlobString(key, value);
						}
					}

					localRes.commit();
					loadResources(progress.getPreloaderCallback(Loon.self, root), localRes);

				}
			}).setWindow(ScriptInjector.TOP_WINDOW).inject();
		} else {
			loadResources(progress.getPreloaderCallback(Loon.self, root), null);
		}
	}

	private native JsArray<JavaScriptObject> loadJavaScriptResources(LocalAssetResources res) /*-{
		return new $wnd.LocalResources().running(res);
	}-*/;

	Preloader loadResources(final PreloaderCallback callback, final LocalAssetResources localRes) {
		this.preloader = createPreloader(localRes);
		this.preloader.preload("assets.txt", new PreloaderCallback() {
			@Override
			public void error(String file) {
				callback.error(file);
				startMain();
			}

			@Override
			public void update(PreloaderState state) {
				callback.update(state);
				if (state.hasEnded()) {
					startMain();
				}
			}
		});
		return this.preloader;
	}

	private void startMain() {
		getRootPanel().clear();
		if (loadingListener != null) {
			loadingListener.beforeSetup();
		}
		mainLoop();
		if (loadingListener != null) {
			loadingListener.afterSetup();
		}
	}

	void mainLoop() {
		this.resources = new GWTResources(preloader);
		this.createGame().start();
	}

	public abstract void onMain();

	public String getPreloaderBaseURL() {
		return GWT.getHostPageBaseURL();
	}

	public Preloader createPreloader(LocalAssetResources res) {
		return new Preloader(PathUtils.getCombinePaths(getPreloaderBaseURL(), LSystem.getPathPrefix()), res);
	}

	private static native String getFullURL()/*-{
		return $wnd.location.href;
	}-*/;

	private static native void initTime()
	/*-{
		Date.now = Date.now || function() {
			return new Date().getTime();
		};
		window.performance = window.performance || {};
		performance.now = (function() {
			return performance.now || performance.mozNow || performance.msNow
					|| performance.oNow || performance.webkitNow || Date.now;
		})();
	}-*/;

	private GWTGame game;

	public Panel getRootPanel() {
		return root;
	}

	protected GWTGame createGame() {
		LSystem.freeStaticObject();
		Loon.self = this;
		return this.game = new GWTGame(this, root, config);
	}

	@Override
	public LGame getGame() {
		return game;
	}

	protected GWTGame initialize() {
		if (game != null) {
			game.register(mainData.onScreen());
		}
		return game;
	}

	@Override
	public void register(LSetting s, LazyLoading.Data data) {
		this.setting = s;
		this.mainData = data;
	}

	@Override
	public int getContainerWidth() {
		int width = getJSNIAvailWidth() <= 0 ? getJSNIScreenWidth() : getJSNIAvailWidth();
		if (isIOS() && isLandscape()) {
			return getContainerHeight();
		}
		if (isNextGenerationIos()) {
			return width * 2;
		}
		return width;
	}

	@Override
	public int getContainerHeight() {
		int height = getJSNIAvailHeight() <= 0 ? getJSNIScreenHeight() : getJSNIAvailHeight();
		if (isIOS() && isLandscape()) {
			return getContainerWidth();
		}
		if (isNextGenerationIos()) {
			return height * 2;
		}
		return height;
	}

	@Override
	public Orientation getOrientation() {
		if (_orientation == Orientation.Landscape) {
			return Orientation.Landscape;
		}
		if (getJSNIScreenHeight() > getJSNIScreenWidth()) {
			return Orientation.Portrait;
		} else {
			return Orientation.Landscape;
		}
	}

	public GWTGame getHTMLGame() {
		return game;
	}

	public static int getClientWidth() {
		int result = Window.getClientWidth();
		if (result > 0) {
			return result;
		}
		Document doc = Document.get();
		if (doc == null) {
			return getJSNIScreenWidth();
		}
		result = doc.getClientWidth();
		if (result <= 0) {
			result = doc.getBody().getClientWidth();
		}
		if (result <= 0) {
			result = doc.getDocumentElement().getClientWidth();
		}
		return result;
	}

	public static int getClientHeight() {
		int result = Window.getClientHeight();
		if (result > 0) {
			return result;
		}
		Document doc = Document.get();
		if (doc == null) {
			return getJSNIScreenHeight();
		}
		result = doc.getClientHeight();
		if (result <= 0) {
			result = doc.getBody().getClientHeight();
		}
		if (result <= 0) {
			result = doc.getDocumentElement().getClientHeight();
		}
		return result;
	}

	@Override
	public void close() {
		closeImpl();
	}

	private static native void closeImpl()
	/*-{
		$wnd.close();
	}-*/;

	public native static void consoleLog(String msg) /*-{
		if ($wnd.console) {
			$wnd.console.log(msg);
		} else {
			$doc.title = "GWT Log:" + msg;
		}
	}-*/;

	public native static void alert(String msg)/*-{
		if (typeof (window.alert) === "function") {
			window.alert.call(null, msg);
		} else {
			console.warn("alert is not a function");
		}
	}-*/;

	@Override
	public void sysText(final SysInput.TextEvent event, final KeyMake.TextType textType, final String label,
			final String initVal) {
		if (game == null) {
			event.cancel();
			return;
		}
		LSystem.load(new Updateable() {

			@Override
			public void action(Object a) {

				String result = Window.prompt(label, initVal);
				if (game.input() instanceof GWTInputMake) {
					((GWTInputMake) game.input()).emitFakeMouseUp();
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
	public void sysDialog(final SysInput.ClickEvent event, final String title, final String text, final String ok,
			final String cancel) {
		if (game == null) {
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
				if (game.input() instanceof GWTInputMake) {
					((GWTInputMake) game.input()).emitFakeMouseUp();
				}
				if (result) {
					event.clicked();
				} else {
					event.cancel();
				}
			}
		});
	}

	private native void registerOrientationChangedHandler(OrientationChangedEvent handler) /*-{
		var callback = function() {
			handler.@loon.html5.gwt.Loon.OrientationChangedEvent::onOrientationChanged()();
		}
		$wnd.addEventListener("orientationchange", callback, false);
	}-*/;

	public boolean isMobile() {
		return isAndroid() || isIOS() || isOpenHarmony() || isWindowsPhone() || isWeiXin() || isBlackBerry()
				|| getUserAgent().contains("mobile");
	}

	/**
	 * 通过orientation属性取得屏幕是否竖屏，若浏览器不支持orientation,有可能取不到
	 * 
	 * @return
	 */
	protected native boolean isPortrait() /*-{
		var result = false;
		if ($wnd.orientation != null && $wnd.orientation == 0) {
			result = true;
		}
		return result;
	}-*/;

	/**
	 * 通过orientation属性取得屏幕是否横屏，若浏览器不支持orientation,有可能取不到
	 * 
	 * @return
	 */
	protected native boolean isLandscape() /*-{
		var result = false;
		if ($wnd.orientation != null
				&& ($wnd.orientation == 90 || orientation == -90)) {
			result = true;
		}
		return result;
	}-*/;

	/**
	 * 若同时取不到横竖，则判定为不支持
	 */
	public boolean noSupportOrientation() {
		return !isPortrait() && !isLandscape();
	}

	public native float getOrientationValue() /*-{
		return $wnd.orientation || 0;
	}-*/;

	private Orientation calculateScreenOrientation() {
		return isPortrait() ? Orientation.Portrait : Orientation.Landscape;
	}

	public String addHandler(OrientationChangedHandler handler) {
		int newHandlerIdValue = _currentHandlerId++;
		String newHandlerId = String.valueOf(newHandlerIdValue);
		_handlers.put(newHandlerId, handler);
		return newHandlerId;
	}

	public native static Style getSystemFontJSNI() /*-{
		return (document.documentElement.currentStyle ? document.documentElement.currentStyle
				: window.getComputedStyle(document.documentElement)).fontFamily;
	}-*/;

	/**
	 * 屏幕[完整宽度]
	 * 
	 * @return
	 */
	protected native static int getJSNIScreenWidth() /*-{
		return $wnd.screen.width || 0;
	}-*/;

	/**
	 * 屏幕[完整高度]
	 * 
	 * @return
	 */
	protected native static int getJSNIScreenHeight() /*-{
		return $wnd.screen.height || 0;
	}-*/;

	/**
	 * 屏幕[可用宽度]
	 * 
	 * @return
	 */
	protected native int getJSNIAvailWidth() /*-{
		return $wnd.screen.availWidth || 0;
	}-*/;

	/**
	 * 屏幕[可用高度]
	 * 
	 * @return
	 */
	protected native int getJSNIAvailHeight() /*-{
		return $wnd.screen.availHeight || 0;
	}-*/;

	public native static float devicePixelRatio() /*-{
		return $wnd.devicePixelRatio || 1;
	}-*/;

	public native static float backingStorePixelRatio() /*-{
		return $wnd.webkitBackingStorePixelRatio || 1;
	}-*/;

	private void fullscreenChanged() {
		if (!isFullscreen()) {
			if (config.isFixedSize()) {
				game.graphics().restoreSize();
			}
			if (config.fullscreenOrientation != null)
				unlockOrientation();
		} else {
			if (config.fullscreenOrientation != null) {
				lockOrientation(config.fullscreenOrientation);
			}
		}
	}

	public void setFullscreen(boolean f) {
		if (f) {
			if (game != null) {
				setFullscreen(game.graphics().getCanvas(), getContainerWidth(), getContainerHeight());
			}
		} else {
			exitFullscreen();
		}
	}

	public boolean setFullscreen(CanvasElement element, int w, int h) {
		return setFullscreenJSNI(this, element, w, h);
	}

	private native boolean setFullscreenJSNI(Loon loonApp, CanvasElement element, int screenWidth, int screenHeight)/*-{
		if (element.requestFullscreen) {
			element.width = screenWidth;
			element.height = screenHeight;
			element.requestFullscreen();
			$doc.addEventListener("fullscreenchange", function() {
				loonApp.@loon.html5.gwt.Loon::fullscreenChanged()();
			}, false);
			return true;
		}
		if (element.webkitRequestFullScreen) {
			element.width = screenWidth;
			element.height = screenHeight;
			element.webkitRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT);
			$doc.addEventListener("webkitfullscreenchange", function() {
				loonApp.@loon.html5.gwt.Loon::fullscreenChanged()();
			}, false);
			return true;
		}
		if (element.mozRequestFullScreen) {
			element.width = screenWidth;
			element.height = screenHeight;
			element.mozRequestFullScreen();
			$doc.addEventListener("mozfullscreenchange", function() {
				loonApp.@loon.html5.gwt.Loon::fullscreenChanged()();
			}, false);
			return true;
		}
		if (element.msRequestFullscreen) {
			element.width = screenWidth;
			element.height = screenHeight;
			element.msRequestFullscreen();
			$doc.addEventListener("msfullscreenchange", function() {
				loonApp.@loon.html5.gwt.Loon::fullscreenChanged()();
			}, false);
			return true;
		}

		return false;
	}-*/;

	private native void exitFullscreen() /*-{
		if ($doc.exitFullscreen)
			$doc.exitFullscreen();
		if ($doc.msExitFullscreen)
			$doc.msExitFullscreen();
		if ($doc.webkitExitFullscreen)
			$doc.webkitExitFullscreen();
		if ($doc.mozExitFullscreen)
			$doc.mozExitFullscreen();
		if ($doc.webkitCancelFullScreen)
			$doc.webkitCancelFullScreen();
	}-*/;

	public boolean lockOrientation(OrientationLockType orientation) {
		return lockOrientationJSNI(orientation.getName());
	}

	public boolean unlockOrientation() {
		return unlockOrientationJSNI();
	}

	private native boolean lockOrientationJSNI(String orientationEnumValue) /*-{
		var screen = $wnd.screen;
		screen.newLockOrientation = screen.lockOrientation
				|| screen.mozLockOrientation || screen.msLockOrientation
				|| screen.webkitLockOrientation;
		if (screen.newLockOrientation) {
			return screen.newLockOrientation(orientationEnumValue);
		} else if (screen.orientation && screen.orientation.lock) {
			screen.orientation.lock(orientationEnumValue);
			return true;
		}
		return false;
	}-*/;

	private native boolean unlockOrientationJSNI() /*-{
		var screen = $wnd.screen;
		screen.newUnlockOrientation = screen.unlockOrientation
				|| screen.mozUnlockOrientation || screen.msUnlockOrientation
				|| screen.webkitUnlockOrientation;
		if (screen.newUnlockOrientation) {
			return screen.newUnlockOrientation();
		} else if (screen.orientation && screen.orientation.unlock) {
			screen.orientation.unlock();
			return true;
		}
		return false;
	}-*/;

	public boolean supportsDisplayModeChange() {
		return supportsFullscreenJSNI();
	}

	private native boolean supportsFullscreenJSNI() /*-{
		if ("fullscreenEnabled" in $doc) {
			return $doc.fullscreenEnabled;
		}
		if ("webkitFullscreenEnabled" in $doc) {
			return $doc.webkitFullscreenEnabled;
		}
		if ("mozFullScreenEnabled" in $doc) {
			return $doc.mozFullScreenEnabled;
		}
		if ("msFullscreenEnabled" in $doc) {
			return $doc.msFullscreenEnabled;
		}
		return false;
	}-*/;

	public boolean isFullscreen() {
		return isFullscreenJSNI();
	}

	private native boolean isFullscreenJSNI() /*-{
		if ("fullscreenElement" in $doc) {
			return $doc.fullscreenElement != null;
		}
		if ("msFullscreenElement" in $doc) {
			return $doc.msFullscreenElement != null;
		}
		if ("webkitFullscreenElement" in $doc) {
			return $doc.webkitFullscreenElement != null;
		}
		if ("mozFullScreenElement" in $doc) {
			return $doc.mozFullScreenElement != null;
		}
		if ("webkitIsFullScreen" in $doc) {
			return $doc.webkitIsFullScreen;
		}
		if ("mozFullScreen" in $doc) {
			return $doc.mozFullScreen;
		}
		return false
	}-*/;

	public boolean isHdpi() {
		return devicePixelRatio() == 1.5;
	}

	public boolean isXhdpi() {
		return devicePixelRatio() > 1.5;
	}

	public String getUserAgent() {
		if (!StringUtils.isEmpty(userAgent)) {
			return userAgent;
		}
		return userAgent = getUserAgentJSNI();
	}

	public native static String getUserAgentJSNI() /*-{
		return $wnd.navigator.userAgent.toLowerCase();
	}-*/;

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

	public boolean isIOS() {
		return isIPad() || isIPadRetina() || isIPhone() || isRetina() || isIOSPhone();
	}

	public boolean isIOSPhone() {
		String userAgent = getUserAgent();
		return userAgent.matches("/\\(i[^;]+;( U;)? cpu.+mac os x/");
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
		if (!isIOS()) {
			return false;
		}
		String userAgent = getUserAgent();
		if (userAgent.contains("os 6_")) {
			return true;
		}
		return false;
	}

	public boolean isIOS9() {
		if (!isIOS()) {
			return false;
		}
		String userAgent = getUserAgent();
		if (userAgent.contains("os 9_")) {
			return true;
		}
		return false;
	}

	public boolean isNextGenerationIos() {
		boolean ratio = (devicePixelRatio() > 1);
		return isIOS() && ratio;
	}

	public native boolean isStandalone() /*-{
		return $wnd.navigator.standalone;
	}-*/;

	private static native void initRequestAnimFrame()
	/*-{
		$wnd.requestAnimFrame = (function() {
			return window.requestAnimationFrame
					|| window.webkitRequestAnimationFrame
					|| window.mozRequestAnimationFrame
					|| window.oRequestAnimationFrame
					|| window.msRequestAnimationFrame
					|| function stTime(callback, element) {
						$wnd.setTimeout(callback, 16);
					};
		})();

		$wnd.cancelAnimationFrame = (function() {
			return $wnd.cancelAnimationFrame = $wnd.cancelAnimationFrame
					|| $wnd.cancelRequestAnimationFrame
					|| $wnd.msCancelRequestAnimationFrame
					|| $wnd.mozCancelRequestAnimationFrame
					|| $wnd.oCancelRequestAnimationFrame
					|| $wnd.webkitCancelRequestAnimationFrame
					|| $wnd.msCancelAnimationFrame
					|| $wnd.mozCancelAnimationFrame
					|| $wnd.webkitCancelAnimationFrame
					|| $wnd.oCancelAnimationFrame || function etime(id) {
						$wnd.clearTimeout(id);
					};
		})();
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
