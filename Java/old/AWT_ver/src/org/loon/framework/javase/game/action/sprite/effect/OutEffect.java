package org.loon.framework.javase.game.action.sprite.effect;

import org.loon.framework.javase.game.action.map.Config;
import org.loon.framework.javase.game.action.sprite.ISprite;
import org.loon.framework.javase.game.core.LObject;
import org.loon.framework.javase.game.core.LSystem;
import org.loon.framework.javase.game.core.geom.RectBox;
import org.loon.framework.javase.game.core.graphics.LImage;
import org.loon.framework.javase.game.core.graphics.device.LGraphics;

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
public class OutEffect extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private LImage image;

	private boolean visible, complete;

	private float alpha;

	private int width, height;

	private int type, multiples;

	private RectBox limit;

	public OutEffect(String fileName, int code) {
		this(new LImage(fileName), code);
	}

	public OutEffect(LImage t, int code) {
		this(t, LSystem.screenRect, code);
	}

	public OutEffect(LImage t, RectBox limit, int code) {
		this.image = t;
		this.type = code;
		this.width = t.getWidth();
		this.height = t.getHeight();
		this.multiples = 1;
		this.limit = limit;
		this.visible = true;
	}

	public void update(long elapsedTime) {
		if (!complete) {
			switch (type) {
			case Config.DOWN:
				move_45D_down(multiples);
				break;
			case Config.UP:
				move_45D_up(multiples);
				break;
			case Config.LEFT:
				move_45D_left(multiples);
				break;
			case Config.RIGHT:
				move_45D_right(multiples);
				break;
			case Config.TDOWN:
				move_down(multiples);
				break;
			case Config.TUP:
				move_up(multiples);
				break;
			case Config.TLEFT:
				move_left(multiples);
				break;
			case Config.TRIGHT:
				move_right(multiples);
				break;
			}
			if (!limit.intersects(x(), y(), width, height)) {
				complete = true;
			}
		}
	}

	public boolean isComplete() {
		return complete;
	}

	public int getHeight() {
		return width;
	}

	public int getWidth() {
		return height;
	}

	public void createUI(LGraphics g) {
		if (!visible) {
			return;
		}
		if (!complete) {
			if (alpha > 0 && alpha < 1) {
				g.setAlpha(alpha);
			}
			g.drawImage(image, x(), y());
			if (alpha > 0 && alpha < 1) {
				g.setAlpha(1);
			}
		}
	}

	public void setAlpha(float a) {
		this.alpha = a;
	}

	public float getAlpha() {
		return alpha;
	}

	public LImage getBitmap() {
		return image;
	}

	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	public int getMultiples() {
		return multiples;
	}

	public void setMultiples(int multiples) {
		this.multiples = multiples;
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
