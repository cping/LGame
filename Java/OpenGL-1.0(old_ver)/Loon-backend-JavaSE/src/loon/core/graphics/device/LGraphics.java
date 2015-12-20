/**
 * Copyright 2008 - 2009
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
 * @version 0.1
 */
package loon.core.graphics.device;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;

import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RenderedImage;
import java.awt.image.VolatileImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;

import loon.JavaSEGraphicsUtils;
import loon.core.geom.Triangle2f;

public class LGraphics implements LTrans {

	class JavaSEGraphics2DStore {

		private Paint paint;

		private Font font;

		private Stroke stroke;

		private AffineTransform transform;

		private Composite composite;

		private Shape clip;

		private RenderingHints renderingHints;

		private Color color;

		private Color background;

		public void save(Graphics2D g2d) {
			paint = g2d.getPaint();
			font = g2d.getFont();
			stroke = g2d.getStroke();
			transform = g2d.getTransform();
			composite = g2d.getComposite();
			clip = g2d.getClip();
			renderingHints = g2d.getRenderingHints();
			color = g2d.getColor();
			background = g2d.getBackground();
		}

		public void restore(Graphics2D g2d) {
			g2d.setPaint(paint);
			g2d.setFont(font);
			g2d.setStroke(stroke);
			g2d.setTransform(transform);
			g2d.setComposite(composite);
			g2d.setClip(clip);
			g2d.setRenderingHints(renderingHints);
			g2d.setColor(color);
			g2d.setBackground(background);
		}

		public Color getBackground() {
			return background;
		}

		public Shape getClip() {
			return clip;
		}

		public Color getColor() {
			return color;
		}

		public Composite getComposite() {
			return composite;
		}

		public Font getFont() {
			return font;
		}

		public Paint getPaint() {
			return paint;
		}

		public RenderingHints getRenderingHints() {
			return renderingHints;
		}

		public void setRenderingHints(RenderingHints renderingHints) {
			this.renderingHints = renderingHints;
		}

		public Stroke getStroke() {
			return stroke;
		}

		public AffineTransform getTransform() {
			return transform;
		}

	}

	final static public double ANGLE_90 = Math.PI / 2;

	final static public double ANGLE_270 = Math.PI * 3 / 2;

	public static final int HCENTER = 1;

	public static final int VCENTER = 2;

	public static final int LEFT = 4;

	public static final int RIGHT = 8;

	public static final int TOP = 16;

	public static final int BOTTOM = 32;

	public static final int BASELINE = 64;

	public static final int SOLID = 0;

	public static final int DOTTED = 1;

	final private JavaSEGraphics2DStore store = new JavaSEGraphics2DStore();

	final private Graphics2D g2d;

	private int strokeStyle = SOLID;

	private boolean isClose;

	private int width, height;

	public LGraphics(BufferedImage awtImage) {
		this.width = awtImage.getWidth();
		this.height = awtImage.getHeight();
		this.g2d = awtImage.createGraphics();
		this.g2d.setClip(0, 0, width, height);
		JavaSEGraphicsUtils.setPoorRenderingHints(g2d);
		this.store.save(g2d);
	}

	public LGraphics(VolatileImage awtImage) {
		this.width = awtImage.getWidth();
		this.height = awtImage.getHeight();
		this.g2d = awtImage.createGraphics();
		this.g2d.setClip(0, 0, width, height);
		JavaSEGraphicsUtils.setPoorRenderingHints(g2d);
		this.store.save(g2d);
	}

	public void restore() {
		this.store.restore(g2d);
	}

	public void save() {
		this.store.save(g2d);
	}

	public void drawSixStart(Color color, int x, int y, int r) {
		JavaSEGraphicsUtils.drawSixStart(g2d, color, x, y, r);
	}

	public void rectFill(int x, int y, int width, int height, Color color) {
		JavaSEGraphicsUtils.rectFill(g2d, x, y, width, height, color);
	}

	public void rectDraw(int x, int y, int width, int height, Color color) {
		JavaSEGraphicsUtils.rectDraw(g2d, x, y, width, height, color);
	}

	public void rectOval(int x, int y, int width, int height, Color color) {
		JavaSEGraphicsUtils.rectOval(g2d, x, y, width, height, color);
	}

	public void drawCenterString(String s, int x, int y) {
		FontMetrics fontmetrics = g2d.getFontMetrics();
		x -= fontmetrics.stringWidth(s) >> 1;
		y += fontmetrics.getAscent() - fontmetrics.getDescent() >> 1;
		g2d.drawString(s, x, y);
	}

