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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import loon.Accelerometer;
import loon.Assets;
import loon.Asyn;
import loon.Graphics;
import loon.LGame;
import loon.LSetting;
import loon.Log;
import loon.Platform;
import loon.Save;
import loon.Support;
import loon.canvas.Canvas;
import loon.event.InputMake;
import loon.opengl.Mesh;
import loon.utils.StringUtils;

public class JavaFXGame extends LGame {

	public static class JavaFXSetting extends LSetting {

		public boolean resizable = true;

		public String[] iconPaths = null;
	}

	static private boolean osIsAndroid;

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
		checkAndroid();
	}

	public static boolean isJavaVersion(String versionPrefix) {
		return JAVA_SPEC.indexOf(versionPrefix) != -1;
	}

	public static String getJavaVersion() {
		return JAVA_VERSION;
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
	private final Support support;

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
		this.support = new NativeSupport();
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

	protected void resume() {
		if (loopRunner == null) {
			return;
		}
		start();
	}

	protected void pause() {
		stop();
	}

	protected void stop() {
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
				GraphicsContext gc = graphics.canvas.fxCanvas.getGraphicsContext2D();
				gc.save();
				if (wasActive != active) {
					status.emit(wasActive ? Status.PAUSE : Status.RESUME);
					wasActive = active;
				}
				if (active) {
					emitFrame();
				}
				gc.restore();
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
	public Type type() {
		return Type.JAVAFX;
	}

	@Override
	public double time() {
		return System.currentTimeMillis();
	}

	@Override
	public int tick() {
		return (int) ((System.nanoTime() - start) / 1000000L);
	}

	@Override
	public void openURL(final String url) {

		invokeAsync(new Runnable() {

			@Override
			public void run() {

				final Scene scene = new Scene(new Group());

				final Stage newStage = new Stage();
				final WebView view = new WebView();
				final WebEngine engine = view.getEngine();

				final ScrollPane scrollPane = new ScrollPane();
				scrollPane.setContent(view);

				engine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {

					@Override
					public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {

						if (newState == Worker.State.SUCCEEDED) {
							newStage.setTitle(engine.getLocation());
						}
						if (newState == Worker.State.FAILED || newState == Worker.State.CANCELLED) {
							newStage.close();
						}

					}

				});
				engine.load(url);

				scene.setRoot(scrollPane);

				newStage.setScene(scene);
				newStage.show();

			}
		});

	}

	public javafx.scene.canvas.Canvas getFxCanvas() {
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
	public Support support() {
		return support;
	}

	@Override
	public Mesh makeMesh(Canvas canvas) {
		return new JavaFXMesh(canvas);
	}

	@Override
	public boolean isMobile() {
		return !isDesktop();
	}

	@Override
	public boolean isDesktop() {
		return isJavaFXDesktop();
	}

	public String getProperty() {
		return getJavaFXProperty();
	}

	public String getDevice() {
		return getProperty("os.arch").toLowerCase();
	}

	public boolean isARMDevice() {
		return getDevice().indexOf("arm") != -1;
	}

}
