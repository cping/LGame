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

import loon.LTexture;
import loon.canvas.LColor;
import loon.opengl.GLEx;

public class FilledView extends GameView {

	private LTexture texture;
	private LColor colorToFill;

	public FilledView(int x, int y, int w, int h) {
		this(null, x, y, w, h, LColor.white);
	}

	public FilledView(int x, int y, int w, int h, LColor color) {
		this(null, x, y, w, h, color);
	}

	public FilledView(LTexture texture, int x, int y, int w, int h, LColor color) {
		super(x, y, w, h);
		colorToFill = color;
	}

	@Override
	public void draw(GLEx g) {
		if (texture == null) {
			g.fillRect(_x, _y, _width, _height, colorToFill);
		} else {
			g.draw(texture, _x, _y, _width, _height, colorToFill);
		}
	}

}
