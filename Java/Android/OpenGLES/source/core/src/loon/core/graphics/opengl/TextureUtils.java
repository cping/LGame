package loon.core.graphics.opengl;

import loon.core.LSystem;
import loon.core.graphics.LColor;
import loon.core.graphics.LImage;
import loon.core.graphics.device.LGraphics;
import loon.core.graphics.opengl.LTexture.Format;

/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
public class TextureUtils {

	public static LTexture filterColor(String res, LColor height) {
		return TextureUtils.filterColor(res, height, Format.DEFAULT);
	}

	public static LTexture filterColor(String res, LColor height, Format format) {
		int color = height.getRGB();
		LImage tmp = new LImage(res);
		LImage image = new LImage(tmp.getWidth(), tmp.getHeight(), true);
		LGraphics g = image.getLGraphics();
		g.drawImage(tmp, 0, 0);
		g.dispose();
		if (tmp != null) {
			tmp.dispose();
			tmp = null;
		}
		int[] pixels = image.getPixels();
		int size = pixels.length;
		for (int i = 0; i < size; i++) {
			if (pixels[i] == color) {
				pixels[i] = 0xffffff;
			}
		}
		image.setFormat(format);
		image.setPixels(pixels, image.getWidth(), image.getHeight());
		LTexture texture = image.getTexture();
		if (image != null) {
			image.dispose();
			image = null;
		}
		return texture;
	}

	public static LTexture filterLimitColor(String res, LColor start, LColor end) {
		return TextureUtils.filterLimitColor(res, start, end, Format.DEFAULT);
	}

	public static LTexture filterLimitColor(String res, LColor start,
			LColor end, Format format) {
		int sred = start.getRed();
		int sgreen = start.getGreen();
		int sblue = start.getBlue();
		int ered = end.getRed();
		int egreen = end.getGreen();
		int eblue = end.getBlue();
		LImage tmp = new LImage(res);
		LImage image = new LImage(tmp.getWidth(), tmp.getHeight(), true);
		LGraphics g = image.getLGraphics();
		g.drawImage(tmp, 0, 0);
		g.dispose();
		if (tmp != null) {
			tmp.dispose();
			tmp = null;
		}
		int[] pixels = image.getPixels();
		int size = pixels.length;
		for (int i = 0; i < size; i++) {
			int[] rgbs = LColor.getRGBs(pixels[i]);
			if ((rgbs[0] >= sred && rgbs[1] >= sgreen && rgbs[2] >= sblue)
					&& (rgbs[0] <= ered && rgbs[1] <= egreen && rgbs[2] <= eblue)) {
				pixels[i] = 0xffffff;
			}
		}
		image.setFormat(format);
		image.setPixels(pixels, image.getWidth(), image.getHeight());
		LTexture texture = image.getTexture();
		if (image != null) {
			image.dispose();
			image = null;
		}
		return texture;
	}

	public static LTexture filterColor(String res, int[] colors) {
		return TextureUtils.filterColor(res, colors, Format.DEFAULT);
	}

	public static LTexture filterColor(String res, int[] colors, Format format) {
		LImage tmp = new LImage(res);
		LImage image = new LImage(tmp.getWidth(), tmp.getHeight(), true);
		LGraphics g = image.getLGraphics();
		g.drawImage(tmp, 0, 0);
		g.dispose();
		if (tmp != null) {
			tmp.dispose();
			tmp = null;
		}
		int[] pixels = image.getPixels();
		int size = pixels.length;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < colors.length; j++) {
				if (pixels[i] == colors[j]) {
					pixels[i] = 0xffffff;
				}
			}
		}
		image.setFormat(format);
		image.setPixels(pixels, image.getWidth(), image.getHeight());
		LTexture texture = image.getTexture();
		if (image != null) {
			image.dispose();
			image = null;
		}
		return texture;
	}

	public static LTexture loadTexture(String fileName) {
		return LTextures.loadTexture(fileName);
	}

	public static LTexture[] getSplitTextures(String fileName, int width,
			int height) {
		return getSplitTextures(LTextures.loadTexture(fileName), width, height);
	}

	public static LTexture[] getSplitTextures(LTexture image, int width,
			int height) {
		if (image == null) {
			return null;
		}

		if (LSystem.isThreadDrawing()) {
			image.loadTexture();
		}

		int frame = 0;
		int wlength = image.getWidth() / width;
		int hlength = image.getHeight() / height;
		int total = wlength * hlength;
		LTexture[] images = new LTexture[total];
		for (int y = 0; y < hlength; y++) {
			for (int x = 0; x < wlength; x++) {
				images[frame] = image.getSubTexture((x * width), (y * height),
						width, height);
				frame++;
			}
		}
		return images;
	}

	public static LTexture[][] getSplit2Textures(String fileName, int width,
			int height) {
		return getSplit2Textures(LTextures.loadTexture(fileName), width, height);
	}

	public static LTexture[][] getSplit2Textures(LTexture image, int width,
			int height) {
		if (image == null) {
			return null;
		}

		if (LSystem.isThreadDrawing()) {
			image.loadTexture();
		}

		int wlength = image.getWidth() / width;
		int hlength = image.getHeight() / height;
		LTexture[][] textures = new LTexture[wlength][hlength];
		for (int y = 0; y < hlength; y++) {
			for (int x = 0; x < wlength; x++) {
				textures[x][y] = image.getSubTexture((x * width), (y * height),
						width, height);
			}
		}
		return textures;
	}

	/**
	 * 0.3.2版起新增的分割图片方法，与上述近似作用的Split函数不同的是，可以指定个别图块大小。
	 * 
	 * @param fileName
	 * @param division
	 * @param width
	 * @param height
	 * @return
	 */
	public static LTexture[] getDivide(String fileName, int count, int[] width,
			int[] height) {
		if (count <= 0) {
			throw new IllegalArgumentException();
		}
		LTexture image = LTextures.loadTexture(fileName);
		if (image == null) {
			return null;
		}

		if (LSystem.isThreadDrawing()) {
			image.loadTexture();
		}

		if (width == null) {
			width = new int[count];
			int w = image.getWidth();
			for (int j = 0; j < count; j++) {
				width[j] = w / count;
			}
		}
		if (height == null) {
			height = new int[count];
			int h = image.getHeight();
			for (int i = 0; i < count; i++) {
				height[i] = h;
			}
		}
		LTexture[] images = new LTexture[count];
		int offsetX = 0;
		for (int i = 0; i < count; i++) {
			images[i] = image.getSubTexture(offsetX, 0, width[i], height[i]);
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
		LImage image = new LImage(width, height, false);
		LGraphics g = image.getLGraphics();
		g.setColor(c);
		g.fillRect(0, 0, width, height);
		g.dispose();
		LTexture tex2d = image.getTexture();
		if (image != null) {
			image.dispose();
			image = null;
		}
		return tex2d;
	}

}
