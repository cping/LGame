/**
 * Copyright 2013 The Loon Authors
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
package loon.foundation;

import java.io.UnsupportedEncodingException;

import loon.utils.FileUtils;

public class NSString extends NSObject {

	protected String content = null;

	NSString() {
		this.content = "";
	}

	NSString(String s) {
		this.content = s;
	}

	NSString(String string, Object... args) {
		this.content = String.format(string, args);
	}

	NSString(byte[] bytes, String encoding) throws UnsupportedEncodingException {
		content = new String(bytes, encoding);
	}

	public static NSString withString(String string) {
		return new NSString(string);
	}
	
	public static NSString string() {
		return new NSString();
	}

	public static String stringWithFormat(String format, Object... args) {
		return String.format(format, args);
	}

	public String stringByAppendingFormat(String string, String format,
			Object... args) {
		return string + String.format(format, args);
	}

	public String stringByAppendingString(String string) {
		return this.content + string;
	}

	public String subStringFromIndex(int start) {
		return this.content.substring(start);
	}

	public String subStringToIndex(int end) {
		int start = 0;
		return this.content.substring(start, end);
	}

	public int length() {
		return this.content.length();
	}

	public String getString() {
		return this.content;
	}

	public void setString(String string) {
		this.content = string;
	}

	public boolean isEqualToString(NSString string) {
		return this.content.equals(string.getString());
	}

	public char characterAtIndex(int index) {
		return this.content.charAt(index);
	}

	public String substringWithRange(NSRange range) {
		return this.content.substring(range.start, range.end);
	}

	public NSArray componentsSeparatedByString(String string) {
		String c[] = this.content.split(string);
		int size = c.length;
		NSString[] strings = new NSString[size];
		for (int i = 0; i < size; i++) {
			strings[i] = new NSString(c[i]);
		}
		NSArray array = NSArray.arrayWithObjects(strings);
		return array;
	}

	public String stringByReplacingCharactersInRange(NSRange range,
			String string) {
		int length = this.length();
		String stringBegin = this.content.substring(0, range.start);
		String stringEnd = this.content.substring(range.end + 1, length);
		return stringBegin + string + stringEnd;
	}

	public String pathExtension() {
		return FileUtils.getExtension(content);
	}

	public String lastPathComponent() {
		int start = -1, end = -1;
		for (int i = content.length() - 1; i >= 0; i--) {
			if (end == -1) {
				if (content.charAt(i) != '/') {
					end = i;
				}
			} else {
				if (content.charAt(i) == '/') {
					start = i;
					break;
				}
			}
		}
		if (end == -1) {
			return "/";
		}
		return content.substring(start + 1, end + 1);
	}

	public NSArray pathComponents() {
		NSMutableArray pathArray = NSMutableArray.array();
		String string;
		int start = 0, end = 0;
		for (int i = 0; i < content.length(); i++) {
			if (content.charAt(i) == '/' || i == length() - 1) {
				end = i;
				string = content.substring(start, end + 1);
				if (string.equals("/")) {
					if (start == 0 || end == content.length() - 1) {
						pathArray.addObject(new NSString("/"));
					}
				} else {
					pathArray.addObject(new NSString(string));
				}
				start = i + 1;
			}
		}
		return pathArray;
	}

	public String stringByAppendingPathComponent(String string) {
		String receive = this.getString();
		if (receive.equals("")) {
			return string;
		} else {
			int index = this.content.length();
			int c = this.content.lastIndexOf('/');
			if (c == index - 1) {
				receive = this.content + string;
			} else {
				receive = this.content + '/' + string;
			}
			return receive;
		}
	}

	public int intValue() {
		return Integer.parseInt(this.content);
	}

	public Float floatValue() {
		return Float.parseFloat(this.content);
	}

	public Double doubleValue() {
		return Double.parseDouble(this.content);
	}

	public boolean booleanValue() {
		return Integer.parseInt(this.content) != 0;
	}

	@Override
	public String toString() {
		return this.content;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof NSString) {
			return ((NSString) o).content.equals(content);
		}
		return content.equals(o);
	}

	@Override
	public int hashCode() {
		return content.hashCode();
	}

	@Override
	protected void addSequence(StringBuilder sbr, String indent) {
		sbr.append(indent);
		sbr.append("<string>");
		sbr.append(content);
		sbr.append("</string>");
	}

}
