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
package loon.component.layout;

import loon.LRelease;
import loon.action.ActionBind;
import loon.component.LToolTip;
import loon.geom.PointF;
import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 动作对象布局用类,可以根据左右高低参数布置一组ActionBind的显示位置
 */
public class Margin implements LRelease {

	private final TArray<ActionBind> _tempBinds = new TArray<ActionBind>();

	private TArray<ActionBind> _childrens;

	private Vector2f _offset;

	private float _marginTop;
	private float _marginRight;
	private float _marginBottom;
	private float _marginLeft;
	private float _marginCenterX;
	private float _marginCenterY;
	private float _size;

	private boolean _isSnap;
	private boolean _isVertical;

	public Margin(float size, boolean vertical) {
		this(size, vertical, true);
	}

	public Margin(float size, boolean vertical, boolean snap) {
		this._size = size;
		this._isVertical = vertical;
		this._offset = new Vector2f();
		this._childrens = new TArray<ActionBind>();
		this._isSnap = snap;
		this.reset();
	}

	public void reset() {
		this._marginTop = this._marginBottom = this._marginLeft = this._marginRight = this._marginCenterX = this._marginCenterY = 0f;
	}

	public Margin addChild(ActionBind bind) {
		if (bind == null) {
			return this;
		}
		if (!_childrens.contains(bind)) {
			_childrens.add(bind);
		}
		return this;
	}

	public boolean removeChild(ActionBind bind) {
		if (bind == null) {
			return false;
		}
		return _childrens.remove(bind);
	}

	public Margin clear() {
		_childrens.clear();
		return this;
	}

	public Margin reverse() {
		_childrens.reverse();
		return this;
	}

	public Margin layout() {

		ActionBind bind;
		PointF size;

		float cols = 0;
		float rows = 0;
		float largest = 0;

		float dx;
		float dy;
		float w;
		float h;

		_tempBinds.clear();
		_tempBinds.addAll(_childrens);

		for (int i = 0; i < _tempBinds.size; i++) {

			bind = _childrens.get(i);
			if (bind == null || bind instanceof LToolTip) {
				continue;
			}

			float bindX = bind.getX();
			float bindY = bind.getY();

			if (_marginCenterX != 0f) {
				bindX = MathUtils.round(this._marginCenterX + bindX);
			}
			if (_marginCenterY != 0f) {
				bindY = MathUtils.round(this._marginCenterY + bindX);
			}

			size = (bind.getWidth() > 1 && bind.getHeight() > 1) ? new PointF(bind.getWidth(), bind.getHeight())
					: new PointF(bindX, bindY);

			w = size.x + this._marginLeft + this._marginRight;
			h = size.y + this._marginTop + this._marginBottom;

			if (!this._isVertical) {
				cols += w;

				if (cols > this._size) {
					rows += (largest == 0) ? rows : largest;
					largest = 0;
					cols = w;
				}

				if (h > largest) {
					largest = h;
				}

				dx = cols - size.x - this._marginRight;
				dy = rows + this._marginTop;
			} else {
				rows += h;

				if (rows > this._size) {
					cols += (largest == 0) ? cols : largest;
					largest = 0;
					rows = h;
				}

				if (w > largest) {
					largest = w;
				}

				dx = cols + this._marginLeft;
				dy = rows - size.y - this._marginBottom;
			}

			this.pos(bind, dx, dy);
		}

		return this;
	}

	protected void pos(ActionBind o, float dx, float dy) {
		o.setLocation((this._isSnap ? MathUtils.round(dx) : dx) + _offset.x,
				(this._isSnap ? MathUtils.round(dy) : dy) + _offset.y);
	}

	public Margin setSize(float size) {
		this._size = size;
		return this;
	}

	public float getSize() {
		return this._size;
	}

	public Margin setVertical(boolean v) {
		this._isVertical = v;
		return this;
	}

	public boolean isVertical() {
		return this._isVertical;
	}

	public Margin setMargin(float left, float top, float right, float bottom) {
		return setMargin(0f, 0f, left, top, right, bottom);
	}

	public Margin setMargin(float centerX, float centerY, float left, float top, float right, float bottom) {
		setMarginCenterX(centerX);
		setMarginCenterY(centerY);
		setMarginLeft(left);
		setMarginTop(top);
		setMarginRight(right);
		setMarginBottom(bottom);
		return this;
	}

	public float getMarginCenterX() {
		return _marginCenterX;
	}

	public Margin setMarginCenterX(float x) {
		this._marginCenterX = x;
		return this;
	}

	public float getMarginCenterY() {
		return _marginCenterY;
	}

	public Margin setMarginCenterY(float y) {
		this._marginCenterY = y;
		return this;
	}

	public float getMarginTop() {
		return _marginTop;
	}

	public Margin setMarginTop(float marginTop) {
		this._marginTop = marginTop;
		return this;
	}

	public float getMarginRight() {
		return _marginRight;
	}

	public Margin setMarginRight(float marginRight) {
		this._marginRight = marginRight;
		return this;
	}

	public float getMarginBottom() {
		return _marginBottom;
	}

	public Margin setMarginBottom(float marginBottom) {
		this._marginBottom = marginBottom;
		return this;
	}

	public float getMarginLeft() {
		return _marginLeft;
	}

	public Margin setMarginLeft(float marginLeft) {
		this._marginLeft = marginLeft;
		return this;
	}

	public Vector2f getOffset() {
		return _offset;
	}

	public Margin setOffset(float x, float y) {
		this._offset.set(x, y);
		return this;
	}

	public Margin setOffset(Vector2f offset) {
		this._offset.set(offset);
		return this;
	}

	@Override
	public void close() {
		_tempBinds.clear();
	}

}
