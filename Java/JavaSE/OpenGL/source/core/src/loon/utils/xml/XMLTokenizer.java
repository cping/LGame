package loon.utils.xml;

/**
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
public class XMLTokenizer {

	private String text;

	private int pointer;

	public XMLTokenizer(String text) {
		this.text = text;
	}

	public boolean hasMoreElements() {
		return this.pointer < this.text.length();
	}

	public String nextElement() {
		if (this.text.charAt(this.pointer) == '<') {
			return nextTag();
		}
		return nextString();
	}

	private String nextTag() {
		int i = 0;
		int j = this.pointer;
		do {
			switch (this.text.charAt(this.pointer)) {
			case '"':
				i = i != 0 ? 0 : 1;
			}
			this.pointer += 1;
		} while ((this.pointer < this.text.length())
				&& ((this.text.charAt(this.pointer) != '>') || (i != 0)));
		if (this.pointer < this.text.length()) {
			this.pointer += 1;
		} else {
			throw new RuntimeException(
					"Tokenizer error: < without > at end of text");
		}
		return this.text.substring(j, this.pointer);
	}

	private String nextString() {
		int i = 0;
		int j = this.pointer;
		do {
			switch (this.text.charAt(this.pointer)) {
			case '"':
				i = i != 0 ? 0 : 1;
			}
			this.pointer += 1;
		} while ((this.pointer < this.text.length())
				&& ((this.text.charAt(this.pointer) != '<') || (i != 0)));
		return this.text.substring(j, this.pointer);
	}

}
