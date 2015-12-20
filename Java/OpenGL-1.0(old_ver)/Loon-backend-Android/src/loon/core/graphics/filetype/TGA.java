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
package loon.core.graphics.filetype;

import java.io.IOException;
import java.io.InputStream;

import loon.core.LRelease;
import loon.core.resource.Resources;

public class TGA {


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

		@Override
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
