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
package loon.srpg.view;

import loon.opengl.GLEx;

public class SRPGDrawView extends SRPGView {

	private boolean lock;

	protected int left;

	protected int top;

	protected int width;

	protected int height;

	public SRPGDrawView() {
		reset();
	}

	public void reset() {
		super.exist = false;
		this.lock = false;
		this.left = 0;
		this.top = 0;
		this.width = 0;
		this.height = 0;
	}

	public boolean isLock() {
		if (isExist()) {
			return lock;
		} else {
			return false;
		}
	}

	public void setLock(boolean flag) {
		this.lock = flag;
	}

	public void setLocation(int x, int y) {
		this.left = x;
		this.top = y;
	}

	public int getLeft() {
		return left;
	}

	public int getTop() {
		return top;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void draw(GLEx g) {

	}

}
