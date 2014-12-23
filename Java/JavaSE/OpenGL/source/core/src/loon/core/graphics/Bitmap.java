package loon.core.graphics;

import loon.core.graphics.device.LGraphics;

public class Bitmap {

	protected LImage bufferedImage;

	public static class Config {
		public static final int RGB_565 = 0;
		public static final int ARGB_8888 = 1;
	}

	public static Bitmap createBitmap(int width, int height) {
		return new Bitmap(width, height);
	}

	public static Bitmap createBitmap(int width, int height, int type) {
		switch (type) {
		case Config.RGB_565:
			return new Bitmap(new LImage(width, height, true));
		default:
			return new Bitmap(new LImage(width, height, false));
		}
	}

	public Bitmap(String filename) {
		this(new LImage(filename));
	}

	Bitmap(int width, int height) {
		this.bufferedImage = new LImage(width, height, true);
	}

	Bitmap(LImage image) {
		this.bufferedImage = image;
	}

	public static Bitmap createBitmap(Bitmap image, int x, int y, int width,
			int height) {
		LImage cliped = new LImage(width, height, true);
		LGraphics g = cliped.getLGraphics();
		g.drawImage(image.bufferedImage, -x, -y);
		return new Bitmap(cliped);
	}

	public static Bitmap createScaledBitmap(Bitmap image, int width,
			int height, boolean flag) {
		return new Bitmap(image.bufferedImage.scaledInstance(width, height));
	}

	public void setBackgroundColor(int color) {
		LGraphics graphics = bufferedImage.getLGraphics();
		graphics.setColor(color);
		graphics.fillRect(0, 0, bufferedImage.getWidth(),
				bufferedImage.getHeight());
		graphics.dispose();
	}

	public int getHeight() {
		return bufferedImage == null ? 0 : this.bufferedImage.getHeight();
	}

	public int getWidth() {
		return bufferedImage == null ? 0 : this.bufferedImage.getWidth();
	}

	public void recycle() {
		if (bufferedImage != null) {
			bufferedImage.dispose();
		}
	}

	public void getPixels(int[] pixels, int offset, int stride, int x, int y,
			int width, int height) {
		if (bufferedImage != null) {
			bufferedImage
					.getPixels(pixels, offset, stride, x, y, width, height);
		}
	}

	public int hashCode() {
		return bufferedImage == null ? 0 : bufferedImage.hashCode();
	}

	public long getRowBytes() {
		return bufferedImage == null ? 0 : bufferedImage.getWidth() * 4;
	}

	public boolean isRecycled() {
		return bufferedImage.isClose();
	}

	public boolean isValid() {
		return bufferedImage != null && !bufferedImage.isClose();
	}
}
