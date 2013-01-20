/// <summary>
/// Copyright 2008 - 2013
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
namespace Loon.Utils.Xml {
	
    using System.IO;
    using System.Collections.Generic;
    using Loon.Core;
    using Loon.Java.Collections;

	public class XMLOutput : TextWriter, LRelease {
	
		private readonly Stack<string> _stack;
	
		private readonly TextWriter _writer;
	
		private string _currentElement;
	
		private bool _flag;
	
		public int _count;
	
		public XMLOutput():this(new StringWriter(System.Globalization.NumberFormatInfo.InvariantInfo)) {
			
		}
	
		public XMLOutput(TextWriter w) {
			this._stack = new Stack<string>();
			this._writer = w;
		}
	
		public void Start_ele(XMLElement ele) {
			for (IIterator it = ele.Elements(); it.HasNext();) {
				XMLElement e = (XMLElement) it.Next();
				Start_ele(e.GetName());
			}
		}
	
		public XMLOutput Start_ele(string name) {
			if (Content()) {
				_writer.Write((char)'\n');
			}
			Newline();
			_writer.Write((char)'<');
			_writer.Write(name);
			_currentElement = name;
			return this;
		}
	
		public XMLOutput Start_ele(string name, object text) {
			return Start_ele(name).Put_txt(text).End();
		}
	
		private bool Content() {
			if (_currentElement == null) {
				return false;
			}
			_count++;
			_stack.Push(_currentElement);
			_currentElement = null;
			_writer.Write(">");
			return true;
		}
	
		public void Start_attr(XMLAttribute attr)  {
			Start_attr(attr.GetName(), attr.GetValue());
		}
	
		public XMLOutput Start_attr(string name, object value_ren) {
            if (_currentElement == null)
            {
                throw new System.InvalidOperationException();
            }
			_writer.Write((char)' ');
			_writer.Write(name);
			_writer.Write("=\"");
			_writer.Write((value_ren == null) ? "null" : value_ren.ToString());
			_writer.Write((char)'"');
			return this;
		}
	
		public XMLOutput Put_txt(object text) {
			Content();
			string str0 = (text == null) ? "null" : text.ToString();
			_flag = str0.Length > 64;
			if (_flag) {
				_writer.Write((char)'\n');
				Newline();
			}
			_writer.Write(str0);
			if (_flag) {
				_writer.Write((char)'\n');
			}
			return this;
		}
	
		public XMLOutput End() {
			if (_currentElement != null) {
				_writer.Write("/>\n");
				_currentElement = null;
			} else {
				_count = MathUtils.Max(_count - 1, 0);
				if (_flag) {
					Newline();
				}
				_writer.Write("</");
				_writer.Write(_stack.Pop());
				_writer.Write(">\n");
			}
			_flag = true;
			return this;
		}
	
		private void Newline() {
			int count = _count;
			if (_currentElement != null){
				count++;
			}
			for (int i = 0; i < count; i++) {
				_writer.Write((char)'\t');
			}
		}
	
		public override void Close() {
			while (_stack.Count != 0) {
				End();
			}
			_writer.Close();
		}
	
		public override void Write(char[] cbuf, int off, int len) {
			Content();
			_writer.Write(cbuf, off, len);
		}
	
		public override void Flush() {
			_writer.Flush();
		}
	
		public override string ToString() {
			return _writer.ToString();
		}

        public override System.Text.Encoding Encoding
        {
            get { return System.Text.Encoding.UTF8; }
        }
    }
}
