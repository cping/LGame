package loon.action.sprite.effect;

import loon.action.sprite.ISprite;
import loon.core.LObject;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.graphics.GraphicsUtils;
import loon.core.graphics.LColor;
import loon.core.graphics.LImage;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.timer.LTimer;
import loon.jni.NativeSupport;
import loon.utils.CollectionUtils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;

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
// 这是一个根据导入的图片黑白象素分布来完成渐变效果的特殊类，根据导入的渐变图不同，能够衍生出无穷多的渐变效果。
// 此种方式在吉里吉里(krkr)，nscript等AVG游戏引擎中较常使用。（因此，也可以直接套用它们的渐变图）
public class PShadowEffect extends LObject implements ISprite {

	private static final long serialVersionUID = 1L;

	private LTimer timer = new LTimer(100);

	private int x, y, width, height, scaleWidth, scaleHeight, pixelCount,
			layer;

	static int[] deasilTrans, widdershinTrans;

	private int[] nowDrawPixels, finalDrawPixels;

	private int[] backgroundPixels, finalBackgroundPixels;

	private boolean visible = true, flag = true, isDirty, isClose;

	private LTexture texture;

	private LImage image;

	private final static int max_pixel = 256;

	private final static int min_pixel = 0;

	private int pixelSkip = 8;

	public PShadowEffect(String fileName) {
		this(GraphicsUtils.loadBitmap(fileName, Config.RGB_565));
	}

	public PShadowEffect(Bitmap img) {
		this(img, 0, 0);
	}

	public PShadowEffect(String fileName, String backFile) {
		this(GraphicsUtils.loadBitmap(fileName, Config.RGB_565), GraphicsUtils
				.loadBitmap(backFile, Config.RGB_565), 0, 0,
				LSystem.screenRect.width, LSystem.screenRect.height);
	}

	public PShadowEffect(Bitmap img, int x, int y) {
		this(img, null, x, y, img.getWidth(), img.getHeight());
	}

	public PShadowEffect(String fileName, int x, int y, int w, int h) {
		this(GraphicsUtils.loadBitmap(fileName, Config.RGB_565), null, x, y, w,
				h);
	}

	public PShadowEffect(Bitmap img, Bitmap back, int x, int y) {
		this(img, back, x, y, img.getWidth(), img.getHeight());
	}

	public PShadowEffect(String fileName, String bacFile, int x, int y, int w,
			int h) {
		this(GraphicsUtils.loadBitmap(fileName, Config.RGB_565), GraphicsUtils
				.loadBitmap(bacFile, Config.RGB_565), x, y, w, h);
	}

	public PShadowEffect(String fileName, Bitmap back, int x, int y, int w,
			int h) {
		this(GraphicsUtils.loadBitmap(fileName, Config.RGB_565), back, x, y, w,
				h);
	}

	private PixelThread pixelThread;

	public PShadowEffect(Bitmap img, Bitmap back, int x, int y, int w, int h) {
		if (deasilTrans == null || widdershinTrans == null) {
			deasilTrans = new int[max_pixel];
			for (int i = 0; i < max_pixel; i++) {
				deasilTrans[i] = Color.rgb(i, i, i);
			}
			int flag = 0;
			widdershinTrans = new int[max_pixel];
			for (int i = 0; i < max_pixel; i++) {
				widdershinTrans[flag++] = deasilTrans[i];
			}
		}
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.visible = true;
		Bitmap temp = null;
		if (NativeSupport.UseLoonNative()) {
			this.scaleWidth = width;
			this.scaleHeight = height;
		} else {
			this.scaleWidth = width / 2;
			this.scaleHeight = height / 2;
		}
		if (back == null) {
			temp = GraphicsUtils.getResize(img, scaleWidth, scaleHeight, false);
			this.texture = new LTexture(scaleWidth, scaleHeight, true);
			this.image = new LImage(scaleWidth, scaleHeight, true);
			this.finalDrawPixels = GraphicsUtils.getPixels(temp);
			this.nowDrawPixels = CollectionUtils.copyOf(finalDrawPixels);
			if (temp != null) {
				temp.recycle();
				temp = null;
			}
		} else {
			temp = GraphicsUtils.getResize(img, scaleWidth, scaleHeight, false);
			this.texture = new LTexture(scaleWidth, scaleHeight, true);
			this.image = new LImage(scaleWidth, scaleHeight, true);
			if (back.getWidth() == scaleWidth
					&& back.getHeight() == scaleHeight) {
				this.finalBackgroundPixels = GraphicsUtils.getPixels(back);
				this.backgroundPixels = CollectionUtils
						.copyOf(finalBackgroundPixels);
			} else {
				Bitmap tmp = GraphicsUtils.getResize(back, scaleWidth,
						scaleHeight, true);
				this.finalBackgroundPixels = GraphicsUtils.getPixels(tmp);
				if (tmp != null) {
					tmp.recycle();
					tmp = null;
				}
				this.backgroundPixels = CollectionUtils
						.copyOf(finalBackgroundPixels);
			}
			this.finalDrawPixels = GraphicsUtils.getPixels(temp);
			this.nowDrawPixels = CollectionUtils.copyOf(finalDrawPixels);
		}
		if (img.getConfig() != Config.ARGB_8888) {
			for (int i = 0; i < finalDrawPixels.length; i++) {
				int c = Color.red(finalDrawPixels[i]);
				finalDrawPixels[i] = Color.rgb(c, c, c);
			}
		}
		this.setBlackToWhite(flag);
		if (temp != null) {
			temp.recycle();
			temp = null;
		}
		if (img != null) {
			img.recycle();
			img = null;
		}
		if (back != null) {
			back.recycle();
			back = null;
		}
	}

