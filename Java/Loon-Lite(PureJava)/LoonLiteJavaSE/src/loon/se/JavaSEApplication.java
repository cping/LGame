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
package loon.se;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.swing.SwingUtilities;

import loon.LGame;
import loon.LSysException;
import loon.LazyLoading;
import loon.Platform;
import loon.canvas.Image;
import loon.canvas.Pixmap;
import loon.events.KeyMake;
import loon.events.SysInput;
import loon.geom.Vector2f;
import loon.se.window.JavaSEAppCanvas;
import loon.se.window.JavaSEAppFrame;
import loon.utils.MathUtils;
import loon.utils.Resolution;
import loon.utils.StringUtils;

public class JavaSEApplication implements Platform {

	final static private RenderingHints VALUE_TEXT_ANTIALIAS_ON = new RenderingHints(
			RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

	final static private RenderingHints VALUE_TEXT_ANTIALIAS_OFF = new RenderingHints(
			RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

	final static RenderingHints _Excellent;

	final static RenderingHints _Quality;

	final static RenderingHints _Speed;

	static {
		_Excellent = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		_Excellent.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		_Excellent.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		_Excellent.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		_Excellent.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		_Excellent.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		_Excellent.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		_Excellent.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		_Excellent.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		_Excellent.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		_Quality = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		_Quality.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		_Quality.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		_Quality.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		_Quality.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		_Quality.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		_Quality.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		_Quality.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		_Speed = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		_Speed.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		_Speed.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		_Speed.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		_Speed.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		_Speed.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		_Speed.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
		_Speed.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		_Speed.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		_Speed.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		_Speed.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		_Speed.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		_Speed.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);

	}

	public static void setRenderingHints(Graphics2D g2d, boolean smooth, boolean antialiasing) {
		if (smooth) {
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		} else {
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		}
		if (antialiasing) {
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		} else {
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}
	}

	public static void setAntialias(Graphics2D g2d, boolean flag) {
		if (flag) {
			g2d.setRenderingHints(VALUE_TEXT_ANTIALIAS_ON);
		} else {
			g2d.setRenderingHints(VALUE_TEXT_ANTIALIAS_OFF);
		}
	}

	protected JavaSEGame game;

	protected JavaSESetting appSetting;

	protected LazyLoading.Data lazyData;

	protected Class<?> mainClass;

	protected GraphicsConfiguration config;

	private JavaSEAppFrame _appFrame;

	private JavaSEAppCanvas _appCanvas;

	private static void configDrawMethod() {
		System.clearProperty("sun.java2d.d3d");
		System.clearProperty("sun.java2d.opengl");
		System.clearProperty("sun.java2d.metal");
		if (JavaSEGame.isMacOS()) {
			System.setProperty("sun.java2d.metal", "true");
		} else {
			System.setProperty("sun.java2d.opengl", "true");
		}
		System.setProperty("sun.java2d.translaccel", "true");
		System.setProperty("sun.java2d.ddforcevram", "true");
	}

	public static JavaSEAppFrame launch(Loon app, JavaSESetting setting, LazyLoading.Data lazy, String[] args) {
		configDrawMethod();
		return new JavaSEApplication(app, setting, lazy, args).runFrame();
	}

	public JavaSEApplication(Loon app, JavaSESetting setting, LazyLoading.Data lazy, String[] args) {
		this.config = getGraphicsConfiguration();
		this.lazyData = lazy;
		this.appSetting = setting;
		if (this.appSetting.args == null) {
			this.appSetting.args = args;
		}
		if (this.appSetting.fullscreen) {
			Rectangle rect = config.getBounds();
			this.appSetting.width_zoom = (int) rect.getWidth();
			this.appSetting.height_zoom = (int) rect.getHeight();
		}
		this.appSetting.mainClass = app.getClass();
		this.game = new JavaSEGame(this, this.appSetting);
	}

	protected void loadScreen() {
		try {
			game.register(lazyData.onScreen());
			game.init();
		} catch (Exception e) {
			e.printStackTrace();
			throw new LSysException(e.getMessage());
		}
	}

	public JavaSEAppCanvas runCanvas() {
		loadScreen();
		_appCanvas = new JavaSEAppCanvas(config, game, appSetting);
		_appCanvas.updateSize();
		_appCanvas.start();
		return _appCanvas;
	}

	public JavaSEAppFrame runFrame() {
		loadScreen();
		_appFrame = new JavaSEAppFrame(config, game, appSetting);
		_appCanvas = new JavaSEAppCanvas(config, game, appSetting);
		_appFrame.playCanvas(_appCanvas);
		if (appSetting.fullscreen) {
			_appFrame.full();
		}
		return _appFrame;
	}

	public boolean isActive() {
		return game == null ? false : game.isActive();
	}

	public JavaSEApplication setSize(int width, int height) {
		if (_appFrame == null && _appCanvas == null) {
			return this;
		}
		if (height <= 0) {
			height = 1;
		}
		if (width <= 0) {
			width = 1;
		}
		if (_appFrame != null) {
			_appFrame.setSize(width, height);
			_appFrame.packFrame();
		} else if (_appCanvas != null) {
			_appCanvas.setSize(width, height);
			_appCanvas.packCanvas();
		}
		return this;
	}

	public JavaSEApplication setLocation(float x, float y) {
		if (_appFrame != null) {
			_appFrame.setLocation(MathUtils.floor(x), MathUtils.floor(y));
		}
		return this;
	}

	public JavaSEApplication setTitle(String title) {
		if (_appFrame != null) {
			_appFrame.setTitle(title);
		}
		return this;
	}

	public JavaSEApplication setResizable(boolean resizable) {
		if (_appFrame != null) {
			_appFrame.setResizable(resizable);
		}
		return this;
	}

	public JavaSEApplication setAlwaysOnTop(boolean always) {
		if (_appFrame != null) {
			_appFrame.setAlwaysOnTop(always);
		}
		return this;
	}

	public JavaSEApplication setVisible(final boolean visible) {
		if (_appFrame == null && _appCanvas == null) {
			return this;
		}
		SwingUtilities.invokeLater(() -> {
			if (visible) {
				if (_appFrame != null) {
					_appFrame.setVisible(visible);
					_appFrame.requestFocus();
				}
				if (_appCanvas != null) {
					_appCanvas.setVisible(visible);
					_appCanvas.requestFocus();
				}
			} else {
				if (_appFrame != null) {
					_appFrame.setVisible(visible);
				}
				if (_appCanvas != null) {
					_appCanvas.setVisible(visible);
				}
			}

		});
		return this;
	}

	public JavaSEApplication setIcon(String path) {
		if (_appFrame == null || StringUtils.isEmpty(path)) {
			return this;
		}
		_appFrame.setIcon(path);
		return this;
	}

	public JavaSEApplication setIcon(Pixmap icon) {
		if (_appFrame == null) {
			return this;
		}
		_appFrame.setIcon(icon);
		return this;
	}

	public JavaSEApplication setIcon(Image icon) {
		if (_appFrame == null) {
			return this;
		}
		_appFrame.setIcon(icon);
		return this;
	}

	public static float dpiScale() {
		return Resolution.convertDPIScale(dpi());
	}

	public static int dpi() {
		return Toolkit.getDefaultToolkit().getScreenResolution();
	}

	public static float ppiY() {
		return dpi();
	}

	public static float ppcX() {
		return dpi() / 2.54f;
	}

	public static boolean isLowResolution() {
		return dpi() < 120;
	}

	public static Vector2f getScale(Dimension dim, float width, float height) {
		return new Vector2f((float) (dim.getWidth() / width), (float) (dim.getHeight() / height));
	}

	public static Vector2f setResolution(Container host, Dimension dim, float width, float height) {
		Dimension dimension = new Dimension(dim.width + host.getInsets().left + host.getInsets().right,
				dim.height + host.getInsets().top + host.getInsets().bottom);
		host.setPreferredSize(dimension);
		return getScale(dim, width, height);
	}

	public static Rectangle getScreenBounds() {
		return getScreenBounds(getGraphicsConfiguration());
	}

	public static Rectangle getScreenBounds(GraphicsConfiguration device) {
		return device.getBounds();
	}

	public static DisplayMode searchFullScreenModeDisplay(GraphicsDevice device, int width, int height) {
		DisplayMode displayModes[] = device.getDisplayModes();
		int currentDisplayPoint = 0;
		DisplayMode fullScreenMode = null;
		DisplayMode normalMode = device.getDisplayMode();
		DisplayMode adisplaymode[] = displayModes;
		int i = 0, length = adisplaymode.length;
		for (int j = length; i < j; i++) {
			DisplayMode mode = adisplaymode[i];
			if (mode.getWidth() == width && mode.getHeight() == height) {
				int point = 0;
				if (normalMode.getBitDepth() == mode.getBitDepth()) {
					point += 40;
				} else {
					point += mode.getBitDepth();
				}
				if (normalMode.getRefreshRate() == mode.getRefreshRate()) {
					point += 5;
				}
				if (currentDisplayPoint < point) {
					fullScreenMode = mode;
					currentDisplayPoint = point;
				}
			}
		}
		return fullScreenMode;
	}

	public static GraphicsConfiguration getGraphicsConfiguration() {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice screenDevice = env.getDefaultScreenDevice();
		return screenDevice.getDefaultConfiguration();
	}

	public static void createBuffer(java.awt.Canvas component, GraphicsConfiguration conf) {
		try {
			component.createBufferStrategy(2, conf.getBufferCapabilities());
		} catch (Exception ex) {
			component.createBufferStrategy(1);
		}
	}

	public static void createBuffer(java.awt.Window component, GraphicsConfiguration conf) {
		try {
			component.createBufferStrategy(2, conf.getBufferCapabilities());
		} catch (Exception ex) {
			component.createBufferStrategy(1);
		}
	}

	public static void setGraphicsExcellent(Graphics2D g) {
		g.setRenderingHints(_Excellent);
	}

	public static void setGraphicsQuality(Graphics2D g) {
		g.setRenderingHints(_Quality);
	}

	public static void setGraphicsSpeed(Graphics2D g) {
		g.setRenderingHints(_Speed);
	}

	@Override
	public int getContainerWidth() {
		if (_appFrame != null) {
			return _appFrame.getWidth();
		}
		if (_appCanvas != null) {
			return _appCanvas.getWidth();
		}
		return game != null ? game.setting.getShowWidth() : 0;
	}

	@Override
	public int getContainerHeight() {
		if (_appFrame != null) {
			return _appFrame.getHeight();
		}
		if (_appCanvas != null) {
			return _appCanvas.getHeight();
		}
		return game != null ? game.setting.getShowHeight() : 0;
	}

	@Override
	public void close() {
		if (_appCanvas != null) {
			_appCanvas.close();
		}
		System.exit(-1);
	}

	@Override
	public Orientation getOrientation() {
		if (getContainerHeight() > getContainerWidth()) {
			return Orientation.Portrait;
		} else {
			return Orientation.Landscape;
		}
	}

	@Override
	public void sysText(final SysInput.TextEvent event, final KeyMake.TextType textType, final String label,
			final String initVal) {
		if (game == null) {
			event.cancel();
			return;
		}
		game.invokeAsync(new Runnable() {

			@Override
			public void run() {

				final String output = (String) javax.swing.JOptionPane.showInputDialog(null, label, "",
						javax.swing.JOptionPane.QUESTION_MESSAGE, null, null, initVal);
				if (output != null) {
					event.input(output);
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
		game.invokeAsync(new Runnable() {

			@Override
			public void run() {
				int optType = javax.swing.JOptionPane.OK_CANCEL_OPTION;
				int msgType = cancel == null ? javax.swing.JOptionPane.INFORMATION_MESSAGE
						: javax.swing.JOptionPane.QUESTION_MESSAGE;
				Object[] options = (cancel == null) ? new Object[] { ok } : new Object[] { ok, cancel };
				Object defOption = (cancel == null) ? ok : cancel;
				int result = javax.swing.JOptionPane.showOptionDialog(null, text, title, optType, msgType, null,
						options, defOption);
				if (result == 0) {
					event.clicked();
				} else {
					event.cancel();
				}
			}
		});

	}

	public JavaSEAppFrame getAppFrame() {
		return _appFrame;
	}

	public JavaSEAppCanvas getAppCanvas() {
		return _appCanvas;
	}

	@Override
	public LGame getGame() {
		return game;
	}
}
