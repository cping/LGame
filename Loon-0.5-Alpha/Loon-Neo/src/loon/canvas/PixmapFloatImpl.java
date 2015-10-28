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

import loon.Director;
import loon.geom.RectBox;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;

public abstract class PixmapFloatImpl extends Director {

	private int _skip = 3;

	private RectBox _clip = new RectBox();

	private float _translateX = 0, _translateY = 0;

	private float _width, _height;

	public PixmapFloatImpl(float tx, float ty, RectBox clip, float w,
			float h, int skip) {
		setClipImpl(tx, ty, clip, w, h, skip);
	}

	protected void setClipImpl(float tx, float ty, RectBox clip, float w,
			float h) {
		this.setClipImpl(tx, ty, clip, w, h, _skip);
	}

	protected void setClipImpl(float tx, float ty, RectBox clip, float w,
			float h, int skip) {
		this._translateX = tx;
		this._translateY = ty;
		this._clip.setBounds(clip);
		this._width = w;
		this._height = h;
		this._skip = skip;
	}

	protected void fillPolygonImpl(float[] xPoints, float[] yPoints, int nPoints) {
		float[] xPointsCopy;
		if (_translateX == 0) {
			xPointsCopy = xPoints;
		} else {
			xPointsCopy = CollectionUtils.copyOf(xPoints);
			for (int i = 0; i < nPoints; i++) {
				xPointsCopy[i] += _translateX;
			}
		}
		float[] yPointsCopy;
		if (_translateY == 0) {
			yPointsCopy = yPoints;
		} else {
			yPointsCopy = CollectionUtils.copyOf(yPoints);
			for (int i = 0; i < nPoints; i++) {
				yPointsCopy[i] += _translateY;
			}
		}
		RectBox bounds = getBoundingBox(xPointsCopy, yPointsCopy, nPoints)
				.getIntersection(_clip);
		for (int x = bounds.x(); x < bounds.x + bounds.width; x += _skip) {
			for (int y = bounds.y(); y < bounds.y + bounds.height; y += _skip) {
				if (contains(xPointsCopy, yPointsCopy, nPoints, bounds, x, y)) {
					drawPointImpl(x, y);
				}
			}
		}

	}

	protected void drawLineImpl(float x1, float x2, float y) {
		if (y >= _clip.y && y < _clip.y + _clip.height) {
			y *= _width;
			float maxX = MathUtils.min(x2, _clip.x + _clip.width - 1);
			for (int x = (int) MathUtils.max(x1, _clip.x); x <= maxX; x += _skip) {
				drawPointImpl(x, (x + y) / _width);
			}
		}
	}

	protected void drawOvalImpl(float x, float y, float width, float height) {
		drawCircleImpl(x, y, width, height, false, new CircleUpdate() {
			public void newPoint(float xLeft, float yTop, float xRight,
					float yBottom) {
				drawPointImpl(xLeft, yTop);
				drawPointImpl(xRight, yTop);
				drawPointImpl(xLeft, yBottom);
				drawPointImpl(xRight, yBottom);
			}
		});

	}

	protected void drawPolygonImpl(float xPoints[], float yPoints[], int nPoints) {
		drawPolylineImpl(xPoints, yPoints, nPoints);
		drawLineImpl(xPoints[nPoints - 1], yPoints[nPoints - 1], xPoints[0],
				yPoints[0]);
	}

	protected void drawPolylineImpl(float xPoints[], float yPoints[],
			float nPoints) {
		for (int i = 1; i < nPoints; i++) {
			drawLineImpl(xPoints[i - 1], yPoints[i - 1], xPoints[i], yPoints[i]);
		}
	}

	protected void fillOvalImpl(float x, float y, float width, float height) {
		drawCircleImpl(x, y, width, height, true, new CircleUpdate() {
			public void newPoint(float xLeft, float yTop, float xRight,
					float yBottom) {
				drawLineImpl(xLeft, xRight, yTop);
				if (yTop != yBottom) {
					drawLineImpl(xLeft, xRight, yBottom);
				}
			}
		});
	}

	protected void drawVerticalLineImpl(float x, float y1, float y2) {
		if (x >= _clip.x && x < _clip.x + _clip.width) {
			int maxY = (int) (MathUtils.min(y2, _clip.y + _clip.height - 1) * _width);
			for (int y = (int) (MathUtils.max(y1, _clip.y) * _width); y <= maxY; y += _width
					+ _skip) {
				drawPointImpl(x, (x + y) / _width);
			}
		}
	}

