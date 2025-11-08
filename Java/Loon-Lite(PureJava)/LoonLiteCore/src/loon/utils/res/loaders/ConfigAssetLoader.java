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

import loon.utils.ConfigReader;

public class ConfigAssetLoader extends AssetAbstractLoader<ConfigReader> {

	private ConfigReader _config;

	public ConfigAssetLoader(String path, String nickname) {
		set(path, nickname);
	}

	@Override
	public boolean isLoaded() {
		return _config != null && !_config.isClosed();
	}

	@Override
	public void loadData() {
		close();
		_config = ConfigReader.shared(_path);
	}

	@Override
	public boolean completed() {
		return _config != null;
	}

	@Override
	public PreloadItem item() {
		return PreloadItem.Config;
	}

	@Override
	public ConfigReader get() {
		return _config;
	}

	@Override
	public void close() {
		if (_config != null) {
			_config.close();
			_config = null;
		}

	}

}
