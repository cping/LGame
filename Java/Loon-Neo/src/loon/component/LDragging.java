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
package loon.component;

import loon.LSystem;
import loon.canvas.LColor;
import loon.events.ActionKey;
import loon.events.SelectAreaListener;
import loon.events.SysTouch;
import loon.geom.Circle;
import loon.geom.RectBox;
import loon.geom.Shape;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 用于显示拖拽范围的拖拽效果组件
 */
public class LDragging extends LComponent {

	private SelectAreaListener _selectArea;

	private boolean _fillRect;

	private boolean _dashRect;

	private boolean _dragging;

	private boolean _circle;

	private int _dashDivisions;

	private RectBox _area;

	private RectBox _display_area;

	private float _dragDrawAlpha;

	private float _startX;

	private float _startY;

	private float _lastX;

	private float _lastY;

	private ActionKey locked;

	private float _lineWidth;

	private LColor _fillColor;

	private LColor _rectColor;

	/**
	 * 构造拖拽用组件,用于渲染出特定的拖拽区域
	 */
	public LDragging() {
		this(false, true, true);
	}

	/**
	 * 构造拖拽用组件,用于渲染出特定的拖拽区域(默认使用矩形渲染,全局渲染模式,使用虚线边框,边框线宽4,每行虚线由5个子线条组成)
	 * 
	 * @param circle 是否拖拽区域为圆形
	 * @param fill   是否完全填充
	 * @param dash   是否用虚线描边
	 */
	public LDragging(boolean circle, boolean fill, boolean dash) {
		this(circle, fill, dash, 4f, 5);
	}

