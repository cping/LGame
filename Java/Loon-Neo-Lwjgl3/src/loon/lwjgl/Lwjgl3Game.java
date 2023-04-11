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
package loon.lwjgl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import loon.*;
import loon.canvas.Image;
import loon.canvas.Pixmap;
import loon.events.KeyMake;
import loon.geom.Dimension;
import loon.jni.NativeSupport;
import loon.utils.Scale;
import loon.utils.reply.Port;

public class Lwjgl3Game extends LGame {

	private Lwjgl3Sync sync;

	private final long windowId;

	public static class JavaSetting extends LSetting {
		public boolean vSyncEnabled = true;
		public String[] iconPaths = null;
		public int synMode = Lwjgl3Sync.LWJGL_GLFW;
	}

	final static private Runtime systemRuntime = Runtime.getRuntime();

	final static Support support = new Lwjgl3Support();

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

	private final Lwjgl3Log log = new Lwjgl3Log();
	private final Lwjgl3Asyn asyn = new Lwjgl3Asyn(pool, log, frame);

	private final Lwjgl3Accelerometer accelerometer = new Lwjgl3Accelerometer();
	private final Lwjgl3Save save;
	private final Lwjgl3ImplGraphics graphics;
	private final Lwjgl3Input input;
	private final Lwjgl3Clipboard clipboard;
	private final Lwjgl3Assets assets = new Lwjgl3Assets(this);

