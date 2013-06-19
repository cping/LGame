/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.utils.json;

import loon.utils.MathUtils;

public class JSONParser {

	public static final String COMMA = ",";

	public static final String COLON = ":";

	public static final String QUOT = "\"";

	public static final String LEFT_BRACE = "{";

	public static final String RIGHT_BRACE = "}";

	public static final String TRUE = "true";

	public static final String FALSE = "false";

	public static final String BLANK = " ";

	private static final String flag_res = "{}\":,";

	private enum State {
		START, KEY, VALUE, END
	};

	public static final JSONObject read(String input) {
		return read(input.toCharArray());
	}

	public static final JSONObject read(char[] input) {
		JSONTokenizer tokenizer = new JSONTokenizer(input, flag_res, BLANK,
				true);
		return parseJSONObject(tokenizer);
	}

	public static final JSONObject readFile(String path) throws Exception {
		JSONTokenizer tokenizer = new JSONTokenizer(null, flag_res, BLANK, true);
		tokenizer.read(path);
		return parseJSONObject(tokenizer);
	}

	private static final JSONObject parseJSONObject(JSONTokenizer tokenizer) {
		JSONObject currentObject = null;
		String currentKey = null;
		Object currentValue = null;
		boolean isNumber = true, isComma = false;
		State state = State.START;
		for (; tokenizer.hasMoreTokens();) {
			String token = tokenizer.nextToken();
			if (LEFT_BRACE.equals(token)) {
				if (state == State.START) {
					currentObject = new JSONObject();
					state = State.KEY;
					isComma = true;
				} else if (state == State.VALUE) {
					tokenizer.putBackToken(token);
					currentValue = parseJSONObject(tokenizer);
					isNumber = false;
					isComma = false;
				} else {
					throw new RuntimeException("expected JSON Object after {");
				}
			} else if (RIGHT_BRACE.equals(token)) {
				if (currentKey != null && currentValue != null) {
					currentObject.put(currentKey, currentValue);
					isComma = false;
				}
				state = State.END;
				return currentObject;
			} else if (QUOT.equals(token)) {
				if (state == State.VALUE) {
					isNumber = false;
				}
				isComma = false;
			} else if (COLON.equals(token)) {
				if (state == State.KEY) {
					state = State.VALUE;
				} else {
					throw new RuntimeException("expected key before :");
				}
			} else if (COMMA.equals(token)) {
				isComma = false;
				if (state == State.VALUE) {
					if (currentKey != null && currentValue != null) {
						currentObject.put(currentKey, currentValue);
					} else {
						throw new RuntimeException("missing key or value");
					}
					state = State.KEY;
				} else {
					throw new RuntimeException("unexpected ,");
				}
			} else {
				if (isComma) {
					if (state == State.KEY) {
						currentKey = token;
					} else if (state == State.VALUE) {
						currentValue = token;
						isComma = false;
						isNumber = false;
					}
				} else if (state == State.KEY) {
					currentKey = token;
				} else if (state == State.VALUE) {
					if (isNumber) {
						if (TRUE.equalsIgnoreCase(token)
								|| FALSE.equalsIgnoreCase(token)) {
							currentValue = Boolean.parseBoolean(token);
						} else if (MathUtils.isNan(token)) {
							try {
								currentValue = Float.parseFloat(token);
							} catch (NumberFormatException e) {
								throw new RuntimeException(
										"non number or non boolean value must be enclosed within [\"]");
							}
						}
					} else {
						currentValue = token;
					}
					isNumber = true;
				}
			}
		}
		if (state != State.END) {
			throw new RuntimeException("missing }");
		}

		return currentObject;
	}

	/*public static void main(String[] args) {
		String test = "{\"action\":0,\"resource\":\"/motor/A\",\"content\":{\"speed\":360, \"power\":50}}";
		try { // JSONObject obj = JSONParser.readFile("assets/skin.json");
			JSONObject obj = JSONParser.read(test);
			System.out.println(obj);
			return;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}*/

}
