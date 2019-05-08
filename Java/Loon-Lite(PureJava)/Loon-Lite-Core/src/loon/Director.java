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

import loon.Log.Level;
import loon.action.collision.CollisionHelper;
import loon.event.Updateable;
import loon.geom.Affine2f;
import loon.geom.Dimension;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.Calculator;
import loon.utils.MathUtils;
import loon.utils.TArray;

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

	RectBox renderRect;
	RectBox viewRect;

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

	public final static EmptyObject newEmptyObject() {
		return new EmptyObject();
	}

	public final static Vector2f makeOrigin(LObject<?> o, Origin origin) {
		return createOrigin(o, origin);
	}

	public final static TArray<Vector2f> makeOrigins(Origin origin, LObject<?>... objs) {
		TArray<Vector2f> result = new TArray<Vector2f>(objs.length);
		for (LObject<?> o : objs) {
			result.add(createOrigin(o, origin));
		}
		return result;
	}

	private static Vector2f createOrigin(LObject<?> o, Origin origin) {
		Vector2f v = new Vector2f(o.x(), o.y());
		switch (origin) {
		case CENTER:
			v.set(o.getWidth() / 2f, o.getHeight() / 2f);
			return v;
		case TOP_LEFT:
			v.set(0.0f, o.getHeight());
			return v;
		case TOP_RIGHT:
			v.set(o.getWidth(), o.getHeight());
			return v;
		case BOTTOM_LEFT:
			v.set(0.0f, 0.0f);
			return v;
		case BOTTOM_RIGHT:
			v.set(o.getWidth(), 0.0f);
			return v;
		case LEFT_CENTER:
			v.set(0.0f, o.getHeight() / 2f);
			return v;
		case TOP_CENTER:
			v.set(o.getWidth() / 2f, o.getHeight());
			return v;
		case BOTTOM_CENTER:
			v.set(o.getWidth() / 2f, 0.0f);
			return v;
		case RIGHT_CENTER:
			v.set(o.getWidth(), o.getHeight() / 2f);
			return v;
		default:
			return v;
		}
	}

	public final static void setPoisiton(LObject<?> objToBePositioned, LObject<?> objStable, Position position) {
		float atp_W = objToBePositioned.getWidth();
		float atp_H = objToBePositioned.getHeight();
		float obj_X = objStable.getX();
		float obj_Y = objStable.getY();
		float obj_XW = objStable.getWidth() + obj_X;
		float obj_YH = objStable.getHeight() + obj_Y;
		setLocation(objToBePositioned, atp_W, atp_H, obj_X, obj_Y, obj_XW, obj_YH, position);
	}

	public final static void setPoisiton(LObject<?> objToBePositioned, float x, float y, float width, float height,
			Position position) {
		float atp_W = objToBePositioned.getWidth();
		float atp_H = objToBePositioned.getHeight();
		float obj_X = x;
		float obj_Y = y;
		float obj_XW = width + obj_X;
		float obj_YH = height + obj_Y;
		setLocation(objToBePositioned, atp_W, atp_H, obj_X, obj_Y, obj_XW, obj_YH, position);
	}

	private static void setLocation(LObject<?> objToBePositioned, float atp_W, float atp_H, float obj_X, float obj_Y,
			float obj_XW, float obj_YH, Position position) {
		switch (position) {
		case CENTER:
			objToBePositioned.setX((obj_XW / 2f) - atp_W / 2f);
			objToBePositioned.setY((obj_YH / 2f) - atp_H / 2f);
			break;
		case SAME:
			objToBePositioned.setLocation(obj_X, obj_Y);
			break;
		case LEFT:
			objToBePositioned.setLocation(obj_X, obj_YH / 2f - atp_H / 2f);
			break;
		case TOP_LEFT:
			objToBePositioned.setLocation(obj_X, obj_YH - atp_H);
			break;
		case TOP_LEFT_CENTER:
			objToBePositioned.setLocation(obj_X - atp_W / 2f, obj_YH - atp_H / 2f);
			break;
		case TOP_RIGHT:
			objToBePositioned.setLocation(obj_XW - atp_W, obj_YH - atp_H);
			break;
		case TOP_RIGHT_CENTER:
			objToBePositioned.setLocation(obj_XW - atp_W / 2f, obj_YH - atp_H / 2f);
			break;
		case TOP_CENTER:
			objToBePositioned.setLocation(obj_XW / 2f - atp_W / 2f, obj_YH - atp_H);
			break;
		case BOTTOM_LEFT:
			objToBePositioned.setLocation(obj_X, obj_Y);
			break;
		case BOTTOM_LEFT_CENTER:
			objToBePositioned.setLocation(obj_X - atp_W / 2f, obj_Y - atp_H / 2f);
			break;
		case BOTTOM_RIGHT:
			objToBePositioned.setLocation(obj_XW - atp_W, obj_Y);
			break;
		case BOTTOM_RIGHT_CENTER:
			objToBePositioned.setLocation(obj_XW - atp_W / 2f, obj_Y - atp_H / 2f);
			break;
		case BOTTOM_CENTER:
			objToBePositioned.setLocation(obj_XW / 2f - atp_W / 2f, obj_Y);
			break;
		case RIGHT_CENTER:
			objToBePositioned.setLocation(obj_XW - atp_W, obj_YH / 2f - atp_H / 2f);
			break;
		default:
			objToBePositioned.setLocation(objToBePositioned.getX(), objToBePositioned.getY());
			break;
		}
	}

	private final static Affine2f _trans = new Affine2f();

	public final static Vector2f local2Global(float centerX, float centerY, float posX, float posY,
			Vector2f resultPoint) {
		return local2Global(0, 1f, 1f, 0, 0, false, false, centerX, centerY, posX, posY, resultPoint);
	}

	public final static Vector2f local2Global(boolean flipX, boolean flipY, float centerX, float centerY, float posX,
			float posY, Vector2f resultPoint) {
		return local2Global(0, 1f, 1f, 0, 0, flipX, flipY, centerX, centerY, posX, posY, resultPoint);
	}

	public final static Vector2f local2Global(float rotation, float centerX, float centerY, float posX, float posY,
			Vector2f resultPoint) {
		return local2Global(rotation, 1f, 1f, 0, 0, false, false, centerX, centerY, posX, posY, resultPoint);
	}

	public final static Vector2f local2Global(float rotation, boolean flipX, boolean flipY, float centerX,
			float centerY, float posX, float posY, Vector2f resultPoint) {
		return local2Global(rotation, 1f, 1f, 0, 0, flipX, flipY, centerX, centerY, posX, posY, resultPoint);
	}

	public final static Vector2f local2Global(float rotation, float scaleX, float scaleY, boolean flipX, boolean flipY,
			float centerX, float centerY, float posX, float posY, Vector2f resultPoint) {
		return local2Global(rotation, scaleX, scaleY, 0, 0, flipX, flipY, centerX, centerY, posX, posY, resultPoint);
	}

	public final static Vector2f local2Global(float rotation, float scaleX, float scaleY, float skewX, float skewY,
			boolean flipX, boolean flipY, float centerX, float centerY, float posX, float posY, Vector2f resultPoint) {
		_trans.idt();
		if (rotation != 0) {
			_trans.translate(centerX, centerY);
			_trans.preRotate(rotation);
			_trans.translate(-centerX, -centerY);
		}
		if (flipX || flipY) {
			if (flipX && flipY) {
				Affine2f.transform(_trans, centerX, centerY, Affine2f.TRANS_ROT180);
			} else if (flipX) {
				Affine2f.transform(_trans, centerX, centerY, Affine2f.TRANS_MIRROR);
			} else if (flipY) {
				Affine2f.transform(_trans, centerX, centerY, Affine2f.TRANS_MIRROR_ROT180);
			}
		}
		if ((scaleX != 1) || (scaleY != 1)) {
			_trans.translate(centerX, centerY);
			_trans.preScale(scaleX, scaleY);
			_trans.translate(-centerX, -centerY);
		}
		if ((skewX != 0) || (skewY != 0)) {
			_trans.translate(centerX, centerY);
			_trans.preShear(skewX, skewY);
			_trans.translate(-centerX, -centerY);
		}
		if (resultPoint != null) {
			_trans.transformPoint(posX, posY, resultPoint);
			return resultPoint;
		}
		return resultPoint;
	}

	public final static float random() {
		return MathUtils.random();
	}

	public final static float random(float start, float end) {
		return MathUtils.random(start, end);
	}

	public final static Calculator newCalculator() {
		return new Calculator();
	}

	public final static Counter newCounter() {
		return new Counter();
	}

	public final static LimitedCounter newLimitedCounter(int limit) {
		return new LimitedCounter(limit);
	}

	public final static ActionCounter newActionCounter(int limit, Updateable update) {
		return new ActionCounter(limit, update);
	}

	public final static void debug(String msg) {
		LSystem.debug(msg);
	}

	public final static void debug(String msg, Object... args) {
		LSystem.debug(msg, args);
	}

	public final static void debug(String msg, Throwable throwable) {
		LSystem.debug(msg, throwable);
	}

	public final static void info(String msg) {
		LSystem.info(msg);
	}

	public final static void info(String msg, Object... args) {
		LSystem.info(msg, args);
	}

	public final static void info(String msg, Throwable throwable) {
		LSystem.info(msg, throwable);
	}

	public final static void error(String msg) {
		LSystem.error(msg);
	}

	public final static void error(String msg, Object... args) {
		LSystem.error(msg, args);
	}

	public final static void error(String msg, Throwable throwable) {
		LSystem.error(msg, throwable);
	}

	public final static void reportError(String msg, Throwable throwable) {
		LSystem.reportError(msg, throwable);
	}

	public final static void d(String msg) {
		LSystem.debug(msg);
	}

	public final static void d(String msg, Object... args) {
		LSystem.debug(msg, args);
	}

	public final static void d(String msg, Throwable throwable) {
		LSystem.debug(msg, throwable);
	}

	public final static void i(String msg) {
		LSystem.info(msg);
	}

	public final static void i(String msg, Object... args) {
		LSystem.info(msg, args);
	}

	public final static void i(String msg, Throwable throwable) {
		LSystem.info(msg, throwable);
	}

	public final static void w(String msg) {
		LSystem.warn(msg);
	}

	public final static void w(String msg, Object... args) {
		LSystem.warn(msg, args);
	}

	public final static void w(String msg, Throwable throwable) {
		LSystem.warn(msg, throwable);
	}

	public final static void e(String msg) {
		LSystem.error(msg);
	}

	public final static void e(String msg, Object... args) {
		LSystem.error(msg, args);
	}

	public final static void e(String msg, Throwable throwable) {
		LSystem.error(msg, throwable);
	}

	public final static void setLogMinLevel(Level level) {
		LSystem.setLogMinLevel(level);
	}
}
