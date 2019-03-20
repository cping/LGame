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
package loon.utils;

public class LoopStringBuilder {

	private char[] chars;
	private int pos;
	private int size;

	LoopStringBuilder(int size) {
		this.size = size;
		pos = 0;
		chars = new char[size];
	}

	public void add(char c) {
		chars[pos++] = c;
		if (pos >= size) {
			pos = 0;
		}
	}

	public String get() {
		int q = pos;
		StringBuilder sbr = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sbr.append(chars[q++]);
			if (q >= size) {
				q = 0;
			}
		}
		return sbr.toString();
	}

}
