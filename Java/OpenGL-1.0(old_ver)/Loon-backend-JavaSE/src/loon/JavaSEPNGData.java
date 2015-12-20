package loon;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import loon.core.graphics.opengl.GLEx;

import org.lwjgl.BufferUtils;

public class JavaSEPNGData {

	static class PNGDecoder {

		public static Format ALPHA = new Format(1, true);
		public static Format LUMINANCE = new Format(1, false);
		public static Format LUMINANCE_ALPHA = new Format(2, true);
		public static Format RGB = new Format(3, false);
		public static Format RGBA = new Format(4, true);
		public static Format BGRA = new Format(4, true);
		public static Format ABGR = new Format(4, true);

		public static class Format {

			final int numComponents;
			final boolean hasAlpha;

			private Format(int numComponents, boolean hasAlpha) {
				this.numComponents = numComponents;
				this.hasAlpha = hasAlpha;
			}

			public int getNumComponents() {
				return numComponents;
			}

			public boolean isHasAlpha() {
				return hasAlpha;
			}
		}

		private static final byte[] SIGNATURE = { (byte) 137, 80, 78, 71, 13,
				10, 26, 10 };

		private static final int IHDR = 0x49484452;
		private static final int PLTE = 0x504C5445;
		private static final int tRNS = 0x74524E53;
		private static final int IDAT = 0x49444154;

		private static final byte COLOR_GREYSCALE = 0;
		private static final byte COLOR_TRUECOLOR = 2;
		private static final byte COLOR_INDEXED = 3;
		private static final byte COLOR_GREYALPHA = 4;
		private static final byte COLOR_TRUEALPHA = 6;

		private final InputStream input;
		private final CRC32 crc;
		private final byte[] buffer;

		private int chunkLength;
		private int chunkType;
		private int chunkRemaining;

		private int width;
		private int height;
		private int bitdepth;
		private int colorType;
		private int bytesPerPixel;
		private byte[] palette;
		private byte[] paletteA;
		private byte[] transPixel;

		public PNGDecoder(InputStream input) throws IOException {
			this.input = input;
			this.crc = new CRC32();
			this.buffer = new byte[4096];

			readFully(buffer, 0, SIGNATURE.length);
			if (!checkSignature(buffer)) {
				throw new IOException("Not a valid PNG file");
			}

			openChunk(IHDR);
			readIHDR();
			closeChunk();

			searchIDAT: for (;;) {
				openChunk();
				switch (chunkType) {
				case IDAT:
					break searchIDAT;
				case PLTE:
					readPLTE();
					break;
				case tRNS:
					readtRNS();
					break;
				}
				closeChunk();
			}

			if (colorType == COLOR_INDEXED && palette == null) {
				throw new IOException("Missing PLTE chunk");
			}
		}

		public int getHeight() {
			return height;
		}

		public int getWidth() {
			return width;
		}

		public boolean hasAlpha() {
			return colorType == COLOR_TRUEALPHA || paletteA != null
					|| transPixel != null;
		}

		public boolean isRGB() {
			return colorType == COLOR_TRUEALPHA || colorType == COLOR_TRUECOLOR
					|| colorType == COLOR_INDEXED;
		}

		public Format decideTextureFormat(Format fmt) {
			switch (colorType) {
			case COLOR_TRUECOLOR:
				if ((fmt == ABGR) || (fmt == RGBA) || (fmt == BGRA)
						|| (fmt == RGB)) {
					return fmt;
				}

				return RGB;
			case COLOR_TRUEALPHA:
				if ((fmt == ABGR) || (fmt == RGBA) || (fmt == BGRA)
						|| (fmt == RGB)) {
					return fmt;
				}

				return RGBA;
			case COLOR_GREYSCALE:
				if ((fmt == LUMINANCE) || (fmt == ALPHA)) {
					return fmt;
				}

				return LUMINANCE;
			case COLOR_GREYALPHA:
				return LUMINANCE_ALPHA;
			case COLOR_INDEXED:
				if ((fmt == ABGR) || (fmt == RGBA) || (fmt == BGRA)) {
					return fmt;
				}

				return RGBA;
			default:
				throw new UnsupportedOperationException("Not yet implemented");
			}
		}

