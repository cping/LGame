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

import loon.action.ActionBind;

public class CollisionActionFilter<T extends ActionBind> implements CollisionAction<T> {

	public final static <T extends ActionBind> CollisionActionFilter<T> create(CollisionActionQuery<T> query) {
		return new CollisionActionFilter<T>(query);
	}

	private final CollisionActionQuery<T> _filter;

	public CollisionActionFilter(CollisionActionQuery<T> f) {
		this._filter = f;
	}

	@Override
	public void onCollision(T src, T dst, int dir) {
		if (_filter != null && _filter.checkQuery(src, dst, dir)) {
			_filter.onCollisionResult(src, dst, dir);
		}
	}

}
