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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import loon.*;
import loon.event.KeyMake;
import loon.geom.Dimension;
import loon.utils.Scale;
import loon.utils.json.JsonImpl;
import loon.utils.reply.Port;

public abstract class JavaSEGame extends LGame {

	final static private boolean osIsLinux;

	final static private boolean osIsUnix;

	final static private boolean osIsMacOs;

	final static private boolean osIsWindows;

	final static private boolean osIsWindowsXP;

	final static private boolean osIsWindows2003;

	final static private boolean osBit64;

	final static public String OS_NAME;

	final static public int JAVA_13 = 0;

	final static public int JAVA_14 = 1;

	final static public int JAVA_15 = 2;

	final static public int JAVA_16 = 3;

	final static public int JAVA_17 = 4;

	final static public int JAVA_18 = 5;

	final static public int JAVA_19 = 6;

	final static Support support = new JavaSESupport();

	static {
		OS_NAME = System.getProperty("os.name").toLowerCase();
		osIsLinux = OS_NAME.indexOf("linux") != -1;
		osIsUnix = OS_NAME.indexOf("nix") != -1 || OS_NAME.indexOf("nux") != 1;
		osIsMacOs = OS_NAME.indexOf("mac") != -1;
		osIsWindows = OS_NAME.indexOf("windows") != -1;
		osIsWindowsXP = OS_NAME.startsWith("Windows")
				&& (OS_NAME.compareTo("5.1") >= 0);
		osIsWindows2003 = "windows 2003".equals(OS_NAME);
		osBit64 = System.getProperty("os.arch").equals("amd64");
	}

	final private static Runtime systemRuntime = Runtime.getRuntime();

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

	public static boolean isWindowsXP() {
		return osIsWindowsXP;
	}

	public static boolean isWindows2003() {
		return osIsWindows2003;
	}

	public static boolean isBit64() {
		return osBit64;
	}

	public static boolean isSun() {
		return System.getProperty("java.vm.vendor").indexOf("Sun") != -1
				|| System.getProperty("java.vm.vendor").indexOf("Oracle") != -1;
	}

	public static boolean isApple() {
		return System.getProperty("java.vm.vendor").indexOf("Apple") != -1;
	}

	public static boolean isHPUX() {
		return System.getProperty("java.vm.vendor").indexOf(
				"Hewlett-Packard Company") != -1;
	}

	public static boolean isIBM() {
		return System.getProperty("java.vm.vendor").indexOf("IBM") != -1;
	}

	public static boolean isBlackdown() {
		return System.getProperty("java.vm.vendor").indexOf("Blackdown") != -1;
	}

	private boolean active = true;
	private final long start = System.nanoTime();
	private final ExecutorService pool = Executors.newFixedThreadPool(4);

	private final JavaSELog log = new JavaSELog();
	private final Asyn asyn = new JavaSEAsyn(pool, log, frame);

	private final JavaSESave save;
	private final JsonImpl json = new JsonImpl();
	private final JavaSEGraphics graphics;
	private final JavaSEInputMake input;
	private final JavaSEAssets assets = new JavaSEAssets(this);

	public static class Headless extends JavaSEGame {
		public Headless(LSetting config) {
			super(config);
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
					setSize(game.setting.width, game.setting.height,
							game.setting.fullscreen);
				}

				@Override
				public void setSize(int width, int height, boolean fullscreen) {
					updateViewport(Scale.ONE, width, height);
				}

				@Override
				public Dimension screenSize() {
					return new Dimension(game.setting.width,
							game.setting.height);
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

	public JavaSEGame(final LSetting config) {
		super(config);

		this.preInit();
		this.graphics = createGraphics();
		this.input = createInput();
		this.save = new JavaSESave(log, config.appName);

		if (config.activationKey != -1) {
			input.keyboardEvents.connect(new Port<KeyMake.Event>() {
				public void onEmit(KeyMake.Event event) {
					if (event instanceof KeyMake.KeyEvent) {
						KeyMake.KeyEvent kevent = (KeyMake.KeyEvent) event;
						if (kevent.keyCode == config.activationKey
								&& kevent.down) {
							toggleActivation();
						}
					}
				}
			});
		}

		this.setTitle(config.appName);
		this.graphics.init();
		this.input.init();
		this.initProcess();
	}

	public abstract void setTitle(String title);

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

	private static void browse(String url) throws Exception {
		String osName = System.getProperty("os.name", "");
		if (osName.startsWith("Mac OS")) {
			Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
			Method openURL = fileMgr.getDeclaredMethod("openURL",
					new Class[] { String.class });
			openURL.invoke(null, new Object[] { url });
		} else if (osName.startsWith("Windows")) {
			Runtime.getRuntime().exec(
					"rundll32 url.dll,FileProtocolHandler " + url);
		} else {
			String[] browsers = { "firefox", "opera", "konqueror", "epiphany",
					"mozilla", "netscape" };
			String browser = null;
			for (int count = 0; count < browsers.length && browser == null; count++) {
				if (Runtime.getRuntime()
						.exec(new String[] { "which", browsers[count] })
						.waitFor() == 0) {
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
						File iexplore = new File(
								"C:\\Program Files\\Internet Explorer\\iexplore.exe");
						if (iexplore.exists()) {
							systemRuntime.exec(iexplore.getAbsolutePath()
									+ " \"" + url + "\"");
						} else {
							systemRuntime
									.exec("rundll32 url.dll,FileProtocolHandler "
											+ url);
						}
					} else if (isMacOS()) {
						systemRuntime.exec("open " + url);
					} else if (isUnix()) {
						String[] browsers = { "epiphany", "firefox", "mozilla",
								"konqueror", "netscape", "opera", "links",
								"lynx" };
						StringBuffer cmd = new StringBuffer();
						for (int i = 0; i < browsers.length; i++) {
							cmd.append((i == 0 ? "" : " || ") + browsers[i]
									+ " \"" + url + "\" ");
						}
						systemRuntime.exec(new String[] { "sh", "-c",
								cmd.toString() });
					}
				} catch (IOException ex) {
					e.printStackTrace();
				}
			}
		}
	}

	protected abstract void preInit();

	protected abstract JavaSEGraphics createGraphics();

	protected abstract JavaSEInputMake createInput();

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
}
