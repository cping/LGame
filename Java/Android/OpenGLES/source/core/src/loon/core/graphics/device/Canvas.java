/**
 * Copyright 2014
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
 * @version 0.4.2
 */
package loon.core.graphics.device;

import loon.core.geom.Dimension;

public class Canvas {

	private boolean _isFillAlpha = false;

	private LFont _oldFont;

	private int _oldAlpha;

	private LColor _oldColor;

	LImage _bufferedImage;
	LGraphics _graphics;

	Canvas() {

	}

	public Canvas(Bitmap bitmap) {
		this._bufferedImage = bitmap.bufferedImage;
		this._graphics = bitmap.bufferedImage.getLGraphics();
		enableAntiAliasing();
		_graphics.save();
	}

	public Canvas(LGraphics g) {
		this._graphics = g;
		enableAntiAliasing();
		_graphics.save();
	}

	public void restore() {
		_graphics.restore();
	}

	public void save() {
		_graphics.save();
	}

	public void rotate(float angle, float cx, float cy) {
		_graphics.rotate(angle, cx, cy);
	}

	public void drawBitmap(Bitmap image, Rect src, Rect dst, Paint paint) {
		if (image == null) {
			return;
		}
		if (paint != null) {
			if (paint.isTransparent()) {
				return;
			}
			setColorAndStroke(paint);
		}
		_graphics.drawImage(image.bufferedImage, dst.left, dst.top, dst.right,
				dst.bottom, src.left, src.top, src.right, src.bottom);
		if (paint != null) {
			resetColorAndStroke();
		}
	}

	public void setAlpha(int alpha) {
		float a = 0;
		if (alpha <= 0) {
			a = 0;
		} else if (alpha > 255) {
			a = 1.0f;
		} else {
			a = (float) alpha / 255f;
			if (a > 1.0f) {
				a = 1.0f;
			}
		}
		_graphics.setAlpha(a);
	}

	public LGraphics get() {
		return _graphics;
	}

	public void drawLine(float x1, float y1, float x2, float y2, Paint paint) {
		if (paint != null) {
			if (paint.isTransparent()) {
				return;
			}
			setColorAndStroke(paint);
		}
		this._graphics.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
		if (paint != null) {
			resetColorAndStroke();
		}
	}

	public void drawBitmap(Bitmap bitmap, int left, int top, Paint paint) {
		if (paint != null) {
			if (paint.isTransparent()) {
				return;
			}
			setColorAndStroke(paint);
		}
		this._graphics.drawImage(bitmap.bufferedImage, left, top);
		if (paint != null) {
			resetColorAndStroke();
		}
	}

	public void drawBitmap(Bitmap bitmap, int left, int top) {
		this._graphics.drawImage(bitmap.bufferedImage, left, top);
	}

	public void drawClear() {
		_graphics.drawClear();
	}

	public void drawClear(LColor c, int w, int h) {
		_graphics.setColor(c);
		_graphics.fillRect(0, 0, w, h);
	}

	public void fillColor(int color) {
		fillColor(new LColor(color));
	}

	public Dimension getDimension() {
		return new Dimension(getWidth(), getHeight());
	}

	public int getHeight() {
		return this._bufferedImage != null ? this._bufferedImage.getHeight()
				: 0;
	}

	public int getWidth() {
		return this._bufferedImage != null ? this._bufferedImage.getWidth() : 0;
	}

