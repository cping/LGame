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

import loon.BaseIO;
import loon.LSysException;
import loon.LSystem;
import loon.utils.ObjectMap;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;

/**
 * 工具类，用于解析yaml文件的配置数据
 */
public final class ParserYamlData {

	public static ParserYamlData parseString(String str, YamlEvent eve) {
		return new ParserYamlData(str, eve).parse();
	}

	public static ParserYamlData parseFile(String path, YamlEvent eve) {
		return new ParserYamlData(BaseIO.loadText(path), eve).parse();
	}

	public final static int LIST_OPEN = '[';
	public final static int LIST_CLOSE = ']';
	public final static int MAP_OPEN = '{';
	public final static int MAP_CLOSE = '}';
	public final static int DOCUMENT_HEADER = 'H';
	public final static int MAP_SEPARATOR = ':';

	private ParserReader _reader;
	private int _lineNo = 1;
	private char _pendingEvent;
	private YamlEvent _event;
	private ObjectMap<String, String> _props;

	protected ParserYamlData(String context, YamlEvent eve) {
		this._reader = new ParserReader(context);
		this._event = eve;
		this._props = new ObjectMap<String, String>();
	}

	protected String readerString() {
		return _reader.getString();
	}

	private void clearEvents() {
		_props.clear();
	}

	private void sendEvents() {
		String str = null;

		if (_pendingEvent == '[') {
			_event.event(LIST_OPEN);
		}
		if (_pendingEvent == '{') {
			_event.event(MAP_OPEN);
		}

		_pendingEvent = 0;

		if ((str = _props.get("anchor")) != null) {
			_event.property("anchor", str);
		}

		if ((str = _props.get("transfer")) != null) {
			_event.property("transfer", str);
		}

		if ((str = _props.get("alias")) != null) {
			_event.content("alias", str);
		}

		if ((str = _props.get("string")) != null) {
			_event.content("string", str);
		}

		if ((str = _props.get("value")) != null) {
			_event.content("value", str);
		}

		_props.clear();
	}

	public int indent() {
		mark();
		int i = 0;
		int ch;
		while (YamlCharacter.is(ch = _reader.read(), YamlCharacter.INDENT)) {
			i++;
		}
		if (ch == '\t') {
			throw new LSysException("Tabs may not be used for indentation." + _lineNo);
		}
		reset();
		return i;
	}

	public boolean array(int type) {
		mark();

		int i = 0;

		while (YamlCharacter.is(_reader.read(), type)) {
			i++;
		}

		if (i != 0) {
			_reader.unread();
			unmark();
			return true;
		}

		reset();
		return false;
	}

	public boolean space() {
		return array(YamlCharacter.SPACE);
	}

	public boolean line() {
		return array(YamlCharacter.LINE);
	}

	public boolean linesp() {
		return array(YamlCharacter.LINESP);
	}

	public boolean word() {
		return array(YamlCharacter.WORD);
	}

	public boolean number() {
		return array(YamlCharacter.DIGIT);
	}

	public boolean indent(int n) {
		mark();

		while (YamlCharacter.is(_reader.read(), YamlCharacter.INDENT) && n > 0) {
			n--;
		}

		if (n == 0) {
			_reader.unread();
			unmark();
			return true;
		}

		reset();
		return false;
	}

	public boolean newline() {
		_lineNo++;
		mark();

		int c = _reader.read();
		int c2 = _reader.read();

		if (c == -1 || (c == 13 && c2 == 10)) {
			unmark();
			return true;
		}

		if (YamlCharacter.is(c, YamlCharacter.LINEBREAK)) {
			_reader.unread();
			unmark();
			return true;
		}

		reset();
		_lineNo--;
		return false;
	}

	public boolean end() {
		mark();

		space();

		if (!newline()) {
			reset();
			return false;
		}

		while (comment(-1, false)) {
			;
		}
		unmark();
		return true;
	}

