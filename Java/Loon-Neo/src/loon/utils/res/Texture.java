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

import loon.LTexture;
import loon.LTextures;

public class Texture {

	private TextureData _texData = null;

	private LTexture _img = null;

	private String _path = null;

	public Texture(String path) {
		_path = path;
		_texData = new TextureData();
	}

	public Texture(LTexture tex, TextureData td) {
		_path = tex.getSource();
		_img = tex;
		_texData = td;
	}

	public TextureData getTextureData() {
		if (_img == null || _img.disposed()) {
			_img = LTextures.loadTexture(_path);
			_texData.w = _img.getWidth();
			_texData.h = _img.getHeight();
			_texData.sourceW = _texData.w;
			_texData.sourceH = _texData.h;
		}
		return _texData;
	}

	public LTexture img() {
		if (_img == null || _img.disposed()) {
			_img = LTextures.loadTexture(_path);
			_texData.w = _img.getWidth();
			_texData.h = _img.getHeight();
			_texData.sourceW = _texData.w;
			_texData.sourceH = _texData.h;
		}
		return _img;
	}

	public TextureData data() {
		return _texData;
	}

	public void close() {
		if (_img != null) {
			_img.close();
		}
	}

}
