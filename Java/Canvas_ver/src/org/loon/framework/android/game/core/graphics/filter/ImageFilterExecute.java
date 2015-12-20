package org.loon.framework.android.game.core.graphics.filter;

import android.graphics.Bitmap;

/**
 * Copyright 2008 - 2010
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
public class ImageFilterExecute {
	int pixel;
	int index;
	int transparency;
	private Bitmap bitmap;

	private ImageFilter filter;

	public ImageFilterExecute(Bitmap bit, ImageFilter filter) {
		this.bitmap = bit;
		this.filter = filter;
	}

	/**
	 * 过滤图片为指定效果
	 * 
	 * @return
	 */
	public Bitmap doFilter() {
		int width = bitmap.getWidth(), height = bitmap.getHeight();
		int length = width * height;
		int pixels[] = new int[length];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				index = x + width * y;
				pixel = pixels[index];
				transparency = (pixel >> 24) & 0xFF;
				if (transparency > 1) {
					pixel = filter.filterRGB(x, y, pixel);
					pixels[index] = pixel;
				}
			}
		}
		Bitmap dst = Bitmap.createBitmap(pixels, width, height, bitmap
				.getConfig());
		pixels = null;
		return dst;
	}

}
