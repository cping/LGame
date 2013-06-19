package loon.core.graphics.device;

import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.geom.Triangle2f;
import loon.core.graphics.GraphicsUtils;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.LImage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

import android.graphics.Paint.FontMetrics;
import android.graphics.PorterDuff.Mode;

/**
 * 
 * Copyright 2008 - 2011
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
 * @email javachenpeng@yahoo.com
 * @version 0.1.2
 */
public final class LGraphics implements LTrans {

	private final static android.graphics.DashPathEffect dashPathEffect = new android.graphics.DashPathEffect(
			new float[] { 5, 5 }, 0);

	private final static LFont defaultFont = LFont.getDefaultFont();

	private final Matrix tmp_matrix;

	private int strokeStyle;

	private Path path;

	private Bitmap grapBitmap;

	private Canvas canvas;

	private Paint paint;

	private RectF rectF;

	private Rect srcR, dstR, clip;

	private LFont font;

	private boolean isClose;

	private int width, height;

	public LGraphics(Bitmap bit) {
		this.grapBitmap = bit;
		this.tmp_matrix = new Matrix();
		this.initGraphics();
	}

	/**
	 * 初始化画布
	 */
	public void initGraphics() {
		if (paint == null) {
			paint = new Paint();
		}
		if (path == null) {
			path = new Path();
		}
		if (rectF == null) {
			rectF = new RectF();
		}
		if (srcR == null) {
			srcR = new Rect();
		}
		if (dstR == null) {
			dstR = new Rect();
		}
		if (canvas == null) {
			this.canvas = new Canvas(grapBitmap);
		} else {
			this.canvas.setBitmap(grapBitmap);
		}
		this.width = grapBitmap.getWidth();
		this.height = grapBitmap.getHeight();
		this.canvas.clipRect(0, 0, width, height);
		this.clip = canvas.getClipBounds();
		this.setFont(defaultFont);
		this.canvas.save(Canvas.CLIP_SAVE_FLAG);
		this.setColor(LColor.white);
		this.isClose = false;
	}

	/**
	 * 刷新Font数据
	 */
	public void resetFont() {
		this.paint.setColor(0xFFFFFFFF);
		this.setFont(defaultFont);
	}

	/**
	 * 刷新画布
	 */
	public void reset() {
		if (canvas != null && !isClose) {
			if (paint == null) {
				paint = new Paint();
			}
			if (path == null) {
				path = new Path();
			}
			if (rectF == null) {
				rectF = new RectF();
			}
			if (srcR == null) {
				srcR = new Rect();
			}
			if (dstR == null) {
				dstR = new Rect();
			}
			if (grapBitmap != null) {
				this.width = grapBitmap.getWidth();
				this.height = grapBitmap.getHeight();
				this.canvas.clipRect(0, 0, width, height);
			}
			this.clip = canvas.getClipBounds();
			this.setFont(defaultFont);
			this.canvas.save(Canvas.CLIP_SAVE_FLAG);
		}
	}

	/**
	 * 执行Canvas的restore
	 */
	public void restore() {
		if (isClose) {
			return;
		}
		canvas.restore();
	}

	/**
	 * 执行Canvas的save
	 * 
	 * @param flag
	 */
	public void save(int flag) {
		if (isClose) {
			return;
		}
		canvas.save(flag);
	}

	/**
	 * 执行Canvas的save
	 * 
	 */
	public void save() {
		if (isClose) {
			return;
		}
		canvas.save();
	}


	/**
	 * 创建一个新的 LGraphics对象，它是当前LGraphics对象的克隆
	 */
	public LGraphics create() {
		if (isClose) {
			return null;
		}
		return new LGraphics(Bitmap.createBitmap(grapBitmap));
	}

	/**
	 * 基于此 LGraphics创建一个新的 LGraphics对象，但是使用新的转换和剪贴区域
	 * 
	 */
	public LGraphics create(int x, int y, int w, int h) {
		if (isClose) {
			return null;
		}
		return new LGraphics(Bitmap.createBitmap(grapBitmap, x, y, w, h));
	}

	public boolean isAntiAlias() {
		if (isClose) {
			return false;
		}
		return paint.isAntiAlias();
	}

	public void setAntiAlias(boolean flag) {
		if (isClose) {
			return;
		}
		paint.setAntiAlias(flag);
	}

	public void setAlphaValue(int alpha) {
		if (isClose) {
			return;
		}
		paint.setAlpha(alpha);
	}

	public void setAlpha(float alpha) {
		setAlphaValue((int) (255 * alpha));
	}

	public float getAlpha() {
		if (isClose) {
			return 0F;
		}
		return paint.getAlpha() / 255;
	}

	public float getAlphaValue() {
		if (isClose) {
			return 0F;
		}
		return paint.getAlpha();
	}

	public void setColor(int r, int g, int b) {
		if (isClose) {
			return;
		}
		paint.setColor(LColor.getRGB(r, g, b));
	}

	public void setColorValue(int pixels) {
		if (isClose) {
			return;
		}
		paint.setColor(pixels);
	}

