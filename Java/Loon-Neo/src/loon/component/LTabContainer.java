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
package loon.component;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.events.GameKey;
import loon.opengl.GLEx;

public class LTabContainer extends LContainer {

	protected int _curTabIndex = 0;

	private LComponent _mainComp = null;

	private boolean _active;

	public LTabContainer(LTexture background, int x, int y) {
		this(background, x, y, background == null ? 0 : background.getWidth(),
				background == null ? 0 : background.getHeight());
	}

	public LTabContainer(LTexture background, int x, int y, int w, int h) {
		this(background, null, x, y, w, h);
	}

	public LTabContainer(LTexture background, LColor color, int x, int y, int w, int h) {
		super(x, y, w, h);
		if (background != null && (color == null || LColor.white.equals(color))) {
			this.setBackground(background);
		} else if (background == null && color != null) {
			this.setBackground(color);
		} else {
			this.setBackground(LColor.black);
		}
		this._active = true;
	}

	public LTabContainer(LTexture background) {
		this(background, 0, 0);
	}

	public LTabContainer(LColor color) {
		this((LTexture) null, color, 0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public LTabContainer(String fileName, int x, int y) {
		this(LSystem.loadTexture(fileName), x, y);
	}

	public LTabContainer(String fileName) {
		this(fileName, 0, 0);
	}

	public LTabContainer(int x, int y, int w, int h) {
		this((LTexture) null, x, y, w, h);
	}

	public LTabContainer(LColor color, int x, int y, int w, int h) {
		this((LTexture) null, color, x, y, w, h);
	}

	public LComponent setCurTab(int i) {
		if (!_active) {
			return (_mainComp != null) ? _mainComp : getCurTab();
		}
		if (i == _curTabIndex) {
			return getCurTab();
		}
		if (getComponentCount() == 0) {
			_curTabIndex = i;
			return getCurTab();
		}
		if (i < 0) {
			_curTabIndex = 0;
		} else if (i >= getComponentCount()) {
			_curTabIndex = getComponentCount() - 1;
		} else {
			_curTabIndex = i;
		}
		return this;
	}

	public int getCurTabIndex() {
		return this._curTabIndex;
	}

	public LComponent getCurTab() {
		if (_curTabIndex < 0 || _curTabIndex >= getComponentCount()) {
			return null;
		}
		return _childs[getComponentCount() - _curTabIndex - 1];
	}

	public LComponent nextTab() {
		return setCurTab(_curTabIndex + 1);
	}

	public LComponent prevTab() {
		return setCurTab(_curTabIndex - 1);
	}

	public LComponent beginTab() {
		return setCurTab(0);
	}

	public LComponent endTab() {
		return setCurTab(getComponentCount() - 1);
	}

	public LTabContainer setActive(boolean a) {
		this._active = a;
		return this;
	}

	public boolean isActive() {
		return this._active;
	}

	@Override
	protected LComponent findComponentChecked(LComponent comp) {
		if (getCurTab() == comp || comp == this) {
			return comp;
		}
		return null;
	}

	@Override
	public void createUI(GLEx g, int x, int y) {

	}

	@Override
	protected void renderComponents(GLEx g) {
		if (_component_isClose) {
			return;
		}
		LComponent node = getCurTab();
		if (node != null) {
			node.createUI(g);
		}
	}

	@Override
	public void keyPressed(GameKey key) {
		if (_component_isClose) {
			return;
		}
		super.keyPressed(key);
		LComponent node = getCurTab();
		if (node != null) {
			node.keyPressed(key);
		}
	}

	@Override
	public void keyReleased(GameKey key) {
		if (_component_isClose) {
			return;
		}
		super.keyReleased(key);
		LComponent node = getCurTab();
		if (node != null) {
			node.keyReleased(key);
		}
	}

	@Override
	protected void processResize() {
		if (_component_isClose) {
			return;
		}
		LComponent node = getCurTab();
		if (node != null) {
			node.processResize();
		}
	}

	public LComponent getMainComponent() {
		return _mainComp;
	}

	public LTabContainer setMainComponent(LComponent mc) {
		this._mainComp = mc;
		return this;
	}

	@Override
	public String getUIName() {
		return "Tab";
	}

	@Override
	public void destory() {

	}

}