		public void decode(ByteBuffer buffer, int stride, Format fmt)
				throws IOException {
			final int offset = buffer.position();
			final int lineSize = ((width * bitdepth + 7) / 8) * bytesPerPixel;
			byte[] curLine = new byte[lineSize + 1];
			byte[] prevLine = new byte[lineSize + 1];
			byte[] palLine = (bitdepth < 8) ? new byte[width + 1] : null;

			final Inflater inflater = new Inflater();
			try {
				for (int y = 0; y < height; y++) {
					readChunkUnzip(inflater, curLine, 0, curLine.length);
					unfilter(curLine, prevLine);

					buffer.position(offset + y * stride);

					switch (colorType) {
					case COLOR_TRUECOLOR:
						if (fmt == ABGR) {
							copyRGBtoABGR(buffer, curLine);
						} else if (fmt == RGBA) {
							copyRGBtoRGBA(buffer, curLine);
						} else if (fmt == BGRA) {
							copyRGBtoBGRA(buffer, curLine);
						} else if (fmt == RGB) {
							copy(buffer, curLine);
						} else {
							throw new UnsupportedOperationException(
									"Unsupported format for this image");
						}
						break;
					case COLOR_TRUEALPHA:
						if (fmt == ABGR) {
							copyRGBAtoABGR(buffer, curLine);
						} else if (fmt == RGBA) {
							copy(buffer, curLine);
						} else if (fmt == BGRA) {
							copyRGBAtoBGRA(buffer, curLine);
							break;
						} else if (fmt == RGB) {
							copyRGBAtoRGB(buffer, curLine);
							break;
						} else {
							throw new UnsupportedOperationException(
									"Unsupported format for this image");
						}
						break;
					case COLOR_GREYSCALE:
						if ((fmt == LUMINANCE) || (fmt == ALPHA)) {
							copy(buffer, curLine);
						} else {
							throw new UnsupportedOperationException(
									"Unsupported format for this image");
						}
						break;
					case COLOR_GREYALPHA:
						if (fmt == LUMINANCE_ALPHA) {
							copy(buffer, curLine);
						} else {
							throw new UnsupportedOperationException(
									"Unsupported format for this image");
						}
						break;
					case COLOR_INDEXED:
						switch (bitdepth) {
						case 8:
							palLine = curLine;
							break;
						case 4:
							expand4(curLine, palLine);
							break;
						case 2:
							expand2(curLine, palLine);
							break;
						case 1:
							expand1(curLine, palLine);
							break;
						default:
							throw new UnsupportedOperationException(
									"Unsupported bitdepth for this image");
						}
						if (fmt == ABGR) {
							copyPALtoABGR(buffer, palLine);
						} else if (fmt == RGBA) {
							copyPALtoRGBA(buffer, palLine);
						} else if (fmt == BGRA) {
							copyPALtoBGRA(buffer, palLine);
						} else {
							throw new UnsupportedOperationException(
									"Unsupported format for this image");
						}
						break;
					default:
						throw new UnsupportedOperationException(
								"Not yet implemented");
					}

					byte[] tmp = curLine;
					curLine = prevLine;
					prevLine = tmp;
				}
			} finally {
				inflater.end();
			}
		}

		private void copy(ByteBuffer buffer, byte[] curLine) {
			buffer.put(curLine, 1, curLine.length - 1);
		}

