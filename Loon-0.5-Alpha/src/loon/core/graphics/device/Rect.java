/**
 * Copyright 2014
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
 * @version 0.4.2
 */
package loon.core.graphics.device;

public class Rect {

	public int left = 0;
	public int top = 0;
	public int right = 0;
	public int bottom = 0;

	public Rect() {
	}

	public Rect(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	public Rect(Rect r) {
		left = r.left;
		top = r.top;
		right = r.right;
		bottom = r.bottom;
	}

	@Override
	public boolean equals(Object obj) {
		Rect r = (Rect) obj;
		if (r != null) {
			return left == r.left && top == r.top && right == r.right
					&& bottom == r.bottom;
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(32);
		sb.append("Rect(");
		sb.append(left);
		sb.append(", ");
		sb.append(top);
		sb.append(" - ");
		sb.append(right);
		sb.append(", ");
		sb.append(bottom);
		sb.append(")");
		return sb.toString();
	}

	public String toShortString() {
		return toShortString(new StringBuilder(32));
	}

	public String toShortString(StringBuilder sb) {
		sb.setLength(0);
		sb.append('[');
		sb.append(left);
		sb.append(',');
		sb.append(top);
		sb.append("][");
		sb.append(right);
		sb.append(',');
		sb.append(bottom);
		sb.append(']');
		return sb.toString();
	}

	public String flattenToString() {
		StringBuilder sb = new StringBuilder(32);
		sb.append(left);
		sb.append(' ');
		sb.append(top);
		sb.append(' ');
		sb.append(right);
		sb.append(' ');
		sb.append(bottom);
		return sb.toString();
	}

	public final boolean isEmpty() {
		return left >= right || top >= bottom;
	}

	public final int width() {
		return right - left;
	}

	public final int height() {
		return bottom - top;
	}

	public final int centerX() {
		return (left + right) >> 1;
	}

	public final int centerY() {
		return (top + bottom) >> 1;
	}

	public final float exactCenterX() {
		return (left + right) * 0.5f;
	}

	public final float exactCenterY() {
		return (top + bottom) * 0.5f;
	}

	public void setEmpty() {
		left = right = top = bottom = 0;
	}

	public void set(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	public void set(Rect src) {
		this.left = src.left;
		this.top = src.top;
		this.right = src.right;
		this.bottom = src.bottom;
	}

	public void offset(int dx, int dy) {
		left += dx;
		top += dy;
		right += dx;
		bottom += dy;
	}

	public void offsetTo(int newLeft, int newTop) {
		right += newLeft - left;
		bottom += newTop - top;
		left = newLeft;
		top = newTop;
	}

	public void inset(int dx, int dy) {
		left += dx;
		top += dy;
		right -= dx;
		bottom -= dy;
	}

	public boolean contains(int x, int y) {
		return left < right && top < bottom && x >= left && x < right
				&& y >= top && y < bottom;
	}

	public boolean contains(int left, int top, int right, int bottom) {
		return this.left < this.right && this.top < this.bottom
				&& this.left <= left && this.top <= top && this.right >= right
				&& this.bottom >= bottom;
	}

	public boolean contains(Rect r) {
		return this.left < this.right && this.top < this.bottom
				&& left <= r.left && top <= r.top && right >= r.right
				&& bottom >= r.bottom;
	}

	public boolean intersect(int left, int top, int right, int bottom) {
		if (this.left < right && left < this.right && this.top < bottom
				&& top < this.bottom) {
			if (this.left < left) {
				this.left = left;
			}
			if (this.top < top) {
				this.top = top;
			}
			if (this.right > right) {
				this.right = right;
			}
			if (this.bottom > bottom) {
				this.bottom = bottom;
			}
			return true;
		}
		return false;
	}

	public boolean intersect(Rect r) {
		return intersect(r.left, r.top, r.right, r.bottom);
	}

	public boolean setIntersect(Rect a, Rect b) {
		if (a.left < b.right && b.left < a.right && a.top < b.bottom
				&& b.top < a.bottom) {
			left = Math.max(a.left, b.left);
			top = Math.max(a.top, b.top);
			right = Math.min(a.right, b.right);
			bottom = Math.min(a.bottom, b.bottom);
			return true;
		}
		return false;
	}

	public boolean intersects(int left, int top, int right, int bottom) {
		return this.left < right && left < this.right && this.top < bottom
				&& top < this.bottom;
	}

	public static boolean intersects(Rect a, Rect b) {
		return a.left < b.right && b.left < a.right && a.top < b.bottom
				&& b.top < a.bottom;
	}

	public void union(int left, int top, int right, int bottom) {
		if ((left < right) && (top < bottom)) {
			if ((this.left < this.right) && (this.top < this.bottom)) {
				if (this.left > left)
					this.left = left;
				if (this.top > top)
					this.top = top;
				if (this.right < right)
					this.right = right;
				if (this.bottom < bottom)
					this.bottom = bottom;
			} else {
				this.left = left;
				this.top = top;
				this.right = right;
				this.bottom = bottom;
			}
		}
	}

	public void union(Rect r) {
		union(r.left, r.top, r.right, r.bottom);
	}

	public void union(int x, int y) {
		if (x < left) {
			left = x;
		} else if (x > right) {
			right = x;
		}
		if (y < top) {
			top = y;
		} else if (y > bottom) {
			bottom = y;
		}
	}

	public void sort() {
		if (left > right) {
			int temp = left;
			left = right;
			right = temp;
		}
		if (top > bottom) {
			int temp = top;
			top = bottom;
			bottom = temp;
		}
	}

	public void scale(float scale) {
		if (scale != 1.0f) {
			left = (int) (left * scale + 0.5f);
			top = (int) (top * scale + 0.5f);
			right = (int) (right * scale + 0.5f);
			bottom = (int) (bottom * scale + 0.5f);
		}
	}

}
