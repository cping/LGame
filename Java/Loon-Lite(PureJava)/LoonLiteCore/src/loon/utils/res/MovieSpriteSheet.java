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
package loon.utils.res;

import loon.Json;
import loon.LRelease;
import loon.LTexture;
import loon.utils.ListMap;
import loon.utils.TArray;

public class MovieSpriteSheet implements LRelease {

	protected TextureData[] _datas = null;

	private TextureAtlas _ta = null;

	private boolean _close = false;

	protected MovieSpriteSheet(TextureAtlas ta, String[] frameNames) {
		init(ta, frameNames);
	}

	protected MovieSpriteSheet(Json.Object jsonObj, String[] frameNames, LTexture sheet) {
		TextureAtlas ta = new TextureAtlas(sheet, jsonObj);
		init(ta, frameNames);
	}

	protected MovieSpriteSheet(Json.Object jsonObj, LTexture sheet) {
		TextureAtlas ta = new TextureAtlas(sheet, jsonObj);
		init(ta, ta._names);
	}

	public TextureAtlas textureAtlas() {
		return _ta;
	}

	public LTexture sheet() {
		return _ta.img();
	}

	public TextureData[] datas() {
		return _datas;
	}

	public TextureData getSSD(String name) {
		return _ta.getFrame(name);
	}

	protected void init(TextureAtlas ta, TArray<String> frameNames) {
		_ta = ta;
		_datas = new TextureData[frameNames.size];
		for (int i = 0; i < frameNames.size; i++) {
			_datas[i] = _ta.getFrame(frameNames.get(i));
		}
	}

	protected void init(TextureAtlas ta, String[] frameNames) {
		_ta = ta;
		_datas = new TextureData[frameNames.length];
		for (int i = 0; i < frameNames.length; i++) {
			_datas[i] = _ta.getFrame(frameNames[i]);
		}
	}

	public LTexture[] getTextures() {
		LTexture tex = _ta.img();
		int size = _datas.length;
		LTexture[] texList = new LTexture[size];
		for (int i = 0; i < size; i++) {
			TextureData data = _datas[i];
			texList[i] = tex.copy(data._x, data._y, data._w, data._h);
		}
		return texList;
	}

	public Texture getTexture(String name) {
		return _ta.getTexture(name);
	}

	public MovieSpriteSheet getSpriteSheet(String prefix) {
		TArray<TextureData> list = new TArray<TextureData>();
		for (int i = 0; i < _datas.length; i++) {
			TextureData td = _datas[i];
			if (td._name.startsWith(prefix)) {
				list.add(td);
			}
		}
		ListMap<String, TextureData> frames = new ListMap<String, TextureData>(list.size);
		String[] frameNames = new String[list.size];
		for (int i = 0; i < frameNames.length; i++) {
			TextureData td = list.get(i);
			frameNames[i] = td._name;
			frames.put(td._name, td);
		}
		TextureAtlas ta = new TextureAtlas(_ta.img(), frames);
		MovieSpriteSheet ss = new MovieSpriteSheet(ta, frameNames);
		return ss;
	}

	@Override
	public void close() {
		if (_ta != null) {
			_ta.close();
		}
		_close = true;
	}

	public boolean isClosed() {
		return _close;
	}
}
