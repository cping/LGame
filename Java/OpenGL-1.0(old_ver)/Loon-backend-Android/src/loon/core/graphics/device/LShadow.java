/**
 * 
 * Copyright 2014
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
 * @version 0.4.1
 */
package loon.core.graphics.device;

import loon.core.graphics.opengl.LTexture;

public class LShadow {

	private int size;

	private float alpha;

	private LColor color;

	private LTexture texture;

	public LShadow(String file, LColor c) {
		this(LImage.createImage(file), 5, 0.5f, c);
	}

	public LShadow(String file, int size, float a, LColor c) {
		this(LImage.createImage(file), size, a, c);
	}

	public LShadow(String file) {
		this(LImage.createImage(file), 5, 0.5f, LColor.black);
	}

	public LShadow(LTexture tex) {
		this(tex.getImage(), 5, 0.5f, LColor.black);
	}

	public LShadow(LImage image) {
		this(image, 5, 0.5f, LColor.black);
	}

	/**
	 * 引入指定图像，并以此生成阴影.
	 * @param image 图像
	 * @param size 模糊程度(越高则图像越模糊)
	 * @param a 透明度
	 * @param c 希望阴影化区域显示的颜色
	 */
	public LShadow(LImage image, int size, float a, LColor c) {
		this.size = size;
		this.alpha = 0.5f;
		this.color = c;
		LImage tmp = this.makeShadow(image);
		this.texture = tmp.getTexture();
		if (tmp != null) {
			tmp.dispose();
			tmp = null;
		}
	}

	private LImage makeShadow(final LImage image) {
		int dstWidth = image.getWidth();
		int dstHeight = image.getHeight();

		int left = (size - 1) >> 1;
		int right = size - left;
		int xStart = left;
		int xStop = dstWidth - right;
		int yStart = left;
		int yStop = dstHeight - right;

		int shadowRgb = color.getRGB() & 0x00FFFFFF;

		int[] aHistory = new int[size];
		int historyIdx = 0;

		int aSum;

		int[] dataBuffer = image.getPixels();
		int lastPixelOffset = right * dstWidth;
		float sumDivider = alpha / size;

		for (int y = 0, bufferOffset = 0; y < dstHeight; y++, bufferOffset = y
				* dstWidth) {
			aSum = 0;
			historyIdx = 0;
			for (int x = 0; x < size; x++, bufferOffset++) {
				int a = dataBuffer[bufferOffset] >>> 24;
				aHistory[x] = a;
				aSum += a;
			}

			bufferOffset -= right;

			for (int x = xStart; x < xStop; x++, bufferOffset++) {
				int a = (int) (aSum * sumDivider);
				dataBuffer[bufferOffset] = a << 24 | shadowRgb;

				aSum -= aHistory[historyIdx];

				a = dataBuffer[bufferOffset + right] >>> 24;
				aHistory[historyIdx] = a;
				aSum += a;

				if (++historyIdx >= size) {
					historyIdx -= size;
				}
			}
		}

		for (int x = 0, bufferOffset = 0; x < dstWidth; x++, bufferOffset = x) {
			aSum = 0;
			historyIdx = 0;
			for (int y = 0; y < size; y++, bufferOffset += dstWidth) {
				int a = dataBuffer[bufferOffset] >>> 24;
				aHistory[y] = a;
				aSum += a;
			}

			bufferOffset -= lastPixelOffset;

			for (int y = yStart; y < yStop; y++, bufferOffset += dstWidth) {
				int a = (int) (aSum * sumDivider);
				dataBuffer[bufferOffset] = a << 24 | shadowRgb;

				aSum -= aHistory[historyIdx];

				a = dataBuffer[bufferOffset + lastPixelOffset] >>> 24;
				aHistory[historyIdx] = a;
				aSum += a;

				if (++historyIdx >= size) {
					historyIdx -= size;
				}
			}
		}
		LImage dst = new LImage(image.getWidth(), image.getHeight(), true);
		dst.setPixels(dataBuffer, image.getWidth(), image.getHeight());
		dataBuffer = null;
		return dst;
	}

	public int getSize() {
		return size;
	}

	public float getAlpha() {
		return alpha;
	}

	public LColor getColor() {
		return color;
	}

	public LTexture getTexture() {
		return texture;
	}

}
