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

import loon.font.BDFont;

public class BDFontAssetLoader extends AssetAbstractLoader<BDFont> {

	private BDFont _font;

	private int _fontSize;

	public BDFontAssetLoader(String path, String nickname, int fontSize) {
		set(path, nickname);
		_fontSize = fontSize;
	}

	@Override
	public boolean isLoaded() {
		return _font != null && !_font.isClosed();
	}

	@Override
	public boolean completed() {
		if (_fontSize <= 0) {
			_font = new BDFont(_path);
		} else {
			_font = new BDFont(_path, _fontSize);
		}
		return _font != null;
	}

	@Override
	public PreloadItem item() {
		return PreloadItem.BitmapDistributionFont;
	}

	@Override
	public BDFont get() {
		return _font;
	}

	@Override
	public void close() {
		if (_font != null) {
			_font.close();
			_font = null;
		}
	}

}
