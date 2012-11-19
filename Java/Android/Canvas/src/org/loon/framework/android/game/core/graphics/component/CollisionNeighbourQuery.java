package org.loon.framework.android.game.core.graphics.component;

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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class CollisionNeighbourQuery implements CollisionQuery {

	private int x;

	private int y;

	private int distance;

	private boolean diag;

	private Class<?> cls;

	public void init(int x, int y, int distance, boolean diag, Class<?> cls) {
		this.x = x;
		this.y = y;
		this.distance = distance;
		this.diag = diag;
		this.cls = cls;
	}

	public boolean checkCollision(Actor actor) {
		if (this.cls != null && !this.cls.isInstance(actor)) {
			return false;
		} else {
			int actorX = actor.getX();
			int actorY = actor.getY();
			if (actorX == this.x && actorY == this.y) {
				return false;
			} else {
				int ax = actor.getX();
				int ay = actor.getY();
				int dx;
				int dy;
				if (!this.diag) {
					dx = Math.abs(ax - this.x);
					dy = Math.abs(ay - this.y);
					return dx + dy <= this.distance;
				} else {
					dx = this.x - this.distance;
					dy = this.y - this.distance;
					int x2 = this.x + this.distance;
					int y2 = this.y + this.distance;
					return ax >= dx && ay >= dy && ax <= x2 && ay <= y2;
				}
			}
		}
	}
}
