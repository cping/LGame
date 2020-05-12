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
		SAME, CENTER, LEFT, TOP_LEFT, TOP_LEFT_CENTER, TOP_RIGHT, TOP_RIGHT_CENTER, BOTTOM_CENTER, BOTTOM_LEFT, BOTTOM_LEFT_CENTER, BOTTOM_RIGHT, BOTTOM_RIGHT_CENTER, RIGHT_CENTER, TOP_CENTER
	}

	protected RectBox renderRect;
	
	protected RectBox viewRect;

	public Director() {
		this(LSystem.viewSize);
	}

	public Director(Dimension rect) {
		if (rect != null) {
			this.renderRect = new RectBox(0, 0, rect.width, rect.height);
			this.viewRect = new RectBox(0, 0, rect.width, rect.height);
		} else {
			this.renderRect = new RectBox();
			this.viewRect = new RectBox();
		}
	}

	public void setSize(int width, int height) {
		this.renderRect.setBounds(0, 0, width, height);
		this.viewRect = new RectBox(0, 0, width, height);
	}

	public RectBox getRenderRect() {
		return renderRect;
	}

	public RectBox getViewRect() {
		return viewRect;
	}

	public int getViewLeft() {
		return viewRect.Left();
	}

	public int getViewTop() {
		return viewRect.Top();
	}

	public void view(RectBox rect) {
		rect.offset(-viewRect.Left(), -viewRect.Top());
	}

	public void view(int[] point) {
		point[0] -= viewRect.Left();
		point[1] -= viewRect.Top();
	}

	int[] point = new int[2];

	public int[] view(int x, int y) {
		point[0] = x - viewRect.Left();
		point[1] = y - viewRect.Top();
		return point;
	}

	public boolean canView(RectBox rect) {
		return viewRect.contains(rect);
	}

	public boolean canView(int x, int y) {
		return viewRect.contains(x, y);
	}

	public void move(int dx, int dy) {
		viewRect.offset(dx, dy);
	}

	public void center(int x, int y, RectBox world) {
		x -= (int) renderRect.getWidth() >> 1;
		y -= (int) renderRect.getHeight() >> 1;
		viewRect.offset(x, y);
		CollisionHelper.confine(viewRect, world);
	}

	public boolean isOrientationPortrait() {
		if (viewRect.width <= viewRect.height) {
			return true;
		} else {
			return false;
		}
	}

}
