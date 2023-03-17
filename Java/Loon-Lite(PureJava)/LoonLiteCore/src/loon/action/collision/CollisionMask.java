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
package loon.action.collision;

import loon.BaseIO;
import loon.LSysException;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.canvas.Pixmap;
import loon.geom.PointF;
import loon.geom.Polygon;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.FloatArray;
import loon.utils.TArray;

/**
 * 一个根据像素产生碰撞区域的简单不规则形状生成器
 */
public class CollisionMask {

	public static Hitbox makeHitBox(String res) {
		return new Hitbox(makePolygon(res));
	}

	public static Hitbox makeHitBox(Image image) {
		return new Hitbox(makePolygon(image));
	}

	public static Hitbox makeHitBox(Pixmap image) {
		return new Hitbox(makePolygon(image));
	}

	public static Polygon makePolygon(String res) {
		return makePolygon(BaseIO.loadImage(res));
	}

	public static Polygon makePolygon(Image image) {
		if (image == null) {
			throw new LSysException("Image is null !");
		}
		return makePolygon(image.getPixels(), (int) image.width(), (int) image.height());
	}

	public static Polygon makePolygon(Pixmap image) {
		if (image == null) {
			throw new LSysException("Image is null !");
		}
		return makePolygon(image.getData(), image.getWidth(), image.getHeight());
	}

	public static Polygon makePolygon(int[] pixels, int w, int h) {
		return makePolygon(pixels, 0, 0, 0, 0, w, h, 3);
	}

	public static Polygon makePolygon(int[] pixels, int offsetX, int offsetY, int startX, int startY, int limitX,
			int limitY, int interval) {
		Polygon split = null;
		Polygon result = null;
		TArray<PointF[]> points = new TArray<>();
		PointF[] tmpPoint;
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
					if ((pixel & LColor.TRANSPARENT) == LColor.TRANSPARENT) {
						x1 = x;
						y1 = y;
						secondPoint = true;
					}
				} else {
					if ((pixel & LColor.TRANSPARENT) == LColor.TRANSPARENT) {
						x2 = x;
						y2 = y;
					}
				}
			}
			if (secondPoint && (x2 > -1) && (y2 > -1)) {
				tmpPoint = new PointF[2];
				tmpPoint[0] = new PointF(offsetX + x1, offsetY + y1);
				tmpPoint[1] = new PointF(offsetX + x2, offsetY + y2);
				points.add(tmpPoint);
			}
		}
		split = makePolygon(points);
		if (split != null) {
			points = new TArray<>();

			for (int x = startX; x < limitX - interval; x += interval) {
				secondPoint = false;
				x1 = y1 = -1;
				x2 = y2 = -1;
				for (int y = startY; y < limitY; y++) {
					pixel = pixels[x + limitX * y];
					if (!secondPoint) {
						if ((pixel & LColor.TRANSPARENT) == LColor.TRANSPARENT) {
							x1 = x;
							y1 = y;
							secondPoint = true;
						}
					} else {
						if ((pixel & LColor.TRANSPARENT) == LColor.TRANSPARENT) {
							x2 = x;
							y2 = y;
						}
					}
				}
				if (secondPoint && (x2 > -1) && (y2 > -1)) {
					tmpPoint = new PointF[2];
					tmpPoint[0] = new PointF(offsetX + x1, offsetY + y1);
					tmpPoint[1] = new PointF(offsetX + x2, offsetY + y2);
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
	private static Polygon makePolygon(TArray<PointF[]> points) {
		Polygon polygon = null;
		if (!points.isEmpty()) {
			int size = points.size;
			polygon = new Polygon();
			for (int i = 0; i < size; i++) {
				PointF p = (points.get(i))[0];
				polygon.addPoint(p.x, p.y);
			}
			for (int i = size - 1; i >= 0; i--) {
				PointF p = (points.get(i))[1];
				polygon.addPoint(p.x, p.y);
			}
		}
		return polygon;
	}

	/**
	 * 转换顶点数据为Vector2f对象
	 *
	 * @param vertices
	 * @return
	 */
	public TArray<Vector2f> convertPoints(float[] vertices) {
		TArray<Vector2f> vectores = new TArray<>();
		for (int i = 0; i < vertices.length; i = i + 2) {
			vectores.add(new Vector2f(vertices[i], vertices[i + 1]));
		}
		return vectores;
	}

	/**
	 * 转换矩形对象为顶点数据
	 *
	 * @param rect
	 * @return
	 */
	public static float[] getRectVertices(RectBox rect) {
		FloatArray vertices = new FloatArray();
		float x1 = rect.x, y1 = rect.y, x2 = rect.x + rect.width, y2 = rect.y,
				x3 = rect.x + rect.width, y3 = rect.y + rect.height, x4 = rect.x,
				y4 = rect.y + rect.height;
		vertices.add(x1);
		vertices.add(y1);
		vertices.add(x2);
		vertices.add(y2);
		vertices.add(x3);
		vertices.add(y3);
		vertices.add(x4);
		vertices.add(y4);
		return vertices.toArray();
	}

}
