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

import loon.LSystem;
import loon.utils.CharUtils;

public final class YamlCharacter {

	public final static int PRINTABLE = 1;
	public final static int WORD = 2;
	public final static int LINE = 3;
	public final static int LINESP = 4;
	public final static int SPACE = 5;
	public final static int LINEBREAK = 6;
	public final static int DIGIT = 7;
	public final static int INDENT = 8;
	public final static int EOF = -1;

	public static boolean is(char c, int type) {
		switch (type) {
		case PRINTABLE:
			return isPrintableChar(c);
		case WORD:
			return isWordChar(c);
		case LINE:
			return isLineChar(c);
		case LINESP:
			return isLineSpChar(c);
		case SPACE:
			return isSpaceChar(c);
		case LINEBREAK:
			return isLineBreakChar(c);
		case DIGIT:
			return CharUtils.isDigit(c);
		case INDENT:
			return (c == LSystem.SPACE);
		default:
			return false;
		}
	}

	public static boolean is(int c, int type) {
		if (c == -1) {
			return false;
		}
		char ch = (char) c;
		switch (type) {
		case PRINTABLE:
			return isPrintableChar(ch);
		case WORD:
			return isWordChar(ch);
		case LINE:
			return isLineChar(ch);
		case LINESP:
			return isLineSpChar(ch);
		case SPACE:
			return isSpaceChar(ch);
		case LINEBREAK:
			return isLineBreakChar(ch);
		case DIGIT:
			return CharUtils.isDigit(ch);
		case INDENT:
			return (ch == LSystem.SPACE);
		default:
			return false;
		}
	}

	public static boolean isPrintableChar(char c) {
		if (c >= 0x20 && c <= 0x7e) {
			return true;
		}
		if (c == 9 || c == 10 || c == 13 || c == 0x85) {
			return true;
		}
		if (c >= 0xa0 && c <= 0xd7ff) {
			return true;
		}
		if (c >= 0xe000 && c <= 0xfffd) {
			return true;
		}
		return false;
	}

	public static boolean isLineChar(char c) {
		if (c == 0x20 || c == 9 || c == 10 || c == 13 || c == 0x85) {
			return false;
		}
		return isPrintableChar(c);
	}

	public static boolean isLineSpChar(char c) {
		if (c == 10 || c == 13 || c == 0x85) {
			return false;
		}
		return isPrintableChar(c);
	}

	public static boolean isWordChar(char c) {
		if (c >= 0x41 && c <= 0x5a) {
			return true;
		}
		if (c >= 0x61 && c <= 0x7a) {
			return true;
		}
		if (c >= 0x30 && c <= 0x39) {
			return true;
		}
		if (c == LSystem.DASHED) {
			return true;
		}
		return false;
	}

	public static boolean isSpaceChar(char c) {
		if (c == 9 || c == 0x20) {
			return true;
		}
		return false;
	}

	public static boolean isLineBreakChar(char c) {
		if (c == 10 || c == 13 || c == 0x85 || c == 0x2028 || c == 0x2029) {
			return true;
		}
		return false;
	}

	public static boolean isIndicator(char c) {
		final String indicators = "-:[]{},?*&!|#@%^'\"";
		return (indicators.indexOf(c) != -1) ? true : false;
	}

	public static boolean isIndicatorSpace(char c) {
		final String indicators = ":-";
		return (indicators.indexOf(c) != -1) ? true : false;
	}

	public static boolean isIndicatorInline(char c) {
		final String indicators = "[]{},";
		return (indicators.indexOf(c) != -1) ? true : false;
	}

	public static boolean isIndicatorNonSpace(char c) {
		final String indicators = "?*&!]|#@%^\"'";
		return (indicators.indexOf(c) != -1) ? true : false;
	}

	public static boolean isIndicatorSimple(char c) {
		final String indicators = ":[]{},";
		return (indicators.indexOf(c) != -1) ? true : false;
	}

	public static boolean isLooseIndicatorSimple(char c) {
		final String indicators = "[]{},";
		return (indicators.indexOf(c) != -1) ? true : false;
	}
}
