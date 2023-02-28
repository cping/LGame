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
package loon.action.map.tmx.objects;

public class TMXEllipse {
	private int x;
	private int y;
	private int radiusX;
	private int radiusY;

	public void set(int x, int y, int width, int height) {
		this.x = x + (width / 2);
		this.y = y + (height / 2);
		this.radiusX = width / 2;
		this.radiusY = height / 2;
	}

	public int getCenterX() {
		return x;
	}

	public int getCenterY() {
		return y;
	}

	public int getRadiusX() {
		return radiusX;
	}

	public int getRadiusY() {
		return radiusY;
	}
}
