package loon.action.scripting.pack;

import loon.core.LObject;
import loon.core.geom.RectBox;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexturePack;
import loon.core.timer.LTimer;
import loon.utils.MathUtils;


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
public class PackSprite extends LObject {

	public static interface DirectionListener {

		public void update(PackSprite spr, int direction);

	}

	private DirectionListener listener;

	private int direction;

	private PackAnimation animation;

	boolean visible = true;

	private int frame;

	private float width, height;

	private float subX, subY, subWidth, subHeight;

	private LColor color;

	private LTimer timer;

	public PackSprite(PackAnimation a) {
		setAnimation(a);
	}

	public void sub(float x, float y, float w, float h) {
		this.subX = x;
		this.subY = y;
		this.subWidth = w;
		this.subHeight = h;
	}

	public void setSize(float w, float h) {
		this.width = w;
		this.height = h;
	}

	public void setFrame(int frame) {
		this.frame = frame;
	}

	public int getFrame() {
		return frame;
	}

	protected void nextFrame() {
		frame++;
	}

	protected void prevFrame() {
		frame--;
	}

	protected void reset() {
		frame = 0;
	}

	public int getWidth() {
		return (width == 0 ? animation.getFrame(frame).getWidth() : (int) width);
	}

	public int getHeight() {
		return (height == 0 ? animation.getFrame(frame).getHeight()
				: (int) height);
	}

	public RectBox getCollisionBox() {
		return getRect(x(), y(), getWidth(), getHeight());
	}

	public int getLeft() {
		return MathUtils.round(getX());
	}

	public int getTop() {
		return MathUtils.round(getY());
	}

	public boolean collidesWith(PackSprite s) {
		return ((getLeft() < (s.getLeft() + s.getWidth()))
				&& ((getLeft() + getWidth()) > s.getLeft())
				&& (getTop() < (s.getTop() + s.getHeight())) && ((getTop() + getHeight()) > s
				.getTop()));
	}

	public void setDelay(long d) {
		timer.setDelay(d);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public void setAnimation(PackAnimation animation) {
		this.animation = animation;
		if (timer == null) {
			this.timer = new LTimer(150);
		}
		this.visible = true;
	}

	public void draw(LTexturePack pack, PackView view) {
		PackFrame o = animation.getFrame(frame);
		final float rx = view.worldToRealX(getX());
		final float ry = view.worldToRealY(getY());
		if (o.flag) {
			if (subWidth == 0 && subHeight == 0 && width != 0 && height != 0) {
				pack.draw(o.id, rx, ry, width, height, rotation, color);
			} else if (subX != 0 || subY != 0 || subWidth != 0
					|| subHeight != 0) {
				pack.draw(o.id, rx, ry, width, height, subX, subY, subWidth,
						subHeight, rotation, color);
			} else {
				pack.draw(o.id, rx, ry, rotation, color);
			}
		} else {
			if (subWidth == 0 && subHeight == 0 && width != 0 && height != 0) {
				pack.draw(o.name, rx, ry, width, height, rotation, color);
			} else if (subX != 0 || subY != 0 || subWidth != 0
					|| subHeight != 0) {
				pack.draw(o.name, rx, ry, width, height, subX, subY, subWidth,
						subHeight, rotation, color);
			} else {
				pack.draw(o.name, rx, ry, rotation, color);
			}
		}
	}

	public void update(long elapsedTime) {
		if (timer.action(elapsedTime)) {
			nextFrame();
		}
	}

	public float getUWidth() {
		return width;
	}

	public float getUHeight() {
		return height;
	}

	public float getSubHeight() {
		return subHeight;
	}

	public float getSubWidth() {
		return subWidth;
	}

	public float getSubX() {
		return subX;
	}

	public float getSubY() {
		return subY;
	}

	public LColor getColor() {
		return color;
	}

	public void setColor(LColor color) {
		this.color = color;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int d) {
		this.direction = d;
		if (listener != null) {
			listener.update(this, d);
		}
	}

	public DirectionListener getDirectionListener() {
		return listener;
	}

	public void setDirectionListener(DirectionListener listener) {
		this.listener = listener;
	}

}
