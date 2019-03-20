/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.action.map.view;

import loon.LSystem;
import loon.opengl.GLEx;

public abstract class GameView {

	protected int _x;
	protected int _y;
	protected int _width;
	protected int _height;

	public GameView() {
		this(0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public GameView(int x, int y, int w, int h) {
		this._x = x;
		this._y = y;
		this._width = w;
		this._height = h;
	}

	public void setPlace(int x, int y) {
		pos(x, y);
	}
	
	public void pos(int x, int y) {
		this._x = x;
		this._y = y;
	}

	public abstract void draw(GLEx g);
}
