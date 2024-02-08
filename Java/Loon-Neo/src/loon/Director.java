/**
 * Copyright 2013 The Loon Authors
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
 */
package loon;

import loon.action.ActionBind;
import loon.action.collision.CollisionHelper;
import loon.geom.Dimension;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.geom.XYZW;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 此类被Screen继承,所有功能可以在Screen直接使用
 */
public class Director extends SoundBox {

	public enum Origin {

		FIXED {
			@Override
			public float ox(float width) {
				return 0;
			}

			@Override
			public float oy(float height) {
				return 0;
			}
		},

		CENTER {
			@Override
			public float ox(float width) {
				return width / 2;
			}

			@Override
			public float oy(float height) {
				return height / 2;
			}
		},

		TOP_LEFT {
			@Override
			public float ox(float width) {
				return 0;
			}

			@Override
			public float oy(float height) {
				return height;
			}
		},

		TOP_RIGHT {
			@Override
			public float ox(float width) {
				return width;
			}

			@Override
			public float oy(float height) {
				return height;
			}
		},

		BOTTOM_LEFT {
			@Override
			public float ox(float width) {
				return 0;
			}

			@Override
			public float oy(float height) {
				return 0;
			}
		},

		BOTTOM_RIGHT {
			@Override
			public float ox(float width) {
				return width;
			}

			@Override
			public float oy(float height) {
				return 0;
			}
		},

		LEFT_CENTER {
			@Override
			public float ox(float width) {
				return 0;
			}

			@Override
			public float oy(float height) {
				return height / 2;
			}
		},

		TOP_CENTER {
			@Override
			public float ox(float width) {
				return width / 2;
			}

			@Override
			public float oy(float height) {
				return height;
			}
		},

		BOTTOM_CENTER {
			@Override
			public float ox(float width) {
				return width / 2;
			}

			@Override
			public float oy(float height) {
				return 0;
			}
		},

		RIGHT_CENTER {
			@Override
			public float ox(float width) {
				return width;
			}

			@Override
			public float oy(float height) {
				return height / 2;
			}
		};

		public abstract float ox(float width);

		public abstract float oy(float height);
	}

	public enum Position {
		SAME, CENTER, LEFT, TOP_LEFT, TOP_LEFT_CENTER, TOP_RIGHT, TOP_RIGHT_CENTER, BOTTOM_CENTER, BOTTOM_LEFT,
		BOTTOM_LEFT_CENTER, BOTTOM_RIGHT, BOTTOM_RIGHT_CENTER, RIGHT_CENTER, TOP_CENTER
	}

	private boolean _isTranslate;

	private int[] _tempPoint = new int[2];

	private final RectBox _renderRect;
	private final RectBox _viewRect;

	private boolean _renderlocked = false;

	private boolean _viewlocked = false;

	public Director() {
		this(LSystem.viewSize);
	}

	public Director(Dimension rect) {
		if (rect != null) {
			this._renderRect = new RectBox(0, 0, rect.width, rect.height);
			this._viewRect = new RectBox(0, 0, rect.width, rect.height);
		} else {
			this._renderRect = new RectBox();
			this._viewRect = new RectBox();
		}
	}

	protected void offsetDirectorStart(GLEx g) {
		if (_isTranslate) {
			g.translate(_viewRect.x, _viewRect.y);
		}
	}

	protected void offsetDirectorStop(GLEx g) {
		if (_isTranslate) {
			g.translate(-_viewRect.x, -_viewRect.y);
		}
	}

	public boolean isTranslate() {
		return _isTranslate;
	}

	public float getX() {
		return _renderRect.x + _viewRect.x;
	}

	public float getY() {
		return _renderRect.y + _viewRect.y;
	}

	public int getWidth() {
		return _renderRect.width;
	}

	public int getHeight() {
		return _renderRect.height;
	}

	public int getHalfWidth() {
		return MathUtils.ifloor(_renderRect.getWidth() / 2f);
	}

	public int getHalfHeight() {
		return MathUtils.ifloor(_renderRect.getHeight() / 2f);
	}

	public int getHalfViewWidth() {
		return MathUtils.ifloor(_viewRect.getWidth() / 2f);
	}

	public int getHalfViewHeight() {
		return MathUtils.ifloor(_viewRect.getHeight() / 2f);
	}

	public int getHalfRenderWidth() {
		return MathUtils.ifloor(_renderRect.getWidth() / 2f);
	}

	public int getHalfRenderHeight() {
		return MathUtils.ifloor(_renderRect.getHeight() / 2f);
	}

	public float getRenderX() {
		return _renderRect.x;
	}

	public float getRenderY() {
		return _renderRect.y;
	}

	public float getViewX() {
		return _viewRect.x;
	}

	public float getViewY() {
		return _viewRect.y;
	}

	public int getRenderWidth() {
		return _renderRect.width;
	}

	public int getRenderHeight() {
		return _renderRect.height;
	}

	public int getViewWidth() {
		return _viewRect.width;
	}

	public int getViewHeight() {
		return _viewRect.height;
	}

	public void resetDirectorPos() {
		_renderRect.setLocation(0f, 0f);
		_viewRect.setLocation(0f, 0f);
		_isTranslate = false;
	}

	protected void setDirectorViewLocation(float x, float y) {
		_isTranslate = (x != 0f || y != 0f);
		_viewRect.setLocation(x, y);
	}

