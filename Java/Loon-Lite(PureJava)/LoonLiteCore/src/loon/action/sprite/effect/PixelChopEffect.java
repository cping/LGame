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

import loon.LSysException;
import loon.action.sprite.ISprite;
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
		LTR,
		/**
		 * WNTES and NETSW combined to Cross
		 */
		WNCROSS,
		/**
		 * TTB and LTR combined to Cross
		 */
		LTCROSS,
		/**
		 * ALL combined to Cross
		 */
		ALLCROSS;
	}

	private ChopDirection _direction;

	private float _viewX, _viewY;

	private float _width;

	private int _mode;

	public static PixelBaseEffect chop(ChopDirection dir, LColor color, int size, ISprite spr) {
		return chop(dir, color, size, spr, false);
	}

	public static PixelBaseEffect chop(ChopDirection dir, LColor color, int size, ISprite spr, boolean rand) {
		return chop(dir, color, size, spr, rand, 0f, 0f);
	}

	public static PixelBaseEffect chop(ChopDirection dir, LColor color, int size, ISprite spr, boolean rand,
			float offsetX, float offsetY) {
		if (spr == null) {
			throw new LSysException("The chop target does not exist !");
		}
		final int frame = MathUtils.ifloor(MathUtils.max(spr.getWidth(), spr.getHeight()) / 3f);
		final float centerX = spr.getX() + spr.getWidth() / 2 + offsetX;
		final float centerY = spr.getY() + spr.getHeight() / 2 + offsetY;
		if (rand) {
			return createRandom(color, centerX, centerY, size, frame);
		} else {
			return create(dir, color, centerX, centerY, size, frame);
		}
	}

	public static PixelBaseEffect chopRandom(LColor color, int size, ISprite spr) {
		return chop(null, color, size, spr, true, 0f, 0f);
	}

	public static PixelBaseEffect chopRandom(LColor color, int size, ISprite spr, float offsetX, float offsetY) {
		return chop(null, color, size, spr, true, offsetX, offsetY);
	}

	public static PixelBaseEffect create(ChopDirection dir, LColor color, float x, float y, int width, int frameLimit) {
		return PixelChopEffect.get(dir, color, x, y, width, frameLimit).setAutoRemoved(true);
	}

	public static PixelBaseEffect createRandom(LColor color, float x, float y, int width, int frameLimit) {
		return PixelChopEffect.getRandom(color, x, y, width, frameLimit).setAutoRemoved(true);
	}

	public static PixelChopEffect get(ChopDirection dir, LColor color, float x, float y, int width, int frameLimit) {
		return new PixelChopEffect(dir, color, x, y, width, frameLimit);
	}

	public static PixelChopEffect getRandom(LColor color, float x, float y, int width, int frameLimit) {
		final int rand = MathUtils.nextInt(0, 6);
		switch (rand) {
		default:
		case 0:
			return new PixelChopEffect(ChopDirection.WNTES, color, x, y, width, frameLimit);
		case 1:
			return new PixelChopEffect(ChopDirection.NETSW, color, x, y, width, frameLimit);
		case 2:
			return new PixelChopEffect(ChopDirection.LTR, color, x, y, width, frameLimit);
		case 3:
			return new PixelChopEffect(ChopDirection.TTB, color, x, y, width, frameLimit);
		case 4:
			return new PixelChopEffect(ChopDirection.LTCROSS, color, x, y, width, frameLimit);
		case 5:
			return new PixelChopEffect(ChopDirection.WNCROSS, color, x, y, width, frameLimit);
		case 6:
			return new PixelChopEffect(ChopDirection.ALLCROSS, color, x, y, width, frameLimit);
		}
	}

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
		this._direction = dir;
		this._mode = mode;
		this._width = width;
		this._viewX = x;
		this._viewY = y;
		this._limit = frameLimit;
		setDelay(0);
		setEffectDelay(0);
	}

	private void paintChop(GLEx g, ChopDirection dir, float tx, float ty) {
		final float x = _viewX - tx;
		final float y = _viewY - ty;
		int f = super._frame;
		if (f > _limit) {
			f = _limit - f;
		}
		float x1 = 0f;
		float y1 = 0f;
		float x2 = 0f;
		float y2 = 0f;
		float offset = 0f;
		if (_mode == 0) {
			switch (dir) {
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
		} else if (_mode == 1) {
			switch (dir) {
			case LTR:
				offset = MathUtils.floor(_limit / 3);
				x1 = x - _limit - offset + f;
				y1 = y;
				x2 = x + _limit + offset + f;
				y2 = y1;
				break;
			case TTB:
				offset = MathUtils.floor(_limit / 3);
				x1 = x;
				y1 = y - _limit - offset + f;
				x2 = x1;
				y2 = y + _limit + offset + f;
				break;
			case NETSW:
				x1 = x - _limit - f;
				y1 = y + _limit + f;
				x2 = x + _limit - f;
				y2 = y - _limit + f;
				break;
			case WNTES:
			default:
				x1 = x - _limit + f;
				y1 = y - _limit + f;
				x2 = x + _limit + f;
				y2 = y + _limit + f;
				break;
			}
		} else {
			switch (dir) {
			case LTR:
				offset = MathUtils.floor(_limit / 3);
				x1 = x - _limit - offset;
				y1 = y;
				x2 = x + _limit + offset;
				y2 = y1;
				break;
			case TTB:
				offset = MathUtils.floor(_limit / 3);
				x1 = x;
				y1 = y - _limit - offset;
				x2 = x1;
				y2 = y + _limit + offset;
				break;
			case NETSW:
				x1 = x - _limit;
				y1 = y + _limit;
				x2 = x + _limit;
				y2 = y - _limit;
				break;
			case WNTES:
			default:
				x1 = x - _limit;
				y1 = y - _limit;
				x2 = x + _limit;
				y2 = y + _limit;
				break;
			}
		}
		g.drawLine(x1, y1, x2, y2, _width);
	}

	@Override
	public void draw(GLEx g, float tx, float ty) {
		if (super._completed) {
			return;
		}
		int tmp = g.color();
		g.setColor(_baseColor);
		switch (this._direction) {
		case LTCROSS:
			paintChop(g, ChopDirection.LTR, tx, ty);
			paintChop(g, ChopDirection.TTB, tx, ty);
			break;
		case WNCROSS:
			paintChop(g, ChopDirection.WNTES, tx, ty);
			paintChop(g, ChopDirection.NETSW, tx, ty);
			break;
		case ALLCROSS:
			paintChop(g, ChopDirection.LTR, tx, ty);
			paintChop(g, ChopDirection.TTB, tx, ty);
			paintChop(g, ChopDirection.WNTES, tx, ty);
			paintChop(g, ChopDirection.NETSW, tx, ty);
			break;
		default:
			paintChop(g, this._direction, tx, ty);
			break;
		}
		g.setColor(tmp);
		if (super._frame >= _limit) {
			super._completed = true;
		}
	}

	public int getMode() {
		return _mode;
	}

	/**
	 * line display mode
	 * 
	 * 0:both ends 1:move line 2:only show line
	 * 
	 * @param mode
	 * 
	 */
	public PixelChopEffect setMode(int mode) {
		this._mode = mode;
		return this;
	}

}
