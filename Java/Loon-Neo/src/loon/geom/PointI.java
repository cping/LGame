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

import loon.utils.MathUtils;
import loon.utils.NumberUtils;

/*最简化的整型坐标处理类,以减少对象大小*/
public class PointI implements XY {

	public int x = 0;
	public int y = 0;

	public PointI() {
		this(0, 0);
	}

	public PointI(int x1, int y1) {
		set(x1, y1);
	}

	public PointI(PointI p) {
		this.x = p.x;
		this.y = p.y;
	}

	public void set(int x1, int y1) {
		this.x = x1;
		this.y = y1;
	}

	public final boolean equals(int x, int y) {
		return MathUtils.equal(x, this.x) && MathUtils.equal(y, this.y);
	}

	public final int length() {
		return MathUtils.sqrt(MathUtils.mul(x, x) + MathUtils.mul(y, y));
	}

	public final PointI negate() {
		x = -x;
		y = -y;
		return this;
	}

	public final PointI offset(int x, int y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public final PointI set(PointI p) {
		this.x = p.x;
		this.y = p.y;
		return this;
	}

	public final int distanceTo(PointI p) {
		final int tx = this.x - p.x;
		final int ty = this.y - p.y;
		return MathUtils.sqrt(MathUtils.mul(tx, tx) + MathUtils.mul(ty, ty));
	}

	public final int distanceTo(int x, int y) {
		final int tx = this.x - x;
		final int ty = this.y - y;
		return MathUtils.sqrt(MathUtils.mul(tx, tx) + MathUtils.mul(ty, ty));
	}

	public final int distanceTo(PointI p1, PointI p2) {
		final int tx = p2.x - p1.x;
		final int ty = p2.y - p1.y;
		final int u = MathUtils.div(MathUtils.mul(x - p1.x, tx) + MathUtils.mul(y - p1.y, ty),
				MathUtils.mul(tx, tx) + MathUtils.mul(ty, ty));
		final int ix = p1.x + MathUtils.mul(u, tx);
		final int iy = p1.y + MathUtils.mul(u, ty);
		final int dx = ix - x;
		final int dy = iy - y;
		return MathUtils.sqrt(MathUtils.mul(dx, dx) + MathUtils.mul(dy, dy));
	}

	public PointI cpy(PointI p) {
		return new PointI(p.x, p.y);
	}

	public PointI cpy() {
		return cpy(this);
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	public String toCSS() {
		return this.x + "px " + this.y + "px";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + NumberUtils.floatToIntBits(x);
		result = prime * result + NumberUtils.floatToIntBits(y);
		return result;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
}
