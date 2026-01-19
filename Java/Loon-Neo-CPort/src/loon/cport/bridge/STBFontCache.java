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

import loon.utils.IntMap;

public final class STBFontCache {

	private final static IntMap<STBFont> _fontCache = new IntMap<STBFont>();

	private final static STBFont createCache(int hashCodeKey, String path) {
		STBFont font = _fontCache.get(hashCodeKey);
		if (font == null) {
			font = STBFont.create(path);
			_fontCache.put(hashCodeKey, font);
		}
		return font;
	}

	private final static STBFont createCache(int hashCodeKey, String path, String fontName, int style) {
		STBFont font = _fontCache.get(hashCodeKey);
		if (font == null) {
			font = STBFont.create(path, fontName, style);
			_fontCache.put(hashCodeKey, font);
		}
		return font;
	}

	private final static STBFont createSystemCache(int hashCodeKey, String styleName, String path, String defStyle,
			int style) {
		STBFont font = _fontCache.get(hashCodeKey);
		if (font == null) {
			font = STBFont.createSystemFont(styleName, path, defStyle, style);
			_fontCache.put(hashCodeKey, font);
		}
		return font;
	}

	public final static STBFont create(String path) {
		return createCache(path.hashCode(), path);
	}

	public final static STBFont create(String path, String fontName, int style) {
		final int hashCodeKey = (path + "|" + fontName + "|" + style).hashCode();
		return createCache(hashCodeKey, path, fontName, style);
	}

	public final static STBFont createSystemFont(String styleName, String path, String fontName, int style) {
		final int hashCodeKey = (styleName + "|" + path + "|" + fontName + "|" + style).hashCode();
		return createSystemCache(hashCodeKey, styleName, path, fontName, style);
	}

	public final static void close() {
		for (STBFont font : _fontCache) {
			if (font != null) {
				font.close();
			}
		}
		_fontCache.clear();
	}
}
