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
public final class LShadow {

	private int _shadowSize;

	private float _shadowAlpha;

	private LColor _shadowColor;

	private LTexture _texture;

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
		this._shadowSize = shadowSize;
		this._shadowAlpha = 0.5f;
		this._shadowColor = c;
		Image tmp = this.makeShadow(image);
		this._texture = tmp.texture();
		if (tmp != null) {
			tmp.close();
			tmp = null;
		}
	}

	private Image makeShadow(final Image image) {
		final int dstWidth = image.getWidth();
		final int dstHeight = image.getHeight();

		final int left = (_shadowSize - 1) >> 1;
		final int right = _shadowSize - left;
		final int xStart = left;
		final int xStop = dstWidth - right;
		final int yStart = left;
		final int yStop = dstHeight - right;

		final int shadowRgb = _shadowColor.getRGB() & 0x00FFFFFF;

		final int[] aHistory = new int[_shadowSize];
		int historyIdx = 0;

		int aSum;

		int[] dataBuffer = image.getPixels();
		final int lastPixelOffset = right * dstWidth;
		final float sumDivider = _shadowAlpha / _shadowSize;

		for (int y = 0, bufferOffset = 0; y < dstHeight; y++, bufferOffset = y * dstWidth) {
			aSum = 0;
			historyIdx = 0;
			for (int x = 0; x < _shadowSize; x++, bufferOffset++) {
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

				if (++historyIdx >= _shadowSize) {
					historyIdx -= _shadowSize;
				}
			}
		}

		for (int x = 0, bufferOffset = 0; x < dstWidth; x++, bufferOffset = x) {
			aSum = 0;
			historyIdx = 0;
			for (int y = 0; y < _shadowSize; y++, bufferOffset += dstWidth) {
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

				if (++historyIdx >= _shadowSize) {
					historyIdx -= _shadowSize;
				}
			}
		}
		final Image dst = Image.createImage(image.getWidth(), image.getHeight());
		dst.setPixels(dataBuffer, image.getWidth(), image.getHeight());
		dataBuffer = null;
		return dst;
	}

	public int getSize() {
		return _shadowSize;
	}

	public float getAlpha() {
		return _shadowAlpha;
	}

	public LColor getColor() {
		return _shadowColor;
	}

	public LTexture getTexture() {
		return _texture;
	}

}
