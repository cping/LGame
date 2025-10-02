/**
 * 
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
package loon.action.collision;

import loon.geom.Vector2f;

public final class CollisionClassQuery implements CollisionQuery {

	private String _flag;

	private CollisionQuery _subQuery;

	private Vector2f _offsetLocation;

	public CollisionClassQuery(String flag, CollisionQuery subQuery, Vector2f offset) {
		this._flag = flag;
		this._subQuery = subQuery;
		this._offsetLocation = offset;
	}

	@Override
	public boolean checkCollision(CollisionObject actor) {
		return _flag.equals(actor.getObjectFlag()) ? this._subQuery.checkCollision(actor) : false;
	}

	@Override
	public void setOffsetPos(Vector2f offset) {
		_offsetLocation = offset;
	}

	@Override
	public Vector2f getOffsetPos() {
		return _offsetLocation;
	}
}
