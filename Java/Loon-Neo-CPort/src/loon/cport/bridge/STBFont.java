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
import loon.utils.PathUtils;
import loon.utils.StringUtils;

public final class STBFont implements LRelease {

	public final static class VMetric {

		public int ascent;

		public int descent;

		public int lineGap;

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

	private boolean _closed;

	private long _fontHandle;

	private final RectI _codepointBitmapBox = new RectI();

	private final RectI _textSize = new RectI();

	private final RectI _charSize = new RectI();

	private final RectI _outSize = new RectI();

	private final VMetric _metric = new VMetric();

	private final int[] _outDims = new int[] { 0, 0 };

	private STBFont(long handle) {
		_fontHandle = handle;
	}

	public int measureWidth(String text, float fontScale) {
		return STBCall.measureTextWidth(_fontHandle, text, fontScale);
	}

	public int measureHieght(String text, float fontScale) {
		return STBCall.measureTextHieght(_fontHandle, text, fontScale);
	}

	public byte[] textLinesToBytes(String text, float fontScale, int align) {
		return STBCall.drawTextLinesToBytes(_fontHandle, text, fontScale, align, _outDims);
	}

	public int[] textLinesToInt32(String text, float fontScale, LColor fontColor) {
		return textLinesToInt32(text, fontScale, 0, fontColor, LColor.transparent);
	}

	public int[] textLinesToInt32(String text, float fontScale, int align, LColor fontColor, LColor bgColor) {
		return STBCall.drawTextLinesToInt32(_fontHandle, text, fontScale, align, fontColor.getRed(),
				fontColor.getGreen(), fontColor.getBlue(), bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(),
				bgColor.getAlpha(), _outDims);
	}

	public RectI getOutSize() {
		_outSize.width = _outDims[0];
		_outSize.height = _outDims[1];
		return _outSize;
	}

	public RectI getCodepointBitmapBox(float fontsize, int point) {
		int[] rect = STBCall.getCodepointBitmapBox(_fontHandle, fontsize, point);
		_codepointBitmapBox.set(rect[0], rect[1], rect[2], rect[3]);
		return _codepointBitmapBox;
	}

	public VMetric getFontVMetrics(float fontsize) {
		int[] ms = STBCall.getFontVMetrics(_fontHandle, fontsize);
		_metric.ascent = ms[0];
		_metric.descent = ms[1];
		_metric.lineGap = ms[2];
		return _metric;
	}

	public RectI getStringSize(float fontsize, String text) {
		int[] ms = STBCall.getCharsSize(_fontHandle, fontsize, text);
		_textSize.width = ms[0];
		_textSize.height = ms[1];
		return _textSize;
	}

	public RectI getCharSize(float fontsize, int point) {
		int[] ms = STBCall.getCharSize(_fontHandle, fontsize, point);
		_charSize.width = ms[0];
		_charSize.height = ms[1];
		return _charSize;
	}

	public int getCodepointHMetrics(int point) {
		return STBCall.getCodepointHMetrics(_fontHandle, point);
	}

	public byte[] makeCodepointBytePixels(int point, float fontScale, int width, int height) {
		return STBCall.makeCodepointBitmap(_fontHandle, point, fontScale, width, height);
	}

	public int[] makeCodepointPixels32(int point, float fontScale, int width, int height, LColor fontColor) {
		return STBCall.makeCodepointBitmap32(_fontHandle, point, fontScale, width, height, fontColor.getRed(),
				fontColor.getGreen(), fontColor.getBlue());
	}

	public byte[] makeDrawTextBytePixels(int point, String text, float fontScale, int width, int height) {
		return STBCall.makeDrawTextToBitmap(_fontHandle, text, fontScale, width, height);
	}

	public int[] makeDrawTextPixels32(String text, float fontScale, int width, int height, LColor fontColor) {
		return STBCall.makeDrawTextToBitmap32(_fontHandle, text, fontScale, width, height, fontColor.getRed(),
				fontColor.getGreen(), fontColor.getBlue());
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
