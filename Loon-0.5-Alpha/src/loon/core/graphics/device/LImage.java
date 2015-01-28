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
 * @version 0.1.1
 */
package loon.core.graphics.device;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.ImageProducer;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;

import loon.JavaSEGraphicsUtils;
import loon.LSystem;
import loon.core.LRelease;
import loon.core.graphics.filetype.TGA;
import loon.core.graphics.opengl.GLLoader;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.graphics.opengl.LTexture.Format;
import loon.jni.NativeSupport;
import loon.utils.StringUtils;

public class LImage implements LRelease {

	private final static ArrayList<LImage> images = new ArrayList<LImage>(100);

	private BufferedImage bufferedImage;

	private String fileName;

	private LGraphics g;

	private int width, height;

	private boolean isClose, isUpdate, isAutoDispose = true;

	private LTexture texture;

	private Format format = Format.DEFAULT;

	public static LImage createImage(byte[] buffer) {
		return new LImage(JavaSEGraphicsUtils.toolKit.createImage(buffer));
	}

	public static LImage createImage(byte[] buffer, int imageoffset,
			int imagelength) {
		return new LImage(JavaSEGraphicsUtils.toolKit.createImage(buffer,
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
			this.bufferedImage = JavaSEGraphicsUtils.createImage(width, height,
					transparency);
		} catch (Exception e) {
			try {
				LTextures.destroyAll();
				LSystem.gc();
				this.width = width;
				this.height = height;
				this.bufferedImage = JavaSEGraphicsUtils.createImage(width,
						height, transparency);
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
		this.bufferedImage = JavaSEGraphicsUtils.createImage(width, height,
				type);
		if (!images.contains(this)) {
			images.add(this);
		}
	}

	private final static ArrayList<String> _otherImages = new ArrayList<String>(
			10);

	static {
		_otherImages.add("tga");
	}

	public final static boolean existType(String fileName) {
		if (fileName == null) {
			return false;
		}
		return _otherImages.contains(LSystem.getExtension(fileName
				.toLowerCase()));
	}

	public LImage(String fileName) {
		if (fileName == null) {
			throw new RuntimeException("file name is null !");
		}
		String res;
		if (StringUtils.startsWith(fileName, '/')) {
			res = fileName.substring(1);
		} else {
			res = fileName;
		}
		this.fileName = fileName;
		BufferedImage img = null;
		if (existType(fileName)) {
			String ext = LSystem.getExtension(fileName.toLowerCase());
			if ("tga".equals(ext)) {
				try {
					TGA.State tga = TGA.load(res);
					if (tga != null) {
						img = JavaSEGraphicsUtils.createImage(tga.width,
								tga.height, tga.type == 4 ? true : false);
						img.setRGB(0, 0, tga.width, tga.height, tga.pixels, 0,
								tga.width);
						tga.dispose();
						tga = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			img = JavaSEGraphicsUtils.loadBufferedImage(res);
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
		JavaSEGraphicsUtils.waitImage(img);
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
		this.bufferedImage = JavaSEGraphicsUtils.getBufferImage(img);
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

	public LColor getColorAt(int x, int y) {
		return new LColor(new Color(this.getRGBAt(x, y), true));
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
		isUpdate = true;
		bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);
	}

	public void setRGB(int startX, int startY, int w, int h, int[] rgbArray,
			int offset, int scansize) {
		setPixels(rgbArray, offset, scansize, startX, startY, w, h);
	}

	public void setPixels(int[] pixels, int offset, int stride, int x, int y,
			int width, int height) {
		isUpdate = true;
		bufferedImage.setRGB(x, y, width, height, pixels, offset, stride);
	}

	public int[] setPixels(int[] pixels, int x, int y, int w, int h) {
		isUpdate = true;
		bufferedImage.setRGB(x, y, w, h, pixels, 0, w);
		return pixels;
	}

	public void setPixel(LColor c, int x, int y) {
		isUpdate = true;
		bufferedImage.setRGB(x, y, c.getRGB());
	}

	public void setPixel(int rgb, int x, int y) {
		isUpdate = true;
		bufferedImage.setRGB(x, y, rgb);
	}

	public int getPixel(int x, int y) {
		return bufferedImage.getRGB(x, y);
	}

	public int getRGB(int x, int y) {
		return bufferedImage.getRGB(x, y);
	}

	public void setRGB(int rgb, int x, int y) {
		isUpdate = true;
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
		return new LImage(JavaSEGraphicsUtils.getResize(bufferedImage, w, h));
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
		return JavaSEGraphicsUtils.hashImage(bufferedImage);
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
		if (texture == null || texture.isClose() || isUpdate) {
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

	public LImage light(float v) {
		return getLightImage(this, v);
	}

	public LImage light(int v) {
		return getLightImage(this, v);
	}

	public static LImage getLightImage(LImage img, float v) {
		return getLightImage(img, (int) (v * 255));
	}

	public static LImage getLightImage(LImage img, int v) {
		LImage newImage = new LImage(img.getWidth(), img.getHeight(), true);
		LGraphics g = newImage.getLGraphics();
		g.drawImage(img, 0, 0);
		getLight(newImage, v);
		return newImage;
	}

	public static void getLight(LImage img, int v) {
		int width = img.getWidth();
		int height = img.getHeight();
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				int rgbValue = img.getRGB(x, y);
				if (rgbValue != 0) {
					int color = getLight(rgbValue, v);
					img.setRGB(color, x, y);
				}
			}
		}
	}

	public static int getLight(int color, int v) {
		int red = LColor.getRed(color);
		int green = LColor.getGreen(color);
		int blue = LColor.getBlue(color);
		red += v;
		green += v;
		blue += v;
		blue = blue > 255 ? 255 : blue;
		red = red > 255 ? 255 : red;
		green = green > 255 ? 255 : green;
		red = red < 0 ? 0 : red;
		green = green < 0 ? 0 : green;
		blue = blue < 0 ? 0 : blue;
		return LColor.getRGB(red, green, blue);
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
