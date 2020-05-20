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
package loon.utils.html;

import loon.BaseIO;
import loon.LSysException;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.xml.XMLTokenizer;

/**
 * 自带的html解析用类
 */
public class HtmlParser {

	public static HtmlElement parse(String path) {
		return new HtmlParser().parseText(BaseIO.loadText(path));
	}

	public static HtmlElement loadText(String text) {
		return new HtmlParser().parseText(text);
	}

	private TArray<HtmlElement> stack = new TArray<HtmlElement>();

	private HtmlElement topElement;

	private HtmlElement rootElement;

	private HtmlParser() {
	}

	public HtmlElement parseText(String text) {
		int count = 0;
		for (XMLTokenizer tokenizer = new XMLTokenizer(text); tokenizer.hasMoreElements();) {
			String str = tokenizer.nextElement();
			String data = null;
			if ((str.startsWith("<!--")) && (str.endsWith("-->"))) {
				data = str;
				if (topElement != null) {
					topElement.addData(data);
				}
			} else if (str.charAt(0) == '<') {
				newElement(str, count);
			} else {
				data = str;
				if (topElement != null) {
					topElement.addData(data);
				}
			}
			count++;
		}
		return rootElement;
	}

	private void pushElement(HtmlElement root, int idx) {
		if (this.topElement == null) {
			this.rootElement = root;
		} else {
			this.topElement.addContents(root);
		}
		this.stack.add(root);
		this.topElement = root;
	}

	private void popElement(int idx) {
		this.stack.pop();
		if (stack.size() > 0) {
			this.topElement = this.stack.peek();
		} else {
			this.topElement = null;
		}
	}

	private void newElement(String context, int idx) {
		int i;
		String name;
		if (context.endsWith("/>")) {
			i = 2;
			name = context.substring(1, context.length() - 2);
		} else if (context.startsWith("</")) {
			i = 1;
			name = context.substring(2, context.length() - 1);
		} else {
			i = 0;
			name = context.substring(1, context.length() - 1);
		}
		name = name.trim();
		if (!StringUtils.isEmpty(name) && !name.startsWith("!")) {
			if (name.indexOf(' ') < 0) {
				switch (i) {
				case 0:
					pushElement(new HtmlElement(name), idx);
					break;
				case 1:
					if (this.topElement.getName().equals(name)) {
						popElement(idx);
					}
					break;
				case 2:
					pushElement(new HtmlElement(name), idx);
					popElement(idx);
					break;
				}
			} else {
				HtmlElement el = null;
				String newName = name.substring(0, name.indexOf(' ')).trim();
				switch (i) {
				case 0:
					el = new HtmlElement(newName);
					pushElement(el, idx);
					break;
				case 1:
					throw new LSysException("Syntax Error: " + context);
				case 2:
					el = new HtmlElement(newName);
					pushElement(el, idx);
					popElement(idx);
					break;
				}
				String text = name.substring(name.indexOf(' ') + 1).trim();
				int start = 0;
				int end = 0;

				StrBuilder sbr1 = new StrBuilder(128);
				StrBuilder sbr2 = new StrBuilder(32);
				for (int m = 0; m < text.length(); m++) {

					switch (text.charAt(m)) {
					case '"':

						start = start != 0 ? 0 : 1;
						break;
					case ' ':
						if ((end == 1) && (start == 1)) {
							sbr1.append(text.charAt(m));
						} else if (sbr2.length() > 0) {
							String key = sbr2.toString();
							String value = sbr1.toString();
							if (key.length() > 0) {
								HtmlAttribute a = el.addAttribute(key, value);
								a.element = el;
							}
							end = 0;
							sbr1.delete(0, sbr1.length());
							sbr2.delete(0, sbr2.length());
						}
						break;
					case '=':
						if (start == 0) {
							end = 1;
						}
						break;
					case '!':
					case '#':
					case '$':
					case '%':
					case '&':
					case '\'':
					case '(':
					case ')':
					case '*':
					case '+':
					case ',':
					case '-':
					case '.':
					case '/':
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
					case ':':
					case ';':
					case '<':
					default:
						if (end != 0) {
							sbr1.append(text.charAt(m));
						} else {
							sbr2.append(text.charAt(m));
						}
					}

				}
				if (sbr1.length() > 0) {
					String key = sbr2.toString();
					String value = sbr1.toString();
					HtmlAttribute a = el.addAttribute(key, value);
					a.element = el;
				}
			}
		}
	}

	public HtmlElement root() {
		return rootElement;
	}

	public void dispose() {
		if (stack != null) {
			stack.clear();
			stack = null;
		}
		if (topElement != null) {
			topElement.dispose();
			topElement = null;
		}
		if (rootElement != null) {
			rootElement.dispose();
			rootElement = null;
		}
	}

}
