/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;

import loon.JavaApp.LSetting;
import loon.core.graphics.Screen;
//网页加载用类(需要lwjgl_applet.jar支持)
public abstract class JavaApplet extends Applet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Canvas _canvas;

	private LGame _game;

	public JavaApplet(final LSetting setting,
			final Class<? extends Screen> clazz, final Object... args) {
		_canvas = new Canvas() {

			private static final long serialVersionUID = 1L;

			public final void addNotify() {
				super.addNotify();
				setting.canvas = _canvas;
				_game = LGame.register(setting, clazz, args);
			}

			public final void removeNotify() {
				super.removeNotify();
				_game.exit();
			}
		};
		setLayout(new BorderLayout());
		_canvas.setIgnoreRepaint(true);
		add(_canvas);
		_canvas.setFocusable(true);
		_canvas.requestFocus();
	}

	public LGame getGame() {
		return _game;
	}

	public Canvas getCanvas() {
		return _canvas;
	}

	public void destroy() {
		remove(_canvas);
		super.destroy();
	}
}
