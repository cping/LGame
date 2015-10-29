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

/*最简化的整型坐标处理类,以减少对象大小*/
public class PointI {

	public int x = 0;
	public int y = 0;
	
	public PointI(){
		
	}

	public PointI(int x1, int y1) {
		this.x = x1;
		this.y = y1;
	}

	public void set(int x1, int y1) {
		this.x = x1;
		this.y = y1;
	}
}