		private void copyRGBtoABGR(ByteBuffer buffer, byte[] curLine) {
			if (transPixel != null) {
				byte tr = transPixel[1];
				byte tg = transPixel[3];
				byte tb = transPixel[5];
				for (int i = 1, n = curLine.length; i < n; i += 3) {
					byte r = curLine[i];
					byte g = curLine[i + 1];
					byte b = curLine[i + 2];
					byte a = (byte) 0xFF;
					if (r == tr && g == tg && b == tb) {
						a = 0;
					}
					buffer.put(a).put(b).put(g).put(r);
				}
			} else {
				for (int i = 1, n = curLine.length; i < n; i += 3) {
					buffer.put((byte) 0xFF).put(curLine[i + 2])
							.put(curLine[i + 1]).put(curLine[i]);
				}
			}
		}

		private void copyRGBtoRGBA(ByteBuffer buffer, byte[] curLine) {
			if (transPixel != null) {
				byte tr = transPixel[1];
				byte tg = transPixel[3];
				byte tb = transPixel[5];
				for (int i = 1, n = curLine.length; i < n; i += 3) {
					byte r = curLine[i];
					byte g = curLine[i + 1];
					byte b = curLine[i + 2];
					byte a = (byte) 0xFF;
					if (r == tr && g == tg && b == tb) {
						a = 0;
					}
					buffer.put(r).put(g).put(b).put(a);
				}
			} else {
				for (int i = 1, n = curLine.length; i < n; i += 3) {
					buffer.put(curLine[i]).put(curLine[i + 1])
							.put(curLine[i + 2]).put((byte) 0xFF);
				}
			}
		}

		private void copyRGBtoBGRA(ByteBuffer buffer, byte[] curLine) {
			if (transPixel != null) {
				byte tr = transPixel[1];
				byte tg = transPixel[3];
				byte tb = transPixel[5];
				for (int i = 1, n = curLine.length; i < n; i += 3) {
					byte r = curLine[i];
					byte g = curLine[i + 1];
					byte b = curLine[i + 2];
					byte a = (byte) 0xFF;
					if (r == tr && g == tg && b == tb) {
						a = 0;
					}
					buffer.put(b).put(g).put(r).put(a);
				}
			} else {
				for (int i = 1, n = curLine.length; i < n; i += 3) {
					buffer.put(curLine[i + 2]).put(curLine[i + 1])
							.put(curLine[i]).put((byte) 0xFF);
				}
			}
		}

		private void copyRGBAtoABGR(ByteBuffer buffer, byte[] curLine) {
			for (int i = 1, n = curLine.length; i < n; i += 4) {
				buffer.put(curLine[i + 3]).put(curLine[i + 2])
						.put(curLine[i + 1]).put(curLine[i]);
			}
		}

		private void copyRGBAtoBGRA(ByteBuffer buffer, byte[] curLine) {
			for (int i = 1, n = curLine.length; i < n; i += 4) {
				buffer.put(curLine[i + 2]).put(curLine[i + 1])
						.put(curLine[i + 0]).put(curLine[i + 3]);
			}
		}

		private void copyRGBAtoRGB(ByteBuffer buffer, byte[] curLine) {
			for (int i = 1, n = curLine.length; i < n; i += 4) {
				buffer.put(curLine[i]).put(curLine[i + 1]).put(curLine[i + 2]);
			}
		}

		private void copyPALtoABGR(ByteBuffer buffer, byte[] curLine) {
			if (paletteA != null) {
				for (int i = 1, n = curLine.length; i < n; i += 1) {
					int idx = curLine[i] & 255;
					byte r = palette[idx * 3 + 0];
					byte g = palette[idx * 3 + 1];
					byte b = palette[idx * 3 + 2];
					byte a = paletteA[idx];
					buffer.put(a).put(b).put(g).put(r);
				}
			} else {
				for (int i = 1, n = curLine.length; i < n; i += 1) {
					int idx = curLine[i] & 255;
					byte r = palette[idx * 3 + 0];
					byte g = palette[idx * 3 + 1];
					byte b = palette[idx * 3 + 2];
					byte a = (byte) 0xFF;
					buffer.put(a).put(b).put(g).put(r);
				}
			}
		}

