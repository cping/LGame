/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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

import loon.utils.StringUtils;

/**
 * 一个最基础的矩形碰撞器
 */
public class AABB implements XY {

	public float minX, minY;

	public float maxX, maxY;

	public AABB() {
		this(0.0F, 0.0F, 0.0F, 0.0F);
	}

	public AABB(float minX, float minY, float maxX, float maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	public AABB cpy() {
		return new AABB(this.minX, this.minY, this.maxX, this.maxY);
	}

	public boolean isHit(AABB b) {
		return this.minX < b.maxX && b.minX < this.maxX && this.minY < b.maxY
				&& b.minY < this.maxY;
	}

	public void set(float minX, float minY, float maxX, float maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	@Override
	public float getX() {
		return minX;
	}

	@Override
	public float getY() {
		return minY;
	}

	public boolean contains(Circle circle) {
		float xmin = circle.x - circle.radius;
		float xmax = xmin + 2f * circle.radius;

		float ymin = circle.y - circle.radius;
		float ymax = ymin + 2f * circle.radius;

		return ((xmin > minX && xmin < minX + maxX) && (xmax > minX && xmax < minX
				+ maxX))
				&& ((ymin > minY && ymin < minY + maxY) && (ymax > minY && ymax < minY
						+ maxY));
	}

	@Override
	public String toString() {
		return StringUtils.format("AABB [minX:{0},minY:{1},maxX:{2},maxY:{3}]",
				minX, minY, maxX, maxY);
	}

}
