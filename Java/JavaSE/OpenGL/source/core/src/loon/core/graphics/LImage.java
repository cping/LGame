package loon.core.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.ImageProducer;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.util.ArrayList;

import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.geom.Matrix.Transform2i;
import loon.core.graphics.device.LGraphics;
import loon.core.graphics.opengl.GLLoader;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.graphics.opengl.LTexture.Format;
import loon.core.resource.Resources;
import loon.jni.NativeSupport;
import loon.utils.CollectionUtils;
import loon.utils.GraphicsUtils;
import loon.utils.MathUtils;

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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1.1
 */
public class LImage implements LRelease {

	/**
	 * 0.3.3新增类，用以直接针对LImage进行像素操作。
	 */
	public static class Processor implements LRelease {

		private final LImage object;

		private int[] drawPixels = null;

		private int[] processedPixels = null;

		public final int objWidth, objHeight;

		private final int pixelsSize;

		private int pixelsParam = 0;

		public Processor(int w, int h) {
			this(w, h, false);
		}

		public Processor(int w, int h, boolean alpha) {
			this(new LImage(w, h, alpha));
		}

		public Processor(LImage image) {
			this.objWidth = image.getWidth();
			this.objHeight = image.getHeight();
			this.pixelsSize = objWidth * objHeight;
			this.object = image;
			this.drawPixels = image.getPixels();
		}

		public int[] getPixels() {
			return CollectionUtils.copyOf(drawPixels);
		}

		public int getColors() {
			if (objWidth < 1 || objHeight < 1) {
				return 0;
			}
			byte[] map = new byte[(256 * 256 * 256) / 8];
			int colors = 0;
			for (int i = 0; i < objHeight; i++) {
				for (int j = 0; j < objWidth; j++) {
					int rgb = get(j, i) & 0x00ffffff;
					int index = rgb / 8;
					int bit = 1 << (rgb % 8);
					if ((map[index] & bit) == 0) {
						map[index] |= bit;
						colors++;
					}
				}
			}
			return colors;
		}

		public void drawImage(LImage img, int x, int y) {
			if (img != null) {
				LGraphics g = object.getLGraphics();
				g.drawImage(img, x, y);
				g.dispose();
				savePixels();
			}
		}

		public void drawImage(LImage img, int x, int y, int w, int h) {
			if (img != null) {
				LGraphics g = object.getLGraphics();
				g.drawImage(img, x, y, w, h);
				g.dispose();
				savePixels();
			}
		}

		public void setAlpha(final float alpha) {
			setAlphaValue((int) (255 * alpha));
		}

		public void setAlphaValue(final int alpha) {
			if (alpha < 0 || alpha > 255) {
				return;
			}
			updatePixels();
			final int decrement = 255 - alpha;
			for (int start = 0; start < pixelsSize; start += objWidth) {
				final int end = start + objWidth;
				for (int index = start; index < end; ++index) {
					final int pixel = drawPixels[index];
					int a = LColor.getAlpha(pixel);
					a -= decrement;
					if (a < 0) {
						a = 0;
					}
					processedPixels[index] = (pixel & 0x00ffffff) | a << 24;
				}
			}
			object.setPixels(processedPixels, objWidth, objHeight);
		}

		public void colorFilter(LColor color) {
			this.colorFilter(color, true);
		}

		public void colorFilter(LColor color, boolean savePixels) {
			updatePixels();
			final int red = color.getRed();
			final int green = color.getGreen();
			final int blue = color.getBlue();
			final int decrementR = 255 - red;
			final int decrementG = 255 - green;
			final int decrementB = 255 - blue;
			for (int start = 0; start < pixelsSize; start += objWidth) {
				final int end = start + objWidth;
				for (int index = start; index < end; ++index) {
					final int pixel = drawPixels[index];
					int r = LColor.getRed(pixel);
					r -= decrementR;
					if (r < 0) {
						r = 0;
					}
					int g = LColor.getGreen(pixel);
					g -= decrementG;
					if (g < 0) {
						g = 0;
					}
					int b = LColor.getBlue(pixel);
					b -= decrementB;
					if (b < 0) {
						b = 0;
					}
					processedPixels[index] = (pixel & 0xff000000)
							| r << 0x00ff0000 | g << 0x0000ff00
							| b << 0x000000ff;
				}
			}
			object.setPixels(processedPixels, objWidth, objHeight);
			if (savePixels) {
				drawPixels = processedPixels;
			}
		}

		public void scroll(final float dx) {
			updatePixels();
			pixelsParam += MathUtils.fromFloat(dx);
			final int dpx = MathUtils.fromInt(pixelsParam);
			if (dpx > objWidth) {
				pixelsParam -= MathUtils.toInt(objWidth);
			}
			for (int y = 0; y < objHeight; ++y) {
				final int yOffset = y * objWidth;
				for (int x = 0; x < objWidth; ++x) {
					processedPixels[x + yOffset] = drawPixels[(x + objWidth + dpx)
							% objWidth + yOffset];
				}
			}
			object.setPixels(processedPixels, objWidth, objHeight);
		}

		public final void rotate(final float alpha) {
			rotate(alpha, objWidth / 2, objHeight / 2);
		}