	public void setColor(int pixels) {
		if (isClose) {
			return;
		}
		paint.setColor(LColor.getRGB(pixels));
	}

	public void setColor(int r, int g, int b, int a) {
		if (isClose) {
			return;
		}
		paint.setColor(LColor.getARGB(r, g, b, a));
	}

	public void setColor(LColor c) {
		if (isClose) {
			return;
		}
		paint.setColor(c.getRGB());
	}

	public void setColorARGB(LColor c) {
		if (isClose) {
			return;
		}
		paint.setColor(c.getARGB());
	}

	public void setColorAll(LColor c) {
		if (isClose) {
			return;
		}
		paint.setColor(c.getRGB());
		canvas.drawColor(c.getRGB());
	}

	public LColor getColor() {
		if (isClose) {
			return LColor.black;
		}
		return new LColor(paint.getColor());
	}

	public final int getColorRGB() {
		if (isClose) {
			return 0;
		}
		return paint.getColor() & 0x00FFFFFF;
	}

	public final int getColorARGB() {
		if (isClose) {
			return 0;
		}
		return paint.getColor();
	}

	public final void setColorRGB24(int rgb) {
		if (isClose) {
			return;
		}
		paint.setColor(0xFF000000 | (rgb & 0x00FFFFFF));
	}

	public final void setColorARGB32(int argb) {
		if (isClose) {
			return;
		}
		paint.setColor(argb);
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

	public FontMetrics getFontMetrics() {
		if (font != null) {
			return font.getFontMetrics();
		}
		return null;
	}

	public LFont getFont() {
		if (isClose) {
			return null;
		}
		return font;
	}

	public LFont getLFont() {
		return getFont();
	}

	public void setFont(int size) {
		setFont(LFont.getFont(LSystem.FONT_NAME, 0, size));
	}

	public void setFont(String familyName, int style, int size) {
		setFont(LFont.getFont(familyName, style, size));
	}

	public void setFont(LFont font) {
		if (isClose) {
			return;
		}
		Paint typefacePaint = font.getTypefacePaint();
		if (this.paint != null) {
			this.paint.setTextSize(font.getSize());
			this.paint.setTypeface(typefacePaint.getTypeface());
			this.paint.setUnderlineText(typefacePaint.isUnderlineText());
		} else {
			this.paint = new Paint(typefacePaint);
		}
		this.font = font;
	}

	/**
	 * 重新分配画布图像为缓存图
	 */
	public void allocate() {
		if (isClose) {
			return;
		}
		canvas.setBitmap(grapBitmap);
	}

	/**
	 * 翻转画布为指定角度
	 */
	public void setTransform(int transform, int width, int height) {
		switch (transform) {
		case TRANS_ROT90: {
			translate(height, 0);
			rotate(ANGLE_90);
			break;
		}
		case TRANS_ROT180: {
			translate(width, height);
			rotate((float) Math.PI);
			break;
		}
		case TRANS_ROT270: {
			translate(0, width);
			rotate(ANGLE_270);
			break;
		}
		case TRANS_MIRROR: {
			translate(width, 0);
			scale(-1, 1);
			break;
		}
		case TRANS_MIRROR_ROT90: {
			translate(height, 0);
			rotate(ANGLE_90);
			translate(width, 0);
			scale(-1, 1);
			break;
		}
		case TRANS_MIRROR_ROT180: {
			translate(width, 0);
			scale(-1, 1);
			translate(width, height);
			rotate((float) Math.PI);
			break;
		}
		case TRANS_MIRROR_ROT270: {
			rotate(ANGLE_270);
			scale(-1, 1);
			break;
		}
		}
	}

	public boolean hitClip(int x, int y, int width, int height) {
		return getClipBounds().intersects(new RectBox(x, y, width, height));
	}

	public RectBox getClipBounds() {
		if (isClose) {
			return new RectBox(0, 0, 1, 1);
		}
		Rect r = canvas.getClipBounds();
		return new RectBox(r.left, r.top, r.width(), r.height());
	}

	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		if (isClose) {
			return;
		}
		rectF.set(x, y, x + width, y + height);
		canvas.drawRoundRect(rectF, arcWidth, arcHeight, paint);
	}

	public void setPaintMode() {
		if (isClose) {
			return;
		}
		paint.setXfermode(null);
	}

	public void setFill() {
		if (isClose) {
			return;
		}
		paint.setStyle(Paint.Style.FILL);
	}

	public void setStyle(Paint.Style style) {
		if (isClose) {
			return;
		}
		paint.setStyle(style);
	}

	public void setStrokeWidth(float w) {
		if (isClose) {
			return;
		}
		paint.setStrokeWidth(w);
	}

	public void rotate(float theta) {
		if (isClose) {
			return;
		}
		canvas.rotate(theta);
	}

	public void rotate(float theta, float x, float y) {
		if (isClose) {
			return;
		}
		canvas.rotate(theta, x, y);
	}

