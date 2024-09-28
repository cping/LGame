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

public class StrTokenizer {

	private String _strings;

	private String _delimiters;

	private boolean _returnDelimiters;

	private int _position;

	public StrTokenizer(String str) {
		this(str, " \t\n\r\f", false);
	}

	public StrTokenizer(String str, String delimiters) {
		this(str, delimiters, false);
	}

	public StrTokenizer(String str, String delimiters, boolean returnDelimiters) {
		if (str == null) {
			throw new LSysException("string == null");
		}
		this._strings = str;
		this._delimiters = delimiters;
		this._returnDelimiters = returnDelimiters;
		this._position = 0;
	}

	public void reset() {
		this._position = 0;
	}

	public int countTokens() {
		int count = 0;
		boolean inToken = false;
		for (int i = _position, length = _strings.length(); i < length; i++) {
			if (_delimiters.indexOf(_strings.charAt(i), 0) >= 0) {
				if (_returnDelimiters) {
					count++;
				}
				if (inToken) {
					count++;
					inToken = false;
				}
			} else {
				inToken = true;
			}
		}
		if (inToken) {
			count++;
		}
		return count;
	}

	public boolean hasMoreElements() {
		return hasMoreTokens();
	}

	public boolean hasMoreTokens() {
		if (_delimiters == null) {
			throw new NullPointerException("delimiters == null");
		}
		int length = _strings.length();
		if (_position < length) {
			if (_returnDelimiters) {
				return true;
			}
			for (int i = _position; i < length; i++) {
				if (_delimiters.indexOf(_strings.charAt(i), 0) == -1) {
					return true;
				}
			}
		}
		return false;
	}

	public String nextToken() {
		if (_delimiters == null) {
			throw new LSysException("delimiters == null");
		}
		int i = _position;
		int length = _strings.length();

		if (i < length) {
			if (_returnDelimiters) {
				if (_delimiters.indexOf(_strings.charAt(_position), 0) >= 0)
					return String.valueOf(_strings.charAt(_position++));
				for (_position++; _position < length; _position++)
					if (_delimiters.indexOf(_strings.charAt(_position), 0) >= 0)
						return _strings.substring(i, _position);
				return _strings.substring(i);
			}

			while (i < length && _delimiters.indexOf(_strings.charAt(i), 0) >= 0) {
				i++;
			}
			_position = i;
			if (i < length) {
				for (_position++; _position < length; _position++) {
					if (_delimiters.indexOf(_strings.charAt(_position), 0) >= 0) {
						return _strings.substring(i, _position);
					}
				}
				return _strings.substring(i);
			}
		}
		return null;
	}

	public String nextToken(String delims) {
		this._delimiters = delims;
		return nextToken();
	}

}