		private void copyPALtoRGBA(ByteBuffer buffer, byte[] curLine) {
			if (paletteA != null) {
				for (int i = 1, n = curLine.length; i < n; i += 1) {
					int idx = curLine[i] & 255;
					byte r = palette[idx * 3 + 0];
					byte g = palette[idx * 3 + 1];
					byte b = palette[idx * 3 + 2];
					byte a = paletteA[idx];
					buffer.put(r).put(g).put(b).put(a);
				}
			} else {
				for (int i = 1, n = curLine.length; i < n; i += 1) {
					int idx = curLine[i] & 255;
					byte r = palette[idx * 3 + 0];
					byte g = palette[idx * 3 + 1];
					byte b = palette[idx * 3 + 2];
					byte a = (byte) 0xFF;
					buffer.put(r).put(g).put(b).put(a);
				}
			}
		}

		private void copyPALtoBGRA(ByteBuffer buffer, byte[] curLine) {
			if (paletteA != null) {
				for (int i = 1, n = curLine.length; i < n; i += 1) {
					int idx = curLine[i] & 255;
					byte r = palette[idx * 3 + 0];
					byte g = palette[idx * 3 + 1];
					byte b = palette[idx * 3 + 2];
					byte a = paletteA[idx];
					buffer.put(b).put(g).put(r).put(a);
				}
			} else {
				for (int i = 1, n = curLine.length; i < n; i += 1) {
					int idx = curLine[i] & 255;
					byte r = palette[idx * 3 + 0];
					byte g = palette[idx * 3 + 1];
					byte b = palette[idx * 3 + 2];
					byte a = (byte) 0xFF;
					buffer.put(b).put(g).put(r).put(a);
				}
			}
		}

		private void expand4(byte[] src, byte[] dst) {
			for (int i = 1, n = dst.length; i < n; i += 2) {
				int val = src[1 + (i >> 1)] & 255;
				switch (n - i) {
				default:
					dst[i + 1] = (byte) (val & 15);
				case 1:
					dst[i] = (byte) (val >> 4);
				}
			}
		}

		private void expand2(byte[] src, byte[] dst) {
			for (int i = 1, n = dst.length; i < n; i += 4) {
				int val = src[1 + (i >> 2)] & 255;
				switch (n - i) {
				default:
					dst[i + 3] = (byte) ((val) & 3);
				case 3:
					dst[i + 2] = (byte) ((val >> 2) & 3);
				case 2:
					dst[i + 1] = (byte) ((val >> 4) & 3);
				case 1:
					dst[i] = (byte) ((val >> 6));
				}
			}
		}

		private void expand1(byte[] src, byte[] dst) {
			for (int i = 1, n = dst.length; i < n; i += 8) {
				int val = src[1 + (i >> 3)] & 255;
				switch (n - i) {
				default:
					dst[i + 7] = (byte) ((val) & 1);
				case 7:
					dst[i + 6] = (byte) ((val >> 1) & 1);
				case 6:
					dst[i + 5] = (byte) ((val >> 2) & 1);
				case 5:
					dst[i + 4] = (byte) ((val >> 3) & 1);
				case 4:
					dst[i + 3] = (byte) ((val >> 4) & 1);
				case 3:
					dst[i + 2] = (byte) ((val >> 5) & 1);
				case 2:
					dst[i + 1] = (byte) ((val >> 6) & 1);
				case 1:
					dst[i] = (byte) ((val >> 7));
				}
			}
		}

		private void unfilter(byte[] curLine, byte[] prevLine)
				throws IOException {
			switch (curLine[0]) {
			case 0: // none
				break;
			case 1:
				unfilterSub(curLine);
				break;
			case 2:
				unfilterUp(curLine, prevLine);
				break;
			case 3:
				unfilterAverage(curLine, prevLine);
				break;
			case 4:
				unfilterPaeth(curLine, prevLine);
				break;
			default:
				throw new IOException("invalide filter type in scanline: "
						+ curLine[0]);
			}
		}

		private void unfilterSub(byte[] curLine) {
			final int bpp = this.bytesPerPixel;
			for (int i = bpp + 1, n = curLine.length; i < n; ++i) {
				curLine[i] += curLine[i - bpp];
			}
		}

