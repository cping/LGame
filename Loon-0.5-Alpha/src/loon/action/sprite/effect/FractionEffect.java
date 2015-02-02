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

import java.util.Random;

import loon.LSystem;
import loon.action.sprite.ISprite;
import loon.core.LObject;
import loon.core.geom.RectBox;
import loon.core.graphics.device.LImage;
import loon.core.graphics.device.LPixmapData;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.processes.RealtimeProcess;
import loon.core.processes.RealtimeProcessManager;
import loon.core.timer.LTimer;
import loon.jni.NativeSupport;
import loon.utils.MathUtils;

public class FractionEffect extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int maxElements = 6;

	private LTimer timer = new LTimer(40);

	private int width, height, scaleWidth, scaleHeight, size;

	private float expandLimit = 1.2f;

	private int exWidth, exHeigth;

	// 0 = x
	// 1 = y
	// 2 = vx
	// 3 = vy
	// 4 = color
	// 5 = countToCrush;
	private float[] fractions;

	private LPixmapData pixmap;

	private boolean isClose, isVisible;

	private int loopCount, loopMaxCount = 16;

	private long elapsed;

	private LTexture tmp;

	private PixelProcess pixelProcess;

	public FractionEffect(String resName, boolean remove, float scale) {
		init(LTextures.loadTexture(resName), 1.2f, remove, scale);
	}

	public FractionEffect(String resName, float limit, boolean remove,
			float scale) {
		init(LTextures.loadTexture(resName), limit, remove, scale);
	}

	public FractionEffect(String resName) {
		this(resName, 1.2f);
	}

	public FractionEffect(String resName, float scale) {
		init(LTextures.loadTexture(resName), 1.2f, false, scale);
	}

	public FractionEffect(String resName, float limit, float scale) {
		init(LTextures.loadTexture(resName), limit, false, scale);
	}

	public FractionEffect(LTexture texture, float scale) {
		init(texture, 1.2f, false, scale);
	}

	public FractionEffect(LTexture texture, float limit, float scale) {
		init(texture, limit, false, scale);
	}

	public FractionEffect(LTexture texture, float limit, boolean remove,
			float scale) {
		init(texture, limit, remove, scale);
	}

	private void init(LTexture tex2d, float limit, boolean remove, float scale) {
		this.isVisible = true;
		this.expandLimit = limit;
		this.width = tex2d.getWidth();
		this.height = tex2d.getHeight();
		this.scaleWidth = (int) (width * scale);
		this.scaleHeight = (int) (height * scale);
		this.loopMaxCount = (MathUtils.max(scaleWidth, scaleHeight) / 2) + 1;
		this.fractions = new float[(scaleWidth * scaleHeight) * maxElements];
		this.exWidth = (int) (scaleWidth * expandLimit);
		this.exHeigth = (int) (scaleHeight * expandLimit);
		LImage image = tex2d.getImage().scaledInstance(scaleWidth, scaleHeight);
		int[] pixels = image.getPixels();
		if (image != null) {
			image.dispose();
			image = null;
		}
		this.size = pixels.length;
		this.pixmap = new LPixmapData(exWidth, exHeigth, true);
		int no = 0, idx = 0;
		int length = fractions.length;
		float angle = 0;
		float speed = 0;
		Random random = LSystem.random;
		for (int y = 0; y < scaleHeight; y++) {
			for (int x = 0; x < scaleWidth; x++) {
				if (idx + maxElements < length) {
					no = y * scaleWidth + x;
					angle = random.nextInt(360);
					speed = 10f / random.nextInt(30);
					fractions[idx + 0] = x;
					fractions[idx + 1] = y;
					fractions[idx + 2] = (MathUtils.cos(angle * MathUtils.PI
							/ 180) * speed);
					fractions[idx + 3] = (MathUtils.sin(angle * MathUtils.PI
							/ 180) * speed);
					fractions[idx + 4] = (pixels[no] == 0xff00 ? 0xffffff
							: pixels[no]);
					fractions[idx + 5] = x / 6 + random.nextInt(10);
					idx += maxElements;
				}
			}
		}
		if (remove) {
			if (tex2d != null) {
				tex2d.destroy();
				tex2d = null;
			}
		}
		this.tmp = tex2d;
		this.startUsePixelThread();
	}

	public void setDelay(long d) {
		timer.setDelay(d);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	private class PixelProcess extends RealtimeProcess {

		public PixelProcess() {
			setDelay(30);
		}

		public void run() {
			if (!isClose && !isComplete()) {
				if (!isVisible) {
					return;
				}
				if (timer.action(elapsed)) {
					if (pixmap.isDirty()) {
						return;
					}
					pixmap.reset();
					NativeSupport.filterFractions(size, fractions,
							pixmap.getWidth(), pixmap.getHeight(),
							pixmap.getPixels(), maxElements);
					pixmap.submit();
					loopCount++;
				}
			}
		}
	}

	final void startUsePixelThread() {
		if (pixelProcess == null || pixelProcess.isDead()) {
			pixelProcess = new PixelProcess();
			RealtimeProcessManager.get().addProcess(pixelProcess);
		}
	}

	final void endUsePixelThread() {
		if (pixelProcess != null) {
			pixelProcess.kill();
		}
	}

	public void update(long elapsedTime) {
		this.elapsed = elapsedTime;
	}

	public void createUI(GLEx g) {
		if (isClose) {
			return;
		}
		if (!isVisible) {
			return;
		}
		if (isComplete()) {
			return;
		}
		if (alpha > 0 && alpha < 1) {
			g.setAlpha(alpha);
		}
		pixmap.draw(g, x(), y(), width, height);
		if (alpha > 0 && alpha < 1) {
			g.setAlpha(1f);
		}
	}

	public void reset() {
		pixmap.reset();
		loopCount = 0;
	}

	public boolean isComplete() {
		final boolean stop = pixmap.isClose() || loopCount > loopMaxCount;
		if (!stop) {
			startUsePixelThread();
		} else {
			endUsePixelThread();
		}
		return stop;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public LTexture getBitmap() {
		return tmp;
	}

	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean visible) {
		this.isVisible = true;
	}

	public int getLoopCount() {
		return loopCount;
	}

	public void setLoopCount(int loopCount) {
		this.loopCount = loopCount;
	}

	public int getLoopMaxCount() {
		return loopMaxCount;
	}

	public void setLoopMaxCount(int loopMaxCount) {
		this.loopMaxCount = loopMaxCount;
	}

	public void dispose() {
		this.isClose = true;
		this.endUsePixelThread();
		if (pixmap != null) {
			pixmap.dispose();
		}
		if (tmp != null) {
			tmp.destroy();
			tmp = null;
		}
	}

}
