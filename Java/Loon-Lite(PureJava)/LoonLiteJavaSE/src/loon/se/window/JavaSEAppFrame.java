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
package loon.se.window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import loon.se.JavaSEApplication;
import loon.se.JavaSESetting;

public class JavaSEAppFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static DisplayMode methods[] = new DisplayMode[] { new DisplayMode(800, 600, 32, 0),
			new DisplayMode(800, 600, 24, 0), new DisplayMode(800, 600, 16, 0), new DisplayMode(640, 480, 32, 0),
			new DisplayMode(640, 480, 24, 0), new DisplayMode(640, 480, 16, 0) };

	protected final GraphicsDevice _device;

	protected final GraphicsConfiguration _config;

	protected final JavaSESetting _setting;

	protected JavaSEAppCanvas _canvas;

	public JavaSEAppFrame(JavaSESetting setting) {
		this(JavaSEApplication.getGraphicsConfiguration(), setting);
	}

	public JavaSEAppFrame(GraphicsConfiguration config, JavaSESetting setting) {
		super(setting.appName, config);
		_config = config;
		_setting = setting;
		_device = config.getDevice();
		setBackground(Color.BLACK);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				System.exit(-1);
			}
		});
		setResizable(_setting.isAllowScreenResizabled);
		setUndecorated(false);
		setIgnoreRepaint(true);
		final java.awt.DisplayMode desktop = _device.getDisplayMode();
		if (desktop.getWidth() == setting.getShowWidth() && desktop.getHeight() == setting.getShowHeight()) {
			setUndecorated(true);
		}
	}

	public void play() {
		playCanvas(null);
	}

	public void playCanvas(JavaSEAppCanvas canvas) {
		this._canvas = canvas;
		if (this._canvas == null) {
			this._canvas = new JavaSEAppCanvas(_config, _setting);
		}
		add(this._canvas);
		addNotify();
		validate();
		pack();
		updateSize();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		canvas.start();
	}

	public JavaSEAppFrame updateSize() {
		JavaSEApplication.setResolution(this, new Dimension(_setting.getShowWidth(), _setting.getShowHeight()), 0, 0);
		return this;
	}

	private static DisplayMode getBestDisplayMode(GraphicsDevice device) {
		for (int x = 0, xn = methods.length; x < xn; x++) {
			DisplayMode[] modes = device.getDisplayModes();
			for (int i = 0, in = modes.length; i < in; i++) {
				if (modes[i].getWidth() == methods[x].getWidth() && modes[i].getHeight() == methods[x].getHeight()
						&& modes[i].getBitDepth() == methods[x].getBitDepth()) {
					return methods[x];
				}
			}
		}
		return null;
	}

	public BufferedImage createCompatibleImage(int w, int h, int transparancy) {
		Window window = _config.getDevice().getFullScreenWindow();
		if (window != null) {
			GraphicsConfiguration gc = window.getGraphicsConfiguration();
			return gc.createCompatibleImage(w, h, transparancy);
		}
		return null;
	}

	public JFrame getFullScreenWindow() {
		return (JFrame) _config.getDevice().getFullScreenWindow();
	}

	@Override
	public int getWidth() {
		Window window = _config.getDevice().getFullScreenWindow();
		if (window != null) {
			return window.getWidth();
		} else {
			return 0;
		}
	}

	@Override
	public int getHeight() {
		Window window = _config.getDevice().getFullScreenWindow();
		if (window != null) {
			return window.getHeight();
		} else {
			return 0;
		}
	}

	public void restoreScreen() {
		Window window = _config.getDevice().getFullScreenWindow();
		if (window != null) {
			window.dispose();
		}
		_config.getDevice().setFullScreenWindow(null);
	}

	public DisplayMode[] getCompatibleDisplayModes() {
		return _device.getDisplayModes();
	}

	public DisplayMode findFirstMode(DisplayMode modes[]) {
		DisplayMode goodModes[] = _device.getDisplayModes();
		for (int i = 0; i < modes.length; i++) {
			for (int j = 0; j < goodModes.length; j++) {
				if (displayModesMatch(modes[i], goodModes[j])) {
					return modes[i];
				}
			}
		}
		return null;
	}

	public DisplayMode getCurrentDisplayMode() {
		return _device.getDisplayMode();
	}

	public boolean displayModesMatch(DisplayMode mode1, DisplayMode mode2)

	{
		if (mode1.getWidth() != mode2.getWidth() || mode1.getHeight() != mode2.getHeight()) {
			return false;
		}

		if (mode1.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI && mode2.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI
				&& mode1.getBitDepth() != mode2.getBitDepth()) {
			return false;
		}

		if (mode1.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN
				&& mode2.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN
				&& mode1.getRefreshRate() != mode2.getRefreshRate()) {
			return false;
		}

		return true;
	}

	public void setFullScreen(DisplayMode displayMode) {
		_device.setFullScreenWindow(this);
		if (_device.isDisplayChangeSupported()) {
			_device.setDisplayMode(getBestDisplayMode(_device));
		}
		if (displayMode != null && _device.isDisplayChangeSupported()) {
			try {
				_device.setDisplayMode(displayMode);
			} catch (IllegalArgumentException ex) {
			}
			setSize(displayMode.getWidth(), displayMode.getHeight());
		}
		try {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					createBufferStrategy(2);
				}
			});
		} catch (Exception ex) {
		}
	}

	@Override
	public Graphics2D getGraphics() {
		Window window = _device.getFullScreenWindow();
		if (window != null) {
			BufferStrategy strategy = window.getBufferStrategy();
			return (Graphics2D) strategy.getDrawGraphics();
		} else {
			return null;
		}
	}

	public void full() {
		setFullScreen(findFirstMode(methods));
	}

}
