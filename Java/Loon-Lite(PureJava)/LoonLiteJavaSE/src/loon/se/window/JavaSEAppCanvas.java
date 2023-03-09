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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferStrategy;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.SwingUtilities;

import loon.LRelease;
import loon.se.JavaSEApplication;
import loon.se.JavaSECanvas;
import loon.se.JavaSEGame;
import loon.se.JavaSEInputMake;
import loon.se.JavaSESetting;

public class JavaSEAppCanvas extends Canvas implements JavaSELoop, LRelease {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JavaSEGame _game;

	private AtomicBoolean _running = new AtomicBoolean(false);

	private GraphicsConfiguration _config;

	private BufferStrategy _bufferStrategy;

	private JavaSEAppLoop _loop;

	private JavaSESetting _setting;

	private int _frameCount;

	public JavaSEAppCanvas(JavaSESetting setting) {
		this(null, setting);
	}

	public JavaSEAppCanvas(JavaSEGame game, JavaSESetting setting) {
		this(JavaSEApplication.getGraphicsConfiguration(), game, setting);
	}

	public JavaSEAppCanvas(GraphicsConfiguration config, JavaSEGame game, JavaSESetting setting) {
		super(config);
		this._game = game;
		this._loop = new JavaSEAppLoop(game, this, setting.fps);
		this._config = config;
		this._setting = setting;
		if (game != null) {
			addInputListener(game.input());
		}
		addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
				start();
			}

			@Override
			public void componentResized(ComponentEvent e) {
				Component comp = e.getComponent();
				game.graphics().onSizeChanged(comp.getWidth(), comp.getHeight());
			}

			@Override
			public void componentMoved(ComponentEvent e) {

			}

			@Override
			public void componentHidden(ComponentEvent e) {
				stop();
			}
		});
		setBackground(Color.BLACK);
		setEnabled(true);
		setIgnoreRepaint(true);
	}

	public JavaSEAppCanvas addInputListener(JavaSEInputMake input) {
		this.addFocusListener(input);
		this.addKeyListener(input);
		this.addMouseListener(input);
		this.addMouseMotionListener(input);
		return this;
	}

	public JavaSEAppCanvas start() {
		if (_running.get()) {
			return this;
		}
		SwingUtilities.invokeLater(() -> {
			JavaSEApplication.createBuffer(this, _config);
			_bufferStrategy = getBufferStrategy();
			if (_bufferStrategy == null) {
				createBufferStrategy(3);
				_bufferStrategy = getBufferStrategy();
			}
			setVisible(true);
			_running.set(true);
			_loop.start();
		});
		return this;
	}

	public JavaSEAppCanvas stop() {
		if (!_running.get()) {
			return this;
		}
		_running.set(false);
		_loop.terminate();
		return this;
	}

	@Override
	public void process(final boolean active) {
		_bufferStrategy = getBufferStrategy();
		if (_bufferStrategy == null) {
			createBufferStrategy(3);
			_bufferStrategy = getBufferStrategy();
		}
		Graphics2D g = null;
		do {
			do {
				try {
					g = (Graphics2D) this._bufferStrategy.getDrawGraphics();
					if (g != null) {
						((JavaSECanvas) _game.getCanvas()).updateContext(g);
						_game.process(active);
					}
				} finally {
					g.dispose();
				}
			} while (_bufferStrategy.contentsRestored());
		} while (this._bufferStrategy.contentsLost());
		this._bufferStrategy.show();
		this._frameCount++;
	}

	public JavaSEGame getGame() {
		return _game;
	}

	public int getFrameCount() {
		return this._frameCount;
	}

	public void packCanvas() {
		addNotify();
		validate();
		updateSize();
		setVisible(true);
	}

	public void updateSize() {
		Dimension dimension = new Dimension(_setting.getShowWidth(), _setting.getShowHeight());
		setPreferredSize(dimension);
		setSize(dimension);
	}

	@Override
	public JavaSELoop set(boolean r) {
		_running.set(r);
		return this;
	}

	@Override
	public boolean get() {
		return _running.get();
	}

	@Override
	public void close() {
		if (_game != null) {
			_game.close();
		}
		stop();
	}

}
