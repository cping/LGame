/**
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
package loon.srpg.actor;

public class SRPGPosition {

	public int number;

	public int[] pos;

	public int[] past;

	public int vector;

	public int ability;

	public int[][] route;

	public int[] target;

	public int[][] area;

	public int enemy;

	public boolean counter;

	public SRPGPosition() {
		this.reset();
	}

	public void reset() {
		this.number = -1;
		this.pos = new int[2];
		this.past = new int[2];
		this.target = new int[2];
		this.ability = -1;
		this.area = null;
		this.route = null;
		this.enemy = -1;
		this.counter = false;
	}

	public SRPGPosition(int index, int x, int y) {
		this();
		this.number = index;
		this.pos[0] = x;
		this.pos[1] = y;
	}

	public void setPos(int x, int y) {
		this.pos[0] = x;
		this.pos[1] = y;
	}

	public void setPast(int x, int y) {
		this.past[0] = x;
		this.past[1] = y;
	}

	public void setTarget(int x, int y) {
		this.target[0] = x;
		this.target[1] = y;
	}

}
