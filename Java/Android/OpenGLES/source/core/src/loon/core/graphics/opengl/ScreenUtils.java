package loon.core.graphics.opengl;

import java.nio.IntBuffer;
import java.util.HashMap;

import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.graphics.LImage;
import loon.core.graphics.opengl.LTexture.Format;
import loon.jni.NativeSupport;


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
 * @email ceponline javachenpeng@yahoo.com
 * @version 0.1
 */
public class ScreenUtils {

	private static HashMap<Integer, ScreenCache> screenCache = new HashMap<Integer, ScreenCache>(
			10);

	private static class ScreenCache implements LRelease {

		LImage image, commit;

		IntBuffer buffer;

		int[] drawPixels;

		int x, y, width, height, trueWidth, trueHeight;

		private Runnable runnable;

		public ScreenCache(int x, int y, int width, int height) {
			this.trueWidth = width;
			this.trueHeight = height;
			this.x = (int) (x * LSystem.scaleWidth);
			this.y = (int) (y * LSystem.scaleHeight);
			this.width = (int) (width * LSystem.scaleWidth);
			this.height = (int) (height * LSystem.scaleHeight);
			this.drawPixels = new int[this.width * this.height];
			this.image = new LImage(this.width, this.height, true);
			this.buffer = NativeSupport.newIntBuffer(this.width * this.height);
		}

		public synchronized void commit() {
			if (runnable != null) {
				return;
			}
			runnable = new Runnable() {
				@Override
				public void run() {
					GLEx.gl10
							.glReadPixels(
									x,
									(int) (LSystem.screenRect.height * LSystem.scaleHeight)
											- y - height, width, height,
									GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer);
					for (int i = 0, ny = 0; i < height; i++, ny++) {
						for (int j = 0; j < width; j++) {
							final int pix = buffer.get(i * width + j);
							final int pb = (pix >> 16) & 0xff;
							final int pr = (pix << 16) & 0x00ff0000;
							final int pixel = (pix & 0xff00ff00) | pr | pb;
							drawPixels[(height - ny - 1) * width + j] = pixel;
						}
					}
					if (image == null) {
						image = new LImage(width, height, true);
					} else if (image != null && image.isClose()) {
						image.dispose();
						image = null;
						image = new LImage(width, height, true);
					}
					image.setPixels(drawPixels, width, height);
					if (image.getWidth() != trueWidth
							|| image.getHeight() != trueHeight) {
						LImage tmp = image
								.scaledInstance(trueWidth, trueHeight);
						if (image != null) {
							image.dispose();
							image = null;
						}
						commit = tmp;
					} else {
						commit = image;
					}
				}
			};
			LSystem.callScreenRunnable(runnable);
		}

		public void reset() {
			if (buffer != null) {
				buffer.rewind();
			}
			commit = null;
			runnable = null;
		}

		@Override
		public void dispose() {
			buffer = null;
			drawPixels = null;
			runnable = null;
			if (image != null) {
				image.dispose();
				image = null;
			}
			if (commit != null) {
				commit.dispose();
				commit = null;
			}
		}
	}

	/**
	 * 获得当前游戏屏幕截图
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public synchronized static LImage toScreenCaptureImage(int x, int y,
			int width, int height) {
		if (GLEx.gl10 == null) {
			return null;
		}
		if (width < 0) {
			width = 1;
		}
		if (height < 0) {
			height = 1;
		}
		if (x < 0 || x > width) {
			x = 0;
		}
		if (y < 0 || y > height) {
			y = 0;
		}
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, x);
		hashCode = LSystem.unite(hashCode, y);
		hashCode = LSystem.unite(hashCode, width);
		hashCode = LSystem.unite(hashCode, height);
		if (screenCache.size() > LSystem.DEFAULT_MAX_CACHE_SIZE / 10) {
			for (ScreenCache sc : screenCache.values()) {
				sc.dispose();
				sc = null;
			}
			screenCache.clear();
		}
		ScreenCache cache = screenCache.get(hashCode);
		if (cache == null) {
			cache = new ScreenCache(x, y, width, height);
		} else {
			cache.reset();
		}
		screenCache.put(hashCode, cache);
		cache.commit();
		for (; cache.commit == null;) {
			try {
				Thread.yield();
			} catch (Exception e) {
			}
		}
		return cache.commit;
	}

	/**
	 * 获得当前游戏屏幕截图
	 * 
	 * @return
	 */
	public static LImage toScreenCaptureImage() {
		return toScreenCaptureImage(0, 0, LSystem.screenRect.width,
				LSystem.screenRect.height);
	}

	/**
	 * 将当前游戏截屏转化为纹理
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public synchronized static LTexture toScreenCaptureTexture(int x, int y,
			int width, int height) {
		if (GLEx.gl10 == null) {
			return null;
		}
		LImage temp = toScreenCaptureImage(x, y, width, height);
		LTexture texture = new LTexture(GLLoader.getTextureData(temp),
				Format.LINEAR);
		if (temp != null && !temp.isClose()) {
			temp.dispose();
			temp = null;
		}
		return texture;
	}

	/**
	 * 将当前游戏截屏转化为纹理
	 * 
	 * @return
	 */
	public static LTexture toScreenCaptureTexture() {
		return toScreenCaptureTexture(0, 0, LSystem.screenRect.width,
				LSystem.screenRect.height);
	}

	public static void disposeAll() {
		if (screenCache != null) {
			for (ScreenCache sc : screenCache.values()) {
				sc.dispose();
				sc = null;
			}
			screenCache.clear();
		}
	}
}
