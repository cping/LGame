package loon.action.collision;

import java.util.ArrayList;

import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.geom.Point;
import loon.core.geom.Polygon;
import loon.core.geom.RectBox;
import loon.core.graphics.LImage;
import loon.core.graphics.opengl.LTexture;


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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
public class CollisionMask implements LRelease {

	private int top, left, right, bottom;

	private LTexture.Mask data;

	private RectBox rect;

	public static Polygon makePolygon(String res) {
		return makePolygon(LImage.createImage(res));
	}

	public static Polygon makePolygon(LImage image) {
		if (image == null) {
			throw new RuntimeException("Image is null !");
		}
		return makePolygon(image.getPixels(), image.getWidth(), image
				.getHeight());
	}

	public static Polygon makePolygon(int[] pixels, int w, int h) {
		return makePolygon(pixels, 0, 0, 0, 0, w, h, 3);
	}

	public static Polygon makePolygon(int[] pixels, int offsetX, int offsetY,
			int startX, int startY, int limitX, int limitY, int interval) {
		Polygon split = null;
		Polygon result = null;
		ArrayList<Point[]> points = new ArrayList<Point[]>();
		Point[] tmpPoint;
		int x1, y1, x2, y2;
		boolean secondPoint;
		int pixel = 0;
		for (int y = startY; y < limitY - interval; y += interval) {

			secondPoint = false;
			x1 = y1 = -1;
			x2 = y2 = -1;
			for (int x = startX; x < limitX; x++) {
				pixel = pixels[x + limitX * y];
				if (!secondPoint) {
					if ((pixel & LSystem.TRANSPARENT) == LSystem.TRANSPARENT) {
						x1 = x;
						y1 = y;
						secondPoint = true;
					}
				} else {
					if ((pixel & LSystem.TRANSPARENT) == LSystem.TRANSPARENT) {
						x2 = x;
						y2 = y;
					}
				}
			}
			if (secondPoint && (x2 > -1) && (y2 > -1)) {
				tmpPoint = new Point[2];
				tmpPoint[0] = new Point(offsetX + x1, offsetY + y1);
				tmpPoint[1] = new Point(offsetX + x2, offsetY + y2);
				points.add(tmpPoint);
			}
		}
		split = makePolygon(points);
		if (split != null) {
			points = new ArrayList<Point[]>();

			for (int x = startX; x < limitX - interval; x += interval) {
				secondPoint = false;
				x1 = y1 = -1;
				x2 = y2 = -1;
				for (int y = startY; y < limitY; y++) {
					pixel = pixels[x + limitX * y];
					if (!secondPoint) {
						if ((pixel & LSystem.TRANSPARENT) == LSystem.TRANSPARENT) {
							x1 = x;
							y1 = y;
							secondPoint = true;
						}
					} else {
						if ((pixel & LSystem.TRANSPARENT) == LSystem.TRANSPARENT) {
							x2 = x;
							y2 = y;
						}
					}
				}
				if (secondPoint && (x2 > -1) && (y2 > -1)) {
					tmpPoint = new Point[2];
					tmpPoint[0] = new Point(offsetX + x1, offsetY + y1);
					tmpPoint[1] = new Point(offsetX + x2, offsetY + y2);
					points.add(tmpPoint);
				}
			}
			result = makePolygon(points);

		}
		return result;
	}

	/**
	 * 将指定的Point集合注入Polygon当中
	 * 
	 * @param points
	 * @return
	 */
	private static Polygon makePolygon(ArrayList<Point[]> points) {
		Polygon polygon = null;
		if (!points.isEmpty()) {
			int size = points.size();
			polygon = new Polygon();
			for (int i = 0; i < size; i++) {
				Point p = (points.get(i))[0];
				polygon.addPoint(p.x, p.y);
			}
			for (int i = size - 1; i >= 0; i--) {
				Point p = (points.get(i))[1];
				polygon.addPoint(p.x, p.y);
			}
		}
		return polygon;
	}

	public static LTexture.Mask createMask(String res) {
		return createMask(LImage.createImage(res));
	}

	public static LTexture.Mask createMask(LImage image) {
		if (image == null) {
			throw new RuntimeException("Image is null !");
		}
		return createMask(image.getPixels(), image.getWidth(), image
				.getHeight());
	}

	public static LTexture.Mask createMask(int[] pixels, int w, int h) {
		int width = w;
		int height = h;
		LTexture.Mask data = new LTexture.Mask(width, height);
		boolean[][] mask = new boolean[height][width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				mask[y][x] = (pixels[x + w * y] & LSystem.TRANSPARENT) == LSystem.TRANSPARENT;
			}
		}
		data.setData(mask);
		return data;
	}

	public CollisionMask(LTexture.Mask data) {
		set(data, 0, 0, data.getWidth(), data.getHeight());
	}

	public void set(LTexture.Mask data, int x, int y, int w, int h) {
		this.data = data;
		if (rect == null) {
			this.rect = new RectBox(x, y, w, h);
		} else {
			this.rect.setBounds(x, y, w, h);
		}
	}

	public RectBox getBounds() {
		return rect;
	}

	private void calculateBoundingBox() {
		top = (int) (rect.y - rect.height / 2);
		left = (int) (rect.x - rect.width / 2);
		right = (left + rect.width);
		bottom = (top + rect.height);
	}

	public boolean checkBoundingBoxCollision(CollisionMask other) {
		return rect.intersects(other.getBounds())
				|| rect.contains(other.getBounds());
	}

	public boolean checkBoundingBoxCollision(int x, int y) {
		return rect.intersects(x, y) || rect.contains(x, y);
	}

	public boolean collidesWith(CollisionMask other) {
		if (checkBoundingBoxCollision(other)) {
			other.calculateBoundingBox();
			calculateBoundingBox();
			boolean a = false;
			boolean b = false;
			for (int y = top; y < bottom; y++) {
				for (int x = left; x < right; x++) {
					a = data.getPixel(x - left, y - top);
					b = other.data.getPixel(x - other.left, y - other.top);
					if (a && b) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean collidesWith(int x, int y) {
		if (checkBoundingBoxCollision(x, y)) {
			calculateBoundingBox();
			return data.getPixel(x - left, y - top);
		}
		return false;
	}

	@Override
	public void dispose() {
		if (data != null) {
			data.dispose();
			data = null;
		}
	}

}
