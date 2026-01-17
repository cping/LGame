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
import loon.cport.bridge.STBFont.FontData;

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
		FontData result = _stbFont.drawString(text, fontScale, color.getARGB());
		if (_fontPixmap == null) {
			_fontPixmap = new Pixmap(result.pixels, result.fontSize.width, result.fontSize.height, _hasAlpha);
		} else {
			_fontPixmap.setData(result.pixels, result.fontSize.width, result.fontSize.height, _hasAlpha);
		}
		return _fontPixmap;
	}

	public Pixmap charToPixmap(int point, float fontScale, LColor color) {
		FontData result = _stbFont.drawChar(point, fontScale, color.getARGB());
		if (_fontPixmap == null) {
			_fontPixmap = new Pixmap(result.pixels, result.fontSize.width, result.fontSize.height, _hasAlpha);
		} else {
			_fontPixmap.setData(result.pixels, result.fontSize.width, result.fontSize.height, _hasAlpha);
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
