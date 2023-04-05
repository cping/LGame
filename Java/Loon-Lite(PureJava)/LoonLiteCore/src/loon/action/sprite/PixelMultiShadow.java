/**
 * Copyright 2008 - 2023 The Loon Game Engine Authors
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
package loon.action.sprite;

import loon.opengl.GLEx;
import loon.utils.ObjectMap;

public class PixelMultiShadow implements ISpritesShadow {

	private ObjectMap<Object, ISpritesShadow> _shadowList;

	private boolean _closed;

	public PixelMultiShadow() {
		_shadowList = new ObjectMap<Object, ISpritesShadow>();
	}

	public PixelMultiShadow addShadow(ISprite e, ISpritesShadow shadow) {
		if (e == null) {
			return this;
		}
		if (shadow == null) {
			return this;
		}
		_shadowList.put(e, shadow);
		return this;
	}

	public PixelMultiShadow addShadow(Object tag, ISpritesShadow shadow) {
		if (tag == null) {
			return this;
		}
		if (shadow == null) {
			return this;
		}
		_shadowList.put(tag, shadow);
		return this;
	}

	public ISpritesShadow removeShadow(Object tag) {
		if (tag == null) {
			return null;
		}
		return _shadowList.remove(tag);
	}

	public ISpritesShadow removeShadow(ISprite e) {
		if (e == null) {
			return null;
		}
		return _shadowList.remove(e);
	}

	public ISpritesShadow getShadow(Object tag) {
		if (tag == null) {
			return null;
		}
		return _shadowList.get(tag);
	}

	public ISpritesShadow getShadow(ISprite e) {
		if (e == null) {
			return null;
		}
		return _shadowList.get(e);
	}

	@Override
	public ISpritesShadow drawShadow(GLEx g, ISprite e, float x, float y) {
		if (e == null) {
			return this;
		}
		ISpritesShadow shadow = _shadowList.get(e);
		if (shadow == null) {
			shadow = _shadowList.get(e.getTag());
		}
		if (shadow != null) {
			shadow.drawShadow(g, e, x, y);
		}
		return this;
	}

	@Override
	public void close() {
		if (_closed) {
			return;
		}
		for (ISpritesShadow shadow : _shadowList.values()) {
			if (shadow != null) {
				shadow.close();
			}
		}
		_shadowList.clear();
		_closed = true;
	}

	@Override
	public boolean isClosed() {
		return _closed;
	}

}
