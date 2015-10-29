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

/*最简化的整型体积处理类,以减少对象大小*/
public class RectI {
	
	public int width = 0;
	public int height = 0;
	public int x = 0;
	public int y = 0;
	
	public RectI(){
		
	}
	
	public RectI(int x1, int y1, int w1, int h1) {
		this.x = x1;
		this.y = y1;
		this.width = w1;
		this.height = h1;
	}

	public RectI set(RectI r){
		this.x = r.x;
		this.y = r.y;
		this.width = r.width;
		this.height = r.height;
		return this;
	}
	
	public RectI set(int x1, int y1, int w1, int h1){
		this.x = x1;
		this.y = y1;
		this.width = w1;
		this.height = h1;
		return this;
	}
	
	public boolean inside(int x, int y) {
		return (x >= this.x) && ((x - this.x) < this.width) && (y >= this.y)
				&& ((y - this.y) < this.height);
	}
	
	public int getRight() {
		return this.x + this.width;
	}

	public int getBottom() {
		return this.y + this.height;
	}

	public RectI getIntersection(RectI rect) {
		int x1 =  MathUtils.max(x, rect.x);
		int x2 =  MathUtils.min(x + width, rect.x + rect.width);
		int y1 =  MathUtils.max(y, rect.y);
		int y2 =  MathUtils.min(y + height, rect.y + rect.height);
		return new RectI(x1, y1, x2 - x1, y2 - y1);
	}
	
	public static RectI getIntersection(RectI a, RectI b) {
		int a_x = a.x;
		int a_r = a.getRight();
		int a_y = a.y;
		int a_t = a.getBottom();
		int b_x = b.x;
		int b_r = b.getRight();
		int b_y = b.y;
		int b_t = b.getBottom();
		int i_x = MathUtils.max(a_x, b_x);
		int i_r = MathUtils.min(a_r, b_r);
		int i_y = MathUtils.max(a_y, b_y);
		int i_t = MathUtils.min(a_t, b_t);
		return i_x < i_r && i_y < i_t ? new RectI(i_x, i_y, i_r - i_x, i_t
				- i_y) : null;
	}
	

	public static RectI getIntersection(RectI a, RectI b, RectI result) {
		int a_x = a.x;
		int a_r = a.getRight();
		int a_y = a.y;
		int a_t = a.getBottom();
		int b_x = b.x;
		int b_r = b.getRight();
		int b_y = b.y;
		int b_t = b.getBottom();
		int i_x = MathUtils.max(a_x, b_x);
		int i_r = MathUtils.min(a_r, b_r);
		int i_y = MathUtils.max(a_y, b_y);
		int i_t = MathUtils.min(a_t, b_t);
		if (i_x < i_r && i_y < i_t) {
			result.set(i_x, i_y, i_r - i_x, i_t - i_y);
			return result;
		}
		return result;
	}
}