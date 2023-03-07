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
package loon.fx;

import javafx.animation.AnimationTimer;

import loon.Accelerometer;
import loon.Assets;
import loon.Asyn;
import loon.Clipboard;
import loon.Graphics;
import loon.LGame;
import loon.LSetting;
import loon.Log;
import loon.Platform;
import loon.Save;
import loon.canvas.Canvas;
import loon.events.InputMake;
import loon.opengl.Mesh;
import loon.utils.StringUtils;

public class JavaFXGame extends LGame {

	static private boolean osIsAndroid;

	final static private boolean osIsLinux;

	final static private boolean osIsUnix;

	final static private boolean osIsMacOs;

	final static private boolean osIsWindows;

	final static private boolean osBit64;

	final static private String OS_ARCH;

	final static private String OS_NAME;

	final static private String JAVA_SPEC;

	final static private String JAVA_VERSION;

	final static private String JAVAFX_VERSION;

	final static private String JAVAFX_PLATFORM;

	static {
		OS_NAME = getProperty("os.name").toLowerCase();
		JAVA_SPEC = getProperty("java.specification.version").toLowerCase();
		JAVA_VERSION = getProperty("java.version").toLowerCase();
		JAVAFX_VERSION = getProperty("javafx.version").toLowerCase();
		JAVAFX_PLATFORM = getProperty("javafx.platform").toLowerCase();
		osIsLinux = OS_NAME.indexOf("linux") != -1;
		osIsUnix = OS_NAME.indexOf("nix") != -1 || OS_NAME.indexOf("nux") != 1;
		osIsMacOs = OS_NAME.indexOf("mac") != -1;
		osIsWindows = OS_NAME.indexOf("windows") != -1;
		OS_ARCH = getProperty("os.arch");
		osBit64 = OS_ARCH.indexOf("amd64") != -1 || OS_ARCH.indexOf("x86_64") != -1;
		checkAndroid();
	}

	public static boolean isJavaVersion(String versionPrefix) {
		return JAVA_SPEC.indexOf(versionPrefix) != -1;
	}

	public static String getJavaVersion() {
		return JAVA_VERSION;
	}

	public static String getJavaFXVersion() {
		return JAVAFX_VERSION;
	}

