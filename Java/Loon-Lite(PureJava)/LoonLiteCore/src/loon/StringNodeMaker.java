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
package loon;

import loon.action.ActionBind;
import loon.action.sprite.SpriteLabel;
import loon.component.LLabel;
import loon.component.LTextBar;
import loon.font.IFont;
import loon.geom.IV;
import loon.utils.StringUtils;

public class StringNodeMaker<T extends ActionBind> implements IV<T> {

	public final static boolean isType(String typeName) {
		if (StringUtils.isNullOrEmpty(typeName)) {
			return false;
		}
		String tName = typeName.toLowerCase().trim();
		boolean result = false;
		if (tName.equals("spritelabel") || tName.equals("sl")) {
			result = true;
		} else if (tName.equals("label") || tName.equals("l")) {
			result = true;
		} else if (tName.equals("textbar") || tName.equals("text")) {
			result = true;
		}
		return result;
	}

	public final static StringNodeType toType(String typeName) {
		if (StringUtils.isNullOrEmpty(typeName)) {
			return StringNodeType.Label;
		}
		String tName = typeName.toLowerCase().trim();
		if (tName.equals("spritelabel") || tName.equals("sl")) {
			return StringNodeType.Sprite;
		} else if (tName.equals("label") || tName.equals("l")) {
			return StringNodeType.Label;
		} else if (tName.equals("textbar") || tName.equals("text")) {
			return StringNodeType.TextBar;
		}
		return StringNodeType.Label;
	}

	public final static <T extends ActionBind> T create(String typeName, String text) {
		return create(typeName, text, 0f, 0f);
	}

	public final static <T extends ActionBind> T create(String typeName, String text, float x, float y) {
		return create(typeName, LSystem.getSystemGameFont(), text, x, y);
	}

	public final static <T extends ActionBind> T create(String typeName, IFont font, String text, float x, float y) {
		return new StringNodeMaker<T>(toType(typeName), font, text, x, y).get();
	}

	public final static <T extends ActionBind> T create(StringNodeType nodeType, String text) {
		return create(nodeType, text, 0f, 0f);
	}

	public final static <T extends ActionBind> T create(StringNodeType nodeType, String text, float x, float y) {
		return create(nodeType, LSystem.getSystemGameFont(), text, x, y);
	}

	public final static <T extends ActionBind> T create(StringNodeType nodeType, IFont font, String text, float x,
			float y) {
		return new StringNodeMaker<T>(nodeType, font, text, x, y).get();
	}

	private StringNodeType _nodeType;

	private T _value;

	@SuppressWarnings("unchecked")
	private StringNodeMaker(StringNodeType nodeType, IFont font, String text, float x, float y) {
		if (nodeType == null) {
			return;
		}
		this._nodeType = nodeType;
		switch (nodeType) {
		case Sprite:
			_value = (T) new SpriteLabel(font, text, x, y);
			break;
		case Label:
			_value = (T) LLabel.make(text, font, x, y);
			break;
		case TextBar:
			_value = (T) new LTextBar(font, text, x, y);
			break;
		}
	}

	public StringNodeType getNodeType() {
		return _nodeType;
	}

	@Override
	public T get() {
		return _value;
	}

}
