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
package loon.fx;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import loon.Graphics;
import loon.canvas.Canvas;
import loon.canvas.Gradient;
import loon.canvas.Gradient.Config;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.canvas.Path;
import loon.font.LFont;
import loon.font.TextLayout;
import loon.geom.Affine2f;
import loon.opengl.BlendMethod;
import loon.opengl.TextureSource;
import loon.utils.MathUtils;

public class JavaFXCanvas extends Canvas {

	final JavaFXResizeCanvas fxCanvas;

	private SnapshotParameters snapshotParameters;
	private LColor tmpColor = LColor.white.cpy();

	protected LColor color;
	protected GraphicsContext context;
	private boolean graphicsMain;

	protected JavaFXCanvas(Graphics gfx, JavaFXImage image) {
		this(gfx, image, false);
	}

	protected JavaFXCanvas(Graphics gfx, JavaFXImage image, boolean gm) {
		super(gfx, image);
		if (this.image != null) {
			this.image = image;
			this.width = image.getWidth();
			this.height = image.getHeight();
		}
		this.graphicsMain = gm;
		this.fxCanvas = new JavaFXResizeCanvas(width, height);
		this.setContextInit(fxCanvas.getGraphicsContext2D());
		if (image != null) {
			this.isDirty = true;
		} else {
			this.isDirty = false;
		}
		this.snapshotParameters = new SnapshotParameters();
		if (image.hasAlpha()) {
			snapshotParameters.setFill(Color.TRANSPARENT);
		}
	}

	public void updateContext(GraphicsContext g) {
		this.context = g;
	}

	public void setContextInit(GraphicsContext g) {
		this.updateContext(g);
		if (this.context != null) {
			float scale = image.scale().factor;
			context.scale(scale, scale);
			context.setFill(Color.BLACK);
			context.clearRect(0, 0, width, height);
		}
	}

	@Override
	public Canvas save() {
		context.save();
		this.isDirty = true;
		return this;
	}

	@Override
	public Canvas setColor(LColor c) {
		if (c == null || c.equals(this.color)) {
			return this;
		}
		return applyColor(c);
	}

	private Color getLColorToFX(LColor c) {
		if (c == null) {
			return Color.WHITE;
		}
		return javafx.scene.paint.Color.rgb(c.getRed(), c.getGreen(), c.getBlue(), c.a);
	}

	private LColor getFXToLColor(Color c) {
		return new LColor((float) c.getRed(), (float) c.getGreen(), (float) c.getBlue(), (float) c.getOpacity());
	}

	private Canvas applyColor(LColor c) {
		this.color = c;
		Color fxcolor = getLColorToFX(c);
		context.setFill(fxcolor);
		context.setStroke(fxcolor);
		this.isDirty = true;
		return this;
	}

	public Canvas updateDirty() {
		isDirty = true;
		return this;
	}

