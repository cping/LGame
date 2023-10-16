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

import loon.action.collision.CollisionHelper;
import loon.geom.Dimension;
import loon.geom.RectBox;

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

	private int[] _tempPoint = new int[2];

	private RectBox _renderRect;
	private RectBox _viewRect;

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

	public void setSize(int width, int height) {
		this._renderRect.setBounds(0, 0, width, height);
		this._viewRect = new RectBox(0, 0, width, height);
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

	public Director view(RectBox rect) {
		rect.offset(-_viewRect.Left(), -_viewRect.Top());
		return this;
	}

	public Director view(int[] point) {
		point[0] -= _viewRect.Left();
		point[1] -= _viewRect.Top();
		return this;
	}

	public int[] view(int x, int y) {
		_tempPoint[0] = x - _viewRect.Left();
		_tempPoint[1] = y - _viewRect.Top();
		return _tempPoint;
	}

	public boolean canView(RectBox rect) {
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
}
