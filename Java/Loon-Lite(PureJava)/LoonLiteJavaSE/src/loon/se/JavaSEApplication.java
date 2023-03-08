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

import loon.LGame;
import loon.LSysException;
import loon.LazyLoading;
import loon.Platform;
import loon.events.KeyMake;
import loon.events.SysInput;
import loon.geom.Vector2f;
import loon.se.window.JavaSEAppCanvas;
import loon.se.window.JavaSEAppFrame;

public class JavaSEApplication implements Platform {

	protected JavaSEGame game;

	protected JavaSESetting appSetting;

	protected LazyLoading.Data lazyData;

	protected Class<?> mainClass;

	protected GraphicsConfiguration config;

	private JavaSEAppFrame _appFrame;

	private JavaSEAppCanvas _appCanvas;

	public static JavaSEAppFrame launch(Loon app, JavaSESetting setting, LazyLoading.Data lazy, String[] args) {
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
		this.mainClass = app.getClass();
		this.game = new JavaSEGame(this, this.appSetting);
	}

	protected void loadScreen() {
		try {
			game.register(lazyData.onScreen());
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

	public JavaSEAppFrame getAppFrame() {
		return _appFrame;
	}

	public JavaSEAppCanvas getAppCanvas() {
		return _appCanvas;
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

	public static void setGraphicsQuality(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	public static void setGraphicsSpeed(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}

	public static void systemLog(String message) {
		System.out.println(message);
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
		if (game != null) {
			game.close();
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
	public LGame getGame() {
		return game;
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

}
