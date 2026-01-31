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
package loon.cport.bridge;

import loon.LRelease;
import loon.LSystem;
import loon.canvas.LColor;
import loon.geom.RectI;
import loon.utils.MathUtils;
import loon.utils.PathUtils;
import loon.utils.StringUtils;

public final class STBFont implements LRelease {

	public final static class VMetric {

		public int ascent;

		public int descent;

		public int lineGap;

	}

	public final static class VFormat {

		protected int width;

		protected int height;

		protected int format;

	}

	public static class FontData {

		public final RectI fontSize = new RectI();
		public int[] pixels;

	}

	public final static boolean existsSysFont() {
		return existsFont(LSystem.getSystemGameFontName());
	}

	public final static boolean existsFont(String fontName) {
		String path = fontName;
		final String ext = PathUtils.getExtension(fontName).trim().toLowerCase();
		if (StringUtils.isEmpty(ext)) {
			path += ".ttf";
		}
		String baseFile = path;
		if (!(SDLCall.fileExists(path) || SDLCall.rwFileExists(path))) {
			baseFile = PathUtils.getCombinePaths(LSystem.getPathPrefix(), path);
			if (!(SDLCall.fileExists(baseFile) || SDLCall.rwFileExists(baseFile))) {
				baseFile = path;
				if ((SDLCall.fileExists(baseFile) || SDLCall.rwFileExists(baseFile))) {
					return true;
				}
				return false;
			} else {
				return true;
			}
		}
		return true;
	}

	private final static String convertFontPath(String path) {
		String baseFile = PathUtils.getCombinePaths(SDLCall.getBasePath(), LSystem.getPathPrefix(), path);
		if (!SDLCall.fileExists(baseFile)) {
			baseFile = PathUtils.getCombinePaths(LSystem.getPathPrefix(), path);
			if (!SDLCall.fileExists(baseFile)) {
				baseFile = path;
			}
		}
		return baseFile;
	}

	public final static STBFont create(String path) {
		long handle = STBCall.loadFontInfo(convertFontPath(path));
		return new STBFont(handle);
	}

	public final static STBFont create(String path, String styleName, int style) {
		long handle = STBCall.loadFontStyleInfo(convertFontPath(path), styleName, style);
		return new STBFont(handle);
	}

	public final static STBFont createSystemFont(String styleName, String path, String defStyle, int style) {
		long handle = STBCall.loadSystemFontStyleInfo(styleName, convertFontPath(path), defStyle, style);
		return new STBFont(handle);
	}

	private boolean _closed;

	private long _fontHandle;

	private final RectI _codepointBitmapBox = new RectI();

	private final RectI _textSize = new RectI();

	private final RectI _charSize = new RectI();

	private final RectI _rectSize = new RectI();

	private final VMetric _metric = new VMetric();

	private final int[] _fontSize = new int[4];

	private final int[] _outsize = new int[2];

	private STBFont(long handle) {
		_fontHandle = handle;
	}

	public int measureWidth(String text, float fontScale) {
		return STBCall.measureTextWidth(_fontHandle, text, MathUtils.iceil(fontScale));
	}

	public int measureHieght(String text, float fontScale) {
		return STBCall.measureTextHieght(_fontHandle, text, MathUtils.iceil(fontScale));
	}

	public RectI getTextLinesSize(String text, float fontScale, int align) {
		STBCall.getTextLinesSize(_fontHandle, text, fontScale, align, _fontSize);
		_textSize.width = _fontSize[0];
		_textSize.height = _fontSize[1];
		return _textSize;
	}

	public byte[] textLinesToBytes(String text, float fontScale, int align) {
		RectI rect = getTextLinesSize(text, fontScale, align);
		byte[] bytes = new byte[rect.width * rect.height];
		STBCall.drawTextLinesToBytes(align, text, fontScale, align, _fontSize, bytes);
		return bytes;
	}

	public int[] textLinesToInt32(String text, float fontScale, LColor fontColor) {
		return textLinesToInt32(text, fontScale, 0, fontColor, LColor.transparent);
	}

