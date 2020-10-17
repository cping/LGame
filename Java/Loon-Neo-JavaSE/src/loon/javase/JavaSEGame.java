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
package loon.javase;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.lwjgl.opengl.Display;

import loon.*;
import loon.canvas.Image;
import loon.canvas.Pixmap;
import loon.events.KeyMake;
import loon.geom.Dimension;
import loon.jni.NativeSupport;
import loon.utils.Scale;
import loon.utils.reply.Port;

public class JavaSEGame extends LGame {

	public static class JavaSetting extends LSetting {
		public boolean vSyncEnabled = true;
		public String[] iconPaths = null;
	}

	final static private Runtime systemRuntime = Runtime.getRuntime();

	final static Support support = new JavaSESupport();

	final static private boolean osIsLinux;

	final static private boolean osIsUnix;

	final static private boolean osIsMacOs;

	final static private boolean osIsWindows;

	final static private boolean osBit64;

	final static private String OS_NAME;

	final static private String JAVA_SPEC;

	final static private String JAVA_VERSION;

	static {
		OS_NAME = getProperty("os.name").toLowerCase();
		JAVA_SPEC = getProperty("java.specification.version").toLowerCase();
		JAVA_VERSION = getProperty("java.version").toLowerCase();
		osIsLinux = OS_NAME.indexOf("linux") != -1;
		osIsUnix = OS_NAME.indexOf("nix") != -1 || OS_NAME.indexOf("nux") != 1;
		osIsMacOs = OS_NAME.indexOf("mac") != -1;
		osIsWindows = OS_NAME.indexOf("windows") != -1;
		String arch = getProperty("os.arch");
		osBit64 = arch.indexOf("amd64") != -1 || arch.indexOf("x86_64") != -1;
	}

	protected static String getProperty(String value) {
		String result = null;
		try {
			result = System.getProperty(value, "").trim();
		} catch (Throwable cause) {
			result = "";
		}
		return result;
	}

	public static boolean isJavaVersion(String versionPrefix) {
		return JAVA_SPEC.indexOf(versionPrefix) != -1;
	}

	public static String getJavaVersion() {
		return JAVA_VERSION;
	}

	public static boolean isSun() {
		return getProperty("java.vm.vendor").indexOf("Sun") != -1
				|| getProperty("java.vm.vendor").indexOf("Oracle") != -1;
	}

	public static boolean isApple() {
		return getProperty("java.vm.vendor").indexOf("Apple") != -1;
	}

	public static boolean isHPUX() {
		return getProperty("java.vm.vendor").indexOf("Hewlett-Packard Company") != -1;
	}

	public static boolean isIBM() {
		return getProperty("java.vm.vendor").indexOf("IBM") != -1;
	}

	public static boolean isBlackdown() {
		return getProperty("java.vm.vendor").indexOf("Blackdown") != -1;
	}
	
	public static boolean isLinux() {
		return osIsLinux;
	}

	public static boolean isMacOS() {
		return osIsMacOs;
	}

	public static boolean isUnix() {
		return osIsUnix;
	}

	public static boolean isWindows() {
		return osIsWindows;
	}

	public static boolean isBit64() {
		return osBit64;
	}

	private boolean active = true;
	private final long start = System.nanoTime();
	private final ExecutorService pool = Executors.newFixedThreadPool(4);

	private final JavaSELog log = new JavaSELog();
	private final Asyn asyn = new JavaSEAsyn(pool, log, frame);

	private final JavaSEAccelerometer accelerometer = new JavaSEAccelerometer();
	private final JavaSESave save;
	private final JavaSEGraphics graphics;
	private final JavaSEInputMake input;
	private final JavaSEAssets assets = new JavaSEAssets(this);

	public static class Headless extends JavaSEGame {
		public Headless(Loon game, LSetting config) {
			super(game, config);
		}

		@Override
		public void setTitle(String title) {
		}

		@Override
		protected void preInit() {
		}

		@Override
		protected JavaSEGraphics createGraphics() {
			return new JavaSEGraphics(this, null, Scale.ONE) {
				{
					setSize(game.setting.width, game.setting.height, game.setting.fullscreen);
				}

				@Override
				public void setSize(int width, int height, boolean fullscreen) {
					updateViewport(Scale.ONE, width, height);
				}

				@Override
				public Dimension screenSize() {
					return new Dimension(game.setting.width, game.setting.height);
				}

				@Override
				protected void init() {
				}

				@Override
				protected void upload(BufferedImage img, LTexture tex) {
				}
			};
		}

		@Override
		protected JavaSEInputMake createInput() {
			return new JavaSEInputMake(this);
		}

	}

	public JavaSEGame(final Loon game, final LSetting config) {
		super(config, game);
		this.preInit();
		this.graphics = createGraphics();
		this.input = createInput();
		this.save = new JavaSESave(log, config.appName);
		if (config.activationKey != -1) {
			input.keyboardEvents.connect(new Port<KeyMake.Event>() {
				public void onEmit(KeyMake.Event event) {
					if (event instanceof KeyMake.KeyEvent) {
						KeyMake.KeyEvent kevent = (KeyMake.KeyEvent) event;
						if (kevent.keyCode == config.activationKey && kevent.down) {
							toggleActivation();
						}
					}
				}
			});
		}
		Display.setInitialBackground(0, 0, 0);
		this.setTitle(config.appName);
		this.initProcess();
		if (setting instanceof JavaSetting) {
			setIcon(((JavaSetting) setting).iconPaths);
		}
		this.graphics.init();
		this.input.init();

	}

