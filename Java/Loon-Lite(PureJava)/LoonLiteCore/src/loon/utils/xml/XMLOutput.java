/**
 * Copyright 2008 - 2013
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
package loon.utils.xml;

import java.util.Iterator;

import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.utils.MathUtils;
import loon.utils.StrBuilder;
import loon.utils.TArray;

public class XMLOutput implements LRelease {

	private final TArray<String> _stack = new TArray<String>();

	private final StrBuilder _writer;

	private String _currentElement;

	private boolean _flag;

	private int _count;

	public XMLOutput() {
		this(new StrBuilder());
	}

	public XMLOutput(StrBuilder s) {
		this._writer = s;
	}

	public void start_ele(XMLElement ele) {
		for (Iterator<?> it = ele.elements(); it.hasNext();) {
			XMLElement e = (XMLElement) it.next();
			start_ele(e.getName());
		}
	}

	public XMLOutput start_ele(String name) {
		if (content()) {
			_writer.append(LSystem.LF);
		}
		newline();
		_writer.append('<');
		_writer.append(name);
		_currentElement = name;
		return this;
	}

	public XMLOutput start_ele(String name, Object text) {
		return start_ele(name).put_txt(text).end();
	}

	private boolean content() {
		if (_currentElement == null) {
			return false;
		}
		_count++;
		_stack.add(_currentElement);
		_currentElement = null;
		_writer.append(">");
		return true;
	}

	public void start_attr(XMLAttribute attr) {
		start_attr(attr.getName(), attr.getValue());
	}

	public XMLOutput start_attr(String name, Object value) {
		if (_currentElement == null) {
			throw new LSysException("current element is null");
		}
		_writer.append(' ');
		_writer.append(name);
		_writer.append("=\"");
		_writer.append(value == null ? "null" : value.toString());
		_writer.append('"');
		return this;
	}

	public XMLOutput put_txt(Object text) {
		content();
		String string = text == null ? "null" : text.toString();
		_flag = string.length() > 64;
		if (_flag) {
			_writer.append(LSystem.LF);
			newline();
		}
		_writer.append(string);
		if (_flag) {
			_writer.append(LSystem.LF);
		}
		return this;
	}

	public XMLOutput end() {
		if (_stack.size <= 0) {
			return this;
		}
		if (_currentElement != null) {
			_writer.append("/>\n");
			_currentElement = null;
		} else {
			_count = MathUtils.max(_count - 1, 0);
			if (_flag) {
				newline();
			}
			_writer.append("</");
			_writer.append(_stack.pop());
			_writer.append(">\n");
		}
		_flag = true;
		return this;
	}

	private void newline() {
		int count = _count;
		if (_currentElement != null) {
			count++;
		}
		for (int i = 0; i < count; i++) {
			_writer.append('\t');
		}
	}

	@Override
	public void close() {
		while (_stack.size() != 0) {
			end();
		}
	}

	public void append(char[] cbuf, int off, int len) {
		content();
		_writer.append(cbuf, off, len);
	}

	public int count() {
		return _count;
	}

	@Override
	public String toString() {
		return _writer.toString();
	}

	public void dispose() {
		if (_stack != null) {
			_stack.clear();
		}
	}
}
