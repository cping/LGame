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
import loon.action.sprite.Entity;
import loon.action.sprite.Picture;
import loon.action.sprite.Sprite;
import loon.component.Actor;
import loon.component.LButton;
import loon.component.LPaper;
import loon.geom.IV;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

public class TextureNodeMaker<T extends ActionBind> implements IV<T> {

	public final static <T extends ActionBind> T create(TextureNodeType nodeType, String path) {
		return create(nodeType, path, 0f, 0f);
	}

	public final static <T extends ActionBind> T create(TextureNodeType nodeType, String path, float x, float y) {
		return create(nodeType, StringUtils.isEmpty(path) ? (LTexture) null : LTextures.loadTexture(path), x, y);
	}

	public final static <T extends ActionBind> T create(TextureNodeType nodeType, LTexture texture) {
		return create(nodeType, texture, 0f, 0f);
	}

	public final static <T extends ActionBind> T create(TextureNodeType nodeType, LTexture texture, float x, float y) {
		return new TextureNodeMaker<T>(nodeType, texture, x, y).get();
	}

	private TextureNodeType _nodeType;

	private T _value;

	@SuppressWarnings("unchecked")
	private TextureNodeMaker(TextureNodeType nodeType, LTexture texture, float x, float y) {
		if (nodeType == null) {
			return;
		}
		this._nodeType = nodeType;
		switch (_nodeType) {
		case Entity:
		default:
			_value = (T) new Entity(texture, x, y);
			break;
		case Sprite:
			_value = (T) new Sprite(texture, x, y);
			break;
		case Picture:
			_value = (T) new Picture(texture, x, y);
			break;
		case Actor:
			_value = (T) new Actor(texture, x, y);
			break;
		case Paper:
			_value = (T) new LPaper(texture, x, y);
			break;
		case Button:
			_value = (T) new LButton(texture, MathUtils.ifloor(x), MathUtils.ifloor(y));
		}
	}

	public TextureNodeType getNodeType() {
		return _nodeType;
	}

	@Override
	public T get() {
		return _value;
	}

}
