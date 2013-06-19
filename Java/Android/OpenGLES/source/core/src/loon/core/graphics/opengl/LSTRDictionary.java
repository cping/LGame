package loon.core.graphics.opengl;

import java.util.ArrayList;
import java.util.HashMap;

import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;

/**
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
public final class LSTRDictionary {

	private final static HashMap<String, LFont> cacheList = new HashMap<String, LFont>(
			20);

	private final static HashMap<LFont, Dict> fontList = new HashMap<LFont, Dict>(
			20);

	private static HashMap<String, LSTRFont> lazyEnglish = new HashMap<String, LSTRFont>(
			10);

	public final static String added = "0123456789";

	public final static char split = '$';

	private static StringBuffer lazyKey;

	static class Dict implements LRelease {

		ArrayList<Character> dicts;

		LSTRFont font;

		static Dict newDict() {
			return new Dict();
		}

		Dict() {
			dicts = new ArrayList<Character>(512);
		}

		@Override
		public void dispose() {
			if (font != null) {
				font.dispose();
				font = null;
			}
			if (dicts != null) {
				dicts.clear();
				dicts = null;
			}
		}

	}

	public static void clearStringLazy() {
		synchronized (cacheList) {
			if (cacheList != null) {
				cacheList.clear();
			}
		}
		synchronized (fontList) {
			for (Dict d : fontList.values()) {
				if (d != null) {
					d.dispose();
					d = null;
				}
			}
			fontList.clear();
		}
	}
	
	private final static int size = LSystem.DEFAULT_MAX_CACHE_SIZE * 5;

	public final static Dict bind(final LFont font, final String mes) {
		final String message = mes + added;
		if (cacheList.size() > size) {
			clearStringLazy();
		}
		synchronized (fontList) {
			LFont cFont = cacheList.get(message);
			Dict pDict = fontList.get(font);
			if (cFont == null || pDict == null) {
				if (pDict == null) {
					pDict = Dict.newDict();
					fontList.put(font, pDict);
				}
				synchronized (pDict) {
					cacheList.put(message, font);
					ArrayList<Character> charas = pDict.dicts;
					int oldSize = charas.size();
					char[] chars = message.toCharArray();
					for (int i = 0; i < chars.length; i++) {
						if (!charas.contains(chars[i])) {
							charas.add(chars[i]);
						}
					}
					int newSize = charas.size();
					if (oldSize != newSize) {
						if (pDict.font != null) {
							pDict.font.dispose();
							pDict.font = null;
						}
						StringBuffer sbr = new StringBuffer(newSize);
						for (int i = 0; i < newSize; i++) {
							sbr.append(charas.get(i));
						}
						pDict.font = new LSTRFont(font, sbr.toString());
					}
				}
			}
			return pDict;
		}
	}

	public final static void drawString(LFont font, String message, float x,
			float y, float angle, LColor c) {
		Dict pDict = bind(font, message);
		if (pDict.font != null) {
			synchronized (pDict.font) {
				pDict.font.drawString(message, x, y, angle, c);
			}
		}
	}

	public final static void drawString(LFont font, String message, float x,
			float y, float sx, float sy, float ax, float ay, float angle,
			LColor c) {
		Dict pDict = bind(font, message);
		if (pDict.font != null) {
			synchronized (pDict.font) {
				pDict.font.drawString(message, x, y, sx, sy, ax, ay, angle, c);
			}
		}
	}

	/**
	 * 生成特定字符串的缓存用ID
	 * 
	 * @param font
	 * @param text
	 * @return
	 */
	public static String makeStringLazyKey(final LFont font, final String text) {
		int hashCode = 0;
		hashCode = LSystem.unite(hashCode, font.getSize());
		hashCode = LSystem.unite(hashCode, font.getStyle());
		if (lazyKey == null) {
			lazyKey = new StringBuffer();
			lazyKey.append(font.getFontName().toLowerCase());
			lazyKey.append(hashCode);
			lazyKey.append(split);
			lazyKey.append(text);
		} else {
			lazyKey.delete(0, lazyKey.length());
			lazyKey.append(font.getFontName().toLowerCase());
			lazyKey.append(hashCode);
			lazyKey.append(split);
			lazyKey.append(text);
		}
		return lazyKey.toString();
	}

	/**
	 * 生成一组西方字符缓存键值
	 * 
	 * @param font
	 * @return
	 */
	private static String makeLazyWestKey(LFont font) {
		if (lazyKey == null) {
			lazyKey = new StringBuffer();
			lazyKey.append(font.getFontName().toLowerCase());
			lazyKey.append(font.getStyle());
			lazyKey.append(font.getSize());
		} else {
			lazyKey.delete(0, lazyKey.length());
			lazyKey.append(font.getFontName().toLowerCase());
			lazyKey.append(font.getStyle());
			lazyKey.append(font.getSize());
		}
		return lazyKey.toString();
	}

	/**
	 * 清空西文字体缓存
	 */
	public static void clearEnglishLazy() {
		synchronized (lazyEnglish) {
			for (LSTRFont str : lazyEnglish.values()) {
				if (str != null) {
					str.dispose();
					str = null;
				}
			}
		}
	}

	public static LSTRFont getGLFont(LFont f) {
		if (lazyEnglish.size() > LSystem.DEFAULT_MAX_CACHE_SIZE) {
			clearEnglishLazy();
		}
		String key = makeLazyWestKey(f);
		LSTRFont font = lazyEnglish.get(key);
		if (font == null) {
			font = new LSTRFont(f, true);
			lazyEnglish.put(key, font);
		}
		return font;
	}

	public final static void dispose() {
		clearEnglishLazy();
		clearStringLazy();
	}

}
