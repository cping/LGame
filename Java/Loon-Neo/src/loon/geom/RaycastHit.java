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
package loon.geom;

import loon.action.collision.CollisionObject;

public class RaycastHit {

	private float _fraction;

	private Vector2f _normal;
	private Vector2f _point;
	private Vector2f _gridPos;

	private CollisionObject _object;

	public RaycastHit() {
		this(null);
	}

	public RaycastHit(CollisionObject o) {
		_object = o;
		_normal = new Vector2f();
		_point = new Vector2f();
		_gridPos = new Vector2f();
	}

	public float getFraction() {
		return _fraction;
	}

	public void setFraction(float f) {
		this._fraction = f;
	}

	public Vector2f getNormal() {
		return _normal;
	}

	public void setNormal(Vector2f n) {
		if (n == null) {
			return;
		}
		this._normal = n;
	}

	public Vector2f getPoint() {
		return _point;
	}

	public void setPoint(Vector2f p) {
		if (p == null) {
			return;
		}
		this._point = p;
	}

	public Vector2f getGridPos() {
		return _gridPos;
	}

	public void setGridPos(Vector2f g) {
		if (g == null) {
			return;
		}
		this._gridPos = g;
	}

	public CollisionObject getObject() {
		return _object;
	}

	public void setObject(CollisionObject o) {
		this._object = o;
	}

}
