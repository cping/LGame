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

import loon.LSystem;
import loon.canvas.LColor;
import loon.utils.StringKeyValue;

public class HtmlFont {

	protected String face;

	protected float size;

	protected LColor color;

	protected String text;

	protected HtmlFont(HtmlElement ele) {
		this.face = ele.getAttribute("face", LSystem.UNKNOWN);
		this.size = ele.getFloatAttribute("size", 2.0f);
		String colorStr = ele.getAttribute("color", null);
		if (colorStr == null) {
			this.color = LColor.white;
		} else {
			this.color = LColor.decode(colorStr);
		}
		text = ele.getData();
	}

	public String getFace() {
		return face;
	}

	public float getSize() {
		return size;
	}

	public LColor getColor() {
		return color;
	}

	public String getText() {
		return text;
	}

	public String toString() {
		StringKeyValue builder = new StringKeyValue("HtmlFont");
		builder.kv("face", face).comma().kv("size", size).comma().kv("text", text).comma().kv("color", color);
		return builder.toString();
	}

}
