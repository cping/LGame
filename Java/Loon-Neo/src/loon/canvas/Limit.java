/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.canvas;

import loon.geom.PointI;
import loon.geom.RectF;
import loon.geom.RectI;
import loon.utils.MathUtils;

public class Limit {

	private final static PointI tmp_point = new PointI(0, 0);

	private final static RectF tmp_rect_f = new RectF();

	private final static RectI tmp_rect_I = new RectI();

	public final static void setBoundingPointAtAngle(PointI p, int boundingX,
			int boundingY, int boundingWidth, int boundingHeight, int angle) {
		if (angle >= 315 || angle <= 45) {
			p.set(boundingX + boundingWidth,
					boundingY
							+ (boundingHeight
									* (65536 - MathUtils.toShift(angle)) >>> 17));
		} else if (angle > 45 && angle < 135) {
			p.set(boundingX
					+ (boundingWidth * (65536 + MathUtils.toShift(angle)) >>> 17),
					boundingY);
		} else if (angle >= 135 && angle <= 225) {
			p.set(boundingX,
					boundingY
							+ (boundingHeight
									* (65536 + MathUtils.toShift(angle)) >>> 17));
		} else {
			p.set(boundingX
					+ (boundingWidth * (65536 - MathUtils.toShift(angle)) >>> 17),
					boundingY + boundingHeight);
		}
	}

	public final static PointI getBoundingPointAtAngle(int boundingX,
			int boundingY, int boundingWidth, int boundingHeight, int angle) {
		if (angle >= 315 || angle <= 45) {
			return new PointI(
					boundingX + boundingWidth,
					boundingY
							+ (boundingHeight
									* (65536 - MathUtils.toShift(angle)) >>> 17));
		} else if (angle > 45 && angle < 135) {
			return new PointI(
					boundingX
							+ (boundingWidth
									* (65536 + MathUtils.toShift(angle)) >>> 17),
					boundingY);
		} else if (angle >= 135 && angle <= 225) {
			return new PointI(
					boundingX,
					boundingY
							+ (boundingHeight
									* (65536 + MathUtils.toShift(angle)) >>> 17));
		} else {
			return new PointI(
					boundingX
							+ (boundingWidth
									* (65536 - MathUtils.toShift(angle)) >>> 17),
					boundingY + boundingHeight);
		}
	}

	public final static RectF getBoundingBox(float[] points, int npoints) {
		float boundsMinX = Float.MAX_VALUE;
		float boundsMinY = Float.MAX_VALUE;
		float boundsMaxX = Float.MIN_VALUE;
		float boundsMaxY = Float.MIN_VALUE;

		for (int i = 0; i < npoints; i = +2) {
			float x = points[i];
			boundsMinX = MathUtils.min(boundsMinX, x);
			boundsMaxX = MathUtils.max(boundsMaxX, x);
			float y = points[i + 1];
			boundsMinY = MathUtils.min(boundsMinY, y);
			boundsMaxY = MathUtils.max(boundsMaxY, y);
		}

		return new RectF(boundsMinX, boundsMinY, boundsMaxX - boundsMinX,
				boundsMaxY - boundsMinY);
	}

	public final static RectF setBoundingBox(RectF rect, float[] xpoints,
			float[] ypoints, int npoints) {
		float boundsMinX = Float.MAX_VALUE;
		float boundsMinY = Float.MAX_VALUE;
		float boundsMaxX = Float.MIN_VALUE;
		float boundsMaxY = Float.MIN_VALUE;

		for (int i = 0; i < npoints; i++) {
			float x = xpoints[i];
			boundsMinX = MathUtils.min(boundsMinX, x);
			boundsMaxX = MathUtils.max(boundsMaxX, x);
			float y = ypoints[i];
			boundsMinY = MathUtils.min(boundsMinY, y);
			boundsMaxY = MathUtils.max(boundsMaxY, y);
		}

		return rect.set(boundsMinX, boundsMinY, boundsMaxX - boundsMinX,
				boundsMaxY - boundsMinY);
	}

	public final static RectF getBoundingBox(float[] xpoints, float[] ypoints,
			int npoints) {
		float boundsMinX = Float.MAX_VALUE;
		float boundsMinY = Float.MAX_VALUE;
		float boundsMaxX = Float.MIN_VALUE;
		float boundsMaxY = Float.MIN_VALUE;

		for (int i = 0; i < npoints; i++) {
			float x = xpoints[i];
			boundsMinX = MathUtils.min(boundsMinX, x);
			boundsMaxX = MathUtils.max(boundsMaxX, x);
			float y = ypoints[i];
			boundsMinY = MathUtils.min(boundsMinY, y);
			boundsMaxY = MathUtils.max(boundsMaxY, y);
		}

		return new RectF(boundsMinX, boundsMinY, boundsMaxX - boundsMinX,
				boundsMaxY - boundsMinY);
	}