	public void reset() {
		if (isClose) {
			return;
		}
		if (flag) {
			pixelCount = min_pixel;
		} else {
			pixelCount = max_pixel;
		}
		this.visible = true;
		this.nowDrawPixels = CollectionUtils.copyOf(finalDrawPixels);
		this.backgroundPixels = CollectionUtils.copyOf(finalBackgroundPixels);
		this.startUsePixelThread();
	}

	@Override
	public void createUI(GLEx g) {
		if (isClose) {
			return;
		}
		if (!visible) {
			return;
		}
		synchronized (texture) {
			if (alpha > 0 && alpha < 1) {
				g.setAlpha(alpha);
			}
			if (!isComplete() && isDirty) {
				g.copyImageToTexture(texture, image, 0, 0);
				g.drawTexture(texture, x, y, width, height);
				isDirty = false;
			} else if (!isComplete()) {
				g.drawTexture(texture, x, y, width, height);
			}
			if (alpha > 0 && alpha < 1) {
				g.setAlpha(1f);
			}
		}
	}

	private long elapsed;

	@Override
	public void update(long elapsedTime) {
		this.elapsed = elapsedTime;
	}

	private final static int BLACK = LColor.black.getRGB();

	private final static int WHITE = LColor.white.getRGB();

	private class PixelThread extends Thread {
		@Override
		public void run() {
			for (; !isClose && !isComplete();) {
				if (image == null) {
					return;
				}
				if (visible && timer.action(elapsed)) {
					if (backgroundPixels == null) {
						if (flag) {
							NativeSupport.filterColor(max_pixel, pixelCount,
									pixelCount += pixelSkip, finalDrawPixels,
									nowDrawPixels, widdershinTrans, BLACK,
									WHITE);
						} else {
							NativeSupport.filterColor(max_pixel, pixelCount,
									pixelCount -= pixelSkip, finalDrawPixels,
									nowDrawPixels, deasilTrans, BLACK, WHITE);
						}
						image.setPixels(nowDrawPixels, scaleWidth, scaleHeight);
					} else {
						if (flag) {
							NativeSupport.filterColor(max_pixel, pixelCount,
									pixelCount += pixelSkip, finalDrawPixels,
									backgroundPixels, widdershinTrans, BLACK,
									WHITE);
						} else {
							NativeSupport
									.filterColor(max_pixel, pixelCount,
											pixelCount -= pixelSkip,
											finalDrawPixels, backgroundPixels,
											deasilTrans, BLACK, WHITE);
						}
						image.setPixels(backgroundPixels, scaleWidth,
								scaleHeight);
					}
					isDirty = true;
				}
			}
		}
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	final void startUsePixelThread() {
		if (pixelThread == null) {
			pixelThread = new PixelThread();
			pixelThread.start();
		}
	}

	final void endUsePixelThread() {
		if (pixelThread != null) {
			try {
				pixelThread.interrupt();
				pixelThread = null;
			} catch (Exception ex) {
				pixelThread = null;
			}
		}
	}

	public boolean isComplete() {
		final boolean stop = flag ? (pixelCount > max_pixel)
				: (pixelCount < min_pixel);
		if (!stop) {
			startUsePixelThread();
		} else {
			endUsePixelThread();
		}
		return stop;
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	public boolean isBlackToWhite() {
		return flag;
	}

	public void setBlackToWhite(boolean flag) {
		this.flag = flag;
		if (flag) {
			pixelCount = min_pixel;
		} else {
			pixelCount = max_pixel;
		}
	}

	@Override
	public int getLayer() {
		return layer;
	}

	@Override
	public void setLayer(int layer) {
		this.layer = layer;
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(x, y, width, height);
	}

	@Override
	public int x() {
		return x;
	}

	@Override
	public int y() {
		return y;
	}

	@Override
	public LTexture getBitmap() {
		return texture;
	}

	public boolean isClose() {
		return isClose;
	}

	public int getPixelCount() {
		return pixelCount;
	}

	public int getPixelSkip() {
		return pixelSkip;
	}

	public void setPixelSkip(int pixelSkip) {
		this.pixelSkip = pixelSkip;
	}

	@Override
	public void dispose() {
		this.isClose = true;
		this.endUsePixelThread();
		this.finalDrawPixels = null;
		this.nowDrawPixels = null;
		if (texture != null) {
			texture.destroy();
			texture = null;
		}
		if (image != null) {
			image.dispose();
			image = null;
		}
	}

}
