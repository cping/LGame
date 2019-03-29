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

import loon.geom.RectBox;
import loon.utils.MathUtils;

public class CollisionInRangeQuery implements CollisionQuery {

	private float dx;

	private float dy;

	private float dist;

	private float x;

	private float y;

	private float r;

	private RectBox object;

	public CollisionInRangeQuery init(float x, float y, float r) {
		this.x = x;
		this.y = y;
		this.r = r;
		return this;
	}

	@Override
	public boolean checkCollision(CollisionObject actor) {

		object = actor.getRectBox();

		dx = MathUtils.abs(object.getCenterX() - x);
		dy = MathUtils.abs(object.getCenterY() - y);

		dist = MathUtils.sqrt(dx * dx + dy * dy);

		return dist <= this.r;
	}
}
