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
package loon.utils.res.loaders;

import loon.BaseIO;
import loon.LTexture;

public class TextureAssetLoader extends AssetAbstractLoader<LTexture> {

	private LTexture _texture;

	public TextureAssetLoader(String path, String nickname) {
		this.set(path, nickname);
	}

	@Override
	public boolean completed() {
		_texture = BaseIO.loadTexture(_path);
		return _texture != null;
	}
	
	@Override
	public LTexture get() {
		return _texture;
	}

	@Override
	public PreloadItem item() {
		return PreloadItem.Texture;
	}

	@Override
	public void close() {
		if (_texture != null) {
			_texture.close();
			_texture = null;
		}
	}
}
