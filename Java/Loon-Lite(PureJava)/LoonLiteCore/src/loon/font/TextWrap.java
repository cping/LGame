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
package loon.font;

public final class TextWrap {

	public static final TextWrap MANUAL = new TextWrap(Float.MAX_VALUE);

	public final float width;

	public final float indent;

	public TextWrap(float width) {
		this(width, 0);
	}

	public TextWrap(float width, float indent) {
		this.width = width;
		this.indent = indent;
	}

	@Override
	public int hashCode() {
		return (int) width ^ (int) indent;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof TextWrap) {
			TextWrap ow = (TextWrap) other;
			return width == ow.width && indent == ow.indent;
		} else {
			return false;
		}
	}
}
