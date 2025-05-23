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
package loon.action.collision;

import loon.geom.Vector2f;

public class IncrementalForce implements Force {

	private final Vector2f _direction;

	private final String _identifier;

	private float _currentIncrement;

	private final float _increment;

	private final float _limit;

	private Vector2f _current;

	public IncrementalForce(String i, Vector2f d, float inc, float limit) {
		this._identifier = i;
		this._direction = d;
		this._increment = inc;
		this._limit = limit;
		this._current = d;
	}

	@Override
	public String identifier() {
		return _identifier;
	}

	@Override
	public void update(long e) {
		if (_currentIncrement >= _limit) {
			return;
		}
		_currentIncrement += _increment;
		_current = _direction.mul(_currentIncrement);
	}

	@Override
	public Vector2f direction() {
		return _current;
	}

	public IncrementalForce reset() {
		_currentIncrement = 1;
		return this;
	}

}