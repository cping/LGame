package loon.action.sprite.effect;

import loon.action.sprite.ISprite;
import loon.core.LObject;
import loon.core.geom.RectBox;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.timer.LTimer;



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
/**
 * 0.3.2起新增类，百叶窗特效 0--竖屏,1--横屏
 */
public class CrossEffect extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int width, height;

	private boolean visible, complete;

	private LTexture otexture, ntexture;

	private LTimer timer;

	private int count, code;

	private int maxcount = 16;

	private int part;

	private int left;

	private int right;

	private LTexture tmp;

	public CrossEffect(int c, String fileName) {
		this(c, new LTexture(fileName));
	}

	public CrossEffect(int c, String file1, String file2) {
		this(c, new LTexture(file1), new LTexture(file2));
	}

	public CrossEffect(int c, LTexture o) {
		this(c, o, null);
	}

	public CrossEffect(int c, LTexture o, LTexture n) {
		this.code = c;
		this.otexture = o;
		this.ntexture = n;
		this.width = o.getWidth();
		this.height = o.getHeight();
		if (width > height) {
			maxcount = 16;
		} else {
			maxcount = 8;
		}
		this.timer = new LTimer(160);
		this.visible = true;
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

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void update(long elapsedTime) {
		if (complete) {
			return;
		}
		if (this.count > this.maxcount) {
			this.complete = true;
		}
		if (timer.action(elapsedTime)) {
			count++;
		}
	}

	@Override
	public void createUI(GLEx g) {
		if (!visible) {
			return;
		}
		if (complete) {
			if (ntexture != null) {
				if (alpha > 0 && alpha < 1) {
					g.setAlpha(alpha);
				}
				g.drawTexture(ntexture, x(), y());
				if (alpha > 0 && alpha < 1) {
					g.setAlpha(1f);
				}
			}
			return;
		}
		if (alpha > 0 && alpha < 1) {
			g.setAlpha(alpha);
		}
		part = 0;
		left = 0;
		right = 0;
		tmp = null;
		switch (code) {
		default:
			part = width / this.maxcount / 2;
			for (int i = 0; i <= this.maxcount; i++) {
				if (i <= this.count) {
					tmp = this.ntexture;
					if (tmp == null) {
						continue;
					}
				} else {
					tmp = this.otexture;
				}
				tmp.glBegin();
				left = i * 2 * part;
				right = width - ((i + 1) * 2 - 1) * part;
				tmp.draw(x() + left, y(), part, height, left, 0, left + part,
						height);
				tmp.draw(x() + right, y(), part, height, right, 0,
						right + part, height);
				tmp.glEnd();
			}
			break;
		case 1:
			part = height / this.maxcount / 2;
			for (int i = 0; i <= this.maxcount; i++) {
				if (i <= this.count) {
					tmp = this.ntexture;
					if (tmp == null) {
						continue;
					}
				} else {
					tmp = this.otexture;
				}
				int up = i * 2 * part;
				int down = height - ((i + 1) * 2 - 1) * part;
				tmp.glBegin();
				tmp.draw(0, up, width, part, 0, up, width, up + part);
				tmp.draw(0, down, width, part, 0, down, width, down + part);
				tmp.glEnd();
			}
			break;
		}
		if (alpha > 0 && alpha < 1) {
			g.setAlpha(1f);
		}
	}

	public void reset() {
		this.complete = false;
		this.count = 0;
	}

	@Override
	public LTexture getBitmap() {
		return otexture;
	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public int getMaxCount() {
		return maxcount;
	}

	public void setMaxCount(int maxcount) {
		this.maxcount = maxcount;
	}

	@Override
	public void dispose() {
		if (otexture != null) {
			otexture.destroy();
			otexture = null;
		}
		if (ntexture != null) {
			ntexture.destroy();
			ntexture = null;
		}
	}

}
