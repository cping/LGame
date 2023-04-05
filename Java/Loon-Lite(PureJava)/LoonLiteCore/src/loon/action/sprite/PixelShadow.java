/**
 * Copyright 2008 - 2023 The Loon Game Engine Authors
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
package loon.action.sprite;

import loon.LTexture;
import loon.LTextures;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.canvas.Pixmap;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

public class PixelShadow implements ISpritesShadow {

	private final static float ellipseX = 0.6f;

	private final static float ellipseY = 0.3f;

	public static void drawShadowOval(GLEx g, float offsetX, float offsetY, float width, float height, LColor color) {
		final float ovalWidth = ellipseX * width;
		final float ovalHeight = ellipseY * height;
		final float startX = (width - ovalWidth) / 2f;
		final float startY = height - ovalHeight + ovalHeight / 4;
		g.fillOval(startX + offsetX, startY + offsetY, ovalWidth, ovalHeight, color);
	}

	public static void drawShadowOval(Canvas g, float offsetX, float offsetY, float width, float height, LColor color) {
		final float ovalWidth = ellipseX * width;
		final float ovalHeight = ellipseY * height;
		final float startX = (width - ovalWidth) / 2f;
		final float startY = height - ovalHeight + ovalHeight / 4;
		g.fillOval(startX + offsetX, startY + offsetY, ovalWidth, ovalHeight, color);
	}

	public static void drawShadowOval(Pixmap g, float offsetX, float offsetY, float width, float height, LColor color) {
		final float ovalWidth = ellipseX * width;
		final float ovalHeight = ellipseY * height;
		final float startX = (width - ovalWidth) / 2f;
		final float startY = height - ovalHeight + ovalHeight / 4;
		g.fillOval(MathUtils.floor(startX + offsetX), MathUtils.floor(startY + offsetY), MathUtils.floor(ovalWidth),
				MathUtils.floor(ovalHeight), color);
	}

	public static void drawOval(Canvas g, float width, float height, LColor color) {
		final float ovalWidth = ellipseX * width;
		final float ovalHeight = ellipseY * height;
		g.fillOval(0, 0, MathUtils.floor(ovalWidth), MathUtils.floor(ovalHeight), color);
	}

	private LTexture _shadowImage;

	private float _offsetX, _offsetY;

	private boolean _closed;

	private int _width, _height;

	private LColor _color;

	public PixelShadow(String path, int w, int h, LColor color) {
		this(LTextures.loadTexture(path), w, h, color);
	}

	public PixelShadow(int w, int h, LColor color) {
		this((LTexture) null, w, h, color);
	}

	public PixelShadow(LTexture texture, int w, int h, LColor color) {
		this._color = color;
		if (_color == null) {
			_color = LColor.black.cpy();
		}
		if (texture != null) {
			this._shadowImage = texture;
			this._width = texture.getWidth();
			this._height = texture.getHeight();
		} else {
			this._width = w;
			this._height = h;
			Image image = Image.createImage(_width, _height);
			Canvas canvas = image.getCanvas();
			drawOval(canvas, _width, _height, _color);
			this._shadowImage = image.texture();
		}
		ellipseOffset();
	}

	protected void ellipseOffset() {
		final float ovalWidth = ellipseX * _width;
		final float ovalHeight = ellipseY * _height;
		this._offsetX = (_width - ovalWidth) / 2f;
		this._offsetY = _height - ovalHeight + ovalHeight / 4;
	}

	public PixelShadow drawShadow(GLEx g, ISprite e, float x, float y) {
		if (e.showShadow() && e.isVisible()) {
			float newX, newY;
			if (e instanceof Sprite) {
				Sprite ey = (Sprite) e;
				newX = ey.drawX(_offsetX + x);
				newY = ey.drawY(_offsetY + y);
			} else if (e instanceof Entity) {
				Entity ey = (Entity) e;
				newX = ey.drawX(_offsetX + x);
				newY = ey.drawY(_offsetY + y);
			} else {
				newX = e.getX() + e.getOffsetX() + _offsetX + x;
				newY = e.getY() + e.getOffsetY() + _offsetY + y;
			}
			g.draw(_shadowImage, newX, newY, _width, _height, _color);
		}
		return this;
	}

	public PixelShadow drawShadow(GLEx g, float offsetX, float offsetY) {
		g.draw(_shadowImage, _offsetX + offsetX, _offsetY + offsetY, _width, _height, _color);
		return this;
	}

	public float getOffsetX() {
		return this._offsetX;
	}

	public PixelShadow setOffsetX(float offsetX) {
		this._offsetX = offsetX;
		return this;
	}

	public float getOffsetY() {
		return this._offsetY;
	}

	public PixelShadow setOffsetY(float offsetY) {
		this._offsetY = offsetY;
		return this;
	}

	public int getWidth() {
		return _width;
	}

	public int getHeight() {
		return _height;
	}

	public LColor getColor() {
		return _color;
	}

	public PixelShadow setColor(LColor c) {
		this._color = c;
		return this;
	}

	@Override
	public void close() {
		if (_closed) {
			return;
		}
		if (_shadowImage != null) {
			_shadowImage.close();
			_shadowImage = null;
		}
		_closed = true;
	}

	@Override
	public boolean isClosed() {
		return _closed;
	}

}
