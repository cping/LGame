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
package loon.robovm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import loon.Graphics;
import loon.canvas.Canvas;
import loon.canvas.Gradient;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.canvas.Path;
import loon.canvas.Pattern;
import loon.font.TextLayout;
import loon.utils.Scale;

import org.robovm.apple.coregraphics.CGAffineTransform;
import org.robovm.apple.coregraphics.CGBitmapContext;
import org.robovm.apple.coregraphics.CGBlendMode;
import org.robovm.apple.coregraphics.CGImage;
import org.robovm.apple.coregraphics.CGLineCap;
import org.robovm.apple.coregraphics.CGLineJoin;
import org.robovm.apple.coregraphics.CGMutablePath;
import org.robovm.apple.coregraphics.CGRect;

public class RoboVMCanvas extends Canvas {

	private float strokeWidth = 1;
	private int strokeColor = 0xFF000000;
	private int fillColor = 0xFF000000;
	private CGBitmapContext bctx;

	private LinkedList<RoboVMCanvasState> states = new LinkedList<RoboVMCanvasState>();

	public RoboVMCanvas(Graphics gfx, RoboVMCanvasImage image) {
		super(gfx, image);

		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException(

			"Invalid size " + width + "x" + height);
		}
		states.addFirst(new RoboVMCanvasState());

		bctx = image.bctx;

		bctx.clearRect(new CGRect(0, 0, texWidth(), texHeight()));

