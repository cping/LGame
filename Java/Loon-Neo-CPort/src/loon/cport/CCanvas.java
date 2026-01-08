/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.cport;

import loon.Graphics;
import loon.canvas.Canvas;
import loon.canvas.Gradient;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.canvas.Path;
import loon.canvas.Pattern;
import loon.canvas.Pixmap;
import loon.canvas.PixmapMatrixTransform;
import loon.font.LFont;
import loon.font.TextLayout;
import loon.opengl.TextureSource;
import loon.utils.MathUtils;
import loon.utils.SortedList;

public class CCanvas extends Canvas {

	private SortedList<CCanvasState> _stateStack = new SortedList<CCanvasState>();

	private final Pixmap _canvas;

	public CCanvas(Graphics gfx, CImage image) {
		super(gfx, image);
		this._canvas = image.bufferedImage();
		float scale = image.scale().factor;
		if (scale != 1f) {
			_canvas.scale(scale, scale);
		}
		_stateStack.push(new CCanvasState());
	}

	public float alpha() {
		return currentState().alpha;
	}

	@Override
	public int getStrokeColor() {
		return currentState().strokeColor;
	}

	@Override
	public int getFillColor() {
		return currentState().fillColor;
	}

	@Override
	public Canvas setColor(LColor color) {
		int argb = color.getARGB();
		this.setStrokeColor(argb);
		this.setFillColor(argb);
		this.setAlpha(color.a);
		return this;
	}

	@Override
	public Canvas setColor(int r, int g, int b, int a) {
		int argb = LColor.getARGB(r, g, b, a);
		this.setStrokeColor(argb);
		this.setFillColor(argb);
		this.setAlpha(a);
		return this;
	}

	@Override
	public Canvas setColor(int r, int g, int b) {
		int rgb = LColor.getRGB(r, g, b);
		this.setStrokeColor(rgb);
		this.setFillColor(rgb);
		return this;
	}

	@Override
	public Image snapshot() {
		return new CImage(gfx, image.scale(), _canvas.cpy(), TextureSource.RenderCanvas);
	}

