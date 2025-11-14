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

import loon.canvas.LColor;
import loon.component.LComponent;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

public class TableColumnLayout {

	public static final int HORIZONTAL_ALIGN_CENTER = 1;
	public static final int HORIZONTAL_ALIGN_RIGHT = 2;
	public static final int HORIZONTAL_ALIGN_LEFT = 3;

	public static final int VERTICAL_ALIGN_TOP = 4;
	public static final int VERTICAL_ALIGN_CENTER = 5;
	public static final int VERTICAL_ALIGN_BOTTOM = 6;

	private LColor _drawColumnGridColor = LColor.white;

	private LColor _drawColumnTableLayoutGridColor = LColor.red;

	private int _verticalAlignment = VERTICAL_ALIGN_CENTER;

	private int _horizontalAlignment = HORIZONTAL_ALIGN_CENTER;

	private int _leftMargin = 0;

	private int _rightMargin = 0;

	private int _topMargin = 0;

	private int _bottomMargin = 0;

	private int _x;

	private int _y;

	private int _width;

	private int _height;

	private LComponent _component;

	public TableColumnLayout(LComponent component, int x, int y, int width, int height) {
		this._component = component;
		this._x = x;
		this._y = y;
		this._width = width;
		this._height = height;
	}

	public int getHeight() {
		return _height;
	}

	public TableColumnLayout setHeight(int height) {
		this._height = height;
		adjustComponent();
		return this;
	}

	public int getWidth() {
		return _width;
	}

	public TableColumnLayout setWidth(int width) {
		this._width = width;
		adjustComponent();
		return this;
	}

	public int getX() {
		return _x;
	}

	public TableColumnLayout setX(int x) {
		this._x = x;
		adjustComponent();
		return this;
	}

	public int getY() {
		return _y;
	}

	public TableColumnLayout setY(int y) {
		this._y = y;
		adjustComponent();
		return this;
	}

	public int getVerticalAlignment() {
		return _verticalAlignment;
	}

	public TableColumnLayout setVerticalAlignment(int verticalAlignment) {
		this._verticalAlignment = verticalAlignment;
		adjustComponent();
		return this;
	}

	public int getHorizontalAlignment() {
		return _horizontalAlignment;
	}

	public TableColumnLayout setHorizontalAlignment(int horizontalAlignment) {
		this._horizontalAlignment = horizontalAlignment;
		adjustComponent();
		return this;
	}

	public int getLeftMargin() {
		return _leftMargin;
	}

	public TableColumnLayout setLeftMargin(int leftMargin) {
		this._leftMargin = leftMargin;
		adjustComponent();
		return this;
	}

	public int getRightMargin() {
		return _rightMargin;
	}

	public TableColumnLayout setRightMargin(int rightMargin) {
		this._rightMargin = rightMargin;
		adjustComponent();
		return this;
	}

	public int getTopMargin() {
		return _topMargin;
	}

	public TableColumnLayout setTopMargin(int topMargin) {
		this._topMargin = topMargin;
		adjustComponent();
		return this;
	}

	public int getBottomMargin() {
		return _bottomMargin;
	}

	public TableColumnLayout setBottomMargin(int bottomMargin) {
		this._bottomMargin = bottomMargin;
		adjustComponent();
		return this;
	}

	public TableColumnLayout setMargin(int marginLeft, int marginRight, int marginTop, int marginBottom) {
		setLeftMargin(marginLeft);
		setRightMargin(marginRight);
		setTopMargin(marginTop);
		setBottomMargin(marginBottom);
		return this;
	}

	public TableColumnLayout setComponent(LComponent component) {
		this._component = component;
		adjustComponent();
		return this;
	}

	public LComponent getComponent() {
		return _component;
	}

	public boolean canWidthShrink(int newWidth) {
		return newWidth > getMinWidth();
	}

	public int getMinWidth() {
		if (_component != null) {
			return MathUtils.ceil(_leftMargin + _rightMargin + _component.getWidth());
		}
		return 1;
	}

	public boolean canHeightShrink(int newHeight) {
		return newHeight > getMinHeight();
	}

	public int getMinHeight() {
		if (_component != null) {
			return MathUtils.ceil(_topMargin + _bottomMargin + _component.getHeight());
		}
		return 1;
	}

	public TableColumnLayout adjustComponent() {
		if (_component != null) {
			switch (_horizontalAlignment) {
			case HORIZONTAL_ALIGN_LEFT:
				_component.setX(getX() + _leftMargin);
				break;
			case HORIZONTAL_ALIGN_CENTER:
				_component.setX(getX() + (getWidth() / 2 - _component.getWidth() / 2));
				break;
			case HORIZONTAL_ALIGN_RIGHT:
				_component.setX((getX() + getWidth()) - (_component.getWidth() + _rightMargin));
				break;
			}
			switch (_verticalAlignment) {
			case VERTICAL_ALIGN_TOP:
				_component.setY(getY() + _topMargin);
				break;
			case VERTICAL_ALIGN_CENTER:
				_component.setY(getY() + (getHeight() / 2 - _component.getHeight() / 2));
				break;
			case VERTICAL_ALIGN_BOTTOM:
				_component.setY((getY() + getHeight()) - (_component.getHeight() + _bottomMargin));
			}
		}
		return this;
	}

	public void paint(GLEx g) {
		g.drawRect(getX(), getY(), getWidth(), getHeight(), _drawColumnGridColor);
		if (_component != null && _component.getContainer() != null
				&& _component.getContainer() instanceof TableLayout) {
			if (((TableLayout) _component.getContainer()).isGrid()) {
				g.drawRect(_component.getContainer().getX() + _component.getX() - _leftMargin,
						_component.getContainer().getY() + _component.getY() - _topMargin,
						_component.getWidth() + _rightMargin, _component.getHeight() + _bottomMargin,
						_drawColumnTableLayoutGridColor);
			}
		}
	}

	public void setDrawColumnGridColor(LColor c) {
		_drawColumnGridColor = c;
	}

	public void setDrawColumnTableLayoutGridColor(LColor c) {
		_drawColumnTableLayoutGridColor = c;
	}

	public LColor getDrawColumnGridColor() {
		return _drawColumnGridColor;
	}

	public LColor getDrawColumnTableLayoutGridColor() {
		return _drawColumnTableLayoutGridColor;
	}

}
