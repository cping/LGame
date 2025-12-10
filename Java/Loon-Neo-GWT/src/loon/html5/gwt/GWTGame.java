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
import loon.LGame;
import loon.LSetting;
import loon.LSystem;
import loon.Support;
import loon.geom.Vector2f;
import loon.html5.gwt.Loon.OrientationLockType;
import loon.html5.gwt.preloader.LocalAssetResources;
import loon.jni.NativeSupport;
import loon.jni.TimerCallback;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;

public final class GWTGame extends LGame {

	private static final int MIN_DELAY = 5;

	/**
	 * 由于手机版的浏览器对webgl支持实在各种奇葩，不同手机环境差异实在惊人，干脆把常用的刷新方式都写出来，用户自己选……
	 */
	public static enum Repaint {
		// RequestAnimationFrame效率最高
		// Schedule在某些情况下更适用（有间断时）
		// AnimationScheduler本质是前两者的api混合，会等待canvas渲染后刷新，虽然效率最低，但是最稳，不容易造成webgl卡死现象
		// 为了稳定考虑，所以默认用这个.
		RequestAnimationFrame, Schedule, AnimationScheduler;
	}

	public static class GWTSetting extends LSetting {

		// 经过几天来的实测，webgl对不同浏览器（以及在不同手机环境）下的差异太大，于是把刷新模式也交给用户定制好了……
		// 暂时来说，canvas还是目前手机版html5的王道，webgl差异不解决，很难推（大家都用chrome世界就完美了……）
		/**
		 * 经过几天的反复测试，最终还是默认用gwt提供的AnimationScheduler刷新（本质还是RequestAnimationFrame
		 * 但是不同平台上综合来说，这个有对象绑定，会匀速刷新canvas，不容易造成卡死……）
		 **/
		public Repaint repaint = Repaint.AnimationScheduler;

		// 是否支持使用flash加载资源（如果要做成静态文件包，涉及跨域问题(也就是非服务器端运行时)，所以需要禁止此项）
		public boolean preferFlash = false;

		// 当前浏览器的渲染模式
		public Mode mode = GWTUrl.Renderer.requestedMode();

		// 当此项存在时，会尝试加载内部资源
		public LocalAssetResources internalRes = null;

		// 当此项存在时，同样会尝试加载内部资源
		public boolean jsloadRes = false;

		public boolean transparentCanvas = false;

		public boolean antiAliasing = true;

		public boolean stencil = false;

		public boolean premultipliedAlpha = false;

		public boolean preserveDrawingBuffer = false;

		// 如果此项开启，按照屏幕大小等比缩放
		public boolean useRatioScaleFactor = false;

		public OrientationLockType fullscreenOrientation;

		// 需要绑定的层id
		public String rootId = "loon-root";

		// webgl模式
		public String powerPreference = "high-performance";

		// 初始化时的进度条样式（不实现则默认加载）
		public GWTProgress progress = null;

		// 如果此项为true,则仅以异步加载资源
		public boolean asynResource = false;

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

	private final static Support support = new NativeSupport();

	static final AgentInfo agentInfo = computeAgentInfo();

	final GWTSetting gwtconfig;

	private final double start;

	private final GWTLog log;
	private final Asyn syn;
	private final GWTAccelerometer accelerometer;
	private final GWTAssets assets;

	private final GWTGraphics graphics;
	private final GWTInputMake input;
	private final GWTSave save;
	private final GWTClipboard clipboard;

	private final Vector2f offsetPos = new Vector2f();

	private final Loon game;

	private boolean initGwt = false;

