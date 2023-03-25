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
package loon.se;

import java.awt.Color;
import java.awt.Graphics2D;
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
import loon.LSystem;
import loon.canvas.Canvas;
import loon.canvas.Gradient;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.canvas.Path;
import loon.font.LFont;
import loon.font.TextLayout;
import loon.geom.Affine2f;
import loon.opengl.BlendMethod;
import loon.opengl.TextureSource;
import loon.utils.MathUtils;

public class JavaSECanvas extends Canvas {

	private boolean graphicsInited = false;
	private boolean graphicsMain = false;

	protected Graphics2D context;
	private Deque<JavaSECanvasState> stateStack = new LinkedList<JavaSECanvasState>();

	private AffineTransform transform = new AffineTransform();

	private Ellipse2D.Float ellipse = new Ellipse2D.Float();
	private Line2D.Float line = new Line2D.Float();
	private Rectangle2D.Float rect = new Rectangle2D.Float();
	private RoundRectangle2D.Float roundRect = new RoundRectangle2D.Float();

	private JavaSESetting setting;

	public JavaSECanvas(Graphics gfx, JavaSEImage image) {
		this(gfx, image, false);
	}

	public JavaSECanvas(Graphics gfx, JavaSEImage image, boolean gm) {
		this(gfx, image, null, gm);
	}

	public JavaSECanvas(Graphics gfx, JavaSEImage image, Graphics2D graphics2d, boolean gm) {
		super(gfx, image);
		this.graphicsMain = gm;
		if (image != null && image.seImage() != null) {
			context = image.seImage().createGraphics();
			this.isDirty = true;
		} else {
			context = graphics2d;
			this.isDirty = false;
		}
		if (gfx.setting() instanceof JavaSESetting) {
			setting = (JavaSESetting) gfx.setting();
		}
		setContextInit(context);
	}

	public void updateContext(Graphics2D g2d) {
		if (this.context != g2d) {
			this.context = g2d;
			if (setting != null) {
				switch (setting.graphicsMode) {
				case 0:
					JavaSEApplication.setGraphicsExcellent(g2d);
					break;
				case 1:
					JavaSEApplication.setGraphicsQuality(g2d);
					break;
				case 2:
					JavaSEApplication.setGraphicsSpeed(g2d);
					break;
				default:
					throw new IllegalArgumentException("Unexpected Graphics Mode value: " + setting.graphicsMode);
				}
			}
		}
	}

	public void setContextInit(Graphics2D g2d) {
		if (!graphicsInited) {
			this.updateContext(g2d);
			if (this.context != null) {
				float scale = image.scale().factor;
				this.context.scale(scale, scale);
				this.stateStack.push(new JavaSECanvasState());
				this.context.setBackground(new Color(0, true));
			}
			graphicsInited = true;
		}
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
		return this;
	}

	@Override
	public Canvas setColor(int r, int g, int b, int a) {
		int argb = LColor.getARGB(r, g, b, a);
		this.setStrokeColor(argb);
		this.setFillColor(argb);
		return this;
	}

	@Override
	public Canvas setColor(int r, int g, int b) {
		int rgb = LColor.getRGB(r, g, b);
		this.setStrokeColor(rgb);
		this.setFillColor(rgb);
		return this;
	}

	protected Color getLColorToSE(LColor c) {
		if (c == null) {
			return Color.white;
		}
		return new Color(c.r, c.g, c.b, c.a);
	}