	public void setTitle(String title) {
		Display.setTitle(title);
	}

	@Override
	public double time() {
		return System.currentTimeMillis();
	}

	@Override
	public Type type() {
		return Type.JAVASE;
	}

	@Override
	public int tick() {
		return (int) ((System.nanoTime() - start) / 1000000L);
	}

	@Override
	public JavaSEAssets assets() {
		return assets;
	}

	@Override
	public Asyn asyn() {
		return asyn;
	}

	@Override
	public JavaSEGraphics graphics() {
		return graphics;
	}

	@Override
	public JavaSEInputMake input() {
		return input;
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
	public Accelerometer accel() {
		return accelerometer;
	}

	@Override
	public Support support() {
		return support;
	}

	private static void browse(String url) throws Exception {
		if (isMacOS()) {
			Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
			Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] { String.class });
			openURL.invoke(null, new Object[] { url });
		} else if (isWindows()) {
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
		} else {
			String[] browsers = { "google-chrome", "firefox", "mozilla", "opera", "epiphany", "konqueror", "netscape",
					"links", "lynx", "epiphany", "conkeror", "midori", "kazehakase", };
			String browser = null;
			for (int count = 0; count < browsers.length && browser == null; count++) {
				if (Runtime.getRuntime().exec(new String[] { "which", browsers[count] }).waitFor() == 0) {
					browser = browsers[count];
				}
			}
			if (browser == null) {
				throw new Exception("Could not find web browser");
			} else {
				Runtime.getRuntime().exec(new String[] { browser, url });
			}
		}
	}

	@Override
	public void openURL(String url) {
		try {
			java.net.URI uri = new java.net.URI(url);
			java.awt.Desktop.getDesktop().browse(uri);
		} catch (Throwable e) {
			try {
				browse(url);
			} catch (Throwable err) {
				try {
					if (isWindows()) {
						File iexplore = new File("C:\\Program Files\\Internet Explorer\\iexplore.exe");
						if (iexplore.exists()) {
							systemRuntime.exec(iexplore.getAbsolutePath() + " \"" + url + "\"");
						} else {
							File chrome = new File("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
							if (chrome.exists()) {
								systemRuntime.exec(chrome.getAbsolutePath() + " \"" + url + "\"");
								return;
							}
							chrome = new File("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
							if (chrome.exists()) {
								systemRuntime.exec(chrome.getAbsolutePath() + " \"" + url + "\"");
								return;
							}
							systemRuntime.exec("rundll32 url.dll,FileProtocolHandler " + url);
						}
					} else if (isMacOS()) {
						systemRuntime.exec("open " + url);
					} else if (isUnix()) {
						String[] browsers = { "google-chrome", "firefox", "mozilla", "opera", "epiphany", "konqueror",
								"netscape", "links", "lynx", "epiphany", "conkeror", "midori", "kazehakase" };
						StringBuffer cmd = new StringBuffer();
						for (int i = 0; i < browsers.length; i++) {
							cmd.append((i == 0 ? "" : " || ") + browsers[i] + " \"" + url + "\" ");
						}
						systemRuntime.exec(new String[] { "sh", "-c", cmd.toString() });
					}
				} catch (IOException ex) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void preInit() {
		try {
			NativeSupport.loadJNI("lwjgl");
			NativeSupport.loadJNI("lplus");
		} catch (Throwable exc) {
			exc.printStackTrace();
		}
	}

	protected JavaSEGraphics createGraphics() {
		return new JavaSELwjglGraphics(this);
	}

	protected JavaSEInputMake createInput() {
		return new JavaSELwjglInputMake(this);
	}

	protected void shutdown() {
		status.emit(Status.EXIT);
		try {
			pool.shutdown();
			pool.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException ie) {
		}
		System.exit(0);
	}

	protected void processFrame() {
		try {
			input.update();
		} catch (Exception e) {
			log.warn("your system input exception !", e);
		}
		emitFrame();
	}

	protected void toggleActivation() {
		active = !active;
	}

	static void setIcon(String[] imagePaths) {
		if (imagePaths == null || imagePaths.length == 0) {
			return;
		}
		Pixmap[] pixmaps = new Pixmap[imagePaths.length];
		for (int i = 0; i < imagePaths.length; i++) {
			pixmaps[i] = Image.createImage(imagePaths[i]).getPixmap();
		}
		setIcon(pixmaps);
		for (Pixmap pixmap : pixmaps) {
			pixmap.close();
		}
	}

	static void setIcon(Pixmap[] pixmap) {
		if (pixmap == null || pixmap.length == 0) {
			return;
		}
		int size = pixmap.length;
		ByteBuffer[] buffers = new ByteBuffer[size];
		for (int i = 0; i < size; i++) {
			buffers[i] = pixmap[i].convertPixmapToByteBuffer(true);
		}
		Display.setIcon(buffers);
	}

	public void reset() {
		boolean wasActive = Display.isActive();
		while (!Display.isCloseRequested()) {
			boolean newActive = active && Display.isActive();
			if (wasActive != newActive) {
				status.emit(wasActive ? Status.PAUSE : Status.RESUME);
				wasActive = newActive;
			}
			((JavaSELwjglGraphics) graphics()).checkScaleFactor();
			if (newActive) {
				processFrame();
			}
			Display.processMessages();
			Display.update();
			Display.sync(setting.fps);
		}
		shutdown();
	}
}
