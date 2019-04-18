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
package loon.font;

import loon.HorizontalAlign;
import loon.canvas.LColor;
import loon.geom.PointF;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * Loon的字体渲染辅助工具,它并非针对某一特定Font的,而是所有实现了IFont接口的类都可以使用此工具
 */
public class FontUtils {

	private static final int UNSPECIFIED = -1;

	public static void drawLeft(GLEx g, String s, int x, int y) {
		drawString(g, g.getFont(), s, HorizontalAlign.LEFT, x, y, 0, LColor.white);
	}

	public static void drawLeft(GLEx g, IFont font, String s, int x, int y) {
		drawString(g, font, s, HorizontalAlign.LEFT, x, y, 0, LColor.white);
	}

	public static void drawCenter(GLEx g, String s, int x, int y, int width) {
		drawString(g, g.getFont(), s, HorizontalAlign.CENTER, x, y, width, LColor.white);
	}

	public static void drawCenter(GLEx g, IFont font, String s, int x, int y, int width) {
		drawString(g, font, s, HorizontalAlign.CENTER, x, y, width, LColor.white);
	}

	public static void drawCenter(GLEx g, String s, int x, int y, int width, LColor color) {
		drawString(g, g.getFont(), s, HorizontalAlign.CENTER, x, y, width, color);
	}

	public static void drawCenter(GLEx g, IFont font, String s, int x, int y, int width, LColor color) {
		drawString(g, font, s, HorizontalAlign.CENTER, x, y, width, color);
	}

	public static void drawRight(GLEx g, String s, int x, int y, int width) {
		drawString(g, g.getFont(), s, HorizontalAlign.RIGHT, x, y, width, LColor.white);
	}

	public static void drawRight(GLEx g, IFont font, String s, int x, int y, int width) {
		drawString(g, font, s, HorizontalAlign.RIGHT, x, y, width, LColor.white);
	}

	public static void drawRight(GLEx g, String s, int x, int y, int width, LColor color) {
		drawString(g, g.getFont(), s, HorizontalAlign.RIGHT, x, y, width, color);
	}

	public static void drawRight(GLEx g, IFont font, String s, int x, int y, int width, LColor color) {
		drawString(g, font, s, HorizontalAlign.RIGHT, x, y, width, color);
	}

	public static final void drawString(GLEx g, IFont font, final String s, final HorizontalAlign align, final int x,
			final int y, final int width, LColor color) {
		if (align == HorizontalAlign.LEFT) {
			font.drawString(g, s, x, y, color);
		} else if (align == HorizontalAlign.CENTER) {
			font.drawString(g, s, x + (width / 2) - (font.stringWidth(s) / 2), y, color);
		} else if (align == HorizontalAlign.RIGHT) {
			font.drawString(g, s, x + width - font.stringWidth(s), y, color);
		}
	}

	public static float measureText(final IFont font, final CharSequence chars) {
		return FontUtils.measureText(font, chars, 0, chars.length());
	}

	public static float measureText(final IFont font, final CharSequence chars, final int start, final int end) {
		final int textLength = end - start;
		if (textLength <= 0) {
			return 0;
		} else if (textLength == 1) {
			return font.charWidth(chars.charAt(start));
		}
		if (chars instanceof String) {
			return font.stringWidth(((String) chars).substring(start, end).toString());
		} else {
			return font.stringWidth(new StringBuffer(chars).substring(start, end).toString());
		}
	}

	public static PointF getTextWidthAndHeight(IFont font, String message) {
		return getTextWidthAndHeight(font, message, 1f, 1f);
	}

	public static PointF getTextWidthAndHeight(IFont font, String message, float defWidth, float defHeight) {
		if (font != null && message != null) {
			TArray<CharSequence> result = new TArray<CharSequence>();
			result = splitLines(message, result);
			float maxWidth = 0f;
			float space = font.getSize() / 4f;
			for (int i = 0; i < result.size; i++) {
				maxWidth = MathUtils.max(maxWidth, font.stringWidth(new StringBuffer(result.get(i)).toString()));
			}
			if (defWidth >= 1f && defHeight >= 1f) {
				return new PointF(MathUtils.max(defWidth, maxWidth) + space,
						MathUtils.max(defHeight, (result.size * font.getHeight())) + space);
			} else {
				return new PointF(maxWidth + space, (result.size * font.getHeight()) + space);
			}
		}
		return new PointF(defWidth, defHeight);
	}

	public static <T extends TArray<CharSequence>> T splitLines(final CharSequence chars, final T result) {
		return StringUtils.splitArray(chars, '\n', result);
	}

	public static <T extends TArray<CharSequence>> T splitLines(final IFont font, final CharSequence chars,
			final T result, final AutoWrap autoWrap, final float autoWrapWidth) {
		switch (autoWrap) {
		case VERTICAL:
			return FontUtils.splitLinesByLetters(font, chars, result, autoWrapWidth);
		case HORIZONTAL:
			return FontUtils.splitLinesByWords(font, chars, result, autoWrapWidth);
		default:
		case NONE:
		case CJK:
			return FontUtils.splitLinesByCJK(font, chars, result, autoWrapWidth);
		}
	}

