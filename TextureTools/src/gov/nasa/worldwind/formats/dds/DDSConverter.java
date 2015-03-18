package gov.nasa.worldwind.formats.dds;

import gov.nasa.worldwind.util.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;

public class DDSConverter {
	private static final int DDSD_CAPS = 0x0001;
	private static final int DDSD_HEIGHT = 0x0002;
	private static final int DDSD_WIDTH = 0x0004;
	private static final int DDSD_PIXELFORMAT = 0x1000;
	private static final int DDSD_MIPMAPCOUNT = 0x20000;
	private static final int DDSD_LINEARSIZE = 0x80000;
	private static final int DDPF_FOURCC = 0x0004;
	private static final int DDSCAPS_TEXTURE = 0x1000;

	protected static class Color {
		private int r, g, b;

		public Color() {
			this.r = this.g = this.b = 0;
		}

		public Color(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}

		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			final Color color = (Color) o;
			if (b != color.b)
				return false;
			if (g != color.g)
				return false;
			// noinspection RedundantIfStatement
			if (r != color.r)
				return false;
			return true;
		}

		public int hashCode() {
			int result;
			result = r;
			result = 29 * result + g;
			result = 29 * result + b;
			return result;
		}
	}

	public static ByteBuffer convertToDDS(ByteBuffer image, String mimeType)
			throws IOException {
		if (image == null) {
			String message = Logging.getMessage("nullValue.ByteBufferIsNull");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}
		if (mimeType == null) {
			String message = Logging.getMessage("nullValue.MimeTypeIsNull");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}
		String suffix = WWIO.makeSuffixForMimeType(mimeType);
		if (suffix == null) {
			String message = Logging.getMessage(
					"DDSConverter.UnsupportedMimeType", mimeType);
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}
		File tempFile = WWIO.saveBufferToTempFile(image, suffix);
		return convertToDDS(tempFile);
	}

	public static ByteBuffer convertToDDS(File file) throws IOException {
		if (file == null) {
			String message = Logging.getMessage("nullValue.FileIsNull");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}
		if (!file.exists() || !file.canRead()) {
			String message = Logging
					.getMessage("DDSConverter.NoFileOrNoPermission");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}
		java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(file);
		if (image == null) {
			return null;
		}
		// Don't waste the space for transparency if
		if (image.getColorModel().hasAlpha())
			return convertToDxt3(image);
		else
			return convertToDxt1NoTransparency(image);
	}

	public static ByteBuffer convertToDxt1NoTransparency(ByteBuffer image,
			String mimeType) throws IOException {
		if (image == null) {
			String message = Logging.getMessage("nullValue.ByteBufferIsNull");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}
		if (mimeType == null) {
			String message = Logging.getMessage("nullValue.MimeTypeIsNull");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}
		String suffix = WWIO.makeSuffixForMimeType(mimeType);
		if (suffix == null) {
			String message = Logging.getMessage(
					"DDSConverter.UnsupportedMimeType", mimeType);
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}
		File tempFile = WWIO.saveBufferToTempFile(image, suffix);
		return convertToDxt1NoTransparency(tempFile);
	}

	public static ByteBuffer convertToDxt1NoTransparency(File file)
			throws IOException {
		if (file == null) { //
			String message = Logging.getMessage("nullValue.FileIsNull");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}
		if (!file.exists() || !file.canRead()) {
			String message = Logging
					.getMessage("DDSConverter.NoFileOrNoPermission");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}
		java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(file);
		if (image == null) {
			return null; // TODO: logger
		}
		return convertToDxt1NoTransparency(image);
	}

	public static ByteBuffer convertToDxt1NoTransparency(BufferedImage image) {
		if (image == null) {
			return null;
		}
		int[] pixels = new int[16];
		int bufferSize = 128 + image.getWidth() * image.getHeight() / 2;
		ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buildHeaderDxt1(buffer, image.getWidth(), image.getHeight());
		int numTilesWide = image.getWidth() / 4;
		int numTilesHigh = image.getHeight() / 4;
		for (int i = 0; i < numTilesHigh; i++) {
			for (int j = 0; j < numTilesWide; j++) {
				java.awt.image.BufferedImage originalTile = image.getSubimage(
						j * 4, i * 4, 4, 4);
				originalTile.getRGB(0, 0, 4, 4, pixels, 0, 4);
				Color[] colors = getColors888(pixels);
				for (int k = 0; k < pixels.length; k++) {
					pixels[k] = getPixel565(colors[k]);
					colors[k] = getColor565(pixels[k]);
				}
				int[] extremaIndices = determineExtremeColors(colors);
				if (pixels[extremaIndices[0]] < pixels[extremaIndices[1]]) {
					int t = extremaIndices[0];
					extremaIndices[0] = extremaIndices[1];
					extremaIndices[1] = t;
				}
				buffer.putShort((short) pixels[extremaIndices[0]]);
				buffer.putShort((short) pixels[extremaIndices[1]]);
				long bitmask = computeBitMask(colors, extremaIndices);
				buffer.putInt((int) bitmask);
			}
		}
		return buffer;
	}

	public static ByteBuffer convertToDxt3(ByteBuffer image, String mimeType)
			throws IOException {
		if (image == null) {
			String message = Logging.getMessage("nullValue.ByteBufferIsNull");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}
		if (mimeType == null) {
			String message = Logging.getMessage("nullValue.MimeTypeIsNull");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}
		String suffix = WWIO.makeSuffixForMimeType(mimeType);
		if (suffix == null) {
			String message = Logging.getMessage(
					"DDSConverter.UnsupportedMimeType", mimeType);
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}
		File tempFile = WWIO.saveBufferToTempFile(image, suffix);
		return convertToDxt3(tempFile);
	}

	public static ByteBuffer convertToDxt3(File file) throws IOException {
		if (file == null) {
			String message = Logging.getMessage("nullValue.FileIsNull");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}
		if (!file.exists() || !file.canRead()) {
			String message = Logging
					.getMessage("DDSConverter.NoFileOrNoPermission");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}
		java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(file);
		if (image == null) {
			return null;
		}
		return convertToDxt3(image);
	}

	public static ByteBuffer convertToDxt3(BufferedImage image) {
		if (image == null)
			return null; // TODO: arg check
		// Don't waste the space for transparency if
		if (!image.getColorModel().hasAlpha())
			return convertToDxt1NoTransparency(image);
		int[] pixels = new int[16];
		int bufferSize = 128 + image.getWidth() * image.getHeight();
		ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buildHeaderDxt3(buffer, image.getWidth(), image.getHeight());
		int numTilesWide = image.getWidth() / 4;
		int numTilesHigh = image.getHeight() / 4;
		for (int i = 0; i < numTilesHigh; i++) {
			for (int j = 0; j < numTilesWide; j++) {
				java.awt.image.BufferedImage originalTile = image.getSubimage(
						j * 4, i * 4, 4, 4);
				originalTile.getRGB(0, 0, 4, 4, pixels, 0, 4);
				Color[] colors = getColors888(pixels);
				// Store the alhpa table.
				for (int k = 0; k < pixels.length; k += 2) {
					buffer.put((byte) ((pixels[k] >>> 28) | (pixels[k + 1] >>> 24)));
				}
				for (int k = 0; k < pixels.length; k++) {
					pixels[k] = getPixel565(colors[k]);
					colors[k] = getColor565(pixels[k]);
				}
				int[] extremaIndices = determineExtremeColors(colors);
				if (pixels[extremaIndices[0]] < pixels[extremaIndices[1]]) {
					int t = extremaIndices[0];
					extremaIndices[0] = extremaIndices[1];
					extremaIndices[1] = t;
				}
				buffer.putShort((short) pixels[extremaIndices[0]]);
				buffer.putShort((short) pixels[extremaIndices[1]]);
				long bitmask = computeBitMask(colors, extremaIndices);
				buffer.putInt((int) bitmask);
			}
		}
		return buffer;
	}

	protected static void buildHeaderDxt1(ByteBuffer buffer, int width,
			int height) {
		buffer.rewind();
		buffer.put((byte) 'D');
		buffer.put((byte) 'D');
		buffer.put((byte) 'S');
		buffer.put((byte) ' ');
		buffer.putInt(124);
		int flag = DDSD_CAPS | DDSD_HEIGHT | DDSD_WIDTH | DDSD_PIXELFORMAT
				| DDSD_MIPMAPCOUNT | DDSD_LINEARSIZE;
		buffer.putInt(flag);
		buffer.putInt(height);
		buffer.putInt(width);
		buffer.putInt(width * height / 2);
		buffer.putInt(0); // depth
		buffer.putInt(0); // mipmap count
		buffer.position(buffer.position() + 44); // 11 unused double-words
		buffer.putInt(32); // pixel format size
		buffer.putInt(DDPF_FOURCC);
		buffer.put((byte) 'D');
		buffer.put((byte) 'X');
		buffer.put((byte) 'T');
		buffer.put((byte) '1');
		buffer.putInt(0); // bits per pixel for RGB (non-compressed) formats
		buffer.putInt(0); // rgb bit masks for RGB formats
		buffer.putInt(0); // rgb bit masks for RGB formats
		buffer.putInt(0); // rgb bit masks for RGB formats
		buffer.putInt(0); // alpha mask for RGB formats
		buffer.putInt(DDSCAPS_TEXTURE);
		buffer.putInt(0); // ddsCaps2
		buffer.position(buffer.position() + 12); // 3 unused double-words
	}

	protected static void buildHeaderDxt3(ByteBuffer buffer, int width,
			int height) {
		buffer.rewind();
		buffer.put((byte) 'D');
		buffer.put((byte) 'D');
		buffer.put((byte) 'S');
		buffer.put((byte) ' ');
		buffer.putInt(124);
		int flag = DDSD_CAPS | DDSD_HEIGHT | DDSD_WIDTH | DDSD_PIXELFORMAT
				| DDSD_MIPMAPCOUNT | DDSD_LINEARSIZE;
		buffer.putInt(flag);
		buffer.putInt(height);
		buffer.putInt(width);
		buffer.putInt(width * height);
		buffer.putInt(0); // depth
		buffer.putInt(0); // mipmap count
		buffer.position(buffer.position() + 44); // 11 unused double-words
		buffer.putInt(32); // pixel format size
		buffer.putInt(DDPF_FOURCC);
		buffer.put((byte) 'D');
		buffer.put((byte) 'X');
		buffer.put((byte) 'T');
		buffer.put((byte) '3');
		buffer.putInt(0); // bits per pixel for RGB (non-compressed) formats
		buffer.putInt(0); // rgb bit masks for RGB formats
		buffer.putInt(0); // rgb bit masks for RGB formats
		buffer.putInt(0); // rgb bit masks for RGB formats
		buffer.putInt(0); // alpha mask for RGB formats
		buffer.putInt(DDSCAPS_TEXTURE);
		buffer.putInt(0); // ddsCaps2
		buffer.position(buffer.position() + 12); // 3 unused double-words
	}

	protected static int[] determineExtremeColors(Color[] colors) {
		int farthest = Integer.MIN_VALUE;
		int[] ex = new int[2];
		for (int i = 0; i < colors.length - 1; i++) {
			for (int j = i + 1; j < colors.length; j++) {
				int d = distance(colors[i], colors[j]);
				if (d > farthest) {
					farthest = d;
					ex[0] = i;
					ex[1] = j;
				}
			}
		}
		return ex;
	}

	protected static long computeBitMask(Color[] colors, int[] extremaIndices) {
		Color[] colorPoints = new Color[] { null, null, new Color(),
				new Color() };
		colorPoints[0] = colors[extremaIndices[0]];
		colorPoints[1] = colors[extremaIndices[1]];
		if (colorPoints[0].equals(colorPoints[1]))
			return 0;
		// colorPoints[0].r = (colorPoints[0].r & 0xF8) | (colorPoints[0].r >> 5
		// );
		// colorPoints[0].g = (colorPoints[0].g & 0xFC) | (colorPoints[0].g >> 6
		// );
		// colorPoints[0].b = (colorPoints[0].b & 0xF8) | (colorPoints[0].b >> 5
		// );
		//
		// colorPoints[1].r = (colorPoints[1].r & 0xF8) | (colorPoints[1].r >> 5
		// );
		// colorPoints[1].g = (colorPoints[1].g & 0xFC) | (colorPoints[1].g >> 6
		// );
		// colorPoints[1].b = (colorPoints[1].b & 0xF8) | (colorPoints[1].b >> 5
		// );
		colorPoints[2].r = (2 * colorPoints[0].r + colorPoints[1].r + 1) / 3;
		colorPoints[2].g = (2 * colorPoints[0].g + colorPoints[1].g + 1) / 3;
		colorPoints[2].b = (2 * colorPoints[0].b + colorPoints[1].b + 1) / 3;
		colorPoints[3].r = (colorPoints[0].r + 2 * colorPoints[1].r + 1) / 3;
		colorPoints[3].g = (colorPoints[0].g + 2 * colorPoints[1].g + 1) / 3;
		colorPoints[3].b = (colorPoints[0].b + 2 * colorPoints[1].b + 1) / 3;
		long bitmask = 0;
		for (int i = 0; i < colors.length; i++) {
			int closest = Integer.MAX_VALUE;
			int mask = 0;
			for (int j = 0; j < colorPoints.length; j++) {
				int d = distance(colors[i], colorPoints[j]);
				if (d < closest) {
					closest = d;
					mask = j;
				}
			}
			bitmask |= mask << i * 2;
		}
		return bitmask;
	}

	protected static int getPixel565(Color color) {
		int r = color.r >> 3;
		int g = color.g >> 2;
		int b = color.b >> 3;
		return r << 11 | g << 5 | b;
	}

	protected static Color getColor565(int pixel) {
		Color color = new Color();
		color.r = (int) (((long) pixel) & 0xf800) >> 11;
		color.g = (int) (((long) pixel) & 0x07e0) >> 5;
		color.b = (int) (((long) pixel) & 0x001f);
		return color;
	}

	protected static Color getColor888(int r8g8b8) {
		return new Color((int) (((long) r8g8b8) & 0xff0000) >> 16,
				(int) (((long) r8g8b8) & 0x00ff00) >> 8,
				(int) (((long) r8g8b8) & 0x0000ff));
	}

	protected static Color[] getColors888(int[] pixels) {
		Color[] colors = new Color[pixels.length];
		for (int i = 0; i < pixels.length; i++) {
			colors[i] = new Color();
			colors[i].r = (int) (((long) pixels[i]) & 0xff0000) >> 16;
			colors[i].g = (int) (((long) pixels[i]) & 0x00ff00) >> 8;
			colors[i].b = (int) (((long) pixels[i]) & 0x0000ff);
		}
		return colors;
	}

	protected static int distance(Color ca, Color cb) {
		return (cb.r - ca.r) * (cb.r - ca.r) + (cb.g - ca.g) * (cb.g - ca.g)
				+ (cb.b - ca.b) * (cb.b - ca.b);
	}

	protected static void equalTransparentCase(TransparentColor[] colors,
			int[] extremaIndices, short value) {
		// we want extremaIndices[0] to be greater than extremaIndices[1]
		if (value == 0) {
			// transparent
			colors[extremaIndices[0]] = TransparentColor.OFF_TRANSPARENT;
		}
		/*
		 * else { // not transparent anywhere - it's all one colour, so we don't
		 * need to bother making changes }
		 */
	}

	protected static int distance(TransparentColor ca, TransparentColor cb) {
		return (cb.r - ca.r) * (cb.r - ca.r) + (cb.g - ca.g) * (cb.g - ca.g)
				+ (cb.b - ca.b) * (cb.b - ca.b) + (cb.a - ca.a) * (cb.a - ca.a);
	}

	// public static void main(String[] args)
	// {
	// try
	// {
	// String fileName = "testdata/0000_0001";
	// ByteBuffer buffer = convertToDxt1NoTransparency(new File(fileName +
	// ".jpg"));
	// buffer.rewind();
	// FileOutputStream fos = new FileOutputStream(fileName + ".dds");
	// channels.FileChannel channel = fos.getChannel();
	// channel.write(buffer);
	// }
	// catch (IOException e)
	// {
	// e.printStackTrace();
	// }
	//
	// return;
	// }
	protected static long computeBitMask(TransparentColor[] colors,
			int[] extremaIndices) {
		TransparentColor[] colorPoints = { null, null, new TransparentColor(),
				new TransparentColor() };
		colorPoints[0] = colors[extremaIndices[0]];
		colorPoints[1] = colors[extremaIndices[1]];
		colorPoints[2].r = (colorPoints[0].r + colorPoints[1].r) / 2;
		colorPoints[2].g = (colorPoints[0].g + colorPoints[1].g) / 2;
		colorPoints[2].b = (colorPoints[0].b + colorPoints[1].b) / 2;
		colorPoints[2].a = 1;
		colorPoints[3].r = 0;
		colorPoints[3].g = 0;
		colorPoints[3].b = 0;
		colorPoints[3].a = 0;
		long bitmask = 0;
		for (int i = 0; i < colors.length; i++) {
			int closest = Integer.MAX_VALUE;
			int mask = 0;
			if (colors[i].a == 0) {
				mask = 3;
			} else {
				for (int j = 0; j < colorPoints.length; j++) {
					int d = distance(colors[i], colorPoints[j]);
					if (d < closest) {
						closest = d;
						mask = j;
					}
				}
			}
			bitmask |= mask << i * 2;
		}
		return bitmask;
	}

	protected static short getShort5551(TransparentColor color) {
		short s = 0;
		s |= ((color.r & 0x0f8) << 8) | ((color.g & 0x0f8) << 4)
				| ((color.b & 0x0f8) >> 3) | ((color.a & 0x0f8) >> 7);
		// System.out.println(Integer.toBinaryString(s));
		return s;
	}

	protected static int[] determineExtremeColors(TransparentColor[] colors) {
		int farthest = Integer.MIN_VALUE;
		int[] ex = { 0, 0 };
		for (int i = 0; i < colors.length - 1; i++) {
			for (int j = i + 1; j < colors.length; j++) {
				int d = distance(colors[i], colors[j]);
				if (d > farthest) {
					farthest = d;
					ex[0] = i;
					ex[1] = j;
				}
			}
		}
		return ex;
	}

	protected static TransparentColor[] getColors5551(int[] pixels) {
		TransparentColor colors[] = new TransparentColor[pixels.length];
		for (int i = 0; i < pixels.length; i++) {
			colors[i] = generateColor5551(pixels[i]);
		}
		return colors;
	}

	protected static TransparentColor generateColor5551(int pixel) {
		short alpha = (short) (pixel >> 24);
		if ((alpha & 0xf0) == 0) {
			return TransparentColor.TRANSPARENT;
		}
		// ok, it's not transparent - that's already been ruled out.
		TransparentColor tc = new TransparentColor();
		tc.a = 0x000000ff;
		tc.r = (pixel & 0x00ff0000) >> 16;
		tc.g = (pixel & 0x0000ff00) >> 8;
		tc.b = (pixel & 0x000000ff);
		return tc;
	}

	protected static class TransparentColor {
		private static final TransparentColor TRANSPARENT = new TransparentColor(
				0, 0, 0, 0);
		private static final TransparentColor OFF_TRANSPARENT = new TransparentColor(
				0, 0, 1, 0);
		private int r, g, b, a;

		private TransparentColor() {
		}

		private TransparentColor(int r, int g, int b, int a) {
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
		}

		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			final TransparentColor that = (TransparentColor) o;
			if (a != that.a)
				return false;
			if (b != that.b)
				return false;
			if (g != that.g)
				return false;
			// noinspection RedundantIfStatement
			if (r != that.r)
				return false;
			return true;
		}

		public int hashCode() {
			int result;
			result = r;
			result = 29 * result + g;
			result = 29 * result + b;
			result = 29 * result + a;
			return result;
		}

		public String toString() {
			return "TransColor argb: " + this.a + ", " + this.r + ", " + this.g
					+ ", " + this.b;
		}
	}
}
