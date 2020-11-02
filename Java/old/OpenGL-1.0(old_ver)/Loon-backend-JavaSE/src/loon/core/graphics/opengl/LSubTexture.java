package loon.core.graphics.opengl;

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
 * @version 0.1
 */
public class LSubTexture {

	private LTexture parent;

	private int x, y, x2, y2;

	public LSubTexture(LTexture parent, int x, int y, int x2, int y2) {
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.x2 = x2;
		this.y2 = y2;
	}

	public LTexture get() {
		return this.parent.getSubTexture(x, y, getWidth(), getHeight());
	}

	public LTexture getParent() {
		return parent;
	}

	public int getSubX() {
		return x;
	}

	public int getSubY() {
		return y;
	}

	public int getSubX2() {
		return x2;
	}

	public int getSubY2() {
		return y2;
	}

	public int getWidth() {
		return x2 - x;
	}

	public int getHeight() {
		return y2 - y;
	}

	public int getParentWidth() {
		if (parent != null) {
			return parent.getWidth();
		}
		return 0;
	}

	public int getParentHeight() {
		if (parent != null) {
			return parent.getHeight();
		}
		return 0;
	}
}