	public static boolean checkAndroid() {
		if (osIsAndroid) {
			return osIsAndroid;
		}
		String jvm = getProperty("java.runtime.name").toLowerCase();
		if (jvm.indexOf("android runtime") != -1) {
			return (osIsAndroid = true);
		}
		try {
			Class.forName("android.Manifest");
			return (osIsAndroid = true);
		} catch (Throwable cause) {
			osIsAndroid = false;
		}
		return osIsAndroid;
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

	public static boolean isAndroid() {
		return osIsAndroid;
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

	protected AnimationTimer loopRunner;

	private final JavaFXAccelerometer accelerometer;

	private final JavaFXSave save;
	private final JavaFXGraphics graphics;
	private final JavaFXAssets assets;
	private final JavaFXLog log;
	private final Asyn asyn;

	private final long start = System.nanoTime();

	private JavaFXInputMake input;

	private boolean active = true;

	public JavaFXGame(Platform plat, LSetting config) {
		super(config, plat);
		this.graphics = new JavaFXGraphics(this);
		this.input = new JavaFXInputMake(this);
		this.assets = new JavaFXAssets(this);
		this.log = new JavaFXLog();
		this.save = new JavaFXSave(log, config.appName);
		this.accelerometer = new JavaFXAccelerometer();
		this.asyn = new JavaFXAsyn(log, frame);
		this.initProcess();
	}

	protected static String getProperty(String value) {
		return getProperty(value, "");
	}

	protected static String getProperty(String value, String def) {
		String result = null;
		try {
			result = System.getProperty(value, def).trim();
		} catch (Throwable cause) {
			result = "";
		}
		return result;
	}

	public static boolean isJavaFXDesktop() {
		String result = getJavaFXProperty();
		return (result.indexOf("desktop") != -1 || result.indexOf("mac") != -1 || result.indexOf("win") != -1
				|| result.indexOf("linux") != -1) && !isAndroid();
	}

	public static String getJavaFXProperty() {
		String glass = getProperty("glass.platform", null);
		if (!StringUtils.isEmpty(glass)) {
			return glass.trim().toLowerCase();
		}
		String monocle = getProperty("monocle.platform", null);
		if (!StringUtils.isEmpty(monocle)) {
			return monocle.trim().toLowerCase();
		}
		return getProperty("javafx.platform", "desktop").trim().toLowerCase();
	}

	protected void toggleActivation() {
		active = !active;
	}

	protected void start() {
		if (loopRunner == null) {
			init();
		}
		loopRunner.start();
	}

	public LGame resume() {
		if (loopRunner == null) {
			return this;
		}
		start();
		return this;
	}

	public LGame pause() {
		stop();
		return this;
	}

	public void stop() {
		if (loopRunner == null) {
			return;
		}
		loopRunner.stop();
	}

	protected void init() {
		if (loopRunner != null) {
			loopRunner.stop();
			loopRunner = null;
		}
	}

	public void reset() {
		init();
		loopRunner = new AnimationTimer() {

			boolean wasActive = active;

			@Override
			public void handle(long time) {
				if (wasActive != active) {
					status.emit(wasActive ? Status.PAUSE : Status.RESUME);
					wasActive = active;
				}
				if (active) {
					emitFrame();
				}
			}
		};
		loopRunner.start();
	}

	protected void shutdown() {
		if (status.isClosed()) {
			return;
		}
		status.emit(Status.EXIT);
		stop();
		System.exit(0);
	}

	@Override
	public Environment env() {
		return Environment.JAVAFX;
	}

	@Override
	public double time() {
		return System.currentTimeMillis();
	}

	@Override
	public int tick() {
		return (int) ((System.nanoTime() - start) / 1000000L);
	}

	public JavaFXResizeCanvas getFxCanvas() {
		return graphics.canvas.fxCanvas;
	}

	@Override
	public Assets assets() {
		return this.assets;
	}

	@Override
	public Asyn asyn() {
		return this.asyn;
	}

	@Override
	public Graphics graphics() {
		return this.graphics;
	}

	@Override
	public InputMake input() {
		return this.input;
	}

	@Override
	public Log log() {
		return this.log;
	}

	@Override
	public Save save() {
		return this.save;
	}

	@Override
	public Accelerometer accel() {
		return this.accelerometer;
	}

	@Override
	public boolean isMobile() {
		Sys sys = getPlatform();
		return (!isDesktop()) || sys == Sys.IOS || sys == Sys.ANDROID;
	}

	@Override
	public boolean isDesktop() {
		Sys sys = getPlatform();
		return isJavaFXDesktop() || sys == Sys.WINDOWS || sys == Sys.LINUX || sys == Sys.MAC;
	}

	@Override
	public boolean isBrowser() {
		return !isDesktop() && !isMobile();
	}

	public String getProperty() {
		return getJavaFXProperty();
	}

	public String getDevice() {
		return getProperty(OS_ARCH).toLowerCase();
	}

	public boolean isARMDevice() {
		return getDevice().indexOf("arm") != -1;
	}

	@Override
	public Sys getPlatform() {
		if (JAVAFX_PLATFORM.contains("android") || isAndroid()) {
			return Sys.ANDROID;
		}
		if (JAVAFX_PLATFORM.contains("ios")) {
			return Sys.IOS;
		}
		String monoclePlatformName = getProperty("monocle.platform", "");
		String glassPlatformName = getProperty("glass.platform", "");
		if (monoclePlatformName == "EGL" && glassPlatformName == "Monocle") {
			return Sys.EMBEDDED;
		}
		if (isMacOS()) {
			return Sys.MAC;
		}
		if (isLinux()) {
			return Sys.LINUX;
		}
		if (isWindows()) {
			return Sys.WINDOWS;
		}
		return Sys.BROWSER;
	}

	@Override
	public Clipboard clipboard() {
		return new JavaFXClipboard();
	}

	@Override
	public Mesh makeMesh(Canvas canvas) {
		return new JavaFXMesh(canvas);
	}

}
