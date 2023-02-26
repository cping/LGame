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
package loon.action.collision;

import loon.action.ActionBind;
import loon.geom.PointF;
import loon.geom.RectF;

public class CollisionData {

	public boolean overlaps;
	public float ti;
	public PointF move = new PointF();
	public PointF normal = new PointF();
	public PointF touch = new PointF();
	public RectF itemRect = new RectF();
	public RectF otherRect = new RectF();
	public ActionBind item;
	public ActionBind other;
	public CollisionResult type;

	public CollisionData() {
	}

	public void set(boolean overlaps, float ti, float moveX, float moveY, float normalX, float normalY, float touchX,
			float touchY, float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2) {
		this.overlaps = overlaps;
		this.ti = ti;
		this.move.set(moveX, moveY);
		this.normal.set(normalX, normalY);
		this.touch.set(touchX, touchY);
		this.itemRect.set(x1, y1, w1, h1);
		this.otherRect.set(x2, y2, w2, h2);
	}
}