		public final void rotate(final float alpha, final float x, final float y) {
			updatePixels();
			Transform2i transform = new Transform2i();
			transform.rotate(alpha, x, y);
			for (int j = 0; j < objHeight; ++j) {
				final int yOffset = j * objWidth;
				final int fpY = MathUtils.fromInt(j);
				final int constX = MathUtils.mul(fpY, transform.matrixs[0][1])
						+ transform.matrixs[0][2];
				final int constY = MathUtils.mul(fpY, transform.matrixs[1][1])
						+ transform.matrixs[1][2];
				for (int i = 0; i < objWidth; ++i) {
					final int fpX = MathUtils.fromInt(i);
					final int tx = MathUtils.toInt(MathUtils.mul(fpX,
							transform.matrixs[0][0]) + constX);
					if (tx < 0 || objWidth <= tx) {
						processedPixels[i + yOffset] = 0x00000000;
					} else {
						final int ty = MathUtils.toInt(MathUtils.mul(fpX,
								transform.matrixs[1][0]) + constY);
						if (ty < 0 || objHeight <= ty) {
							processedPixels[i + yOffset] = 0x00000000;
						} else {
							processedPixels[i + yOffset] = drawPixels[tx + ty
									* objWidth];
						}
					}
				}
			}
			object.setPixels(processedPixels, objWidth, objHeight);
		}

		public final void zoom(final float lambda) {
			zoom(lambda, objWidth / 2, objHeight / 2);
		}

		public final void zoom(final float lambda, final float x, final float y) {
			updatePixels();
			if (0 == lambda) {
				return;
			}
			final int fpLambda = MathUtils.fromFloat(lambda);
			final int x_c = MathUtils.fromFloat(x);
			final int y_c = MathUtils.fromFloat(y);
			final int transX = x_c - MathUtils.div(x_c, fpLambda);
			final int transY = y_c - MathUtils.div(y_c, fpLambda);
			for (int j = 0; j < objHeight; ++j) {
				final int yOffset = j * objWidth;
				final int fpY = MathUtils.fromInt(j);
				final int ty = MathUtils.toInt(MathUtils.div(fpY, fpLambda)
						+ transY);
				for (int i = 0; i < objWidth; ++i) {
					if (ty < 0 || objHeight <= ty) {
						processedPixels[i + yOffset] = 0x00000000;
					} else {
						final int fpX = MathUtils.fromInt(i);
						final int tx = MathUtils.toInt(MathUtils.div(fpX,
								fpLambda) + transX);
						if (tx < 0 || objWidth <= tx) {
							processedPixels[i + yOffset] = 0x00000000;
						} else {
							processedPixels[i + yOffset] = drawPixels[tx + ty
									* objWidth];
						}
					}
				}
			}
			object.setPixels(processedPixels, objWidth, objHeight);
		}

		public final void transform(final Transform2i t) {
			updatePixels();
			for (int y = 0; y < objHeight; ++y) {
				final int yOffset = y * objWidth;
				final int fpY = MathUtils.fromInt(y);
				final int constX = MathUtils.mul(fpY, t.matrixs[0][1])
						+ t.matrixs[0][2];
				final int constY = MathUtils.mul(fpY, t.matrixs[1][1])
						+ t.matrixs[1][2];
				for (int x = 0; x < objWidth; ++x) {
					final int fpX = MathUtils.fromInt(x);
					final int tx = MathUtils.toInt(MathUtils.mul(fpX,
							t.matrixs[0][0]) + constX);
					if (tx < 0 || objWidth <= tx) {
						processedPixels[x + yOffset] = 0x00000000;
					} else {
						final int ty = MathUtils.toInt(MathUtils.mul(fpX,
								t.matrixs[1][0]) + constY);
						if (ty < 0 || objHeight <= ty) {
							processedPixels[x + yOffset] = 0x00000000;
						} else {
							processedPixels[x + yOffset] = drawPixels[tx + ty
									* objWidth];
						}
					}
				}
			}
			object.setPixels(processedPixels, objWidth, objHeight);
		}

		public void convolve(int[][] kernels) {
			updatePixels();
			final int M = kernels.length;
			final int N = kernels[0].length;
			for (int i = 0; i < objHeight; ++i) {
				final int iOffset = i * objWidth;
				for (int j = 0; j < objWidth; ++j) {
					int fpA = 0;
					int fpR = 0;
					int fpG = 0;
					int fpB = 0;
					for (int k = 0; k < M; ++k) {
						final int y = i - 1 + k;
						if (0 <= y && y < objHeight) {
							final int yOffset = y * objWidth;
							for (int l = 0; l < N; ++l) {
								final int x = j - 1 + l;
								if (0 <= x && x < objWidth) {
									final int color = drawPixels[x + yOffset];
									final int fp = kernels[k][l];
									fpA += MathUtils.mul(MathUtils
											.fromInt(LColor.getAlpha(color)),
											fp);
									fpR += MathUtils.mul(MathUtils
											.fromInt(LColor.getRed(color)), fp);
									fpG += MathUtils.mul(MathUtils
											.fromInt(LColor.getGreen(color)),
											fp);
									fpB += MathUtils
											.mul(MathUtils.fromInt(LColor
													.getBlue(color)), fp);
								}
							}
						}
					}
					processedPixels[j + iOffset] = LColor.getARGB(
							safeComponent(MathUtils.toInt(fpR)),
							safeComponent(MathUtils.toInt(fpG)),
							safeComponent(MathUtils.toInt(fpB)),
							safeComponent(MathUtils.toInt(fpA)));
				}
			}
			object.setPixels(processedPixels, objWidth, objHeight);
			drawPixels = processedPixels;
		}

		public int safeComponent(int component) {
			return (int) MathUtils.min(MathUtils.max(0, component), 255);
		}

		public void wave(final float f, final float t) {
			updatePixels();
			final float cos = MathUtils.cos(2 * MathUtils.PI * f * t);
			final int dx = (int) (cos * 10);
			for (int y = 0; y < objHeight; ++y) {
				final int yOffset = y * objWidth;
				for (int x = 0; x < objWidth; ++x) {
					if (y % 2 == 0) {
						processedPixels[x + yOffset] = drawPixels[(x + objWidth + dx)
								% objWidth + yOffset];
					} else {
						processedPixels[x + yOffset] = drawPixels[(x + objWidth - dx)
								% objWidth + yOffset];
					}
				}
			}
			object.setPixels(processedPixels, objWidth, objHeight);
		}

