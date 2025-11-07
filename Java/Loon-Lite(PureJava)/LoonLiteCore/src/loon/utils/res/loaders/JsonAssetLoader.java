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
import loon.Json;
import loon.LSystem;

public class JsonAssetLoader extends AssetAbstractLoader<Json.Object> {

	private Json.Object _json;

	private String _context;

	public JsonAssetLoader(String path, String nickname) {
		set(path, nickname);
	}

	@Override
	public boolean isLoaded() {
		return _json != null;
	}

	@Override
	public void loadData() {
		close();
		_context = BaseIO.loadText(_path);
		if (_context == null && _path.indexOf('.') == -1) {
			_context = BaseIO.loadText(_path + ".json");
		}
		if (_context == null) {
			return;
		}
		_json = LSystem.base().json().parse(_context);
	}

	@Override
	public boolean completed() {
		if (_context == null) {
			return false;
		}
		return _json != null;
	}

	@Override
	public Json.Object get() {
		return _json;
	}

	@Override
	public PreloadItem item() {
		return PreloadItem.Json;
	}

	@Override
	public void close() {
		_context = null;
		_json = null;
	}

}
