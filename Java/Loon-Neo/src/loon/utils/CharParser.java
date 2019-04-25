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
package loon.utils;

/**
 * 一个通用的字符串解析模板
 */
public class CharParser {

	protected int poistion;

	protected String context;

	public void setText(String c) {
		this.context = c;
	}

	public String getText() {
		return this.context;
	}

	public char nextChar() {
		if (eof()) {
			return (char) -1;
		}
		return context.charAt(poistion);
	}

	public boolean startsWith(String prefix) {
		return context.substring(poistion, context.length()).startsWith(prefix);
	}

	public boolean eof() {
		return (poistion >= context.length());
	}

	protected void consumeWhitespace() {
		while (!eof() && CharUtils.isWhitespace(nextChar())) {
			consumeChar();
		}
	}

	public char consumeChar() {
		if (poistion < context.length()) {
			char consumedChar = context.charAt(poistion);
			poistion++;
			return consumedChar;
		} else {
			return (char) -1;
		}
	}

}
