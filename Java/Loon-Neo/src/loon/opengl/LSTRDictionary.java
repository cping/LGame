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

import java.util.Iterator;

import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.canvas.Canvas;
import loon.canvas.LColor;
import loon.font.LFont;
import loon.utils.ArrayMap;
import loon.utils.CharArray;
import loon.utils.IntMap;
import loon.utils.StrBuilder;
import loon.utils.ArrayMap.Entry;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * 纹理字体缓存用字典类,用于派生与管理默认的LFont本地系统字体
 */
public final class LSTRDictionary implements LRelease {

	private static LSTRDictionary instance;

	public static void freeStatic() {
		if (instance != null) {
			instance.dispose();
		}
		instance = null;
	}

	public final static LSTRDictionary make() {
		return new LSTRDictionary();
	}

	public final static LSTRDictionary get() {
		synchronized (LSTRDictionary.class) {
			if (instance == null) {
				instance = make();
			}
			return instance;
		}
	}

	public final Canvas createFontCanvas(float w, float h) {
		final int cacheSize = _fontCanvasList.size;
		if (cacheSize > _CACHE_SIZE) {
			clearFontCanvasLazy();
		}
		int keyFlag = 1;
		keyFlag = LSystem.unite(keyFlag, w);
		keyFlag = LSystem.unite(keyFlag, h);
		Canvas canvas = _fontCanvasList.get(keyFlag);
		if (canvas == null || canvas.image == null || canvas.image.isClosed()) {
			canvas = LSystem.base().graphics().createCanvas(w, h);
			_fontCanvasList.put(keyFlag, canvas);
		}
		return canvas;
	}

	public LSTRDictionary setAsyn(boolean asyn) {
		this.tmp_asyn = asyn;
		return this;
	}

	public boolean isAsyn() {
		return this.tmp_asyn;
	}

	public boolean asyn() {
		return this.tmp_asyn;
	}

	private final int _CACHE_SIZE = (int) (LSystem.DEFAULT_MAX_CACHE_SIZE * 1.5f);
	// 每次渲染图像到纹理时，同时追加一些常用非中文标记上去，以避免LSTRFont反复重构纹理(有字符重复检测,用户使用中已有下列字符时则不会重复添加)
	private final static String ADDED = "0123456789iabfghkdocnpqrstumwvxyzljeJIABFGHKCDOMNPQSTUVWXYZLRE=:.,!?@#$&%^*+(-){~}[│]<>\"'─\\/～▼▲◆【】：，。…？！";

	private final static char[] _checkMessage = ADDED.toCharArray();

	private final static StrBuilder _lazyKey = new StrBuilder(1024);

	private final static char split = '$';

	private final CharArray _templateChars = new CharArray(256);

	private final IntMap<CharSequence[]> _messageTemps = new IntMap<CharSequence[]>();

	private final IntMap<Canvas> _fontCanvasList = new IntMap<Canvas>();

	private boolean tmp_asyn = true;

	private boolean _allowCacheBind = true;

	private final IntMap<Dict> _fontList = new IntMap<Dict>(_CACHE_SIZE);

	private final ArrayMap _cacheList = new ArrayMap(_CACHE_SIZE);

	private final ArrayMap _englishFontList = new ArrayMap(_CACHE_SIZE);

	private Dict _lastDict;

	public final static boolean isAllInBaseCharsPool(String c) {
		if (StringUtils.isEmpty(c)) {
			return false;
		}
		final int charsCount = c.length();
		int idx = 0;
		for (int j = 0; j < charsCount; j++) {
			for (int i = 0; i < ADDED.length(); i++) {
				final char ch = c.charAt(j);
				if (ADDED.charAt(i) == ch || ch == LSystem.SPACE) {
					idx++;
					break;
				}
			}
		}
		return idx == charsCount;
	}

	public static class Dict implements LRelease {

		protected LSTRFont font;

		public static Dict newDict(LSTRFont font) {
			return new Dict(font);
		}

		public static Dict newDict() {
			return newDict(null);
		}

		private Dict(LSTRFont font) {
			this.font = font;
		}

		public LTexture getTexture() {
			if (font != null) {
				return font.getTexture();
			}
			return null;
		}

		public CharArray getCharArray() {
			if (font == null) {
				return new CharArray();
			}
			return font.getCharArray();
		}

