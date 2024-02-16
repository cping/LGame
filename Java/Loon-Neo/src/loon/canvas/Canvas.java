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
package loon.canvas;

import loon.Graphics;
import loon.LRelease;
import loon.LSysException;
import loon.LTexture;
import loon.canvas.Paint.Style;
import loon.font.LFont;
import loon.font.TextLayout;

/**
 * Canvas渲染用类,作为Image的渲染器封装而存在,随着平台不同,在后台渲染上可能存在一定差异.<br>
 * 如果要统一效果,可以使用Pixmap进行像素处理.
 */
public abstract class Canvas implements LRelease {

	protected LFont _font;

	public static enum Composite {

		SRC,

		DST_ATOP,

		SRC_OVER,

		DST_OVER,

		SRC_IN,

		DST_IN,

		SRC_OUT,

		DST_OUT,

		SRC_ATOP,

		XOR,

		MULTIPLY
	}

	public static enum LineCap {
		BUTT, ROUND, SQUARE
	}

	public static enum LineJoin {
		BEVEL, MITER, ROUND
	}

	public interface Drawable {
		float width();

		float height();

		void draw(Object gc, float x, float y, float width, float height);

		void draw(Object gc, float dx, float dy, float dw, float dh, float sx, float sy, float sw, float sh);
	}

	public final Image image;

	public final float width;

	public final float height;

	public abstract Image snapshot();

	@Override
	public void close() {
	}

	public abstract Canvas clear();

	public abstract Canvas clearRect(float x, float y, float width, float height);

	public abstract Canvas clip(Path clipPath);

	public abstract Canvas clipRect(float x, float y, float width, float height);

	public abstract Path createPath();

	public abstract Gradient createGradient(Gradient.Config config);

	public Canvas rect(float x, float y, float w, float h, Paint paint) {
		if (paint == null) {
			fillRect(x, y, w, h);
		} else {
			int tmp = getStrokeColor();
			setStrokeWidth(paint.strokeWidth);
			int fill = getFillColor();
			int stroke = getStrokeColor();
			Style style = paint.style;
			switch (style) {
			case FILL:
				setFillColor(paint.color);
				fillRect(x, y, w, h);
				break;
			case STROKE:
				setStrokeColor(paint.color);
				strokeRect(x, y, w, h);
				break;
			case FILL_AND_STROKE:
				setFillColor(paint.color);
				setStrokeColor(paint.color);
				fillRect(x, y, w, h);
				strokeRect(x, y, w, h);
				break;
			}
			setFillColor(fill);
			setStrokeColor(stroke);
			setStrokeWidth(tmp);
		}
		return this;
	}

	public Canvas draw(Drawable image, float x, float y) {
		return draw(image, x, y, image.width(), image.height());
	}

	public Canvas drawCentered(Drawable image, float x, float y) {
		return draw(image, x - image.width() / 2, y - image.height() / 2);
	}

	public Canvas draw(Drawable image, float x, float y, float w, float h) {
		image.draw(gc(), x, y, w, h);
		isDirty = true;
		return this;
	}

	public Canvas draw(Drawable image, float dx, float dy, float dw, float dh, float sx, float sy, float sw, float sh) {
		image.draw(gc(), dx, dy, dw, dh, sx, sy, sw, sh);
		isDirty = true;
		return this;
	}

	public abstract Canvas drawLine(float x0, float y0, float x1, float y1);

	public abstract Canvas drawPoint(float x, float y);

	public abstract Canvas drawText(String text, float x, float y);

	public Canvas drawText(String message, float x, float y, int c1, int c2) {
		int tmp = getFillColor();
		setFillColor(c1);
		drawText(message, x + 1, y);
		drawText(message, x - 1, y);
		drawText(message, x, y + 1);
		drawText(message, x, y - 1);
		setFillColor(c2);
		drawText(message, x, y);
		setFillColor(tmp);
		return this;
	}

	public Canvas drawText(String message, float x, float y, LColor c1, LColor c2) {
		int tmp = getFillColor();
		setColor(c1);
		drawText(message, x + 1, y);
		drawText(message, x - 1, y);
		drawText(message, x, y + 1);
		drawText(message, x, y - 1);
		setColor(c2);
		drawText(message, x, y);
		setFillColor(tmp);
		return this;
	}

	public abstract Canvas drawOval(float x, float y, float w, float h);

	public abstract Canvas fillOval(float x, float y, float w, float h);

	public abstract Canvas drawOval(float x, float y, float w, float h, LColor c);

	public abstract Canvas fillOval(float x, float y, float w, float h, LColor c);

	public abstract Canvas fillCircle(float x, float y, float radius);

	public abstract Canvas fillPath(Path path);

	public abstract Canvas fillRect(float x, float y, float width, float height);

	public abstract Canvas fillRoundRect(float x, float y, float width, float height, float radius);

	public abstract Canvas fillText(TextLayout text, float x, float y);

	public abstract Canvas restore();

	public abstract Canvas rotate(float radians);

	public abstract Canvas save();

	public abstract Canvas scale(float x, float y);

	public abstract Canvas setAlpha(float alpha);

	public abstract Canvas setCompositeOperation(Composite composite);

	public abstract Canvas setFillColor(int color);

	public abstract Canvas setColor(LColor color);

	public abstract int getStrokeColor();

	public abstract int getFillColor();

	public abstract Canvas setColor(int r, int g, int b);

	public abstract Canvas setColor(int r, int g, int b, int a);

	public abstract Canvas setFillGradient(Gradient gradient);

	public abstract Canvas setFillPattern(Pattern pattern);

	public abstract Canvas setLineCap(LineCap cap);

	public abstract Canvas setLineJoin(LineJoin join);

	public abstract Canvas setMiterLimit(float miter);

	public abstract Canvas setStrokeColor(int color);

	public abstract Canvas setStrokeWidth(float strokeWidth);

	public abstract Canvas strokeCircle(float x, float y, float radius);

	public abstract Canvas strokePath(Path path);

	public abstract Canvas strokeRect(float x, float y, float width, float height);

	public abstract Canvas strokeRoundRect(float x, float y, float width, float height, float radius);

	public abstract Canvas strokeText(TextLayout text, float x, float y);

	public LTexture toTexture() {
		return toTexture(LTexture.Format.LINEAR);
	}

	public LTexture toTexture(LTexture.Format config) {
		try {
			return image.createTexture(config);
		} finally {
			close();
		}
	}

	public abstract Canvas transform(float m11, float m12, float m21, float m22, float dx, float dy);

	public abstract Canvas translate(float x, float y);

	protected boolean isDirty;

	protected final Graphics gfx;

	protected Canvas(Graphics gfx, Image image) {
		this.gfx = gfx;
		this.image = image;
		if (this.image != null) {
			this.image.canvas = this;
			this.width = image.width();
			this.height = image.height();
		} else {
			width = -1;
			height = -1;
		}
		if (width <= 0 || height <= 0) {
			throw new LSysException("Canvas must be > 0 in width and height: " + width + "x" + height);
		}
	}

	protected abstract Object gc();

	public LFont getFont() {
		return _font;
	}

	public Canvas setFont(LFont font) {
		this._font = font;
		return this;
	}
}