	public final static RectI setBoundingBox(RectI rect, int xpoints[],
			int ypoints[], int npoints) {
		int boundsMinX = Integer.MAX_VALUE;
		int boundsMinY = Integer.MAX_VALUE;
		int boundsMaxX = Integer.MIN_VALUE;
		int boundsMaxY = Integer.MIN_VALUE;

		for (int i = 0; i < npoints; i++) {
			int x = xpoints[i];
			boundsMinX = MathUtils.min(boundsMinX, x);
			boundsMaxX = MathUtils.max(boundsMaxX, x);
			int y = ypoints[i];
			boundsMinY = MathUtils.min(boundsMinY, y);
			boundsMaxY = MathUtils.max(boundsMaxY, y);
		}

		return rect.set(boundsMinX, boundsMinY, boundsMaxX - boundsMinX,
				boundsMaxY - boundsMinY);
	}

	public final static RectI getBoundingBox(int xpoints[], int ypoints[],
			int npoints) {
		int boundsMinX = Integer.MAX_VALUE;
		int boundsMinY = Integer.MAX_VALUE;
		int boundsMaxX = Integer.MIN_VALUE;
		int boundsMaxY = Integer.MIN_VALUE;

		for (int i = 0; i < npoints; i++) {
			int x = xpoints[i];
			boundsMinX = MathUtils.min(boundsMinX, x);
			boundsMaxX = MathUtils.max(boundsMaxX, x);
			int y = ypoints[i];
			boundsMinY = MathUtils.min(boundsMinY, y);
			boundsMaxY = MathUtils.max(boundsMaxY, y);
		}

		return new RectI(boundsMinX, boundsMinY, boundsMaxX - boundsMinX,
				boundsMaxY - boundsMinY);
	}

	public final static int getBoundingShape(int xPoints[], int yPoints[],
			int startAngle, int arcAngle, int centerX, int centerY,
			int boundingX, int boundingY, int boundingWidth, int boundingHeight) {
		xPoints[0] = centerX;
		yPoints[0] = centerY;
		setBoundingPointAtAngle(tmp_point, boundingX, boundingY, boundingWidth,
				boundingHeight, startAngle);
		xPoints[1] = tmp_point.x;
		yPoints[1] = tmp_point.y;
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
		setBoundingPointAtAngle(tmp_point, boundingX, boundingY, boundingWidth,
				boundingHeight, (startAngle + arcAngle) % 360);
		if (xPoints[i - 1] != tmp_point.x || yPoints[i - 1] != tmp_point.y) {
			xPoints[i] = tmp_point.x;
			yPoints[i++] = tmp_point.y;
		}
		return i;
	}

	public final static int getBoundingShape(float xPoints[], float yPoints[],
			float startAngle, float arcAngle, float centerX, float centerY,
			float boundingX, float boundingY, float boundingWidth,
			float boundingHeight) {
		xPoints[0] = centerX;
		yPoints[0] = centerY;
		setBoundingPointAtAngle(tmp_point, (int) boundingX, (int) boundingY,
				(int) boundingWidth, (int) boundingHeight, (int) startAngle);
		xPoints[1] = tmp_point.x;
		yPoints[1] = tmp_point.y;
		int i = 2;
		for (int angle = 0; angle < arcAngle; i++, angle += 90) {
			if (angle + 90 > arcAngle
					&& ((startAngle + angle - 45) % 360) / 90 == ((startAngle
							+ arcAngle + 45) % 360) / 90) {
				break;
			}
			float modAngle = (startAngle + angle) % 360;
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
		setBoundingPointAtAngle(tmp_point, (int) boundingX, (int) boundingY,
				(int) boundingWidth, (int) boundingHeight,
				(int) (startAngle + arcAngle) % 360);
		if (xPoints[i - 1] != tmp_point.x || yPoints[i - 1] != tmp_point.y) {
			xPoints[i] = tmp_point.x;
			yPoints[i++] = tmp_point.y;
		}
		return i;
	}

	public final static boolean contains(int[] xPoints, int[] yPoints,
			int nPoints, RectI bounds, int x, int y) {
		if ((bounds != null && bounds.inside(x, y))
				|| (bounds == null && setBoundingBox(tmp_rect_I, xPoints,
						yPoints, nPoints).inside(x, y))) {
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
							&& MathUtils.round(dx * ry, dy) >= rx) {
						hits++;
					}
				}
				i = j;
			}
			return (hits % 2) != 0;
		}

		return false;
	}

	public final static boolean contains(float[] xPoints, float[] yPoints,
			int nPoints, RectF bounds, float x, float y) {
		if ((bounds != null && bounds.inside(x, y))
				|| (bounds == null && setBoundingBox(tmp_rect_f, xPoints,
						yPoints, nPoints).inside(x, y))) {
			int hits = 0;
			float ySave = 0;
			int i = 0;

			while (i < nPoints && yPoints[i] == y) {
				i++;
			}
			for (int n = 0; n < nPoints; n++) {
				int j = (i + 1) % nPoints;

				float dx = xPoints[j] - xPoints[i];
				float dy = yPoints[j] - yPoints[i];

				if (dy != 0) {

					float rx = x - xPoints[i];
					float ry = y - yPoints[i];

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
							&& MathUtils.round(dx * ry, dy) >= rx) {
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
