/*
 * Copyright 2010 Google Inc.
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
 */
package java.util;

public class StringTokenizer implements Enumeration<Object> {

	private String string;

	private String delimiters;

	private boolean returnDelimiters;

	private int position;

	public StringTokenizer(String string) {
		this(string, " \t\n\r\f", false);
	}

	public StringTokenizer(String string, String delimiters) {
		this(string, delimiters, false);
	}

	public StringTokenizer(String string, String delimiters, boolean returnDelimiters) {
		if (string == null) {
			throw new NullPointerException("string == null");
		}
		this.string = string;
		this.delimiters = delimiters;
		this.returnDelimiters = returnDelimiters;
		this.position = 0;
	}

	public int countTokens() {
		int count = 0;
		boolean inToken = false;
		for (int i = position, length = string.length(); i < length; i++) {
			if (delimiters.indexOf(string.charAt(i), 0) >= 0) {
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

	@Override
	public boolean hasMoreElements() {
		return hasMoreTokens();
	}

	public boolean hasMoreTokens() {
		if (delimiters == null) {
			throw new NullPointerException("delimiters == null");
		}
		int length = string.length();
		if (position < length) {
			if (returnDelimiters) {
				return true;
			}
			for (int i = position; i < length; i++) {
				if (delimiters.indexOf(string.charAt(i), 0) == -1) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Object nextElement() {
		return nextToken();
	}

	public String nextToken() {
		if (delimiters == null) {
			throw new NullPointerException("delimiters == null");
		}
		int i = position;
		int length = string.length();

		if (i < length) {
			if (returnDelimiters) {
				if (delimiters.indexOf(string.charAt(position), 0) >= 0)
					return String.valueOf(string.charAt(position++));
				for (position++; position < length; position++)
					if (delimiters.indexOf(string.charAt(position), 0) >= 0)
						return string.substring(i, position);
				return string.substring(i);
			}

			while (i < length && delimiters.indexOf(string.charAt(i), 0) >= 0) {
				i++;
			}
			position = i;
			if (i < length) {
				for (position++; position < length; position++) {
					if (delimiters.indexOf(string.charAt(position), 0) >= 0) {
						return string.substring(i, position);
					}
				}
				return string.substring(i);
			}
		}
		throw new NoSuchElementException();
	}

	public String nextToken(String delims) {
		this.delimiters = delims;
		return nextToken();
	}

}
