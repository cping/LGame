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

import loon.LSysException;

public class StrTokenizer {

	private String strings;

	private String delimiters;

	private boolean returnDelimiters;

	private int position;

	public StrTokenizer(String str) {
		this(str, " \t\n\r\f", false);
	}

	public StrTokenizer(String str, String delimiters) {
		this(str, delimiters, false);
	}

	public StrTokenizer(String str, String delimiters, boolean returnDelimiters) {
		if (str == null) {
			throw new LSysException("string == null");
		}
		this.strings = str;
		this.delimiters = delimiters;
		this.returnDelimiters = returnDelimiters;
		this.position = 0;
	}

	public void reset() {
		this.position = 0;
	}

	public int countTokens() {
		int count = 0;
		boolean inToken = false;
		for (int i = position, length = strings.length(); i < length; i++) {
			if (delimiters.indexOf(strings.charAt(i), 0) >= 0) {
				if (returnDelimiters) {
					count++;
				}
				if (inToken) {
					count++;
					inToken = false;
				}
			} else {
				inToken = true;
			}
		}
		if (inToken) {
			count++;
		}
		return count;
	}

	public boolean hasMoreElements() {
		return hasMoreTokens();
	}

	public boolean hasMoreTokens() {
		if (delimiters == null) {
			throw new NullPointerException("delimiters == null");
		}
		int length = strings.length();
		if (position < length) {
			if (returnDelimiters) {
				return true;
			}
			for (int i = position; i < length; i++) {
				if (delimiters.indexOf(strings.charAt(i), 0) == -1) {
					return true;
				}
			}
		}
		return false;
	}

	public String nextToken() {
		if (delimiters == null) {
			throw new LSysException("delimiters == null");
		}
		int i = position;
		int length = strings.length();

		if (i < length) {
			if (returnDelimiters) {
				if (delimiters.indexOf(strings.charAt(position), 0) >= 0)
					return String.valueOf(strings.charAt(position++));
				for (position++; position < length; position++)
					if (delimiters.indexOf(strings.charAt(position), 0) >= 0)
						return strings.substring(i, position);
				return strings.substring(i);
			}

			while (i < length && delimiters.indexOf(strings.charAt(i), 0) >= 0) {
				i++;
			}
			position = i;
			if (i < length) {
				for (position++; position < length; position++) {
					if (delimiters.indexOf(strings.charAt(position), 0) >= 0) {
						return strings.substring(i, position);
					}
				}
				return strings.substring(i);
			}
		}
		return null;
	}

	public String nextToken(String delims) {
		this.delimiters = delims;
		return nextToken();
	}

}
