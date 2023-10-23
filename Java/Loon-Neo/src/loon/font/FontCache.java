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
package loon.font;

import loon.LRelease;
import loon.opengl.LSTRFont;
import loon.utils.ObjectMap;

/**
 * 不分具体实现的IFont存储器,所有IFont实现都可以使用此类
 */
public class FontCache implements LRelease {

	private ObjectMap<String, IFont> _cacheFonts;

	public FontCache() {
		this._cacheFonts = new ObjectMap<String, IFont>();
	}

	public FontCache putBMFont(String name, String path) {
		if (!_cacheFonts.containsKey(name)) {
			BMFont cache = new BMFont(path);
			this._cacheFonts.put(name, cache);
		}
		return this;
	}

	public FontCache putBDFont(String name, String path, float size) {
		if (!_cacheFonts.containsKey(name)) {
			BDFont cache = new BDFont(path, size);
			this._cacheFonts.put(name, cache);
		}
		return this;
	}

	public FontCache putStrFont(String name, String fontName, int size, String message) {
		return putStrFont(fontName, LFont.getFont(fontName, size), message);
	}

	public FontCache putStrFont(String name, LFont font, String message) {
		if (!_cacheFonts.containsKey(name)) {
			LSTRFont cache = new LSTRFont(font, message);
			this._cacheFonts.put(name, cache);
		}
		return this;
	}

	public FontCache putFont(String name, IFont font) {
		this._cacheFonts.put(name, font);
		return this;
	}

	public IFont getFont(String name) {
		return _cacheFonts.get(name);
	}

	public IFont removeFont(String name) {
		return _cacheFonts.remove(name);
	}

	public FontCache clear() {
		_cacheFonts.clear();
		return this;
	}

	@Override
	public void close() {
		_cacheFonts.close();
	}

}
