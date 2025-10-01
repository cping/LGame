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
package loon.utils.parse;

import loon.LSysException;
import loon.utils.ObjectMap;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;

public final class ParserReader {

	private char[] _context = null;
	private int _currentIndex = 0;
	private int _currentChar = 0;
	private final StrBuilder _tempBuffer;
	private int _tempIndex = 0;
	private int _level = 0;
	private int _eofIndex = 0;
	private final ObjectMap<Integer, Integer> _markMap;

	public ParserReader(String context) {
		if (context != null) {
			this._context = context.toCharArray();
		} else {
			this._context = new char[0];
		}
		this._tempBuffer = new StrBuilder();
		this._currentIndex = 0;
		this._tempIndex = 0;
		this._level = 0;
		this._eofIndex = -1;
		this._markMap = new ObjectMap<Integer, Integer>();
	}

	public String getString() {
		int begin = _markMap.get(_level - 1);
		int end = _tempIndex;
		if (begin > end) {
			throw new LSysException(StringUtils.format("begin:{0} > end:{1}", begin, end));
		} else {
			return _tempBuffer.substring(begin, end);
		}
	}

	public ParserReader positionChar(int idx) {
		this._currentIndex = idx;
		return this;
	}

	public int nextChar() {
		if (_currentIndex < 0) {
			this._currentIndex = 0;
		}
		final int len = this._context.length - 1;
		if (_currentIndex >= len) {
			return -1;
		}
		return _context[_currentIndex++];
	}

	public int read() {
		if (_tempIndex == _eofIndex) {
			_tempIndex++;
			return -1;
		}
		if (_tempIndex < _tempBuffer.length()) {
			_currentChar = (int) _tempBuffer.charAt(_tempIndex);
		} else {
			if (_eofIndex != -1) {
				return -1;
			}
			_currentChar = nextChar();
			if (_currentChar == -1) {
				_eofIndex = _tempIndex;
			}
			_tempBuffer.append((char) _currentChar);
		}
		_tempIndex++;
		return _currentChar;
	}

	public int currentChar() {
		read();
		unread();
		return _currentChar;
	}

	public int previous() {
		return _tempIndex <= 1 || _tempIndex >= _tempBuffer.length() ? -1 : _tempBuffer.charAt(_tempIndex - 2);
	}

	public ParserReader mark() {
		_markMap.put(_level, _tempIndex);
		_level++;
		return this;
	}

	public ParserReader unmark() {
		_level--;
		if (_level < 0) {
			throw new LSysException("no more mark() is to unmark()");
		}
		return this;
	}

	public ParserReader reset() {
		unmark();
		this._tempIndex = _markMap.get(_level);
		return this;
	}

	public ParserReader unread() {
		_tempIndex--;
		if (_tempIndex < 0) {
			_tempIndex = 0;
		}
		return this;
	}

	@Override
	public String toString() {
		return getString();
	}
}
