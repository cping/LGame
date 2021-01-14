/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.utils.json;

import loon.LSystem;
import loon.utils.CharUtils;
import loon.utils.MathUtils;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;

/**
 * 自带的json解析用类
 */
final class JsonParser {

	private final static String HEXCHARS = "0123456789abcdef0123456789ABCDEF";

	private final static String JSONTOKENS = " \b\r\t\n\r\f{}[]:,/\\\"";

	private static enum Token {
		EOF(false), NULL(true), TRUE(true), FALSE(true), STRING(true), NUMBER(true), COMMA(false), COLON(false),
		OBJECT_START(true), OBJECT_END(false), ARRAY_START(true), ARRAY_END(false);

		public boolean isValue;

		Token(boolean isValue) {
			this.isValue = isValue;
		}
	}

	public static final class JsonParserContext<T> {
		private final Class<T> clazz;

		JsonParserContext(Class<T> clazz) {
			this.clazz = clazz;
		}

		public T from(String s) throws JsonParserException {
			return from(s, true);
		}

		public T from(String s, boolean filter) throws JsonParserException {
			return new JsonParser(s, filter).parse(clazz);
		}

	}

	private static final char[] TRUE = { 'r', 'u', 'e' };

	private static final char[] FALSE = { 'a', 'l', 's', 'e' };

	private static final char[] NULL = { 'u', 'l', 'l' };

	private StrBuilder _reusableBuffer = new StrBuilder();

	private String _strings;

	private int _linePos = 1, _rowPos, _charOffset, _utf8adjust;

	private int _tokenLinePos, _tokenCharPos, _tokenCharOffset;

	private int _index;

	private int _bufferLength;

	private boolean _eof;

	private Object value;

	private Token token;

	JsonParser(String s, boolean filter) throws JsonParserException {
		if (s == null) {
			throw new JsonParserException("The json is null !", 0, 0, 0);
		}
		this._strings = filter ? quotesFilter(s) : s;
		this._bufferLength = _strings.length();
		this._eof = (s.length() == 0);
	}

	public static JsonParserContext<JsonObject> object() {
		return new JsonParserContext<JsonObject>(JsonObject.class);
	}

	public static JsonParserContext<JsonArray> array() {
		return new JsonParserContext<JsonArray>(JsonArray.class);
	}

	public static JsonParserContext<Object> any() {
		return new JsonParserContext<Object>(Object.class);
	}

	@SuppressWarnings("unchecked")
	<T> T parse(Class<T> clazz) throws JsonParserException {
		advanceToken();
		Object parsed = currentValue();
		if (advanceToken() != Token.EOF) {
			throw createParseException("Expected end of input, got " + token, true);
		}
		if (clazz != Object.class && (parsed == null || clazz != parsed.getClass())) {
			throw createParseException("JSON did not contain the correct type, expected " + clazz.getName() + ".",
					true);
		}
		return (T) (parsed);
	}

	public static String quotesFilter(String jsonText) {
		if (StringUtils.isEmpty(jsonText)) {
			return LSystem.EMPTY;
		}
		final StrBuilder jsonContext = new StrBuilder();
		final StrBuilder jsonValue = new StrBuilder();
		int len = jsonText.length();
		boolean quote = false;
		char c = '\0';
		for (int i = 0; i < len; i++) {
			c = jsonText.charAt(i);
			if (c == LSystem.DOUBLE_QUOTES) {
				quote = !quote;
			}
			if (JSONTOKENS.indexOf(c) != -1) {
				if (jsonValue.length() > 0) {
					String value = jsonValue.toString();
					if (MathUtils.isNan(value)) {
						jsonContext.append(value);
					} else {
						jsonContext.append(LSystem.DOUBLE_QUOTES);
						jsonContext.append(value);
						jsonContext.append(LSystem.DOUBLE_QUOTES);
					}
					jsonValue.setLength(0);
				}
				jsonContext.append(c);
			} else if (quote) {
				jsonContext.append(c);
			} else {
				jsonValue.append(c);
			}
		}
		return jsonContext.toString();
	}

	private Object currentValue() throws JsonParserException {
		if (token.isValue) {
			return value;
		}
		throw createParseException("Expected JSON value, got " + token, true);
	}