		private void unfilterUp(byte[] curLine, byte[] prevLine) {
			for (int i = 1, n = curLine.length; i < n; ++i) {
				curLine[i] += prevLine[i];
			}
		}

		private void unfilterAverage(byte[] curLine, byte[] prevLine) {
			final int bpp = this.bytesPerPixel;

			int i;
			for (i = 1; i <= bpp; ++i) {
				curLine[i] += (byte) ((prevLine[i] & 0xFF) >>> 1);
			}
			for (int n = curLine.length; i < n; ++i) {
				curLine[i] += (byte) (((prevLine[i] & 0xFF) + (curLine[i - bpp] & 0xFF)) >>> 1);
			}
		}

		private void unfilterPaeth(byte[] curLine, byte[] prevLine) {
			final int bpp = this.bytesPerPixel;

			int i;
			for (i = 1; i <= bpp; ++i) {
				curLine[i] += prevLine[i];
			}
			for (int n = curLine.length; i < n; ++i) {
				int a = curLine[i - bpp] & 255;
				int b = prevLine[i] & 255;
				int c = prevLine[i - bpp] & 255;
				int p = a + b - c;
				int pa = p - a;
				if (pa < 0)
					pa = -pa;
				int pb = p - b;
				if (pb < 0)
					pb = -pb;
				int pc = p - c;
				if (pc < 0)
					pc = -pc;
				if (pa <= pb && pa <= pc)
					c = a;
				else if (pb <= pc)
					c = b;
				curLine[i] += (byte) c;
			}
		}

		private void readIHDR() throws IOException {
			checkChunkLength(13);
			readChunk(buffer, 0, 13);
			width = readInt(buffer, 0);
			height = readInt(buffer, 4);
			bitdepth = buffer[8] & 255;
			colorType = buffer[9] & 255;

			switch (colorType) {
			case COLOR_GREYSCALE:
				if (bitdepth != 8) {
					throw new IOException("Unsupported bit depth: " + bitdepth);
				}
				bytesPerPixel = 1;
				break;
			case COLOR_GREYALPHA:
				if (bitdepth != 8) {
					throw new IOException("Unsupported bit depth: " + bitdepth);
				}
				bytesPerPixel = 2;
				break;
			case COLOR_TRUECOLOR:
				if (bitdepth != 8) {
					throw new IOException("Unsupported bit depth: " + bitdepth);
				}
				bytesPerPixel = 3;
				break;
			case COLOR_TRUEALPHA:
				if (bitdepth != 8) {
					throw new IOException("Unsupported bit depth: " + bitdepth);
				}
				bytesPerPixel = 4;
				break;
			case COLOR_INDEXED:
				switch (bitdepth) {
				case 8:
				case 4:
				case 2:
				case 1:
					bytesPerPixel = 1;
					break;
				default:
					throw new IOException("Unsupported bit depth: " + bitdepth);
				}
				break;
			default:
				throw new IOException("unsupported color format: " + colorType);
			}

			if (buffer[10] != 0) {
				throw new IOException("unsupported compression method");
			}
			if (buffer[11] != 0) {
				throw new IOException("unsupported filtering method");
			}
			if (buffer[12] != 0) {
				throw new IOException("unsupported interlace method");
			}
		}

		private void readPLTE() throws IOException {
			int paletteEntries = chunkLength / 3;
			if (paletteEntries < 1 || paletteEntries > 256
					|| (chunkLength % 3) != 0) {
				throw new IOException("PLTE chunk has wrong length");
			}
			palette = new byte[paletteEntries * 3];
			readChunk(palette, 0, palette.length);
		}

