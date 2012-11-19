package org.loon.framework.javase.game.core.graphics.component;

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
public class CollisionInRangeQuery implements CollisionQuery {

	private int x;

	private int y;

	private int r;

	public void init(int x, int y, int r) {
		this.x = x;
		this.y = y;
		this.r = r;
	}

	public boolean checkCollision(Actor actor) {
		int actorX = actor.toPixel(actor.getX());
		int actorY = actor.toPixel(actor.getY());
		int dx = actorX - this.x;
		int dy = actorY - this.y;
		int dist = (int) Math.sqrt((double) (dx * dx + dy * dy));
		return dist <= this.r;
	}
}