		public LSTRFont getSTR() {
			return font;
		}

		public boolean equals(LFont f) {
			if (f == null) {
				return false;
			}
			if (font != null && (f == font.getFont() || f.equals(font.getFont()))) {
				return true;
			}
			return false;
		}

		public boolean include(String mes) {
			if (font == null) {
				return false;
			}
			final int size = mes.length();
			for (int i = 0; i < size; i++) {
				final char flag = mes.charAt(i);
				if (!font.containsChar(flag) && !StringUtils.isWhitespace(flag)) {
					return false;
				}
			}
			return true;
		}

		public boolean isUpdateing() {
			return font == null ? true : font.isUpdateing();
		}

		public boolean isClosed() {
			return font == null ? true : font.isClosed();
		}

		@Override
		public void close() {
			if (font != null) {
				font.close();
				font = null;
			}
		}

		@Override
		public String toString() {
			return font == null ? LSystem.EMPTY : font.getText();
		}

	}

	private void closeDict(IntMap<Dict> list) {
		synchronized (list) {
			for (Iterator<Dict> it = list.iterator(); it.hasNext();) {
				Dict dict = it.next();
				if (dict != null) {
					dict.close();
					dict = null;
				}
			}
			list.clear();
		}
	}

	private void closeDict(ArrayMap list) {
		synchronized (list) {
			for (int i = list.size() - 1; i > -1; i--) {
				final Entry entry = list.getEntry(i);
				if (entry != null) {
					Dict dict = (Dict) entry.getValue();
					if (dict != null) {
						dict.close();
						dict = null;
					}
				}
			}
			list.clear();
		}
	}

	private static final int toFontStringHash(LFont font) {
		int hashCode = 132;
		hashCode = LSystem.unite(hashCode, font.getFontName());
		hashCode = LSystem.unite(hashCode, LSystem.UNDERLINE);
		hashCode = LSystem.unite(hashCode, font.getStyle());
		hashCode = LSystem.unite(hashCode, LSystem.UNDERLINE);
		hashCode = LSystem.unite(hashCode, font.getSize());
		return hashCode;
	}

	public LFont searchCacheFont(String mes) {
		LFont cFont = (LFont) _cacheList.get(mes);
		if (cFont == null) {
			for (int i = _cacheList.size() - 1; i > -1; i--) {
				final Entry obj = _cacheList.getEntry(i);
				if (obj != null) {
					final String key = (String) obj.getKey();
					if (checkMessage(key, mes)) {
						cFont = (LFont) obj.getValue();
						break;
					}
				}
			}
		}
		return cFont;
	}

	public Dict searchCacheDict(LFont font) {
		if (font != null) {
			final int fontFlag = toFontStringHash(font);
			final Dict pDict = _fontList.get(fontFlag);
			if (pDict != null) {
				return pDict;
			}
		}
		return null;
	}

	public Dict searchCacheDict(LFont font, String mes) {
		if (font != null) {
			final int fontFlag = toFontStringHash(font);
			final Dict pDict = _fontList.get(fontFlag);
			if (pDict != null && pDict.include(mes)) {
				return pDict;
			}
		}
		return null;
	}

	public Dict searchCacheDict(String mes) {
		final LFont cFont = searchCacheFont(mes);
		if (cFont != null) {
			return searchCacheDict(cFont, mes);
		}
		return null;
	}

	public void clearEnglishLazy() {
		if (_englishFontList.size() == 0) {
			return;
		}
		closeDict(_englishFontList);
	}

	public void clearStringLazy() {
		if (_cacheList == null) {
			return;
		}
		synchronized (_cacheList) {
			if (_cacheList != null) {
				_cacheList.clear();
			}
		}
		if (_fontList.size() == 0) {
			return;
		}
		closeDict(_fontList);
	}

	public void clearFontCanvasLazy() {
		if (_fontCanvasList.size == 0) {
			return;
		}
		for (Canvas canvas : _fontCanvasList.values()) {
			if (canvas != null) {
				if (canvas.image != null) {
					canvas.image.close();
				}
				canvas.close();
				canvas = null;
			}
		}
		_fontCanvasList.clear();
	}

