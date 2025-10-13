/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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

import loon.LSystem;
import loon.events.Created;
import loon.utils.StringUtils;
import loon.utils.cache.CacheMap;

public final class BMFontCache extends CacheMap<BMFont> {

	private final static String DEF_BMFONT = "deffont";

	private static BMFontCache _fontCache = null;

	private BMFontCache() {
	}

	public static void freeStatic() {
		_fontCache = null;
	}

	public static BMFontCache shared() {
		synchronized (BMFontCache.class) {
			if (_fontCache == null) {
				_fontCache = new BMFontCache();
			}
			return _fontCache;
		}
	}

	private Created<BMFont> _createBMFont;

	public BMFontCache setCreateMethod(Created<BMFont> c) {
		_createBMFont = c;
		return this;
	}

	public Created<BMFont> getCreateBMMethod() {
		return _createBMFont;
	}

	public BMFont get(String fntName, Created<BMFont> newFont) {
		String key = StringUtils.isEmpty(fntName) ? DEF_BMFONT : fntName;
		BMFont font = super.exist(key);
		if (font == null || font.isClosed()) {
			if (newFont != null) {
				BMFont lazyFont = newFont.make();
				if (lazyFont != null && !lazyFont.isClosed()) {
					font = lazyFont;
				} else {
					font = create(fntName);
				}
			} else {
				font = create(fntName);
			}
			put(key, font);
		}
		return font;
	}

	@Override
	public BMFont get(String fntName) {
		return get(fntName, null);
	}

	@Override
	public BMFont create(String fntName) {
		if (_createBMFont != null) {
			_createBMFont.make();
		}
		BMFont bitmapFont;
		String suffix = LSystem.getExtension(fntName);
		if (fntName.equals(DEF_BMFONT)) {
			fntName = LSystem.getSystemImagePath() + DEF_BMFONT;
			bitmapFont = new BMFont(fntName + (StringUtils.isEmpty(suffix) ? ".txt" : LSystem.EMPTY), fntName + ".png");
		} else {
			bitmapFont = new BMFont(fntName + (StringUtils.isEmpty(suffix) ? ".fnt" : LSystem.EMPTY),
					LSystem.getNotExtension(fntName) + ".png");
		}
		return bitmapFont;
	}

	public static BMFont getDefaultFont() {
		return shared().get(DEF_BMFONT);
	}

}