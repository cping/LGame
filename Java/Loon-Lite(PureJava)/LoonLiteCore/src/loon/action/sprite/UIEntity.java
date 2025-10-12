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
package loon.action.sprite;

import loon.LSystem;
import loon.component.LComponent;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 转化UI组件为Sprite组件
 */
public class UIEntity extends Draw {

	private LComponent _uicomponet;

	public UIEntity(LComponent c) {
		this._uicomponet = c;
	}

	public LComponent getUIComponent() {
		return this._uicomponet;
	}

	@Override
	void onProcess(long elapsedTime) {
		if (_uicomponet != null) {
			_uicomponet.update(elapsedTime);
		}
	}

	@Override
	public void draw(GLEx g, float offsetX, float offsetY) {
		if (_uicomponet != null) {
			_uicomponet.createUI(g, MathUtils.ifloor(offsetX), MathUtils.ifloor(offsetY));
		}
	}

	public String getUIName() {
		return "UIEntity:" + _uicomponet == null ? LSystem.UNKNOWN : _uicomponet.getName();
	}

	@Override
	public void _onDestroy() {
		super._onDestroy();
		if (_uicomponet != null) {
			_uicomponet.close();
		}
	}
}
