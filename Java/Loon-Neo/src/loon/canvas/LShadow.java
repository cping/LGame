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
package loon.canvas;

import loon.LTexture;

/**
 * 一个图片阴影效果实现
 */
public class LShadow {

	private int shadowSize;

	private float shadowAlpha;

	private LColor shadowColor;

	private LTexture texture;

	public LShadow(String file, LColor c) {
		this(Image.createImage(file), 5, 0.5f, c);
	}

	public LShadow(String file, int shadowSize, float a, LColor c) {
		this(Image.createImage(file), shadowSize, a, c);
	}

	public LShadow(String file) {
		this(Image.createImage(file), 5, 0.5f, LColor.black);
	}

	public LShadow(LTexture tex) {
		this(tex.getImage(), 5, 0.5f, LColor.black);
	}

	public LShadow(Image image) {
		this(image, 5, 0.5f, LColor.black);
	}

	/**
	 * 引入指定图像，并以此生成阴影.
	 * 
	 * @param image      图像
	 * @param shadowSize 模糊程度(越高则图像越模糊)
	 * @param a          透明度
	 * @param c          希望阴影化区域显示的颜色
	 */
	public LShadow(Image image, int shadowSize, float a, LColor c) {
		this.shadowSize = shadowSize;
		this.shadowAlpha = 0.5f;
		this.shadowColor = c;
		Image tmp = this.makeShadow(image);
		this.texture = tmp.texture();
		if (tmp != null) {
			tmp.close();
			tmp = null;
		}
	}

	private Image makeShadow(final Image image) {
		int dstWidth = image.getWidth();
		int dstHeight = image.getHeight();

		int left = (shadowSize - 1) >> 1;
		int right = shadowSize - left;
		int xStart = left;
		int xStop = dstWidth - right;
		int yStart = left;
		int yStop = dstHeight - right;

		int shadowRgb = shadowColor.getRGB() & 0x00FFFFFF;

		int[] aHistory = new int[shadowSize];
		int historyIdx = 0;

		int aSum;

		int[] dataBuffer = image.getPixels();
		int lastPixelOffset = right * dstWidth;
		float sumDivider = shadowAlpha / shadowSize;

		for (int y = 0, bufferOffset = 0; y < dstHeight; y++, bufferOffset = y * dstWidth) {
			aSum = 0;
			historyIdx = 0;
			for (int x = 0; x < shadowSize; x++, bufferOffset++) {
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

				if (++historyIdx >= shadowSize) {
					historyIdx -= shadowSize;
				}
			}
		}

		for (int x = 0, bufferOffset = 0; x < dstWidth; x++, bufferOffset = x) {
			aSum = 0;
			historyIdx = 0;
			for (int y = 0; y < shadowSize; y++, bufferOffset += dstWidth) {
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

				if (++historyIdx >= shadowSize) {
					historyIdx -= shadowSize;
				}
			}
		}
		Image dst = Image.createImage(image.getWidth(), image.getHeight());
		dst.setPixels(dataBuffer, image.getWidth(), image.getHeight());
		dataBuffer = null;
		return dst;
	}

	public int getSize() {
		return shadowSize;
	}

	public float getAlpha() {
		return shadowAlpha;
	}

	public LColor getColor() {
		return shadowColor;
	}

	public LTexture getTexture() {
		return texture;
	}

}