	public void setBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			this._bufferedImage = null;
			this._graphics = null;
		} else {
			this._bufferedImage = bitmap.bufferedImage;
			this._graphics = this._bufferedImage.getLGraphics();
			enableAntiAliasing();
		}
	}

	public void drawText(String text, float x, float y, Paint paint) {
		drawText(text, (int) x, (int) y, paint);
	}

	public void drawText(String text, int x, int y, Paint paint) {
		if (text == null || text.length() == 0) {
			return;
		}
		if (paint == null) {
			_graphics.drawString(text, x, y);
		} else {
			if (paint != null) {
				if (paint.isTransparent()) {
					return;
				}
				setColorAndStroke(paint);
			}
			int pos = LGraphics.LEFT;
			if (paint.align != null) {
				switch (paint.align) {
				case LEFT:
					pos = LGraphics.LEFT;
					break;
				case CENTER:
					pos = LGraphics.HCENTER;
					break;
				case RIGHT:
					pos = LGraphics.RIGHT;
					break;
				}
			}
			int w = paint.strokeWidth;
			switch (paint.style) {
			case Paint.Style.STROKE:
				_graphics.drawString(text, x, y - w, pos);
				_graphics.drawString(text, x + w, y - w, pos);
				_graphics.drawString(text, x + w, y, pos);
				_graphics.drawString(text, x + w, y + w, pos);
				_graphics.drawString(text, x, y + w, pos);
				_graphics.drawString(text, x - w, y + w, pos);
				_graphics.drawString(text, x - w, y, pos);
				_graphics.drawString(text, x - w, y - w, pos);
				break;
			case Paint.Style.FILL:
				_graphics.drawString(text, x, y, pos);
				break;
			case Paint.Style.FILL_AND_STROKE:
				_graphics.drawString(text, x, y, pos);
				break;
			default:
				_graphics.drawString(text, x, y, pos);
				break;
			}
			if (paint != null) {
				resetColorAndStroke();
			}
		}
	}

	public void drawCircle(float x, float y, float radius, Paint paint) {
		if (paint.isTransparent()) {
			return;
		}
		if (paint != null) {
			if (paint.isTransparent()) {
				return;
			}
			setColorAndStroke(paint);
		}
		float doubleRadius = radius * 2;

		int style = paint.style;
		switch (style) {
		case Paint.Style.FILL:
			if (_isFillAlpha) {
				_graphics.setAlpha(1.0f);
			}
			this._graphics.fillOval((int) (x - radius), (int) (y - radius),
					(int) doubleRadius, (int) doubleRadius);
			if (_isFillAlpha) {
				_graphics.setAlpha(0.5f);
			}
			return;

		case Paint.Style.STROKE:
			this._graphics.drawOval((int) (x - radius), (int) (y - radius),
					(int) doubleRadius, (int) doubleRadius);
			return;
		case Paint.Style.FILL_AND_STROKE:
			this._graphics.fillOval((int) (x - radius), (int) (y - radius),
					(int) doubleRadius, (int) doubleRadius);
			this._graphics.drawOval((int) (x - radius), (int) (y - radius),
					(int) doubleRadius, (int) doubleRadius);
			return;
		default:
			break;
		}
		if (paint != null) {
			resetColorAndStroke();
		}
	}

	public void drawRect(Rect rect, Paint paint) {
		if (paint == null) {
			return;
		}
		if (paint != null) {
			if (paint.isTransparent()) {
				return;
			}
			setColorAndStroke(paint);
		}
		switch (paint.style) {
		case Paint.Style.STROKE:
			_graphics.drawRect(rect.left, rect.top, rect.right - rect.left,
					rect.bottom - rect.top);
			break;
		case Paint.Style.FILL:
			_graphics.fillRect(rect.left, rect.top, rect.right - rect.left,
					rect.bottom - rect.top);
			break;
		case Paint.Style.FILL_AND_STROKE:
			_graphics.fillRect(rect.left, rect.top, rect.right - rect.left,
					rect.bottom - rect.top);
			_graphics.drawRect(rect.left, rect.top, rect.right - rect.left,
					rect.bottom - rect.top);
			break;
		}
		if (paint != null) {
			resetColorAndStroke();
		}
	}

	public void drawRect(float x, float y, float w, float h, Paint paint) {

		if (paint != null) {
			if (paint.isTransparent()) {
				return;
			}
			setColorAndStroke(paint);
		}
		int style = paint.style;

		x = Math.abs(x - w);

		y = Math.abs(y - h);

		switch (style) {
		case Paint.Style.FILL:
			if (_isFillAlpha) {
				this._graphics.setAlpha(0.5f);
			}
			this._graphics.fillRect((int) w, (int) h, (int) x, (int) y);
			if (_isFillAlpha) {
				_graphics.setAlpha(1.0f);
			}
			return;
		case Paint.Style.STROKE:
			this._graphics.drawRect((int) w, (int) h, (int) x, (int) y);
			return;
		case Paint.Style.FILL_AND_STROKE:
			this._graphics.fillRect((int) w, (int) h, (int) x, (int) y);
			this._graphics.drawRect((int) w, (int) h, (int) x, (int) y);
			return;
		default:
			break;
		}
		if (paint != null) {
			resetColorAndStroke();
		}
	}

	public void drawColor(int color) {
		_graphics.setColor(color);
		_graphics.fillRect(_graphics.getClipX(), _graphics.getClipY(),
				_graphics.getClipWidth(), _graphics.getClipHeight());
	}

	public void drawPath(Path path, Paint paint) {
		if (paint != null) {
			if (paint.isTransparent()) {
				return;
			}
			setColorAndStroke(paint);
		}
		int style = paint.style;
		switch (style) {
		case Paint.Style.FILL:
			if (_isFillAlpha) {
				this._graphics.setAlpha(0.5f);
			}
			_graphics.fill(path, paint.getPaint());
			if (_isFillAlpha) {
				this._graphics.setAlpha(1.0f);
			}
			return;
		case Paint.Style.STROKE:
			_graphics.draw(path, paint.getPaint());
			return;
		case Paint.Style.FILL_AND_STROKE:
			_graphics.fill(path, paint.getPaint());
			_graphics.draw(path, paint.getPaint());
			return;
		default:
			break;
		}
		if (paint != null) {
			resetColorAndStroke();
		}
	}

	public int stringWidth(String str) {
		if (_graphics != null) {
			return _graphics.getLFont().stringWidth(str);
		} else {
			return LFont.getDefaultFont().stringWidth(str);
		}
	}

	public void setClip(int left, int top, int width, int height) {
		this._graphics.setClip(left, top, width, height);
	}

	private void enableAntiAliasing() {
		_graphics.setAntiAlias(true);
	}

	public void fillColor(LColor color) {
		this._graphics.setColor(color);
		this._graphics.fillRect(0, 0, getWidth(), getHeight());
	}

	private void setColorAndStroke(Paint paint) {
		if (paint != null) {
			this._oldAlpha = (int) (_graphics.getAlpha() * 255);
			this._oldFont = _graphics.getLFont();
			this._oldColor = _graphics.getColor();

			this.setAlpha(paint.alpha);
			this._graphics.setAntiAlias(paint.antiAlias);
			this._graphics.setColor(paint.color);
			this._graphics.setFont(paint.typeface.font);
		}
	}

	private void resetColorAndStroke() {
		this.setAlpha(_oldAlpha);
		this._graphics.setAntiAlias(false);
		this._graphics.setColor(_oldColor);
		this._graphics.setFont(_oldFont);
	}

	public boolean isFillAlpha() {
		return _isFillAlpha;
	}

	public void setFillAlpha(boolean f) {
		this._isFillAlpha = f;
	}
}
