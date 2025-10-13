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

public final class BDFontCache extends CacheMap<BDFont> {

	private final static String DEF_PIXFONT = "pixfont";

	private static BDFontCache _fontCache = null;

	private BDFontCache() {
	}

	public static void freeStatic() {
		_fontCache = null;
	}

	public static BDFontCache shared() {
		synchronized (BDFontCache.class) {
			if (_fontCache == null) {
				_fontCache = new BDFontCache();
			}
			return _fontCache;
		}
	}

	private Created<BDFont> _createBDFont;

	public BDFontCache setCreateMethod(Created<BDFont> c) {
		_createBDFont = c;
		return this;
	}

	public Created<BDFont> getCreateBDMethod() {
		return _createBDFont;
	}

	public BDFont get(String fntName, Created<BDFont> newFont) {
		String key = StringUtils.isEmpty(fntName) ? DEF_PIXFONT : fntName;
		BDFont font = super.exist(key);
		if (font == null || font.isClosed()) {
			if (newFont != null) {
				BDFont lazyFont = newFont.make();
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
	public BDFont get(String fntName) {
		return get(fntName, null);
	}

	@Override
	public BDFont create(String fntName) {
		if (_createBDFont != null) {
			return _createBDFont.make();
		}
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
