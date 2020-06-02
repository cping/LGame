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
import loon.LTexture;
import loon.canvas.LColor;
import loon.event.ActionKey;
import loon.event.SelectAreaListener;
import loon.event.SysTouch;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

public class LDragging extends LComponent {

	private SelectAreaListener _selectArea;

	private boolean _fillRect;

	private boolean _dashRect;

	private boolean _dragging;

	private boolean _circle;

	private int _dashDivisions;

	private RectBox _area;

	private RectBox _display_area;

	private float _startX;

	private float _startY;

	private float _lastX;

	private float _lastY;

	private ActionKey locked;

	private float _lineWidth;

	private LColor _fillColor;

	private LColor _rectColor;

	/**
	 * 构造拖拽用组件,用于渲染出特定的拖拽区域(默认使用全天渲染模式,使用虚线边框,边框线宽4,每行虚线由5个子线条组成)
	 */
	public LDragging() {
		this(false, true, true, 4f, 5);
	}

	/**
	 * 构造拖拽用组件,用于渲染出特定的拖拽区域
	 * 
	 * @param circle
	 * @param fill
	 * @param dash
	 * @param lineWidth
	 * @param dashDivisions
	 */
	public LDragging(boolean circle, boolean fill, boolean dash, float lineWidth, int dashDivisions) {
		this(0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), null, null, circle, fill, dash, lineWidth,
				dashDivisions);
	}

	/**
	 * 构造拖拽用组件,用于渲染出特定的拖拽区域
	 * 
	 * @param x
	 *            组件初始x
	 * @param y
	 *            组件初始y
	 * @param width
	 *            组件初始width
	 * @param height
	 *            组件初始height
	 * @param fillColor
	 *            填充选中区域用的颜色(需要fill项为true)
	 * @param rectColor
	 *            填充选中区域边框的颜色(若fill项为false则直接使用fillColor颜色)
	 * @param circle
	 *            使用圆形选择区域而非矩形
	 * @param fill
	 *            是否填充整个选框
	 * @param dash
	 *            是否使用虚线填充
	 * @param lineWidth
	 *            边框的宽度
	 * @param dashDivisions
	 *            若dash项为true时,每行显示多少个虚线
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
			_rectColor = this._fillColor.darker();
		} else {
			_rectColor = new LColor(rectColor);
		}
		this._circle = circle;
		this._fillRect = fill;
		this._dashRect = dash;
		this._lineWidth = lineWidth;
		this._dashDivisions = dashDivisions;
		this._area = new RectBox();
		this._display_area = new RectBox();
		this.locked = new ActionKey();
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
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		if (!_dragging) {
			return;
		}
		final float areaX = this._display_area.x;
		final float areaY = this._display_area.y;
		final float areaWidth = this._display_area.width;
		final float areaHeight = this._display_area.height;
		if (_circle) {
			final float areaSize = MathUtils.max(areaWidth, areaHeight) + areaWidth / 32f;
			if (_fillRect) {
				float alpha = _fillColor.a;
				if (alpha >= 1f) {
					_fillColor.a = 0.5f;
				}
				int tint = g.getTint();
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
				int tint = g.getTint();
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
					_fillColor.a = 0.5f;
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

	public SelectAreaListener getSelectAreaListener() {
		return _selectArea;
	}

	public void setSelectAreaListener(SelectAreaListener s) {
		this._selectArea = s;
	}

	public boolean isFillRect() {
		return _fillRect;
	}

	public int getDashDivisions() {
		return _dashDivisions;
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

	public void setLineWidth(float l) {
		this._lineWidth = l;
	}

	public LColor getRectColor() {
		return _rectColor;
	}

	public void setRectColor(LColor r) {
		this._rectColor = new LColor(r);
	}

	public LColor getFillColor() {
		return _fillColor;
	}

	public void setFillColor(LColor r) {
		this._fillColor = new LColor(r);
	}

	@Override
	public String getUIName() {
		return "Dragging";
	}

}
