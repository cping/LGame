package org.loon.framework.javase.game.action.sprite;

import org.loon.framework.javase.game.core.LObject;
import org.loon.framework.javase.game.core.LSystem;
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
 * @version 0.1.0
 */
public class WaitSprite extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private float alpha;

	private LTimer delay;

	private boolean visible;

	private WaitAnimation wait;
	
	private int style;

	private Cycle cycle;

	public WaitSprite(int s) {
		this(s, (int) LSystem.screenRect.width, (int) LSystem.screenRect.height);
	}

	public WaitSprite(int s, int w, int h) {
		this.style = s;
		this.wait = new WaitAnimation(s, w, h);
		this.wait.setRunning(true);
		this.delay = new LTimer(120);
		this.alpha = 1.0F;
		this.visible = true;
		if (s > 1) {
			int width = w / 2;
			int height = h / 2;
			cycle = newSample(s - 2, width, height);
			if (cycle != null) {
				RectBox limit = cycle.getCollisionBox();
				setLocation(
						(w - (limit.getWidth() == 0 ? 20 : limit.getWidth())) / 2,
						(h - (limit.getHeight() == 0 ? 20 : limit.getHeight())) / 2);
			}
		}
	}

	private final static Cycle newSample(int type, float srcWidth,
			float srcHeight) {
		float width = 1;
		float height = 1;
		float offset = 0;
		int padding = 0;
		switch (type) {
		case 0:
			width = 100;
			height = 40;
			if (srcWidth < srcHeight) {
				offset = 0;
			} else {
				offset = 8;
			}
			break;
		case 1:
			width = 30;
			height = 30;
			if (srcWidth < srcHeight) {
				offset = 0;
			} else {
				offset = 6;
			}
			break;
		case 2:
			width = 80;
			height = 80;
			offset = 14;
			padding = -15;
			break;
		case 3:
			width = 100;
			height = 100;
			if (srcWidth < srcHeight) {
				offset = -4;
			}
			break;
		case 4:
			width = 60;
			height = 60;
			offset = 12;
			if (srcWidth < srcHeight) {
				padding = -60;
			} else {
				padding = -80;
			}
			break;
		case 5:
			width = 60;
			height = 60;
			offset = 12;
			if (srcWidth < srcHeight) {
				padding = -60;
			} else {
				padding = -80;
			}
			break;
		case 6:
			width = 80;
			height = 80;
			if (srcWidth < srcHeight) {
				offset = -2;
				padding = -20;
			} else {
				padding = -30;
			}
			break;
		default:
			return null;
		}
		return Cycle.getSample(type, srcWidth, srcHeight, width, height,
				offset, padding);
	}

	public void createUI(LGraphics g) {
		if (!visible) {
			return;
		}
		if (style < 2) {
			if (alpha > 0.1 && alpha < 1.0) {
				g.setAlpha(alpha);
				wait.draw(g, x(), y());
				g.setAlpha(1.0F);
			} else {
				wait.draw(g, x(), y());
			}
		} else {
			if (cycle != null) {
				cycle.createUI(g);
			}
		}
	}

	public int getHeight() {
		if (cycle != null) {
			return cycle.getCollisionBox().height;
		} else {
			return wait.getHeight();
		}
	}

	public int getWidth() {
		if (cycle != null) {
			return cycle.getCollisionBox().width;
		} else {
			return wait.getWidth();
		}
	}

	public void update(long elapsedTime) {
		if (!visible) {
			return;
		}
		if (cycle != null) {
			if (cycle.x() != x() || cycle.y() != y()) {
				cycle.setLocation(x(), y());
			}
			cycle.update(elapsedTime);
		} else {
			if (delay.action(elapsedTime)) {
				wait.next();
			}
		}
	}

	public void setAlpha(float alpha) {
		if (cycle != null) {
			cycle.setAlpha(alpha);
		} else {
			this.alpha = alpha;
		}
	}

	public float getAlpha() {
		if (cycle != null) {
			return cycle.getAlpha();
		} else {
			return alpha;
		}
	}

	public RectBox getCollisionBox() {
		if (cycle != null) {
			return cycle.getCollisionBox();
		} else {
			return getRect(x(), y(), getWidth(), getHeight());
		}
	}

	public boolean isVisible() {
		return cycle != null ? cycle.isVisible() : visible;
	}

	public void setVisible(boolean visible) {
		if (cycle != null) {
			cycle.setVisible(visible);
		} else {
			this.visible = visible;
		}
	}

	public LImage getBitmap() {
		return null;
	}

	public void dispose() {

	}

}
