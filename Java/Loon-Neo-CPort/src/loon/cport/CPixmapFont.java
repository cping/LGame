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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import loon.LRelease;
import loon.LSystem;
import loon.canvas.LColor;
import loon.canvas.Pixmap;
import loon.cport.bridge.STBFont;
import loon.cport.bridge.STBFont.FontData;
import loon.utils.StringUtils;

public class CPixmapFont implements LRelease {

	private static class CacheKey {
		private final String text;
		private final float fontScale;
		private final int colorARGB;

		CacheKey(String text, float fontScale, LColor color) {
			this.text = text;
			this.fontScale = fontScale;
			this.colorARGB = color.getARGB();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof CacheKey)) {
				return false;
			}
			CacheKey key = (CacheKey) o;
			return Float.compare(key.fontScale, fontScale) == 0 && colorARGB == key.colorARGB
					&& Objects.equals(text, key.text);
		}

		@Override
		public int hashCode() {
			int hashCode = 1;
			hashCode = LSystem.unite(hashCode, text);
			hashCode = LSystem.unite(hashCode, fontScale);
			hashCode = LSystem.unite(hashCode, colorARGB);
			return hashCode;
		}
	}

	private static final int MAX_CACHE_SIZE = LSystem.DEFAULT_MAX_CACHE_SIZE;

	private STBFont _stbFont;
	private boolean _hasAlpha;

	private Map<CacheKey, Pixmap> _fontCache = new LinkedHashMap<CacheKey, Pixmap>(16, 0.75f, true) {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(Map.Entry<CacheKey, Pixmap> v) {
			if (size() > MAX_CACHE_SIZE) {
				Pixmap pix = v.getValue();
				if (pix != null) {
					pix.close();
				}
				return true;
			}
			return false;
		}
	};

	public CPixmapFont(STBFont font) {
		this(font, false);
	}

	public CPixmapFont(STBFont font, boolean hasAlpha) {
		_stbFont = font;
		_hasAlpha = hasAlpha;
	}

	public Pixmap textToPixmap(String text, float fontScale, LColor color) {
		if (StringUtils.isEmpty(text)) {
			return null;
		}
		if (text.length() == 1) {
			return charToPixmap(text.charAt(0), fontScale, color);
		}
		CacheKey key = new CacheKey(text, fontScale, color);
		Pixmap pixmap = _fontCache.get(key);
		if (pixmap != null) {

			return pixmap;
		}
		FontData result = _stbFont.drawString(text, fontScale, color.getARGB());
		pixmap = new Pixmap(result.pixels, result.fontSize.width, result.fontSize.height, _hasAlpha);
		_fontCache.put(key, pixmap);
		return pixmap;
	}

	public Pixmap charToPixmap(int point, float fontScale, LColor color) {
		String text = String.valueOf((char) point);
		CacheKey key = new CacheKey(text, fontScale, color);
		Pixmap pixmap = _fontCache.get(key);
		if (pixmap != null) {
			return pixmap;
		}
		FontData result = _stbFont.drawChar(point, fontScale, color.getARGB());
		pixmap = new Pixmap(result.pixels, result.fontSize.width, result.fontSize.height, _hasAlpha);
		_fontCache.put(key, pixmap);
		return pixmap;
	}

	public boolean hasAlpha() {
		return _hasAlpha;
	}

	@Override
	public void close() {
		for (Pixmap pixmap : _fontCache.values()) {
			pixmap.close();
		}
		_fontCache.clear();
	}

}
