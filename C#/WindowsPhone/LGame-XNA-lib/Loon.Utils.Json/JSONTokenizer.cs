/// <summary>
/// Copyright 2008 - 2012
/// Licensed under the Apache License, Version 2.0 (the "License"); you may not
/// use this file except in compliance with the License. You may obtain a copy of
/// the License at
/// http://www.apache.org/licenses/LICENSE-2.0
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
/// WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
/// License for the specific language governing permissions and limitations under
/// the License.
/// </summary>
///
/// @project loon
/// @email javachenpeng@yahoo.com
using Loon.Core.Resource;
using Loon.Core;
using System.IO;
using System.Text;
namespace Loon.Utils.Json {
	
	
	public class JSONTokenizer {
	
		private char[] _contents;
	
		private string _limits;
	
		private string _whites;
	
		private bool _includelimits;
	
		private bool _eof, _whitesFlag;
	
		private int _pos, _length;
	
		public JSONTokenizer(string limits, bool includelimits):this(null, limits, null, includelimits) {
			
		}
	
		public JSONTokenizer(char[] input, string limits, bool includelimits):this(input, limits, null, includelimits) {
			
		}
	
		public JSONTokenizer(char[] input, string limits, string whites,
				bool includelimits) {
			this._contents = input;
			this._limits = limits;
			this._whites = whites;
			this._includelimits = includelimits;
			this._pos = 0;
			this._eof = false;
			this._whitesFlag = !(this._whites == null);
			if (input != null) {
				this._length = input.Length;
			}
		}
	
		protected internal void Read(string path) {
			TextReader reader = new StreamReader(Resources.OpenSource(path),System.Text.Encoding.UTF8);
			char[] data = new char[2048];
			try {
				int offset = 0;
				for (;;) {
					int size = data.Length;
					int length = reader.Read(data, offset, size - offset);
					if (length == -1) {
						break;
					}
					if (length == 0) {
						char[] newData = new char[size * 2];
						System.Array.Copy(data,0,newData,0,size);
						data = newData;
					} else {
						offset += length;
					}
				}
			} catch (IOException ex) {
                Loon.Utils.Debugging.Log.Exception(ex);
			} finally {
				try {
					reader.Close();
				} catch (IOException) {
				}
			}
			this._contents = data;
			this._length = data.Length;
		}
	
		private StringBuilder nextToken = new StringBuilder();
	
		public string NextToken() {
			nextToken.Clear();
			char c = NextChar();
			switch ((int) c) {
			case '\b':
			case '\f':
			case '\n':
			case '\r':
			case '\t':
				c = NextChar();
				break;
			}
			while (!_eof) {
				if (c != '\n' && c != '\f' && c != '\n' && c != '\r' && c != '\t') {
					if (_limits.IndexOf(c) == -1) {
						nextToken.Append(c);
					} else {
						if (nextToken.Length > 0 || !_includelimits) {
							Callback();
						} else {
							nextToken.Append(c);
						}
						break;
					}
				}
				c = NextChar();
			}
			return nextToken.ToString();
		}
	
		public bool HasMoreTokens() {
			return (_pos < _length);
		}
	
		public void PutBackToken(string token) {
			int length = token.Length;
			if (_pos - length > 0) {
				_pos -= length;
			}
		}
	
		private char NextChar() {
			char c = ' ';
			if (_whitesFlag) {
				for (; _whites.IndexOf(c) != -1;) {
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
	
		private void Callback() {
			if (_pos > 0) {
				_pos--;
			}
		}
	
	}
}
