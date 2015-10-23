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

import java.util.ArrayList;

import loon.geom.Dimension;
import loon.geom.Point.Point2i;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.processes.Process;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;

public class Director extends SoundBox {

	public enum Origin {
		CENTER, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, LEFT_CENTER, TOP_CENTER, BOTTOM_CENTER, RIGHT_CENTER
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
	
	public void setSize(int width,int height){
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
		confine(viewRect, world);
	}

	public static void confine(RectBox rect, RectBox field) {
		int x = rect.Right() > field.Right() ? field.Right()
				- (int) rect.getWidth() : rect.Left();
		if (x < field.Left()) {
			x = field.Left();
		}
		int y = (int) (rect.Bottom() > field.Bottom() ? field.Bottom()
				- rect.getHeight() : rect.Top());
		if (y < field.Top()) {
			y = field.Top();
		}
		rect.offset(x, y);
	}

	public static int[] intersect(RectBox rect1, RectBox rect2) {
		if (rect1.Left() < rect2.Right() && rect2.Left() < rect1.Right()
				&& rect1.Top() < rect2.Bottom() && rect2.Top() < rect1.Bottom()) {
			return new int[] {
					rect1.Left() < rect2.Left() ? rect2.Left() - rect1.Left()
							: 0,
					rect1.Top() < rect2.Top() ? rect2.Top() - rect1.Top() : 0,
					rect1.Right() > rect2.Right() ? rect1.Right()
							- rect2.Right() : 0,
					rect1.Bottom() > rect2.Bottom() ? rect1.Bottom()
							- rect2.Bottom() : 0 };
		}
		return null;
	}

	public boolean isOrientationPortrait() {
		if (viewRect.width <= viewRect.height) {
			return true;
		} else {
			return false;
		}
	}

	public static Vector2f makeOrigin(LObject o, Origin origin) {
		return createOrigin(o, origin);
	}

	public static ArrayList<Vector2f> makeOrigins(Origin origin,
			LObject... objs) {
		ArrayList<Vector2f> result = new ArrayList<Vector2f>(objs.length);
		for (LObject o : objs) {
			result.add(createOrigin(o, origin));
		}
		return result;
	}

	private static Vector2f createOrigin(LObject o, Origin origin) {
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

	public static void setPoisiton(LObject objToBePositioned,
			LObject objStable, Position position) {
		float atp_W = objToBePositioned.getWidth();
		float atp_H = objToBePositioned.getHeight();
		float obj_X = objStable.getX();
		float obj_Y = objStable.getY();
		float obj_XW = objStable.getWidth() + obj_X;
		float obj_YH = objStable.getHeight() + obj_Y;
		setLocation(objToBePositioned, atp_W, atp_H, obj_X, obj_Y, obj_XW,
				obj_YH, position);
	}

	public static void setPoisiton(LObject objToBePositioned, float x, float y,
			float width, float height, Position position) {
		float atp_W = objToBePositioned.getWidth();
		float atp_H = objToBePositioned.getHeight();
		float obj_X = x;
		float obj_Y = y;
		float obj_XW = width + obj_X;
		float obj_YH = height + obj_Y;
		setLocation(objToBePositioned, atp_W, atp_H, obj_X, obj_Y, obj_XW,
				obj_YH, position);
	}

	private static void setLocation(LObject objToBePositioned, float atp_W,
			float atp_H, float obj_X, float obj_Y, float obj_XW, float obj_YH,
			Position position) {
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
			objToBePositioned.setLocation(obj_X - atp_W / 2f, obj_YH - atp_H
					/ 2f);
			break;
		case TOP_RIGHT:
			objToBePositioned.setLocation(obj_XW - atp_W, obj_YH - atp_H);
			break;
		case TOP_RIGHT_CENTER:
			objToBePositioned.setLocation(obj_XW - atp_W / 2f, obj_YH - atp_H
					/ 2f);
			break;
		case TOP_CENTER:
			objToBePositioned.setLocation(obj_XW / 2f - atp_W / 2f, obj_YH
					- atp_H);
			break;
		case BOTTOM_LEFT:
			objToBePositioned.setLocation(obj_X, obj_Y);
			break;
		case BOTTOM_LEFT_CENTER:
			objToBePositioned.setLocation(obj_X - atp_W / 2f, obj_Y - atp_H
					/ 2f);
			break;
		case BOTTOM_RIGHT:
			objToBePositioned.setLocation(obj_XW - atp_W, obj_Y);
			break;
		case BOTTOM_RIGHT_CENTER:
			objToBePositioned.setLocation(obj_XW - atp_W / 2f, obj_Y - atp_H
					/ 2f);
			break;
		case BOTTOM_CENTER:
			objToBePositioned.setLocation(obj_XW / 2f - atp_W / 2f, obj_Y);
			break;
		case RIGHT_CENTER:
			objToBePositioned.setLocation(obj_XW - atp_W, obj_YH / 2f - atp_H
					/ 2f);
			break;
		default:
			objToBePositioned.setLocation(objToBePositioned.getX(),
					objToBePositioned.getY());
			break;
		}
	}