	public final boolean checkMessage(String key, String message) {
		final int size = message.length();
		final int limit = key.length();
		int idx = 0;
		for (int j = 0; j < limit; j++) {
			final char name = key.charAt(j);
			for (int i = 0; i < size; i++) {
				char flag = message.charAt(i);
				if (flag == name || StringUtils.isWhitespace(flag)) {
					idx++;
				}
				if (idx >= limit) {
					return true;
				}
			}
			if (idx >= limit) {
				return true;
			}
		}
		return false;
	}

	private boolean checkEnglishString(String mes) {
		final int len = mes.length();
		final int tempLength = _checkMessage.length;
		int count = 0;
		for (int n = 0; n < len; n++) {
			if (count >= len) {
				return true;
			}
			final int src = mes.charAt(n);
			for (int i = 0; i < tempLength; i++) {
				final int dst = _checkMessage[i];
				if (src == dst || StringUtils.isWhitespace(src)) {
					count++;
					break;
				}
			}
		}
		return count == len;
	}

	public final Dict bind(final LFont font, final TArray<? extends CharSequence> chars) {
		if (_messageTemps.size > _CACHE_SIZE) {
			_messageTemps.clear();
		}
		final int length = chars.size;
		CharSequence[] buffers = _messageTemps.get(length);
		if (buffers == null) {
			_messageTemps.put(length, (buffers = new CharSequence[length]));
		}
		for (int i = 0, size = length; i < size; i++) {
			buffers[i] = chars.get(i);
		}
		return bind(font, StringUtils.unificationCharSequence(_templateChars, buffers, ADDED), false);
	}

	public final Dict bind(final LFont font, final String[] messages) {
		return bind(font, StringUtils.unificationStrings(_templateChars, messages, ADDED), false);
	}

	public final Dict bind(final LFont font, final String mes) {
		return bind(font, mes, true);
	}

	public final Dict bind(final LFont font, final String mes, final boolean autoStringFilter) {
		if (!_allowCacheBind) {
			return null;
		}
		if (StringUtils.isEmpty(mes)) {
			return null;
		}
		if (_lastDict != null && !_lastDict.isUpdateing() && _lastDict.equals(font) && _lastDict.include(mes)) {
			return _lastDict;
		}
		if (checkEnglishString(mes)) {
			Dict pDict = (Dict) _englishFontList.get(font);
			if (pDict != null && pDict.isClosed()) {
				_englishFontList.remove(font);
				pDict = null;
			}
			if (pDict == null) {
				pDict = Dict.newDict(new LSTRFont(font, ADDED, tmp_asyn));
				_englishFontList.put(font, pDict);
			}
			return (_lastDict = pDict);
		}
		final String message;
		if (autoStringFilter) {
			message = StringUtils.unificationStrings(_templateChars, mes, ADDED) + ADDED;
		} else {
			message = mes + ADDED;
		}
		// 查询字典缓存
		Dict cacheDict = searchCacheDict(font, message);

		if (cacheDict != null && !cacheDict.isClosed()) {
			return _lastDict = cacheDict;
		}

		if (_cacheList.size() > _CACHE_SIZE) {
			clearStringLazy();
		}
		// 查询字体缓存
		final LFont cFont = searchCacheFont(message);
		final int fontFlag = toFontStringHash(font);
		Dict pDict = _fontList.get(fontFlag);
		if (pDict != null && pDict.isClosed()) {
			_fontList.remove(fontFlag);
			pDict = null;
		}
		// 判定当前font与字体和已存在的文字图片纹理，是否和缓存的font适配
		if ((cFont == null || pDict == null || (pDict != null && !pDict.include(mes)))) {
			if (pDict == null) {
				pDict = Dict.newDict();
				_fontList.put(fontFlag, pDict);
			}
			synchronized (pDict) {
				_cacheList.put(message, font);
				final CharArray charas = pDict.getCharArray();
				final int oldSize = charas.length;
				final int size = message.length();
				for (int i = 0; i < size; i++) {
					final char flag = message.charAt(i);
					if (!charas.contains(flag) && !StringUtils.isWhitespace(flag)) {
						charas.add(flag);
					}
				}
				final int newSize = charas.length;
				// 如果旧有大小，不等于新的纹理字符大小，重新扩展LSTRFont纹理字符
				if (oldSize != newSize) {
					/*
					 * if (pDict.font != null) { pDict.font.close(); pDict.font = null; }
					 */
					// 个别浏览器纹理同步会卡出国，只能异步……
					if (pDict.font == null) {
						pDict.font = new LSTRFont(font, charas.getThisArray(), tmp_asyn);
					} else {
						pDict.font.updateTexture(charas.getThisArray(), tmp_asyn);
					}
				}
			}
		}
		if (pDict == null || pDict.isClosed()) {
			return (_lastDict = null);
		}
		return (_lastDict = pDict);
	}

