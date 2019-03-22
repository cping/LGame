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
package loon.action.map;

import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.MathUtils;

public class GeometryUtils {

	public static Vector2f nearestToLine(Vector2f p1, Vector2f p2, Vector2f p3, Vector2f n) {
		int ax = (int) (p2.x - p1.x), ay = (int) (p2.y - p1.y);
		float u = (p3.x - p1.x) * ax + (p3.y - p1.y) * ay;
		u /= (ax * ax + ay * ay);
		n.x = p1.x + MathUtils.round(ax * u);
		n.y = p1.y + MathUtils.round(ay * u);
		return n;
	}

	public static boolean lineIntersection(Vector2f p1, Vector2f p2, boolean seg1, Vector2f p3, Vector2f p4,
			boolean seg2, Vector2f result) {
		float y43 = p4.getY() - p3.getY();
		float x21 = p2.getX() - p1.getX();
		float x43 = p4.getX() - p3.getX();
		float y21 = p2.getY() - p1.getY();
		float denom = y43 * x21 - x43 * y21;
		if (denom == 0) {
			return false;
		}

		float y13 = p1.getY() - p3.getY();
		float x13 = p1.getX() - p3.getX();
		float ua = (x43 * y13 - y43 * x13) / denom;
		if (seg1 && ((ua < 0) || (ua > 1))) {
			return false;
		}

		if (seg2) {
			float ub = (x21 * y13 - y21 * x13) / denom;
			if ((ub < 0) || (ub > 1)) {
				return false;
			}
		}

		float x = p1.getX() + ua * x21;
		float y = p1.getY() + ua * y21;
		result.setLocation(x, y);
		return true;
	}

	public static int whichSide(Vector2f p1, float theta, Vector2f p2) {
		theta += MathUtils.PI / 2;
		int x = (int) (p1.x + MathUtils.round(1000 * MathUtils.cos(theta)));
		int y = (int) (p1.y + MathUtils.round(1000 * MathUtils.sin(theta)));
		return dot(p1.x(), p1.y(), p2.x(), p2.y(), x, y);
	}

	public static void shiftToContain(RectBox tainer, RectBox tained) {
		if (tained.x < tainer.x) {
			tainer.x = tained.x;
		}
		if (tained.y < tainer.y) {
			tainer.y = tained.y;
		}
		if (tained.x + tained.width > tainer.x + tainer.width) {
			tainer.x = tained.x - (tainer.width - tained.width);
		}
		if (tained.y + tained.height > tainer.y + tainer.height) {
			tainer.y = tained.y - (tainer.height - tained.height);
		}
	}

	/**
	 * 将目标矩形添加到原始矩形的边界。
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	public static RectBox add(RectBox source, RectBox target) {
		if (target == null) {
			return new RectBox(source);
		} else if (source == null) {
			source = new RectBox(target);
		} else {
			source.add(target);
		}
		return source;
	}

	/**
	 * 填充指定瓦片的边界。瓦片从左到右，从上到下。
	 * 
	 * @param width
	 * @param height
	 * @param tileWidth
	 * @param tileHeight
	 * @param tileIndex
	 * @return
	 */
	public static RectBox getTile(int width, int height, int tileWidth, int tileHeight, int tileIndex) {
		return getTile(width, height, tileWidth, tileHeight, tileIndex, null);
	}

	/**
	 * 填充指定瓦片的边界。瓦片从左到右，从上到下。
	 * 
	 * @param width
	 * @param height
	 * @param tileWidth
	 * @param tileHeight
	 * @param tileIndex
	 * @param result
	 */
	public static RectBox getTile(int width, int height, int tileWidth, int tileHeight, int tileIndex, RectBox result) {
		if (result == null) {
			result = new RectBox();
		}
		int tilesPerRow = width / tileWidth;
		if (tilesPerRow == 0) {
			result.setBounds(0, 0, width, height);
		} else {
			int row = tileIndex / tilesPerRow;
			int col = tileIndex % tilesPerRow;
			result.setBounds(tileWidth * col, tileHeight * row, tileWidth, tileHeight);
		}
		return result;
	}

	public static int dot(Vector2f v1s, Vector2f v1e, Vector2f v2s, Vector2f v2e) {
		return (int) ((v1e.x - v1s.x) * (v2e.x - v2s.x) + (v1e.y - v1s.y) * (v2e.y - v2s.y));
	}

	public static int dot(int v1sx, int v1sy, int v1ex, int v1ey, int v2sx, int v2sy, int v2ex, int v2ey) {
		return ((v1ex - v1sx) * (v2ex - v2sx) + (v1ey - v1sy) * (v2ey - v2sy));
	}

	public static int dot(Vector2f vs, Vector2f v1e, Vector2f v2e) {
		return (int) ((v1e.x - vs.x) * (v2e.x - vs.x) + (v1e.y - vs.y) * (v2e.y - vs.y));
	}

	public static int dot(int vsx, int vsy, int v1ex, int v1ey, int v2ex, int v2ey) {
		return ((v1ex - vsx) * (v2ex - vsx) + (v1ey - vsy) * (v2ey - vsy));
	}

	public static void transPointList(float[] points, float x, float y) {
		int i = 0, len = points.length;
		for (i = 0; i < len; i += 2) {
			points[i] += x;
			points[i + 1] += y;
		}
	}

	public static void transPointList(int[] points, int x, int y) {
		int i = 0, len = points.length;
		for (i = 0; i < len; i += 2) {
			points[i] += x;
			points[i + 1] += y;
		}
	}
}