	public int[] textLinesToInt32(String text, float fontScale, int align, LColor fontColor, LColor bgColor) {
		RectI rect = getTextLinesSize(text, fontScale, align);
		int[] pixels = new int[rect.width * rect.height];
		STBCall.drawTextLinesToInt32(_fontHandle, text, fontScale, align, fontColor.getRed(), fontColor.getGreen(),
				fontColor.getBlue(), bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), bgColor.getAlpha(),
				_fontSize, pixels);
		return pixels;
	}

	public RectI getOutSize() {
		_rectSize.width = _fontSize[0];
		_rectSize.height = _fontSize[1];
		return _rectSize;
	}

	public RectI getCodepointBitmapBox(float fontsize, int point) {
		STBCall.getCodepointBitmapBox(_fontHandle, fontsize, point, _fontSize);
		_codepointBitmapBox.set(_fontSize[0], _fontSize[1], _fontSize[2], _fontSize[3]);
		return _codepointBitmapBox;
	}

	public VMetric getFontVMetrics(float fontsize) {
		STBCall.getFontVMetrics(_fontHandle, fontsize, _fontSize);
		_metric.ascent = _fontSize[0];
		_metric.descent = _fontSize[1];
		_metric.lineGap = _fontSize[2];
		return _metric;
	}

	public RectI getStringSize(float fontsize, String text) {
		STBCall.getCharsSize(_fontHandle, MathUtils.iceil(fontsize), text, _fontSize);
		_textSize.width = _fontSize[0];
		_textSize.height = _fontSize[1];
		return _textSize;
	}

	public RectI getCharSize(float fontsize, int point) {
		STBCall.getCharSize(_fontHandle, MathUtils.iceil(fontsize), point, _fontSize);
		_charSize.width = _fontSize[0];
		_charSize.height = _fontSize[1];
		return _charSize;
	}

	public int getCodepointHMetrics(int point) {
		return STBCall.getCodepointHMetrics(_fontHandle, point);
	}

	public byte[] makeCodepointBytePixels(int point, float fontScale, int width, int height) {
		final byte[] bytes = new byte[width * height];
		STBCall.makeCodepointBitmap(_fontHandle, point, fontScale, width, height, bytes);
		return bytes;
	}

	public int[] makeCodepointPixels32(int point, float fontScale, int width, int height, LColor fontColor) {
		final int[] pixels = new int[width * height];
		STBCall.makeCodepointBitmap32(_fontHandle, point, fontScale, width, height, fontColor.getRed(),
				fontColor.getGreen(), fontColor.getBlue(), pixels);
		return pixels;
	}

	public byte[] makeDrawTextBytePixels(int point, String text, float fontScale, int width, int height) {
		final byte[] bytes = new byte[width * height];
		STBCall.makeDrawTextToBitmap(_fontHandle, text, fontScale, width, height, bytes);
		return bytes;
	}

	public int[] makeDrawTextPixels32(String text, float fontScale, int width, int height, LColor fontColor) {
		final int[] pixels = new int[width * height];
		STBCall.makeDrawTextToBitmap32(_fontHandle, text, fontScale, width, height, fontColor.getRed(),
				fontColor.getGreen(), fontColor.getBlue(), pixels);
		return pixels;
	}

	public FontData drawChar(int codepoint, float fontScale, int color) {
		final int maxFontSize = (int) (fontScale * fontScale);
		final int size = MathUtils.iceil(maxFontSize * 8);
		int[] outpixels = new int[size];
		STBCall.drawChar(_fontHandle, codepoint, MathUtils.iceil(fontScale), color, _outsize, outpixels);
		final FontData result = new FontData();
		result.fontSize.set(_outsize[0], _outsize[1]);
		final int length = result.fontSize.width * result.fontSize.height;
		final int[] newPixels = new int[length];
		System.arraycopy(outpixels, 0, newPixels, 0, length);
		result.pixels = newPixels;
		outpixels = null;
		return result;
	}

	public FontData drawString(String text, float fontScale, int color) {
		final int maxFontSize = (int) (fontScale * fontScale);
		final int size = MathUtils.iceil((maxFontSize * text.length()) + (fontScale * text.length() * 8));
		int[] outpixels = new int[size];
		STBCall.drawString(_fontHandle, text, MathUtils.iceil(fontScale), color, _outsize, outpixels);
		final FontData result = new FontData();
		result.fontSize.set(_outsize[0], _outsize[1]);
		final int length = result.fontSize.width * result.fontSize.height;
		final int[] newPixels = new int[length];
		System.arraycopy(outpixels, 0, newPixels, 0, length);
		result.pixels = newPixels;
		outpixels = null;
		return result;
	}

	public long getHandle() {
		return _fontHandle;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		if (_fontHandle != 0 && !_closed) {
			STBCall.closeFontInfo(_fontHandle);
			_fontHandle = 0;
			_closed = true;
		}
	}

}
