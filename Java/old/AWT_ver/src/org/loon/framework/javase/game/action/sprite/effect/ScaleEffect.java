package org.loon.framework.javase.game.action.sprite.effect;

import org.loon.framework.javase.game.action.sprite.ISprite;
import org.loon.framework.javase.game.core.LObject;
import org.loon.framework.javase.game.core.geom.RectBox;
import org.loon.framework.javase.game.core.graphics.LImage;
import org.loon.framework.javase.game.core.graphics.device.LGraphics;
import org.loon.framework.javase.game.core.timer.LTimer;

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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
/**
 * 缩放特效，扩大或缩小指定图像
 */
public class ScaleEffect extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private float alpha;

	private int width, height;

	private boolean visible, complete;

	private LTimer timer;

	private LImage image;

	private int count;

	private int maxcount = 20;

	private int centerX, centerY;

	private boolean flag;

	public ScaleEffect(String fileName, boolean f) {
		this(new LImage(fileName), f);
	}

	public ScaleEffect(LImage t, boolean f) {
		this.image = t;
		this.width = t.getWidth();
		this.height = t.getHeight();
		this.timer = new LTimer(100);
		this.visible = true;
		if (f) {
			this.count = maxcount;
			this.flag = f;
		}
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public boolean isComplete() {
		return complete;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public void update(long elapsedTime) {
		if (complete) {
			return;
		}
		if (flag) {
			if (this.count <= 0) {
				this.complete = true;
			}
			if (timer.action(elapsedTime)) {
				count--;
			}
		} else {
			if (this.count >= this.maxcount) {
				this.complete = true;
			}
			if (timer.action(elapsedTime)) {
				count++;
			}
		}
	}

	public void createUI(LGraphics g) {
		if (!visible) {
			return;
		}
		if (complete) {
			if (!flag) {
				if (alpha > 0 && alpha < 1) {
					g.setAlpha(alpha);
				}
				g.drawImage(this.image, x(), y(), width, height);
				if (alpha > 0 && alpha < 1) {
					g.setAlpha(1f);
				}
			}
			return;
		}
		if (this.centerX < 0) {
			this.centerX = (width / 2);
		}
		if (this.centerY < 0) {
			this.centerY = (height / 2);
		}
		final float partx = this.centerX / this.maxcount;
		final float party = this.centerY / this.maxcount;
		final float partWidth = (width - this.centerX) / this.maxcount;
		final float partHeight = (height - this.centerY) / this.maxcount;
		final int x = (int) (this.centerX - this.count * partx) + x();
		final int y = (int) (this.centerY - this.count * party) + y();
		final int width = (int) (this.centerX + this.count * partWidth);
		final int height = (int) (this.centerY + this.count * partHeight);
		if (alpha > 0 && alpha < 1) {
			g.setAlpha(alpha);
		}
		g.drawImage(this.image, x, y, width, height);
		if (alpha > 0 && alpha < 1) {
			g.setAlpha(1f);
		}
	}

	public void reset() {
		this.complete = false;
		this.count = 0;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public float getAlpha() {
		return alpha;
	}

	public int getCenterX() {
		return centerX;
	}

	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}

	public int getCenterY() {
		return centerY;
	}

	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}

	public int getMaxCount() {
		return maxcount;
	}

	public void setMaxCount(int maxcount) {
		this.maxcount = maxcount;
	}

	public LImage getBitmap() {
		return image;
	}

	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void dispose() {
		if (image != null) {
			image.dispose();
			image = null;
		}
	}

}
