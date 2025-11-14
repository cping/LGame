/**
 * Copyright 2014
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
 * @version 0.4.2
 */
package loon.component.table;

import loon.LTexture;
import loon.canvas.Canvas;
import loon.canvas.LColor;
import loon.geom.Dimension;
import loon.opengl.GLEx;

public class TextureCellRenderer implements ICellRenderer {

	private boolean _scaleTexture = true;

	private LColor _drawColor = LColor.white;

	public TextureCellRenderer setDrawColor(LColor c) {
		_drawColor = c;
		return this;
	}

	public LColor getDrawColor() {
		return _drawColor;
	}

	@Override
	public void paint(GLEx g, Object vl, int x, int y, int width, int height) {
		if (!(vl instanceof LTexture)) {
			return;
		}
		LTexture textire = (LTexture) vl;
		if (_scaleTexture) {
			g.draw(textire, x, y, width, height, _drawColor);
		} else {
			g.draw(textire, x, y, _drawColor);
		}
	}

	@Override
	public void paint(Canvas g, Object vl, int x, int y, int width, int height) {
		if (!(vl instanceof LTexture)) {
			return;
		}
		LTexture textire = (LTexture) vl;
		int old = g.getFillColor();
		if (_scaleTexture) {
			g.draw(textire.getImage(), x, y, width, height);
		} else {
			g.draw(textire.getImage(), x, y);
		}
		g.setFillColor(old);
	}

	public void setScaleTexture(boolean s) {
		this._scaleTexture = s;
	}

	@Override
	public Dimension getCellContentSize(Object vl) {
		if (vl == null) {
			return null;
		}
		if (!(vl instanceof LTexture)) {
			return null;
		}
		LTexture texture = (LTexture) vl;
		return new Dimension(texture.getWidth(), texture.getHeight());
	}
}