	public boolean getString() {
		char ch;
		int c;

		int i = 0;
		_reader.mark();
		boolean dash_first = false;
		for (;;) {
			c = _reader.read();
			if (c == -1) {
				break;
			}
			ch = (char) c;
			if (i == 0 && '-' == ch) {
				dash_first = true;
				continue;
			}
			if (i == 0 && (YamlCharacter.isSpaceChar(ch) || YamlCharacter.isIndicatorNonSpace(ch)
					|| YamlCharacter.isIndicatorSpace(ch))) {
				break;
			}
			if (dash_first && (YamlCharacter.isSpaceChar(ch) || YamlCharacter.isLineBreakChar(ch))) {
				unmark();
				return false;
			}
			if (!YamlCharacter.isLineSpChar(ch)
					|| (YamlCharacter.isIndicatorSimple(ch) && _reader.previous() != '\\')) {
				break;
			}
			i++;
		}

		_reader.unread();
		_reader.unmark();
		if (i != 0) {
			return true;
		}

		return false;
	}

	public boolean loose_string_result() {

		char ch = 0;
		int c;

		int i = 0;

		while (true) {
			c = _reader.read();
			if (c == -1) {
				break;
			}
			ch = (char) c;
			if (!YamlCharacter.isLineSpChar(ch)
					|| (YamlCharacter.isLooseIndicatorSimple(ch) && _reader.previous() != '\\')) {
				break;
			}
			i++;
		}

		_reader.unread();
		if (i == 0) {
			if (YamlCharacter.isLineBreakChar(ch))
				return true;
			else
				return false;
		}
		return true;
	}

	private boolean string_q1() {
		if (_reader.currentChar() != '\'') {
			return false;
		}
		_reader.read();
		int c = 0;
		while (YamlCharacter.is(c = _reader.read(), YamlCharacter.LINESP)) {
			if (c == '\'' && _reader.previous() != '\\') {
				break;
			}
		}
		if (c != '\'') {
			throw new LSysException("Unterminated string", _lineNo);
		}
		return true;
	}

	private boolean string_q2() {
		if (_reader.currentChar() != '"') {
			return false;
		}
		_reader.read();
		int c = 0;
		while (YamlCharacter.is(c = _reader.read(), YamlCharacter.LINESP)) {
			if (c == '"' && _reader.previous() != '\\') {
				break;
			}
		}

		if (c != '"') {
			throw new LSysException("Unterminated string", _lineNo);
		}
		return true;
	}

	public boolean loose_string() {
		mark();
		boolean q2 = false;
		boolean q1 = false;
		if ((q1 = string_q1()) || (q2 = string_q2()) || loose_string_result()) {
			String str = _reader.getString().trim();
			if (q2) {
				str = fix_q2(str);
			} else if (q1) {
				str = fix_q1(str);
			}
			if (q1 || q2) {
				_props.put("string", str);
			} else if (LSystem.EMPTY.equals(str)) {
				_props.put("value", null);
			} else {
				_props.put("value", str);
			}
			unmark();
			return true;
		}

		reset();
		return false;
	}

	public boolean isString() {
		mark();
		boolean q2 = false;
		boolean q1 = false;
		if ((q1 = string_q1()) || (q2 = string_q2()) || getString()) {
			String str = _reader.getString().trim();
			if (q2) {
				str = fix_q2(str);
			} else if (q1) {
				str = fix_q1(str);
			}
			if (q1 || q2) {
				_props.put("string", str);
			} else {
				_props.put("value", str);
			}
			unmark();
			return true;
		}

		reset();
		return false;
	}

	private String fix_q2(String str) {
		if (str.length() > 2) {
			return StringUtils.unescape(str.substring(1, str.length() - 1));
		} else {
			return LSystem.EMPTY;
		}
	}

	private String fix_q1(String str) {
		if (str.length() > 2) {
			return str.substring(1, str.length() - 1);
		} else {
			return LSystem.EMPTY;
		}
	}

	public boolean alias() {
		mark();

		if (_reader.read() != '*') {
			_reader.unread();
			unmark();
			return false;
		}

		if (!word()) {
			reset();
			return false;
		}

		unmark();
		_props.put("alias", _reader.getString());
		return true;
	}

