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
		TArray<PointF[]> points = new TArray<PointF[]>();
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
			points = new TArray<PointF[]>();

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

}