	public static class Headless extends Lwjgl3Game {
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
		protected Lwjgl3ImplGraphics createGraphics() {
			return new Lwjgl3ImplGraphics(this, null, Scale.ONE) {
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
		protected Lwjgl3Input createInput() {
			return new Lwjgl3Input(this);
		}

	}

	private final GLFWErrorCallback errorCallback;

	public Lwjgl3Game(final Loon game, final LSetting config) {
		super(config, game);
		this.preInit();

		if (isMacOS()) {
			System.setProperty("java.awt.headless", "true");
		}
		if (config instanceof JavaSetting) {
			this.sync = new Lwjgl3Sync(((JavaSetting) config).synMode);
		} else {
			this.sync = new Lwjgl3Sync(Lwjgl3Sync.LWJGL_GLFW);
		}
		glfwSetErrorCallback(errorCallback = new GLFWErrorCallback() {
			@Override
			public void invoke(int error, long description) {
				log().error("GL Error (" + error + "):" + getDescription(description));
			}
		});
		if (!glfwInit())
			throw new RuntimeException("Failed to init GLFW.");

		long monitor = glfwGetPrimaryMonitor();
		GLFWVidMode vidMode = glfwGetVideoMode(monitor);

		int width = config.getShowWidth(), height = config.getShowHeight();
		if (config.fullscreen) {
			width = vidMode.width();
			height = vidMode.height();
		} else {
			monitor = 0;
		}
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
		glfwWindowHint(GLFW_RED_BITS, 8);
		glfwWindowHint(GLFW_GREEN_BITS, 8);
		glfwWindowHint(GLFW_BLUE_BITS, 8);
		glfwWindowHint(GLFW_ALPHA_BITS, 8);
		glfwWindowHint(GLFW_STENCIL_BITS, 0);
		glfwWindowHint(GLFW_DEPTH_BITS, 16);
		glfwWindowHint(GLFW_SAMPLES, 0);
		windowId = glfwCreateWindow(width, height, config.appName, monitor, 0);
		if (windowId == 0) {
			throw new RuntimeException("Failed to create windowId; see error log.");
		}
		this.graphics = createGraphics();
		this.input = createInput();

		graphics.setSize(width, height, config.fullscreen);

		glfwMakeContextCurrent(windowId);

		if (config instanceof JavaSetting) {
			glfwSwapInterval(((JavaSetting) config).vSyncEnabled ? 1 : 0);
		} else {
			glfwSwapInterval(0);
		}
		GL.createCapabilities();

		this.save = new Lwjgl3Save(log, config.appName);
		this.clipboard = new Lwjgl3Clipboard();

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

		this.setTitle(config.appName);
		this.initProcess();
		if (config instanceof JavaSetting) {
			setIcon(windowId, ((JavaSetting) config).iconPaths);
		}

		this.graphics.init();
		this.input.init();
		glfwShowWindow(windowId);
		for (int i = 0; i < 2; i++) {
			GL11.glClearColor(0, 0, 0, 1);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			glfwSwapBuffers(windowId);
		}

	}

	public long getWindowHandle() {
		return windowId;
	}

	public void setTitle(String title) {
		((Lwjgl3Graphics) graphics()).setTitle(title);
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
	public Lwjgl3Assets assets() {
		return assets;
	}

	@Override
	public Lwjgl3Asyn asyn() {
		return asyn;
	}

	@Override
	public Lwjgl3ImplGraphics graphics() {
		return graphics;
	}

	@Override
	public Lwjgl3Input input() {
		return input;
	}

	@Override
	public Lwjgl3Log log() {
		return log;
	}

	@Override
	public Lwjgl3Save save() {
		return save;
	}

	@Override
	public Lwjgl3Accelerometer accel() {
		return accelerometer;
	}

	@Override
	public Lwjgl3Clipboard clipboard() {
		return clipboard;
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
			NativeSupport.loadJNI("lplus");
		} catch (Throwable exc) {
			exc.printStackTrace();
		}
	}

	protected Lwjgl3ImplGraphics createGraphics() {
		return new Lwjgl3Graphics(this, windowId);
	}

	protected Lwjgl3Input createInput() {
		return new Lwjgl3InputMake(this, windowId);
	}

	public void shutdown() {
		super.shutdown();
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

	static void setIcon(long win, String[] imagePaths) {
		if (isMacOS()) {
			return;
		}
		if (imagePaths == null || imagePaths.length == 0) {
			return;
		}
		Pixmap[] pixmaps = new Pixmap[imagePaths.length];
		for (int i = 0; i < imagePaths.length; i++) {
			pixmaps[i] = Image.createImage(imagePaths[i]).getPixmap();
		}
		setIcon(win, pixmaps);
		for (Pixmap pixmap : pixmaps) {
			pixmap.close();
		}
	}

	static void setIcon(long win, Pixmap[] images) {
		if (isMacOS()) {
			return;
		}
		if (images == null || images.length == 0) {
			return;
		}
		GLFWImage.Buffer buffer = GLFWImage.malloc(images.length);
		Pixmap[] tmpPixmaps = new Pixmap[images.length];

		for (int i = 0; i < images.length; i++) {
			Pixmap pixmap = images[i];

			GLFWImage icon = GLFWImage.malloc();
			icon.set(pixmap.getWidth(), pixmap.getHeight(), pixmap.convertPixmapToByteBuffer(true));
			buffer.put(icon);

			icon.free();
		}

		buffer.position(0);
		glfwSetWindowIcon(win, buffer);

		buffer.free();
		for (Pixmap pixmap : tmpPixmaps) {
			if (pixmap != null) {
				pixmap.close();
			}
		}
	}

	public void reset() {
		boolean wasActive = glfwGetWindowAttrib(windowId, GLFW_VISIBLE) > 0;
		while (!glfwWindowShouldClose(windowId)) {
			boolean newActive = active && glfwGetWindowAttrib(windowId, GLFW_VISIBLE) > 0;
			if (wasActive != newActive) {
				status.emit(wasActive ? Status.PAUSE : Status.RESUME);
				wasActive = newActive;
			}
			if (newActive) {
				processFrame();
			}
			glfwPollEvents();
			sync.sync(setting.fps);
			glfwSwapBuffers(windowId);
		}
		((Lwjgl3InputMake) input).shutdown();
		((Lwjgl3Graphics) graphics).shutdown();
		errorCallback.close();
		shutdown();
		glfwDestroyWindow(windowId);
		glfwTerminate();
	}

}
