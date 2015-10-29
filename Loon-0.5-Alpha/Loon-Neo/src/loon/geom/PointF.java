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

/*最简化的浮点坐标处理类,以减少对象大小*/
public class PointF {

	public float x = 0;
	public float y = 0;
	
	public PointF(){
		
	}

	public PointF(float x1, float y1) {
		this.x = x1;
		this.y = y1;
	}

	public void set(float x1, float y1) {
		this.x = x1;
		this.y = y1;
	}
}
