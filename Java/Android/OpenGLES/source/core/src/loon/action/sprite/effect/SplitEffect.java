package loon.action.sprite.effect;

import loon.action.map.Config;
import loon.action.sprite.ISprite;
import loon.core.LObject;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
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
public class SplitEffect extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Vector2f v1, v2;

	private int width, height, halfWidth, halfHeight, multiples, direction;

	private boolean visible, complete, special;

	private RectBox limit;

	private LTexture texture;

	private LTimer timer;

	public SplitEffect(String fileName, int d) {
		this(new LTexture(fileName), d);
	}

	public SplitEffect(LTexture t, int d) {
		this(t, LSystem.screenRect, d);
	}

	public SplitEffect(LTexture t, RectBox limit, int d) {
		this.texture = t;
		this.width = texture.getWidth();
		this.height = texture.getHeight();
		this.halfWidth = width / 2;
		this.halfHeight = height / 2;
		this.multiples = 2;
		this.direction = d;
		this.limit = limit;
		this.timer = new LTimer(10);
		this.visible = true;
		this.v1 = new Vector2f();
		this.v2 = new Vector2f();
		switch (direction) {
		case Config.UP:
		case Config.DOWN:
			special = true;
		case Config.TLEFT:
		case Config.TRIGHT:
			v1.set(0, 0);
			v2.set(halfWidth, 0);
			break;
		case Config.LEFT:
		case Config.RIGHT:
			special = true;
		case Config.TUP:
		case Config.TDOWN:
			v1.set(0, 0);
			v2.set(0, halfHeight);
			break;
		}
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

	@Override
	public void update(long elapsedTime) {
		if (!complete) {
			if (timer.action(elapsedTime)) {
				switch (direction) {
				case Config.LEFT:
				case Config.RIGHT:
				case Config.TLEFT:
				case Config.TRIGHT:
					v1.move_multiples(Config.TLEFT, multiples);
					v2.move_multiples(Config.TRIGHT, multiples);
					break;
				case Config.UP:
				case Config.DOWN:
				case Config.TUP:
				case Config.TDOWN:
					v1.move_multiples(Config.TUP, multiples);
					v2.move_multiples(Config.TDOWN, multiples);
					break;
				}

				if (special) {
					if (!limit.intersects(v1.x, v1.y, halfHeight, halfWidth)
							&& !limit.intersects(v2.x, v2.y, halfHeight,
									halfWidth)) {
						this.complete = true;
					}
				} else if (!limit.intersects(v1.x, v1.y, halfWidth, halfHeight)
						&& !limit.intersects(v2.x, v2.y, halfWidth, halfHeight)) {
					this.complete = true;
				}
			}
		}
	}

	@Override
	public void createUI(GLEx g) {
		if (!visible) {
			return;
		}
		if (!complete) {
			if (alpha > 0 && alpha < 1) {
				g.setAlpha(alpha);
			}
			final float x1 = v1.x + getX();
			final float y1 = v1.y + getX();

			final float x2 = v2.x + getX();
			final float y2 = v2.y + getX();

			texture.glBegin();
			switch (direction) {
			case Config.LEFT:
			case Config.RIGHT:
			case Config.TUP:
			case Config.TDOWN:
				texture
						.draw(x1, y1, width, halfHeight, 0, 0, width,
								halfHeight);
				texture.draw(x2, y2, width, halfHeight, 0, halfHeight, width,
						height);
				break;
			case Config.UP:
			case Config.DOWN:
			case Config.TLEFT:
			case Config.TRIGHT:
				texture
						.draw(x1, y1, halfWidth, height, 0, 0, halfWidth,
								height);
				texture.draw(x2, y2, halfWidth, height, halfWidth, 0, width,
						height);
				break;

			}
			texture.glEnd();
			if (alpha > 0 && alpha < 1) {
				g.setAlpha(1f);
			}
		}
	}

	public boolean isComplete() {
		return complete;
	}

	@Override
	public LTexture getBitmap() {
		return texture;
	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	public int getMultiples() {
		return multiples;
	}

	public void setMultiples(int multiples) {
		this.multiples = multiples;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public void dispose() {
		if (texture != null) {
			texture.destroy();
			texture = null;
		}
	}

}
