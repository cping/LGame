/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
package loon.action.behaviors;

import loon.LSystem;

public class BehaviorTree<T> {

	public float updatePeriod;

	protected T _context;

	protected Behavior<T> _root;

	protected float _elapsedTime;

	protected float _deltaTime;

	public BehaviorTree(T context, Behavior<T> rootNode) {
		this(context, rootNode, 0.2f);
	}

	public BehaviorTree(T context, Behavior<T> rootNode, float update) {
		_context = context;
		_root = rootNode;
		this.updatePeriod = _elapsedTime = update;
		this._deltaTime = LSystem.DEFAULT_EASE_DELAY;
	}

	public void setElapsedTime(float time) {
		this._elapsedTime = time;
	}

	public void tick() {
		tick(this._deltaTime);
	}

	public void tick(float delta) {
		if (updatePeriod > 0f) {
			_elapsedTime -= delta;
			if (_elapsedTime <= 0) {
				while (_elapsedTime <= 0) {
					_elapsedTime += updatePeriod;
				}
				_root.tick(_context);
			}
		} else {
			_root.tick(_context);
		}
	}
}