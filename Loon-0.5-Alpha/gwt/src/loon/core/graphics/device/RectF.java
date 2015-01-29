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
 * @version 0.4.2
 */
package loon.core.graphics.device;

public class RectF {
	public RectF(float l, float t, float r, float b) {
		left = l;
		top = t;
		right = r;
		bottom = b;
	}

	public RectF() {
		top = 0;
		left = 0;
		right = 0;
		bottom = 0;
	}

	public float top;
	public float left;
	public float right;
	public float bottom;

	public float width() {
		return Math.abs(right - left);
	}

	public float height() {
		return Math.abs(bottom - top);
	}

	public void set(float l, float t, float r, float b) {
		left = l;
		top = t;
		right = r;
		bottom = b;
	}
}