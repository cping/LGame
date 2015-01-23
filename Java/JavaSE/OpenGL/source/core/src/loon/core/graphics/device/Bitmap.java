/**
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
 * @version 0.4.2
 */
package loon.core.graphics.device;


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

	public LImage getImage() {
		return bufferedImage;
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
