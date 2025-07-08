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
import loon.action.sprite.ScrollText;
import loon.action.sprite.SpriteLabel;
import loon.action.sprite.StatusBar;
import loon.component.LCheckBox;
import loon.component.LClickButton;
import loon.component.LLabel;
import loon.component.LMenuSelect;
import loon.component.LMessageBox;
import loon.component.LTextBar;
import loon.font.IFont;
import loon.geom.IV;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

public class StringNodeMaker<T extends ActionBind> implements IV<T> {

	public final static boolean isType(String typeName) {
		if (StringUtils.isNullOrEmpty(typeName)) {
			return false;
		}
		return toType(typeName) != StringNodeType.Unknown;
	}

	public final static StringNodeType toType(String typeName) {
		if (StringUtils.isNullOrEmpty(typeName)) {
			return StringNodeType.Unknown;
		}
		String tName = typeName.toLowerCase().trim();
		if (tName.equals("spritelabel") || tName.equals("sl")) {
			return StringNodeType.Sprite;
		} else if (tName.equals("label") || tName.equals("l")) {
			return StringNodeType.Label;
		} else if (tName.equals("textbar") || tName.equals("text")) {
			return StringNodeType.TextBar;
		} else if (tName.equals("checkbox") || tName.equals("check")) {
			return StringNodeType.Check;
		} else if (tName.equals("clickbutton") || tName.equals("click")
				|| (tName.equals("button") || (tName.equals("c")))) {
			return StringNodeType.Click;
		} else if (tName.equals("selectmenu") || tName.equals("menu")
				|| (tName.equals("select") || (tName.equals("m")))) {
			return StringNodeType.Select;
		} else if (tName.equals("scrolltext") || tName.equals("scroll")
				|| (tName.equals("st") || (tName.equals("stext")))) {
			return StringNodeType.ScrollText;
		} else if (tName.equals("status") || tName.equals("progress")) {
			return StringNodeType.Status;
		}
		return StringNodeType.Unknown;
	}

	public final static <T extends ActionBind> T create(String typeName, String text) {
		return create(typeName, text, 0f, 0f);
	}

	public final static <T extends ActionBind> T create(String typeName, String text, float x, float y) {
		return create(typeName, LSystem.getSystemGameFont(), text, x, y);
	}

	public final static <T extends ActionBind> T create(String typeName, IFont font, String text, float x, float y) {
		return create(toType(typeName), font, text, x, y, 0f, 0f);
	}

	public final static <T extends ActionBind> T create(String typeName, String text, float x, float y, float w,
			float h) {
		return new StringNodeMaker<T>(toType(typeName), LSystem.getSystemGameFont(), text, x, y, w, h).get();
	}

	public final static <T extends ActionBind> T create(String typeName, IFont font, String text, float x, float y,
			float w, float h) {
		return new StringNodeMaker<T>(toType(typeName), font, text, x, y, w, h).get();
	}

	public final static <T extends ActionBind> T create(StringNodeType nodeType, String text) {
		return create(nodeType, text, 0f, 0f);
	}

	public final static <T extends ActionBind> T create(StringNodeType nodeType, String text, float x, float y) {
		return create(nodeType, LSystem.getSystemGameFont(), text, x, y);
	}

	public final static <T extends ActionBind> T create(StringNodeType nodeType, IFont font, String text, float x,
			float y) {
		return create(nodeType, font, text, x, y, 0f, 0f);
	}

	public final static <T extends ActionBind> T create(StringNodeType nodeType, IFont font, String text, float x,
			float y, float w, float h) {
		return new StringNodeMaker<T>(nodeType, font, text, x, y, w, h).get();
	}

	private StringNodeType _nodeType;

	private T _value;

	@SuppressWarnings("unchecked")
	private StringNodeMaker(StringNodeType nodeType, IFont font, String text, float x, float y, float w, float h) {
		if (nodeType == null) {
			return;
		}
		this._nodeType = nodeType;
		switch (nodeType) {
		case Sprite:
			_value = (T) new SpriteLabel(font, text, x, y);
			break;
		default:
		case Label:
			_value = (T) LLabel.make(text, font, x, y);
			break;
		case TextBar:
			_value = (T) new LTextBar(font, text, x, y);
			break;
		case Check:
			_value = (T) LCheckBox.at(font, text, MathUtils.ifloor(x), MathUtils.ifloor(y));
			break;
		case Select:
			_value = (T) new LMenuSelect(font, StringUtils.split(text, LSystem.COMMA), x, y);
			break;
		case Click:
			_value = (T) LClickButton.make(font, text, MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w),
					MathUtils.ifloor(h));
			break;
		case Message:
			_value = (T) new LMessageBox(StringUtils.split(text, LSystem.COMMA), font, MathUtils.ifloor(x),
					MathUtils.ifloor(y), MathUtils.ifloor(w), MathUtils.ifloor(h));
			break;
		case ScrollText:
			_value = (T) new ScrollText(text, font, MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w),
					MathUtils.ifloor(h));
			break;
		case Status:
			StatusBar status = new StatusBar(font, MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w),
					MathUtils.ifloor(h));
			status.setText(text);
			_value = (T) status;
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
