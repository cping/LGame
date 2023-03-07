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
import loon.LazyLoading;
import loon.Platform;
import loon.events.KeyMake.TextType;
import loon.events.SysInput.ClickEvent;
import loon.events.SysInput.TextEvent;
import loon.geom.Vector2f;
import loon.se.window.JavaSEAppCanvas;
import loon.se.window.JavaSEAppFrame;

public class JavaSEApplication implements Platform {

	protected JavaSEGame game;

	protected JavaSESetting appSetting;

	protected LazyLoading.Data lazyData;

	protected Class<?> mainClass;

	protected GraphicsConfiguration config;

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
	}

	public JavaSEAppCanvas runCanvas() {
		JavaSEAppCanvas canvas = new JavaSEAppCanvas(config, game, appSetting);
		canvas.updateSize();
		canvas.start();
		return canvas;
	}

	public JavaSEAppFrame runFrame() {
		JavaSEAppFrame frame = new JavaSEAppFrame(config, game, appSetting);
		JavaSEAppCanvas canvas = new JavaSEAppCanvas(config, game, appSetting);
		frame.playCanvas(canvas);
		if (appSetting.fullscreen) {
			frame.full();
		}
		return frame;
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

	@Override
	public void close() {
		if (game != null) {
			game.close();
		}
	}

	@Override
	public int getContainerWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getContainerHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Orientation getOrientation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LGame getGame() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sysText(TextEvent event, TextType textType, String label, String initialValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sysDialog(ClickEvent event, String title, String text, String ok, String cancel) {
		// TODO Auto-generated method stub

	}

}