	public boolean anchor() {
		mark();

		if (_reader.read() != '&') {
			_reader.unread();
			unmark();
			return false;
		}

		if (!word()) {
			reset();
			return false;
		}

		unmark();
		_props.put("anchor", _reader.getString());
		return true;
	}

	public boolean comment(int n, boolean explicit) {
		mark();

		if (n != -1 && indent() >= n) {
			reset();
			return false;
		}

		space();

		int c;
		if ((c = _reader.read()) == '#') {
			linesp();
		} else {
			if (c == -1) {
				unmark();
				return false;
			}

			if (explicit) {
				reset();
				return false;
			} else {
				_reader.unread();
			}
		}

		boolean b = newline();

		if (b == false) {
			reset();
			return false;
		}

		unmark();
		return true;
	}

	public boolean header() {
		mark();

		int c = _reader.read();
		int c2 = _reader.read();
		int c3 = _reader.read();

		if (c != '-' || c2 != '-' || c3 != '-') {
			reset();
			return false;
		}

		while (space() && directive()) {
			;
		}

		unmark();
		_event.event(DOCUMENT_HEADER);
		return true;
	}

	public boolean directive() {
		mark();

		if (_reader.read() != '#') {
			_reader.unread();
			unmark();
			return false;
		}

		if (!word()) {
			reset();
			return false;
		}

		if (_reader.read() != ':') {
			reset();
			return false;
		}

		if (!line()) {
			reset();
			return false;
		}

		_event.content("directive", _reader.getString());
		unmark();
		return true;
	}

	public boolean transfer() {
		mark();

		if (_reader.read() != '!') {
			_reader.unread();
			unmark();
			return false;
		}

		if (!line()) {
			reset();
			return false;
		}
		_props.put("transfer", _reader.getString());
		unmark();

		return true;
	}

	public boolean properties() {
		mark();

		if (transfer()) {
			space();
			anchor();
			unmark();
			return true;
		}

		if (anchor()) {
			space();
			transfer();
			unmark();
			return true;
		}

		reset();
		return false;
	}

	public boolean key(int n) {
		if (_reader.currentChar() == '?') {
			_reader.read();
			if (!value_nested(n + 1)) {
				throw new LSysException("'?' key indicator without a nested value", _lineNo);
			}
			if (!indent(n)) {
				throw new LSysException("Incorrect indentations after nested key", _lineNo);
			}
			return true;
		}

		if (!value_inline()) {
			return false;
		}

		space();
		return true;
	}

	public boolean value(int n) {

		if (value_nested(n) || value_block(n)) {
			return true;
		}

		if (!loose_value_inline()) {
			return false;
		}
		if (!end()) {
			throw new LSysException("Unterminated inline value", _lineNo);
		}
		return true;
	}

	public boolean loose_value(int n) {

		if (value_nested(n) || value_block(n)) {
			return true;
		}
		if (!loose_value_inline()) {
			return false;
		}
		if (!end()) {
			throw new LSysException("Unterminated inline value", _lineNo);
		}
		return true;
	}

	public boolean value_na(int n) {
		if (value_nested(n) || value_block(n)) {
			return true;
		}
		if (!value_inline_na()) {
			return false;
		}

		if (!end()) {
			throw new LSysException("Unterminated inline value", _lineNo);
		}

		return true;
	}

	public boolean value_inline() {
		mark();

		if (properties()) {
			space();
		}
		if (alias() || isString()) {
			sendEvents();
			unmark();
			return true;
		}

		if (list() || map()) {
			unmark();
			return true;
		}

		clearEvents();
		reset();
		return false;
	}

	public boolean loose_value_inline() {
		mark();

		if (properties()) {
			space();
		}
		if (alias() || loose_string()) {
			sendEvents();
			unmark();
			return true;
		}

		if (list() || map()) {
			unmark();
			return true;
		}

		clearEvents();
		reset();
		return false;
	}

	public boolean value_inline_na() {
		mark();

		if (properties()) {
			space();
		}
		if (isString()) {
			sendEvents();
			unmark();
			return true;
		}

		if (list() || map()) {
			unmark();
			return true;
		}

		clearEvents();
		reset();
		return false;
	}

