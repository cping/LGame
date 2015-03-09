package loon.srpg.effect;

import loon.core.graphics.Screen;
import loon.core.graphics.opengl.GLEx;


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
public class SRPGEffect {

	protected boolean isExist;

	protected int[] start;

	protected int[] target;

	protected int frame;

	public SRPGEffect() {
		this.reset();
	}

	public SRPGEffect(int x1, int y1, int x2, int y2) {
		this.reset();
		this.setEffectPosition(x1, y1, x2, y2);
	}

	public void reset() {
		this.isExist = false;
		this.start = new int[2];
		this.target = new int[2];
		this.frame = 0;
	}

	public void update() {
		this.start = new int[2];
		this.target = new int[2];
		this.frame = 0;
	}

	public final void setExist(boolean isExist) {
		this.isExist = isExist;
	}

	public final boolean isExist() {
		return isExist;
	}

	public void setEffectPosition(int x1, int y1, int x2, int y2) {
		this.start[0] = x1;
		this.start[1] = y1;
		this.target[0] = x2;
		this.target[1] = y2;
	}

	public void wait(Screen screen) {
		for (; isExist();) {
			try {
				screen.wait();
			} catch (Exception ex) {
				break;
			}
		}
	}

	public final void next() {
		this.frame++;
	}

	public void draw(GLEx g, int x, int y) {

	}
}
