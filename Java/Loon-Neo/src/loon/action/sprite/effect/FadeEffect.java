/**
 * Copyright 2008 - 2010
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

import loon.LObject;
import loon.LSystem;
import loon.LTexture;
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.opengl.GLEx;

public class FadeEffect extends LObject<ISprite> implements BaseEffect, ISprite {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LColor color;

	public float time;

	public float currentFrame;

	public int type;

	public boolean finished;

	private float opacity;

	private int offsetX, offsetY;

	private int width;

	private int height;

	private boolean visible;

	public static FadeEffect getInstance(int type, LColor c) {
		return getInstance(type, c, LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	public static FadeEffect getInstance(int type, int timer, LColor c) {
		return new FadeEffect(c, timer, type, LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	public static FadeEffect getInstance(int type, LColor c, int w, int h) {
		return new FadeEffect(c, 120, type, w, h);
	}

	public FadeEffect(int type, LColor c) {
		this(c, 120, type, LSystem.viewSize.getWidth(), LSystem.viewSize
				.getHeight());
	}

	public FadeEffect(LColor c, int delay, int type, int w, int h) {
		this.visible = true;
		this.type = type;
		this.setDelay(delay);
		this.setColor(c);
		this.width = w;
		this.height = h;
	}

	public float getDelay() {
		return time;
	}

	public void setDelay(int delay) {
		this.time = delay;
		if (type == TYPE_FADE_IN) {
			this.currentFrame = this.time;
		} else {
			this.currentFrame = 0;
		}
	}

	public LColor getColor() {
		return color;
	}

	public void setColor(LColor color) {
		this.color = color;
	}

	public float getCurrentFrame() {
		return currentFrame;
	}

	public void setCurrentFrame(float currentFrame) {
		this.currentFrame = currentFrame;
	}

	@Override
	public boolean isCompleted() {
		return finished;
	}

	public void setStop(boolean finished) {
		this.finished = finished;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public void setVisible(boolean visible) {
		this.opacity = visible ? 255 : 0;
		this.visible = visible;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public float getOpacity() {
		return opacity;
	}

	@Override
	public void createUI(GLEx g) {
		createUI(g, 0, 0);
	}

	@Override
	public void createUI(GLEx g, float sx, float sy) {
		if (!visible) {
			return;
		}
		if (finished) {
			return;
		}
		float op = (currentFrame / time);
		setOpacity(op);
		if (opacity > 0) {
			int old = g.color();
			g.setColor(color.r, color.g, color.b, opacity);
			g.fillRect(offsetX + this.x() + sx, offsetY + this.y() + sy, width,
					height);
			g.setColor(old);
			return;
		}
	}

	@Override
	public void update(long timer) {
		if (type == TYPE_FADE_IN) {
			currentFrame--;
			if (currentFrame == 0) {
				setOpacity(0);
				finished = true;
			}
		} else {
			currentFrame++;
			if (currentFrame == time) {
				setOpacity(0);
				finished = true;
			}
		}
	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(x(), y(), getWidth(), getHeight());
	}

	@Override
	public float getHeight() {
		return height;
	}

	@Override
	public float getWidth() {
		return width;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	public int getFadeType() {
		return type;
	}

	@Override
	public LTexture getBitmap() {
		return null;
	}

	@Override
	public void close() {
		visible = false;
		finished = true;
	}

}