	private static final int[] SHIFT = { 0, 1144, 2289, 3435, 4583, 5734, 6888,
			8047, 9210, 10380, 11556, 12739, 13930, 15130, 16340, 17560, 18792,
			20036, 21294, 22566, 23853, 25157, 26478, 27818, 29179, 30560,
			31964, 33392, 34846, 36327, 37837, 39378, 40951, 42560, 44205,
			45889, 47615, 49385, 51202, 53070, 54991, 56970, 59009, 61113,
			63287, 65536 };

	public final static int round(int div1, int div2) {
		final int remainder = div1 % div2;
		if (Math.abs(remainder) * 2 <= Math.abs(div2)) {
			return div1 / div2;
		} else if (div1 * div2 < 0) {
			return div1 / div2 - 1;
		} else {
			return div1 / div2 + 1;
		}
	}

	public final static long round(long div1, long div2) {
		final long remainder = div1 % div2;
		if (Math.abs(remainder) * 2 <= Math.abs(div2)) {
			return div1 / div2;
		} else if (div1 * div2 < 0) {
			return div1 / div2 - 1;
		} else {
			return div1 / div2 + 1;
		}
	}

	public final static int toShift(int angle) {
		if (angle <= 45) {
			return SHIFT[angle];
		} else if (angle >= 315) {
			return -SHIFT[360 - angle];
		} else if (angle >= 135 && angle <= 180) {
			return -SHIFT[180 - angle];
		} else if (angle >= 180 && angle <= 225) {
			return SHIFT[angle - 180];
		} else if (angle >= 45 && angle <= 90) {
			return SHIFT[90 - angle];
		} else if (angle >= 90 && angle <= 135) {
			return -SHIFT[angle - 90];
		} else if (angle >= 225 && angle <= 270) {
			return SHIFT[270 - angle];
		} else {
			return -SHIFT[angle - 270];
		}
	}

	public final static Point2i getBoundingPointAtAngle(int boundingX,
			int boundingY, int boundingWidth, int boundingHeight, int angle) {
		if (angle >= 315 || angle <= 45) {
			return new Point2i(boundingX + boundingWidth, boundingY
					+ (boundingHeight * (65536 - toShift(angle)) >>> 17));
		} else if (angle > 45 && angle < 135) {
			return new Point2i(boundingX
					+ (boundingWidth * (65536 + toShift(angle)) >>> 17),
					boundingY);
		} else if (angle >= 135 && angle <= 225) {
			return new Point2i(boundingX, boundingY
					+ (boundingHeight * (65536 + toShift(angle)) >>> 17));
		} else {
			return new Point2i(boundingX
					+ (boundingWidth * (65536 - toShift(angle)) >>> 17),
					boundingY + boundingHeight);
		}
	}

