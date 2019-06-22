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

import loon.utils.NumberUtils;
import loon.utils.StringKeyValue;

/**
 * 一个最基础的矩形碰撞器
 */
public class AABB implements XY, BoxSize {

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

	public int width() {
		return (int) this.maxX;
	}

	public int height() {
		return (int) this.maxY;
	}

	@Override
	public float getWidth() {
		return this.maxX;
	}

	@Override
	public float getHeight() {
		return this.maxY;
	}

	public AABB cpy() {
		return new AABB(this.minX, this.minY, this.maxX, this.maxY);
	}

	public boolean isHit(AABB b) {
		return this.minX < b.maxX && b.minX < this.maxX && this.minY < b.maxY && b.minY < this.maxY;
	}

	public void set(float minX, float minY, float maxX, float maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	@Override
	public void setX(float x) {
		this.minX = x;
	}

	@Override
	public void setY(float y) {
		this.minY = y;
	}

	@Override
	public void setWidth(float w) {
		this.maxX = w;
	}

	@Override
	public void setHeight(float h) {
		this.maxY = h;
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

		return ((xmin > minX && xmin < minX + maxX) && (xmax > minX && xmax < minX + maxX))
				&& ((ymin > minY && ymin < minY + maxY) && (ymax > minY && ymax < minY + maxY));
	}
	
	public RectBox toRectBox() {
		return new RectBox(this.minX, this.minY, this.maxX, this.maxY);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + NumberUtils.floatToIntBits(minX);
		result = prime * result + NumberUtils.floatToIntBits(minY);
		result = prime * result + NumberUtils.floatToIntBits(maxX);
		result = prime * result + NumberUtils.floatToIntBits(maxY);
		return result;
	}
	
	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("AABB");
		builder.kv("minX", minX)
		.comma()
		.kv("minY", minY)
		.comma()
		.kv("maxX", maxX)
		.comma()
		.kv("maxY", maxY);
		return builder.toString();
	}

}
