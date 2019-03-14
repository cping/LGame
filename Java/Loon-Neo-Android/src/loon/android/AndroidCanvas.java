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
package loon.android;

import java.util.LinkedList;

import loon.Graphics;
import loon.canvas.Canvas;
import loon.canvas.Gradient;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.canvas.Path;
import loon.canvas.Pattern;
import loon.font.TextLayout;
import loon.utils.MathUtils;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;

public class AndroidCanvas extends Canvas {

	private static Matrix m = new Matrix();
	private static Rect srcR = new Rect();
	private static RectF dstR = new RectF();

	private final android.graphics.Canvas canvas;
	private final LinkedList<AndroidCanvasState> paintStack = new LinkedList<>();

	public AndroidCanvas(Graphics gfx, AndroidImage image) {
		super(gfx, image);
		canvas = new android.graphics.Canvas(image.bitmap());
		paintStack.addFirst(new AndroidCanvasState());
		float factor = image.scale().factor;
		scale(factor, factor);
	}

	void draw(Bitmap bitmap, float x, float y, float w, float h, float x1,
			float y1, float w1, float h1) {
		srcR.set((int) x1, (int) y1, (int) w1, (int) h1);
		dstR.set(x, y, x + w, y + h);
		canvas.drawBitmap(bitmap, srcR, dstR, currentState().prepareImage());
		isDirty = true;
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
	public int getStrokeColor() {
		return currentState().strokeColor;
	}

	@Override
	public int getFillColor() {
		return currentState().fillColor;
	}

	public float alpha() {
		return currentState().alpha;
	}

	@Override
	public Canvas clear() {
		canvas.drawColor(0, PorterDuff.Mode.SRC);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas clearRect(float x, float y, float width, float height) {
		//api28开始不支持CLIP_SAVE_FLAG在Android Canvas外部使用
		//canvas.save(android.graphics.Canvas.CLIP_SAVE_FLAG);
		canvas.save();
		canvas.clipRect(x, y, x + width, y + height);
		canvas.drawColor(0, PorterDuff.Mode.SRC);
		canvas.restore();
		isDirty = true;
		return this;
	}

	@Override
	public Canvas clip(Path clipPath) {
		canvas.clipPath(((AndroidPath) clipPath).path);
		return this;
	}

	@Override
	public Canvas clipRect(float x, float y, float width, float height) {
		canvas.clipRect(x, y, x + width, y + height);
		return this;
	}

	@Override
	public Path createPath() {
		return new AndroidPath();
	}

	@Override
	public Gradient createGradient(Gradient.Config cfg) {
		return new AndroidGradient(cfg);
	}

	@Override
	public Canvas drawLine(float x0, float y0, float x1, float y1) {
		canvas.drawLine(x0, y0, x1, y1, currentState().prepareStroke());
		isDirty = true;
		return this;
	}

	@Override
	public Canvas drawPoint(float x, float y) {
		canvas.drawPoint(x, y, currentState().prepareStroke());
		isDirty = true;
		return this;
	}

	@Override
	public Canvas drawText(String text, float x, float y) {
		return fillText(_font.getLayoutText(text), x, y);
	}

	@Override
	public Canvas fillCircle(float x, float y, float radius) {
		canvas.drawCircle(x, y, radius, currentState().prepareFill());
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillPath(Path path) {
		canvas.drawPath(((AndroidPath) path).path, currentState().prepareFill());
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillRect(float x, float y, float width, float height) {
		float left = x;
		float top = y;
		float right = left + width;
		float bottom = top + height;
		canvas.drawRect(left, top, right, bottom, currentState().prepareFill());
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillRoundRect(float x, float y, float width, float height,
			float radius) {
		canvas.translate(x, y);
		dstR.set(0, 0, width, height);
		canvas.drawRoundRect(dstR, radius, radius, currentState().prepareFill());
		canvas.translate(-x, -y);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillText(TextLayout layout, float x, float y) {
		((AndroidTextLayout) layout).draw(canvas, x, y, currentState()
				.prepareFill());
		isDirty = true;
		return this;
	}

	@Override
	public Canvas restore() {
		canvas.restore();
		paintStack.removeFirst();
		return this;
	}

	@Override
	public Canvas rotate(float angle) {
		canvas.rotate(MathUtils.degToRad(angle));
		return this;
	}

	@Override
	public Canvas save() {
		canvas.save();
		paintStack.addFirst(new AndroidCanvasState(currentState()));
		return this;
	}

	@Override
	public Canvas scale(float x, float y) {
		canvas.scale(x, y);
		return this;
	}

	@Override
	public Canvas setAlpha(float alpha) {
		currentState().setAlpha(alpha);
		return this;
	}

	@Override
	public Canvas setCompositeOperation(Composite composite) {
		currentState().setCompositeOperation(composite);
		return this;
	}

	@Override
	public Canvas setFillColor(int color) {
		currentState().setFillColor(color);
		return this;
	}

	@Override
	public Canvas setFillGradient(Gradient gradient) {
		currentState().setFillGradient((AndroidGradient) gradient);
		return this;
	}

	@Override
	public Canvas setFillPattern(Pattern pattern) {
		currentState().setFillPattern((AndroidPattern) pattern);
		return this;
	}

	@Override
	public Canvas setLineCap(LineCap cap) {
		currentState().setLineCap(cap);
		return this;
	}

	@Override
	public Canvas setLineJoin(LineJoin join) {
		currentState().setLineJoin(join);
		return this;
	}

	@Override
	public Canvas setMiterLimit(float miter) {
		currentState().setMiterLimit(miter);
		return this;
	}

	@Override
	public Canvas setStrokeColor(int color) {
		currentState().setStrokeColor(color);
		return this;
	}

	@Override
	public Canvas setStrokeWidth(float strokeWidth) {
		currentState().setStrokeWidth(strokeWidth);
		return this;
	}

	@Override
	public Image snapshot() {
		Bitmap bitmap = ((AndroidImage) this.image).bitmap();
		return new AndroidImage(gfx, image.scale(), bitmap.copy(
				bitmap.getConfig(), false), "<canvas>");
	}

	@Override
	public Canvas strokeCircle(float x, float y, float radius) {
		canvas.drawCircle(x, y, radius, currentState().prepareStroke());
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokePath(Path path) {
		canvas.drawPath(((AndroidPath) path).path, currentState()
				.prepareStroke());
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeRect(float x, float y, float width, float height) {
		float left = x;
		float top = y;
		float right = left + width;
		float bottom = top + height;
		canvas.drawRect(left, top, right, bottom, currentState()
				.prepareStroke());
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeRoundRect(float x, float y, float width, float height,
			float radius) {
		canvas.translate(x, y);
		dstR.set(0, 0, width, height);
		canvas.drawRoundRect(dstR, radius, radius, currentState()
				.prepareStroke());
		canvas.translate(-x, -y);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas strokeText(TextLayout layout, float x, float y) {
		((AndroidTextLayout) layout).draw(canvas, x, y, currentState()
				.prepareStroke());
		isDirty = true;
		return this;
	}

	@Override
	public Canvas transform(float m11, float m12, float m21, float m22,
			float dx, float dy) {
		m.setValues(new float[] { m11, m21, dx, m12, m22, dy, 0, 0, 1 });
		canvas.concat(m);
		return this;
	}

	@Override
	public Canvas translate(float x, float y) {
		canvas.translate(x, y);
		return this;
	}

	@Override
	protected AndroidCanvas gc() {
		return this;
	}

	private AndroidCanvasState currentState() {
		return paintStack.peek();
	}

}
