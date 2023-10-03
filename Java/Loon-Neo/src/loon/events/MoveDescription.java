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
package loon.events;

import loon.geom.Vector2f;

public class MoveDescription {

	private Vector2f _fromPoint;

	private Vector2f _toPoint;

	public MoveDescription(float startX, float startY, float endX, float endY) {
		this._fromPoint = Vector2f.at(startX, startY);
		this._toPoint = Vector2f.at(endX, endY);
	}

	public Vector2f getFromPoint() {
		return _fromPoint.cpy();
	}

	public MoveDescription setFromPoint(Vector2f f) {
		this._fromPoint = f;
		return this;
	}

	public Vector2f getToPoint() {
		return _toPoint.cpy();
	}

	public MoveDescription setToPoint(Vector2f t) {
		this._toPoint = t;
		return this;
	}

}