	protected void setDirectorViewLocationX(float x) {
		_viewRect.setLocation(x, _viewRect.getY());
		if (x != 0f) {
			_isTranslate = true;
		}
	}

	protected void setDirectorViewLocationY(float y) {
		_viewRect.setLocation(_viewRect.getX(), y);
		if (y != 0f) {
			_isTranslate = true;
		}
	}

	public void updateRenderSize(float x, float y, float width, float height) {
		if (isRenderEqualView()) {
			setDirectorRenderSize(x, y, width, height);
			setDirectorViewSize(x, y, width, height);
		} else {
			setDirectorRenderSize(x, y, width, height);
		}
	}

	protected void setDirectorSize(float x, float y, float width, float height) {
		setDirectorRenderSize(x, y, width, height);
		setDirectorViewSize(x, y, width, height);
	}

	protected void setDirectorPos(float x, float y) {
		setDirectorRenderPos(x, y);
		setDirectorViewPos(x, y);
	}

	public void resetDirectorViewPos() {
		setDirectorViewPos(0f, 0f);
	}

	protected void setDirectorRenderPos(float x, float y) {
		if (_renderlocked) {
			return;
		}
		_renderRect.setLocation(x, y);
	}

	protected void setDirectorViewPos(float x, float y) {
		if (_viewlocked) {
			return;
		}
		_viewRect.setLocation(x, y);
	}

	protected void setDirectorRenderSize(float x, float y, float width, float height) {
		if (_renderlocked) {
			return;
		}
		this._renderRect.setBounds(x, y, width, height);
	}

	protected void setDirectorViewSize(float x, float y, float width, float height) {
		if (_viewlocked) {
			return;
		}
		this._viewRect.setBounds(x, y, width, height);
	}

	protected void updateTouch(LProcess p) {
		if (p != null) {
			final RectBox v = _viewRect;
			float x = v.getWidth() / _renderRect.getWidth();
			float y = v.getHeight() / _renderRect.getHeight();
			p.setOffsetTouchX(v.x);
			p.setOffsetTouchY(v.y);
			p.setScaleTouchX(x);
			p.setScaleTouchY(y);
		}
	}

	public boolean isRenderEqualView() {
		return _renderRect.equals(_viewRect);
	}

	public RectBox lookAt(ActionBind[] objects, XY padding) {

		if (objects == null) {
			return null;
		}

		if (padding == null) {
			padding = new Vector2f();
		}

		final int size = objects.length;

		final RectBox rect = new RectBox(_viewRect);

		float x = 0f;
		float y = 0f;
		float width = 0f;
		float height = 0f;

		for (int i = 0; i < size; i += 1) {

			final ActionBind o = objects[i];

			if (o != null) {

				x = o.getX();
				y = o.getY();
				width = o.getWidth();
				height = o.getHeight();

				rect.x = MathUtils.min(x, rect.x);
				rect.y = MathUtils.min(y, rect.y);
				rect.width = MathUtils.iceil(MathUtils.max(width, rect.width));
				rect.height = MathUtils.iceil(MathUtils.max(height, rect.height));

			}
		}

		width = rect.width + 2f * padding.getX();
		height = rect.height + 2f * padding.getY();

		RectBox result = new RectBox(rect.x, rect.y, width, height);

		return result;
	}

	public void clearDirector() {
		_viewRect.setEmpty();
		_renderRect.setEmpty();
		_renderlocked = false;
		_viewlocked = false;
		_isTranslate = false;
	}

	public RectBox getRenderRect() {
		return _renderRect;
	}

	public RectBox getViewRect() {
		return _viewRect;
	}

	public int getViewLeft() {
		return _viewRect.Left();
	}

	public int getViewTop() {
		return _viewRect.Top();
	}

	public Director offsetView(RectBox rect) {
		if (rect == null) {
			return this;
		}
		rect.offset(-_viewRect.Left(), -_viewRect.Top());
		return this;
	}

	public Director getViews(int[] point) {
		point[0] -= _viewRect.Left();
		point[1] -= _viewRect.Top();
		return this;
	}

	public int[] getViews(int x, int y) {
		_tempPoint[0] = x - _viewRect.Left();
		_tempPoint[1] = y - _viewRect.Top();
		return _tempPoint;
	}

	public void view(float width, float height) {
		setView(width, height);
	}

	public void setView(float width, float height) {
		this._viewRect.setSize(width, height);
	}

	public boolean canView(XYZW rect) {
		if (rect == null) {
			return false;
		}
		return _viewRect.contains(rect);
	}

	public boolean canView(RectBox rect) {
		if (rect == null) {
			return false;
		}
		return _viewRect.contains(rect);
	}

	public boolean canView(int x, int y) {
		return _viewRect.contains(x, y);
	}

	public Director move(int dx, int dy) {
		_viewRect.offset(dx, dy);
		return this;
	}

	public Director center(int x, int y, RectBox world) {
		x -= _renderRect.width() >> 1;
		y -= _renderRect.height() >> 1;
		_viewRect.offset(x, y);
		CollisionHelper.confine(_viewRect, world);
		return this;
	}

	public boolean isOrientationPortrait() {
		if (_viewRect.width <= _viewRect.height) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isRenderlocked() {
		return _renderlocked;
	}

	public void setRenderlocked(boolean r) {
		this._renderlocked = r;
	}

	public boolean isViewlocked() {
		return _viewlocked;
	}

	public void setViewlocked(boolean v) {
		this._viewlocked = v;
	}
}
