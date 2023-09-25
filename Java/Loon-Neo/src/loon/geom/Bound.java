/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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

public class Bound {

	protected final PointF leftTop;

	protected final PointF rightTop;

	protected final PointF leftBottom;

	protected final PointF rightBottom;

	public Bound() {
		this(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f);
	}

	public Bound(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
		this.leftTop = new PointF(x0, y0);
		this.rightTop = new PointF(x1, y1);
		this.leftBottom = new PointF(x2, y2);
		this.rightBottom = new PointF(x3, y3);
	}

	public PointF getLeftTop() {
		return leftTop;
	}

	public Bound setLeftTop(float left, float top) {
		this.leftTop.set(left, top);
		return this;
	}

	public PointF getRightTop() {
		return rightTop;
	}

	public Bound setRightTop(float right, float top) {
		this.rightTop.set(right, top);
		return this;
	}

	public PointF getLeftBottom() {
		return leftBottom;
	}

	public Bound setLeftBottom(float left, float bottom) {
		this.leftBottom.set(left, bottom);
		return this;
	}

	public PointF getRightBottom() {
		return rightBottom;
	}

	public Bound setRightBottom(float right, float bottom) {
		this.rightBottom.set(right, bottom);
		return this;
	}

}
