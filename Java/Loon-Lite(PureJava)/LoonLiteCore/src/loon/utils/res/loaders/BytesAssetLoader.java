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
import loon.utils.ArrayByte;

public class BytesAssetLoader extends AssetAbstractLoader<ArrayByte> {

	private ArrayByte _bytes;

	public BytesAssetLoader(String path, String nickname) {
		set(path, nickname);
	}

	@Override
	public ArrayByte get() {
		return _bytes;
	}

	@Override
	public boolean completed() {
		return (_bytes = BaseIO.loadArrayByte(_path)) != null;
	}

	@Override
	public PreloadItem item() {
		return PreloadItem.Bytes;
	}

	@Override
	public void close() {
		if (_bytes != null) {
			_bytes.close();
			_bytes = null;
		}
	}

}