	protected void drawLineImpl(float x1, float y1, float x2, float y2) {

		x1 += _translateX;
		y1 += _translateY;
		x2 += _translateX;
		y2 += _translateY;

		float dx = x2 - x1;
		float dy = y2 - y1;

		if (dx == 0) {
			if (y1 < y2) {
				drawVerticalLineImpl(x1, y1, y2);
			} else {
				drawVerticalLineImpl(x1, y2, y1);
			}
		} else if (dy == 0) {
			if (x1 < x2) {
				drawLineImpl(x1, x2, y1);
			} else {
				drawLineImpl(x2, x1, y1);
			}
		} else {
			boolean swapXY = false;
			int dxNeg = 1;
			int dyNeg = 1;
			boolean negativeSlope = false;
			if (MathUtils.abs(dy) > MathUtils.abs(dx)) {
				float temp = x1;
				x1 = y1;
				y1 = temp;
				temp = x2;
				x2 = y2;
				y2 = temp;
				dx = x2 - x1;
				dy = y2 - y1;
				swapXY = true;
			}

			if (x1 > x2) {
				float temp = x1;
				x1 = x2;
				x2 = temp;
				temp = y1;
				y1 = y2;
				y2 = temp;
				dx = x2 - x1;
				dy = y2 - y1;
			}

			if (dy * dx < 0) {
				if (dy < 0) {
					dyNeg = -1;
					dxNeg = 1;
				} else {
					dyNeg = 1;
					dxNeg = -1;
				}
				negativeSlope = true;
			}

			float d = 2 * (dy * dyNeg) - (dx * dxNeg);
			float incrH = 2 * dy * dyNeg;
			float incrHV = 2 * ((dy * dyNeg) - (dx * dxNeg));
			float x = x1;
			float y = y1;
			float tempX = x;
			float tempY = y;

			if (swapXY) {
				float temp = x;
				x = y;
				y = temp;
			}

			drawPointImpl(x, y);
			x = tempX;
			y = tempY;

			while (x < x2) {
				if (d <= 0) {
					x++;
					d += incrH;
				} else {
					d += incrHV;
					x++;
					if (!negativeSlope) {
						y++;
					} else {
						y--;
					}
				}

				tempX = x;
				tempY = y;
				if (swapXY) {
					float temp = x;
					x = y;
					y = temp;
				}
				drawPointImpl(x, y);
				x = tempX;
				y = tempY;
			}
		}
	}

	protected void drawArcImpl(float x, float y, float width, float height,
			float start, float arcAngle) {
		if (arcAngle == 0) {
			return;
		}
		start %= 360;
		if (start < 0) {
			start += 360;
		}
		if (arcAngle % 360 == 0) {
			arcAngle = 360;
		} else {
			arcAngle %= 360;
		}
		final float startAngle = arcAngle > 0 ? start
				: (start + arcAngle < 0 ? start + arcAngle + 360 : start
						+ arcAngle);

		final float centerX = x + _translateX + width / 2;
		final float centerY = y + _translateY + height / 2;
		final float xPoints[] = new float[7];
		final float yPoints[] = new float[7];
		final int nPoints = getBoundingShape(xPoints, yPoints, startAngle,
				MathUtils.abs(arcAngle), centerX, centerY, x + _translateX - 1,
				y + _translateY - 1, width + 2, height + 2);
		final RectBox bounds = getBoundingBox(xPoints, yPoints, nPoints)
				.getIntersection(_clip);
		this.drawCircleImpl(x, y, width, height, false, new CircleUpdate() {
			public void newPoint(float xLeft, float yTop, float xRight,
					float yBottom) {
				drawArcPointImpl(xPoints, yPoints, nPoints, bounds, xLeft, yTop);
				drawArcPointImpl(xPoints, yPoints, nPoints, bounds, xRight,
						yTop);
				drawArcPointImpl(xPoints, yPoints, nPoints, bounds, xLeft,
						yBottom);
				drawArcPointImpl(xPoints, yPoints, nPoints, bounds, xRight,
						yBottom);
			}
		});
	}

	protected void drawArcPointImpl(float[] xPoints, float[] yPoints,
			int nPoints, RectBox bounds, float x, float y) {
		if (contains(xPoints, yPoints, nPoints, bounds, x, y)) {
			drawPointImpl(x, y);
		}
	}

