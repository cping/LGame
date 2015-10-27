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
package loon.html5.gwt;

import loon.Graphics;
import loon.canvas.Canvas;
import loon.canvas.Gradient;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.canvas.Path;
import loon.canvas.Pattern;
import loon.font.TextLayout;

import com.google.gwt.canvas.dom.client.Context2d;

public class GWTCanvas extends Canvas {

	private final Context2d ctx;

	private int strokeColor;

	private int fillColor;

	public GWTCanvas(Graphics gfx, GWTImage image) {
		super(gfx, image);
		this.ctx = image.canvas.getContext2d();
		float scale = image.scale().factor;
		ctx.scale(scale, scale);
	}

	@Override
	public int getStrokeColor() {
		return strokeColor;
	}

	@Override
	public int getFillColor() {
		return fillColor;
	}

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
		return null;
	}

	@Override
	public Canvas setColor(int r, int g, int b) {
		int rgb = LColor.getRGB(r, g, b);
		this.setStrokeColor(rgb);
		this.setFillColor(rgb);
		return null;
	}
	
	@Override
	public Canvas clear() {
		return clearRect(0, 0, width, height);
	}

	@Override
	public Canvas clearRect(float x, float y, float width, float height) {
		ctx.clearRect(x, y, width, height);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas clip(Path path) {
		assert path instanceof GWTPath;
		((GWTPath) path).replay(ctx);
		ctx.clip();
		return this;
	}

	@Override
	public Canvas clipRect(float x, float y, float width, float height) {
		ctx.beginPath();
		ctx.rect(x, y, width, height);
		ctx.clip();
		return this;
	}

	@Override
	public Path createPath() {
		return new GWTPath();
	}

	@Override
	public Gradient createGradient(Gradient.Config config) {
		return new GWTGradient(ctx, config);
	}

	@Override
	public Canvas drawLine(float x0, float y0, float x1, float y1) {
		ctx.beginPath();
		ctx.moveTo(x0, y0);
		ctx.lineTo(x1, y1);
		ctx.stroke();
		isDirty = true;
		return this;
	}

	@Override
	public Canvas drawPoint(float x, float y) {
		ctx.beginPath();
		ctx.moveTo(x, y);
		ctx.lineTo(x, y);
		ctx.stroke();
		isDirty = true;
		return this;
	}

	@Override
	public Canvas drawText(String text, float x, float y) {
		ctx.fillText(text, x, y);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillCircle(float x, float y, float radius) {
		ctx.beginPath();
		ctx.arc(x, y, radius, 0, 2 * Math.PI);
		ctx.fill();
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillPath(Path path) {
		assert path instanceof GWTPath;
		((GWTPath) path).replay(ctx);
		ctx.fill();
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillRect(float x, float y, float w, float h) {
		ctx.fillRect(x, y, w, h);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillRoundRect(float x, float y, float w, float h, float radius) {
		addRoundRectPath(x, y, w, h, radius);
		ctx.fill();
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillText(TextLayout layout, float x, float y) {
		((GWTTextLayout) layout).fill(ctx, x, y);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas restore() {
		ctx.restore();
		return this;
	}

	@Override
	public Canvas rotate(float radians) {
		ctx.rotate(radians);
		return this;
	}

	@Override
	public Canvas save() {
		ctx.save();
		return this;
	}

	@Override
	public Canvas scale(float x, float y) {
		ctx.scale(x, y);
		return this;
	}

	@Override
	public Canvas setAlpha(float alpha) {
		ctx.setGlobalAlpha(alpha);
		return this;
	}

	@Override
	public Canvas setCompositeOperation(Canvas.Composite composite) {
		ctx.setGlobalCompositeOperation(convertComposite(composite));
		return this;
	}

	@Override
	public Canvas setFillColor(int color) {
		ctx.setFillStyle(GWTGraphics.cssColorString(color));
		return this;
	}

	@Override
	public Canvas setFillGradient(Gradient gradient) {
		assert gradient instanceof GWTGradient;
		ctx.setFillStyle(((GWTGradient) gradient).gradient);
		return this;
	}

	@Override
	public Canvas setFillPattern(Pattern pattern) {
		assert pattern instanceof GWTPattern;
		ctx.setFillStyle(((GWTPattern) pattern).pattern(ctx));
		return this;
	}

	@Override
	public Canvas setLineCap(LineCap cap) {
		ctx.setLineCap(convertLineCap(cap));
		return this;
	}

	@Override
	public Canvas setLineJoin(LineJoin join) {
		ctx.setLineJoin(convertLineJoin(join));
		return this;
	}

	@Override
	public Canvas setMiterLimit(float miter) {
		ctx.setMiterLimit(miter);
		return this;
	}

	@Override
	public Canvas setStrokeColor(int color) {
		strokeColor = color;
		ctx.setStrokeStyle(GWTGraphics.cssColorString(color));
		return this;
	}

	@Override
	public Canvas setStrokeWidth(float w) {
		ctx.setLineWidth(w);
		return this;
	}

	@Override
	public Image snapshot() {
		return image;
	}

	@Override
	public Canvas strokeCircle(float x, float y, float radius) {
		ctx.beginPath();
		ctx.arc(x, y, radius, 0, 2 * Math.PI);
		ctx.stroke();
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokePath(Path path) {
		assert path instanceof GWTPath;
		((GWTPath) path).replay(ctx);
		ctx.stroke();
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeRect(float x, float y, float w, float h) {
		ctx.strokeRect(x, y, w, h);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeRoundRect(float x, float y, float w, float h,
			float radius) {
		addRoundRectPath(x, y, w, h, radius);
		ctx.stroke();
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeText(TextLayout layout, float x, float y) {
		((GWTTextLayout) layout).stroke(ctx, x, y);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas transform(float m11, float m12, float m21, float m22,
			float dx, float dy) {
		ctx.transform(m11, m12, m21, m22, dx, dy);
		return this;
	}

	@Override
	public Canvas translate(float x, float y) {
		ctx.translate(x, y);
		return this;
	}

	@Override
	protected Context2d gc() {
		return ctx;
	}

	private void addRoundRectPath(float x, float y, float width, float height,
			float radius) {
		float midx = x + width / 2, midy = y + height / 2, maxx = x + width, maxy = y
				+ height;
		ctx.beginPath();
		ctx.moveTo(x, midy);
		ctx.arcTo(x, y, midx, y, radius);
		ctx.arcTo(maxx, y, maxx, midy, radius);
		ctx.arcTo(maxx, maxy, midx, maxy, radius);
		ctx.arcTo(x, maxy, x, midy, radius);
		ctx.closePath();
	}

	private String convertComposite(Canvas.Composite composite) {
		switch (composite) {
		case SRC:
			return "copy";
		case DST_ATOP:
			return "destination-atop";
		case SRC_OVER:
			return "source-over";
		case DST_OVER:
			return "destination-over";
		case SRC_IN:
			return "source-in";
		case DST_IN:
			return "destination-in";
		case SRC_OUT:
			return "source-out";
		case DST_OUT:
			return "destination-out";
		case SRC_ATOP:
			return "source-atop";
		case XOR:
			return "xor";
		default:
			return "copy";
		}
	}

	private Context2d.LineCap convertLineCap(LineCap cap) {
		switch (cap) {
		case BUTT:
			return Context2d.LineCap.BUTT;
		case ROUND:
			return Context2d.LineCap.ROUND;
		case SQUARE:
			return Context2d.LineCap.SQUARE;
		}
		return Context2d.LineCap.SQUARE;
	}

	private Context2d.LineJoin convertLineJoin(LineJoin join) {
		switch (join) {
		case BEVEL:
			return Context2d.LineJoin.BEVEL;
		case MITER:
			return Context2d.LineJoin.MITER;
		case ROUND:
			return Context2d.LineJoin.ROUND;
		}
		return Context2d.LineJoin.ROUND;
	}


}
