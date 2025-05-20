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
package loon.lwjgl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Deque;
import java.util.LinkedList;

import loon.Graphics;
import loon.canvas.Canvas;
import loon.canvas.Gradient;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.canvas.Path;
import loon.canvas.Pattern;
import loon.font.LFont;
import loon.font.TextLayout;
import loon.opengl.TextureSource;
import loon.utils.MathUtils;

class Lwjgl3Canvas extends Canvas {

	final Graphics2D g2d;
	private Deque<Lwjgl3CanvasState> stateStack = new LinkedList<>();

	private Ellipse2D.Float ellipse = new Ellipse2D.Float();
	private Line2D.Float line = new Line2D.Float();
	private Rectangle2D.Float rect = new Rectangle2D.Float();
	private RoundRectangle2D.Float roundRect = new RoundRectangle2D.Float();

	public Lwjgl3Canvas(Graphics gfx, Lwjgl3Image image) {
		super(gfx, image);
		g2d = image.bufferedImage().createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		float scale = image.scale().factor;
		g2d.scale(scale, scale);
		stateStack.push(new Lwjgl3CanvasState());
		g2d.setBackground(new Color(0, true));
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
		BufferedImage bmp = ((Lwjgl3Image) image).bufferedImage();
		ColorModel cm = bmp.getColorModel();
		boolean isAlphaPremultiplied = bmp.isAlphaPremultiplied();
		WritableRaster raster = bmp.copyData(null);
		BufferedImage snap = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		return new Lwjgl3Image(gfx, image.scale(), snap, TextureSource.RenderCanvas);
	}

	@Override
	public Canvas clear() {
		currentState().prepareClear(g2d);
		g2d.clearRect(0, 0, MathUtils.iceil(width), MathUtils.iceil(height));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas clearRect(float x, float y, float width, float height) {
		currentState().prepareClear(g2d);
		g2d.clearRect(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.iceil(width), MathUtils.iceil(height));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas clip(Path path) {
		currentState().clipper = (Lwjgl3Path) path;
		return this;
	}

	@Override
	public Canvas clipRect(float x, float y, float width, final float height) {
		final int cx = MathUtils.ifloor(x), cy = MathUtils.ifloor(y);
		final int cwidth = MathUtils.iceil(width), cheight = MathUtils.iceil(height);
		currentState().clipper = new Lwjgl3CanvasState.Clipper() {
			@Override
			public void setClip(Graphics2D g2d) {
				g2d.setClip(cx, cy, cwidth, cheight);
			}
		};
		return this;
	}

	@Override
	public Path createPath() {
		return new Lwjgl3Path();
	}

	@Override
	public Gradient createGradient(Gradient.Config cfg) {
		if (cfg instanceof Gradient.Linear) {
			return Lwjgl3Gradient.create((Gradient.Linear) cfg);
		} else if (cfg instanceof Gradient.Radial) {
			return Lwjgl3Gradient.create((Gradient.Radial) cfg);
		} else {
			throw new IllegalArgumentException("Unknown config: " + cfg);
		}
	}

	@Override
	public Canvas drawOval(float x, float y, float w, float h) {
		currentState().prepareFill(g2d);
		g2d.drawOval(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(w), MathUtils.floor(h));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillOval(float x, float y, float w, float h) {
		currentState().prepareFill(g2d);
		g2d.fillOval(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(w), MathUtils.floor(h));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas drawOval(float x, float y, float w, float h, LColor c) {
		int tmp = getStrokeColor();
		setStrokeColor(c.getARGB());
		currentState().prepareStroke(g2d);
		g2d.drawOval(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(w), MathUtils.floor(h));
		setStrokeColor(tmp);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillOval(float x, float y, float w, float h, LColor c) {
		int tmp = getFillColor();
		setFillColor(c.getARGB());
		currentState().prepareFill(g2d);
		g2d.fillOval(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(w), MathUtils.floor(h));
		setFillColor(tmp);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas drawLine(float x0, float y0, float x1, float y1) {
		currentState().prepareStroke(g2d);
		line.setLine(x0, y0, x1, y1);
		g2d.draw(line);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas drawPoint(float x, float y) {
		currentState().prepareStroke(g2d);
		g2d.drawLine((int) x, (int) y, (int) x, (int) y);
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
		currentState().prepareFill(g2d);
		ellipse.setFrame(x - radius, y - radius, 2 * radius, 2 * radius);
		g2d.fill(ellipse);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillPath(Path path) {
		currentState().prepareFill(g2d);
		g2d.fill(((Lwjgl3Path) path).path);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillRect(float x, float y, float width, float height) {
		currentState().prepareFill(g2d);
		rect.setRect(x, y, width, height);
		g2d.fill(rect);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillRoundRect(float x, float y, float width, float height, float radius) {
		currentState().prepareFill(g2d);
		roundRect.setRoundRect(x, y, width, height, radius * 2, radius * 2);
		g2d.fill(roundRect);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillText(TextLayout layout, float x, float y) {
		currentState().prepareFill(g2d);
		((Lwjgl3TextLayout) layout).fill(g2d, x, y);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas restore() {
		stateStack.pop();
		g2d.setTransform(currentState().transform);
		return this;
	}

	@Override
	public Canvas rotate(float angle) {
		g2d.rotate(angle);
		return this;
	}

	@Override
	public Canvas save() {
		currentState().transform = g2d.getTransform();
		stateStack.push(new Lwjgl3CanvasState(currentState()));
		return this;
	}

	@Override
	public Canvas scale(float x, float y) {
		g2d.scale(x, y);
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
		currentState().fillGradient = (Lwjgl3Gradient) gradient;
		currentState().fillPattern = null;
		currentState().fillColor = 0;
		return this;
	}

	@Override
	public Canvas setFillPattern(Pattern pattern) {
		currentState().fillPattern = (Lwjgl3Pattern) pattern;
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
		currentState().prepareStroke(g2d);
		ellipse.setFrame(x - radius, y - radius, 2 * radius, 2 * radius);
		g2d.draw(ellipse);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokePath(Path path) {
		currentState().prepareStroke(g2d);
		g2d.setColor(new Color(currentState().strokeColor, false));
		g2d.draw(((Lwjgl3Path) path).path);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeRect(float x, float y, float width, float height) {
		currentState().prepareStroke(g2d);
		rect.setRect(x, y, width, height);
		g2d.draw(rect);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeRoundRect(float x, float y, float width, float height, float radius) {
		currentState().prepareStroke(g2d);
		roundRect.setRoundRect(x, y, width, height, radius * 2, radius * 2);
		g2d.draw(roundRect);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeText(TextLayout layout, float x, float y) {
		currentState().prepareStroke(g2d);
		((Lwjgl3TextLayout) layout).stroke(g2d, x, y);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas transform(float m11, float m12, float m21, float m22, float dx, float dy) {
		g2d.transform(new AffineTransform(m11, m12, m21, m22, dx, dy));
		return this;
	}

	@Override
	public Canvas translate(float x, float y) {
		g2d.translate(x, y);
		return this;
	}

	@Override
	protected Graphics2D gc() {
		currentState().prepareFill(g2d);
		return g2d;
	}

	private Lwjgl3CanvasState currentState() {
		return stateStack.getFirst();
	}

}
