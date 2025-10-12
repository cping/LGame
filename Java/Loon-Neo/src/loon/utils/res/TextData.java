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
package loon.utils.res;

import loon.BaseIO;
import loon.LRelease;
import loon.utils.StringUtils;

public final class TextData implements LRelease {

	private String _path;

	private String _text;

	public TextData(String t) {
		this(null, t);
	}

	public TextData(String p, String t) {
		_path = p;
		_text = t;
	}

	public String getPath() {
		return _path;
	}

	public String getText() {
		if (!StringUtils.isNullOrEmpty(_path) && StringUtils.isNullOrEmpty(_text)) {
			_text = BaseIO.loadText(_path);
		}
		return _text;
	}

	@Override
	public void close() {
		_text = null;
	}

}
