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
package loon.component.layout;

import loon.LSystem;
import loon.component.LComponent;
import loon.utils.TArray;

public class LayoutStyles {

	private final LayoutManager _layoutType;

	private final TArray<LComponent> _styles;

	private float _spaceX, _spaceY;

	private float _spaceWidth, _spaceHeight;

	public LayoutStyles(LayoutManager layout) {
		this(layout, LSystem.DEFAULT_MAX_CACHE_SIZE);
	}

	public LayoutStyles(LayoutManager layout, int capacity) {
		this._layoutType = layout;
		this._styles = new TArray<LComponent>(capacity);
	}

	public LayoutManager getLayoutType() {
		return this._layoutType;
	}

	public LayoutStyles add(LComponent c) {
		if (c != null && !_styles.contains(c)) {
			_styles.add(c);
		}
		return this;
	}

	public boolean contains(LComponent c) {
		if (c != null) {
			return _styles.contains(c);
		}
		return false;
	}

	public boolean remove(LComponent c) {
		if (c != null) {
			return _styles.remove(c);
		}
		return false;
	}

	public LayoutStyles clear() {
		_styles.clear();
		return this;
	}

	public TArray<LComponent> getComponentArray() {
		return this._styles;
	}

	public LComponent[] getComponents() {
		final int size = this._styles.size;
		final LComponent[] comps = new LComponent[size];
		for (int i = 0; i < size; i++) {
			comps[i] = this._styles.get(i);
		}
		return comps;
	}

	public LayoutStyles space(float s) {
		setSpaceX(s);
		setSpaceY(s);
		return this;
	}

	public LayoutStyles size(float s) {
		setSpaceWidth(s);
		setSpaceHeight(s);
		return this;
	}

	public float getSpaceX() {
		return _spaceX;
	}

	public LayoutStyles setSpaceX(float sx) {
		this._spaceX = sx;
		return this;
	}

	public float getSpaceY() {
		return _spaceY;
	}

	public LayoutStyles setSpaceY(float sy) {
		this._spaceY = sy;
		return this;
	}

	public float getSpaceWidth() {
		return _spaceWidth;
	}

	public LayoutStyles setSpaceWidth(float sw) {
		this._spaceWidth = sw;
		return this;
	}

	public float getSpaceHeight() {
		return _spaceHeight;
	}

	public LayoutStyles setSpaceHeight(float sh) {
		this._spaceHeight = sh;
		return this;
	}

}