	public final LSTRDictionary unbind(final LFont font) {
		final int fontFlag = toFontStringHash(font);
		Dict cDict = _fontList.remove(fontFlag);
		if (cDict != null) {
			cDict.close();
			cDict = null;
		}
		Dict eDict = (Dict) _englishFontList.remove(font);
		if (eDict != null) {
			eDict.close();
			eDict = null;
		}
		return this;
	}

	public final void drawString(LFont font, String message, float x, float y, float angle, LColor c) {
		final Dict pDict = bind(font, message);
		if (pDict == null) {
			return;
		}
		if (pDict.font != null && !pDict.font.isClosed()) {
			synchronized (pDict.font) {
				pDict.font.drawString(message, x, y, angle, c);
			}
		}
	}

	public final void drawString(LFont font, String message, float x, float y, float sx, float sy, float ax, float ay,
			float angle, LColor c) {
		final Dict pDict = bind(font, message);
		if (pDict == null) {
			return;
		}
		if (pDict.font != null && !pDict.font.isClosed()) {
			synchronized (pDict.font) {
				pDict.font.drawString(message, x, y, sx, sy, ax, ay, angle, c);
			}
		}
	}

	public final void drawString(GLEx gl, LFont font, String message, float x, float y, float angle, LColor c) {
		final Dict pDict = bind(font, message);
		if (pDict == null) {
			return;
		}
		if (pDict.font != null && !pDict.font.isClosed()) {
			synchronized (pDict.font) {
				pDict.font.drawString(gl, message, x, y, angle, c);
			}
		}
	}

	public final void drawString(GLEx gl, LFont font, String message, float x, float y, float sx, float sy, float angle,
			LColor c) {
		final Dict pDict = bind(font, message);
		if (pDict == null) {
			return;
		}
		if (pDict.font != null && !pDict.font.isClosed()) {
			synchronized (pDict.font) {
				pDict.font.drawString(gl, message, x, y, sx, sy, angle, c);
			}
		}
	}

	public final void drawString(GLEx gl, LFont font, String message, float x, float y, float sx, float sy, float ax,
			float ay, float angle, LColor c) {
		final Dict pDict = bind(font, message);
		if (pDict == null) {
			return;
		}
		if (pDict.font != null && !pDict.font.isClosed()) {
			synchronized (pDict.font) {
				pDict.font.drawString(gl, x, y, sx, sy, ax, ay, angle, message, c);
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
	public final static String makeStringLazyKey(final LFont font, final String text) {
		int hashCode = 0;
		hashCode = LSystem.unite(hashCode, font.getSize());
		hashCode = LSystem.unite(hashCode, font.getStyle());
		hashCode = LSystem.unite(hashCode, font.getAscent());
		hashCode = LSystem.unite(hashCode, font.getLeading());
		hashCode = LSystem.unite(hashCode, font.getDescent());

		_lazyKey.setLength(0);
		_lazyKey.append(font.getFontName().toLowerCase());
		_lazyKey.append(hashCode);
		_lazyKey.append(split);
		_lazyKey.append(text);

		return _lazyKey.toString();
	}

	public final LSTRFont STRFont(LFont font) {
		if (_fontList != null) {
			for (Iterator<Dict> it = _fontList.iterator(); it.hasNext();) {
				Dict dict = it.next();
				if (dict != null && dict.font != null && dict.font.getFont().equals(font)) {
					return dict.font;
				}
			}
		}
		return null;
	}

	public final static String getAddedString() {
		return ADDED;
	}

	public boolean isAllowCache() {
		return _allowCacheBind;
	}

	public LSTRDictionary setAllowCache(boolean a) {
		this._allowCacheBind = a;
		return this;
	}

	public final void dispose() {
		try {
			_cacheList.clear();
			_messageTemps.clear();
			clearStringLazy();
			clearEnglishLazy();
			clearFontCanvasLazy();
		} catch (Exception ex) {
		}
	}

	@Override
	public void close() {
		dispose();
	}

}
