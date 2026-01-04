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
import loon.geom.RectI;

public final class STBFont implements LRelease {

	public final static class VMetric {

		public int ascent;

		public int descent;

		public int lineGap;

	}

	public final static STBFont create(String path) {
		long handle = STBCall.loadFontInfo(path);
		return new STBFont(handle);
	}

	public final static STBFont create(String path, String styleName, int style) {
		long handle = STBCall.loadFontStyleInfo(path, styleName, style);
		return new STBFont(handle);
	}

	private boolean _closed;

	private long _fontHandle;

	private final RectI _codepointBitmapBox = new RectI();

	private final VMetric _metric = new VMetric();

	private STBFont(long handle) {
		_fontHandle = handle;
	}

	public RectI getCodepointBitmapBox(float fontsize, int point) {
		int[] rect = STBCall.getCodepointBitmapBox(_fontHandle, fontsize, point);
		_codepointBitmapBox.set(rect[0], rect[1], rect[2], rect[3]);
		rect = null;
		return _codepointBitmapBox;
	}

	public VMetric getFontVMetrics(float fontsize) {
		int[] ms = STBCall.getFontVMetrics(_fontHandle, fontsize);
		_metric.ascent = ms[0];
		_metric.descent = ms[1];
		_metric.lineGap = ms[2];
		ms = null;
		return _metric;
	}

	public int getCodepointHMetrics(int point) {
		return STBCall.getCodepointHMetrics(_fontHandle, point);
	}

	public byte[] makeCodepointBytePixels(int point, float fontScale, int width, int height) {
		return STBCall.makeCodepointBitmap(_fontHandle, point, fontScale, width, height);
	}

	public int[] makeCodepointPixels32(int point, float fontScale, int width, int height) {
		return STBCall.makeCodepointBitmap32(_fontHandle, point, fontScale, width, height);
	}

	public byte[] makeDrawTextBytePixels(int point, String text, float fontScale, int width, int height) {
		return STBCall.makeDrawTextToBitmap(_fontHandle, text, fontScale, width, height);
	}

	public int[] makeDrawTextPixels32(String text, float fontScale, int width, int height) {
		return STBCall.makeDrawTextToBitmap32(_fontHandle, text, fontScale, width, height);
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
