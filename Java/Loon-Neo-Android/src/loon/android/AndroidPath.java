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
package loon.android;

import android.graphics.Path;

class AndroidPath implements loon.canvas.Path {

	Path path;

	AndroidPath() {
		path = new Path();
	}

	@Override
	public loon.canvas.Path bezierTo(float c1x, float c1y, float c2x, float c2y, float x, float y) {
		path.cubicTo(c1x, c1y, c2x, c2y, x, y);
		return this;
	}

	@Override
	public loon.canvas.Path close() {
		path.close();
		return this;
	}

	@Override
	public loon.canvas.Path lineTo(float x, float y) {
		path.lineTo(x, y);
		return this;
	}

	@Override
	public loon.canvas.Path moveTo(float x, float y) {
		path.moveTo(x, y);
		return this;
	}

	@Override
	public loon.canvas.Path quadraticCurveTo(float cpx, float cpy, float x, float y) {
		path.quadTo(cpx, cpy, x, y);
		return this;
	}

	@Override
	public loon.canvas.Path reset() {
		path.reset();
		return this;
	}
}
