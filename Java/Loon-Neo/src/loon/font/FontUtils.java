package loon.font;

import loon.utils.StringUtils;
import loon.utils.TArray;

public class FontUtils {

	private static final int UNSPECIFIED = -1;

	public static float measureText(final IFont font, final CharSequence chars) {
		return FontUtils.measureText(font, chars, 0, chars.length());
	}

	public static float measureText(final IFont font, final CharSequence chars,
			final int start, final int end) {
		final int textLength = end - start;
		if (textLength <= 0) {
			return 0;
		} else if (textLength == 1) {
			return font.charWidth(chars.charAt(start));
		}
		if (chars instanceof String) {
			return font.stringWidth(((String) chars).substring(start, end)
					.toString());
		} else {
			return font.stringWidth(new StringBuffer(chars).substring(start,
					end).toString());
		}
	}

	public static <T extends TArray<CharSequence>> T splitLines(
			final CharSequence chars, final T result) {
		return StringUtils.splitArray(chars, '\n', result);
	}

	public static <T extends TArray<CharSequence>> T splitLines(
			final IFont font, final CharSequence chars, final T result,
			final AutoWrap autoWrap, final float autoWrapWidth) {
		switch (autoWrap) {
		case VERTICAL:
			return FontUtils.splitLinesByLetters(font, chars, result,
					autoWrapWidth);
		case HORIZONTAL:
			return FontUtils.splitLinesByWords(font, chars, result,
					autoWrapWidth);
		default:
		case NONE:
		case CJK:
			return FontUtils
					.splitLinesByCJK(font, chars, result, autoWrapWidth);
		}
	}

	private static <T extends TArray<CharSequence>> T splitLinesByLetters(
			final IFont font, final CharSequence chars, final T result,
			final float autoWrapWidth) {
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
				final float lookaheadLineWidth = FontUtils.measureText(font,
						chars, lineStart, lastNonWhitespace);

				final boolean isEndReached = (i == (textLength - 1));
				if (isEndReached) {
					if (lookaheadLineWidth <= autoWrapWidth) {
						result.add(chars.subSequence(lineStart,
								lastNonWhitespace));
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

	private static <T extends TArray<CharSequence>> T splitLinesByWords(
			final IFont font, final CharSequence chars, final T result,
			final float autoWrapWidth) {
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

			final float wordWidth = FontUtils.measureText(font, chars,
					wordStart, wordEnd);

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
					lineWidthRemaining -= FontUtils.getAdvance(font,
							chars, lastWordEnd - 1);
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

	private static <T extends TArray<CharSequence>> T splitLinesByCJK(
			final IFont font, final CharSequence chars, final T result,
			final float autoWrapWidth) {
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

				final float lineWidth = FontUtils.measureText(font, chars,
						lineStart, lineEnd);

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

	private static float getAdvance(final IFont font,
			final CharSequence chars, final int idx) {
		return -font.charWidth(chars.charAt(idx));
	}

}
