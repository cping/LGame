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
 * @version 0.1
 */
package loon.action.sprite.effect;

import loon.LSystem;
import loon.action.sprite.ISprite;
import loon.core.LObject;
import loon.core.geom.RectBox;
import loon.core.graphics.device.LColor;
import loon.core.graphics.device.LImage;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.processes.RealtimeProcess;
import loon.core.processes.RealtimeProcessManager;
import loon.core.timer.LTimer;
import loon.jni.NativeSupport;
import loon.utils.CollectionUtils;

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
		this(LImage.createImage(fileName));
	}

	public PShadowEffect(LImage img) {
		this(img, 0, 0);
	}

	public PShadowEffect(String fileName, String backFile) {
		this(LImage.createImage(fileName), LImage.createImage(backFile), 0, 0,
				LSystem.screenRect.width, LSystem.screenRect.height);
	}

	public PShadowEffect(LImage img, int x, int y) {
		this(img, null, x, y, img.getWidth(), img.getHeight());
	}

	public PShadowEffect(String fileName, int x, int y, int w, int h) {
		this(LImage.createImage(fileName), null, x, y, w, h);
	}

	public PShadowEffect(LImage img, LImage back, int x, int y) {
		this(img, back, x, y, img.getWidth(), img.getHeight());
	}

	public PShadowEffect(String fileName, String bacFile, int x, int y, int w,
			int h) {
		this(LImage.createImage(fileName), LImage.createImage(bacFile), x, y,
				w, h);
	}

	public PShadowEffect(String fileName, LImage back, int x, int y, int w,
			int h) {
		this(LImage.createImage(fileName), back, x, y, w, h);
	}

	private PixelProcess pixelProcess;

	public PShadowEffect(LImage img, LImage back, int x, int y, int w, int h) {
		if (deasilTrans == null || widdershinTrans == null) {
			deasilTrans = new int[max_pixel];
			for (int i = 0; i < max_pixel; i++) {
				deasilTrans[i] = LColor.getRGB(i, i, i);
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
		LImage temp = null;
		// 此处与Android版处理有差异，因为微机上无论如何不会慢……
		this.scaleWidth = width;
		this.scaleHeight = height;
		if (back == null) {
			temp = img.scaledInstance(scaleWidth, scaleHeight);
			this.texture = new LTexture(scaleWidth, scaleHeight, true);
			this.image = new LImage(scaleWidth, scaleHeight, true);
			this.finalDrawPixels = temp.getPixels();
			this.nowDrawPixels = CollectionUtils.copyOf(finalDrawPixels);
			if (temp != null) {
				temp.dispose();
				temp = null;
			}
		} else {
			temp = img.scaledInstance(scaleWidth, scaleHeight);
			this.texture = new LTexture(scaleWidth, scaleHeight, true);
			this.image = new LImage(scaleWidth, scaleHeight, true);
			if (back.getWidth() == scaleWidth
					&& back.getHeight() == scaleHeight) {
				this.finalBackgroundPixels = back.getPixels();
				this.backgroundPixels = CollectionUtils
						.copyOf(finalBackgroundPixels);
			} else {
				LImage tmp = back.scaledInstance(scaleWidth, scaleHeight);
				this.finalBackgroundPixels = tmp.getPixels();
				if (tmp != null) {
					tmp.dispose();
					tmp = null;
				}
				this.backgroundPixels = CollectionUtils
						.copyOf(finalBackgroundPixels);
			}
			this.finalDrawPixels = temp.getPixels();
			this.nowDrawPixels = CollectionUtils.copyOf(finalDrawPixels);
		}
		this.setBlackToWhite(flag);
		if (temp != null) {
			temp.dispose();
			temp = null;
		}
		if (img != null) {
			img.dispose();
			img = null;
		}
		if (back != null) {
			back.dispose();
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
		this.startUsePixelProcess();
	}

	private final static int BLACK = LColor.black.getRGB();

	private final static int WHITE = LColor.white.getRGB();

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

	public void update(long elapsedTime) {
		this.elapsed = elapsedTime;
	}

	private class PixelProcess extends RealtimeProcess {

		public PixelProcess() {
			setDelay(30);
		}

		public void run() {
			if (!isClose && !isComplete()) {
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

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	final void startUsePixelProcess() {
		if (pixelProcess == null || pixelProcess.isDead()) {
			pixelProcess = new PixelProcess();
			RealtimeProcessManager.get().addProcess(pixelProcess);
		}
	}

	final void endUsePixelProcess() {
		if (pixelProcess != null) {
			pixelProcess.kill();
		}
	}

	public boolean isComplete() {
		final boolean stop = flag ? (pixelCount > max_pixel)
				: (pixelCount < min_pixel);
		if (!stop) {
			startUsePixelProcess();
		} else {
			endUsePixelProcess();
		}
		return stop;
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public int getHeight() {
		return height;
	}

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

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public RectBox getCollisionBox() {
		return getRect(x, y, width, height);
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

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

	public void dispose() {
		this.isClose = true;
		this.endUsePixelProcess();
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
