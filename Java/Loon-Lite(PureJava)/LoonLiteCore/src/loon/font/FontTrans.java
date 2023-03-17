/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
package loon.font;

public class FontTrans {

	protected ITranslator _translator;

	protected boolean checkEndIndexUpdate(int endIndex, String src, String dst) {
		if (src == null || dst == null) {
			return false;
		}
		return src.length() == endIndex && src.length() != dst.length();
	}

	protected String toMessage(String msg) {
		if (this._translator == null) {
			return msg;
		}
		if (_translator.isAllow()) {
			return this._translator.toTanslation(msg, msg);
		}
		return msg;
	}

}