	protected void fillArcImpl(float x, float y, float width, float height,
			float start, float arcAngle) {
		if (arcAngle == 0) {
			return;
		}
		if (arcAngle < 0) {
			start = 360 - arcAngle;
			arcAngle = 360 + arcAngle;
		}
		start %= 360;
		if (start < 0) {
			start += 360;
		}
		if (arcAngle % 360 == 0) {
			fillOvalImpl(x, y, width, height);
		} else {
			arcAngle %= 360;
		}
		final float startAngle = arcAngle > 0 ? start
				: (start + arcAngle < 0 ? start + arcAngle + 360 : start
						+ arcAngle);

		final float centerX = x + _translateX + width / 2;
		final float centerY = y + _translateY + height / 2;
		final float xPoints[] = new float[7];
		final float yPoints[] = new float[7];
		final int nPoints = getBoundingShape(xPoints, yPoints, startAngle,
				MathUtils.abs(arcAngle), centerX, centerY, x + _translateX - 1,
				y + _translateY - 1, width + 2, height + 2);
		final RectBox bounds = getBoundingBox(xPoints, yPoints, nPoints);

		this.drawCircleImpl(x, y, width, height, true, new CircleUpdate() {
			public void newPoint(float xLeft, float yTop, float xRight,
					float yBottom) {
				drawArcImpl(xPoints, yPoints, nPoints, bounds, xLeft, xRight,
						yTop);
				if (yTop != yBottom) {
					drawArcImpl(xPoints, yPoints, nPoints, bounds, xLeft,
							xRight, yBottom);
				}
			}
		});
	}

	protected void drawArcImpl(float[] xPoints, float[] yPoints, int nPoints,
			RectBox bounds, float xLeft, float xRight, float y) {
		if (y >= _clip.y && y < _clip.y + _clip.height) {
			for (int x = (int) MathUtils.max(xLeft, _clip.x); x <= xRight; x += _skip) {
				if (contains(xPoints, yPoints, nPoints, bounds, x, y)) {
					drawPointImpl(x, y);
				}
			}
		}
	}

	protected void drawCircleImpl(float x, float y, float width, float height,
			boolean fill, CircleUpdate listener) {
		float a = width / 2;
		float b = height / 2;
		float squareA = (width * width / 4);
		float squareB = (height * height / 4);
		float squareAB = round(width * width * height * height, 16L);

		x += _translateX;
		y += _translateY;
		float centerX = x + a;
		float centerY = y + b;

		int deltaX = (width % 2 == 0) ? 0 : 1;
		int deltaY = (height % 2 == 0) ? 0 : 1;

		float currentY = b;
		float currentX = 0;

		float lastx1 = centerX - currentX;
		float lastx2 = centerX + currentX + deltaX;
		float lasty1 = centerY - currentY;
		float lasty2 = centerY + currentY + deltaY;
		while (currentX <= a && currentY >= 0) {
			float deltaA = (currentX + 1) * (currentX + 1) * squareB + currentY
					* currentY * squareA - squareAB;
			float deltaB = (currentX + 1) * (currentX + 1) * squareB
					+ (currentY - 1) * (currentY - 1) * squareA - squareAB;
			float deltaC = currentX * currentX * squareB + (currentY - 1)
					* (currentY - 1) * squareA - squareAB;
			if (deltaA <= 0) {
				currentX++;
			} else if (deltaC >= 0) {
				currentY--;
			} else {
				int min = (int) MathUtils.min(
						MathUtils.abs(deltaA),
						MathUtils.min(MathUtils.abs(deltaB),
								MathUtils.abs(deltaC)));
				if (min == MathUtils.abs(deltaA)) {
					currentX++;
				} else if (min == MathUtils.abs(deltaC)) {
					currentY--;
				} else {
					currentX++;
					currentY--;
				}
			}

			float x1 = centerX - currentX;
			float x2 = centerX + currentX + deltaX;
			float y1 = centerY - currentY;
			float y2 = centerY + currentY + deltaY;
			if (!fill || lasty1 != y1) {
				listener.newPoint(lastx1, lasty1, lastx2, lasty2);
				lasty1 = y1;
				lasty2 = y2;
			}
			lastx1 = x1;
			lastx2 = x2;
		}
		if (lasty1 < lasty2) {
			for (; lasty1 <= lasty2; lasty1++, lasty2--) {
				listener.newPoint(centerX - a, lasty1, centerX + a + deltaX,
						lasty2);
			}
		}
	}

