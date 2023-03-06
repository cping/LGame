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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.concurrent.atomic.AtomicBoolean;

import loon.se.JavaSEApplication;
import loon.se.JavaSESetting;

public class JavaSEAppCanvas extends Canvas implements JavaSELoop {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AtomicBoolean _running = new AtomicBoolean(false);

	private GraphicsConfiguration _config;

	private BufferStrategy _bufferStrategy;

	private JavaSEAppLoop _loop;

	private JavaSESetting _setting;

	private int _frameCount;

	public JavaSEAppCanvas(JavaSESetting setting) {
		this(JavaSEApplication.getGraphicsConfiguration(), setting);
	}

	public JavaSEAppCanvas(GraphicsConfiguration config, JavaSESetting setting) {
		super(config);
		this._loop = new JavaSEAppLoop(this, setting.fps);
		this._config = config;
		this._setting = setting;
		setBackground(Color.BLACK);
		setEnabled(true);
		setIgnoreRepaint(true);
	}

	public JavaSEAppCanvas start() {
		if (_running.get()) {
			return this;
		}
		JavaSEApplication.createBuffer(this, _config);
		_bufferStrategy = getBufferStrategy();
		if (_bufferStrategy == null) {
			createBufferStrategy(3);
			_bufferStrategy = getBufferStrategy();
		}
		setVisible(true);
		_running.set(true);
		_loop.start();
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
	public void process() {
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
						JavaSEApplication.setGraphicsSpeed(g);
						g.setColor(Color.yellow);
						g.drawString("FDFDFD", 0, 25);

					}
				} finally {
					if (g != null) {
						g.dispose();
					}
				}
			} while (_bufferStrategy.contentsRestored());
			this._bufferStrategy.show();
		} while (this._bufferStrategy.contentsLost());
		Toolkit.getDefaultToolkit().sync();
		this._frameCount++;
	}

	public int getFrameCount() {
		return this._frameCount;
	}

	public void updateSize() {
		setPreferredSize(new Dimension(_setting.getShowWidth(), _setting.getShowHeight()));
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

}
