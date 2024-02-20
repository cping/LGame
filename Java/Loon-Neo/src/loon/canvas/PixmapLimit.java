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
import loon.geom.RectBox;

/**
 * 像素边缘查找用类,用于获得指定像素集合的边缘所在
 */
public class PixmapLimit {

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
