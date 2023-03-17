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
package loon.utils.json;

import loon.LSystem;

public final class JsonParserException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final int linePos;
	private final int charPos;
	private final int charOffset;

	public JsonParserException(Exception e, String message, int linePos, int charPos, int charOffset) {
		super(message, e);
		LSystem.error(message, e);
		this.linePos = linePos;
		this.charPos = charPos;
		this.charOffset = charOffset;
	}

	public int getLinePosition() {
		return linePos;
	}

	public int getCharPosition() {
		return charPos;
	}

	public int getCharOffset() {
		return charOffset;
	}
}
