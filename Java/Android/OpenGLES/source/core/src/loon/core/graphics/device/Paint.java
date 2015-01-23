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


public class Paint {

	public Align align;

	public int color = 0;

	public int alpha = 255;

	public boolean antiAlias = true;

	public int style = 0;

	public int strokeWidth = 1;

	public Typeface typeface = Typeface.DEFAULT;

	public float textSize = typeface.font.getSize();

	public static class Style {
		public static final int FILL = 0;
		public static final int STROKE = 10;
		public static final int FILL_AND_STROKE = 20;
	}

	public Paint() {
	}

	private android.graphics.Paint _paint = new android.graphics.Paint();

	android.graphics.Paint getPaint() {
		_paint.set(typeface.font.getTypefacePaint());
		_paint.setAntiAlias(this.antiAlias);
		_paint.setAlpha(alpha);
		switch (style) {
		case Style.FILL:
			_paint.setStyle(android.graphics.Paint.Style.FILL);
			break;
		case Style.STROKE:
			_paint.setStyle(android.graphics.Paint.Style.STROKE);
			break;
		case Style.FILL_AND_STROKE:
			_paint.setStyle(android.graphics.Paint.Style.FILL_AND_STROKE);
			break;
		}
		_paint.setColor(color);
		_paint.setStrokeWidth(strokeWidth);
		_paint.setTextSize(textSize);
		return _paint;
	}

	public void set(Paint src) {
		this.color = src.color;
		this.alpha = src.alpha;
		this.antiAlias = src.antiAlias;
		this.textSize = src.textSize;
		this.style = src.style;
		this.strokeWidth = src.strokeWidth;
		this.typeface = src.typeface;
	}

	public void reset() {
		this.color = 0;
		this.alpha = 255;
		this.antiAlias = false;
		this.textSize = typeface.font.getSize();
		this.style = 0;
		this.strokeWidth = 1;
		this.typeface = Typeface.DEFAULT;
	}

	public boolean isTransparent() {
		return alpha == 0;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public int getAlpha() {
		return alpha;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setAntiAlias(boolean flag) {
		this.antiAlias = flag;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	public void setStrokeWidth(int width) {
		this.strokeWidth = width;
	}

	public void setTextSize(float size) {
		textSize = size;
	}

	public float getTextSize() {
		return textSize;
	}

	public int measureText(String text) {
		return typeface.font.stringWidth(text);
	}

	public int measureText(char[] bufc, int index, int count) {
		String str = String.valueOf(bufc, index, count);
		return measureText(str);
	}

	public void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = (int) strokeWidth;
	}

	public void setTextAlign(Align align) {
		this.align = align;
	}

	public void setTypeface(LFont font) {
		this.typeface = new Typeface(font);
	}

	public void setTypeface(Typeface typeface) {
		if (typeface == null) {
			this.typeface = Typeface.DEFAULT;
		} else {
			this.typeface = typeface;
		}
	}
}