		private void readtRNS() throws IOException {
			switch (colorType) {
			case COLOR_GREYSCALE:
				checkChunkLength(2);
				transPixel = new byte[2];
				readChunk(transPixel, 0, 2);
				break;
			case COLOR_TRUECOLOR:
				checkChunkLength(6);
				transPixel = new byte[6];
				readChunk(transPixel, 0, 6);
				break;
			case COLOR_INDEXED:
				if (palette == null) {
					throw new IOException("tRNS chunk without PLTE chunk");
				}
				paletteA = new byte[palette.length / 3];
				Arrays.fill(paletteA, (byte) 0xFF);
				readChunk(paletteA, 0, paletteA.length);
				break;
			default:
			}
		}

		private void closeChunk() throws IOException {
			if (chunkRemaining > 0) {
				skip(chunkRemaining + 4);
			} else {
				readFully(buffer, 0, 4);
				int expectedCrc = readInt(buffer, 0);
				int computedCrc = (int) crc.getValue();
				if (computedCrc != expectedCrc) {
					throw new IOException("Invalid CRC");
				}
			}
			chunkRemaining = 0;
			chunkLength = 0;
			chunkType = 0;
		}

		private void openChunk() throws IOException {
			readFully(buffer, 0, 8);
			chunkLength = readInt(buffer, 0);
			chunkType = readInt(buffer, 4);
			chunkRemaining = chunkLength;
			crc.reset();
			crc.update(buffer, 4, 4);
		}

		private void openChunk(int expected) throws IOException {
			openChunk();
			if (chunkType != expected) {
				throw new IOException("Expected chunk: "
						+ Integer.toHexString(expected));
			}
		}

		private void checkChunkLength(int expected) throws IOException {
			if (chunkLength != expected) {
				throw new IOException("Chunk has wrong size");
			}
		}

		private int readChunk(byte[] buffer, int offset, int length)
				throws IOException {
			if (length > chunkRemaining) {
				length = chunkRemaining;
			}
			readFully(buffer, offset, length);
			crc.update(buffer, offset, length);
			chunkRemaining -= length;
			return length;
		}

		private void refillInflater(Inflater inflater) throws IOException {
			while (chunkRemaining == 0) {
				closeChunk();
				openChunk(IDAT);
			}
			int read = readChunk(buffer, 0, buffer.length);
			inflater.setInput(buffer, 0, read);
		}

		private void readChunkUnzip(Inflater inflater, byte[] buffer,
				int offset, int length) throws IOException {
			try {
				do {
					int read = inflater.inflate(buffer, offset, length);
					if (read <= 0) {
						if (inflater.finished()) {
							throw new EOFException();
						}
						if (inflater.needsInput()) {
							refillInflater(inflater);
						} else {
							throw new IOException("Can't inflate " + length
									+ " bytes");
						}
					} else {
						offset += read;
						length -= read;
					}
				} while (length > 0);
			} catch (DataFormatException ex) {
				throw (IOException) (new IOException("inflate error")
						.initCause(ex));
			}
		}

		private void readFully(byte[] buffer, int offset, int length)
				throws IOException {
			do {
				int read = input.read(buffer, offset, length);
				if (read < 0) {
					throw new EOFException();
				}
				offset += read;
				length -= read;
			} while (length > 0);
		}

		private int readInt(byte[] buffer, int offset) {
			return ((buffer[offset]) << 24)
					| ((buffer[offset + 1] & 255) << 16)
					| ((buffer[offset + 2] & 255) << 8)
					| ((buffer[offset + 3] & 255));
		}

		private void skip(long amount) throws IOException {
			while (amount > 0) {
				long skipped = input.skip(amount);
				if (skipped < 0) {
					throw new EOFException();
				}
				amount -= skipped;
			}
		}

		private static boolean checkSignature(byte[] buffer) {
			for (int i = 0; i < SIGNATURE.length; i++) {
				if (buffer[i] != SIGNATURE[i]) {
					return false;
				}
			}
			return true;
		}
	}

	private int width;

	private int height;

	private int texHeight;

	private int texWidth;

	private int bitDepth;

	private boolean hasAlpha;

	private ByteBuffer scratch;

	public int getDepth() {
		return bitDepth;
	}