	public GWTGame(Loon game, Panel panel, GWTSetting config) {
		super(config, game);
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable e) {
				reportError("Uncaught Exception: ", e);
			}
		});
		this.game = game;
		this.gwtconfig = config;
		this.start = initNow();
		this.log = new GWTLog();
		this.syn = new Asyn.Default(log, frame);
		this.accelerometer = new GWTAccelerometer();
		log.info("Browser orientation: " + game.getOrientation());
		log.info("Browser screen width: " + game.getContainerWidth() + ", screen height: " + game.getContainerHeight());
		log.info("devicePixelRatio: " + Loon.devicePixelRatio() + " backingStorePixelRatio: "
				+ Loon.backingStorePixelRatio());

		if (config.useRatioScaleFactor) {
			int width = setting.width;
			int height = setting.height;
			float scale = Loon.devicePixelRatio();
			width *= scale;
			height *= scale;
			setting.width_zoom = width;
			setting.height_zoom = height;
			setting.updateScale();
			// 若缩放值为无法实现的数值，则默认操作
		} else {
			setting.updateScale();
		}
		try {
			graphics = new GWTGraphics(panel, this, config);
			assets = new GWTAssets(this);
			clipboard = new GWTClipboard();
			input = new GWTInputMake(this, graphics.canvas);
			save = new GWTSave(this);
		} catch (Throwable e) {
			log.error("init()", e);
			Window.alert("failed to init(): " + e.getMessage());
			throw new RuntimeException(e);
		}
		if (setting != null && setting.appName != null) {
			setTitle(setting.appName);
		}
		this.initProcess();
	}

	public Vector2f getOffsetPos() {
		return offsetPos;
	}

	public GWTGame setOffsetPos(float x, float y) {
		offsetPos.set(x, y);
		return this;
	}

	private void init() {
		if (!initGwt) {
			if (game != null) {
				game.initialize();
				LSystem.PAUSED = false;
				initGwt = true;
			}
		}
	}

	public void start() {
		init();
		Repaint repaint = game.config.repaint;
		// 此处使用了三种不同的画面刷新模式，万一有浏览器刷不动，大家可以换模式看看……
		switch (repaint) {
		case RequestAnimationFrame:
			requestAnimationFrame(game.setting.fps, new TimerCallback() {

				@Override
				public void fire() {
					requestAnimationFrame(game.setting.fps, this);
					emitFrame();
				}
			});
			break;
		case AnimationScheduler:
			final AnimationScheduler scheduler = AnimationScheduler.get();
			scheduler.requestAnimationFrame(new AnimationCallback() {

				@Override
				public void execute(double timestamp) {
					emitFrame();
					scheduler.requestAnimationFrame(this, graphics.canvas);
				}
			}, graphics.canvas);
			break;
		case Schedule:
			final Duration duration = new Duration();
			final int framed = (int) ((1f / game.setting.fps) * 1000f);
			new Timer() {
				@Override
				public void run() {
					emitFrame();
					this.schedule(Math.max(MIN_DELAY, framed - duration.elapsedMillis()));

				}
			}.schedule(framed);
			break;
		}
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
	public Asyn asyn() {
		return syn;
	}

	@Override
	public GWTAccelerometer accel() {
		return accelerometer;
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
	public GWTInputMake input() {
		return input;
	}

	@Override
	public GWTLog log() {
		return log;
	}

	@Override
	public GWTSave save() {
		return save;
	}

	@Override
	public GWTClipboard clipboard() {
		return clipboard;
	}

	@Override
	public Support support() {
		return support;
	}

	private native JavaScriptObject getWindow() /*-{
		return $wnd;
	}-*/;

	private native void requestAnimationFrame(float frameRate, TimerCallback callback) /*-{
		var fn = function() {
			callback.@loon.jni.TimerCallback::fire()();
		};
		if (frameRate < 60) {
			$wnd.setTimeout(fn, 1000 / frameRate);
		} else {
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
		}
	}-*/;

	private static native AgentInfo computeAgentInfo() /*-{
		var userAgent = navigator.userAgent.toLowerCase();
		return {
			isFirefox : userAgent.indexOf("firefox") != -1,
			isChrome : userAgent.indexOf("chrome") != -1,
			isSafari : $wnd.opera || userAgent.indexOf("safari") != -1,
			isOpera : userAgent.indexOf("opera") != -1,
			isIE : userAgent.indexOf("msie") != -1
					|| userAgent.indexOf("trident") != -1,
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

	/**
	 * 检测浏览器窗体是否被隐藏起来
	 * 
	 * @return
	 */
	private native boolean isHidden() /*-{
		return $doc.hidden;
	}-*/;

	public boolean isShow() {
		return isHidden();
	}

	@Override
	public boolean isMobile() {
		if (game == null) {
			return false;
		}
		return super.isMobile() || game.isMobile();
	}

}