	public void drawShadeString(String s, int x, int y, Color color,
			Color color1, int k) {
		g2d.setColor(color);
		g2d.drawString(s, x + k, y + k);
		g2d.setColor(color1);
		g2d.drawString(s, x, y);
	}

	public void drawCenterShadeString(String s, int x, int y, Color color,
			Color color1, int k) {
		FontMetrics fontmetrics = g2d.getFontMetrics();
		x -= fontmetrics.stringWidth(s) >> 1;
		y += fontmetrics.getAscent() - fontmetrics.getDescent() >> 1;
		drawShadeString(s, x, y, color, color1, k);
	}

	public void drawCenterShadeString(String s, int x, int y, Color color,
			Color color1) {
		drawCenterShadeString(s, x, y, color, color1,
				g2d.getFont().getSize() / 14 + 2);
	}

	public void drawCenterRoundedString(String s, int x, int y, Color color,
			Color color1) {
		g2d.setColor(color);
		FontMetrics fontmetrics = g2d.getFontMetrics();
		x -= fontmetrics.stringWidth(s) >> 1;
		y += fontmetrics.getAscent() - fontmetrics.getDescent() >> 1;
		g2d.drawString(s, x + 1, y + 1);
		g2d.drawString(s, x + 1, y - 1);
		g2d.drawString(s, x - 1, y + 1);
		g2d.drawString(s, x - 1, y - 1);
		g2d.setColor(color1);
		g2d.drawString(s, x, y);
	}

	public void drawStyleString(String message, int x, int y, LColor color,
			LColor color1) {
		JavaSEGraphicsUtils.drawStyleString(g2d, message, x, y, color.getAWTColor(),
				color1.getAWTColor());
	}

	public void draw3DString(String s, int x, int y, Color c) {
		g2d.setColor(Color.black);
		for (int i = -2; i < 4; i++) {
			for (int j = -2; j < 4; j++) {
				g2d.drawString(s, x + i, y + j);
			}
		}
		g2d.setColor(c);
		g2d.drawString(s, x, y);
	}

	public void drawRGB(final int[] rgbData, int offset, int scanlength, int x,
			int y, int width, int height, boolean processAlpha) {
		if (rgbData == null) {
			return;
		}
		if (width == 0 || height == 0) {
			return;
		}
		BufferedImage buf = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		buf.setRGB(0, 0, width, height, rgbData, 0, scanlength);
		g2d.drawImage(buf, x, y, null);

	}

	public double getAlpha() {
		return JavaSEGraphicsUtils.getAlpha(g2d);
	}

	public void setAntialiasAll(boolean flag) {
		JavaSEGraphicsUtils.setAntialiasAll(g2d, flag);
	}

	public void setAntiAlias(boolean flag) {
		JavaSEGraphicsUtils.setAntialias(g2d, flag);
	}

	public void setAlphaValue(double alpha) {
		setAlpha(alpha / 255);
	}

	public void setAlpha(double alpha) {
		JavaSEGraphicsUtils.setAlpha(g2d, alpha);
	}

	public void fillTriangle(Triangle2f[] ts) {
		fillTriangle(ts, 0, 0);
	}

	public void fillTriangle(Triangle2f[] ts, int x, int y) {
		if (ts == null) {
			return;
		}
		int size = ts.length;
		for (int i = 0; i < size; i++) {
			fillTriangle(ts[i], x, y);
		}
	}

	public void fillTriangle(Triangle2f t) {
		fillTriangle(t, 0, 0);
	}

	public void fillTriangle(Triangle2f t, int x, int y) {
		if (t == null) {
			return;
		}
		int[] xpos = new int[3];
		int[] ypos = new int[3];
		xpos[0] = x + (int) t.xpoints[0];
		xpos[1] = x + (int) t.xpoints[1];
		xpos[2] = x + (int) t.xpoints[2];
		ypos[0] = y + (int) t.ypoints[0];
		ypos[1] = y + (int) t.ypoints[1];
		ypos[2] = y + (int) t.ypoints[2];
		g2d.fillPolygon(xpos, ypos, 3);
	}

	public void drawTriangle(Triangle2f[] ts) {
		drawTriangle(ts, 0, 0);
	}