		Scale scale = image.scale();
		bctx.translateCTM(0, scale.scaled(height));
		bctx.scaleCTM(scale.factor, -scale.factor);
	}

	public int texWidth() {
		return image.pixelWidth();
	}

	public int texHeight() {
		return image.pixelHeight();
	}

	public CGImage cgimage() {
		return bctx.toImage();
	}

	@Override
	public Canvas clear() {
		bctx.clearRect(new CGRect(0, 0, texWidth(), texHeight()));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas clearRect(float x, float y, float width, float height) {
		bctx.clearRect(new CGRect(x, y, width, height));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas clip(Path clipPath) {
		bctx.addPath(((RoboVMPath) clipPath).cgPath);
		bctx.clip();
		return this;
	}

	@Override
	public Canvas clipRect(float x, float y, float width, float height) {
		bctx.clipToRect(new CGRect(x, y, width, height));
		return this;
	}

	@Override
	public Path createPath() {
		return new RoboVMPath();
	}

	@Override
	public Gradient createGradient(Gradient.Config cfg) {
		if (cfg instanceof Gradient.Linear)
			return new RoboVMGradient.Linear((Gradient.Linear) cfg);
		else if (cfg instanceof Gradient.Radial)
			return new RoboVMGradient.Radial((Gradient.Radial) cfg);
		else
			throw new IllegalArgumentException("Unknown config: " + cfg);
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
	public int getStrokeColor() {
		return strokeColor;
	}

	@Override
	public int getFillColor() {
		return fillColor;
	}

	@Override
	public void close() {
		((RoboVMCanvasImage) image).dispose();
	}

	@Override
	public Canvas drawLine(float x0, float y0, float x1, float y1) {
		bctx.beginPath();
		bctx.moveToPoint(x0, y0);
		bctx.addLineToPoint(x1, y1);
		bctx.strokePath();
		isDirty = true;
		return this;
	}

	@Override
	public Canvas drawPoint(float x, float y) {
		save();
		setStrokeWidth(0.5f);
		strokeRect(x + 0.25f, y + 0.25f, 0.5f, 0.5f);
		restore();
		return this;
	}

	@Override
	public Canvas drawText(String text, float x, float y) {
		return fillText(_font.getLayoutText(text), x, y);
	}

	@Override
	public Canvas fillCircle(float x, float y, float radius) {
		RoboVMGradient gradient = currentState().gradient;
		if (gradient == null) {
			bctx.fillEllipseInRect(new CGRect(x - radius, y - radius,
					2 * radius, 2 * radius));
		} else {
			CGMutablePath cgPath = CGMutablePath.createMutable();
			cgPath.addArc(null, x, y, radius, 0, 2 * Math.PI, false);
			bctx.addPath(cgPath);
			bctx.clip();
			gradient.fill(bctx);
		}
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillPath(Path path) {
		bctx.addPath(((RoboVMPath) path).cgPath);
		RoboVMGradient gradient = currentState().gradient;
		if (gradient == null) {
			bctx.fillPath();
		} else {
			bctx.clip();
			gradient.fill(bctx);
		}
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillRect(float x, float y, float width, float height) {
		RoboVMGradient gradient = currentState().gradient;
		if (gradient == null) {
			bctx.fillRect(new CGRect(x, y, width, height));
		} else {
			bctx.saveGState();
			bctx.clipToRect(new CGRect(x, y, width, height));
			gradient.fill(bctx);
			bctx.restoreGState();
		}
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillRoundRect(float x, float y, float width, float height,
			float radius) {
		addRoundRectPath(x, y, width, height, radius);
		RoboVMGradient gradient = currentState().gradient;
		if (gradient == null) {
			bctx.fillPath();
		} else {
			bctx.clip();
			gradient.fill(bctx);
		}
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillText(TextLayout layout, float x, float y) {
		RoboVMGradient gradient = currentState().gradient;
		RoboVMTextLayout ilayout = (RoboVMTextLayout) layout;
		if (gradient == null) {
			ilayout.fill(bctx, x, y, fillColor);
		} else {
			CGBitmapContext maskContext = RoboVMGraphics.createCGBitmap(
					texWidth(), texHeight());
			maskContext.clearRect(new CGRect(0, 0, texWidth(), texHeight()));
			float scale = image.scale().factor;
			maskContext.scaleCTM(scale, scale);
			setFillColor(maskContext, 0xFFFFFFFF);
			ilayout.fill(maskContext, 0, 0, fillColor);
			bctx.saveGState();
			bctx.clipToMask(new CGRect(x, y, width, height),
					maskContext.toImage());
			gradient.fill(bctx);
			bctx.restoreGState();
			maskContext.dispose();
		}

		isDirty = true;
		return this;
	}

	@Override
	public Canvas restore() {
		states.removeFirst();
		bctx.restoreGState();
		return this;
	}

	@Override
	public Canvas rotate(float radians) {
		bctx.rotateCTM(radians);
		return this;
	}

	@Override
	public Canvas save() {
		states.addFirst(new RoboVMCanvasState(currentState()));
		bctx.saveGState();
		return this;
	}

	@Override
	public Canvas scale(float x, float y) {
		bctx.scaleCTM(x, y);
		return this;
	}

	@Override
	public Canvas setAlpha(float alpha) {
		bctx.setAlpha(alpha);
		return this;
	}

	@Override
	public Canvas setCompositeOperation(Composite composite) {
		bctx.setBlendMode(compToBlend.get(composite));
		return this;
	}

	@Override
	public Canvas setFillColor(int color) {
		this.fillColor = color;
		currentState().gradient = null;
		setFillColor(bctx, color);
		return this;
	}

	@Override
	public Canvas setFillGradient(Gradient gradient) {
		currentState().gradient = (RoboVMGradient) gradient;
		return this;
	}

	@Override
	public Canvas setFillPattern(Pattern pattern) {
		currentState().gradient = null;
		bctx.setFillColor(((RoboVMPattern) pattern).colorWithPattern);
		return this;
	}

	@Override
	public Canvas setLineCap(LineCap cap) {
		bctx.setLineCap(decodeCap.get(cap));
		return this;
	}

	@Override
	public Canvas setLineJoin(LineJoin join) {
		bctx.setLineJoin(decodeJoin.get(join));
		return this;
	}

	@Override
	public Canvas setMiterLimit(float miter) {
		bctx.setMiterLimit(miter);
		return this;
	}

	@Override
	public Canvas setStrokeColor(int color) {
		this.strokeColor = color;
		setStrokeColor(bctx, color);
		return this;
	}

	@Override
	public Canvas setStrokeWidth(float strokeWidth) {
		this.strokeWidth = strokeWidth;
		bctx.setLineWidth(strokeWidth);
		return this;
	}

	@Override
	public Image snapshot() {
		return new RoboVMImage(gfx, image.scale(),
				((RoboVMImage) image).cgImage(), "<canvas>");
	}

	@Override
	public Canvas strokeCircle(float x, float y, float radius) {
		bctx.strokeEllipseInRect(new CGRect(x - radius, y - radius, 2 * radius,
				2 * radius));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokePath(Path path) {
		bctx.addPath(((RoboVMPath) path).cgPath);
		bctx.strokePath();
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeRect(float x, float y, float width, float height) {
		bctx.strokeRect(new CGRect(x, y, width, height));
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeRoundRect(float x, float y, float width, float height,
			float radius) {
		addRoundRectPath(x, y, width, height, radius);
		bctx.strokePath();
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeText(TextLayout layout, float x, float y) {
		((RoboVMTextLayout) layout)
				.stroke(bctx, x, y, strokeWidth, strokeColor);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas transform(float m11, float m12, float m21, float m22,
			float dx, float dy) {
		bctx.concatCTM(new CGAffineTransform(m11, m12, m21, m22, dx, dy));
		return this;
	}

	@Override
	public Canvas translate(float x, float y) {
		bctx.translateCTM(x, y);
		return this;
	}

	@Override
	protected CGBitmapContext gc() {
		return bctx;
	}

	private void addRoundRectPath(float x, float y, float width, float height,
			float radius) {
		float midx = x + width / 2, midy = y + height / 2, maxx = x + width, maxy = y
				+ height;
		bctx.beginPath();
		bctx.moveToPoint(x, midy);
		bctx.addArcToPoint(x, y, midx, y, radius);
		bctx.addArcToPoint(maxx, y, maxx, midy, radius);
		bctx.addArcToPoint(maxx, maxy, midx, maxy, radius);
		bctx.addArcToPoint(x, maxy, x, midy, radius);
		bctx.closePath();
	}

	private RoboVMCanvasState currentState() {
		return states.peek();
	}

	static void setStrokeColor(CGBitmapContext bctx, int color) {
		float blue = (color & 0xFF) / 255f;
		color >>= 8;
		float green = (color & 0xFF) / 255f;
		color >>= 8;
		float red = (color & 0xFF) / 255f;
		color >>= 8;
		float alpha = (color & 0xFF) / 255f;
		bctx.setRGBStrokeColor(red, green, blue, alpha);
	}

	static void setFillColor(CGBitmapContext bctx, int color) {
		float blue = (color & 0xFF) / 255f;
		color >>= 8;
		float green = (color & 0xFF) / 255f;
		color >>= 8;
		float red = (color & 0xFF) / 255f;
		color >>= 8;
		float alpha = (color & 0xFF) / 255f;
		bctx.setRGBFillColor(red, green, blue, alpha);
	}

	private static Map<Composite, CGBlendMode> compToBlend = new HashMap<Composite, CGBlendMode>();
	static {
		compToBlend.put(Composite.SRC, CGBlendMode.Copy);
		compToBlend.put(Composite.DST_ATOP, CGBlendMode.DestinationAtop);
		compToBlend.put(Composite.SRC_OVER, CGBlendMode.Normal);
		compToBlend.put(Composite.DST_OVER, CGBlendMode.DestinationOver);
		compToBlend.put(Composite.SRC_IN, CGBlendMode.SourceIn);
		compToBlend.put(Composite.DST_IN, CGBlendMode.DestinationIn);
		compToBlend.put(Composite.SRC_OUT, CGBlendMode.SourceOut);
		compToBlend.put(Composite.DST_OUT, CGBlendMode.DestinationOut);
		compToBlend.put(Composite.SRC_ATOP, CGBlendMode.SourceAtop);
		compToBlend.put(Composite.XOR, CGBlendMode.XOR);
		compToBlend.put(Composite.MULTIPLY, CGBlendMode.Multiply);
	}

	private static Map<LineCap, CGLineCap> decodeCap = new HashMap<LineCap, CGLineCap>();
	static {
		decodeCap.put(LineCap.BUTT, CGLineCap.Butt);
		decodeCap.put(LineCap.ROUND, CGLineCap.Round);
		decodeCap.put(LineCap.SQUARE, CGLineCap.Square);
	}

	private static Map<LineJoin, CGLineJoin> decodeJoin = new HashMap<LineJoin, CGLineJoin>();
	static {
		decodeJoin.put(LineJoin.BEVEL, CGLineJoin.Bevel);
		decodeJoin.put(LineJoin.MITER, CGLineJoin.Miter);
		decodeJoin.put(LineJoin.ROUND, CGLineJoin.Round);
	}

}
