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
package loon.opengl;

import loon.BaseIO;
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.LTexture.Format;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.utils.BufferUtils;

public class TextureUtils {

	public static LTexture filterGray(String res) {
		return filterGray(res, Format.LINEAR);
	}

	public static LTexture filterGray(String res, Format cofing) {
		Image tmp = BaseIO.loadImage(res).cpy(true);
		LTexture texture = null;
		if (tmp != null) {
			tmp.setFormat(cofing);
			int[] pixels = BufferUtils.toGray(tmp.getPixels(), (int) tmp.width(), (int) tmp.height());
			tmp.setPixels(pixels, (int) tmp.width(), (int) tmp.height());
			texture = tmp.texture();
			tmp.close();
			tmp = null;
			pixels = null;
		}
		return texture;
	}

	public static LTexture filterColor(String res, LColor col) {
		return TextureUtils.filterColor(res, col, -1, 0f, false, Format.DEFAULT);
	}

	public static LTexture filterColor(String res, LColor col, int newColor, float vague) {
		return TextureUtils.filterColor(res, col, newColor, vague, false, Format.DEFAULT);
	}

	public static LTexture filterColor(String res, LColor col, int newColor, float vague, boolean alpha,
			Format format) {
		Image tmp = BaseIO.loadImage(res).cpy(true);
		int[] pixels = null;
		if (newColor == -1 && vague == 0f) {
			pixels = BufferUtils.toColorKey(tmp.getPixels(), col.getRGB());
		} else {
			pixels = BufferUtils.toColorKey(tmp.getPixels(), col.getRGB(), newColor, vague, alpha);
		}
		tmp.setFormat(format);
		tmp.setPixels(pixels, (int) tmp.width(), (int) tmp.height());
		LTexture texture = tmp.texture();
		pixels = null;
		return texture;
	}

	public static LTexture filterColor(String res, int[] colors) {
		return TextureUtils.filterColor(res, colors, -1, 0f, false, Format.DEFAULT);
	}

	public static LTexture filterColor(String res, int[] colors, int newColor, float vague) {
		return TextureUtils.filterColor(res, colors, newColor, vague, false, Format.DEFAULT);
	}

	public static LTexture filterColor(String res, int[] colors, int newColor, float vague, boolean alpha,
			Format format) {
		Image tmp = BaseIO.loadImage(res).cpy(true);
		int[] pixels = null;
		if (newColor == -1 && vague == 0f) {
			pixels = BufferUtils.toColorKeys(tmp.getPixels(), colors);
		} else {
			pixels = BufferUtils.toColorKeys(tmp.getPixels(), colors, newColor, vague, alpha);
		}
		tmp.setFormat(format);
		tmp.setPixels(pixels, (int) tmp.width(), (int) tmp.height());
		LTexture texture = tmp.texture();
		pixels = null;
		return texture;
	}

	public static LTexture filterLimitColor(String res, LColor start, LColor end) {
		return TextureUtils.filterLimitColor(res, start, end, Format.DEFAULT);
	}

	public static LTexture filterLimitColor(String res, LColor start, LColor end, Format format) {
		Image tmp = BaseIO.loadImage(res).cpy(true);
		int[] pixels = BufferUtils.toColorKeyLimit(tmp.getPixels(), start.getRGB(), end.getRGB());
		tmp.setFormat(format);
		tmp.setPixels(pixels, (int) tmp.width(), (int) tmp.height());
		LTexture texture = tmp.texture();
		pixels = null;
		return texture;
	}

	public static LTexture[] getSplitTextures(String fileName, int tileWidth, int tileHeight) {
		return getSplitTextures(LSystem.loadTexture(fileName), tileWidth, tileHeight);
	}

	public static LTexture[] getSplitTextures(LTexture image, int tileWidth, int tileHeight) {
		if (image == null) {
			return null;
		}
		if (tileWidth == 0 || tileHeight == 0 || (tileWidth == image.getWidth() && tileHeight == image.getHeight())) {
			return new LTexture[] { image };
		}
		int frame = 0;
		int wlength = (int) image.width() / tileWidth;
		int hlength = (int) image.height() / tileHeight;
		int total = wlength * hlength;

		LTexture[] images = new LTexture[total];
		for (int y = 0; y < hlength; y++) {
			for (int x = 0; x < wlength; x++) {
				images[frame] = image.copy((x * tileWidth), (y * tileHeight), tileWidth, tileHeight);
				frame++;
			}
		}
		return images;
	}

	public static LTexture[][] getSplit2Textures(String fileName, int tileWidth, int tileHeight) {
		return getSplit2Textures(LSystem.loadTexture(fileName), tileWidth, tileHeight);
	}

	public static LTexture[][] getSplit2Textures(LTexture image, int tileWidth, int tileHeight) {
		if (image == null) {
			return null;
		}
		int wlength = (int) (image.width() / tileWidth);
		int hlength = (int) (image.height() / tileHeight);
		LTexture[][] textures = new LTexture[wlength][hlength];
		for (int y = 0; y < hlength; y++) {
			for (int x = 0; x < wlength; x++) {
				textures[x][y] = image.copy((x * tileWidth), (y * tileHeight), tileWidth, tileHeight);
			}
		}
		return textures;
	}

	/**
	 * 0.3.2版起新增的分割图片方法，与上述近似作用的Split函数不同的是，可以指定个别图块大小。
	 * 
	 * @param fileName
	 * @param count
	 * @param width
	 * @param height
	 * @return
	 */
	public static LTexture[] getDivide(String fileName, int count, int[] width, int[] height) {
		if (count <= 0) {
			throw new LSysException("count <= 0 !");
		}
		LTexture image = LSystem.loadTexture(fileName);
		if (image == null) {
			return null;
		}
		if (width == null) {
			width = new int[count];
			int w = (int) image.width();
			for (int j = 0; j < count; j++) {
				width[j] = w / count;
			}
		}
		if (height == null) {
			height = new int[count];
			int h = (int) image.height();
			for (int i = 0; i < count; i++) {
				height[i] = h;
			}
		}
		LTexture[] images = new LTexture[count];
		int offsetX = 0;
		for (int i = 0; i < count; i++) {
			images[i] = image.copy(offsetX, 0, width[i], height[i]);
			offsetX += width[i];
		}
		return images;
	}

	/**
	 * 0.3.2版起新增的分割图片方法，成比例切分图片为指定数量
	 * 
	 * @param fileName
	 * @param count
	 * @return
	 */
	public static LTexture[] getDivide(String fileName, int count) {
		return getDivide(fileName, count, null, null);
	}

	/**
	 * 创建一张指定色彩的纹理
	 * 
	 * @param width
	 * @param height
	 * @param c
	 * @return
	 */
	public static LTexture createTexture(int width, int height, LColor c) {
		Canvas canvas = LSystem.base().graphics().createCanvas(width, height);
		canvas.setColor(c);
		canvas.fillRect(0, 0, width, height);
		canvas.close();
		LTexture tex2d = canvas.toTexture();
		if (canvas.image != null) {
			canvas.image.close();
		}
		return tex2d;
	}

}