	public void drawTriangle(Triangle2f[] ts, int x, int y) {
		if (ts == null) {
			return;
		}
		int size = ts.length;
		for (int i = 0; i < size; i++) {
			drawTriangle(ts[i], x, y);
		}
	}

	public void drawTriangle(Triangle2f t) {
		drawTriangle(t, 0, 0);
	}

	public void drawTriangle(Triangle2f t, int x, int y) {
		if (t == null) {
			return;
		}
		int[] xpos = new int[3];
		int[] ypos = new int[3];
		xpos[0] = x + (int) t.xpoints[0];
		xpos[1] = x + (int) t.xpoints[1];
		xpos[2] = x + (int) t.xpoints[2];
		ypos[0] = y + (int) t.ypoints[0];
		ypos[1] = y + (int) t.ypoints[1];
		ypos[2] = y + (int) t.ypoints[2];
		g2d.drawPolygon(xpos, ypos, 3);
	}

	public boolean drawImage(Image img, AffineTransform xform) {
		return g2d.drawImage(img, xform, null);
	}

	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		g2d.drawImage(img, op, x, y);
	}

	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		g2d.drawRenderedImage(img, xform);
	}

	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		g2d.drawRenderableImage(img, xform);
	}

	public void drawString(String str, int x, int y) {
		g2d.drawString(str, x, y);
	}

	public void drawSubString(String str, int offset, int len, int x, int y,
			int anchor) {
		drawString(str.substring(offset, offset + len), x, y, anchor);
	}

	public void drawString(String str, int x, int y, int anchor) {
		int newx = x;
		int newy = y;
		if (anchor == 0) {
			anchor = TOP | LEFT;
		}
		if ((anchor & TOP) != 0) {
			newy += g2d.getFontMetrics().getAscent();
		} else if ((anchor & BOTTOM) != 0) {
			newy -= g2d.getFontMetrics().getDescent();
		}
		if ((anchor & HCENTER) != 0) {
			newx -= g2d.getFontMetrics().stringWidth(str) / 2;
		} else if ((anchor & RIGHT) != 0) {
			newx -= g2d.getFontMetrics().stringWidth(str);
		}
		g2d.drawString(str, newx, newy);
	}

	public void drawString(String str, float x, float y) {
		g2d.drawString(str, x, y);
	}

	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		g2d.drawString(iterator, x, y);
	}

	public void drawString(AttributedCharacterIterator iterator, float x,
			float y) {
		g2d.drawString(iterator, x, y);
	}

	public void drawChars(char[] message, int offset, int length, int x, int y) {
		g2d.drawChars(message, offset, length, x, y);
	}

	public void drawGlyphVector(GlyphVector g, float x, float y) {
		g2d.drawGlyphVector(g, x, y);
	}

	public void fill(Path p) {
		g2d.fill(p.path2D);
	}

	public void draw(Path p) {
		g2d.draw(p.path2D);
	}

	public void setClip(Path p) {
		g2d.setClip(p.path2D);
	}

	public void drawRegion(LImage src, int x_src, int y_src, int width,
			int height, int transform, int x_dst, int y_dst, int anchor) {
		Image img = src.getBufferedImage();
		if (img != null) {
			drawRegion(img, x_src, y_src, width, height, transform, x_dst,
					y_dst, anchor);
		}
	}

	public void drawRegion(Image img, int x_src, int y_src, int width,
			int height, int transform, int x_dst, int y_dst, int anchor) {

		if (x_src + width > img.getWidth(null)
				|| y_src + height > img.getHeight(null) || width <= 0
				|| height <= 0 || x_src < 0 || y_src < 0) {
			throw new IllegalArgumentException("Image size Exception !");
		}

		AffineTransform t = new AffineTransform();

		int dW = width, dH = height;
		switch (transform) {
		case TRANS_NONE: {
			break;
		}
		case TRANS_ROT90: {
			t.translate(height, 0);
			t.rotate(Math.PI / 2);
			dW = height;
			dH = width;
			break;
		}
		case TRANS_ROT180: {
			t.translate(width, height);
			t.rotate(Math.PI);
			break;
		}
		case TRANS_ROT270: {
			t.translate(0, width);
			t.rotate(Math.PI * 3 / 2);
			dW = height;
			dH = width;
			break;
		}
		case TRANS_MIRROR: {
			t.translate(width, 0);
			t.scale(-1, 1);
			break;
		}
		case TRANS_MIRROR_ROT90: {
			t.translate(height, 0);
			t.rotate(Math.PI / 2);
			t.translate(width, 0);
			t.scale(-1, 1);
			dW = height;
			dH = width;
			break;
		}
		case TRANS_MIRROR_ROT180: {
			t.translate(width, 0);
			t.scale(-1, 1);
			t.translate(width, height);
			t.rotate(Math.PI);
			break;
		}
		case TRANS_MIRROR_ROT270: {
			t.rotate(Math.PI * 3 / 2);
			t.scale(-1, 1);
			dW = height;
			dH = width;
			break;
		}
		default:
			throw new IllegalArgumentException("Bad transform !");
		}

		boolean badAnchor = false;
		if (anchor == 0) {
			anchor = TOP | LEFT;
		}
		if ((anchor & 0x7f) != anchor || (anchor & BASELINE) != 0) {
			badAnchor = true;
		}
		if ((anchor & TOP) != 0) {
			if ((anchor & (VCENTER | BOTTOM)) != 0)
				badAnchor = true;
		} else if ((anchor & BOTTOM) != 0) {
			if ((anchor & VCENTER) != 0)
				badAnchor = true;
			else {
				y_dst -= dH - 1;
			}
		} else if ((anchor & VCENTER) != 0) {
			y_dst -= (dH - 1) >>> 1;
		} else {
			badAnchor = true;
		}
		if ((anchor & LEFT) != 0) {
			if ((anchor & (HCENTER | RIGHT)) != 0)
				badAnchor = true;
		} else if ((anchor & RIGHT) != 0) {
			if ((anchor & HCENTER) != 0)
				badAnchor = true;
			else {
				x_dst -= dW - 1;
			}
		} else if ((anchor & HCENTER) != 0) {
			x_dst -= (dW - 1) >>> 1;
		} else {
			badAnchor = true;
		}

		if (badAnchor) {
			throw new IllegalArgumentException("Bad Anchor");
		}

		AffineTransform savedT = g2d.getTransform();

		g2d.translate(x_dst, y_dst);
		g2d.transform(t);

		g2d.drawImage(img, 0, 0, width, height, x_src, y_src, x_src + width,
				y_src + height, null);

		g2d.setTransform(savedT);
	}

	public void drawRegion(LImage src, int x_src, int y_src, int width,
			int height, int transform, int x_dst, int y_dst) {
		Image img = src.getBufferedImage();
		if (img != null) {
			drawRegion(img, x_src, y_src, width, height, transform, x_dst,
					y_dst);
		}
	}

	public void drawRegion(Image img, int x_src, int y_src, int width,
			int height, int transform, int x_dst, int y_dst) {
		AffineTransform savedT = g2d.getTransform();
		g2d.translate(x_dst, y_dst);
		transform(transform, width, height);
		g2d.drawImage(img, 0, 0, width, height, x_src, y_src, x_src + width,
				y_src + height, null);
		g2d.setTransform(savedT);
	}

	public void transform(int transform, int width, int height) {
		switch (transform) {
		case TRANS_ROT90: {
			g2d.translate(height, 0);
			g2d.rotate(ANGLE_90);
			break;
		}
		case TRANS_ROT180: {
			g2d.translate(width, height);
			g2d.rotate(Math.PI);
			break;
		}
		case TRANS_ROT270: {
			g2d.translate(0, width);
			g2d.rotate(ANGLE_270);
			break;
		}
		case TRANS_MIRROR: {
			g2d.translate(width, 0);
			g2d.scale(-1, 1);
			break;
		}
		case TRANS_MIRROR_ROT90: {
			g2d.translate(height, 0);
			g2d.rotate(ANGLE_90);
			g2d.translate(width, 0);
			g2d.scale(-1, 1);
			break;
		}
		case TRANS_MIRROR_ROT180: {
			g2d.translate(width, 0);
			g2d.scale(-1, 1);
			g2d.translate(width, height);
			g2d.rotate(Math.PI);
			break;
		}
		case TRANS_MIRROR_ROT270: {
			g2d.rotate(ANGLE_270);
			g2d.scale(-1, 1);
			break;
		}
		}
	}

	public void translate(int x, int y) {
		g2d.translate(x, y);
	}

	public void translate(double tx, double ty) {
		g2d.translate(tx, ty);
	}

	public void rotate(double theta) {
		g2d.rotate(theta);
	}

	public void rotate(double theta, double x, double y) {
		g2d.rotate(theta, x, y);
	}

	public void scale(double sx, double sy) {
		g2d.scale(sx, sy);
	}

	public LColor getColor() {
		return new LColor(g2d.getColor());
	}

	public void setColor(LColor c) {
		g2d.setColor(c.getAWTColor());
	}

	public void setColor(int r, int g, int b) {
		g2d.setColor(new Color(r, g, b));
	}

	public void setColor(int r, int g, int b, int a) {
		g2d.setColor(new Color(r, g, b, a));
	}

	public void setColor(int pixel) {
		g2d.setColor(new Color(pixel));
	}

	public void setColor(Color c) {
		g2d.setColor(c);
	}

	public void setGrayScale(int grey) {
		if (grey < 0 || grey > 255) {
			throw new IllegalArgumentException();
		}
		setColor(grey, grey, grey);
	}

	public int getGrayScale() {
		return (getRedComponent() + getGreenComponent() + getBlueComponent()) / 3;
	}

	public int getGreenComponent() {
		return (getColor().getGreen() >> 8) & 255;
	}

	public int getRedComponent() {
		return (getColor().getRed() >> 16) & 255;
	}

	public int getBlueComponent() {
		return (getColor().getBlue() >> 16) & 255;
	}

	public void setPaintMode() {
		g2d.setPaintMode();
	}

	public void setXORMode(Color c1) {
		g2d.setXORMode(c1);
	}

	public Font getFont() {
		return g2d.getFont();
	}

	public LFont getLFont() {
		return new LFont(g2d.getFont());
	}

	public void setFont(Font font) {
		g2d.setFont(font);
	}

	public void setFont(LFont font) {
		if (font != null) {
			g2d.setFont(font.getFont());
		}
	}

	public void setFont(int size) {
		g2d.setFont(JavaSEGraphicsUtils.getFont(size));
	}

	public FontMetrics getFontMetrics(Font f) {
		return g2d.getFontMetrics(f);
	}

	public FontMetrics getFontMetrics() {
		return g2d.getFontMetrics();
	}

	public Rectangle getClipBounds() {
		return g2d.getClipBounds();
	}

	public int getClipX() {
		return g2d.getClipBounds().x;
	}

	public int getClipY() {
		return g2d.getClipBounds().y;
	}

	public int getClipWidth() {
		return g2d.getClipBounds().width;
	}

	public int getClipHeight() {
		return g2d.getClipBounds().height;
	}

	public void clipRect(int x, int y, int width, int height) {
		g2d.clipRect(x, y, width, height);
	}

	public void setClip(int x, int y, int width, int height) {
		g2d.setClip(x, y, width, height);
	}

	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		g2d.copyArea(x, y, width, height, dx, dy);
	}

	public void copyArea(int x_src, int y_src, int width, int height,
			int x_dest, int y_dest, int anchor) {
		if (width <= 0 || height <= 0) {
			return;
		}
		boolean badAnchor = false;
		if ((anchor & 0x7f) != anchor || (anchor & BASELINE) != 0) {
			badAnchor = true;
		}
		if ((anchor & TOP) != 0) {
			if ((anchor & (VCENTER | BOTTOM)) != 0)
				badAnchor = true;
		} else if ((anchor & BOTTOM) != 0) {
			if ((anchor & VCENTER) != 0)
				badAnchor = true;
			else {
				y_dest -= height - 1;
			}
		} else if ((anchor & VCENTER) != 0) {
			y_dest -= (height - 1) >>> 1;
		} else {
			badAnchor = true;
		}
		if ((anchor & LEFT) != 0) {
			if ((anchor & (HCENTER | RIGHT)) != 0)
				badAnchor = true;
		} else if ((anchor & RIGHT) != 0) {
			if ((anchor & HCENTER) != 0)
				badAnchor = true;
			else {
				x_dest -= width;
			}
		} else if ((anchor & HCENTER) != 0) {
			x_dest -= (width - 1) >>> 1;
		} else {
			badAnchor = true;
		}
		if (badAnchor) {
			throw new IllegalArgumentException("Bad Anchor");
		}
		g2d.copyArea(x_src, y_src, width, height, x_dest - x_src, y_dest
				- y_src);
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		g2d.drawLine(x1, y1, x2, y2);
	}

	public void fillRect(int x, int y, int width, int height) {
		g2d.fillRect(x, y, width, height);
	}

	public void fill() {
		drawClear(g2d.getColor());
	}

	public void drawClear(Color color) {
		Color oldColor = g2d.getColor();
		g2d.setColor(color);
		g2d.clearRect(0, 0, width, height);
		g2d.setColor(oldColor);
	}

	public void drawClear() {
		drawClear(Color.black);
	}

	public void clearRect(int x, int y, int width, int height) {
		g2d.clearRect(x, y, width, height);
	}

	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		g2d.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		g2d.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	public void drawOval(int x, int y, int width, int height) {
		g2d.drawOval(x, y, width, height);
	}

	public void fillOval(int x, int y, int width, int height) {
		g2d.fillOval(x, y, width, height);
	}

	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		g2d.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		g2d.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		g2d.drawPolyline(xPoints, yPoints, nPoints);
	}

	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		g2d.drawPolygon(xPoints, yPoints, nPoints);
	}

	public void drawPolygon(Polygon p) {
		g2d.drawPolygon(p);
	}

	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		g2d.fillPolygon(xPoints, yPoints, nPoints);
	}

	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		g2d.fill3DRect(x, y, width, height, raised);
	}

	public void fillPolygon(Polygon p) {
		g2d.fillPolygon(p);
	}

	public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
		int[] x = { x1, x2, x3 };
		int[] y = { y1, y2, y3 };
		g2d.fillPolygon(x, y, 3);
	}

	public boolean drawImage(LImage img, AffineTransform aTransform) {
		if (img != null) {
			return g2d.drawImage(img.getBufferedImage(), aTransform, null);
		}
		return false;
	}

	public boolean drawImage(LImage img, int x, int y) {
		if (img != null) {
			return g2d.drawImage(img.getBufferedImage(), x, y, null);
		}
		return false;
	}

	public boolean drawImage(LImage img, int x, int y, int width, int height) {
		if (img != null) {
			return g2d.drawImage(img.getBufferedImage(), x, y, width, height,
					null);
		}
		return false;
	}

	public void drawImage(LImage img, int x, int y, int anchor) {
		if (img != null) {
			int newx = x;
			int newy = y;
			if (anchor == 0) {
				anchor = TOP | LEFT;
			}
			if ((anchor & RIGHT) != 0) {
				newx -= img.getWidth();
			} else if ((anchor & HCENTER) != 0) {
				newx -= img.getWidth() / 2;
			}
			if ((anchor & BOTTOM) != 0) {
				newy -= img.getHeight();
			} else if ((anchor & VCENTER) != 0) {
				newy -= img.getHeight() / 2;
			}
			g2d.drawImage(img.getBufferedImage(), newx, newy, null);
		}
	}

	public boolean drawImage(LImage img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2) {
		if (img != null) {
			return g2d.drawImage(img.getBufferedImage(), dx1, dy1, dx2, dy2,
					sx1, sy1, sx2, sy2, null);
		}
		return false;
	}

	public void drawImage(Image img, int x, int y, int anchor) {
		int newx = x;
		int newy = y;
		if (anchor == 0) {
			anchor = TOP | LEFT;
		}
		if ((anchor & RIGHT) != 0) {
			newx -= img.getWidth(null);
		} else if ((anchor & HCENTER) != 0) {
			newx -= img.getWidth(null) / 2;
		}
		if ((anchor & BOTTOM) != 0) {
			newy -= img.getHeight(null);
		} else if ((anchor & VCENTER) != 0) {
			newy -= img.getHeight(null) / 2;
		}
		g2d.drawImage(img, newx, newy, null);
	}

	public boolean drawImage(Image img, int x, int y) {
		return g2d.drawImage(img, x, y, null);
	}

	public boolean drawImage(Image img, int x, int y, int width, int height) {
		return g2d.drawImage(img, x, y, width, height, null);
	}

	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2) {
		return g2d.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
	}

	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, Color bgcolor) {
		return g2d.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
				bgcolor, null);
	}

	public int getStrokeStyle() {
		return strokeStyle;
	}

	public void setStrokeStyle(int style) {
		if (style != SOLID && style != DOTTED) {
			throw new IllegalArgumentException("Invalid line style !");
		}
		this.strokeStyle = style;
	}

	public void drawChars(char[] chars, int ofs, int len, int x, int y,
			int align) {
		drawString(new String(chars, ofs, len), x, y, align);
	}

	public void drawRect(int x, int y, int width, int height) {
		g2d.drawRect(x, y, width, height);
	}

	public void dispose() {
		g2d.dispose();
		isClose = true;
	}

	public boolean isClose() {
		return isClose;
	}

}