	public void scale(float s) {
		if (isClose) {
			return;
		}
		canvas.scale(s, s);
	}

	public void scale(float sx, float sy) {
		if (isClose) {
			return;
		}
		canvas.scale(sx, sy);
	}

	public void rectFill(int x, int y, int width, int height, LColor color) {
		if (isClose) {
			return;
		}
		setColor(color);
		fillRect(x, y, width, height);
	}

	public void rectDraw(int x, int y, int width, int height, LColor color) {
		if (isClose) {
			return;
		}
		setColor(color);
		drawRect(x, y, width, height);
	}

	public void rectOval(int x, int y, int width, int height, LColor color) {
		if (isClose) {
			return;
		}
		setColor(color);
		drawOval(x, y, width, height);
		fillOval(x, y, width, height);
	}

	public void setFilterBitmap(boolean filter) {
		if (isClose) {
			return;
		}
		paint.setFilterBitmap(filter);
	}

	public void drawBitmap(Bitmap bit, int x, int y) {
		if (isClose) {
			return;
		}
		if (bit == null) {
			return;
		}
		canvas.drawBitmap(bit, x, y, paint);
	}

	public void drawImage(String fileName, int x, int y, int w, int h) {
		drawImage(GraphicsUtils.loadImage(fileName, true), x, y, w, h);
	}

	public void drawImage(String fileName, int x, int y) {
		drawImage(GraphicsUtils.loadImage(fileName, true), x, y);
	}

	public void drawImage(LImage img, int x, int y) {
		if (img != null) {
			drawBitmap(img.getBitmap(), x, y);
		}
	}

	public void drawBitmap(Bitmap bit, int x, int y, int anchor) {
		if (isClose) {
			return;
		}
		if (bit == null) {
			return;
		}
		int newx = x;
		int newy = y;
		if (anchor == 0) {
			anchor = LTrans.TOP | LTrans.LEFT;
		}
		if ((anchor & LTrans.RIGHT) != 0) {
			newx -= bit.getWidth();
		} else if ((anchor & LTrans.HCENTER) != 0) {
			newx -= bit.getWidth() / 2;
		}
		if ((anchor & LTrans.BOTTOM) != 0) {
			newy -= bit.getHeight();
		} else if ((anchor & LTrans.VCENTER) != 0) {
			newy -= bit.getHeight() / 2;
		}
		canvas.drawBitmap(bit, newx, newy, paint);
	}

	public void drawImage(LImage img, int x, int y, int anchor) {
		if (img != null) {
			drawBitmap(img.getBitmap(), x, y, anchor);
		}
	}