	private Token advanceToken() throws JsonParserException {
		int c = advanceChar();

		while (CharUtils.isWhitespace(c)) {
			c = advanceChar();
		}
		_tokenLinePos = _linePos;
		_tokenCharPos = _index - _rowPos - _utf8adjust;
		_tokenCharOffset = _charOffset + _index;

		switch (c) {
		case -1:
			return token = Token.EOF;
		case '[':
			JsonArray list = new JsonArray();
			if (advanceToken() != Token.ARRAY_END)
				while (true) {
					list.add(currentValue());
					if (advanceToken() == Token.ARRAY_END) {
						break;
					}
					if (token != Token.COMMA) {
						throw createParseException("Expected a comma or end of the array instead of " + token, true);
					}
					if (advanceToken() == Token.ARRAY_END) {
						throw createParseException("Trailing comma found in array", true);
					}
				}
			value = list;
			return token = Token.ARRAY_START;
		case ']':
			return token = Token.ARRAY_END;
		case ',':
			return token = Token.COMMA;
		case ':':
			return token = Token.COLON;
		case '{':
			JsonObject map = new JsonObject();
			if (advanceToken() != Token.OBJECT_END)
				while (true) {
					if (token != Token.STRING) {
						throw createParseException("Expected STRING, got " + token, true);
					}
					String key = (String) value;
					if (advanceToken() != Token.COLON) {
						throw createParseException("Expected COLON, got " + token, true);
					}
					advanceToken();
					map.put(key, currentValue());
					if (advanceToken() == Token.OBJECT_END) {
						break;
					}
					if (token != Token.COMMA) {
						throw createParseException("Expected a comma or end of the object instead of " + token, true);
					}
					if (advanceToken() == Token.OBJECT_END) {
						throw createParseException("Trailing object found in array", true);
					}
				}
			value = map;
			return token = Token.OBJECT_START;
		case '}':
			return token = Token.OBJECT_END;
		case 't':
			consumeKeyword((char) c, TRUE);
			value = Boolean.TRUE;
			return token = Token.TRUE;
		case 'f':
			consumeKeyword((char) c, FALSE);
			value = Boolean.FALSE;
			return token = Token.FALSE;
		case 'n':
			consumeKeyword((char) c, NULL);
			value = null;
			return token = Token.NULL;
		case '\"':
			value = consumeTokenString();
			return token = Token.STRING;
		case '-':
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			value = consumeTokenNumber((char) c);
			return token = Token.NUMBER;
		case '+':
		case '.':
			throw createParseException("Numbers may not start with '" + (char) c + "'", true);
		default:
		}

		if (CharUtils.isAsciiLetter(c)) {
			throw createHelpfulException((char) c, (char[]) null, 0);
		}

		throw createParseException("Unexpected character: " + (char) c, true);

	}

	private void consumeKeyword(char first, char[] expected) throws JsonParserException {
		for (int i = 0; i < expected.length; i++) {
			int adChar = advanceChar();
			if (adChar != expected[i]) {
				throw createHelpfulException(first, expected, i);
			}
		}

		if (CharUtils.isAsciiLetter(peekChar())) {
			throw createHelpfulException(first, expected, expected.length);
		}
	}

	private Number consumeTokenNumber(char c) throws JsonParserException {
		int start = _index - 1;
		int end = _index;

		boolean isDouble = false;
		while (CharUtils.isDigitCharacter(peekChar())) {
			char next = (char) advanceChar();
			isDouble = next == '.' || next == 'e' || next == 'E' || isDouble;
			end++;
		}

		String number = _strings.substring(start, end);

		try {
			if (isDouble) {
				if (number.charAt(0) == '0') {
					if (number.charAt(1) == '.') {
						if (number.length() == 2)
							throw createParseException("Malformed number: " + number, true);
					} else if (number.charAt(1) != 'e' && number.charAt(1) != 'E')
						throw createParseException("Malformed number: " + number, true);
				}
				if (number.charAt(0) == '-') {
					if (number.charAt(1) == '0') {
						if (number.charAt(2) == '.') {
							if (number.length() == 3)
								throw createParseException("Malformed number: " + number, true);
						} else if (number.charAt(2) != 'e' && number.charAt(2) != 'E')
							throw createParseException("Malformed number: " + number, true);
					} else if (number.charAt(1) == '.') {
						throw createParseException("Malformed number: " + number, true);
					}
				}

				return Double.parseDouble(number);
			}

			if (number.charAt(0) == '0') {
				if (number.length() == 1) {
					return 0;
				}
				throw createParseException("Malformed number: " + number, true);
			}
			if (number.length() > 1 && number.charAt(0) == '-' && number.charAt(1) == '0') {
				if (number.length() == 2) {
					return -0.0;
				}
				throw createParseException("Malformed number: " + number, true);
			}
			int length = number.charAt(0) == '-' ? number.length() - 1 : number.length();
			if (length < 10) {
				return Integer.parseInt(number);
			}
			if (length < 19) {
				return Long.parseLong(number);
			}
			return new java.math.BigInteger(number);
		} catch (NumberFormatException e) {
			throw createParseException("Malformed number: " + number, true);
		}
	}