	private static <T extends TArray<CharSequence>> T splitLinesByLetters(final IFont font, final CharSequence chars,
			final T result, final float autoWrapWidth) {
		final int textLength = chars.length();

		int lineStart = 0;
		int lineEnd = 0;
		int lastNonWhitespace = 0;
		boolean charsAvailable = false;

		for (int i = 0; i < textLength; i++) {
			final char character = chars.charAt(i);
			if (character != ' ') {
				if (charsAvailable) {
					lastNonWhitespace = i + 1;
				} else {
					charsAvailable = true;
					lineStart = i;
					lastNonWhitespace = lineStart + 1;
					lineEnd = lastNonWhitespace;
				}
			}

			if (charsAvailable) {
				final float lookaheadLineWidth = FontUtils.measureText(font, chars, lineStart, lastNonWhitespace);

				final boolean isEndReached = (i == (textLength - 1));
				if (isEndReached) {
					if (lookaheadLineWidth <= autoWrapWidth) {
						result.add(chars.subSequence(lineStart, lastNonWhitespace));
					} else {
						result.add(chars.subSequence(lineStart, lineEnd));
						if (lineStart != i) {
							result.add(chars.subSequence(i, lastNonWhitespace));
						}
					}
				} else {
					if (lookaheadLineWidth <= autoWrapWidth) {
						lineEnd = lastNonWhitespace;
					} else {
						result.add(chars.subSequence(lineStart, lineEnd));
						i = lineEnd - 1;
						charsAvailable = false;
					}
				}
			}
		}

		return result;
	}

	private static <T extends TArray<CharSequence>> T splitLinesByWords(final IFont font, final CharSequence chars,
			final T result, final float autoWrapWidth) {
		final int textLength = chars.length();

		if (textLength == 0) {
			return result;
		}

		final float spaceWidth = font.charWidth(' ');

		int lastWordEnd = FontUtils.UNSPECIFIED;
		int lineStart = FontUtils.UNSPECIFIED;
		int lineEnd = FontUtils.UNSPECIFIED;

		float lineWidthRemaining = autoWrapWidth;
		boolean firstWordInLine = true;
		int i = 0;
		while (i < textLength) {
			int spacesSkipped = 0;
			while ((i < textLength) && (chars.charAt(i) == ' ')) {
				i++;
				spacesSkipped++;
			}
			final int wordStart = i;

			if (lineStart == FontUtils.UNSPECIFIED) {
				lineStart = wordStart;
			}

			while ((i < textLength) && (chars.charAt(i) != ' ')) {
				i++;
			}

			final int wordEnd = i;

			if (wordStart == wordEnd) {
				if (!firstWordInLine) {
					result.add(chars.subSequence(lineStart, lineEnd));
				}
				break;
			}

			final float wordWidth = FontUtils.measureText(font, chars, wordStart, wordEnd);

			final float widthNeeded;
			if (firstWordInLine) {
				widthNeeded = wordWidth;
			} else {
				widthNeeded = (spacesSkipped * spaceWidth) + wordWidth;
			}

			if (widthNeeded <= lineWidthRemaining) {
				if (firstWordInLine) {
					firstWordInLine = false;
				} else {
					lineWidthRemaining -= FontUtils.getAdvance(font, chars, lastWordEnd - 1);
				}
				lineWidthRemaining -= widthNeeded;
				lastWordEnd = wordEnd;
				lineEnd = wordEnd;

				if (wordEnd == textLength) {
					result.add(chars.subSequence(lineStart, lineEnd));
					break;
				}
			} else {
				if (firstWordInLine) {
					if (wordWidth >= autoWrapWidth) {
						result.add(chars.subSequence(wordStart, wordEnd));
						lineWidthRemaining = autoWrapWidth;
					} else {
						lineWidthRemaining = autoWrapWidth - wordWidth;
						if (wordEnd == textLength) {
							result.add(chars.subSequence(wordStart, wordEnd));
							break;
						}
					}
					firstWordInLine = true;
					lastWordEnd = FontUtils.UNSPECIFIED;
					lineStart = FontUtils.UNSPECIFIED;
					lineEnd = FontUtils.UNSPECIFIED;
				} else {
					result.add(chars.subSequence(lineStart, lineEnd));
					if (wordEnd == textLength) {
						result.add(chars.subSequence(wordStart, wordEnd));
						break;
					} else {
						lineWidthRemaining = autoWrapWidth - wordWidth;
						firstWordInLine = false;
						lastWordEnd = wordEnd;
						lineStart = wordStart;
						lineEnd = wordEnd;
					}
				}
			}
		}
		return result;
	}

	private static <T extends TArray<CharSequence>> T splitLinesByCJK(final IFont font, final CharSequence chars,
			final T result, final float autoWrapWidth) {
		final int textLength = chars.length();

		int lineStart = 0;
		int lineEnd = 0;

		while ((lineStart < textLength) && (chars.charAt(lineStart) == ' ')) {
			lineStart++;
			lineEnd++;
		}

		int i = lineEnd;
		while (i < textLength) {
			lineStart = lineEnd;

			boolean charsAvailable = true;
			while (i < textLength) {

				int j = lineEnd;
				while (j < textLength) {
					if (chars.charAt(j) == ' ') {
						j++;
					} else {
						break;
					}
				}
				if (j == textLength) {
					if (lineStart == lineEnd) {
						charsAvailable = false;
					}
					i = textLength;
					break;
				}

				lineEnd++;

				final float lineWidth = FontUtils.measureText(font, chars, lineStart, lineEnd);

				if (lineWidth > autoWrapWidth) {
					if (lineStart < lineEnd - 1) {
						lineEnd--;
					}

					result.add(chars.subSequence(lineStart, lineEnd));
					charsAvailable = false;
					i = lineEnd;
					break;
				}
				i = lineEnd;
			}

			if (charsAvailable) {
				result.add(chars.subSequence(lineStart, lineEnd));
			}
		}

		return result;
	}

	private static float getAdvance(final IFont font, final CharSequence chars, final int idx) {
		return -font.charWidth(chars.charAt(idx));
	}

}
