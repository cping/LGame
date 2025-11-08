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
import loon.utils.res.TextResource;

public class JsonAssetLoader extends AssetAbstractLoader<Json.Object> {

	private Json.Object _jsonObj;

	private Json.Array _jsonArray;

	private String _context;

	public JsonAssetLoader(String path, String nickname) {
		set(path, nickname);
	}

	@Override
	public boolean isLoaded() {
		return _jsonObj != null || _jsonArray != null;
	}

	@Override
	public void loadData() {
		close();
		_context = TextResource.get().loadText(_path);
		if (_context == null && _path.indexOf('.') == -1) {
			_context = (_path + ".json");
		}
		_context = TextResource.get().loadText(_context);
		if (_context == null) {
			return;
		}
		Object obj = BaseIO.loadJsonObjectContext(_context);
		if (obj instanceof Json.Object) {
			_jsonObj = (Json.Object) obj;
		} else {
			_jsonArray = (Json.Array) obj;
		}
	}

	@Override
	public boolean completed() {
		if (_context == null) {
			return false;
		}
		return _jsonObj != null || _jsonArray != null;
	}

	public Json.Array getArray() {
		return _jsonArray;
	}

	@Override
	public Json.Object get() {
		return _jsonObj;
	}

	@Override
	public PreloadItem item() {
		return PreloadItem.Json;
	}

	@Override
	public void close() {
		_context = null;
		_jsonObj = null;
		_jsonArray = null;
	}

}
