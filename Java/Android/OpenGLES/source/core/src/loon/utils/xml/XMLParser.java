package loon.utils.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Stack;

import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.resource.Resources;

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
// 这是一个简易的XML解析器，自0.3.2起开始支持(为了多平台移植方便……)
public class XMLParser implements LRelease {

	static final int OPEN_TAG = 0;

	static final int CLOSE_TAG = 1;

	static final int OPEN_CLOSE_TAG = 2;

	private Stack<XMLElement> stack = new Stack<XMLElement>();

	private XMLElement topElement;

	private XMLElement rootElement;

	private StringBuffer header = new StringBuffer(1024);

	private void pushElement(XMLElement root, int idx, XMLListener l) {
		if (this.topElement == null) {
			this.rootElement = root;
		} else {
			this.topElement.addContents(root);
		}
		this.stack.push(root);
		this.topElement = root;
		
		if (l != null) {
			l.addElement(idx, root);
		}
	}

	private void popElement(int idx, XMLListener l) {
		if (l != null) {
			l.endElement(idx, this.topElement);
		}
		this.stack.pop();
		if (stack.size() > 0) {
			this.topElement = (this.stack.peek());
		} else {
			this.topElement = null;
		}
	}

	private void newElement(String context, XMLListener l, int index) {
		String o = "";
		int i;
		String str1;
		if (context.endsWith("/>")) {
			i = 2;
			str1 = context.substring(1, context.length() - 2);
		} else if (context.startsWith("</")) {
			i = 1;
			str1 = context.substring(2, context.length() - 1);
		} else {
			i = 0;
			str1 = context.substring(1, context.length() - 1);
		}
		try {
			if (str1.indexOf(' ') < 0) {
				o = str1;
				switch (i) {
				case OPEN_TAG:
					pushElement(new XMLElement(o), index, l);
					break;
				case CLOSE_TAG:
					if (this.topElement.getName().equals(o)) {
						popElement(index, l);
					} else {
						throw new RuntimeException("Expected close of '"
								+ this.topElement.getName() + "' instead of "
								+ context);
					}
					break;
				case OPEN_CLOSE_TAG:
					pushElement(new XMLElement(o), index, l);
					popElement(index, l);
					break;
				}
			} else {
				XMLElement el = null;
				o = str1.substring(0, str1.indexOf(' '));
				switch (i) {
				case OPEN_TAG:
					el = new XMLElement(o);
					pushElement(el, index, l);
					break;
				case CLOSE_TAG:
					throw new RuntimeException("Syntax Error: " + context);
				case OPEN_CLOSE_TAG:
					el = new XMLElement(o);
					pushElement(el, index, l);
					popElement(index, l);
					break;
				}
				String str2 = str1.substring(str1.indexOf(' ') + 1);
				int start = 0;
				int end = 0;

				StringBuffer sbr1 = new StringBuffer(128);
				StringBuffer sbr2 = new StringBuffer(32);
				for (int m = 0; m < str2.length(); m++) {

					switch (str2.charAt(m)) {
					case '"':

						start = start != 0 ? 0 : 1;
						break;
					case ' ':
						if ((end == 1) && (start == 1)) {
							sbr1.append(str2.charAt(m));
						} else if (sbr2.length() > 0) {
							String key = sbr2.toString();
							String value = sbr1.toString();
							if (key.length() > 0) {
								XMLAttribute a = el.addAttribute(key, value);
								a.element = el;
								if (l != null) {
									l.addAttribute(index, a);
								}
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
							sbr1.append(str2.charAt(m));
						} else {
							sbr2.append(str2.charAt(m));
						}
					}

				}
				if (sbr1.length() > 0) {
					String key = sbr2.toString();
					String value = sbr1.toString();
					XMLAttribute a = el.addAttribute(key, value);
					a.element = el;
					if (l != null) {
						l.addAttribute(index, a);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot parse element '" + context
					+ "' - (" + e + ")");
		}
	}

	private void newData(String data, XMLListener l, int index) {
		if (this.topElement != null) {
			XMLData xdata = new XMLData(data);
			this.topElement.addContents(xdata);
			if (l != null) {
				l.addData(index, xdata);
			}
		} else if (this.rootElement == null) {
			this.header.append(data);
		}
	}

	private void newComment(String comment, XMLListener l, int index) {
		if (this.topElement != null) {
			XMLComment c = new XMLComment(comment.substring(4,
					comment.length() - 3));
			this.topElement.addContents(c);
			if (l != null) {
				l.addComment(index, c);
			}
		} else if (this.rootElement == null) {
			this.header.append(comment);
		}
	}

	private void newProcessing(String p, XMLListener l, int index) {
		if (this.topElement != null) {
			XMLProcessing xp = new XMLProcessing(p.substring(2, p.length() - 2));
			this.topElement.addContents(xp);
			if (l != null) {
				l.addHeader(index, xp);
			}
		} else if (this.rootElement == null) {
			this.header.append(p);
		}
	}

	private XMLDocument parseText(String text, XMLListener l) {
		int count = 0;
		for (XMLTokenizer tokenizer = new XMLTokenizer(text); tokenizer
				.hasMoreElements();) {
			String str = tokenizer.nextElement();
			if ((str.startsWith("<?")) && (str.endsWith("?>"))) {
				newProcessing(str, l, count);
			} else if ((str.startsWith("<!--")) && (str.endsWith("-->"))) {
				newComment(str, l, count);
			} else if (str.charAt(0) == '<') {
				newElement(str, l, count);
			} else {
				newData(str, l, count);
			}
			count++;
		}

		return new XMLDocument(this.header.toString(), this.rootElement);
	}

	public static XMLDocument parse(String file) {
		return parse(file, null);
	}

	public static XMLDocument parse(String file, XMLListener l) {
		try {
			return parse(Resources.openResource(file), l);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static XMLDocument parse(InputStream in) {
		return parse(in, null);
	}

	public static XMLDocument parse(InputStream in, XMLListener l) {
		StringBuffer sbr = new StringBuffer(10000);
		try {
			int i = 0;
			while (in.available() == 0) {
				i++;
				try {
					Thread.sleep(100L);
				} catch (Exception e) {
				}
				if (i <= 100) {
					continue;
				}
				throw new RuntimeException("Parser: InputStream timed out !");
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					in, LSystem.encoding));
			while (reader.ready()) {
				sbr.append(reader.readLine());
				sbr.append('\n');
			}
			reader.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new XMLParser().parseText(sbr.toString(), l);
	}

	@Override
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