		public final void mul(LColor mulColor) {
			mul(mulColor.getAlpha(), mulColor.getRed(), mulColor.getGreen(),
					mulColor.getBlue());
		}

		public final void mul(int red, int green, int blue) {
			mul(255, red, green, blue);
		}

		public final void mul(int alpha, int red, int green, int blue) {
			updatePixels();
			final float a = alpha / 255f;
			final float r = red / 255f;
			final float g = green / 255f;
			final float b = blue / 255f;
			for (int j = 0; j < objHeight; ++j) {
				final int yOffset = j * objWidth;
				final int end = yOffset + objWidth;
				for (int i = yOffset; i < end; ++i) {
					final int pixel = drawPixels[i];
					processedPixels[i] = LColor.getARGB(
							(int) (LColor.getRed(pixel) * r),
							(int) (LColor.getGreen(pixel) * g),
							(int) (LColor.getBlue(pixel) * b),
							(int) (LColor.getAlpha(pixel) * a));
				}
			}
			object.setPixels(processedPixels, objWidth, objHeight);
			drawPixels = processedPixels;
		}

		public final void invert() {
			updatePixels();
			for (int j = 0; j < objHeight; ++j) {
				final int yOffset = j * objWidth;
				final int end = yOffset + objWidth;
				for (int i = yOffset; i < end; ++i) {
					final int color = drawPixels[i];
					processedPixels[i] = LColor
							.getARGB(255 - LColor.getRed(color),
									255 - LColor.getGreen(color),
									255 - LColor.getBlue(color),
									LColor.getAlpha(color));
				}
			}
			object.setPixels(processedPixels, objWidth, objHeight);
			drawPixels = processedPixels;
		}

		protected final void set(int x, int y, int pixel) {
			drawPixels[x + y * objWidth] = pixel;
		}

		protected final int get(int x, int y) {
			return drawPixels[x + y * objWidth];
		}

		public final void transparency() {
			this.updatePixels();
			int transparentColor = get(1, 0);
			if (0x00000000 != transparentColor) {
				for (int x = 0; x < objWidth; ++x) {
					for (int y = 0; y < objHeight; ++y) {
						if (get(x, y) == transparentColor) {
							set(x, y, 0x00000000);
						}
					}
				}
			}
		}

		public static final int[][] blurKernel() {
			final float centerValue = 0.2f;
			final float edgeValue = (1 - centerValue) / 8;
			final int c = MathUtils.fromFloat(centerValue);
			final int e = MathUtils.fromFloat(edgeValue);
			int[][] kernels = { { e, e, e }, { e, c, e }, { e, e, e } };
			return kernels;
		}

		public static final int[][] gaussianBlurKernel() {
			final float sum = 16;
			final float centerValue = 4f / sum;
			final float edgeValue = 1f / sum;
			final float midValues = 2f / sum;
			final int c = MathUtils.fromFloat(centerValue);
			final int e = MathUtils.fromFloat(edgeValue);
			final int m = MathUtils.fromFloat(midValues);
			int[][] kernels = { { e, m, e }, { m, c, m }, { e, m, e } };
			return kernels;
		}

		public static final int[][] edgeKernel() {
			final float centerValue = 1;
			final float edgeValue = -1 / 8;
			final int c = MathUtils.fromFloat(centerValue);
			final int e = MathUtils.fromFloat(edgeValue);
			int[][] kernels = { { e, e, e }, { e, c, e }, { e, e, e } };
			return kernels;
		}

		public static final int[][] embossKernel() {
			final float centerValue = 0.5f;
			final float edgeValue = 1 - centerValue;
			final int c = MathUtils.fromFloat(centerValue);
			final int e = MathUtils.fromFloat(edgeValue);
			int[][] kernels = { { 0, 0, 0 }, { 0, c, 0 }, { 0, 0, e } };
			return kernels;
		}

		public static final int[][] sharpenKernel() {
			final float centerValue = 5;
			final float edgeValue = -1;
			final int c = MathUtils.fromFloat(centerValue);
			final int e = MathUtils.fromFloat(edgeValue);
			int[][] kernels = { { 0, e, 0 }, { e, c, e }, { 0, e, 0 } };
			return kernels;
		}

		private final int colorLerp(final int fpX, final int color1,
				final int color2) {
			final int fpY = MathUtils.ONE_FIXED - fpX;
			final int a = MathUtils.toInt(MathUtils.mul(
					MathUtils.fromInt(LColor.getAlpha(color1)), fpY)
					+ MathUtils.mul(MathUtils.fromInt(LColor.getAlpha(color2)),
							fpX));
			final int r = MathUtils.toInt(MathUtils.mul(
					MathUtils.fromInt(LColor.getRed(color1)), fpY)
					+ MathUtils.mul(MathUtils.fromInt(LColor.getRed(color2)),
							fpX));
			final int g = MathUtils.toInt(MathUtils.mul(
					MathUtils.fromInt(LColor.getGreen(color1)), fpY)
					+ MathUtils.mul(MathUtils.fromInt(LColor.getGreen(color2)),
							fpX));
			final int b = MathUtils.toInt(MathUtils.mul(
					MathUtils.fromInt(LColor.getBlue(color1)), fpY)
					+ MathUtils.mul(MathUtils.fromInt(LColor.getBlue(color2)),
							fpX));
			return LColor.getARGB(r, g, b, a);
		}

