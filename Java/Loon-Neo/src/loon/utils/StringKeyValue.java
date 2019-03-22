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
package loon.utils;

public class StringKeyValue {

	private String key;

	private String value;

	private StringBuilder _buffer;

	private boolean _dirty;

	public StringKeyValue(String k) {
		this(k, null);
	}

	public StringKeyValue(String k, String val) {
		this.key = k;
		this.value = val;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public StringKeyValue addValue(CharSequence ch) {
		if (_buffer == null) {
			_buffer = new StringBuilder();
		}
		_buffer.append(ch);
		_dirty = true;
		return this;
	}

	public StringKeyValue removeValue() {
		return removeValue(0, _buffer.length());
	}

	public StringKeyValue removeValue(int start, int end) {
		if (_buffer == null) {
			_buffer = new StringBuilder();
		}
		_buffer.delete(start, end);
		_dirty = true;
		return this;
	}

	public String getValue() {
		if (_dirty && _buffer != null) {
			value = _buffer.toString();
			_dirty = false;
		}
		return value;
	}

	@Override
	public String toString() {
		return getKey() + ":" + getValue();
	}
}
