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

public class BDFontCache extends CacheMap<BDFont> {

	private final static String DEF_PIXFONT = "pixfont";

	private static BDFontCache _fontCache = null;
	
	public static void freeStatic(){
		_fontCache = null;
	}
	
	public static BDFontCache shared() {
		if (_fontCache == null) {
			synchronized (BDFontCache.class) {
				if (_fontCache == null) {
					_fontCache = new BDFontCache();
				}
			}
		}
		return _fontCache;
	}

	@Override
	public BDFont get(String fntName) {
		String key = StringUtils.isEmpty(fntName) ? DEF_PIXFONT : fntName;
		BDFont font = super.get(key);
		if (font == null || font.isClosed()) {
			font = create(key);
			remove(key);
			put(fntName, font);
		}
		return font;
	}

	@Override
	public BDFont create(String fntName) {
		BDFont bitmapFont;
		String suffix = LSystem.getExtension(fntName);
		if (fntName.equals(DEF_PIXFONT)) {
			bitmapFont = new BDFont(
					LSystem.getSystemImagePath() + DEF_PIXFONT + (StringUtils.isEmpty(suffix) ? ".bdf" : LSystem.EMPTY),
					12);
		} else {
			bitmapFont = new BDFont(fntName + (StringUtils.isEmpty(suffix) ? ".bdf" : LSystem.EMPTY), 12);
		}
		return bitmapFont;
	}

	public static BDFont getDefaultBDFont() {
		return shared().get(DEF_PIXFONT);
	}

}
