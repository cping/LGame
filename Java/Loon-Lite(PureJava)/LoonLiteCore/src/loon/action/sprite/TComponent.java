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
import loon.action.ActionBind;
import loon.events.EventActionT;
import loon.geom.BooleanValue;

/**
 * 控制Entity用的Component，具体实现后可用于注入Entity从而产生特定操作效果
 */
public abstract class TComponent<T extends ActionBind> implements EventActionT<T> {

	protected final BooleanValue _paused = new BooleanValue();

	protected T _currentSprite;

	protected String _name;

	public TComponent() {
		this(LSystem.UNKNOWN);
	}

	public TComponent(String name) {
		this._name = name;
	}

	public boolean isPaused() {
		return _paused.get();
	}

	public TComponent<T> pause() {
		_paused.set(true);
		return this;
	}

	public TComponent<T> resume() {
		_paused.set(false);
		return this;
	}

	public BooleanValue state() {
		return _paused;
	}

	public String getName() {
		return this._name;
	}

	public abstract void onAttached(T on);

	public abstract void onDetached(T on);

	public T getCurrent() {
		return _currentSprite;
	}

	public TComponent<T> setCurrent(T c) {
		this._currentSprite = c;
		return this;
	}

}
