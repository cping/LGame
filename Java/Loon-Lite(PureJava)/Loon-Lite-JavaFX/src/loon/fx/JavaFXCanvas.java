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

import java.util.LinkedList;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import loon.Graphics;
import loon.canvas.Canvas;
import loon.canvas.Gradient;
import loon.canvas.Gradient.Config;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.canvas.Path;
import loon.canvas.Pattern;
import loon.font.TextLayout;

public class JavaFXCanvas extends Canvas {

	private final LinkedList<JavaFXCanvasState> paintStack = new LinkedList<JavaFXCanvasState>();
	final javafx.scene.canvas.Canvas fxCanvas;
	final WritableImage fxImage;
	final GraphicsContext context;

	LColor color;
	boolean isDirty;
	
	protected JavaFXCanvas(Graphics gfx, JavaFXImage image) {
		super(gfx, image);
		this.fxImage = image.fxImage();
		this.fxCanvas = new javafx.scene.canvas.Canvas(image.getWidth(), image.getHeight());
		context = fxCanvas.getGraphicsContext2D();
		paintStack.addFirst(new JavaFXCanvasState(context));
	}

	@Override
	public Canvas save() {
		paintStack.addFirst(new JavaFXCanvasState(context));
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
		return javafx.scene.paint.Color.rgb(c.getRed(), c.getGreen(), c.getBlue(), c.a);
	}

	private Canvas applyColor(LColor c) {
		this.color = c;
		Color fxcolor = getLColorToFX(c);
		context.setFill(fxcolor);
		context.setStroke(fxcolor);
		return this;
	}

	@Override
	public Canvas clear() {
		Paint tmp = context.getFill();
		context.setFill(Color.BLACK);
		context.clearRect(0, 0, fxImage.getWidth(), fxImage.getHeight());
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

	@Override
	public Image snapshot() {
		return new JavaFXImage(gfx, image.scale(), fxCanvas.snapshot(new SnapshotParameters(), fxImage), "<canvas>");
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
	public Path createPath() {
		return new JavaFXPath();
	}

	@Override
	public Gradient createGradient(Config config) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Canvas fillCircle(float x, float y, float radius) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Canvas fillPath(Path path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Canvas fillRect(float x, float y, float width, float height) {
		context.fillRect(x, y, width,height);
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillRoundRect(float x, float y, float width, float height, float radius) {
		addRoundRectPath(x, y, width, height, radius);
		context.fill();
		isDirty = true;
		return this;
	}

	@Override
	public Canvas fillText(TextLayout text, float x, float y) {
		
		return null;
	}

	@Override
	public Canvas restore() {
		paintStack.peek().restore();
		paintStack.removeFirst();
		return this;
	}

	@Override
	public Canvas rotate(float radians) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Canvas scale(float x, float y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Canvas setAlpha(float alpha) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Canvas setCompositeOperation(Composite composite) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Canvas setFillColor(int color) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStrokeColor() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFillColor() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Canvas setColor(int r, int g, int b) {
		// TODO Auto-generated method stub
		return null;
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
	public Canvas setFillGradient(Gradient gradient) {
		return null;
	}


	@Override
	public Canvas setLineCap(LineCap cap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Canvas setLineJoin(LineJoin join) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Canvas setMiterLimit(float miter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Canvas setStrokeColor(int color) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Canvas setStrokeWidth(float strokeWidth) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Canvas strokeCircle(float x, float y, float radius) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Canvas strokePath(Path path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Canvas strokeRect(float x, float y, float width, float height) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Canvas strokeRoundRect(float x, float y, float width, float height, float radius) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Canvas strokeText(TextLayout text, float x, float y) {
		return null;
	}

	@Override
	public Canvas transform(float m11, float m12, float m21, float m22, float dx, float dy) {
		context.transform(m11, m12, m21, m22, dx, dy);
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

	protected JavaFXCanvasState currentState() {
		return paintStack.peek();
	}

	private void addRoundRectPath(float x, float y, float width, float height,
			float radius) {
		float midx = x + width / 2, midy = y + height / 2, maxx = x + width, maxy = y
				+ height;
		context.beginPath();
		context.moveTo(x, midy);
		context.arcTo(x, y, midx, y, radius);
		context.arcTo(maxx, y, maxx, midy, radius);
		context.arcTo(maxx, maxy, midx, maxy, radius);
		context.arcTo(x, maxy, x, midy, radius);
		context.closePath();
	}
}
