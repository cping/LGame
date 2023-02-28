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
import loon.utils.StringUtils;
import loon.utils.cache.CacheMap;

public class BMFontCache extends CacheMap<BMFont> {

	private final static String DEF_BMFONT = "deffont";

	private static BMFontCache _fontCache = null;

	public static void freeStatic() {
		_fontCache = null;
	}

	public static BMFontCache shared() {
		if (_fontCache == null) {
			synchronized (BMFontCache.class) {
				if (_fontCache == null) {
					_fontCache = new BMFontCache();
				}
			}
		}
		return _fontCache;
	}

	@Override
	public BMFont get(String fntName) {
		String key = StringUtils.isEmpty(fntName) ? DEF_BMFONT : fntName;
		BMFont font = super.get(key);
		if (font == null || font.isClosed()) {
			font = create(key);
			remove(key);
			put(fntName, font);
		}
		return font;
	}

	@Override
	public BMFont create(String fntName) {
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