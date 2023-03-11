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

import java.util.ArrayList;
import java.util.List;

import loon.font.TextFormat;
import loon.font.TextLayout;
import loon.font.TextWrap;
import loon.geom.RectBox;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

class AndroidTextLayout extends TextLayout {

	public final AndroidFont font;
	private final Paint.FontMetrics metrics;

	public static TextLayout layoutText(AndroidGraphics gfx, String text, TextFormat format) {
		AndroidFont font = gfx.resolveFont(format.font);
		Paint paint = new Paint(format.antialias ? Paint.ANTI_ALIAS_FLAG : 0);
		paint.setTypeface(font.typeface);
		paint.setTextSize(font.size);
		paint.setSubpixelText(true);
		Paint.FontMetrics metrics = paint.getFontMetrics();
		font.paint = paint;
		return new AndroidTextLayout(text, format, font, metrics, paint.measureText(text));
	}

	public static TextLayout[] layoutText(AndroidGraphics gfx, String text, TextFormat format, TextWrap wrap) {
		AndroidFont font = gfx.resolveFont(format.font);
		Paint paint = new Paint(format.antialias ? Paint.ANTI_ALIAS_FLAG : 0);
		paint.setTypeface(font.typeface);
		paint.setTextSize(font.size);
		paint.setSubpixelText(true);
		Paint.FontMetrics metrics = paint.getFontMetrics();
		font.paint = paint;

		List<TextLayout> layouts = new ArrayList<TextLayout>();
		float[] measuredWidth = new float[1];
		for (String ltext : normalizeEOL(text).split("\\n")) {
			if (wrap.width <= 0 || wrap.width == Float.MAX_VALUE) {
				layouts.add(new AndroidTextLayout(ltext, format, font, metrics, paint.measureText(ltext)));

			} else {
				int start = 0, end = ltext.length();
				while (start < end) {
					int count = paint.breakText(ltext, start, end, true, wrap.width, measuredWidth);
					int lineEnd = start + count;
					if (lineEnd < end && font.resources.length > 0) {
						int adjust = accountForLigatures(ltext, start, count, font.resources);
						count += adjust;
						lineEnd += adjust;
					}

					if (lineEnd == end) {
						layouts.add(new AndroidTextLayout(ltext.substring(start, lineEnd), format, font, metrics,
								measuredWidth[0]));
						start += count;

					} else {

						if (!Character.isWhitespace(ltext.charAt(lineEnd - 1))
								&& !Character.isWhitespace(ltext.charAt(lineEnd))) {
							do {
								--lineEnd;
							} while (lineEnd > start && !Character.isWhitespace(ltext.charAt(lineEnd)));
						}

						if (lineEnd == start) {
							layouts.add(new AndroidTextLayout(ltext.substring(start, start + count), format, font,
									metrics, measuredWidth[0]));
							start += count;

						} else {
							while (Character.isWhitespace(ltext.charAt(lineEnd - 1))) {
								--lineEnd;
							}
							String line = ltext.substring(start, lineEnd);
							float size = paint.measureText(line);
							layouts.add(new AndroidTextLayout(line, format, font, metrics, size));
							start = lineEnd;
						}
						while (start < end && Character.isWhitespace(ltext.charAt(start))) {
							start++;
						}
					}
				}
			}
		}
		return layouts.toArray(new TextLayout[layouts.size()]);
	}

	@Override
	public float ascent() {
		return -metrics.ascent;
	}

	@Override
	public float descent() {
		return metrics.descent;
	}

	@Override
	public float leading() {
		return metrics.leading;
	}

	AndroidTextLayout(String text, TextFormat format, AndroidFont font, Paint.FontMetrics metrics, float width) {
		this(text, format, font, metrics, width, -metrics.ascent + metrics.descent);
	}

	AndroidTextLayout(String text, TextFormat format, AndroidFont font, Paint.FontMetrics metrics, float width,
			float height) {
		super(text, format, new RectBox(0, 0, width, height), height);
		this.font = font;
		this.metrics = metrics;
	}

	void draw(Canvas canvas, float x, float y, Paint paint) {
		boolean oldAA = paint.isAntiAlias();
		paint.setAntiAlias(format.antialias);
		try {
			paint.setTypeface(font.typeface);
			paint.setTextSize(font.size);
			paint.setSubpixelText(true);

			if (font.size > 250) {
				Path path = new Path();
				paint.getTextPath(text, 0, text.length(), x, y - metrics.ascent, path);
				canvas.drawPath(path, paint);
			} else {
				canvas.drawText(text, x, y - metrics.ascent, paint);
			}

		} finally {
			paint.setAntiAlias(oldAA);
		}
	}

	static int accountForLigatures(String text, int start, int count, String[] ligatures) {
		int adjust = 0;
		for (String lig : ligatures) {
			int llen = lig.length(), idx = start;
			while ((idx = text.indexOf(lig, idx)) != -1) {
				if (idx + 1 > start + count) {
					break;
				}
				int extra = llen - 1;
				adjust += extra;
				count += extra;
				idx += llen;
			}
		}
		return adjust;
	}

	@Override
	public int stringWidth(String message) {
		return (int) this.font.paint.measureText(message);
	}

	@Override
	public int getHeight() {
		return this.font.paint.getFontMetricsInt(this.font.paint.getFontMetricsInt());
	}

	@Override
	public int charWidth(char ch) {
		char[] chars = Character.toChars(ch);
		int w = (int) this.font.paint.measureText(chars, 0, 1);
		return w;
	}
}
