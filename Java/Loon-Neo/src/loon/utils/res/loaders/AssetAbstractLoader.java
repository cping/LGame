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

import loon.LSysException;
import loon.utils.StringUtils;

public abstract class AssetAbstractLoader<T> implements AssetLoader {

	protected String _path;

	protected String _nickname;

	protected void set(String path, String nickname) {
		if (StringUtils.isEmpty(path)) {
			throw new LSysException("The path name cannot be empty !");
		}
		this._path = path;
		this._nickname = nickname;
	}

	public T sync() {
		if (!isLoaded()) {
			loadData();
		}
		return get();
	}

	public abstract boolean isLoaded();

	public abstract T get();

	@Override
	public boolean load() {
		boolean result = completed();
		if (!result) {
			loadData();
		}
		return completed();
	}

	public void unload() {
		close();
	}

	@Override
	public String getPath() {
		return _path;
	}

	@Override
	public String getNickName() {
		return _nickname;
	}

}
