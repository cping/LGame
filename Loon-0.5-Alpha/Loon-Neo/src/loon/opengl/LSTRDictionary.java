/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.opengl;

import loon.LRelease;
import loon.LSystem;
import loon.canvas.LColor;
import loon.font.LFont;
import loon.utils.ObjectMap;
import loon.utils.TArray;

public final class LSTRDictionary {

	private final static ObjectMap<String, LFont> cacheList = new ObjectMap<String, LFont>(
			20);

	private final static ObjectMap<String, Dict> fontList = new ObjectMap<String, Dict>(
			20);

	private static ObjectMap<String, LSTRFont> lazyEnglish = new ObjectMap<String, LSTRFont>(
			10);

	public final static String added = "0123456789";

	public final static char split = '$';

	private static StringBuffer lazyKey;

	static class Dict implements LRelease {

		TArray<Character> dicts;

		LSTRFont font;

		static Dict newDict() {
			return new Dict();
		}

		Dict() {
			dicts = new TArray<Character>(512);
		}

		public boolean include(String mes) {
			final char[] chars = mes.toCharArray();
			int size = chars.length;
			for (int i = 0; i < size; i++) {
				char flag = chars[i];
				if (!dicts.contains(flag)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public void close() {
			if (font != null) {
				font.close();
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
					d.close();
					d = null;
				}
			}
			fontList.clear();
		}
	}

	private final static int size = LSystem.DEFAULT_MAX_CACHE_SIZE * 2;

	public final static Dict bind(final LFont font, final String mes) {
		final String message = mes + added;
		if (cacheList.size > size) {
			clearStringLazy();
		}
		synchronized (fontList) {
			LFont cFont = cacheList.get(message);
			String fontFlag = font.getFontName() + "_" + font.getStyle() + "_"
					+ font.getSize();
			Dict pDict = fontList.get(fontFlag);
			if ((cFont == null || pDict == null || (pDict != null && !pDict
					.include(mes)))) {
				if (pDict == null) {
					pDict = Dict.newDict();
					fontList.put(fontFlag, pDict);
				}
				synchronized (pDict) {
					cacheList.put(message, font);
					TArray<Character> charas = pDict.dicts;
					int oldSize = charas.size;
					char[] chars = message.toCharArray();
					int size = chars.length;
					for (int i = 0; i < size; i++) {
						char flag = chars[i];
						if (!charas.contains(flag)) {
							charas.add(flag);
						}
					}
					int newSize = charas.size;
					if (oldSize != newSize) {
						if (pDict.font != null) {
							pDict.font.close();
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

	public final static void drawString(GLEx gl, LFont font, String message,
			float x, float y, float angle, LColor c) {
		Dict pDict = bind(font, message);
		if (pDict.font != null) {
			synchronized (pDict.font) {
				pDict.font.drawString(gl, message, x, y, angle, c);
			}
		}
	}

	public final static void drawString(GLEx gl, LFont font, String message,
			float x, float y, float sx, float sy, float angle, LColor c) {
		Dict pDict = bind(font, message);
		if (pDict.font != null) {
			synchronized (pDict.font) {
				pDict.font.drawString(gl, message, x, y, sx, sy, angle, c);
			}
		}
	}

	public final static void drawString(GLEx gl, LFont font, String message,
			float x, float y, float sx, float sy, float ax, float ay,
			float angle, LColor c) {
		Dict pDict = bind(font, message);
		if (pDict.font != null) {
			synchronized (pDict.font) {
				pDict.font.drawString(gl, x, y, sx, sy, ax, ay, angle, message,
						c);
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
		hashCode = LSystem.unite(hashCode, font.getAscent());
		hashCode = LSystem.unite(hashCode, font.getLeading());
		hashCode = LSystem.unite(hashCode, font.getDescent());

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
					str.close();
					str = null;
				}
			}
		}
	}

	public static LSTRFont getGLFont(LFont f) {
		if (lazyEnglish.size > LSystem.DEFAULT_MAX_CACHE_SIZE) {
			clearEnglishLazy();
		}
		String key = makeLazyWestKey(f);
		LSTRFont font = lazyEnglish.get(key);
		if (font == null) {
			font = new LSTRFont(f);
			lazyEnglish.put(key, font);
		}
		return font;
	}

	public final static void dispose() {
		clearEnglishLazy();
		clearStringLazy();
	}

}