	private String consumeTokenString() throws JsonParserException {
		_reusableBuffer.setLength(0);
		while (true) {
			char c = stringChar();

			switch (c) {
			case '\"':
				return _reusableBuffer.toString();
			case '\\':
				int escape = advanceChar();
				switch (escape) {
				case -1:
					throw createParseException("EOF encountered in the middle of a strings escape", false);
				case 'b':
					_reusableBuffer.append('\b');
					break;
				case 'f':
					_reusableBuffer.append('\f');
					break;
				case 'n':
					_reusableBuffer.append('\n');
					break;
				case 'r':
					_reusableBuffer.append('\r');
					break;
				case 't':
					_reusableBuffer.append('\t');
					break;
				case '"':
				case '/':
				case '\\':
					_reusableBuffer.append((char) escape);
					break;
				case 'u':
					_reusableBuffer.append((char) (stringHexChar() << 12 | stringHexChar() << 8 //
							| stringHexChar() << 4 | stringHexChar()));
					break;
				default:
					throw createParseException("Invalid escape: \\" + (char) escape, false);
				}
				break;
			default:
				_reusableBuffer.append(c);
			}
		}
	}

	private char stringChar() throws JsonParserException {
		int c = advanceChar();

		if (c == -1) {
			throw createParseException("String was not terminated before end of input", true);
		}
		if (c < 32) {
			throw createParseException("Strings may not contain control characters: 0x" + Integer.toString(c, 16),
					false);
		}
		return (char) c;
	}

	private int stringHexChar() throws JsonParserException {
		int c = HEXCHARS.indexOf(advanceChar()) % 16;
		if (c == -1) {
			throw createParseException("Expected unicode hex escape character", false);
		}
		return c;
	}

	private int peekChar() {
		return _eof ? -1 : _strings.charAt(_index);
	}

	private int advanceChar() throws JsonParserException {
		if (_eof) {
			return -1;
		}
		int c = _strings.charAt(_index);

		if (c == LSystem.LF) {
			_linePos++;
			_rowPos = _index + 1;
			_utf8adjust = 0;
		}

		_index++;
		if (_index >= _bufferLength) {
			_eof = true;
		}

		return c;
	}

	private JsonParserException createHelpfulException(char first, char[] expected, int failurePosition)
			throws JsonParserException {

		StrBuilder errorToken = new StrBuilder(
				first + (expected == null ? LSystem.EMPTY : new String(expected, 0, failurePosition)));

		while (CharUtils.isAsciiLetter(peekChar()) && errorToken.length() < 15) {
			errorToken.append((char) advanceChar());
		}

		return createParseException(
				"Unexpected token '" + errorToken.toString() + "'"
						+ (expected == null ? LSystem.EMPTY : ". Did you mean '" + first + new String(expected) + "'?"),
				true);
	}

	private JsonParserException createParseException(String message, boolean tokenPos) {
		if (tokenPos) {
			return new JsonParserException(message + " on line " + _tokenLinePos + ", char " + _tokenCharPos,
					_tokenLinePos, _tokenCharPos, _tokenCharOffset);
		} else {
			int charPos = MathUtils.max(1, _index - _rowPos - _utf8adjust);
			return new JsonParserException(message + " on line " + _linePos + ", char " + charPos, _linePos, charPos,
					_index + _charOffset);
		}
	}
}