	private BufferedImage createImage() {
		BufferedImage bmp = ((JavaSEImage) image).seImage();
		ColorModel cm = bmp.getColorModel();
		boolean isAlphaPremultiplied = bmp.isAlphaPremultiplied();
		WritableRaster raster = bmp.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	protected JavaSEImage toSEImage() {
		return (JavaSEImage) image;
	}

	protected void setSEImage(Image img, BufferedImage write) {
		BufferedImage oldImg = ((JavaSEImage) img).buffer;
		if (oldImg != write) {
			((JavaSEImage) img).buffer = write;
		}
		img.setDirty(false);
	}

	@Override
	public Image newSnapshot() {
		if (isMainCanvas()) {
			if (LSystem.base() != null && !((JavaSESetting) LSystem.base().setting).doubleBuffer) {
				return LSystem.base().snapshot();
			}
		}
		return new JavaSEImage(gfx, image.scale(), createImage(), TextureSource.RenderCanvas);
	}

	@Override
	public Image snapshot() {
		if (isMainCanvas()) {
			if (LSystem.base() != null && !((JavaSESetting) LSystem.base().setting).doubleBuffer) {
				return LSystem.base().snapshot();
			}
		}
		if (image == null) {
			return (image = newSnapshot());
		}
		if (image.isDirty() || isDirty) {
			isDirty = false;
		}
		return image;
	}

	public Canvas resetClip() {
		return clipRect(0, 0, width, height);
	}

	@Override
	public Canvas clear() {
		currentState().prepareClear(context);
		context.clearRect(0, 0, MathUtils.iceil(width), MathUtils.iceil(height));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas clearRect(float x, float y, float width, float height) {
		currentState().prepareClear(context);
		context.clearRect(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.iceil(width), MathUtils.iceil(height));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas clip(Path path) {
		currentState().clipper = (JavaSEPath) path;
		isDirty = true;
		return this;
	}

	@Override
	public Canvas clipRect(float x, float y, float width, final float height) {
		currentState().prepareClear(context);
		final int cx = MathUtils.ifloor(x), cy = MathUtils.ifloor(y);
		final int cwidth = MathUtils.iceil(width), cheight = MathUtils.iceil(height);
		context.clipRect(cx, cy, cwidth, cheight);
		isDirty = true;
		return this;
	}

	@Override
	public Path createPath() {
		return new JavaSEPath();
	}

	@Override
	public Gradient createGradient(Gradient.Config cfg) {
		if (cfg instanceof Gradient.Linear) {
			return JavaSEGradient.create((Gradient.Linear) cfg);
		} else if (cfg instanceof Gradient.Radial) {
			return JavaSEGradient.create((Gradient.Radial) cfg);
		} else {
			throw new IllegalArgumentException("Unknown config: " + cfg);
		}
	}

	@Override
	public Canvas drawLine(float x0, float y0, float x1, float y1) {
		currentState().prepareStroke(context);
		line.setLine(x0, y0, x1, y1);
		context.draw(line);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas drawPoint(float x, float y) {
		currentState().prepareStroke(context);
		context.drawLine((int) x, (int) y, (int) x, (int) y);
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
	public Canvas drawText(String text, float x, float y, LColor color) {
		if (_font == null) {
			_font = LFont.getDefaultFont();
		}
		int tmp = getFillColor();
		setFillColor(color);
		fillText(_font.getLayoutText(text), x, y);
		setFillColor(tmp);
		return this;
	}

	@Override
	public Canvas fillCircle(float x, float y, float radius) {
		currentState().prepareFill(context);
		ellipse.setFrame(x - radius, y - radius, 2 * radius, 2 * radius);
		context.fill(ellipse);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillPath(Path path) {
		currentState().prepareFill(context);
		context.fill(((JavaSEPath) path).path);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillRect(float x, float y, float width, float height) {
		currentState().prepareFill(context);
		rect.setRect(x, y, width, height);
		context.fill(rect);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas drawRoundRect(float x, float y, float width, float height, float radius) {
		currentState().prepareStroke(context);
		roundRect.setRoundRect(x, y, width, height, radius * 2, radius * 2);
		context.draw(roundRect);
		isDirty = true;
		return this;
	}
	
	@Override
	public Canvas fillRoundRect(float x, float y, float width, float height, float radius) {
		currentState().prepareFill(context);
		roundRect.setRoundRect(x, y, width, height, radius * 2, radius * 2);
		context.fill(roundRect);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillText(TextLayout layout, float x, float y) {
		currentState().prepareFill(context);
		((JavaSETextLayout) layout).fill(context, x, y);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas restore() {
		stateStack.pop();
		context.setTransform(currentState().transform);
		return this;
	}

	@Override
	public Canvas rotate(float angle) {
		context.rotate(angle);
		return this;
	}

	@Override
	public Canvas save() {
		currentState().transform.setTransform(context.getTransform());
		stateStack.push(new JavaSECanvasState(currentState()));
		return this;
	}

	@Override
	public Canvas scale(float x, float y) {
		context.scale(x, y);
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
		currentState().fillGradient = (JavaSEGradient) gradient;
		currentState().fillPattern = null;
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
		currentState().prepareStroke(context);
		ellipse.setFrame(x - radius, y - radius, 2 * radius, 2 * radius);
		context.draw(ellipse);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokePath(Path path) {
		currentState().prepareStroke(context);
		context.draw(((JavaSEPath) path).path);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeRect(float x, float y, float width, float height) {
		currentState().prepareStroke(context);
		rect.setRect(x, y, width, height);
		context.draw(rect);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeRoundRect(float x, float y, float width, float height, float radius) {
		currentState().prepareStroke(context);
		roundRect.setRoundRect(x, y, width, height, radius * 2, radius * 2);
		context.draw(roundRect);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeText(TextLayout layout, float x, float y) {
		currentState().prepareStroke(context);
		((JavaSETextLayout) layout).stroke(context, x, y);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas setTransform(Affine2f aff) {
		transform.setTransform(aff.m00, aff.m01, aff.m10, aff.m11, aff.tx, aff.ty);
		context.setTransform(transform);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas transform(float m11, float m12, float m21, float m22, float dx, float dy) {
		transform.setTransform(m11, m12, m21, m22, dx, dy);
		context.transform(transform);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas translate(float x, float y) {
		context.translate(x, y);
		isDirty = true;
		return this;
	}

	@Override
	protected Graphics2D gc() {
		currentState().prepareFill(context);
		return context;
	}

	private JavaSECanvasState currentState() {
		return stateStack.getFirst();
	}

	@Override
	public Canvas clear(LColor c) {
		int tmp = getFillColor();
		setFillColor(c);
		currentState().prepareFill(context);
		context.fillRect(0, 0, MathUtils.floor(width), MathUtils.floor(height));
		setFillColor(tmp);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas drawRect(float x, float y, float width, float height, LColor c) {
		int tmp = getStrokeColor();
		setStrokeColor(c);
		currentState().prepareStroke(context);
		context.drawRect(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(width), MathUtils.floor(height));
		setStrokeColor(tmp);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas setBlendMethod(int blend) {
		Composite mode = null;
		switch (blend) {
		case BlendMethod.MODE_ADD:
		case BlendMethod.MODE_ALPHA_ONE:
			mode = Composite.ADD;
			break;
		case BlendMethod.MODE_ALPHA:
			mode = Composite.SRC_OVER;
			break;
		case BlendMethod.MODE_MULTIPLY:
			mode = Composite.MULTIPLY;
			break;
		case BlendMethod.MODE_COLOR_MULTIPLY:
			mode = Composite.MULTIPLY;
			break;
		case BlendMethod.MODE_NORMAL:
		default:
			mode = Composite.SRC_OVER;
			break;
		}
		setCompositeOperation(mode);
		this.isDirty = true;
		return this;
	}

	@Override
	public Canvas drawArc(float x, float y, float w, float h, float startAngle, float endAngle, LColor c) {
		int tmp = getStrokeColor();
		setStrokeColor(c);
		currentState().prepareStroke(context);
		context.drawArc(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(w), MathUtils.floor(h),
				MathUtils.floor(startAngle), MathUtils.floor(endAngle));
		setStrokeColor(tmp);
		isDirty = true;
		return null;
	}

	@Override
	public Canvas drawOval(float x, float y, float w, float h, LColor c) {
		int tmp = getStrokeColor();
		setStrokeColor(c);
		currentState().prepareStroke(context);
		context.drawOval(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(w), MathUtils.floor(h));
		setStrokeColor(tmp);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas drawOval(float x, float y, float w, float h) {
		currentState().prepareStroke(context);
		context.drawOval(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(w), MathUtils.floor(h));
		isDirty = true;
		return this;
	}
	
	@Override
	public Canvas fillRect(float x, float y, float width, float height, LColor c) {
		int tmp = getFillColor();
		setFillColor(c);
		currentState().prepareFill(context);
		context.fillRect(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(width), MathUtils.floor(height));
		setFillColor(tmp);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillOval(float x, float y, float w, float h) {
		currentState().prepareFill(context);
		context.fillOval(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(w), MathUtils.floor(h));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillOval(float x, float y, float w, float h, LColor c) {
		int tmp = getFillColor();
		setFillColor(c);
		currentState().prepareFill(context);
		context.fillOval(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(w), MathUtils.floor(h));
		setFillColor(tmp);
		isDirty = true;
		return this;
	}
	
	@Override
	public Canvas fillArc(float x1, float y1, float width, float height, float start, float end) {
		currentState().prepareFill(context);
		context.fillArc(MathUtils.floor(x1), MathUtils.floor(y1), MathUtils.floor(width), MathUtils.floor(height),
				MathUtils.floor(start), MathUtils.floor(end));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas setFillColor(LColor color) {
		currentState().fillColor = (color == null ? LColor.DEF_COLOR : color.getARGB());
		return this;
	}

	@Override
	public LColor getFilltoLColor() {
		return new LColor(currentState().fillColor);
	}

	@Override
	public LColor getStroketoLColor() {
		return new LColor(currentState().strokeColor);
	}

	@Override
	public Canvas updateDirty() {
		isDirty = true;
		return this;
	}

	@Override
	public Canvas setStrokeColor(LColor color) {
		currentState().strokeColor = (color == null ? LColor.DEF_COLOR : color.getARGB());
		this.isDirty = true;
		return this;
	}

	@Override
	public Canvas setLineWidth(float lineWidth) {
		currentState().strokeWidth = lineWidth;
		this.isDirty = true;
		return this;
	}

	@Override
	public boolean isMainCanvas() {
		return graphicsMain;
	}

	@Override
	public void closeImpl() {
		if (context != null) {
			context.dispose();
			context = null;
			closed = true;
		}
	}

}