		public final void fourCornersGradient(final int topLeftColor,
				final int topRightColor, final int bottomRightColor,
				final int bottomLeftColor) {
			updatePixels();
			final int fpH = MathUtils.fromInt(objHeight);
			final int fpW = MathUtils.fromInt(objWidth);
			for (int y = 0; y < objHeight; ++y) {
				final int yOffset = y * objWidth;
				final int fpYRatio = MathUtils.div(MathUtils.fromInt(y), fpH);
				final int leftColor = colorLerp(fpYRatio, topLeftColor,
						bottomLeftColor);
				final int rightColor = colorLerp(fpYRatio, topRightColor,
						bottomRightColor);
				for (int x = 0; x < objWidth; ++x) {
					final int fpXRatio = MathUtils.div(MathUtils.fromInt(x),
							fpW);
					processedPixels[x + yOffset] = colorLerp(fpXRatio,
							leftColor, rightColor);
				}
			}
			object.setPixels(processedPixels, objWidth, objHeight);
			drawPixels = processedPixels;
		}

		public void interleave(Processor imgProcessor, final int[][] mask) {
			final int m = mask.length, n = mask[0].length;
			int i = 0, j = 0;
			for (int y = 0; y < objHeight; ++y) {
				final int yOffset = y * objWidth;
				for (int x = 0; x < objWidth; ++x) {
					final int offset = x + yOffset;
					processedPixels[offset] = (mask[i][j] == 1) ? drawPixels[offset]
							: imgProcessor.drawPixels[offset];
					if (++j >= n) {
						j = 0;
					}
				}
				if (++i >= m) {
					i = 0;
				}
			}
			object.setPixels(processedPixels, objWidth, objHeight);
			drawPixels = processedPixels;
		}

		public void applyGradient() {
			updatePixels();
			final int transparentColor = get(1, 0);
			for (int y = 0; y < objHeight; ++y) {
				final int yOffset = y * objWidth;
				final int mulColor = get(0, y);
				if (mulColor != transparentColor) {
					final float a = LColor.getAlpha(mulColor) / 255f;
					final float r = LColor.getRed(mulColor) / 255f;
					final float g = LColor.getGreen(mulColor) / 255f;
					final float b = LColor.getBlue(mulColor) / 255f;
					final int end = yOffset + objWidth;
					for (int j = yOffset; j < end; ++j) {
						final int pixel = drawPixels[j];
						if (pixel != transparentColor)
							drawPixels[j] = LColor.getARGB((int) MathUtils.min(
									255, (LColor.getRed(pixel) * r)),
									(int) MathUtils.min(255,
											(LColor.getGreen(pixel) * g)),
									(int) MathUtils.min(
											(LColor.getBlue(pixel) * b), 255),
									(int) MathUtils.min(255,
											(LColor.getAlpha(pixel) * a)));
					}
				}
			}
			object.setPixels(drawPixels, objWidth, objHeight);
		}

		public void savePixels() {
			this.drawPixels = object.getPixels();
		}

		private final void updatePixels() {
			if (drawPixels == null) {
				savePixels();
			}
			if (processedPixels == null || processedPixels == drawPixels) {
				processedPixels = new int[pixelsSize];
			}
		}

		public LImage getImage() {
			return object;
		}

		public LTexture getTexture() {
			if (object != null) {
				return object.getTexture();
			}
			return null;
		}

		public int getWidth() {
			return objWidth;
		}

		public int getHeight() {
			return objHeight;
		}

		public void dispose() {
			if (object != null) {
				object.dispose();
			}
			this.drawPixels = null;
			this.processedPixels = null;
		}

	}

	/**
	 * 0.3.3版新增类，用以处理TGA格式图像（仅支持24&32位图）
	 */
	public static class LFormatTGA {

		private static final int TGA_HEADER_SIZE = 18;

		private static final int TGA_HEADER_INVALID = 0;

		private static final int TGA_HEADER_UNCOMPRESSED = 1;

		private static final int TGA_HEADER_COMPRESSED = 2;

		public static class State implements LRelease {

			public int type;

			public int pixelDepth;

			public int width;

			public int height;

			public int[] pixels;

			public void dispose() {
				if (pixels != null) {
					pixels = null;
				}
			}

		}

		public static State inJustDecode(String res) throws IOException {
			return inJustDecode(Resources.openResource(res));
		}

		public static State inJustDecode(InputStream in) throws IOException {
			return loadHeader(in, new State());
		}

		private static State loadHeader(InputStream in, State info)
				throws IOException {

			in.read();
			in.read();

			info.type = (byte) in.read();

			in.read();
			in.read();
			in.read();
			in.read();
			in.read();
			in.read();
			in.read();
			in.read();
			in.read();

			info.width = (in.read() & 0xff) | ((in.read() & 0xff) << 8);
			info.height = (in.read() & 0xff) | ((in.read() & 0xff) << 8);

			info.pixelDepth = in.read() & 0xff;

			return info;
		}

		private static final short getUnsignedByte(byte[] bytes, int byteIndex) {
			return (short) (bytes[byteIndex] & 0xFF);
		}

		private static final int getUnsignedShort(byte[] bytes, int byteIndex) {
			return (getUnsignedByte(bytes, byteIndex + 1) << 8)
					+ getUnsignedByte(bytes, byteIndex + 0);
		}

		private static void readBuffer(InputStream in, byte[] buffer)
				throws IOException {
			int bytesRead = 0;
			int bytesToRead = buffer.length;
			for (; bytesToRead > 0;) {
				int read = in.read(buffer, bytesRead, bytesToRead);
				bytesRead += read;
				bytesToRead -= read;
			}
		}

		private static final void skipBytes(InputStream in, long toSkip)
				throws IOException {
			for (; toSkip > 0L;) {
				long skipped = in.skip(toSkip);
				if (skipped > 0) {
					toSkip -= skipped;
				} else if (skipped < 0) {
					toSkip = 0;
				}
			}
		}