	public final static RectBox getBoundingBox(int xpoints[], int ypoints[],
			int npoints) {
		int boundsMinX = Integer.MAX_VALUE;
		int boundsMinY = Integer.MAX_VALUE;
		int boundsMaxX = Integer.MIN_VALUE;
		int boundsMaxY = Integer.MIN_VALUE;

		for (int i = 0; i < npoints; i++) {
			int x = xpoints[i];
			boundsMinX = Math.min(boundsMinX, x);
			boundsMaxX = Math.max(boundsMaxX, x);
			int y = ypoints[i];
			boundsMinY = Math.min(boundsMinY, y);
			boundsMaxY = Math.max(boundsMaxY, y);
		}

		return new RectBox(boundsMinX, boundsMinY, boundsMaxX - boundsMinX,
				boundsMaxY - boundsMinY);
	}

	public final static int getBoundingShape(int xPoints[], int yPoints[],
			int startAngle, int arcAngle, int centerX, int centerY,
			int boundingX, int boundingY, int boundingWidth, int boundingHeight) {
		xPoints[0] = centerX;
		yPoints[0] = centerY;
		Point2i startPoint = getBoundingPointAtAngle(boundingX, boundingY,
				boundingWidth, boundingHeight, startAngle);
		xPoints[1] = startPoint.x;
		yPoints[1] = startPoint.y;
		int i = 2;
		for (int angle = 0; angle < arcAngle; i++, angle += 90) {
			if (angle + 90 > arcAngle
					&& ((startAngle + angle - 45) % 360) / 90 == ((startAngle
							+ arcAngle + 45) % 360) / 90) {
				break;
			}
			int modAngle = (startAngle + angle) % 360;
			if (modAngle > 315 || modAngle <= 45) {
				xPoints[i] = boundingX + boundingWidth;
				yPoints[i] = boundingY;
			} else if (modAngle > 135 && modAngle <= 225) {
				xPoints[i] = boundingX;
				yPoints[i] = boundingY + boundingHeight;
			} else if (modAngle > 45 && modAngle <= 135) {
				xPoints[i] = boundingX;
				yPoints[i] = boundingY;
			} else {
				xPoints[i] = boundingX + boundingWidth;
				yPoints[i] = boundingY + boundingHeight;
			}
		}
		Point2i endPoint = getBoundingPointAtAngle(boundingX, boundingY,
				boundingWidth, boundingHeight, (startAngle + arcAngle) % 360);
		if (xPoints[i - 1] != endPoint.x || yPoints[i - 1] != endPoint.y) {
			xPoints[i] = endPoint.x;
			yPoints[i++] = endPoint.y;
		}
		return i;
	}

	public final static boolean contains(int xPoints[], int yPoints[],
			int nPoints, RectBox bounds, int x, int y) {
		if ((bounds != null && bounds.inside(x, y))
				|| (bounds == null && getBoundingBox(xPoints, yPoints, nPoints)
						.inside(x, y))) {
			int hits = 0;
			int ySave = 0;
			int i = 0;

			while (i < nPoints && yPoints[i] == y) {
				i++;
			}
			for (int n = 0; n < nPoints; n++) {
				int j = (i + 1) % nPoints;

				int dx = xPoints[j] - xPoints[i];
				int dy = yPoints[j] - yPoints[i];

				if (dy != 0) {

					int rx = x - xPoints[i];
					int ry = y - yPoints[i];

					if (yPoints[j] == y && xPoints[j] >= x) {
						ySave = yPoints[i];
					}
					if (yPoints[i] == y && xPoints[i] >= x) {
						if ((ySave > y) != (yPoints[j] > y)) {
							hits--;
						}
					}
					if (ry * dy >= 0
							&& (ry <= dy && ry >= 0 || ry >= dy && ry <= 0)
							&& round(dx * ry, dy) >= rx) {
						hits++;
					}
				}
				i = j;
			}
			return (hits % 2) != 0;
		}

		return false;
	}
	

	public void addRealtimeProcess(RealtimeProcess realtimeProcess) {
		RealtimeProcessManager.get().addProcess(realtimeProcess);
	}

	public void removeRealtimeProcess(String id) {
		RealtimeProcessManager.get().delete(id);
	}

	public void deleteIndex(String id) {
		RealtimeProcessManager.get().deleteIndex(id);
	}

	public Process find(String id) {
		return RealtimeProcessManager.get().find(id);
	}
}
