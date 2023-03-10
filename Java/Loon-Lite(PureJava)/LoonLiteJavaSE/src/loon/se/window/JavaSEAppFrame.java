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
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import loon.LRelease;
import loon.se.JavaSEApplication;
import loon.se.JavaSEGame;
import loon.se.JavaSEImage;
import loon.se.JavaSESetting;

public class JavaSEAppFrame extends JFrame implements JavaSEApp<JavaSEAppFrame>, LRelease {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static DisplayMode methods[] = new DisplayMode[] { new DisplayMode(800, 600, 32, 0),
			new DisplayMode(800, 600, 24, 0), new DisplayMode(800, 600, 16, 0), new DisplayMode(640, 480, 32, 0),
			new DisplayMode(640, 480, 24, 0), new DisplayMode(640, 480, 16, 0) };

	protected final GraphicsDevice _device;

	protected final GraphicsConfiguration _config;

	private JavaSEGame _game;

	protected final JavaSESetting _setting;

	protected JavaSEAppCanvas _canvas;

	public JavaSEAppFrame(JavaSESetting setting) {
		this(null, setting);
	}

	public JavaSEAppFrame(JavaSEGame game, JavaSESetting setting) {
		this(JavaSEApplication.getGraphicsConfiguration(), game, setting);
	}

	public JavaSEAppFrame(GraphicsConfiguration config, JavaSEGame game, JavaSESetting setting) {
		super(setting.appName, config);
		_game = game;
		_config = config;
		_setting = setting;
		_device = config.getDevice();
		setBackground(Color.BLACK);
		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					restoreScreen();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				if (_game != null) {
					_game.close();
				}
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
		final String[] paths = _setting.iconPaths;
		if (paths != null) {
			if (paths.length == 1) {
				setIcon(paths[0]);
			} else {
				setIcons(paths);
			}
		}
		requestFocus();
	}

	public void play() {
		playCanvas(null);
	}

	public void packFrame() {
		addNotify();
		validate();
		pack();
		if (_canvas != null) {
			_canvas.packCanvas();
		}
		updateSize();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void playCanvas(JavaSEAppCanvas canvas) {
		this._canvas = canvas;
		if (this._canvas == null) {
			this._canvas = new JavaSEAppCanvas(_config, _game, _setting);
		}
		add(this._canvas);
		packFrame();
		canvas.start();
	}

	public BufferedImage snapshot() {
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_BGR);
		Graphics g = img.getGraphics();
		paint(g);
		g.dispose();
		return img;
	}

	public JavaSEAppFrame setIcons(String[] paths) {
		List<Image> images = new ArrayList<Image>();
		for (String path : paths) {
			if (path != null) {
				images.add(((JavaSEImage) _game.assets().getImageSync(path)).seImage());
			}
		}
		setIconImages(images);
		return this;
	}

	public JavaSEAppFrame setIcon(String path) {
		setIconImage(((JavaSEImage) _game.assets().getImageSync(path)).seImage());
		return this;
	}

	public JavaSEAppFrame setIcon(Image icon) {
		setIconImage(icon);
		return this;
	}

	public JavaSEAppCanvas getCanvas() {
		return _canvas;
	}

	public JavaSEGame getGame() {
		return _game;
	}

	@Override
	public JavaSEAppFrame updateSize() {
		JavaSEApplication.setResolution(this, new Dimension(_setting.getShowWidth(), _setting.getShowHeight()), 0, 0);
		setSize(_setting.getShowWidth(), _setting.getShowHeight());
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

	public void restoreScreen() {
		_config.getDevice().setFullScreenWindow(null);
		packFrame();
	}

	public DisplayMode[] getCompatibleDisplayModes() {
		return _device.getDisplayModes();
	}

	public DisplayMode findFirstMode(DisplayMode[] modes) {
		DisplayMode goodModes[] = _device.getDisplayModes();
		for (int i = 0; i < modes.length; i++) {
			for (int j = 0; j < goodModes.length; j++) {
				if (displayModesMatch(modes[i], goodModes[j])) {
					return modes[i];
				}
			}
		}
		return JavaSEApplication.searchFullScreenModeDisplay(_device, _setting.getShowWidth(),
				_setting.getShowHeight());
	}

	public DisplayMode getCurrentDisplayMode() {
		return _device.getDisplayMode();
	}

	public boolean displayModesMatch(DisplayMode mode1, DisplayMode mode2) {
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

	protected void setFullScreen(final JavaSEAppFrame frame, final DisplayMode displayMode) {
		_device.setFullScreenWindow(frame);
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
	}

	public void full() {
		setFullScreen(this, findFirstMode(methods));
	}

	@Override
	public void close() {
		if (_canvas != null) {
			_canvas.close();
		}
	}

}