	public void drawRegion(Bitmap bit, int x_src, int y_src, int width,
			int height, int transform, int x_dst, int y_dst, int anchor) {
		if (isClose) {
			return;
		}
		if (bit == null) {
			return;
		}
		if (x_src + width > bit.getWidth() || y_src + height > bit.getHeight()
				|| width < 0 || height < 0 || x_src < 0 || y_src < 0) {
			throw new IllegalArgumentException("Area out of Image");
		}
		int dW = width, dH = height;

		Bitmap newBitmap = null;

		switch (transform) {
		case TRANS_NONE: {
			newBitmap = bit;
			break;
		}
		case TRANS_ROT90: {
			tmp_matrix.reset();
			tmp_matrix.preRotate(90);
			newBitmap = Bitmap.createBitmap(bit, x_src, y_src, width, height,
					tmp_matrix, true);
			dW = height;
			dH = width;
			break;
		}
		case TRANS_ROT180: {
			tmp_matrix.reset();
			tmp_matrix.preRotate(180);
			newBitmap = Bitmap.createBitmap(bit, x_src, y_src, width, height,
					tmp_matrix, true);
			break;
		}
		case TRANS_ROT270: {
			tmp_matrix.reset();
			tmp_matrix.preRotate(270);
			newBitmap = Bitmap.createBitmap(bit, x_src, y_src, width, height,
					tmp_matrix, true);
			dW = height;
			dH = width;
			break;
		}
		case TRANS_MIRROR: {
			tmp_matrix.reset();
			tmp_matrix.preScale(-1, 1);
			newBitmap = Bitmap.createBitmap(bit, x_src, y_src, width, height,
					tmp_matrix, true);
			break;
		}
		case TRANS_MIRROR_ROT90: {
			tmp_matrix.reset();
			tmp_matrix.preScale(-1, 1);
			tmp_matrix.preRotate(-90);
			newBitmap = Bitmap.createBitmap(bit, x_src, y_src, width, height,
					tmp_matrix, true);
			dW = height;
			dH = width;
			break;
		}
		case TRANS_MIRROR_ROT180: {
			tmp_matrix.reset();
			tmp_matrix.preScale(-1, 1);
			tmp_matrix.preRotate(-180);
			newBitmap = Bitmap.createBitmap(bit, x_src, y_src, width, height,
					tmp_matrix, true);
			break;
		}
		case TRANS_MIRROR_ROT270: {
			tmp_matrix.reset();
			tmp_matrix.preScale(-1, 1);
			tmp_matrix.preRotate(-270);
			newBitmap = Bitmap.createBitmap(bit, x_src, y_src, width, height,
					tmp_matrix, true);
			dW = height;
			dH = width;
			break;
		}
		default:
			newBitmap = null;
			throw new IllegalArgumentException("Bad transform");
		}

		boolean badAnchor = false;

		if (anchor == 0) {
			anchor = TOP | LEFT;
		}

		if ((anchor & 0x7f) != anchor || (anchor & BASELINE) != 0) {
			badAnchor = true;
		}

		if ((anchor & TOP) != 0) {
			if ((anchor & (VCENTER | BOTTOM)) != 0) {
				badAnchor = true;
			}
		} else if ((anchor & BOTTOM) != 0) {
			if ((anchor & VCENTER) != 0) {
				badAnchor = true;
			} else {
				y_dst -= dH - 1;
			}
		} else if ((anchor & VCENTER) != 0) {
			y_dst -= (dH - 1) >>> 1;
		} else {
			badAnchor = true;
		}

		if ((anchor & LEFT) != 0) {
			if ((anchor & (HCENTER | RIGHT)) != 0) {
				badAnchor = true;
			}
		} else if ((anchor & RIGHT) != 0) {
			if ((anchor & HCENTER) != 0) {
				badAnchor = true;
			} else {
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
		srcR.set(x_src, y_src, x_src + dW, y_src + dH);
		dstR.set(x_dst, y_dst, x_dst + dW, y_dst + dH);

		canvas.drawBitmap(newBitmap, srcR, dstR, paint);

		if (transform != TRANS_NONE) {
			newBitmap.recycle();
			newBitmap = null;
		}

	}

	public void drawRegion(LImage src, int x_src, int y_src, int width,
			int height, int transform, int x_dst, int y_dst, int anchor) {
		if (src == null) {
			return;
		}
		drawRegion(src.getBitmap(), x_src, y_src, width, height, transform,
				x_dst, y_dst, anchor);
	}

	public void drawBitmap(Bitmap bit, int x, int y, int w, int h) {
		if (isClose) {
			return;
		}
		if (bit == null) {
			return;
		}
		int width = bit.getWidth();
		int height = bit.getHeight();
		if (width == w && height == h) {
			drawBitmap(bit, x, y);
			return;
		}
		float scaleWidth = ((float) w) / width;
		float scaleHeight = ((float) h) / height;
		tmp_matrix.reset();
		tmp_matrix.postScale(scaleWidth, scaleHeight);
		tmp_matrix.postTranslate(x, y);
		drawBitmap(bit, tmp_matrix, false);
	}

	public void drawImage(LImage img, Matrix marMatrix, boolean filter) {
		if (isClose) {
			return;
		}
		if (img == null) {
			return;
		}
		paint.setFilterBitmap(filter);
		canvas.drawBitmap(img.getBitmap(), marMatrix, paint);
		paint.setFilterBitmap(false);

	}

	public void drawImage(LImage img, Matrix marMatrix) {
		if (isClose) {
			return;
		}
		if (img == null) {
			return;
		}
		canvas.drawBitmap(img.getBitmap(), marMatrix, paint);

	}

	public void drawBitmap(Bitmap bit, Matrix marMatrix, boolean filter) {
		if (isClose) {
			return;
		}
		if (bit == null) {
			return;
		}
		paint.setFilterBitmap(filter);
		canvas.drawBitmap(bit, marMatrix, paint);
		paint.setFilterBitmap(false);
	}

	public void drawBitmap(Bitmap bit, Matrix marMatrix) {
		if (isClose) {
			return;
		}
		if (bit == null) {
			return;
		}
		canvas.drawBitmap(bit, marMatrix, paint);
	}

	public void drawImage(LImage img, int x, int y, int w, int h) {
		if (isClose) {
			return;
		}
		if (img == null) {
			return;
		}
		int width = img.getWidth();
		int height = img.getHeight();
		if (width == w && height == h) {
			drawImage(img, x, y);
			return;
		}
		float scaleWidth = ((float) w) / width;
		float scaleHeight = ((float) h) / height;
		tmp_matrix.reset();
		tmp_matrix.postScale(scaleWidth, scaleHeight);
		tmp_matrix.postTranslate(x, y);
		drawImage(img, tmp_matrix, false);
	}

	public void drawBitmap(int[] colors, int x, int y, int width, int height,
			boolean hasAlpha) {
		if (isClose) {
			return;
		}
		canvas.drawBitmap(colors, 0, width, x, y, width, height, hasAlpha,
				paint);
	}

	public void drawBitmap(Bitmap bit, int x, int y, int w, int h, int x1,
			int y1, int w1, int h1) {
		if (isClose) {
			return;
		}
		if (bit == null) {
			return;
		}
		srcR.set(x1, y1, w1, h1);
		dstR.set(x, y, x + w, y + h);
		canvas.drawBitmap(bit, srcR, dstR, paint);
	}

	public void drawBitmap(Bitmap bit, Rect r1, Rect r2) {
		if (isClose) {
			return;
		}
		if (bit == null) {
			return;
		}
		canvas.drawBitmap(bit, r1, r2, null);
	}

	public void drawImage(LImage img, int x, int y, int w, int h, int x1,
			int y1, int w1, int h1) {
		if (isClose) {
			return;
		}
		if (img != null) {
			srcR.set(x1, y1, w1, h1);
			dstR.set(x, y, x + w, y + h);
			canvas.drawBitmap(img.getBitmap(), srcR, dstR, paint);
		}
	}

	public void drawArc(int x, int y, int width, int height, int sa, int ea) {
		if (isClose) {
			return;
		}
		paint.setStrokeWidth(0);
		rectF.set(x, y, x + width, y + height);
		canvas.drawArc(rectF, 360 - (ea + sa), ea, true, paint);
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		if (isClose) {
			return;
		}
		if (x1 == x2) {
			x2++;
		}
		if (y1 == y2) {
			y2++;
		}
		canvas.drawLine(x1, y1, x2, y2, paint);
	}

	public void drawRect(int x, int y, int width, int height) {
		if (isClose) {
			return;
		}
		Paint.Style tmp = paint.getStyle();
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(x, y, x + width, y + height, paint);
		paint.setStyle(tmp);
	}

	public void drawBytes(byte[] message, int offset, int length, int x, int y) {
		if (isClose) {
			return;
		}
		drawString(new String(message, offset, length), x, y);
	}

	public void drawChars(char[] message, int offset, int length, int x, int y) {
		if (isClose) {
			return;
		}
		int flag = paint.getFlags();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		canvas.drawText(message, offset, length, x, y, paint);
		paint.setFlags(flag);
	}

	public void drawString(String message, int x, int y, int anchor) {
		if (isClose) {
			return;
		}
		int newx = x;
		int newy = y;
		if (anchor == 0) {
			anchor = LTrans.TOP | LTrans.LEFT;
		}
		if ((anchor & LTrans.TOP) != 0) {
			newy -= font.getFontMetrics().ascent;
		} else if ((anchor & LTrans.BOTTOM) != 0) {
			newy -= font.getFontMetrics().descent;
		}
		if ((anchor & LTrans.HCENTER) != 0) {
			newx -= font.getTypefacePaint().measureText(message) / 2;
		} else if ((anchor & LTrans.RIGHT) != 0) {
			newx -= font.getTypefacePaint().measureText(message);
		}
		int flag = paint.getFlags();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		canvas.drawText(message, newx, newy, paint);
		paint.setFlags(flag);
	}

	public void drawSubstring(String message, int offset, int len, int x,
			int y, int anchor) {
		if (isClose) {
			return;
		}
		int newx = x;
		int newy = y;
		if (anchor == 0) {
			anchor = LTrans.TOP | LTrans.LEFT;
		}
		if ((anchor & LTrans.TOP) != 0) {
			newy -= font.getFontMetrics().ascent;
		} else if ((anchor & LTrans.BOTTOM) != 0) {
			newy -= font.getFontMetrics().descent;
		}
		if ((anchor & LTrans.HCENTER) != 0) {
			newx -= font.getTypefacePaint().measureText(message) / 2;
		} else if ((anchor & LTrans.RIGHT) != 0) {
			newx -= font.getTypefacePaint().measureText(message);
		}
		int flag = paint.getFlags();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		canvas.drawText(message, offset, len + offset, newx, newy, paint);
		paint.setFlags(flag);
	}

	public void drawSubString(String message, int x, int y, int w, int h,
			int anchor) {
		if (isClose) {
			return;
		}
		int flag = paint.getFlags();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		canvas.drawText(message, x, x + y, w, h, paint);
		paint.setFlags(flag);
	}

	public void drawString(String message, float x, float y) {
		if (isClose) {
			return;
		}
		int flag = paint.getFlags();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		canvas.drawText(message, x, y, paint);
		paint.setFlags(flag);
	}

	public void drawChar(char message, int x, int y) {
		if (isClose) {
			return;
		}
		int flag = paint.getFlags();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		canvas.drawText(Character.toString(message), x, y, paint);
		paint.setFlags(flag);
	}

	public void drawString(String message, int x, int y) {
		if (isClose) {
			return;
		}
		int flag = paint.getFlags();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		canvas.drawText(message, x, y, paint);
		paint.setFlags(flag);
	}

	public void draw3DString(String message, int x, int y, LColor c) {
		if (isClose) {
			return;
		}
		int flag = paint.getFlags();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(LColor.black.getRGB());
		for (int i = -2; i < 4; i++) {
			for (int j = -2; j < 4; j++) {
				canvas.drawText(message, x + i, y + j, paint);
			}
		}
		paint.setColor(c.getRGB());
		canvas.drawText(message, x, y, paint);
		paint.setFlags(flag);
	}

	public void drawCenterString(String message, int x, int y) {
		if (isClose) {
			return;
		}
		x -= font.stringWidth(message) >> 1;
		y += font.getHeight() / 3;
		int flag = paint.getFlags();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		canvas.drawText(message, x, y, paint);
		paint.setFlags(flag);
	}

	public void drawShadeString(String message, int x, int y, LColor color,
			LColor color1, int k) {
		if (isClose) {
			return;
		}
		int flag = paint.getFlags();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color.getRGB());
		canvas.drawText(message, x + k, y + k, paint);
		paint.setColor(color1.getRGB());
		canvas.drawText(message, x, y, paint);
		paint.setFlags(flag);
	}

	public void drawCenterShadeString(String message, int x, int y,
			LColor color, LColor color1, int k) {
		if (isClose) {
			return;
		}
		x -= font.stringWidth(message) >> 1;
		y += font.getHeight() / 3;
		drawShadeString(message, x, y, color, color1, k);
	}

	public void drawCenterShadeString(String message, int x, int y,
			LColor color, LColor color1) {
		drawCenterShadeString(message, x, y, color, color1,
				getFont().getSize() / 14 + 2);
	}

	public void drawCenterRoundedString(String message, int x, int y,
			LColor color, LColor color1) {
		if (isClose) {
			return;
		}
		int flag = paint.getFlags();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color.getRGB());
		x -= font.stringWidth(message) >> 1;
		y += font.getHeight() / 3;
		canvas.drawText(message, x + 1, y + 1, paint);
		canvas.drawText(message, x + 1, y - 1, paint);
		canvas.drawText(message, x - 1, y + 1, paint);
		canvas.drawText(message, x - 1, y - 1, paint);
		paint.setColor(color1.getRGB());
		canvas.drawText(message, x, y, paint);
		paint.setFlags(flag);
	}

