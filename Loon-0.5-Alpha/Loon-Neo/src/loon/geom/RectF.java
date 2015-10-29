/**
 * 
 * Copyright 2008 - 2015
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

/*最简化的浮点体积处理类,以减少对象大小*/
public  class RectF {
	
	public float width = 0f;
	public float height = 0f;
	public float x = 0f;
	public float y = 0f;

	public RectF() {
		
	}

	public RectF set(RectF r){
		this.x = r.x;
		this.y = r.y;
		this.width = r.width;
		this.height = r.height;
		return this;
	}
	
	public RectF set(float x1, float y1, float w1, float h1){
		this.x = x1;
		this.y = y1;
		this.width = w1;
		this.height = h1;
		return this;
	}
	
	public RectF(float x1, float y1, float w1, float h1) {
		this.x = x1;
		this.y = y1;
		this.width = w1;
		this.height = h1;
	}
	
	public boolean inside(float x, float y) {
		return (x >= this.x) && ((x - this.x) < this.width) && (y >= this.y)
				&& ((y - this.y) < this.height);
	}
	
	public float getRight() {
		return this.x + this.width;
	}

	public float getBottom() {
		return this.y + this.height;
	}
	
	public RectF getIntersection(RectF rect) {
		float x1 =  MathUtils.max(x, rect.x);
		float x2 =  MathUtils.min(x + width, rect.x + rect.width);
		float y1 =  MathUtils.max(y, rect.y);
		float y2 =  MathUtils.min(y + height, rect.y + rect.height);
		return new RectF(x1, y1, x2 - x1, y2 - y1);
	}

	public static RectF getIntersection(RectF a, RectF b) {
		float a_x = a.x;
		float a_r = a.getRight();
		float a_y = a.y;
		float a_t = a.getBottom();
		float b_x = b.x;
		float b_r = b.getRight();
		float b_y = b.y;
		float b_t = b.getBottom();
		float i_x = MathUtils.max(a_x, b_x);
		float i_r = MathUtils.min(a_r, b_r);
		float i_y = MathUtils.max(a_y, b_y);
		float i_t = MathUtils.min(a_t, b_t);
		return i_x < i_r && i_y < i_t ? new RectF(i_x, i_y, i_r - i_x, i_t
				- i_y) : new RectF();
	}
	
	public static RectF getIntersection(RectF a, RectF b, RectF result) {
		float a_x = a.x;
		float a_r = a.getRight();
		float a_y = a.y;
		float a_t = a.getBottom();
		float b_x = b.x;
		float b_r = b.getRight();
		float b_y = b.y;
		float b_t = b.getBottom();
		float i_x = MathUtils.max(a_x, b_x);
		float i_r = MathUtils.min(a_r, b_r);
		float i_y = MathUtils.max(a_y, b_y);
		float i_t = MathUtils.min(a_t, b_t);
		if (i_x < i_r && i_y < i_t) {
			result.set(i_x, i_y, i_r - i_x, i_t - i_y);
			return result;
		}
		return result;
	}
}