	@Override
	public Canvas clear() {
		Paint tmp = context.getFill();
		context.setFill(Color.BLACK);
		context.fillRect(0, 0, width, height);
		context.setFill(tmp);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas clear(LColor color) {
		Paint tmp = context.getFill();
		context.setFill(getLColorToFX(color));
		context.clearRect(0, 0, width, height);
		context.setFill(tmp);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas clearRect(float x, float y, float width, float height) {
		context.clearRect(x, y, width, height);
		isDirty = true;
		return this;
	}

	protected JavaFXImage toFXImage() {
		return (JavaFXImage) image;
	}

	protected void setFXImage(Image img, WritableImage write) {
		WritableImage oldImg = ((JavaFXImage) img).buffer;
		if (oldImg != write) {
			((JavaFXImage) img).buffer = write;
		}
		img.setDirty(false);
	}

	@Override
	public Image newSnapshot() {
		WritableImage newImage = new WritableImage(toFXImage().buffer.getPixelReader(), (int) width, (int) height);
		return new JavaFXImage(gfx, image.scale(), fxCanvas.snapshot(snapshotParameters, newImage),
				TextureSource.RenderCanvas);
	}

	@Override
	public Image snapshot() {
		if (image == null) {
			WritableImage writeImage = toFXImage().buffer;
			image = new JavaFXImage(gfx, image.scale(), fxCanvas.snapshot(snapshotParameters, writeImage),
					TextureSource.RenderCanvas);
			setFXImage(image, writeImage);
			return image;
		}
		if (image.isDirty() || isDirty) {
			WritableImage writeImage = toFXImage().buffer;
			fxCanvas.snapshot(snapshotParameters, writeImage);
			setFXImage(image, writeImage);
			isDirty = false;
		}
		return image;
	}

	@Override
	public Canvas clip(Path clipPath) {
		((JavaFXPath) clipPath).replay(context);
		context.clip();
		isDirty = true;
		return null;
	}

	@Override
	public Canvas clipRect(float x, float y, float width, float height) {
		context.beginPath();
		context.rect(x, y, width, height);
		context.clip();
		isDirty = true;
		return null;
	}

	@Override
	public Canvas resetClip() {
		return clipRect(0f, 0f, width, height);
	}
	
	@Override
	public Path createPath() {
		return new JavaFXPath();
	}

	@Override
	public Gradient createGradient(Config config) {
		if (config instanceof Gradient.Linear) {
			return JavaFXGradient.create((Gradient.Linear) config);
		} else if (config instanceof Gradient.Radial) {
			return JavaFXGradient.create((Gradient.Radial) config);
		} else {
			throw new IllegalArgumentException("Unknown config: " + config);
		}
	}

	@Override
	public Canvas drawLine(float x0, float y0, float x1, float y1) {
		context.beginPath();
		context.moveTo(x0, y0);
		context.lineTo(x1, y1);
		context.stroke();
		isDirty = true;
		return this;
	}

	@Override
	public Canvas drawPoint(float x, float y) {
		context.beginPath();
		context.moveTo(x, y);
		context.lineTo(x, y);
		context.stroke();
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
		Paint tmp = context.getFill();
		context.setFill(getLColorToFX(color));
		fillText(_font.getLayoutText(text), x, y);
		context.setFill(tmp);
		return this;
	}

	@Override
	public Canvas drawRect(float x, float y, float width, float height, LColor color) {
		Paint tmp = context.getStroke();
		context.setStroke(getLColorToFX(color));
		context.strokeRect(x, y, width, height);
		context.setStroke(tmp);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillCircle(float x, float y, float radius) {
		context.beginPath();
		context.arcTo(x, y, radius, 0f, 2f * MathUtils.PI);
		context.fill();
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillArc(float x1, float y1, float width, float height, float start, float end) {
		if (end - start == 360) {
			context.fillOval(x1, y1, width, height);
		} else {
			context.fillArc(x1, y1, width, height, start, end, ArcType.ROUND);
		}
		isDirty = true;
		return null;
	}

	@Override
	public Canvas fillOval(float x, float y, float width, float height) {
		context.fillOval(x, y, width, height);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillPath(Path path) {
		assert path instanceof JavaFXPath;
		((JavaFXPath) path).replay(context);
		context.fill();
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillRect(float x, float y, float width, float height) {
		context.fillRect(x, y, width, height);
		isDirty = true;
		return this;
	}

	public Canvas fillRect(float x, float y, float width, float height, LColor color) {
		Paint tmp = context.getFill();
		context.setFill(getLColorToFX(color));
		context.fillRect(x, y, width, height);
		context.setFill(tmp);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillRoundRect(float x, float y, float width, float height, float radius) {
		Paint tmp = context.getFill();
		context.setFill(getLColorToFX(color));
		addRoundRectPath(x, y, width, height, radius);
		context.fill();
		context.setFill(tmp);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillText(TextLayout layout, float x, float y) {
		((JavaFXTextLayout) layout).fill(context, x, y);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas restore() {
		context.restore();
		this.isDirty = true;
		return this;
	}

	@Override
	public Canvas rotate(float radians) {
		context.rotate(radians);
		this.isDirty = true;
		return this;
	}

	@Override
	public Canvas scale(float x, float y) {
		context.scale(x, y);
		this.isDirty = true;
		return this;
	}

	@Override
	public Canvas setAlpha(float alpha) {
		context.setGlobalAlpha(alpha);
		this.isDirty = true;
		return this;
	}

	@Override
	public Canvas setLineWidth(float lineWidth) {
		context.setLineWidth(lineWidth);
		this.isDirty = true;
		return this;
	}

	@Override
	public Canvas setCompositeOperation(Composite composite) {
		BlendMode mode = null;
		switch (composite) {
		case SRC_ATOP:
			mode = BlendMode.SRC_ATOP;
			break;
		case MULTIPLY:
			mode = BlendMode.MULTIPLY;
			break;
		case DST_ATOP:
		case DST_IN:
		case DST_OUT:
		case DST_OVER:
		case SRC:
		case SRC_IN:
		case SRC_OUT:
		case SRC_OVER:
		case XOR:
		default:
			mode = BlendMode.SRC_OVER;
			break;
		}
		context.setGlobalBlendMode(mode);
		this.isDirty = true;
		return this;
	}

	@Override
	public Canvas setBlendMethod(final int blend) {
		BlendMode mode = null;
		switch (blend) {
		case BlendMethod.MODE_ADD:
		case BlendMethod.MODE_ALPHA_ONE:
			mode = BlendMode.ADD;
			break;
		case BlendMethod.MODE_ALPHA:
			mode = BlendMode.SRC_OVER;
			break;
		case BlendMethod.MODE_MULTIPLY:
			mode = BlendMode.MULTIPLY;
			break;
		case BlendMethod.MODE_COLOR_MULTIPLY:
			mode = BlendMode.SCREEN;
			break;
		case BlendMethod.MODE_NORMAL:
			mode = BlendMode.SRC_OVER;
			break;
		}
		context.setGlobalBlendMode(mode);
		this.isDirty = true;
		return this;
	}

	@Override
	public Canvas setFillColor(LColor color) {
		context.setFill(getLColorToFX(color));
		this.isDirty = true;
		return this;
	}

	@Override
	public Canvas setFillColor(int color) {
		return setFillColor(tmpColor.setColor(color));
	}

	@Override
	public LColor getStroketoLColor() {
		Paint strokePaint = context.getStroke();
		assert strokePaint instanceof Color;
		LColor color = getFXToLColor((Color) strokePaint);
		return color;
	}

	@Override
	public int getStrokeColor() {
		return getStroketoLColor().getARGB();
	}

	public LColor getFilltoLColor() {
		Paint fillPaint = context.getFill();
		assert fillPaint instanceof Color;
		LColor color = getFXToLColor((Color) fillPaint);
		return color;
	}

	@Override
	public int getFillColor() {
		return getFilltoLColor().getARGB();
	}

	@Override
	public Canvas setColor(int r, int g, int b) {
		int rgb = LColor.getRGB(r, g, b);
		this.setStrokeColor(rgb);
		this.setFillColor(rgb);
		this.isDirty = true;
		return this;
	}

	@Override
	public Canvas setColor(int r, int g, int b, int a) {
		int argb = LColor.getARGB(r, g, b, a);
		this.setStrokeColor(argb);
		this.setFillColor(argb);
		this.isDirty = true;
		return this;
	}

	@Override
	public Canvas setFillGradient(Gradient gradient) {
		assert gradient instanceof JavaFXGradient;
		context.setFill(((JavaFXGradient) gradient).fxpaint);
		this.isDirty = true;
		return this;
	}

	@Override
	public Canvas setLineCap(LineCap cap) {
		StrokeLineCap lineCap = null;
		switch (cap) {
		case BUTT:
			lineCap = StrokeLineCap.BUTT;
			break;
		case ROUND:
			lineCap = StrokeLineCap.ROUND;
			break;
		case SQUARE:
			lineCap = StrokeLineCap.SQUARE;
			break;
		}
		context.setLineCap(lineCap);
		this.isDirty = true;
		return this;
	}

	@Override
	public Canvas setLineJoin(LineJoin join) {
		StrokeLineJoin lineJoin = null;
		switch (join) {
		case BEVEL:
			lineJoin = StrokeLineJoin.BEVEL;
			break;
		case MITER:
			lineJoin = StrokeLineJoin.MITER;
			break;
		case ROUND:
			lineJoin = StrokeLineJoin.ROUND;
			break;
		}
		context.setLineJoin(lineJoin);
		this.isDirty = true;
		return this;
	}

	@Override
	public Canvas setMiterLimit(float miter) {
		context.setMiterLimit(miter);
		this.isDirty = true;
		return this;
	}

	@Override
	public Canvas setStrokeColor(LColor color) {
		context.setStroke(getLColorToFX(color));
		this.isDirty = true;
		return this;
	}

	@Override
	public Canvas setStrokeColor(int color) {
		return setStrokeColor(tmpColor.setColor(color));
	}

	@Override
	public Canvas setStrokeWidth(float strokeWidth) {
		context.setLineWidth(strokeWidth);
		this.isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeCircle(float x, float y, float radius) {
		context.beginPath();
		context.arcTo(x, y, radius, 0, 2 * MathUtils.PI);
		context.stroke();
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokePath(Path path) {
		assert path instanceof JavaFXPath;
		((JavaFXPath) path).replay(context);
		context.stroke();
		isDirty = true;
		return this;
	}

	@Override
	public Canvas drawArc(float x, float y, float w, float h, float startAngle, float endAngle, LColor color) {
		Paint tmp = context.getStroke();
		context.setStroke(getLColorToFX(color));
		context.strokeArc(x, y, w, h, startAngle, endAngle, ArcType.ROUND);
		context.setStroke(tmp);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas drawOval(float x, float y, float w, float h, LColor color) {
		Paint tmp = context.getStroke();
		context.setStroke(getLColorToFX(color));
		context.strokeOval(x, y, w, h);
		context.setStroke(tmp);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeRect(float x, float y, float width, float height) {
		context.strokeRect(x, y, width, height);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeRoundRect(float x, float y, float width, float height, float radius) {
		addRoundRectPath(x, y, width, height, radius);
		context.stroke();
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeText(TextLayout layout, float x, float y) {
		((JavaFXTextLayout) layout).stroke(context, x, y);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas transform(float m11, float m12, float m21, float m22, float dx, float dy) {
		context.transform(m11, m12, m21, m22, dx, dy);
		isDirty = true;
		return this;
	}

	private void addRoundRectPath(float x, float y, float width, float height, float radius) {
		float midx = x + width / 2, midy = y + height / 2, maxx = x + width, maxy = y + height;
		context.beginPath();
		context.moveTo(x, midy);
		context.arcTo(x, y, midx, y, radius);
		context.arcTo(maxx, y, maxx, midy, radius);
		context.arcTo(maxx, maxy, midx, maxy, radius);
		context.arcTo(x, maxy, x, midy, radius);
		context.closePath();
		isDirty = true;
	}

	@Override
	public Canvas setTransform(Affine2f aff) {
		context.setTransform(aff.m00, aff.m01, aff.m10, aff.m11, aff.tx, aff.ty);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas translate(float x, float y) {
		context.translate(x, y);
		return this;
	}

	@Override
	protected JavaFXCanvas gc() {
		return this;
	}

	@Override
	public boolean isMainCanvas() {
		return graphicsMain;
	}

	@Override
	public void closeImpl() {
		if (context != null) {
			context = null;
			closed = true;
		}
	}

}