	public void drawStyleString(String message, int x, int y, int color,
			int color1) {
		if (isClose) {
			return;
		}
		int flag = paint.getFlags();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color);
		canvas.drawText(message, x + 1, y, paint);
		canvas.drawText(message, x - 1, y, paint);
		canvas.drawText(message, x, y + 1, paint);
		canvas.drawText(message, x, y - 1, paint);
		paint.setColor(color1);
		canvas.drawText(message, x, y, paint);
		paint.setFlags(flag);
	}

	public void drawStyleString(String message, int x, int y, LColor c1,
			LColor c2) {
		if (isClose) {
			return;
		}
		int flag = paint.getFlags();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(c1.getRGB());
		canvas.drawText(message, x + 1, y, paint);
		canvas.drawText(message, x - 1, y, paint);
		canvas.drawText(message, x, y + 1, paint);
		canvas.drawText(message, x, y - 1, paint);
		paint.setColor(c2.getRGB());
		canvas.drawText(message, x, y, paint);
		paint.setFlags(flag);
	}

	public void drawRGB(int[] colors, int offset, int stride, int x, int y,
			int width, int height, boolean hasAlpha) {
		if (isClose) {
			return;
		}
		canvas.drawBitmap(colors, offset, stride, x, y, width, height,
				hasAlpha, paint);
	}

	/**
	 * 以当前画布颜色填充全屏
	 * 
	 */
	public void fill() {
		drawClear(paint.getColor());
	}

	/**
	 * 以指定颜色清空屏幕
	 * 
	 * @param c
	 */
	public void drawClear(int c) {
		if (isClose) {
			return;
		}
		canvas.drawColor(c);
	}

	/**
	 * 以指定颜色清空屏幕
	 * 
	 * @param c
	 */
	public void drawClear(LColor c) {
		if (isClose) {
			return;
		}
		canvas.drawColor(c.getARGB());
	}

	/**
	 * 清空屏幕
	 */
	public void drawClear() {
		if (isClose) {
			return;
		}
		canvas.drawColor(0, Mode.CLEAR);
	}

	/**
	 * 设置 LGraphics的背景色
	 */
	public void setBackground(LColor color) {
		if (isClose) {
			return;
		}
		paint.setColor(color.getRGB());
		canvas.drawColor(color.getRGB());
	}

	/**
	 * 获得LGraphics的背景色
	 */
	public LColor getBackground() {
		return getColor();
	}

	public void fillArc(int x, int y, int width, int height, int sa, int ea) {
		if (isClose) {
			return;
		}
		Paint.Style tmp = paint.getStyle();
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		rectF.set(x, y, x + width, y + height);
		canvas.drawArc(rectF, 360 - (sa + ea), ea, true, paint);
		paint.setStyle(tmp);
	}

	public void fillOval(int x, int y, int width, int height) {
		if (isClose) {
			return;
		}
		rectF.set(x, y, x + width, y + height);
		canvas.drawOval(rectF, paint);
	}

	public void fillRect(int x, int y, int width, int height) {
		if (isClose) {
			return;
		}
		canvas.drawRect(x, y, x + width, y + height, paint);
	}

	public void fillAlphaRect(int x, int y, int w, int h, LColor c) {
		fillAlphaRect(x, y, w, h, c.getRGB());
	}

	public void fillAlphaRect(int x, int y, int w, int h, int pixel) {
		if (isClose) {
			return;
		}
		int color = paint.getColor();
		paint.setColor(pixel);
		float f = x;
		float f1 = y;
		float f2 = x + w;
		float f3 = y + h;
		canvas.drawRect(f, f1, f2, f3, paint);
		paint.setColor(color);
	}

	public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
		if (isClose) {
			return;
		}
		Paint.Style tmp = paint.getStyle();
		paint.setStyle(Paint.Style.FILL);
		path.reset();
		path.moveTo(x1, y1);
		path.lineTo(x2, y2);
		path.lineTo(x3, y3);
		path.lineTo(x1, y1);
		canvas.drawPath(path, paint);
		paint.setStyle(tmp);
	}

	public void fillTriangle(Triangle2f t) {
		fillTriangle(t, 0, 0);
	}

	public void fillTriangle(Triangle2f t, int x, int y) {
		if (isClose) {
			return;
		}
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
		fillTriangle(xpos[0], ypos[0], xpos[1], ypos[1], xpos[2], ypos[2]);
	}

	public void drawTriangle(Triangle2f[] ts) {
		drawTriangle(ts, 0, 0);
	}

	public void drawTriangle(Triangle2f[] ts, int x, int y) {
		if (isClose) {
			return;
		}
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
		if (isClose) {
			return;
		}
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
		drawPolygon(xpos, ypos, 3);
	}

	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		if (isClose) {
			return;
		}
		LColor color = getColor();
		LColor colorUp, colorDown;
		if (raised) {
			colorUp = color.brighter();
			colorDown = color.darker();
			setColor(color);
		} else {
			colorUp = color.darker();
			colorDown = color.brighter();
			setColor(colorUp);
		}

		width--;
		height--;
		fillRect(x + 1, y + 1, width - 1, height - 1);

		setColor(colorUp);
		fillRect(x, y, width, 1);
		fillRect(x, y + 1, 1, height);

		setColor(colorDown);
		fillRect(x + width, y, 1, height);
		fillRect(x + 1, y + height, width, 1);
	}

	public void clipPolygon(int[] xpoints, int[] ypoints, int npoints) {
		if (isClose) {
			return;
		}
		try {
			Paint.Style tmp = paint.getStyle();
			paint.setStyle(Paint.Style.FILL);
			path.reset();
			path.moveTo(xpoints[0], ypoints[1]);
			for (int i = 2; i < npoints * 2; i += 2) {
				path.lineTo(xpoints[i], ypoints[i + 1]);
			}
			path.close();
			canvas.drawPath(path, paint);
			paint.setStyle(tmp);
		} catch (Exception e) {
		}
	}

	/**
	 * 绘制指定矩形的 3D突出显示边框
	 */
	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		if (isClose) {
			return;
		}
		LColor color = getColor();
		LColor colorUp, colorDown;
		if (raised) {
			colorUp = color.brighter();
			colorDown = color.darker();
		} else {
			colorUp = color.darker();
			colorDown = color.brighter();
		}

		setColor(colorUp);
		fillRect(x, y, width, 1);
		fillRect(x, y + 1, 1, height);

		setColor(colorDown);
		fillRect(x + width, y, 1, height);
		fillRect(x + 1, y + height, width, 1);
	}

	public int getClipHeight() {
		return clip.bottom - clip.top;
	}

	public int getClipWidth() {
		return clip.right - clip.left;
	}

	public int getClipX() {
		return clip.left;
	}

	public int getClipY() {
		return clip.top;
	}

	public void clearRect(int x, int y, int w, int h) {
		if (isClose) {
			return;
		}
		int oldColor = paint.getColor();
		paint.setColor(Color.BLACK);
		canvas.drawRect(0, 0, w, h, paint);
		paint.setColor(oldColor);
	}

	public void clearScreen(int x, int y, int w, int h) {
		clearRect(x, y, w, h);
	}

	public void quadrilateral(float x1, float y1, float x2, float y2, float x3,
			float y3, float x4, float y4) {
		if (isClose) {
			return;
		}
		path.reset();
		path.moveTo(x1, y1);
		path.lineTo(x2, y2);
		path.lineTo(x3, y3);
		path.lineTo(x4, y4);
		path.close();
		canvas.drawPath(path, paint);
	}

	public void copyArea(LImage img, int sx, int sy, int sw, int sh, int dx,
			int dy, int dw, int dh) {
		copyArea(img.getBitmap(), sx, sy, sw, sh, dx, dy, dw, dh);
	}

	public void copyArea(Bitmap bit, int sx, int sy, int sw, int sh, int dx,
			int dy, int dw, int dh) {
		if (isClose) {
			return;
		}
		if (bit == null) {
			return;
		}
		srcR.set(dx, dy, dx + dw, dy + dh);
		dstR.set(sx, sy, sx + sw, sy + sh);
		canvas.drawBitmap(bit, srcR, dstR, null);
	}

	public void copyArea(int sx, int sy, int width, int height, int dx, int dy,
			int anchor) {
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
				dy -= height - 1;
			}
		} else if ((anchor & VCENTER) != 0) {
			dy -= (height - 1) >>> 1;
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
				dx -= width;
			}
		} else if ((anchor & HCENTER) != 0) {
			dx -= (width - 1) >>> 1;
		} else {
			badAnchor = true;
		}
		if (badAnchor) {
			throw new IllegalArgumentException("Bad Anchor");
		}
		copyArea(sx, sy, width, height, dx - sx, dy - sy);
	}

	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		if (isClose) {
			return;
		}
		if (x < 0) {
			width += x;
			x = 0;
		}
		if (y < 0) {
			height += y;
			y = 0;
		}
		if (x + width > grapBitmap.getWidth()) {
			width = grapBitmap.getWidth() - x;
		}
		if (y + height > grapBitmap.getHeight()) {
			height = grapBitmap.getHeight() - y;
		}
		Bitmap copy = Bitmap.createBitmap(grapBitmap, x, y, width, height);
		canvas.drawBitmap(copy, x + dx, y + dy, null);
		copy.recycle();
		copy = null;
	}

	public void clipRect(int x, int y, int width, int height) {
		if (isClose) {
			return;
		}
		canvas.clipRect(x, y, x + width, y + height);
		clip = canvas.getClipBounds();
	}

	public void shear(double shx, double shy) {
		if (isClose) {
			return;
		}
		tmp_matrix.reset();
		tmp_matrix.setSkew((float) shx, (float) shy);
		canvas.concat(tmp_matrix);
	}

	public void setStrokeStyle(int style) {
		if (isClose) {
			return;
		}
		if (style != SOLID && style != DOTTED) {
			throw new IllegalArgumentException();
		}
		this.strokeStyle = style;
		if (style == SOLID) {
			paint.setPathEffect(null);
		} else {
			paint.setPathEffect(dashPathEffect);
		}
	}

	public int getStrokeStyle() {
		return this.strokeStyle;
	}

	public void translate(float x, float y) {
		if (isClose) {
			return;
		}
		canvas.translate(x, y);
		clip.left -= x;
		clip.right -= x;
		clip.top -= y;
		clip.bottom -= y;
	}

	public void translate(int x, int y) {
		if (isClose) {
			return;
		}
		canvas.translate(x, y);
		clip.left -= x;
		clip.right -= x;
		clip.top -= y;
		clip.bottom -= y;
	}

	public void setClip(Rect rect) {
		setClip(rect.left, rect.top, rect.width(), rect.height());
	}

	public void setClip(int x, int y, int width, int height) {
		if (isClose) {
			return;
		}
		if (x == clip.left && x + width == clip.right && y == clip.top
				&& y + height == clip.bottom) {
			return;
		}
		if (x < clip.left || x + width > clip.right || y < clip.top
				|| y + height > clip.bottom) {
			canvas.restore();
			canvas.save(Canvas.CLIP_SAVE_FLAG);
		}
		clip.left = x;
		clip.top = y;
		clip.right = x + width;
		clip.bottom = y + height;
		canvas.clipRect(clip);
	}

	public void drawOval(int x, int y, int width, int height) {
		if (isClose) {
			return;
		}
		Paint.Style tmp = paint.getStyle();
		paint.setStyle(Paint.Style.STROKE);
		rectF.set(x, y, x + width, y + height);
		canvas.drawOval(rectF, paint);
		paint.setStyle(tmp);
	}

	public void drawPolygon(int[] xpoints, int[] ypoints, int npoints) {
		if (isClose) {
			return;
		}
		canvas.drawLine(xpoints[npoints - 1], ypoints[npoints - 1], xpoints[0],
				ypoints[0], paint);
		for (int i = 0; i < npoints - 1; i++) {
			canvas.drawLine(xpoints[i], ypoints[i], xpoints[i + 1],
					ypoints[i + 1], paint);
		}
	}

	public void drawPolyline(int[] xpoints, int[] ypoints, int npoints) {
		if (isClose) {
			return;
		}
		for (int i = 0; i < npoints - 1; i++) {
			drawLine(xpoints[i], ypoints[i], xpoints[i + 1], ypoints[i + 1]);
		}
	}

	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		if (isClose) {
			return;
		}
		rectF.set(x, y, width, height);
		canvas.drawRoundRect(rectF, arcWidth, arcHeight, paint);
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public Paint getPaint() {
		return paint;
	}

	public Rect getClip() {
		return clip;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}

	public void setBitmap(Bitmap bit) {
		this.width = bit.getWidth();
		this.height = bit.getHeight();
		this.canvas.clipRect(0, 0, width, height);
		this.canvas.setBitmap(bit);
	}

	public Bitmap getBitmap() {
		return grapBitmap;
	}

	public boolean isClose() {
		return isClose;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	public void dispose() {
		isClose = true;
		font = null;
		paint = null;
		path = null;
		canvas = null;
	}
}