	@Override
	public Canvas clear() {
		_canvas.clearRect(0, 0, MathUtils.iceil(width), MathUtils.iceil(height));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas clearRect(float x, float y, float width, float height) {
		_canvas.clearRect(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.iceil(width), MathUtils.iceil(height));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas clip(Path path) {
		if (path != null && path instanceof CPath) {
			_canvas.fillPolygon(((CPath) path).path.getShape());
		}
		return this;
	}

	@Override
	public Canvas clipRect(float x, float y, float width, final float height) {
		final int cx = MathUtils.ifloor(x), cy = MathUtils.ifloor(y);
		final int cwidth = MathUtils.iceil(width), cheight = MathUtils.iceil(height);
		_canvas.clipRect(cx, cy, cwidth, cheight);
		return this;
	}

	@Override
	public Path createPath() {
		return new CPath();
	}

	@Override
	public Gradient createGradient(Gradient.Config cfg) {
		return new CGradient(_canvas, cfg);
	}

	@Override
	public Canvas drawOval(float x, float y, float w, float h) {
		currentState().prepareFill(_canvas);
		_canvas.drawOval(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(w), MathUtils.floor(h));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillOval(float x, float y, float w, float h) {
		currentState().prepareFill(_canvas);
		_canvas.fillOval(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(w), MathUtils.floor(h));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas drawOval(float x, float y, float w, float h, LColor c) {
		int tmp = getStrokeColor();
		setStrokeColor(c.getARGB());
		currentState().prepareStroke(_canvas);
		_canvas.drawOval(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(w), MathUtils.floor(h));
		setStrokeColor(tmp);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillOval(float x, float y, float w, float h, LColor c) {
		int tmp = getFillColor();
		setFillColor(c.getARGB());
		currentState().prepareFill(_canvas);
		_canvas.fillOval(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(w), MathUtils.floor(h));
		setFillColor(tmp);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas drawLine(float x0, float y0, float x1, float y1) {
		currentState().prepareStroke(_canvas);
		_canvas.drawLine(MathUtils.floor(x0), MathUtils.floor(y0), MathUtils.floor(x1), MathUtils.floor(y1));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas drawPoint(float x, float y) {
		currentState().prepareStroke(_canvas);
		_canvas.drawPoint(MathUtils.floor(x), MathUtils.floor(y));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas drawText(String text, float x, float y) {
		if (_font == null) {
			_font = LFont.getDefaultFont();
		}
		return fillText(_font.getLayoutText(text), x, y);
	}

	@Override
	public Canvas fillCircle(float x, float y, float radius) {
		currentState().prepareFill(_canvas);
		_canvas.fillCircle(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(radius));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillPath(Path path) {
		currentState().prepareFill(_canvas);
		if (path != null && path instanceof CPath) {
			_canvas.fillPolygon(((CPath) path).path.getShape());
		}
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillRect(float x, float y, float width, float height) {
		currentState().prepareFill(_canvas);
		_canvas.fillRect(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(width), MathUtils.floor(height));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillRoundRect(float x, float y, float width, float height, float radius) {
		currentState().prepareFill(_canvas);
		_canvas.fillRoundRect(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(width), MathUtils.floor(height),
				MathUtils.floor(radius));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillText(TextLayout layout, float x, float y) {
		currentState().prepareFill(_canvas);
		((CTextLayout) layout).fill(_canvas, x, y);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas restore() {
		_stateStack.pop();
		return this;
	}

	@Override
	public Canvas rotate(float angle) {
		_canvas.rotate(MathUtils.iceil(angle));
		return this;
	}

	@Override
	public Canvas save() {
		_stateStack.push(new CCanvasState(currentState()));
		return this;
	}

	@Override
	public Canvas scale(float x, float y) {
		_canvas.set(_canvas.scale(x, y));
		return this;
	}

	@Override
	public Canvas setAlpha(float alpha) {
		if (alpha > 1f) {
			alpha = 1f;
		}
		currentState().alpha = alpha;
		return this;
	}

	@Override
	public Canvas setCompositeOperation(Composite composite) {
		currentState().composite = composite;
		return this;
	}

	@Override
	public Canvas setFillColor(int color) {
		currentState().fillColor = color;
		currentState().fillGradient = null;
		currentState().fillPattern = null;
		return this;
	}

	@Override
	public Canvas setFillGradient(Gradient gradient) {
		currentState().fillGradient = (CGradient) gradient;
		currentState().fillPattern = null;
		currentState().fillColor = 0;
		return this;
	}

	@Override
	public Canvas setFillPattern(Pattern pattern) {
		currentState().fillPattern = (CPattern) pattern;
		currentState().fillGradient = null;
		currentState().fillColor = 0;
		return this;
	}

	@Override
	public Canvas setLineCap(LineCap cap) {
		currentState().lineCap = cap;
		return this;
	}

	@Override
	public Canvas setLineJoin(LineJoin join) {
		currentState().lineJoin = join;
		return this;
	}

	@Override
	public Canvas setMiterLimit(float miter) {
		currentState().miterLimit = miter;
		return this;
	}

	@Override
	public Canvas setStrokeColor(int color) {
		currentState().strokeColor = color;
		return this;
	}

	@Override
	public Canvas setStrokeWidth(float w) {
		currentState().strokeWidth = w;
		return this;
	}

	@Override
	public Canvas strokeCircle(float x, float y, float radius) {
		currentState().prepareStroke(_canvas);
		_canvas.drawOval(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(radius), MathUtils.ifloor(radius));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokePath(Path path) {
		currentState().prepareStroke(_canvas);
		if (path != null && path instanceof CPath) {
			_canvas.drawPolygon(((CPath) path).path.getShape());
		}
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeRect(float x, float y, float width, float height) {
		currentState().prepareStroke(_canvas);
		_canvas.drawRect(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(width), MathUtils.ifloor(height));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeRoundRect(float x, float y, float width, float height, float radius) {
		currentState().prepareStroke(_canvas);
		_canvas.drawRoundRect(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(width),
				MathUtils.ifloor(height), MathUtils.ifloor(radius), MathUtils.ifloor(radius));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeText(TextLayout layout, float x, float y) {
		currentState().prepareStroke(_canvas);
		((CTextLayout) layout).stroke(_canvas, x, y);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas transform(float m11, float m12, float m21, float m22, float dx, float dy) {
		_canvas.transform(new PixmapMatrixTransform(m11, m12, dx, m21, m22, dy));
		return this;
	}

	@Override
	public Canvas translate(float x, float y) {
		_canvas.translate(MathUtils.ifloor(x), MathUtils.ifloor(y));
		return this;
	}

	@Override
	protected Pixmap gc() {
		currentState().prepareFill(_canvas);
		return _canvas;
	}

	private CCanvasState currentState() {
		return _stateStack.getFirst();
	}
}
