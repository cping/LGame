package org.loon.framework.javase.game.core.graphics.device;

import java.awt.Point;

import org.loon.framework.javase.game.core.geom.RectBox;

/**
 * Copyright 2008 - 2011
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public abstract class LGraphicsMath {

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

	public final static Point getBoundingPointAtAngle(int boundingX,
			int boundingY, int boundingWidth, int boundingHeight, int angle) {
		if (angle >= 315 || angle <= 45) {
			return new Point(boundingX + boundingWidth, boundingY
					+ (boundingHeight * (65536 - toShift(angle)) >>> 17));
		} else if (angle > 45 && angle < 135) {
			return new Point(boundingX
					+ (boundingWidth * (65536 + toShift(angle)) >>> 17),
					boundingY);
		} else if (angle >= 135 && angle <= 225) {
			return new Point(boundingX, boundingY
					+ (boundingHeight * (65536 + toShift(angle)) >>> 17));
		} else {
			return new Point(boundingX
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
		Point startPoint = getBoundingPointAtAngle(boundingX, boundingY,
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
		Point endPoint = getBoundingPointAtAngle(boundingX, boundingY,
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

}