		private static final int compareFormatHeader(InputStream in,
				byte[] header) throws IOException {

			readBuffer(in, header);
			boolean hasPalette = false;
			int result = TGA_HEADER_INVALID;

			int imgIDSize = getUnsignedByte(header, 0);

			if ((header[1] != (byte) 0) && (header[1] != (byte) 1)) {
				return TGA_HEADER_INVALID;
			}

			switch (getUnsignedByte(header, 2)) {
			case 0:
				result = TGA_HEADER_UNCOMPRESSED;
				break;
			case 1:
				hasPalette = true;
				result = TGA_HEADER_UNCOMPRESSED;
				throw new RuntimeException(
						"Indexed State is not yet supported !");
			case 2:
				result = TGA_HEADER_UNCOMPRESSED;
				break;
			case 3:
				result = TGA_HEADER_UNCOMPRESSED;
				break;
			case 9:
				hasPalette = true;
				result = TGA_HEADER_COMPRESSED;
				throw new RuntimeException(
						"Indexed State is not yet supported !");
			case 10:
				result = TGA_HEADER_COMPRESSED;
				break;
			case 11:
				result = TGA_HEADER_COMPRESSED;
				break;
			default:
				return TGA_HEADER_INVALID;
			}
			if (!hasPalette) {
				if (getUnsignedShort(header, 3) != 0) {
					return TGA_HEADER_INVALID;
				}
			}
			if (!hasPalette) {
				if (getUnsignedShort(header, 5) != 0) {
					return TGA_HEADER_INVALID;
				}
			}

			short paletteEntrySize = getUnsignedByte(header, 7);
			if (!hasPalette) {
				if (paletteEntrySize != 0) {
					return TGA_HEADER_INVALID;
				}
			} else {
				if ((paletteEntrySize != 15) && (paletteEntrySize != 16)
						&& (paletteEntrySize != 24) && (paletteEntrySize != 32)) {
					return TGA_HEADER_INVALID;
				}
			}

			if (getUnsignedShort(header, 8) != 0) {
				return TGA_HEADER_INVALID;
			}

			if (getUnsignedShort(header, 10) != 0) {
				return TGA_HEADER_INVALID;
			}

			switch (getUnsignedByte(header, 16)) {
			case 1:
			case 8:
			case 15:
			case 16:
				throw new RuntimeException(
						"this State with non RGB or RGBA pixels are not yet supported.");
			case 24:
			case 32:
				break;
			default:
				return TGA_HEADER_INVALID;
			}

			if (imgIDSize != 0) {
				skipBytes(in, imgIDSize);
			}

			return result;
		}

		private static final void writePixel(int[] pixels, final byte red,
				final byte green, final byte blue, final byte alpha,
				final boolean hasAlpha, final int offset) {
			int pixel;
			if (hasAlpha) {
				pixel = (red & 0xff);
				pixel |= ((green & 0xff) << 8);
				pixel |= ((blue & 0xff) << 16);
				pixel |= ((alpha & 0xff) << 24);
				pixels[offset / 4] = pixel;
			} else {
				pixel = (red & 0xff);
				pixel |= ((green & 0xff) << 8);
				pixel |= ((blue & 0xff) << 16);
				pixels[offset / 4] = pixel;
			}
		}

