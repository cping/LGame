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
package loon.action.sprite.effect;

import loon.action.sprite.effect.RippleEffect.Model;
import loon.canvas.LColor;
import loon.geom.Triangle2f;
import loon.opengl.GLEx;
import loon.utils.timer.LTimer;

/**
 * 波纹扩散效果具体实现类
 */
public class RippleKernel {

	protected int _lineWidth;

	protected LColor _color;

	protected float _x, _y;

	protected int _existTime;

	protected int _limit;

	protected LTimer _timer = new LTimer(0);

	private Triangle2f _tempTriangle;

	public RippleKernel(float x, float y) {
		this(2, x, y);
	}

	public RippleKernel(int width, float x, float y) {
		this(width, x, y, 25);
	}

	public RippleKernel(int width, float x, float y, int l) {
		this._x = x;
		this._y = y;
		this._existTime = 0;
		this._limit = l;
		this._lineWidth = width;
	}

	public void draw(final GLEx g, Model model, float mx, float my) {
		final int span = _existTime * 2;
		final float oldWidth = g.getLineWidth();
		g.setLineWidth(_lineWidth);
		switch (model) {
		case OVAL:
			g.drawOval(mx + _x - span / 2, my + _y - span / 2, span, span);
			break;
		case RECT:
			g.drawRect(mx + _x - span / 2, my + _y - span / 2, span, span);
			break;
		case RHOMBUS:
			g.drawRhombus(6, mx + _x - span / 2, my + _y - span / 2, span);
			break;
		case DASHOVAL:
			g.drawDashCircle(mx + _x - span / 2, my + _y - span / 2, span);
			break;
		case TRIANGLE:
			if (_tempTriangle == null) {
				_tempTriangle = new Triangle2f(span, span);
			} else {
				_tempTriangle.set(span, span);
			}
			g.drawTriangle(_tempTriangle, mx + _x - span / 2, my + _y - span / 2);
			break;
		}
		g.setLineWidth(oldWidth);
		_existTime++;
	}

	public boolean isExpired() {
		return _existTime >= _limit;
	}

}