	/**
	 * 构造拖拽用组件,用于渲染出特定的拖拽区域
	 * 
	 * @param circle        是否拖拽区域为圆形
	 * @param fill          是否完全填充
	 * @param dash          是否用虚线描边
	 * @param lineWidth     线条宽度
	 * @param dashDivisions 虚线间隔
	 */
	public LDragging(boolean circle, boolean fill, boolean dash, float lineWidth, int dashDivisions) {
		this(0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), null, null, circle, fill, dash, lineWidth,
				dashDivisions);
	}

	/**
	 * 构造拖拽用组件,用于渲染出特定的拖拽区域
	 * 
	 * @param x             组件初始x
	 * @param y             组件初始y
	 * @param width         组件初始width
	 * @param height        组件初始height
	 * @param fillColor     填充选中区域用的颜色(需要fill项为true)
	 * @param rectColor     填充选中区域边框的颜色(若fill项为false则直接使用fillColor颜色)
	 * @param circle        使用圆形选择区域而非矩形
	 * @param fill          是否填充整个选框
	 * @param dash          是否使用虚线填充
	 * @param lineWidth     边框的宽度
	 * @param dashDivisions 若dash项为true时,每行显示多少个虚线
	 */
	public LDragging(int x, int y, int width, int height, LColor fillColor, LColor rectColor, boolean circle,
			boolean fill, boolean dash, float lineWidth, int dashDivisions) {
		super(x, y, width, height);
		if (fillColor == null) {
			this._fillColor = new LColor(LColor.yellow);
		} else {
			this._fillColor = new LColor(fillColor);
		}
		if (rectColor == null) {
			_rectColor = new LColor(_fillColor.lighter());
		} else {
			_rectColor = new LColor(rectColor);
		}
		this._circle = circle;
		this._fillRect = fill;
		this._dashRect = dash;
		this._lineWidth = lineWidth;
		this._dashDivisions = dashDivisions;
		this._dragDrawAlpha = 0.5f;
		this._area = new RectBox();
		this._display_area = new RectBox();
		this.locked = new ActionKey();
	}

	@Override
	public void processTouchPressed() {
		super.processTouchPressed();
		if (!(SysTouch.isDrag() && input.isMoving())) {
			if (!locked.isPressed()) {
				start();
				locked.press();
			}
		}
	}

	@Override
	public void processTouchDragged() {
		super.processTouchDragged();
		if (!locked.isPressed()) {
			start();
			locked.press();
		} else {
			drag();
		}
	}

	@Override
	public void processTouchReleased() {
		super.processTouchReleased();
		if (locked.isPressed() && _dragging) {
			if (_selectArea != null) {
				_selectArea.onArea(this._display_area.x, this._display_area.y,
						this._display_area.width - ((this._display_area.width / 6) - _lineWidth - 1),
						this._display_area.height - ((this._display_area.height / 6) - _lineWidth - 1));
			}
			stop();
		}
		locked.release();
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		if (_component_isClose) {
			return;
		}
		if (!_dragging) {
			return;
		}
		final float areaX = x + this._display_area.x;
		final float areaY = y + this._display_area.y;
		final float areaWidth = MathUtils.clamp(this._display_area.width, 1, getWidth());
		final float areaHeight = MathUtils.clamp(this._display_area.height, 1, getHeight());
		if (_circle) {
			final float areaSize = MathUtils.max(areaWidth, areaHeight) + areaWidth / 32f;
			if (_fillRect) {
				float alpha = _fillColor.a;
				if (alpha >= 1f) {
					_fillColor.a = _dragDrawAlpha;
				}
				int tint = g.color();
				g.setColor(_fillColor);
				g.fillCircle(areaX, areaY, areaSize);
				float oldLineWidth = g.getLineWidth();
				g.setLineWidth(_lineWidth);
				g.setColor(_rectColor);
				if (_dashRect) {
					g.drawDashCircle(areaX, areaY, areaSize, _dashDivisions);
				} else {
					g.drawCircle(areaX, areaY, areaSize);
				}
				g.setLineWidth(oldLineWidth);
				_fillColor.a = alpha;
				g.setTint(tint);
			} else {
				int tint = g.color();
				g.setColor(_fillColor);
				float oldLineWidth = g.getLineWidth();
				g.setLineWidth(_lineWidth);
				if (_dashRect) {
					g.drawDashCircle(areaX, areaY, areaSize, _dashDivisions);
				} else {
					g.drawCircle(areaX, areaY, areaSize);
				}
				g.setLineWidth(oldLineWidth);
				g.setTint(tint);
			}
		} else {
			if (_fillRect) {
				float alpha = _fillColor.a;
				if (alpha >= 1f) {
					_fillColor.a = _dragDrawAlpha;
				}
				g.fillRect(areaX, areaY, areaWidth, areaHeight, _fillColor);
				float oldLineWidth = g.getLineWidth();
				g.setLineWidth(_lineWidth);
				if (_dashRect) {
					g.drawDashRect(areaX, areaY, areaWidth, areaHeight, _rectColor, _dashDivisions);
				} else {
					g.drawRect(areaX, areaY, areaWidth, areaHeight, _rectColor);
				}
				g.setLineWidth(oldLineWidth);
				_fillColor.a = alpha;
			} else {
				float oldLineWidth = g.getLineWidth();
				g.setLineWidth(_lineWidth);
				if (_dashRect) {
					g.drawDashRect(areaX, areaY, areaWidth, areaHeight, _fillColor, _dashDivisions);
				} else {
					g.drawRect(areaX, areaY, areaWidth, areaHeight, _fillColor);
				}
				g.setLineWidth(oldLineWidth);
			}
		}
	}

	private void checkDisplayArea() {
		float areaX = this._area.x;
		float areaY = this._area.y;
		float areaWidth = this._area.width;
		float areaHeight = this._area.height;
		if (areaWidth < 0) {
			areaWidth = MathUtils.abs(areaWidth);
			float tmp = areaX;
			areaX = tmp - areaWidth;
		}
		if (areaHeight < 0) {
			areaHeight = MathUtils.abs(areaHeight);
			float tmp = areaY;
			areaY = tmp - areaHeight;
		}
		this._display_area.setBounds(areaX, areaY, areaWidth, areaHeight);
	}

	public RectBox getArea() {
		return this._display_area;
	}

	public LDragging start() {
		if ((getUITouchX() != this._startX || getUITouchY() != this._startY)) {
			clearArea();
			this._startX = getUITouchX();
			this._startY = getUITouchY();
			this._area.setLocation(this._startX, this._startY);
			this._dragging = false;
		}
		return this;
	}

	public LDragging drag() {
		if (getUITouchX() != this._lastX || getUITouchY() != this._lastY) {
			this._lastX = getUITouchX();
			this._lastY = getUITouchY();
			final float newSizeW = this._lastX - this._startX;
			final float newSizeH = this._lastY - this._startY;
			this._area.setSize(newSizeW, newSizeH);
			this._dragging = true;
			checkDisplayArea();
		}
		return this;
	}

	public LDragging stop() {
		if (getUITouchX() != this._lastX || getUITouchY() != this._lastY && this._dragging) {
			clearArea();
			this._lastX = getUITouchX();
			this._lastY = getUITouchY();
			this._dragging = false;
		}
		return this;
	}

	public boolean isDragging() {
		return this._dragging;
	}

	public LDragging setDragging(boolean d) {
		this._dragging = d;
		return this;
	}

	public LDragging clearArea() {
		_area.clear();
		return this;
	}

	/**
	 * 获得对应当前组件拖拽的拖拽范围的具体形状
	 * 
	 * @return
	 */
	public Shape getDragRang() {
		final float areaX = getScalePixelX() + this._display_area.x;
		final float areaY = getScalePixelY() + this._display_area.y;
		final float areaWidth = MathUtils.clamp(this._display_area.width, 1, getWidth());
		final float areaHeight = MathUtils.clamp(this._display_area.height, 1, getHeight());
		if (_circle) {
			final float centerRadius = (MathUtils.max(areaWidth, areaHeight) + areaWidth / 32f / 2f + _lineWidth / 2f)
					/ 2f;
			return new Circle(areaX + centerRadius, areaY + centerRadius, centerRadius);
		} else {
			return new RectBox(areaX, areaY, areaWidth, areaHeight);
		}
	}

	public SelectAreaListener getSelectAreaListener() {
		return _selectArea;
	}

	public LDragging setSelectAreaListener(SelectAreaListener s) {
		this._selectArea = s;
		return this;
	}

	public boolean isFillRect() {
		return _fillRect;
	}

	public int getDashDivisions() {
		return _dashDivisions;
	}

	public float getDragDrawAlpha() {
		return _dragDrawAlpha;
	}

	public LDragging setDragDrawAlpha(float da) {
		this._dragDrawAlpha = da;
		return this;
	}

	public float getStartX() {
		return _startX;
	}

	public float getStartY() {
		return _startY;
	}

	public float getLastX() {
		return _lastX;
	}

	public float getLastY() {
		return _lastY;
	}

	public float getLineWidth() {
		return _lineWidth;
	}

	public boolean isCircleArea() {
		return _circle;
	}

	public LDragging setCircleArea(boolean ca) {
		this._circle = ca;
		return this;
	}

	public LDragging setLineWidth(float l) {
		this._lineWidth = l;
		return this;
	}

	public LColor getRectColor() {
		return _rectColor;
	}

	public LDragging setRectColor(LColor r) {
		this._rectColor = new LColor(r);
		return this;
	}

	public LColor getFillColor() {
		return _fillColor;
	}

	public LDragging setFillColor(LColor r) {
		this._fillColor = new LColor(r);
		return this;
	}

	@Override
	public String getUIName() {
		return "Dragging";
	}

	@Override
	public void destory() {

	}

}
