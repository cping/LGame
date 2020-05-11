/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.action.sprite.effect;

import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 以指定坐标为中心点,出现像素风斩击效果
 * 
 * <pre>
 * // 构建一个斩击效果,中心点200,200,宽2,长25
 * add(new PixelChopEffect(LColor.red, 200, 200, 2, 25));
 * </pre>
 */
public class PixelChopEffect extends PixelBaseEffect {

	public static enum ChopDirection {
		/**
		 * West North To East South
		 */
		WNTES,
		/**
		 * North East to South West
		 */
		NETSW,
		/**
		 * Top to Bottom
		 */
		TTB,
		/**
		 * Left to Right
		 */
		LTR;
	}

	private ChopDirection direction;

	private float viewX, viewY;

	private float width;

	private int mode;

	public PixelChopEffect(LColor color, float x, float y) {
		this(ChopDirection.WNTES, color, 0, x, y, 2);
	}

	public PixelChopEffect(LColor color, float x, float y, int frameLimit) {
		this(ChopDirection.WNTES, color, 0, x, y, 2, 25);
	}

	public PixelChopEffect(LColor color, int mode, float x, float y) {
		this(ChopDirection.WNTES, color, mode, x, y, 2);
	}

	public PixelChopEffect(LColor color, int mode, float x, float y, int frameLimit) {
		this(ChopDirection.WNTES, color, mode, x, y, 2, 25);
	}

	public PixelChopEffect(ChopDirection dir, LColor color, int mode, float x, float y) {
		this(dir, color, mode, x, y, 2);
	}

	public PixelChopEffect(ChopDirection dir, LColor color, float x, float y, int frameLimit) {
		this(dir, color, 0, x, y, 2, 25);
	}

	public PixelChopEffect(ChopDirection dir, LColor color, int mode, float x, float y, int frameLimit) {
		this(dir, color, mode, x, y, 2, 25);
	}

	public PixelChopEffect(LColor color, int x, int y, int width, int frameLimit) {
		this(ChopDirection.WNTES, color, 0, x, y, width, frameLimit);
	}
	
	public PixelChopEffect(ChopDirection dir, LColor color, float x, float y, float width, int frameLimit) {
		this(dir, color, 0, x, y, width, frameLimit);
	}

	public PixelChopEffect(LColor color, float x, float y, float width, int frameLimit) {
		this(ChopDirection.WNTES, color, 0, x, y, width, frameLimit);
	}

	public PixelChopEffect(LColor color, int mode, float x, float y, float width, int frameLimit) {
		this(ChopDirection.WNTES, color, mode, x, y, width, frameLimit);
	}

	public PixelChopEffect(ChopDirection dir, LColor color, int mode, float x, float y, float width, int frameLimit) {
		super(color, x, y, 0, 0);
		this.direction = dir;
		this.mode = mode;
		this.width = width;
		this.viewX = x;
		this.viewY = y;
		this.limit = frameLimit;
		setDelay(0);
		setEffectDelay(0);
	}

	@Override
	public void draw(GLEx g, float tx, float ty) {
		if (super.completed) {
			return;
		}
		int tmp = g.color();
		g.setColor(_baseColor);
		float x = viewX - tx;
		float y = viewY - ty;
		int f = super.frame;
		if (f > limit) {
			f = limit - f;
		}
		float x1 = 0.0f;
		float y1 = 0.0f;
		float x2 = 0.0f;
		float y2 = 0.0f;
		float offset = 0.0f;
		if (mode == 0) {
			switch (direction) {
			case LTR:
				offset = MathUtils.floor(f / 3);
				x1 = x - f - offset;
				y1 = y;
				x2 = x + f + offset;
				y2 = y1;
				break;
			case TTB:
				offset = MathUtils.floor(f / 3);
				x1 = x;
				y1 = y - f - offset;
				x2 = x1;
				y2 = y + f + offset;
				break;
			case NETSW:
				x1 = x - f;
				y1 = y + f;
				x2 = x + f;
				y2 = y - f;
				break;
			case WNTES:
			default:
				x1 = x - f;
				y1 = y - f;
				x2 = x + f;
				y2 = y + f;
				break;
			}
		} else if (mode == 1) {
			switch (direction) {
			case LTR:
				offset = MathUtils.floor(limit / 3);
				x1 = x - limit - offset + f;
				y1 = y;
				x2 = x + limit + offset + f;
				y2 = y1;
				break;
			case TTB:
				offset = MathUtils.floor(limit / 3);
				x1 = x;
				y1 = y - limit - offset + f;
				x2 = x1;
				y2 = y + limit + offset + f;
				break;
			case NETSW:
				x1 = x - limit - f;
				y1 = y + limit + f;
				x2 = x + limit - f;
				y2 = y - limit + f;
				break;
			case WNTES:
			default:
				x1 = x - limit + f;
				y1 = y - limit + f;
				x2 = x + limit + f;
				y2 = y + limit + f;
				break;
			}
		} else {
			switch (direction) {
			case LTR:
				offset = MathUtils.floor(limit / 3);
				x1 = x - limit - offset;
				y1 = y;
				x2 = x + limit + offset;
				y2 = y1;
				break;
			case TTB:
				offset = MathUtils.floor(limit / 3);
				x1 = x;
				y1 = y - limit - offset;
				x2 = x1;
				y2 = y + limit + offset;
				break;
			case NETSW:
				x1 = x - limit;
				y1 = y + limit;
				x2 = x + limit;
				y2 = y - limit;
				break;
			case WNTES:
			default:
				x1 = x - limit;
				y1 = y - limit;
				x2 = x + limit;
				y2 = y + limit;
				break;
			}
		}

		g.drawLine(x1, y1, x2, y2, width);
		g.setColor(tmp);
		if (super.frame >= limit) {
			super.completed = true;
		}
	}

	public int getMode() {
		return mode;
	}

	/**
	 * line display mode
	 * 
	 * 0:both ends 1:move line 2:only show line
	 * 
	 * @param mode
	 * 
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

}