	public ByteBuffer getImageBufferData() {
		return scratch;
	}

	public int getTexHeight() {
		return texHeight;
	}

	public int getTexWidth() {
		return texWidth;
	}

	public ByteBuffer loadImage(InputStream fis) throws IOException {
		return loadImage(fis, false, null);
	}

	public ByteBuffer loadImage(InputStream fis, boolean flipped,
			int[] transparent) throws IOException {
		return loadImage(fis, flipped, false, transparent);
	}

	public ByteBuffer loadImage(InputStream fis, boolean flipped,
			boolean forceAlpha, int[] transparent) throws IOException {

		PNGDecoder decoder = new PNGDecoder(fis);

		if (!decoder.isRGB()) {
			throw new IOException(
					"Only RGB formatted images are supported by the PNGLoader");
		}

		width = decoder.getWidth();
		height = decoder.getHeight();
		texWidth = GLEx.toPowerOfTwo(width);
		texHeight = GLEx.toPowerOfTwo(height);
		hasAlpha = decoder.hasAlpha();

		int perPixel = decoder.hasAlpha() ? 4 : 3;
		bitDepth = decoder.hasAlpha() ? 32 : 24;

		scratch = BufferUtils.createByteBuffer(texWidth * texHeight * perPixel);
		decoder.decode(scratch, texWidth * perPixel,
				perPixel == 4 ? PNGDecoder.RGBA : PNGDecoder.RGB);

		if (height < texHeight - 1) {
			int topOffset = (texHeight - 1) * (texWidth * perPixel);
			int bottomOffset = (height - 1) * (texWidth * perPixel);
			for (int x = 0; x < texWidth; x++) {
				for (int i = 0; i < perPixel; i++) {
					scratch.put(topOffset + x + i, scratch.get(x + i));
					scratch.put(bottomOffset + (texWidth * perPixel) + x + i,
							scratch.get(bottomOffset + x + i));
				}
			}
		}
		if (width < texWidth - 1) {
			for (int y = 0; y < texHeight; y++) {
				for (int i = 0; i < perPixel; i++) {
					scratch.put(((y + 1) * (texWidth * perPixel)) - perPixel
							+ i, scratch.get(y * (texWidth * perPixel) + i));
					scratch.put(
							(y * (texWidth * perPixel)) + (width * perPixel)
									+ i,
							scratch.get((y * (texWidth * perPixel))
									+ ((width - 1) * perPixel) + i));
				}
			}
		}

		if (!decoder.hasAlpha() && forceAlpha) {
			ByteBuffer temp = BufferUtils.createByteBuffer(texWidth * texHeight
					* 4);
			for (int x = 0; x < texWidth; x++) {
				for (int y = 0; y < texHeight; y++) {
					int srcOffset = (y * 3) + (x * texHeight * 3);
					int dstOffset = (y * 4) + (x * texHeight * 4);

					temp.put(dstOffset, scratch.get(srcOffset));
					temp.put(dstOffset + 1, scratch.get(srcOffset + 1));
					temp.put(dstOffset + 2, scratch.get(srcOffset + 2));
					if ((x < getHeight()) && (y < getWidth())) {
						temp.put(dstOffset + 3, (byte) 255);
					} else {
						temp.put(dstOffset + 3, (byte) 0);
					}
				}
			}

			bitDepth = 32;
			scratch = temp;
		}
		if (transparent != null) {
			for (int i = 0; i < texWidth * texHeight * 4; i += 4) {
				boolean match = true;
				for (int c = 0; c < 3; c++) {
					if (toInt(scratch.get(i + c)) != transparent[c]) {
						match = false;
					}
				}

				if (match) {
					scratch.put(i + 3, (byte) 0);
				}
			}
		}
		scratch.position(0);

		return scratch;
	}

	private int toInt(byte b) {
		if (b < 0) {
			return 256 + b;
		}

		return b;
	}

	public void configureEdging(boolean edging) {
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean hasAlpha() {
		return hasAlpha;
	}

}