	public boolean value_nested(int n) {
		mark();
		if (properties()) {
			space();
		}
		if (!end()) {
			clearEvents();
			reset();
			return false;
		}
		sendEvents();

		while (comment(n, false)) {
			;
		}
		if (nlist(n) || nmap(n)) {
			unmark();
			return true;
		}

		reset();
		return false;
	}

	public boolean value_block(int n) {
		mark();

		if (properties()) {
			space();
		}
		if (!block(n)) {
			clearEvents();
			reset();
			return false;
		}

		sendEvents();

		while (comment(n, false)) {
			;
		}
		unmark();
		return true;
	}

	public boolean nmap(int n) {
		mark();
		int in = indent();
		if (n == -1) {
			n = in;
		} else if (in > n) {
			n = in;
		}
		_pendingEvent = '{';

		int i = 0;
		while (true) {
			if (!indent(n)) {
				break;
			}
			if (!nmap_entry(n)) {
				break;
			}
			i++;
		}
		if (i > 0) {
			_event.event(MAP_CLOSE);
			unmark();
			return true;
		}
		_pendingEvent = 0;
		reset();
		return false;
	}

	public boolean nmap_entry(int n) {
		if (!key(n)) {
			return false;
		}
		if (_reader.currentChar() != ':') {
			return false;
		}
		_reader.read();
		_event.event(MAP_SEPARATOR);
		space();

		if (!loose_value(n + 1)) {
			throw new LSysException("no value after ':'", _lineNo);
		}

		return true;
	}

	public boolean nlist(int n) {
		mark();

		int in = indent();

		if (n == -1) {
			n = in;
		} else if (in > n) {
			n = in;
		}
		_pendingEvent = '[';

		int i = 0;
		while (true) {
			if (!indent(n)) {
				break;
			}
			if (!nlist_entry(n)) {
				break;
			}
			i++;
		}

		if (i > 0) {
			_event.event(LIST_CLOSE);
			unmark();
			return true;
		}

		_pendingEvent = 0;
		reset();
		return false;
	}

	boolean start_list() {
		_reader.mark();
		if (_reader.read() == '-') {
			if (YamlCharacter.isLineBreakChar((char) _reader.currentChar()) || space()) {
				_reader.unmark();
				return true;
			}
		}
		_reader.reset();
		return false;
	}

	public boolean nlist_entry(int n) {
		if (!start_list()) {
			return false;
		}
		space();
		if (nmap_inlist(n + 1) || value(n + 1)) {
			return true;
		}
		throw new LSysException("bad nlist", _lineNo);
	}

	public boolean nmap_inlist(int n) {
		mark();
		if (!isString()) {
			reset();
			return false;
		}
		space();
		if (_reader.read() != ':') {
			reset();
			return false;
		}
		if (_pendingEvent == '[') {
			_event.event(LIST_OPEN);
			_pendingEvent = 0;
		}
		_event.event(MAP_OPEN);
		sendEvents();
		_event.event(MAP_SEPARATOR);
		if (!space()) {
			reset();
			return false;
		}
		if (!value(n + 1)) {
			throw new LSysException("No value after ':' in map_in_list", _lineNo);
		}
		n = n + 1;
		int in = indent();

		if (n == -1) {
			n = in;
		} else if (in > n) {
			n = in;
		}
		for (;;) {
			if (!indent(n)) {
				break;
			}
			if (!nmap_entry(n)) {
				break;
			}
		}
		_event.event(MAP_CLOSE);
		unmark();
		return true;
	}

	public boolean block(int n) {
		int c = _reader.currentChar();
		if (c != '|' && c != ']' && c != '>') {
			return false;
		}
		_reader.read();
		if (_reader.currentChar() == '\\') {
			_reader.read();
		}
		space();
		if (number()) {
			space();
		}
		if (!newline()) {
			throw new LSysException("No newline after block definition", _lineNo);
		}
		StrBuilder sbr = new StrBuilder();
		int block_indent = block_line(n, -1, sbr, (char) c);
		while (-1 != block_line(n, block_indent, sbr, (char) c)) {
			;
		}
		String blockString = sbr.toString();
		if (blockString.length() > 0 && YamlCharacter.isLineBreakChar(blockString.charAt(blockString.length() - 1))) {
			blockString = blockString.substring(0, blockString.length() - 1);
		}
		_event.content("string", blockString);

		return true;
	}

