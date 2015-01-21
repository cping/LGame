/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.utils.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import loon.core.LSystem;
import loon.core.resource.Resources;

public class JSONTokenizer {

	private char[] _contents;

	private String _limits;

	private String _whites;

	private boolean _includelimits;

	private boolean _eof, _whitesFlag;

	private int _pos, _length;

	public JSONTokenizer(String limits, boolean includelimits) {
		this(null, limits, null, includelimits);
	}

	public JSONTokenizer(char[] input, String limits, boolean includelimits) {
		this(input, limits, null, includelimits);
	}

	public JSONTokenizer(char[] input, String limits, String whites,
			boolean includelimits) {
		this._contents = input;
		this._limits = limits;
		this._whites = whites;
		this._includelimits = includelimits;
		this._pos = 0;
		this._eof = false;
		this._whitesFlag = !(this._whites == null);
		if (input != null) {
			this._length = input.length;
		}
	}

	protected void read(String path) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				Resources.openResource(path), LSystem.encoding));
		char[] data = new char[2048];
		try {
			int offset = 0;
			for (;;) {
				int size = data.length;
				int length = reader.read(data, offset, size - offset);
				if (length == -1) {
					break;
				}
				if (length == 0) {
					char[] newData = new char[size * 2];
					System.arraycopy(data, 0, newData, 0, size);
					data = newData;
				} else {
					offset += length;
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException ex) {
			}
		}
		this._contents = data;
		this._length = data.length;
	}

	private StringBuffer nextToken = new StringBuffer();

	public String nextToken() {
		nextToken.delete(0, nextToken.length());
		char c = nextChar();
		switch (c) {
		case '\b':
		case '\f':
		case '\n':
		case '\r':
		case '\t':
			c = nextChar();
			break;
		}
		while (!_eof) {
			if (c != '\n' && c != '\f' && c != '\n' && c != '\r' && c != '\t') {
				if (_limits.indexOf(c) == -1) {
					nextToken.append(c);
				} else {
					if (nextToken.length() > 0 || !_includelimits) {
						callback();
					} else {
						nextToken.append(c);
					}
					break;
				}
			}
			c = nextChar();
		}
		return nextToken.toString();
	}

	public boolean hasMoreTokens() {
		return (_pos < _length);
	}

	public void putBackToken(String token) {
		int length = token.length();
		if (_pos - length > 0) {
			_pos -= length;
		}
	}

	private char nextChar() {
		char c = ' ';
		if (_whitesFlag) {
			for (; _whites.indexOf(c) != -1;) {
				if (_pos < _length) {
					c = _contents[_pos];
					_pos++;
				} else {
					_eof = true;
				}
			}
		} else {
			if (_pos < _length) {
				c = _contents[_pos];
				_pos++;
			} else {
				_eof = true;
			}
		}
		return c;
	}

	private void callback() {
		if (_pos > 0) {
			_pos--;
		}
	}

}
