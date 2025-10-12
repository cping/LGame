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
import loon.LSystem;
import loon.utils.ObjectMap;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.parse.StrTokenizer;

public final class TextResource implements LRelease {

	private static TextResource _instance;

	public static void freeStatic() {
		_instance = null;
	}

	public static final TextResource get() {
		synchronized (TextResource.class) {
			if (_instance == null) {
				_instance = new TextResource();
			}
			return _instance;
		}
	}

	private final ObjectMap<String, TextData> _resource = new ObjectMap<String, TextData>();

	private boolean _dirty;

	private int _byteCount = 0;

	private TextResource() {
	}

	public TextData loadLazyData(String path, boolean lazy) {
		if (_resource.size > LSystem.DEFAULT_MAX_CACHE_SIZE) {
			_resource.clear();
		}
		TextData data = _resource.get(path);
		if (data == null) {
			data = new TextData(path, lazy ? null : BaseIO.loadText(path));
			_resource.put(path, data);
			_dirty = true;
		}
		return data;
	}

	public TextData loadTextData(String path) {
		return loadLazyData(path, false);
	}

	public String loadText(String path) {
		return loadTextData(path).getText();
	}

	public StrTokenizer loadStrTokenizer(final String path, final String delimiters) {
		if (StringUtils.isEmpty(path)) {
			return new StrTokenizer(LSystem.EMPTY);
		}
		String text = loadText(path);
		if (text == null) {
			return new StrTokenizer(LSystem.EMPTY);
		}
		if (delimiters == null) {
			return new StrTokenizer(text);
		} else {
			return new StrTokenizer(text, delimiters);
		}
	}

	public Object loadJsonObject(final String path) {
		return BaseIO.loadJsonObjectContext(loadText(path));
	}

	public TextData removeText(String path) {
		TextData text = _resource.remove(path);
		if (text != null) {
			_dirty = true;
		}
		return text;
	}

	public TextResource freeText(String path) {
		TextData data = _resource.remove(path);
		if (data != null) {
			data.close();
			_dirty = true;
		}
		return this;
	}

	public boolean containsPath(String path) {
		return _resource.containsKey(path);
	}

	public int getByteLength() {
		if (_dirty) {
			_byteCount = 0;
			for (TextData res : _resource.values()) {
				if (res != null) {
					final String result = res.getText();
					if (!StringUtils.isNullOrEmpty(result)) {
						_byteCount += result.length();
					}
				}
			}
			_dirty = false;
		}
		return _byteCount;
	}

	public TArray<String> getPaths() {
		final TArray<String> result = new TArray<String>();
		for (String fileName : _resource.keys()) {
			if (!StringUtils.isEmpty(fileName)) {
				result.add(fileName);
			}
		}
		return result;
	}

	public float getKBLength() {
		return (float) getByteLength() / 1024;
	}

	public float getMBLength() {
		return getKBLength() / 1024;
	}

	@Override
	public void close() {
		for (TextData res : _resource.values()) {
			if (res != null) {
				res.close();
			}
		}
		_resource.clear();
		_dirty = true;
	}

}