	public int block_line(int n, int block_indent, StrBuilder sbr, char ch) {
		int in = 0;
		if (block_indent == -1) {
			in = indent();
			if (in < n) {
				return -1;
			}
			n = in;
			indent(n);
		} else {
			in = block_indent;
			if (!indent(block_indent)) {
				return -1;
			}
		}
		if (_reader.currentChar() == -1) {
			return -1;
		}
		mark();

		linesp();
		sbr.append(_reader.getString());

		unmark();

		if (ch == '|') {
			sbr.append('\n');
		} else {
			sbr.append(' ');
		}

		newline();
		return in;
	}

	public boolean list() {
		if (_reader.currentChar() != '[') {
			return false;
		}
		_reader.read();
		sendEvents();
		_event.event(LIST_OPEN);
		while (list_entry()) {
			int c = _reader.currentChar();
			if (c == ']') {
				_reader.read();
				_event.event(LIST_CLOSE);
				return true;
			}
			if (c != ',') {
				throw new LSysException("inline list error: expecting ','", _lineNo);
			}
			_reader.read();
		}
		int c = _reader.currentChar();
		if (c == ']') {
			_reader.read();
			_event.event(LIST_CLOSE);
			return true;
		} else {
			throw new LSysException("inline list error", _lineNo);
		}
	}

	public boolean list_entry() {
		space();
		if (!loose_value_inline()) {
			return false;
		}
		space();
		return true;
	}

	public boolean map() {
		if (_reader.currentChar() != '{') {
			return false;
		}
		_reader.read();
		sendEvents();
		_event.event(MAP_OPEN);
		while (map_entry()) {
			int c = _reader.currentChar();
			if (c == '}') {
				_reader.read();
				_event.event(MAP_CLOSE);
				return true;
			}
			if (c != ',') {
				throw new LSysException("inline map error: expecting ','", _lineNo);
			}
			_reader.read();
		}
		int c = _reader.currentChar();
		if (c == '}') {
			_reader.read();
			_event.event(MAP_CLOSE);
			return true;
		}
		throw new LSysException("inline map error", _lineNo);
	}

	public boolean map_entry() {
		space();
		if (!value_inline()) {
			return false;
		}
		space();
		if (_reader.currentChar() != ':') {
			return false;
		}
		_reader.read();
		_event.event(MAP_SEPARATOR);
		if (!space()) {
			throw new LSysException("No space after ':'", _lineNo);
		}
		if (!loose_value_inline()) {
			throw new LSysException("No value after ':'", _lineNo);
		}
		space();
		return true;
	}

	public boolean document_first() {
		boolean b = nlist(-1) || nmap(-1);
		mark();
		if (!header() && _reader.read() != YamlCharacter.EOF && _reader.read() != YamlCharacter.EOF) {
			throw new LSysException("End of document expected.");
		}
		unmark();
		if (!b) {
			throw new LSysException("first document is not a nested list or map", _lineNo);
		}
		return true;
	}

	public boolean document_next() {
		if (!header()) {
			return false;
		}
		if (!value_na(-1)) {
			return false;
		}
		return true;
	}

	public ParserYamlData parse() {
		try {
			while (comment(-1, false)) {
				;
			}
			if (!header()) {
				document_first();
			} else {
				value_na(-1);
			}
			while (document_next()) {
				;
			}
		} catch (LSysException e) {
			_event.error(e, _lineNo);
		}
		return this;
	}

	private void mark() {
		_reader.mark();
	}

	private void reset() {
		_reader.reset();
	}

	private void unmark() {
		_reader.unmark();
	}

	public YamlEvent getEvent() {
		return _event;
	}

	public void setEvent(YamlEvent event) {
		this._event = event;
	}

	public int getLineNumber() {
		return _lineNo;
	}

}