	protected boolean inside(float x, float y) {
		return (x < _clip.x || x >= _clip.x + _clip.width || y < _clip.y || y >= _clip.y
				+ _clip.height);
	}

	
	protected void drawRectImpl(float x1, float y1, float w1, float h1) {
		float tempX = x1;
		float tempY = y1;
		float tempWidth = x1 + w1;
		float tempHeight = y1 + h1;
		if (tempX > tempWidth) {
			x1 = tempX;
			tempX = tempWidth;
			tempWidth = x1;
		}
		if (tempY > tempHeight) {
			y1 = tempY;
			tempY = tempHeight;
			tempHeight = y1;
		}
		drawLineImpl(tempX, tempY, tempHeight, tempY);
		drawLineImpl(tempX, tempY + 1, tempX, tempHeight);
		drawLineImpl(tempHeight, tempHeight, tempX + 1, tempHeight);
		drawLineImpl(tempHeight, tempHeight - 1, tempHeight, tempY + 1);
	}

	protected void drawRoundRectImpl(float x, float y, float width, float height,
			float arcWidth, float arcHeight) {
		drawLineImpl(x + arcWidth / 2, y, x + width - arcWidth / 2, y);
		drawLineImpl(x, y + arcHeight / 2, x, y + height - arcHeight / 2);
		drawLineImpl(x + arcWidth / 2, y + height, x + width - arcWidth / 2, y
				+ height);
		drawLineImpl(x + width, y + arcHeight / 2, x + width, y + height
				- arcHeight / 2);
		drawArcImpl(x, y, arcWidth, arcHeight, 90, 90);
		drawArcImpl(x + width - arcWidth, y, arcWidth, arcHeight, 0, 90);
		drawArcImpl(x, y + height + -arcHeight, arcWidth, arcHeight, 180, 90);
		drawArcImpl(x + width - arcWidth, y + height + -arcHeight, arcWidth,
				arcHeight, 270, 90);
	}
	
	protected void fillRoundRectImpl(float x, float y, float width, float height,
			float arcWidth, float arcHeight) {
		fillRectNative(x + arcWidth / 2, y, width - arcWidth + 1, height);
		fillRectNative(x, y + arcHeight / 2 - 1, arcWidth / 2, height - arcHeight);
		fillRectNative(x + width - arcWidth / 2, y + arcHeight / 2 - 1, arcWidth / 2,
				height - arcHeight);

		fillArcImpl(x, y, arcWidth - 1, arcHeight - 1, 90, 90);
		fillArcImpl(x + width - arcWidth, y, arcWidth - 1, arcHeight - 1, 0, 90);
		fillArcImpl(x, y + height + -arcHeight, arcWidth - 1, arcHeight - 1, 180,
				90);
		fillArcImpl(x + width - arcWidth, y + height + -arcHeight, arcWidth - 1,
				arcHeight - 1, 270, 90);
	}
	
	protected void fillRoundRectImpl(float x, float y, float width, float height,
			int radius) {
		if (radius < 0) {
			throw new IllegalArgumentException("radius > 0");
		}
		if (radius == 0) {
			fillRectNative(x, y, width, height);
			return;
		}
		int mr = (int) MathUtils.min(width, height) / 2;
		if (radius > mr) {
			radius = mr;
		}
		float d = radius * 2;
		fillRectNative(x + radius, y, width - d, radius);
		fillRectNative(x, y + radius, radius, height - d);
		fillRectNative(x + width - radius, y + radius, radius, height - d);
		fillRectNative(x + radius, y + height - radius, width - d, radius);
		fillRectNative(x + radius, y + radius, width - d, height - d);
		fillArcImpl(x + width - d, y + height - d, d, d, 0, 90);
		fillArcImpl(x, y + height - d, d, d, 90, 180);
		fillArcImpl(x + width - d, y, d, d, 270, 360);
		fillArcImpl(x, y, d, d, 180, 270);
	}
	
	protected void drawPointImpl(float x, float y) {
		if (_skip > 7) {
			int loc = _skip / 2;
			drawPointNative(x - loc, y - loc, _skip);
		} else {
			drawPointNative(x, y, _skip);
		}
	}

	protected abstract void drawPointNative(float x, float y, int skip);

	protected abstract void fillRectNative(float x, float y,float width,float height);
	
	private interface CircleUpdate {
		public void newPoint(float xLeft, float yTop, float xRight,
				float yBottom);
	}

	public float getWidthImpl() {
		return _width;
	}

	public float getHeightImpl() {
		return _height;
	}

}