		private static int[] readBuffer(InputStream in, int width, int height,
				int srcBytesPerPixel, boolean acceptAlpha,
				boolean flipVertically) throws IOException {

			int[] pixels = new int[width * height];
			byte[] buffer = new byte[srcBytesPerPixel];

			final boolean copyAlpha = (srcBytesPerPixel == 4) && acceptAlpha;
			final int dstBytesPerPixel = acceptAlpha ? srcBytesPerPixel : 3;
			final int trgLineSize = width * dstBytesPerPixel;

			int dstByteOffset = 0;

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int read = in.read(buffer, 0, srcBytesPerPixel);

					if (read < srcBytesPerPixel) {
						return pixels;
					}
					int actualByteOffset = dstByteOffset;
					if (!flipVertically) {
						actualByteOffset = ((height - y - 1) * trgLineSize)
								+ (x * dstBytesPerPixel);
					}

					if (copyAlpha) {
						writePixel(pixels, buffer[2], buffer[1], buffer[0],
								buffer[3], true, actualByteOffset);
					} else {
						writePixel(pixels, buffer[2], buffer[1], buffer[0],
								(byte) 0, false, actualByteOffset);
					}

					dstByteOffset += dstBytesPerPixel;
				}
			}
			return pixels;
		}

		private static void loadUncompressed(byte[] header, State tga,
				InputStream in, boolean acceptAlpha, boolean flipVertically)
				throws IOException {

			// 图像宽
			int orgWidth = getUnsignedShort(header, 12);

			// 图像高
			int orgHeight = getUnsignedShort(header, 14);

			// 图像位图(24&32)
			int pixelDepth = getUnsignedByte(header, 16);

			tga.width = orgWidth;
			tga.height = orgHeight;
			tga.pixelDepth = pixelDepth;

			boolean isOriginBottom = (header[17] & 0x20) == 0;

			if (!isOriginBottom) {
				flipVertically = !flipVertically;
			}

			// 不支持的格式
			if ((orgWidth <= 0) || (orgHeight <= 0)
					|| ((pixelDepth != 24) && (pixelDepth != 32))) {
				throw new IOException("Invalid texture information !");
			}

			int bytesPerPixel = (pixelDepth / 8);

			// 获取图像数据并转为int[]
			tga.pixels = readBuffer(in, orgWidth, orgHeight, bytesPerPixel,
					acceptAlpha, flipVertically);
			// 图像色彩模式
			tga.type = (acceptAlpha && (bytesPerPixel == 4) ? 4 : 3);
		}

		private static void loadCompressed(byte[] header, State tga,
				InputStream in, boolean acceptAlpha, boolean flipVertically)
				throws IOException {

			int orgWidth = getUnsignedShort(header, 12);
			int orgHeight = getUnsignedShort(header, 14);
			int pixelDepth = getUnsignedByte(header, 16);

			tga.width = orgWidth;
			tga.height = orgHeight;
			tga.pixelDepth = pixelDepth;

			boolean isOriginBottom = (header[17] & 0x20) == 0;

			if (!isOriginBottom) {
				flipVertically = !flipVertically;
			}

			if ((orgWidth <= 0) || (orgHeight <= 0)
					|| ((pixelDepth != 24) && (pixelDepth != 32))) {
				throw new IOException("Invalid texture information !");
			}

			int bytesPerPixel = (pixelDepth / 8);
			int pixelCount = orgHeight * orgWidth;
			int currentPixel = 0;

			byte[] colorBuffer = new byte[bytesPerPixel];

			int width = orgWidth;
			int height = orgHeight;

			final int dstBytesPerPixel = (acceptAlpha && (bytesPerPixel == 4) ? 4
					: 3);
			final int trgLineSize = orgWidth * dstBytesPerPixel;

			int[] pixels = new int[width * height];

			int dstByteOffset = 0;

			do {
				int chunkHeader = 0;
				try {
					chunkHeader = (byte) in.read() & 0xFF;
				} catch (IOException e) {
					throw new IOException(
							"Could not read RLE imageData header !");
				}

				boolean repeatColor;

				if (chunkHeader < 128) {
					chunkHeader++;
					repeatColor = false;
				} else {
					chunkHeader -= 127;
					readBuffer(in, colorBuffer);
					repeatColor = true;
				}

				for (int counter = 0; counter < chunkHeader; counter++) {
					if (!repeatColor) {
						readBuffer(in, colorBuffer);
					}

					int x = currentPixel % orgWidth;
					int y = currentPixel / orgWidth;

					int actualByteOffset = dstByteOffset;
					if (!flipVertically) {
						actualByteOffset = ((height - y - 1) * trgLineSize)
								+ (x * dstBytesPerPixel);
					}

					if (dstBytesPerPixel == 4) {
						writePixel(pixels, colorBuffer[2], colorBuffer[1],
								colorBuffer[0], colorBuffer[3], true,
								actualByteOffset);
					} else {
						writePixel(pixels, colorBuffer[2], colorBuffer[1],
								colorBuffer[0], (byte) 0, false,
								actualByteOffset);
					}

					dstByteOffset += dstBytesPerPixel;

					currentPixel++;

					if (currentPixel > pixelCount) {
						throw new IOException("Too many pixels read !");
					}
				}
			} while (currentPixel < pixelCount);

			tga.pixels = pixels;
			tga.type = dstBytesPerPixel;

		}

		public static State load(String res) throws IOException {
			return load(res, new State());
		}

		public static State load(String res, State tag) throws IOException {
			InputStream in = Resources.openResource(res);
			State tga = load(in, tag, true, false);
			if (in != null) {
				try {
					in.close();
					in = null;
				} catch (Exception e) {
				}
			}
			return tga;
		}

		public static State load(InputStream in, State tga,
				boolean acceptAlpha, boolean flipVertically) throws IOException {
			if (in.available() < TGA_HEADER_SIZE) {
				return (null);
			}
			byte[] header = new byte[TGA_HEADER_SIZE];
			final int headerType = compareFormatHeader(in, header);
			if (headerType == TGA_HEADER_INVALID) {
				return (null);
			}
			if (headerType == TGA_HEADER_UNCOMPRESSED) {
				loadUncompressed(header, tga, in, acceptAlpha, flipVertically);
			} else if (headerType == TGA_HEADER_COMPRESSED) {
				loadCompressed(header, tga, in, acceptAlpha, flipVertically);
			} else {
				throw new IOException("State file be type 2 or type 10 !");
			}
			return tga;
		}
	}

	private final static String tgaExtension = ".tga";

	private final static ArrayList<LImage> images = new ArrayList<LImage>(100);

	private BufferedImage bufferedImage;

	private String fileName;

	private LGraphics g;

	private int width, height;

	private boolean isClose, isUpdate, isAutoDispose = true;

	private LTexture texture;

	private Format format = Format.DEFAULT;

	public static LImage createImage(byte[] buffer) {
		return new LImage(GraphicsUtils.toolKit.createImage(buffer));
	}

	public static LImage createImage(byte[] buffer, int imageoffset,
			int imagelength) {
		return new LImage(GraphicsUtils.toolKit.createImage(buffer,
				imageoffset, imagelength));
	}

	public static LImage createImage(int width, int height) {
		return new LImage(width, height, false);
	}

	public static LImage createImage(int width, int height, boolean transparency) {
		return new LImage(width, height, transparency);
	}

	public static LImage createImage(int width, int height, int type) {
		return new LImage(width, height, type);
	}

	public static LImage createImage(String fileName) {
		return new LImage(fileName);
	}

	public static LImage createRGBImage(int[] rgb, int width, int height,
			boolean processAlpha) {
		if (rgb == null) {
			throw new NullPointerException();
		}
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException();
		}
		BufferedImage img = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		if (!processAlpha) {
			int l = rgb.length;
			int[] rgbAux = new int[l];
			for (int i = 0; i < l; i++) {
				rgbAux[i] = rgb[i] | 0xff000000;
			}
			rgb = rgbAux;
		}
		img.setRGB(0, 0, width, height, rgb, 0, width);
		return new LImage(img);
	}

	public static LImage[] createImage(int count, int w, int h,
			boolean transparency) {
		LImage[] image = new LImage[count];
		for (int i = 0; i < image.length; i++) {
			image[i] = new LImage(w, h, transparency);
		}
		return image;
	}

	public static LImage[] createImage(int count, int w, int h, int type) {
		LImage[] image = new LImage[count];
		for (int i = 0; i < image.length; i++) {
			image[i] = new LImage(w, h, type);
		}
		return image;
	}

	public static LImage createImage(LImage image, int x, int y, int width,
			int height, int transform) {
		int[] buf = new int[width * height];
		image.getPixels(buf, 0, width, x, y, width, height);
		int th;
		int tw;
		if ((transform & 4) != 0) {
			th = width;
			tw = height;
		} else {
			th = height;
			tw = width;
		}
		if (transform != 0) {
			int[] trans = new int[buf.length];
			int sp = 0;
			for (int sy = 0; sy < height; sy++) {
				int tx;
				int ty;
				int td;

				switch (transform) {
				case LGraphics.TRANS_ROT90:
					tx = tw - sy - 1;
					ty = 0;
					td = tw;
					break;
				case LGraphics.TRANS_ROT180:
					tx = tw - 1;
					ty = th - sy - 1;
					td = -1;
					break;
				case LGraphics.TRANS_ROT270:
					tx = sy;
					ty = th - 1;
					td = -tw;
					break;
				case LGraphics.TRANS_MIRROR:
					tx = tw - 1;
					ty = sy;
					td = -1;
					break;
				case LGraphics.TRANS_MIRROR_ROT90:
					tx = tw - sy - 1;
					ty = th - 1;
					td = -tw;
					break;
				case LGraphics.TRANS_MIRROR_ROT180:
					tx = 0;
					ty = th - sy - 1;
					td = 1;
					break;
				case LGraphics.TRANS_MIRROR_ROT270:
					tx = sy;
					ty = 0;
					td = tw;
					break;
				default:
					throw new RuntimeException("Illegal transformation: "
							+ transform);
				}

				int tp = ty * tw + tx;
				for (int sx = 0; sx < width; sx++) {
					trans[tp] = buf[sp++];
					tp += td;
				}
			}
			buf = trans;
		}

		return createRGBImage(buf, tw, th, true);
	}

	public LImage(int width, int height) {
		this(width, height, true);
	}

	public LImage(int width, int height, boolean transparency) {
		try {
			LSystem.gc(50, 1);
			this.width = width;
			this.height = height;
			this.bufferedImage = GraphicsUtils.createImage(width, height,
					transparency);
		} catch (Exception e) {
			try {
				LTextures.destroyAll();
				LSystem.gc();
				this.width = width;
				this.height = height;
				this.bufferedImage = GraphicsUtils.createImage(width, height,
						transparency);
			} catch (Exception ex) {
				LSystem.gc();
			}
		}
		if (!images.contains(this)) {
			images.add(this);
		}
	}

	public LImage(int width, int height, int type) {
		this.width = width;
		this.height = height;
		this.bufferedImage = GraphicsUtils.createImage(width, height, type);
		if (!images.contains(this)) {
			images.add(this);
		}
	}

	public LImage(String fileName) {
		if (fileName == null) {
			throw new RuntimeException("file name is null !");
		}
		String res;
		if (fileName.startsWith("/")) {
			res = fileName.substring(1);
		} else {
			res = fileName;
		}
		this.fileName = fileName;
		BufferedImage img = null;
		if (fileName.toLowerCase().lastIndexOf(tgaExtension) != -1) {
			try {
				LFormatTGA.State tga = LFormatTGA.load(res);
				if (tga != null) {
					img = GraphicsUtils.createImage(tga.width, tga.height,
							tga.type == 4 ? true : false);
					img.setRGB(0, 0, tga.width, tga.height, tga.pixels, 0,
							tga.width);
					tga.dispose();
					tga = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			img = GraphicsUtils.loadBufferedImage(res);
		}
		if (img == null) {
			throw new RuntimeException("File " + fileName + " was not found !");
		}
		setImage(img);
		if (!images.contains(this)) {
			images.add(this);
		}
	}

	public LImage(BufferedImage img) {
		this.setImage(img);
		if (!images.contains(this)) {
			images.add(this);
		}
	}

	public LImage(Image img) {
		GraphicsUtils.waitImage(img);
		this.setImage(img);
		if (!images.contains(this)) {
			images.add(this);
		}
	}

	public void setImage(LImage img) {
		this.width = img.getWidth();
		this.height = img.getHeight();
		this.bufferedImage = img.bufferedImage;
		this.isAutoDispose = img.isAutoDispose;
	}

	public void setImage(BufferedImage img) {
		this.width = img.getWidth();
		this.height = img.getHeight();
		this.bufferedImage = img;
	}

	public void setImage(Image img) {
		this.width = img.getWidth(null);
		this.height = img.getHeight(null);
		this.bufferedImage = GraphicsUtils.getBufferImage(img);
	}

	public Object clone() {
		return new LImage(bufferedImage);
	}

	public ImageProducer getSource() {
		return bufferedImage.getSource();
	}

	public boolean hasAlpha() {
		return bufferedImage.getColorModel().hasAlpha();
	}

	public Buffer getByteBuffer() {
		return getByteBuffer(this);
	}

	public static Buffer getByteBuffer(LImage image) {
		boolean useByte = (image.getRaster().getTransferType() == DataBuffer.TYPE_BYTE);
		if (useByte) {
			return NativeSupport.getByteBuffer((byte[]) image.getRaster()
					.getDataElements(0, 0, image.getWidth(), image.getHeight(),
							null));
		} else {
			BufferedImage temp = new BufferedImage(image.getWidth(),
					image.getHeight(),
					image.hasAlpha() ? BufferedImage.TYPE_4BYTE_ABGR
							: BufferedImage.TYPE_3BYTE_BGR);
			Graphics g = temp.getGraphics();
			g.drawImage(image.bufferedImage, 0, 0, null);
			g.dispose();
			Buffer buffer = NativeSupport.getByteBuffer((byte[]) temp
					.getRaster().getDataElements(0, 0, temp.getWidth(),
							temp.getHeight(), null));
			if (temp != null) {
				temp.flush();
				temp = null;
			}
			return buffer;
		}
	}

	public LGraphics getLGraphics() {
		if (g == null || g.isClose()) {
			g = new LGraphics(bufferedImage);
			isUpdate = true;
		}
		return g;
	}

	public LGraphics create() {
		return new LGraphics(bufferedImage);
	}

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	public int getWidth() {
		return bufferedImage.getWidth();
	}

	public int getHeight() {
		return bufferedImage.getHeight();
	}

	public Color getColorAt(int x, int y) {
		return new Color(this.getRGBAt(x, y), true);
	}

	public int getRGBAt(int x, int y) {
		if (x >= this.getWidth()) {
			throw new IndexOutOfBoundsException("X is out of bounds: " + x
					+ "," + this.getWidth());
		} else if (y >= this.getHeight()) {
			throw new IndexOutOfBoundsException("Y is out of bounds: " + y
					+ "," + this.getHeight());
		} else if (x < 0) {
			throw new IndexOutOfBoundsException("X is out of bounds: " + x);
		} else if (y < 0) {
			throw new IndexOutOfBoundsException("Y is out of bounds: " + y);
		} else {
			return this.bufferedImage.getRGB(x, y);
		}
	}

	public WritableRaster getRaster() {
		return bufferedImage.getRaster();
	}

	public int[] getPixels() {
		int pixels[] = new int[width * height];
		bufferedImage.getRGB(0, 0, width, height, pixels, 0, width);
		return pixels;
	}

	public int[] getPixels(int pixels[]) {
		bufferedImage.getRGB(0, 0, width, height, pixels, 0, width);
		return pixels;
	}

	public int[] getPixels(int x, int y, int w, int h) {
		int[] pixels = new int[w * h];
		bufferedImage.getRGB(x, y, w, h, pixels, 0, w);
		return pixels;
	}

	public int[] getPixels(int offset, int stride, int x, int y, int width,
			int height) {
		int pixels[] = new int[width * height];
		bufferedImage.getRGB(x, y, width, height, pixels, offset, stride);
		return pixels;
	}

	public int[] getPixels(int pixels[], int offset, int stride, int x, int y,
			int width, int height) {
		bufferedImage.getRGB(x, y, width, height, pixels, offset, stride);
		return pixels;
	}

	public void setPixels(int[] pixels, int width, int height) {
		bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);
	}

	public void setRGB(int startX, int startY, int w, int h, int[] rgbArray,
			int offset, int scansize) {
		setPixels(rgbArray, offset, scansize, startX, startY, w, h);
	}

	public void setPixels(int[] pixels, int offset, int stride, int x, int y,
			int width, int height) {
		bufferedImage.setRGB(x, y, width, height, pixels, offset, stride);
	}

	public int[] setPixels(int[] pixels, int x, int y, int w, int h) {
		bufferedImage.setRGB(x, y, w, h, pixels, 0, w);
		return pixels;
	}

	public void setPixel(Color c, int x, int y) {
		bufferedImage.setRGB(x, y, c.getRGB());
	}

	public void setPixel(int rgb, int x, int y) {
		bufferedImage.setRGB(x, y, rgb);
	}

	public int getPixel(int x, int y) {
		return bufferedImage.getRGB(x, y);
	}

	public int getRGB(int x, int y) {
		return bufferedImage.getRGB(x, y);
	}

	public void setRGB(int rgb, int x, int y) {
		bufferedImage.setRGB(x, y, rgb);
	}

	public LImage getSubImage(int x, int y, int w, int h) {
		return new LImage(bufferedImage.getSubimage(x, y, w, h));
	}

	public LImage scaledInstance(int w, int h) {
		int width = getWidth();
		int height = getHeight();
		if (width == w && height == h) {
			return this;
		}
		return new LImage(GraphicsUtils.getResize(bufferedImage, w, h));
	}

	public void getRGB(int pixels[], int offset, int stride, int x, int y,
			int width, int height) {
		getPixels(pixels, offset, stride, x, y, width, height);
	}

	public void getRGB(int startX, int startY, int w, int h, int[] rgbArray,
			int offset, int scansize) {
		getPixels(rgbArray, offset, scansize, startX, startY, w, h);
	}

	public int hashCode() {
		return GraphicsUtils.hashImage(bufferedImage);
	}

	public boolean isClose() {
		return isClose || bufferedImage == null;
	}

	public boolean isAutoDispose() {
		return isAutoDispose && !isClose();
	}

	public void setAutoDispose(boolean dispose) {
		this.isAutoDispose = dispose;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
		this.isUpdate = true;
	}

	public LTexture getTexture() {
		if (texture == null || isUpdate) {
			setAutoDispose(false);
			LTexture tmp = texture;
			texture = new LTexture(GLLoader.getTextureData(this), format);
			if (tmp != null) {
				tmp.dispose();
				tmp = null;
			}
			isUpdate = false;
		}
		return texture;
	}

	public LPixmapData newPixmap() {
		return new LPixmapData(this);
	}

	public String getPath() {
		return fileName;
	}

	public void dispose() {
		dispose(true);
	}

	private void dispose(boolean remove) {
		isClose = true;
		if (bufferedImage != null) {
			bufferedImage.flush();
			bufferedImage = null;
		}
		if (texture != null && isAutoDispose) {
			texture.dispose();
			texture = null;
		}
		if (remove) {
			images.remove(this);
		}
	}

	public static void disposeAll() {
		for (LImage img : images) {
			if (img != null) {
				img.dispose(false);
				img = null;
			}
		}
		images.clear();
	}

}
