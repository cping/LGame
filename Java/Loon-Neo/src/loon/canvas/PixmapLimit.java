/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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

import loon.canvas.Canvas.ColorPixel;
import loon.geom.AABB;
import loon.geom.PointI;
import loon.geom.RectBox;
import loon.utils.MathUtils;
import loon.utils.SortedList;

/**
 * 像素边缘查找用类,用于获得指定像素集合的边缘所在
 */
public class PixmapLimit {

	/**
	 * 按照指定边缘颜色,切分出指定色块
	 * 
	 * @param pixmap
	 * @param x
	 * @param y
	 * @param blockColor
	 * @param borderColor
	 * @param blackColor
	 * @param borderSize
	 * @return
	 */
	public static Pixmap findBorder(Pixmap pixmap, int x, int y, LColor blockColor, LColor borderColor,
			LColor blackColor, int borderSize) {
		if (pixmap == null) {
			return null;
		}
		boolean[][] fills = floodFill(pixmap, x, y, blockColor.getARGB());
		Pixmap result = putBorder(pixmap, fills, borderColor.getARGB(), blackColor.getARGB(), borderSize);
		return result;
	}

	private static boolean[][] floodFill(Pixmap pixmap, int x, int y, int blockColor) {
		final boolean[][] fills = new boolean[pixmap.getWidth()][pixmap.getHeight()];
		final SortedList<PointI> queue = new SortedList<PointI>();
		queue.clear();
		queue.add(new PointI(x, y));
		int newX = 0;
		int newY = 0;
		PointI temp;
		while (!queue.isEmpty()) {
			temp = queue.remove();
			newX = MathUtils.ifloor(temp.getX());
			newY = MathUtils.ifloor(temp.getY());
			if (newX >= 0 && newX < pixmap.getWidth() && newY >= 0 && newY < pixmap.getHeight()) {
				int pixel = pixmap.getPixel(newX, newY);
				if (!fills[newX][newY] && pixel != blockColor) {
					fills[newX][newY] = true;
					pixmap.putPixel(newX, newY, 0);
					queue.add(new PointI(newX + 1, newY));
					queue.add(new PointI(newX - 1, newY));
					queue.add(new PointI(newX, newY + 1));
					queue.add(new PointI(newX, newY - 1));
				}
			}
		}
		return fills;
	}

	private static void putBorder(Pixmap pixmap, int borderColor, int borderSize, int x, int y) {
		for (int i = -borderSize; i < borderSize; i++) {
			for (int j = -borderSize; j < borderSize; j++) {
				pixmap.putPixel(x + i, y + j, borderColor);
			}
		}
	}

	private static boolean nearBorder(Pixmap pixmap, int x, int y, int black) {
		int tx = 0;
		int ty = 0;
		boolean result = false;
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				tx = x + i;
				ty = y + j;
				if ((tx >= 0 && tx < pixmap.getWidth()) || (ty >= 0 && ty < pixmap.getHeight())) {
					int pixel = pixmap.getPixel(tx, ty);
					if (pixel == black) {
						return true;
					}
				}
			}
		}
		return result;
	}

	private static Pixmap putBorder(Pixmap pixmap, boolean[][] fills, int borderColor, int borderSize, int black) {
		Pixmap outputPixmap = new Pixmap(pixmap.getWidth(), pixmap.getHeight());
		for (int x = 0; x < pixmap.getWidth(); x++) {
			for (int y = 0; y < pixmap.getHeight(); y++) {
				if (fills[x][y] && nearBorder(pixmap, x, y, black)) {
					outputPixmap.putPixel(x, y, borderColor);
					putBorder(outputPixmap, borderColor, borderSize, x, y);
				} else {
					outputPixmap.putPixel(x, y, black);
				}
			}
		}
		return outputPixmap;
	}

	public final static PixmapLimit find(ColorPixel pixels) {
		final PixmapLimit result = new PixmapLimit();
		final LColor c1 = new LColor();
		final LColor c2 = new LColor();
		final int startX = pixels.getPixelWidth() / 2;
		boolean foundFlag = false;
		for (int x = startX - 1; x >= 0 && !foundFlag; x--) {
			for (int y = 0; y < pixels.getPixelHeight(); y++) {
				c1.setColor(pixels.get(x, y));
				c2.setColor(pixels.get(x + 1, y));
				if (!c1.equals(c2)) {
					result.left = x + 1;
					foundFlag = true;
					break;
				}
			}
		}
		if (!foundFlag) {
			result.left = 0;
		}
		foundFlag = false;
		for (int x = startX + 1; x < pixels.getPixelWidth() && !foundFlag; x++) {
			for (int y = 0; y < pixels.getPixelHeight(); y++) {
				c1.setColor(pixels.get(x, y));
				c2.setColor(pixels.get(x - 1, y));
				if (!c1.equals(c2)) {
					result.right = pixels.getPixelWidth() - x;
					foundFlag = true;
					break;
				}
			}
		}
		if (!foundFlag) {
			result.right = 0;
		}
		int startY = pixels.getPixelHeight() / 2;
		foundFlag = false;
		for (int y = startY - 1; y >= 0 && !foundFlag; y--) {
			for (int x = 0; x < pixels.getPixelWidth(); x++) {
				c1.setColor(pixels.get(x, y));
				c2.setColor(pixels.get(x, y + 1));
				if (!c1.equals(c2)) {
					result.top = y + 1;
					foundFlag = true;
					break;
				}
			}
		}
		if (!foundFlag) {
			result.top = 0;
		}

		foundFlag = false;
		for (int y = startY + 1; y < pixels.getPixelHeight() && !foundFlag; y++) {
			for (int x = 0; x < pixels.getPixelWidth(); x++) {
				c1.setColor(pixels.get(x, y));
				c2.setColor(pixels.get(x, y - 1));
				if (!c1.equals(c2)) {
					result.bottom = pixels.getPixelHeight() - y;
					foundFlag = true;
					break;
				}
			}
		}
		if (!foundFlag) {
			result.bottom = 0;
		}
		return result;
	}

	public int left;
	public int right;
	public int top;
	public int bottom;

	public RectBox getRectBox() {
		return RectBox.at(top, left, right, bottom);
	}

	public AABB getAABB() {
		return AABB.at(top, left, right, bottom);
	}
}
