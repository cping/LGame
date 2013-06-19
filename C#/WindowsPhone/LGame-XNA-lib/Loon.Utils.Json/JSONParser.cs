/// <summary>
/// Copyright 2008 - 2012
/// Licensed under the Apache License, Version 2.0 (the "License"); you may not
/// use this file except in compliance with the License. You may obtain a copy of
/// the License at
/// http://www.apache.org/licenses/LICENSE-2.0
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
/// WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
/// License for the specific language governing permissions and limitations under
/// the License.
/// </summary>
///
/// @project loon
/// @email javachenpeng@yahoo.com

namespace Loon.Utils.Json {

    using System;
    using Loon.Java;

	public class JSONParser {
	
		public const string COMMA = ",";
	
		public const string COLON = ":";
	
		public const string QUOT = "\"";
	
		public const string LEFT_BRACE = "{";
	
		public const string RIGHT_BRACE = "}";
	
		public const string TRUE = "true";
	
		public const string FALSE = "false";
	
		public const string BLANK = " ";
	
		private const string flag_res = "{}\":,";
	
		public enum State {
			START, KEY, VALUE, END
		} 
	
		public static JSONObject Read(string input) {
			return Read(input.ToCharArray());
		}
	
		public static JSONObject Read(char[] input) {
			JSONTokenizer tokenizer = new JSONTokenizer(input, flag_res, BLANK,
					true);
			return ParseJSONObject(tokenizer);
		}
	
		public static JSONObject ReadFile(string path) {
			JSONTokenizer tokenizer = new JSONTokenizer(null, flag_res, BLANK, true);
			tokenizer.Read(path);
			return ParseJSONObject(tokenizer);
		}
	
		private static JSONObject ParseJSONObject(JSONTokenizer tokenizer) {
			JSONObject currentObject = null;
			string currentKey = null;
			object currentValue = null;
			bool isNumber = true, isComma = false;
			State state = JSONParser.State.START;
			for (; tokenizer.HasMoreTokens();) {
				string token = tokenizer.NextToken();
				if (LEFT_BRACE.Equals(token)) {
					if (state == JSONParser.State.START) {
						currentObject = new JSONObject();
						state = JSONParser.State.KEY;
						isComma = true;
					} else if (state == JSONParser.State.VALUE) {
						tokenizer.PutBackToken(token);
						currentValue = ParseJSONObject(tokenizer);
						isNumber = false;
						isComma = false;
					} else {
						throw new System.Exception("expected JSON object after {");
					}
				} else if (RIGHT_BRACE.Equals(token)) {
					if (currentKey != null && currentValue != null) {
						currentObject.Put(currentKey, currentValue);
						isComma = false;
					}
					state = JSONParser.State.END;
					return currentObject;
				} else if (QUOT.Equals(token)) {
					if (state == JSONParser.State.VALUE) {
						isNumber = false;
					}
					isComma = false;
				} else if (COLON.Equals(token)) {
					if (state == JSONParser.State.KEY) {
						state = JSONParser.State.VALUE;
					} else {
						throw new Exception("expected key before :");
					}
				} else if (COMMA.Equals(token)) {
					isComma = false;
					if (state == JSONParser.State.VALUE) {
						if (currentKey != null && currentValue != null) {
							currentObject.Put(currentKey, currentValue);
						} else {
							throw new Exception("missing key or value");
						}
						state = JSONParser.State.KEY;
					} else {
						throw new Exception("unexpected ,");
					}
				} else {
					if (isComma) {
						if (state == JSONParser.State.KEY) {
							currentKey = token;
						} else if (state == JSONParser.State.VALUE) {
							currentValue = token;
							isComma = false;
							isNumber = false;
						}
					} else if (state == JSONParser.State.KEY) {
						currentKey = token;
					} else if (state == JSONParser.State.VALUE) {
						if (isNumber) {
							if (TRUE.Equals(token,StringComparison.InvariantCultureIgnoreCase)
									|| FALSE.Equals(token,StringComparison.InvariantCultureIgnoreCase)) {
								currentValue = Boolean.Parse(token);
							} else if (MathUtils.IsNan(token)) {
								try {
									currentValue = Single.Parse(token,JavaRuntime.NumberFormat);
								} catch (FormatException) {
									throw new Exception(
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
			if (state != JSONParser.State.END) {
				throw new Exception("missing }");
			}
	
			return currentObject;
		}
	
		/*public static void main(string[] args) {
			string test = "{\"action\":0,\"resource\":\"/motor/A\",\"content\":{\"speed\":360, \"power\":50}}";
			try { // JSONObject obj = JSONParser.readFile("assets/skin.json");
				JSONObject obj = JSONParser.read(test);
				System.out.println(obj);
				return;
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}*/
	
	}
}
