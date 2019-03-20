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
package loon.action.map;
/**
 * 默认的方向键与真实方向对应关系如下
 * 
 *          n
 *      wn     ne
 *    w           e
 *      sw     es
 *          s
 *
 *          TUP
 *     LEFT     UP
 *   TLEFT       TRIGHT
 *     DOWN     RIGHT
 *         TDOWN
 */
public interface Config {
	
	public static final int EMPTY = -1;
	
	public static final int LEFT = 0;

	public static final int RIGHT = 1;

	public static final int UP = 2;

	public static final int DOWN = 3;

	public static final int TLEFT = 4;

	public static final int TRIGHT = 5;

	public static final int TUP = 6;

	public static final int TDOWN = 7;
	
	public static final int WN = LEFT;
	
	public static final int ES = RIGHT;

	public static final int NE = UP;
	
	public static final int SW = DOWN;
	
	public static final int N = TUP;

	public static final int S = TDOWN;
	
	public static final int W = TLEFT;

	public static final int E = TRIGHT;
}
