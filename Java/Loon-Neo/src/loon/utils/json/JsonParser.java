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

import java.math.BigInteger;

import loon.LSystem;
import loon.utils.MathUtils;

final class JsonParser {

	private enum Token {
		EOF(false), NULL(true), TRUE(true), 
		FALSE(true), STRING(true), NUMBER(true), 
		COMMA(false), COLON(false),
		OBJECT_START(true), OBJECT_END(false), 
		ARRAY_START(true), ARRAY_END(false);
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
			return new JsonParser(s).parse(clazz);
		}
	}

	private static final char[] TRUE = { 'r', 'u', 'e' };
	
	private static final char[] FALSE = { 'a', 'l', 's', 'e' };
	
	private static final char[] NULL = { 'u', 'l', 'l' };

	private StringBuilder _reusableBuffer = new StringBuilder();

	private String _strings;

	private int _linePos = 1, _rowPos, _charOffset, _utf8adjust;

	private int _tokenLinePos, _tokenCharPos, _tokenCharOffset;

	private int _index;

	private int _bufferLength;

	private boolean _eof;
	
	private Object value;

	private Token token;

	JsonParser(String s) throws JsonParserException {
		if (s == null) {
			throw new JsonParserException(new Exception(), "The json is null !", 0, 0, 0);
		}
		this._strings = s;
		this._bufferLength = s.length();
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
			throw createParseException(null, "Expected end of input, got " + token, true);
		}
		if (clazz != Object.class && (parsed == null || clazz != parsed.getClass())) {
			throw createParseException(null, "JSON did not contain the correct type, expected " + clazz.getName() + ".",
					true);
		}
		return (T) (parsed);
	}

	private Object currentValue() throws JsonParserException {
		if (token.isValue) {
			return value;
		}
		throw createParseException(null, "Expected JSON value, got " + token, true);
	}

	private Token advanceToken() throws JsonParserException {
		int c = advanceChar();
		while (isWhitespace(c)) {
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
					if (advanceToken() == Token.ARRAY_END)
						break;
					if (token != Token.COMMA)
						throw createParseException(null, "Expected a comma or end of the array instead of " + token,
								true);
					if (advanceToken() == Token.ARRAY_END)
						throw createParseException(null, "Trailing comma found in array", true);
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
						throw createParseException(null, "Expected STRING, got " + token, true);
					}
					String key = (String) value;
					if (advanceToken() != Token.COLON) {
						throw createParseException(null, "Expected COLON, got " + token, true);
					}
					advanceToken();
					map.put(key, currentValue());
					if (advanceToken() == Token.OBJECT_END) {
						break;
					}
					if (token != Token.COMMA) {
						throw createParseException(null, "Expected a comma or end of the object instead of " + token,
								true);
					}
					if (advanceToken() == Token.OBJECT_END) {
						throw createParseException(null, "Trailing object found in array", true);
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
			throw createParseException(null, "Numbers may not start with '" + (char) c + "'", true);
		default:
		}

		if (isAsciiLetter(c)) {
			throw createHelpfulException((char) c, null, 0);
		}

		throw createParseException(null, "Unexpected character: " + (char) c, true);
	}

	private void consumeKeyword(char first, char[] expected) throws JsonParserException {
		for (int i = 0; i < expected.length; i++) {
			if (advanceChar() != expected[i]) {
				throw createHelpfulException(first, expected, i);
			}
		}

		if (isAsciiLetter(peekChar())) {
			throw createHelpfulException(first, expected, expected.length);
		}
	}

	private Number consumeTokenNumber(char c) throws JsonParserException {
		int start = _index - 1;
		int end = _index;

		boolean isDouble = false;
		while (isDigitCharacter(peekChar())) {
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
							throw createParseException(null, "Malformed number: " + number, true);
					} else if (number.charAt(1) != 'e' && number.charAt(1) != 'E')
						throw createParseException(null, "Malformed number: " + number, true);
				}
				if (number.charAt(0) == '-') {
					if (number.charAt(1) == '0') {
						if (number.charAt(2) == '.') {
							if (number.length() == 3)
								throw createParseException(null, "Malformed number: " + number, true);
						} else if (number.charAt(2) != 'e' && number.charAt(2) != 'E')
							throw createParseException(null, "Malformed number: " + number, true);
					} else if (number.charAt(1) == '.') {
						throw createParseException(null, "Malformed number: " + number, true);
					}
				}

				return Double.parseDouble(number);
			}

			if (number.charAt(0) == '0') {
				if (number.length() == 1) {
					return 0;
				}
				throw createParseException(null, "Malformed number: " + number, true);
			}
			if (number.length() > 1 && number.charAt(0) == '-' && number.charAt(1) == '0') {
				if (number.length() == 2) {
					return -0.0;
				}
				throw createParseException(null, "Malformed number: " + number, true);
			}
			int length = number.charAt(0) == '-' ? number.length() - 1 : number.length();
			if (length < 10) {
				return Integer.parseInt(number);
			}
			if (length < 19) {
				return Long.parseLong(number);
			}
			return new BigInteger(number);
		} catch (NumberFormatException e) {
			throw createParseException(e, "Malformed number: " + number, true);
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
					throw createParseException(null, "EOF encountered in the middle of a _strings escape", false);
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
					throw createParseException(null, "Invalid escape: \\" + (char) escape, false);
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
			throw createParseException(null, "String was not terminated before end of input", true);
		}
		if (c < 32) {
			throw createParseException(null, "Strings may not contain control characters: 0x" + Integer.toString(c, 16),
					false);
		}
		return (char) c;
	}

	private int stringHexChar() throws JsonParserException {
		int c = "0123456789abcdef0123456789ABCDEF".indexOf(advanceChar()) % 16;
		if (c == -1) {
			throw createParseException(null, "Expected unicode hex escape character", false);
		}
		return c;
	}

	private boolean isDigitCharacter(int c) {
		return (c >= '0' && c <= '9') || c == 'e' || c == 'E' || c == '.' || c == '+' || c == '-';
	}

	private boolean isWhitespace(int c) {
		return c == ' ' || c == '\n' || c == '\r' || c == '\t';
	}

	private boolean isAsciiLetter(int c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}

	private int peekChar() {
		return _eof ? -1 : _strings.charAt(_index);
	}

	private int advanceChar() throws JsonParserException {
		if (_eof) {
			return -1;
		}
		int c = _strings.charAt(_index);
		if (c == '\n') {
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

		StringBuilder errorToken = new StringBuilder(
				first + (expected == null ? LSystem.EMPTY : new String(expected, 0, failurePosition)));

		while (isAsciiLetter(peekChar()) && errorToken.length() < 15) {
			errorToken.append((char) advanceChar());
		}

		return createParseException(null, "Unexpected token '" + errorToken + "'"
				+ (expected == null ? LSystem.EMPTY : ". Did you mean '" + first + new String(expected) + "'?"), true);
	}

	private JsonParserException createParseException(Exception e, String message, boolean tokenPos) {
		if (tokenPos)
			return new JsonParserException(e, message + " on line " + _tokenLinePos + ", char " + _tokenCharPos,
					_tokenLinePos, _tokenCharPos, _tokenCharOffset);
		else {
			int charPos = MathUtils.max(1, _index - _rowPos - _utf8adjust);
			return new JsonParserException(e, message + " on line " + _linePos + ", char " + charPos, _linePos, charPos,
					_index + _charOffset);
		}
	}
}
