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
package loon.cport;

import loon.LRelease;
import loon.canvas.LColor;
import loon.canvas.Pixmap;
import loon.cport.bridge.STBFont;
import loon.geom.RectI;

public class CPixmapFont implements LRelease {

	private Pixmap _fontPixmap;

	private STBFont _stbFont;

	private boolean _hasAlpha;

	public CPixmapFont(STBFont font) {
		this(font, true);
	}

	public CPixmapFont(STBFont font, boolean hasAlpha) {
		_stbFont = font;
		_hasAlpha = hasAlpha;
	}

	public Pixmap textToPixmap(String text, float fontScale, LColor color) {
		final int[] pixels = _stbFont.textLinesToInt32(text, fontScale, color);
		final RectI rect = _stbFont.getOutSize();
		if (_fontPixmap == null) {
			_fontPixmap = new Pixmap(pixels, rect.width, rect.height, _hasAlpha);
		} else {
			_fontPixmap.setData(pixels, rect.width, rect.height, _hasAlpha);
		}
		return _fontPixmap;
	}

	public Pixmap charToPixmap(int point, float fontScale) {
		RectI rect = _stbFont.getCharSize(fontScale, point);
		return charToPixmap(point, fontScale, rect.width + 1, rect.height + 1);
	}

	public Pixmap charToPixmap(int point, float fontScale, int width, int height) {
		final int[] pixels = _stbFont.makeCodepointPixels32(point, fontScale, width, height);
		if (_fontPixmap == null) {
			_fontPixmap = new Pixmap(pixels, width, height, _hasAlpha);
		} else {
			_fontPixmap.setData(pixels, width, height, _hasAlpha);
		}
		return _fontPixmap;
	}

	public boolean hasAlpha() {
		return _hasAlpha;
	}

	@Override
	public void close() {
		if (_fontPixmap != null) {
			_fontPixmap.close();
			_fontPixmap = null;
		}
	}
}
