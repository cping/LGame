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

import loon.BaseIO;
import loon.Json;
import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;

public class FontSheet implements LRelease {

	private TextureAtlas _texAtlas = null;

	private boolean _closed = false;
	
	public LTexture sheet() {
		return _texAtlas.img();
	}

	public TextureData getCharData(char ch) {
		String str = String.valueOf(ch);
		return _texAtlas.getFrame(str);
	}

	protected FontSheet(String url) {
		Json.Object jsonObj = LSystem.base().json().parse(BaseIO.loadText(url));
		String imagePath = url;
		LTexture sheet = LTextures.loadTexture(imagePath);
		init(jsonObj, sheet);
	}

	protected FontSheet(Json.Object jsonObj, LTexture sheet) {
		init(jsonObj, sheet);
	}

	protected void init(Json.Object jsonObj, LTexture sheet) {
		_texAtlas = new TextureAtlas(sheet, jsonObj);
	}

	@Override
	public void close() {
		if (_texAtlas != null) {
			_texAtlas.close();
		}
		_closed = true;
	}

	public boolean isClosed() {
		return _closed;
	}
}
